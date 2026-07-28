package com.bike.computer

import android.content.Context
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.net.URLEncoder

/** Google Drive sync over raw REST (OkHttp), no Google SDK: OAuth, GPX upload, route sync. */
object GoogleDriveClient {
    private const val AUTH = "https://accounts.google.com/o/oauth2/v2/auth"
    const val SCOPE =
        "https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/drive.readonly"
    private const val TOKEN = "https://oauth2.googleapis.com/token"
    private val http = OkHttpClient()

    private val JSON = "application/json; charset=UTF-8".toMediaType()

    private fun enc(s: String): String = URLEncoder.encode(s, "UTF-8")

    fun authorizeUrl(clientId: String, redirect: String): String =
        "$AUTH?client_id=${enc(clientId)}&redirect_uri=${enc(redirect)}" +
            "&response_type=code&scope=${enc(SCOPE)}&access_type=offline&prompt=consent"

    @Throws(JSONException::class, IOException::class)
    fun exchangeCode(c: Context, code: String, redirect: String) {
        val res = post(
            TOKEN,
            FormBody.Builder()
                .add("client_id", Prefs.driveClientId(c))
                .add("client_secret", Prefs.driveClientSecret(c))
                .add("code", code)
                .add("redirect_uri", redirect)
                .add("grant_type", "authorization_code")
                .build(),
        )
        Prefs.setDriveTokens(
            c, res.getString("access_token"), res.optString("refresh_token"),
            now() + res.getLong("expires_in"),
        )
    }

    @Throws(JSONException::class, IOException::class)
    private fun freshToken(c: Context): String {
        if (now() < Prefs.driveExpiresAt(c) - 60) return Prefs.driveAccessToken(c)
        val res = post(
            TOKEN,
            FormBody.Builder()
                .add("client_id", Prefs.driveClientId(c))
                .add("client_secret", Prefs.driveClientSecret(c))
                .add("grant_type", "refresh_token")
                .add("refresh_token", Prefs.driveRefreshToken(c))
                .build(),
        )
        val access = res.getString("access_token")
        Prefs.setDriveTokens(c, access, null, now() + res.getLong("expires_in"))
        return access
    }

    @Throws(JSONException::class, IOException::class)
    fun uploadGpx(c: Context, file: File): String {
        val token = freshToken(c)
        val meta = JSONObject().put("name", file.name)
            .put("parents", JSONArray().put(ensureFolder(c, token)))
        val body = MultipartBody.Builder().setType("multipart/related".toMediaType())
            .addPart(meta.toString().toRequestBody(JSON))
            .addPart(file.asRequestBody("application/gpx+xml".toMediaType()))
            .build()
        val req = Request.Builder()
            .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: ""
            if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code}: $txt")
            return "Uploaded to Drive"
        }
    }

    /**
     * Uploads any saved rides not yet on Drive; silent on failure so they simply retry next time
     * connectivity is available. Returns the number newly uploaded.
     */
    fun uploadPendingRides(c: Context): Int {
        if (!Prefs.driveConnected(c) || !Prefs.driveAutoUpload(c)) return 0
        var n = 0
        for (s in RideHistory.all()) {
            if (s.uploaded || s.gpx == null) continue
            val f = File("/sdcard/BikeComputer/rides", s.gpx)
            if (!f.exists()) continue
            try {
                uploadGpx(c, f)
                RideHistory.markUploaded(s.startMs)
                n++
            } catch (t: Throwable) {
                // leave unuploaded; retry later
            }
        }
        return n
    }

    @Throws(JSONException::class, IOException::class)
    private fun ensureFolder(c: Context, token: String): String {
        Prefs.driveFolderId(c).takeIf { it.isNotEmpty() }?.let { return it }
        val name = Prefs.driveRidesFolder(c)
        findFolderId(token, name)?.let {
            Prefs.setDriveFolderId(c, it)
            return it
        }
        val meta = JSONObject().put("name", name).put("mimeType", "application/vnd.google-apps.folder")
        val req = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files?fields=id")
            .header("Authorization", "Bearer $token")
            .post(meta.toString().toRequestBody(JSON))
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: "{}"
            val id = JSONObject(txt).getString("id")
            Prefs.setDriveFolderId(c, id)
            return id
        }
    }

    @Throws(IOException::class)
    private fun findFolderId(token: String, name: String): String? {
        val safe = name.replace("\\", "\\\\").replace("'", "\\'")
        val q = enc("mimeType='application/vnd.google-apps.folder' and name='$safe' and trashed=false")
        val req = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files?q=$q&fields=files(id)")
            .header("Authorization", "Bearer $token")
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: "{}"
            val files = try {
                JSONObject(txt).optJSONArray("files")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            return if (files == null || files.length() <= 0) null else files.getJSONObject(0).getString("id")
        }
    }

    @Throws(JSONException::class, IOException::class)
    fun listRoutes(c: Context): List<DriveFile> {
        val token = freshToken(c)
        val name = Prefs.driveRoutesFolder(c)
        val folder = findFolderId(token, name)
            ?: throw RuntimeException("Can't find Drive folder \"$name\" — create it & add .gpx files, or Reconnect Drive to grant read access")
        val q = enc("'$folder' in parents and trashed=false")
        val req = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files?q=$q&fields=files(id,name)&pageSize=300&orderBy=name")
            .header("Authorization", "Bearer $token")
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: ""
            if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code}: $txt")
            val arr = JSONObject(txt).optJSONArray("files") ?: return emptyList()
            return (0 until arr.length())
                .map { DriveFile(arr.getJSONObject(it).getString("id"), arr.getJSONObject(it).getString("name")) }
                .filter { it.name.endsWith(".gpx", true) }
        }
    }

    @Throws(JSONException::class, IOException::class)
    fun download(c: Context, fileId: String): String {
        val token = freshToken(c)
        val req = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files/$fileId?alt=media")
            .header("Authorization", "Bearer $token")
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: ""
            if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code}: $txt")
            return txt
        }
    }

    @Throws(JSONException::class, IOException::class)
    fun syncRoutes(c: Context, localDir: String): Int {
        val dir = File(localDir)
        dir.mkdirs()
        val added = try {
            syncGpxFiles(c, dir)
        } catch (t: Throwable) {
            0
        }
        return added + syncLinkSheet(c, dir)
    }

    @Throws(JSONException::class, IOException::class)
    private fun syncGpxFiles(c: Context, dir: File): Int {
        var added = 0
        for (f in listRoutes(c)) {
            val local = File(dir, f.name)
            if (!local.exists()) {
                val gpx = try {
                    download(c, f.id)
                } catch (t: Throwable) {
                    null
                }
                if (gpx != null && GpxRoute.parse(gpx).size >= 2) {
                    local.writeText(gpx)
                    added++
                }
            }
        }
        return added
    }

    @Throws(JSONException::class, IOException::class)
    fun ensureLinksSheet(c: Context): String {
        Prefs.driveSheetId(c).takeIf { it.isNotEmpty() }?.let { return it }
        val token = freshToken(c)
        val folder = ensureRoutesFolder(c, token)
        findSheetInFolder(token, folder)?.let {
            Prefs.setDriveSheetId(c, it)
            return it
        }
        val meta = JSONObject().put("name", "Harmin Route Links")
            .put("mimeType", "application/vnd.google-apps.spreadsheet")
            .put("parents", JSONArray().put(folder))
        val body = MultipartBody.Builder().setType("multipart/related".toMediaType())
            .addPart(meta.toString().toRequestBody(JSON))
            .addPart("Name,Google Maps Link\n".toRequestBody("text/csv".toMediaType()))
            .build()
        val req = Request.Builder()
            .url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id")
            .header("Authorization", "Bearer $token")
            .post(body)
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: ""
            if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code}: $txt")
            val id = JSONObject(txt).getString("id")
            Prefs.setDriveSheetId(c, id)
            return id
        }
    }

    @Throws(JSONException::class, IOException::class)
    private fun syncLinkSheet(c: Context, dir: File): Int {
        val id = ensureLinksSheet(c)
        val rows = parseCsv(exportCsv(c, id)).filter { r -> r.any { it.isNotBlank() } }
        val start = if (rows.isNotEmpty()) {
            val first = rows[0]
            if (!GmapsRoute.looksLikeLink(if (1 < first.size) first[1] else "")) 1 else 0
        } else {
            0
        }
        val linkMap = readLinkMap(dir)
        val seen = HashSet<String>()
        var added = 0
        var i = start
        while (i < rows.size) {
            val row = rows[i]
            val name = Regex("[/\\\\:*?\"<>|]")
                .replace((if (row.isNotEmpty()) row[0] else "").trim(), "_").take(60)
            val link = (if (1 < row.size) row[1] else "").trim()
            if (name.isNotEmpty() && GmapsRoute.looksLikeLink(link)) {
                seen.add(name)
                val file = File(dir, "$name.gpx")
                if (!file.exists() || linkMap[name] != link) {
                    val pts = try {
                        GmapsRoute.points(link)
                    } catch (t: Throwable) {
                        emptyList()
                    }
                    if (pts.size >= 2) {
                        file.writeText(GmapsRoute.toGpx(name, pts))
                        linkMap[name] = link
                        added++
                    }
                }
            }
            i++
        }
        for (n in linkMap.keys.toList()) {
            if (!seen.contains(n)) {
                File(dir, "$n.gpx").delete()
                linkMap.remove(n)
            }
        }
        writeLinkMap(dir, linkMap)
        return added
    }

    @Throws(JSONException::class, IOException::class)
    private fun exportCsv(c: Context, id: String): String {
        val token = freshToken(c)
        val req = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files/$id/export?mimeType=text%2Fcsv")
            .header("Authorization", "Bearer $token")
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: ""
            if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code}: $txt")
            return txt
        }
    }

    @Throws(JSONException::class, IOException::class)
    private fun ensureRoutesFolder(c: Context, token: String): String {
        val name = Prefs.driveRoutesFolder(c)
        findFolderId(token, name)?.let { return it }
        val meta = JSONObject().put("name", name).put("mimeType", "application/vnd.google-apps.folder")
        val req = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files?fields=id")
            .header("Authorization", "Bearer $token")
            .post(meta.toString().toRequestBody(JSON))
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: "{}"
            return JSONObject(txt).getString("id")
        }
    }

    @Throws(IOException::class)
    private fun findSheetInFolder(token: String, folder: String): String? {
        val q = enc("'$folder' in parents and mimeType='application/vnd.google-apps.spreadsheet' and trashed=false")
        val req = Request.Builder()
            .url("https://www.googleapis.com/drive/v3/files?q=$q&fields=files(id)")
            .header("Authorization", "Bearer $token")
            .build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: "{}"
            val files = try {
                JSONObject(txt).optJSONArray("files")
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
            return if (files == null || files.length() <= 0) null else files.getJSONObject(0).getString("id")
        }
    }

    private fun readLinkMap(dir: File): HashMap<String, String> {
        val f = File(dir, ".link_sync.json")
        if (!f.exists()) return HashMap()
        return try {
            val o = JSONObject(f.readText())
            val m = HashMap<String, String>()
            val keys = o.keys()
            while (keys.hasNext()) {
                val k = keys.next()
                m[k] = o.getString(k)
            }
            m
        } catch (t: Throwable) {
            HashMap()
        }
    }

    private fun writeLinkMap(dir: File, m: Map<String, String>) {
        try {
            File(dir, ".link_sync.json").writeText(JSONObject(m as Map<*, *>).toString())
        } catch (t: Throwable) {
        }
    }

    private fun parseCsv(text: String): List<List<String>> {
        val rows = ArrayList<List<String>>()
        var row = ArrayList<String>()
        val field = StringBuilder()
        var i = 0
        var inQ = false
        while (i < text.length) {
            val ch = text[i]
            if (inQ) {
                if (ch == '"') {
                    if (i + 1 >= text.length || text[i + 1] != '"') {
                        inQ = false
                    } else {
                        field.append('"')
                        i++
                    }
                } else {
                    field.append(ch)
                }
            } else {
                when (ch) {
                    '\n' -> {
                        row.add(field.toString()); field.setLength(0)
                        rows.add(row); row = ArrayList()
                    }
                    '\r' -> {}
                    '"' -> inQ = true
                    ',' -> {
                        row.add(field.toString()); field.setLength(0)
                    }
                    else -> field.append(ch)
                }
            }
            i++
        }
        if (field.isNotEmpty() || row.isNotEmpty()) {
            row.add(field.toString())
            rows.add(row)
        }
        return rows
    }

    @Throws(IOException::class)
    private fun post(url: String, body: RequestBody): JSONObject {
        val req = Request.Builder().url(url).post(body).build()
        http.newCall(req).execute().use { resp ->
            val txt = resp.body?.string() ?: ""
            if (!resp.isSuccessful) throw RuntimeException("HTTP ${resp.code}: $txt")
            return JSONObject(txt)
        }
    }

    private fun now(): Long = System.currentTimeMillis() / 1000
}

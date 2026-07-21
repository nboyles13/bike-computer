package com.bike.computer

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.GZIPInputStream

/**
 * OkHttp interceptor that serves MapLibre vector tiles and glyphs from a local
 * `.mbtiles` SQLite DB / fonts dir, keyed off the synthetic host `bike.local`.
 */
class LocalTiles(mbtilesPath: String, private val fontsDir: String) : Interceptor {
    private val TAG = "BikeTiles"
    private val PBF: MediaType = "application/x-protobuf".toMediaType()
    private val db: SQLiteDatabase = SQLiteDatabase.openDatabase(mbtilesPath, null, 1)
    private var served = 0

    override fun intercept(chain: Interceptor.Chain): Response {
        val req = chain.request()
        if (req.url.host != "bike.local") {
            return chain.proceed(req)
        }
        val segs = req.url.pathSegments
        var body: ByteArray? = null
        try {
            when (segs.firstOrNull()) {
                "tiles" -> body = tile(segs)
                "fonts" -> body = font(req.url)
            }
        } catch (e: Exception) {
            Log.e(TAG, "intercept ${req.url.encodedPath} failed", e)
        }
        if (served++ < 6) {
            Log.i(TAG, "serve ${req.url.encodedPath} -> ${body?.size ?: -1} bytes")
        }
        return Response.Builder()
            .request(req)
            .protocol(Protocol.HTTP_1_1)
            .code(if (body != null) 200 else 404)
            .message(if (body != null) "OK" else "Not Found")
            .body((body ?: ByteArray(0)).toResponseBody(PBF))
            .build()
    }

    private fun tile(segs: List<String>): ByteArray? {
        val z = segs[1].toInt()
        val x = segs[2].toInt()
        val y = segs[3].removeSuffix(".pbf").toInt()
        val tmsY = ((1 shl z) - 1) - y
        db.rawQuery(
            "SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?",
            arrayOf(z.toString(), x.toString(), tmsY.toString()),
        ).use { c ->
            if (!c.moveToFirst()) return null
            return gunzip(c.getBlob(0))
        }
    }

    private fun gunzip(b: ByteArray): ByteArray {
        if (b.size < 2 || (b[0].toInt() and 0xFF) != 31 || (b[1].toInt() and 0xFF) != 139) {
            return b
        }
        GZIPInputStream(ByteArrayInputStream(b)).use { g ->
            val out = ByteArrayOutputStream(b.size * 5)
            g.copyTo(out, 8192)
            return out.toByteArray()
        }
    }

    private fun font(url: HttpUrl): ByteArray? {
        val stack = url.pathSegments[1]
        val range = url.pathSegments[2]
        val candidates = stack.split(",").map { it.trim() } +
            listOf("KlokanTech Noto Sans Regular", "KlokanTech Noto Sans Bold")
        for (name in candidates) {
            val f = File(File(fontsDir, name), range)
            if (f.exists()) return f.readBytes()
        }
        return null
    }
}

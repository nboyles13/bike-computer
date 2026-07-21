package com.bike.computer

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.TimeUnit

/**
 * Resolves a Google Maps directions link (incl. shortened goo.gl/maps.app) into an
 * ordered list of stops, then into route points and a GPX <rte>.
 */
object GmapsRoute {
    private const val UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0 Safari/537.36"
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
    private val coordRe = Regex("^(-?\\d{1,3}\\.\\d+),(-?\\d{1,3}\\.\\d+)$")
    private val atRe = Regex("@(-?\\d{1,3}\\.\\d+),(-?\\d{1,3}\\.\\d+)")
    private val placeRe = Regex("!3d(-?\\d{1,3}\\.\\d+)!4d(-?\\d{1,3}\\.\\d+)")
    private val dblRe = Regex("!1d(-?\\d{1,3}\\.\\d+)!2d(-?\\d{1,3}\\.\\d+)")
    private val fullUrlRe =
        Regex("https?://(?:www\\.)?(?:google\\.[a-z.]+|maps\\.google\\.[a-z.]+)/maps[^\"'\\\\ ]+")
    private val ignoreNames = setOf("your location", "my location", "current location", "")

    sealed class Stop {
        data class At(val lat: Double, val lon: Double) : Stop()
        data class Named(val q: String) : Stop()
    }

    fun looksLikeLink(s: String): Boolean {
        val t = s.trim().lowercase()
        return t.contains("goo.gl") ||
            (t.contains("google.") && t.contains("/maps")) ||
            t.contains("maps.app")
    }

    fun resolve(link: String): List<Stop> = parse(expand(link.trim()))

    fun toPoints(stops: List<Stop>): List<DoubleArray> {
        val near = (stops.firstOrNull { it is Stop.At } as? Stop.At)
            ?.let { doubleArrayOf(it.lat, it.lon) }
        val out = ArrayList<DoubleArray>()
        for (s in stops) {
            when (s) {
                is Stop.At -> out.add(doubleArrayOf(s.lon, s.lat))
                is Stop.Named -> {
                    val place = try {
                        Geocoder.search(s.q, near).firstOrNull()
                    } catch (e: IOException) {
                        throw RuntimeException(e)
                    }
                    if (place != null) out.add(doubleArrayOf(place.lon, place.lat))
                }
            }
        }
        return out
    }

    fun points(link: String): List<DoubleArray> = toPoints(resolve(link))

    private fun expand(url0: String): String {
        val str = if (url0.startsWith("http")) url0 else "https://$url0"
        val host = try {
            URI(str).host ?: ""
        } catch (t: Throwable) {
            ""
        }
        if (!host.contains("goo.gl")) return str
        return try {
            http.newCall(Request.Builder().url(str).header("User-Agent", UA).build())
                .execute().use { r ->
                    var finalUrl = r.request.url.toString()
                    if (finalUrl.contains("goo.gl")) {
                        val body = r.body?.string() ?: ""
                        val m = fullUrlRe.find(body)
                        if (m != null) {
                            finalUrl = m.value.replace("\\u003d", "=").replace("\\u0026", "&")
                        }
                    }
                    finalUrl
                }
        } catch (t: Throwable) {
            str
        }
    }

    private fun parse(url: String): List<Stop> {
        val query = parseQuery(url.substringAfter('?', ""))
        val origin = query["origin"] ?: query["saddr"]
        val destination = query["destination"] ?: query["daddr"]
        if (origin != null || destination != null) {
            val out = ArrayList<Stop>()
            if (origin != null) stop(origin)?.let { out.add(it) }
            val wp = query["waypoints"] ?: query["via"]
            if (wp != null) {
                for (w in wp.split('|', '\n')) stop(w)?.let { out.add(it) }
            }
            if (destination != null) stop(destination)?.let { out.add(it) }
            if (out.isNotEmpty()) return out
        }

        val dirIdx = url.indexOf("/dir/")
        if (dirIdx >= 0) {
            val list = dblRe.findAll(url)
                .map { Stop.At(it.groupValues[2].toDouble(), it.groupValues[1].toDouble()) }
                .toList()
            if (list.size >= 2) return list
            val after = url.substring(dirIdx + 5)
            val end = listOf(after.indexOf("/@"), after.indexOf("/data="), after.indexOf('?'))
                .filter { it >= 0 }.minOrNull() ?: after.length
            val out2 = ArrayList<Stop>()
            for (seg in after.substring(0, end).split('/')) {
                if (!seg.isBlank() && !(seg.startsWith("@") || seg.startsWith("data="))) {
                    stop(seg)?.let { out2.add(it) }
                }
            }
            if (out2.isNotEmpty()) return out2
        }

        placeRe.find(url)?.let {
            return listOf(Stop.At(it.groupValues[1].toDouble(), it.groupValues[2].toDouble()))
        }
        query["q"]?.let { qv -> stop(qv)?.let { return listOf(it) } }

        val placeIdx = url.indexOf("/place/")
        if (placeIdx >= 0) {
            val sub = url.substring(placeIdx + 7)
            val beforeSlash = sub.substringBefore('/', sub)
            val name = beforeSlash.substringBefore('@', beforeSlash)
            stop(name)?.let { return listOf(it) }
        }

        val at = atRe.find(url)
        return if (at != null) {
            listOf(Stop.At(at.groupValues[1].toDouble(), at.groupValues[2].toDouble()))
        } else {
            emptyList()
        }
    }

    private fun stop(raw: String): Stop? {
        val decoded = try {
            URLDecoder.decode(raw.trim(), "UTF-8")
        } catch (t: Throwable) {
            raw
        }
        val s = decoded.trim()
        coordRe.find(s.replace(" ", ""))?.let {
            return Stop.At(it.groupValues[1].toDouble(), it.groupValues[2].toDouble())
        }
        if (s.lowercase() in ignoreNames || s.startsWith("place_id:")) return null
        return Stop.Named(s)
    }

    private fun parseQuery(q: String): Map<String, String> {
        if (q.isEmpty()) return emptyMap()
        val m = HashMap<String, String>()
        for (kv in q.split('&')) {
            val i = kv.indexOf('=')
            if (i > 0) {
                val k = kv.substring(0, i)
                val v = try {
                    URLDecoder.decode(kv.substring(i + 1), "UTF-8")
                } catch (t: Throwable) {
                    kv.substring(i + 1)
                }
                m[k] = v
            }
        }
        return m
    }

    fun toGpx(name: String, pts: List<DoubleArray>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        sb.append("<gpx version=\"1.1\" creator=\"Harmin\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n")
        sb.append("<metadata><name>").append(xml(name)).append("</name></metadata>\n<rte>\n")
        for (p in pts) {
            sb.append("<rtept lat=\"").append(p[1]).append("\" lon=\"").append(p[0]).append("\"></rtept>\n")
        }
        sb.append("</rte>\n</gpx>\n")
        return sb.toString()
    }

    private fun xml(s: String): String =
        s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
}

package com.bike.computer

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Downloads and parses GPX track/route files into point lists and via waypoints. */
object GpxRoute {
    private val http = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    private val ptRe = Regex("<(?:trkpt|rtept)\\b([^>]*)>", RegexOption.IGNORE_CASE)
    private val latRe = Regex("lat=\"([-0-9.]+)\"", RegexOption.IGNORE_CASE)
    private val lonRe = Regex("lon=\"([-0-9.]+)\"", RegexOption.IGNORE_CASE)
    private val nameRe = Regex(
        "<name>(.*?)</name>",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
    )

    @Throws(IOException::class)
    fun download(url: String): String {
        val u = if (url.startsWith("http")) url else "https://$url"
        http.newCall(
            Request.Builder().url(u).header("User-Agent", "BikeComputer").build(),
        ).execute().use { r ->
            val body = r.body?.string() ?: ""
            if (!r.isSuccessful) throw RuntimeException("HTTP ${r.code}")
            return body
        }
    }

    fun name(gpx: String): String? {
        val name = nameRe.find(gpx)?.groupValues?.get(1)?.trim() ?: return null
        return name.ifEmpty { null }
    }

    fun parse(gpx: String): List<DoubleArray> {
        val pts = ArrayList<DoubleArray>()
        for (m in ptRe.findAll(gpx)) {
            val a = m.groupValues[1]
            val lat = latRe.find(a)?.groupValues?.get(1)?.toDoubleOrNull() ?: continue
            val lon = lonRe.find(a)?.groupValues?.get(1)?.toDoubleOrNull() ?: continue
            pts.add(doubleArrayOf(lon, lat))
        }
        return pts
    }

    @JvmOverloads
    fun toWaypoints(
        points: List<DoubleArray>,
        spacingM: Double = 1200.0,
        maxVias: Int = 24,
    ): List<DoubleArray> {
        if (points.size <= 2) return points
        val total = length(points)
        val spacing = maxOf(spacingM, total / maxVias)
        val out = ArrayList<DoubleArray>()
        out.add(points.first())
        var acc = 0.0
        for (i in 1 until points.size - 1) {
            acc += hav(points[i - 1], points[i])
            if (acc >= spacing) {
                out.add(points[i])
                acc = 0.0
            }
        }
        out.add(points.last())
        return out
    }

    private fun length(p: List<DoubleArray>): Double {
        var s = 0.0
        for (i in 1 until p.size) s += hav(p[i - 1], p[i])
        return s
    }

    private fun hav(a: DoubleArray, b: DoubleArray): Double {
        val p1 = Math.toRadians(a[1])
        val p2 = Math.toRadians(b[1])
        val dp = Math.toRadians(b[1] - a[1])
        val dl = Math.toRadians(b[0] - a[0])
        val s1 = Math.sin(dp / 2)
        val c = Math.cos(p1) * Math.cos(p2)
        val s2 = Math.sin(dl / 2)
        val h = s1 * s1 + c * s2 * s2
        return 2 * 6371000.0 * Math.asin(Math.sqrt(h))
    }
}

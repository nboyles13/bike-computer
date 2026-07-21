package com.bike.computer

import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Parses recorded ride GPX files into ride summaries and HR-zone time buckets. */
object GpxSummary {
    private val fnameFmt = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val nameRe = Regex("ride_(\\d{8}_\\d{6})")
    private val trkptRe = Regex(
        "<trkpt[^>]*?lat=\"([-0-9.]+)\"[^>]*?lon=\"([-0-9.]+)\"[^>]*?>(.*?)</trkpt>",
        RegexOption.DOT_MATCHES_ALL,
    )
    private val timeRe = Regex("<time>([^<]+)</time>")
    private val eleRe = Regex("<ele>([-0-9.]+)</ele>")
    private val hrRe = Regex("<gpxtpx:hr>(\\d+)</gpxtpx:hr>")
    private val powRe = Regex("<power>(\\d+)</power>")

    fun startMsFromName(f: File): Long? {
        val m = nameRe.find(f.name) ?: return null
        return try {
            fnameFmt.parse(m.groupValues[1])?.time
        } catch (t: Throwable) {
            null
        }
    }

    private fun parseIso(value: String): Long =
        try {
            iso.parse(value)?.time ?: 0L
        } catch (t: Throwable) {
            0L
        }

    fun summarize(f: File): RideSummary? {
        val start = startMsFromName(f) ?: return null
        val text = try {
            f.readText()
        } catch (t: Throwable) {
            null
        } ?: return null

        var lastLat = Double.NaN
        var lastLon = Double.NaN
        var lastT = 0L
        var firstT = 0L
        var lastTime = 0L
        var dist = 0.0
        var movingMs = 0L
        var maxSpd = 0.0
        var prevSpd = -1.0
        var ascent = 0.0
        var eleRef = Double.NaN
        var hrSum = 0L
        var hrCnt = 0
        var hrMax = 0
        var powSum = 0L
        var powCnt = 0
        var powMax = 0

        for (m in trkptRe.findAll(text)) {
            val lat = m.groupValues[1].toDoubleOrNull() ?: continue
            val lon = m.groupValues[2].toDoubleOrNull() ?: continue
            val body = m.groupValues[3]
            // Points without a <time> are skipped entirely (matches recovered behavior).
            val timeMatch = timeRe.find(body) ?: continue
            val t = parseIso(timeMatch.groupValues[1])
            if (t > 0) {
                if (firstT == 0L) firstT = t
                lastTime = t
            }
            if (!lastLat.isNaN()) {
                val d = haversine(lastLat, lastLon, lat, lon)
                dist += d
                if (lastT > 0 && t > lastT) {
                    val dt = t - lastT
                    val spd = d / (dt / 1000.0)
                    if (spd in 0.5..30.0) movingMs += dt
                    if (prevSpd >= 0.0) {
                        val sustained = minOf(spd, prevSpd)
                        if (sustained in 0.0..25.0 && sustained > maxSpd) maxSpd = sustained
                    }
                    prevSpd = spd
                }
            }
            lastLat = lat
            lastLon = lon
            if (t > 0) lastT = t

            eleRe.find(body)?.groupValues?.get(1)?.toDoubleOrNull()?.let { e ->
                if (eleRef.isNaN()) eleRef = e
                if (e - eleRef > 1.0) ascent += e - eleRef
                eleRef = e
            }
            hrRe.find(body)?.groupValues?.get(1)?.toIntOrNull()?.let { hr ->
                if (hr > 0) {
                    hrSum += hr
                    hrCnt++
                    if (hr > hrMax) hrMax = hr
                }
            }
            powRe.find(body)?.groupValues?.get(1)?.toIntOrNull()?.let { pw ->
                if (pw > 0) {
                    powSum += pw
                    powCnt++
                    if (pw > powMax) powMax = pw
                }
            }
        }

        if (dist <= 0.0 && hrCnt == 0) return null
        val elapsed = if (lastTime > firstT) lastTime - firstT else 0L
        val mv = if (movingMs > 0) movingMs else elapsed
        val avg = if (mv > 0) dist / (mv / 1000.0) else 0.0
        val hrAvg = if (hrCnt > 0) (hrSum / hrCnt).toInt() else 0
        val powerAvg = if (powCnt > 0) (powSum / powCnt).toInt() else 0
        // RideSummary is still decompiled Java (no Kotlin default args, no named args) — pass
        // all 14 positionally: uploaded=false, name=null.
        return RideSummary(
            start, null, dist, mv, avg.toFloat(), maxSpd.toFloat(),
            hrAvg, hrMax, ascent, powerAvg, powMax, f.name, false, null,
        )
    }

    fun zoneTimes(f: File, maxHr: Int): LongArray {
        val text = try {
            f.readText()
        } catch (t: Throwable) {
            ""
        }
        return zoneTimes(text, maxHr)
    }

    fun zoneTimes(text: String, maxHr: Int): LongArray {
        val zones = LongArray(5)
        var lastT = 0L
        for (m in trkptRe.findAll(text)) {
            val body = m.groupValues[3]
            val timeMatch = timeRe.find(body) ?: continue
            val t = parseIso(timeMatch.groupValues[1])
            if (t <= 0L) continue
            val hr = hrRe.find(body)?.groupValues?.get(1)?.toIntOrNull() ?: 0
            if (lastT > 0 && t > lastT && hr > 0) {
                val dt = (t - lastT).coerceAtMost(10000L)
                val z = zoneOf(hr, maxHr)
                if (z >= 0) zones[z] += dt
            }
            lastT = t
        }
        return zones
    }

    fun zoneOf(hr: Int, maxHr: Int): Int {
        if (hr <= 0 || maxHr <= 0) return -1
        val fr = hr.toDouble() / maxHr.toDouble()
        return when {
            fr >= 0.9 -> 4
            fr >= 0.8 -> 3
            fr >= 0.7 -> 2
            fr >= 0.6 -> 1
            else -> 0
        }
    }

    private fun haversine(la1: Double, lo1: Double, la2: Double, lo2: Double): Double {
        val p1 = Math.toRadians(la1)
        val p2 = Math.toRadians(la2)
        val dp = Math.toRadians(la2 - la1)
        val dl = Math.toRadians(lo2 - lo1)
        val a = Math.sin(dp / 2)
        val c = Math.cos(p1) * Math.cos(p2)
        val b = Math.sin(dl / 2)
        val h = a * a + c * b * b
        return 2 * 6371000.0 * Math.asin(Math.sqrt(h))
    }
}

package com.bike.computer

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.math.abs

/** Persists ride summaries to /sdcard/BikeComputer/rides.json and reconciles recorded GPX. */
object RideHistory {
    private const val FILE = "/sdcard/BikeComputer/rides.json"
    private const val GPX_DIR = "/sdcard/BikeComputer/rides"

    fun all(): List<RideSummary> {
        val f = File(FILE)
        if (!f.exists()) return emptyList()
        val list = try {
            val a = JSONArray(f.readText())
            (0 until a.length()).map { fromJson(a.getJSONObject(it)) }
        } catch (t: Throwable) {
            emptyList()
        }
        return list.sortedByDescending { it.startMs }
    }

    fun add(s: RideSummary) {
        writeAll(all().filter { it.startMs != s.startMs } + s)
    }

    fun byStart(startMs: Long): RideSummary? = all().firstOrNull { it.startMs == startMs }

    fun rename(startMs: Long, name: String?) {
        // NOTE: the decompiled copy$default mask was corrupt (kept the old name and only reset
        // `uploaded`, leaving the `name` argument as dead code — a jadx artifact). Restored to the
        // original intent: set the ride's name, keep everything else.
        writeAll(
            all().map {
                if (it.startMs == startMs) {
                    it.copy(
                        it.startMs, it.route, it.distanceM, it.movingMs, it.avgMps, it.maxMps,
                        it.hrAvg, it.hrMax, it.ascentM, it.powerAvg, it.powerMax, it.gpx,
                        it.uploaded, name,
                    )
                } else {
                    it
                }
            },
        )
    }

    fun delete(startMs: Long) {
        all().firstOrNull { it.startMs == startMs }?.gpx?.let { gpx ->
            try {
                File(GPX_DIR, gpx).delete()
            } catch (t: Throwable) {
            }
        }
        writeAll(all().filter { it.startMs != startMs })
    }

    fun markUploaded(startMs: Long) {
        writeAll(
            all().map {
                if (it.startMs == startMs) {
                    it.copy(
                        it.startMs, it.route, it.distanceM, it.movingMs, it.avgMps, it.maxMps,
                        it.hrAvg, it.hrMax, it.ascentM, it.powerAvg, it.powerMax, it.gpx,
                        true, it.name,
                    )
                } else {
                    it
                }
            },
        )
    }

    fun reconcile(): Boolean {
        val byStart = all().associateBy { it.startMs }.toMutableMap()
        val gpx = File(GPX_DIR).listFiles { f ->
            f.name.startsWith("ride_") && f.name.endsWith(".gpx")
        } ?: return false
        var changed = false
        for (f in gpx) {
            val start = GpxSummary.startMsFromName(f) ?: continue
            val exists = byStart.keys.any { abs(it - start) < 2000 }
            if (!exists) {
                val s = GpxSummary.summarize(f)
                if (s != null) {
                    byStart[s.startMs] = s
                    changed = true
                }
            }
        }
        if (changed) writeAll(byStart.values.toList())
        return changed
    }

    private fun writeAll(list: List<RideSummary>) {
        val arr = JSONArray()
        for (s in list.sortedByDescending { it.startMs }) {
            try {
                arr.put(toJson(s))
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        }
        val f = File(FILE)
        f.parentFile?.mkdirs()
        try {
            val tmp = File("/sdcard/BikeComputer/rides.json.tmp")
            tmp.writeText(arr.toString())
            if (!tmp.renameTo(f)) {
                f.writeText(arr.toString())
                tmp.delete()
            }
        } catch (t: Throwable) {
        }
    }

    fun forRoute(route: String): List<RideSummary> =
        all().filter { it.route == route }.sortedBy { it.movingMs }

    fun routeBests(): Map<String, RideSummary> =
        all().filter { it.route != null }
            .groupBy { it.route!! }
            .mapValues { (_, v) -> v.minByOrNull { it.movingMs }!! }

    private fun toJson(s: RideSummary): JSONObject {
        val o = JSONObject()
        o.put("startMs", s.startMs)
        s.route?.let { o.put("route", it) }
        o.put("distM", s.distanceM)
        o.put("movingMs", s.movingMs)
        o.put("avgMps", s.avgMps.toDouble())
        o.put("maxMps", s.maxMps.toDouble())
        o.put("hrAvg", s.hrAvg)
        o.put("hrMax", s.hrMax)
        o.put("ascentM", s.ascentM)
        o.put("powerAvg", s.powerAvg)
        o.put("powerMax", s.powerMax)
        s.gpx?.let { o.put("gpx", it) }
        if (s.uploaded) o.put("uploaded", true)
        s.name?.let { o.put("name", it) }
        return o
    }

    private fun fromJson(o: JSONObject): RideSummary {
        val route = if (o.isNull("route")) null else o.optString("route").ifBlank { null }
        val gpx = o.optString("gpx").ifBlank { null }
        val name = o.optString("name").ifBlank { null }
        return RideSummary(
            o.getLong("startMs"), route, o.optDouble("distM", 0.0), o.optLong("movingMs", 0L),
            o.optDouble("avgMps", 0.0).toFloat(), o.optDouble("maxMps", 0.0).toFloat(),
            o.optInt("hrAvg"), o.optInt("hrMax"), o.optDouble("ascentM", 0.0),
            o.optInt("powerAvg"), o.optInt("powerMax"), gpx, o.optBoolean("uploaded", false), name,
        )
    }
}

package com.bike.computer

import android.location.Location
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.io.Writer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Records a ride to a GPX file and accumulates live stats (distance, ele, HR zones, power). */
class RideRecorder(private val dir: String) {
    var points = 0
        private set
    var distanceM = 0.0
        private set
    var startMs = 0L
        private set
    var paused = false
        private set
    var maxSpeedMps = 0f
        private set
    var curEleM = 0.0
        private set
    var ascentM = 0.0
        private set
    var maxCadence = 0
        private set
    var hrMax = 0
        private set
    var powerMax = 0
        private set
    var maxHrForZones = 185

    val zoneMs = LongArray(5)
    val hrHistMs = LongArray(251)
    val eleSamples = ArrayList<FloatArray>()

    private var baroOffset = Double.NaN
    private var eleRef = Double.NaN
    private var eleSmoothed = Double.NaN
    private var hrCount = 0
    private var hrSum = 0L
    private var powerCount = 0
    private var powerSum = 0L
    private var last: Location? = null
    private var lastZoneTickMs = 0L
    private var pauseStartMs = 0L
    private var pausedAccumMs = 0L
    private var file: File? = null
    private var writer: Writer? = null

    private val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    private val fnameFmt = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    val powerAvg: Int
        get() = if (powerCount > 0) (powerSum / powerCount).toInt() else 0

    val hrAvg: Int
        get() = if (hrCount > 0) (hrSum / hrCount).toInt() else 0

    val isRecording: Boolean
        get() = writer != null

    val elapsedMs: Long
        get() {
            if (startMs == 0L) return 0L
            val now = System.currentTimeMillis()
            return (now - startMs) - pausedAccumMs - (if (paused) now - pauseStartMs else 0L)
        }

    val avgSpeedMps: Float
        get() = if (elapsedMs > 0) (distanceM / (elapsedMs / 1000.0)).toFloat() else 0f

    fun zoneOf(hr: Int): Int {
        if (hr <= 0) return -1
        val f = hr.toDouble() / maxHrForZones
        return when {
            f >= 0.9 -> 4
            f >= 0.8 -> 3
            f >= 0.7 -> 2
            f >= 0.6 -> 1
            else -> 0
        }
    }

    fun pause() {
        if (!isRecording || paused) return
        paused = true
        pauseStartMs = System.currentTimeMillis()
        lastZoneTickMs = 0L
    }

    fun resume() {
        if (isRecording && paused) {
            pausedAccumMs += System.currentTimeMillis() - pauseStartMs
            paused = false
            lastZoneTickMs = 0L
        }
    }

    @Throws(IOException::class)
    fun start() {
        File(dir).mkdirs()
        startMs = System.currentTimeMillis()
        points = 0
        distanceM = 0.0
        last = null
        paused = false
        pauseStartMs = 0L
        pausedAccumMs = 0L
        maxSpeedMps = 0f
        curEleM = 0.0
        ascentM = 0.0
        eleSmoothed = Double.NaN
        eleRef = Double.NaN
        baroOffset = Double.NaN
        maxCadence = 0
        hrMax = 0
        hrSum = 0L
        hrCount = 0
        powerMax = 0
        powerSum = 0L
        powerCount = 0
        zoneMs.fill(0L)
        hrHistMs.fill(0L)
        lastZoneTickMs = 0L
        eleSamples.clear()
        val f = File(dir, "ride_${fnameFmt.format(Date(startMs))}.gpx")
        file = f
        val w = BufferedWriter(OutputStreamWriter(FileOutputStream(f), Charsets.UTF_8), 8192)
        writer = w
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
        w.write("<gpx version=\"1.1\" creator=\"BikeComputer\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\">\n")
        w.write("<trk><name>Ride ${iso.format(Date(startMs))}</name><trkseg>\n")
        w.flush()
    }

    @JvmOverloads
    @Throws(IOException::class)
    fun add(loc: Location, hr: Int, power: Int = 0, cadence: Int = 0, baroAlt: Double? = null) {
        val w = writer
        if (w == null || paused) return
        if (power > 0) {
            if (power > powerMax) powerMax = power
            powerSum += power
            powerCount++
        }
        if (cadence > 0 && cadence > maxCadence) maxCadence = cadence
        last?.let { distanceM += it.distanceTo(loc).toDouble() }
        last = loc
        points++
        val spd = if (loc.hasSpeed()) loc.speed else 0f
        if (spd > maxSpeedMps) maxSpeedMps = spd

        var alt: Double? = null
        val gpsAlt = if (loc.hasAltitude()) loc.altitude else null
        if (baroAlt != null) {
            if (gpsAlt != null) {
                baroOffset = if (baroOffset.isNaN()) {
                    gpsAlt - baroAlt
                } else {
                    baroOffset * 0.995 + (gpsAlt - baroAlt) * 0.005
                }
            }
            alt = baroAlt + (if (baroOffset.isNaN()) 0.0 else baroOffset)
        } else if (gpsAlt != null) {
            alt = gpsAlt
        }
        if (alt != null) {
            eleSmoothed = if (eleSmoothed.isNaN()) alt else eleSmoothed * 0.8 + alt * 0.2
            curEleM = eleSmoothed
            val thresh = if (baroAlt != null) 1.5 else 3.0
            if (eleRef.isNaN()) eleRef = eleSmoothed
            if (eleSmoothed - eleRef > thresh) {
                ascentM += eleSmoothed - eleRef
                eleRef = eleSmoothed
            } else if (eleSmoothed < eleRef) {
                eleRef = eleSmoothed
            }
            if (eleSamples.isEmpty() || distanceM - eleSamples.last()[0] > 15.0) {
                eleSamples.add(floatArrayOf(distanceM.toFloat(), eleSmoothed.toFloat()))
            }
        }
        if (hr > 0) {
            if (hr > hrMax) hrMax = hr
            hrSum += hr
            hrCount++
            val now = System.currentTimeMillis()
            if (lastZoneTickMs > 0) {
                val dt = (now - lastZoneTickMs).coerceIn(0L, 5000L)
                val z = zoneOf(hr)
                if (z >= 0) zoneMs[z] += dt
                if (hr in 0 until 251) hrHistMs[hr] += dt
            }
            lastZoneTickMs = now
        }

        val t = if (loc.time > 0) loc.time else System.currentTimeMillis()
        val sb = StringBuilder(200)
        sb.append("<trkpt lat=\"").append(loc.latitude).append("\" lon=\"").append(loc.longitude).append("\">")
        if (alt != null) {
            sb.append("<ele>").append(String.format(Locale.US, "%.1f", eleSmoothed)).append("</ele>")
        }
        sb.append("<time>").append(iso.format(Date(t))).append("</time>")
        if (hr > 0 || power > 0 || cadence > 0) {
            sb.append("<extensions>")
            if (power > 0) sb.append("<power>").append(power).append("</power>")
            if (hr > 0 || cadence > 0) {
                sb.append("<gpxtpx:TrackPointExtension>")
                if (hr > 0) sb.append("<gpxtpx:hr>").append(hr).append("</gpxtpx:hr>")
                if (cadence > 0) sb.append("<gpxtpx:cad>").append(cadence).append("</gpxtpx:cad>")
                sb.append("</gpxtpx:TrackPointExtension>")
            }
            sb.append("</extensions>")
        }
        sb.append("</trkpt>\n")
        w.write(sb.toString())
        w.flush()
    }

    @Throws(IOException::class)
    fun stop(): String? {
        val w = writer ?: return null
        w.write("</trkseg></trk></gpx>\n")
        w.flush()
        w.close()
        writer = null
        return file?.absolutePath
    }
}

package com.bike.computer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.widget.Toast
import btools.router.NavHint
import java.util.Locale

/**
 * Foreground service that owns GPS + BLE sensors + barometer, records the ride, drives the
 * status LED, writes the ongoing notification, and (on critical battery) safely powers off.
 */
class RideService : Service(), LocationListener {
    companion object {
        const val ACTION_CANCEL_SHUTDOWN = "com.bike.computer.CANCEL_SHUTDOWN"
        const val ACTION_START_REC = "com.bike.computer.START_REC"
        const val ACTION_START_NAV = "com.bike.computer.START_NAV"
        private const val NAV_NEAR_M = 150.0
        private const val DATA = "/sdcard/BikeComputer"
        private const val CHAN = "ride"
        private const val CRIT_CHAN = "critbatt"
        private const val NOTIF_ID = 1
    }

    inner class LocalBinder : Binder() {
        val service: RideService get() = this@RideService
    }

    private val binder = LocalBinder()
    val recorder: RideRecorder by lazy { RideRecorder("/sdcard/BikeComputer/rides") }
    private val sensorMgr: SensorManager by lazy { getSystemService("sensor") as SensorManager }

    private lateinit var lm: LocationManager
    private var hrSensor: HrSensor? = null
    private var cyclingSensor: CyclingSensor? = null
    private var pressureSensor: Sensor? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var shutdownHandler: Handler? = null
    private val led = LedController()
    val trail = ArrayList<DoubleArray>()
    private val gradeWin = ArrayDeque<DoubleArray>()

    var lastLocation: Location? = null
        private set
    var lastHr = 0
        private set
    var hrStatus = "scanning…"
        private set
    var cyclingStatus = "scanning…"
        private set
    var curCadence = 0
        private set
    var curPower = 0
        private set
    var curSpeedMps = 0f
        private set
    var curEleM = 0.0
        private set
    var curGrade = 0f
        private set
    var manualPause = false
        private set
    var autoPauseEnabled = true
    var onUpdate: (() -> Unit)? = null

    // --- Navigation (owned here so it keeps running across activities / Home) ---
    var navigating = false
        private set
    var navRouteName: String? = null
        private set
    var navStartMs = 0L
        private set
    var navArrow = "↑"
        private set
    var navInstruction = "Continue"
        private set
    var navStreet = ""
        private set
    var navDistText = ""
        private set
    var navStepKey = Int.MIN_VALUE
        private set
    var navDTurnM = Double.MAX_VALUE
        private set
    var navRemainingM = 0.0
        private set
    var navArrived = false
        private set
    var onNavUpdate: (() -> Unit)? = null
    private var navPoints: List<DoubleArray> = emptyList()
    private var navSteps: List<NavHint> = emptyList()
    private var navVersion = 0
    private var progressIdx = 0
    private var destLat = 0.0
    private var destLon = 0.0
    private var routeVias: List<DoubleArray>? = null
    private var announcedIdx = -1
    private var earlyAnnouncedIdx = -1
    private var offRouteSince = 0L
    private var lastRerouteMs = 0L
    private var isForeground = false
    private val navUi = Handler(Looper.getMainLooper())
    val navVersionNum: Int get() = navVersion
    fun navPolyline(): List<DoubleArray> = navPoints

    var ledEnabled = true
        set(v) {
            field = v
            if (v) updateLed() else led.off()
        }

    private var criticalDone = false
    private var cumDistM = 0.0
    private var lowSpeedSince = 0L
    private var lastNotifMs = 0L
    private var lastTrailLoc: Location? = null
    private var shutdownSecs = 0
    private var wifiToggledOff = false
    private var baroAltM = Double.NaN

    private val hasBaro: Boolean get() = pressureSensor != null
    val hrConnected: Boolean get() = lastHr > 0
    val cyclingConnected: Boolean get() = curPower > 0 || curCadence > 0
    val hrDeviceName: String? get() = hrSensor?.deviceName
    val cyclingDeviceName: String? get() = cyclingSensor?.deviceName

    private val baroListener = object : SensorEventListener {
        override fun onSensorChanged(e: SensorEvent) {
            val alt = (1 - Math.pow(e.values[0].toDouble() / 1013.25, 0.19029495718363465)) * 44330.0
            baroAltM = if (baroAltM.isNaN()) alt else baroAltM * 0.85 + 0.15 * alt
        }

        override fun onAccuracyChanged(s: Sensor, a: Int) {}
    }

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, i: Intent) {
            val level = i.getIntExtra("level", -1)
            val scale = i.getIntExtra("scale", -1)
            val plugged = i.getIntExtra("plugged", 0)
            if (level < 0 || scale <= 0) return
            val pct = level * 100 / scale
            if (pct <= 2 && plugged == 0 && recorder.isRecording && !criticalDone) {
                criticalDone = true
                criticalShutdown()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        ledEnabled = Prefs.led(this)
        lm = getSystemService("location") as LocationManager
        pressureSensor = sensorMgr.getDefaultSensor(6)
        pressureSensor?.let { sensorMgr.registerListener(baroListener, it, 3) }
        startGps()
        hrSensor = newHrSensor().apply { start() }
        cyclingSensor = CyclingSensor(
            this,
            { w -> curPower = w; onUpdate?.invoke() },
            { c -> curCadence = c; onUpdate?.invoke() },
            { s -> cyclingStatus = s; onUpdate?.invoke() },
        ).apply { start() }
        registerReceiver(batteryReceiver, IntentFilter("android.intent.action.BATTERY_CHANGED"))
        Voice.enabled = Prefs.voice(this)
        Voice.init(this)
    }

    private fun criticalShutdown() {
        try {
            Runtime.getRuntime().exec(arrayOf("su", "-c", "input keyevent 224"))
        } catch (t: Throwable) {
        }
        shutdownSecs = 10
        shutdownHandler = Handler(mainLooper)
        tickShutdown()
    }

    private fun tickShutdown() {
        if (shutdownSecs <= 0) {
            doShutdown()
            return
        }
        try {
            (getSystemService("notification") as NotificationManager)
                .notify(2, buildShutdownNotification(shutdownSecs))
        } catch (t: Throwable) {
        }
        shutdownSecs--
        shutdownHandler?.postDelayed({ tickShutdown() }, 1000L)
    }

    fun abortShutdown() {
        shutdownHandler?.removeCallbacksAndMessages(null)
        shutdownHandler = null
        try {
            (getSystemService("notification") as NotificationManager).cancel(2)
        } catch (t: Throwable) {
        }
    }

    private fun doShutdown() {
        shutdownHandler = null
        try {
            (getSystemService("notification") as NotificationManager).cancel(2)
        } catch (t: Throwable) {
        }
        val rec = recorder
        val summary = if (rec.startMs > 0 && rec.points >= 2) {
            RideSummary(
                rec.startMs, null, rec.distanceM, rec.elapsedMs, rec.avgSpeedMps, rec.maxSpeedMps,
                rec.hrAvg, rec.hrMax, rec.ascentM, rec.powerAvg, rec.powerMax, null, false, null,
            )
        } else {
            null
        }
        val path = stopRecording()
        if (summary != null) {
            try {
                // NOTE: the decompiled copy$default mask was corrupt (it kept summary.gpx=null and
                // discarded the computed filename — a jadx artifact). Restored to intent: link the
                // saved .gpx file name.
                val gpxName = path?.substringAfterLast('/')
                RideHistory.add(
                    summary.copy(
                        summary.startMs, summary.route, summary.distanceM, summary.movingMs,
                        summary.avgMps, summary.maxMps, summary.hrAvg, summary.hrMax, summary.ascentM,
                        summary.powerAvg, summary.powerMax, gpxName, summary.uploaded, summary.name,
                    ),
                )
            } catch (t: Throwable) {
            }
        }
        Thread {
            try {
                Runtime.getRuntime().exec(arrayOf("su", "-c", "sync")).waitFor()
            } catch (t: Throwable) {
            }
            try {
                Thread.sleep(1500L)
            } catch (e: Exception) {
            }
            try {
                Runtime.getRuntime().exec(arrayOf("su", "-c", "svc power shutdown")).waitFor()
            } catch (t: Throwable) {
            }
            try {
                Thread.sleep(1500L)
            } catch (e: Exception) {
            }
            try {
                Runtime.getRuntime().exec(arrayOf("su", "-c", "reboot -p")).waitFor()
            } catch (t: Throwable) {
            }
        }.start()
    }

    private fun buildShutdownNotification(sec: Int): Notification {
        val cancel = PendingIntent.getService(
            this, 99,
            Intent(this, RideService::class.java).setAction(ACTION_CANCEL_SHUTDOWN),
            201326592,
        )
        return Notification.Builder(this, CRIT_CHAN)
            .setContentTitle("Battery 2% — powering off in ${sec}s")
            .setContentText("Saving your ride. Tap Cancel to keep riding.")
            .setSmallIcon(android.R.drawable.ic_lock_idle_low_battery)
            .setOngoing(true)
            .addAction(0, "Cancel", cancel)
            .build()
    }

    private fun newHrSensor(): HrSensor = HrSensor(
        this,
        { bpm -> lastHr = bpm; updateLed(); onUpdate?.invoke() },
        { },
        { s -> hrStatus = s; onUpdate?.invoke() },
    )

    private fun startGps() {
        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != 0) return
        try {
            lm.requestLocationUpdates("gps", 2000L, 0f, this, Looper.getMainLooper())
        } catch (e: Exception) {
        }
    }

    fun retryPermissions() {
        startGps()
        if (hrSensor == null) {
            hrSensor = newHrSensor().apply { start() }
        }
    }

    private fun setWifi(on: Boolean) {
        if (on) {
            if (wifiToggledOff) {
                wifiToggledOff = false
                Prefs.setWifiDisabledByApp(this, false)
                rootExec("svc wifi enable")
            }
        } else if (Prefs.wifiOffOnRide(this)) {
            wifiToggledOff = true
            Prefs.setWifiDisabledByApp(this, true)
            rootExec("svc wifi disable")
        }
    }

    private fun rootExec(cmd: String) {
        Thread {
            try {
                Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)).waitFor()
            } catch (e: Exception) {
            }
        }.start()
    }

    private fun updateLed() {
        if (!ledEnabled || Prefs.endurance(this)) {
            led.off()
            return
        }
        val z = if (recorder.isRecording && lastHr > 0) recorder.zoneOf(lastHr) else -1
        if (recorder.isRecording && lastHr > 0 && z >= 0) {
            val c = HrZone.entries[z].color.toInt()
            led.set(Color.red(c), Color.green(c), Color.blue(c))
        } else {
            led.off()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_CANCEL_SHUTDOWN) {
            abortShutdown()
            return 1
        }
        if (intent?.action == ACTION_START_NAV) {
            ensureForeground()
        }
        if (intent?.action == ACTION_START_REC && !recorder.isRecording) {
            trail.clear()
            lastTrailLoc = null
            recorder.maxHrForZones = Prefs.maxHr(this)
            recorder.start()
            ensureForeground()
            acquireWake()
            updateLed()
            if (Prefs.closeApps(this)) closeOtherApps()
            setWifi(false)
        }
        return 1
    }

    private fun ensureForeground() {
        if (!isForeground) {
            try {
                startForeground(1, buildNotification())
            } catch (t: Throwable) {
            }
            isForeground = true
        }
    }

    /** Stop the service only when neither recording nor navigating needs it alive. */
    private fun stopIfIdle() {
        if (!recorder.isRecording && !navigating) {
            try {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } catch (t: Throwable) {
            }
            isForeground = false
            stopSelf()
        }
    }

    private fun updateGrade(loc: Location, prev: Location?) {
        if (curSpeedMps < 1f) {
            curGrade *= 0.8f
            return
        }
        if (prev != null) cumDistM += prev.distanceTo(loc).toDouble()
        val alt = when {
            hasBaro && !baroAltM.isNaN() -> baroAltM
            !loc.hasAltitude() -> return
            else -> loc.altitude
        }
        gradeWin.addLast(doubleArrayOf(cumDistM, alt))
        while (gradeWin.size > 2 && cumDistM - gradeWin.first()[0] > 20.0) {
            gradeWin.removeFirst()
        }
        val f = gradeWin.first()
        val run = cumDistM - f[0]
        if (run >= 5.0) {
            val g = (((alt - f[1]) / run) * 100.0).toFloat().coerceIn(-40f, 40f)
            curGrade = curGrade * 0.7f + 0.3f * g
        }
    }

    private fun autoPauseCheck() {
        if (recorder.isRecording && autoPauseEnabled && !manualPause) {
            if (curSpeedMps < 0.8f) {
                if (lowSpeedSince == 0L) lowSpeedSince = System.currentTimeMillis()
                if (recorder.paused || System.currentTimeMillis() - lowSpeedSince <= 4000) return
                recorder.pause()
                return
            }
            lowSpeedSince = 0L
            if (recorder.paused) recorder.resume()
        }
    }

    fun rescanSensor() {
        try {
            hrSensor?.stop()
        } catch (e: Exception) {
        }
        lastHr = 0
        hrStatus = "scanning…"
        hrSensor = newHrSensor().apply { start() }
    }

    fun togglePause() {
        if (recorder.isRecording) {
            if (!recorder.paused) {
                recorder.pause()
                manualPause = true
            } else {
                recorder.resume()
                manualPause = false
            }
        }
    }

    fun stopRecording(): String? {
        manualPause = false
        lowSpeedSince = 0L
        led.off()
        val path = recorder.stop()
        trail.clear()
        lastTrailLoc = null
        releaseWake()
        setWifi(true)
        stopIfIdle()
        return path
    }

    // ---- Navigation engine ----

    fun startNavigation(dLat: Double, dLon: Double) {
        val from = lastLocation
        if (from == null) {
            toast("No GPS fix yet")
            return
        }
        routeVias = null
        navRouteName = null
        destLat = dLat
        destLon = dLon
        resetNavProgress()
        toast("Routing…")
        computeRoute(from.latitude, from.longitude, true)
    }

    fun startRouteNavigation(vias: List<DoubleArray>, name: String?) {
        val from = lastLocation
        if (from == null) {
            toast("No GPS fix yet — try again in a moment")
            return
        }
        if (vias.size < 2) {
            toast("Route has no points")
            return
        }
        routeVias = vias
        navRouteName = name
        destLat = vias.last()[1]
        destLon = vias.last()[0]
        resetNavProgress()
        toast("Snapping route to roads…")
        computeRoute(from.latitude, from.longitude, true)
    }

    fun cancelNavigation() {
        navigating = false
        navSteps = emptyList()
        navPoints = emptyList()
        routeVias = null
        navRouteName = null
        navStepKey = Int.MIN_VALUE
        navDTurnM = Double.MAX_VALUE
        navRemainingM = 0.0
        navArrived = false
        navVersion++
        ActionBus.navigating = false
        ActionBus.navRouteName = null
        ActionBus.navRemainingM = 0.0
        ActionBus.navStartMs = 0L
        onNavUpdate?.invoke()
        stopIfIdle()
    }

    private fun resetNavProgress() {
        announcedIdx = -1
        earlyAnnouncedIdx = -1
        offRouteSince = 0L
        progressIdx = 0
    }

    private fun reroute(fromLat: Double, fromLon: Double) {
        Voice.cue("Rerouting")
        toast("Off route — rerouting…")
        val list = routeVias
        if (list != null) {
            var k = 0
            var best = Double.MAX_VALUE
            for (i in list.indices) {
                val d = hav(fromLat, fromLon, list[i][1], list[i][0])
                if (d < best) {
                    best = d
                    k = i
                }
            }
            if (k in 1 until list.size) routeVias = list.subList(k, list.size).toList()
        }
        computeRoute(fromLat, fromLon, false)
    }

    private fun computeRoute(fromLat: Double, fromLon: Double, initial: Boolean) {
        lastRerouteMs = System.currentTimeMillis()
        Thread {
            val vias = routeVias
            val waypoints = if (vias != null) {
                listOf(doubleArrayOf(fromLon, fromLat)) + vias
            } else {
                listOf(doubleArrayOf(fromLon, fromLat), doubleArrayOf(destLon, destLat))
            }
            val res = BikeRouter.route("$DATA/routing", "$DATA/routing/trekking.brf", waypoints)
            val named: List<NavHint>? = if (res != null) {
                StreetNames.open("$filesDir/california.mbtiles")
                res.steps.map { s ->
                    val nm = streetAfterTurn(res.points, s.indexInTrack, s.lat, s.lon)
                    if (nm.isNotEmpty()) NavHint(s.lat, s.lon, s.indexInTrack, s.cmd, nm) else s
                }
            } else {
                null
            }
            navUi.post { onRouteComputed(res, initial, named) }
        }.start()
    }

    /**
     * Name the road being turned *onto*, not the one being left: sample a point ~25 m past the
     * maneuver node along the track. At the exact junction a name lookup can return either street.
     */
    private fun streetAfterTurn(pts: List<DoubleArray>, idx: Int, turnLat: Double, turnLon: Double): String {
        if (pts.isEmpty()) return StreetNames.nameAt(turnLat, turnLon)
        var i = idx.coerceIn(0, pts.size - 1)
        var lat = pts[i][1]
        var lon = pts[i][0]
        var acc = 0.0
        while (i + 1 < pts.size && acc < 25.0) {
            val nlat = pts[i + 1][1]
            val nlon = pts[i + 1][0]
            acc += hav(lat, lon, nlat, nlon)
            i++
            lat = nlat
            lon = nlon
        }
        val nm = StreetNames.nameAt(lat, lon)
        return if (nm.isNotEmpty()) nm else StreetNames.nameAt(turnLat, turnLon)
    }

    private fun onRouteComputed(res: RouteResult?, initial: Boolean, named: List<NavHint>?) {
        if (res == null) {
            toast(if (initial) "No route found" else "Reroute failed")
            if (initial && !navigating) stopIfIdle()
            return
        }
        navPoints = res.points
        navSteps = named ?: res.steps
        resetNavProgress()
        navStepKey = Int.MIN_VALUE
        navDTurnM = Double.MAX_VALUE
        navArrived = false
        navigating = true
        navVersion++
        if (initial) navStartMs = System.currentTimeMillis()
        ensureForeground()
        ActionBus.navigating = true
        ActionBus.navRouteName = navRouteName
        ActionBus.navDestLat = destLat
        ActionBus.navDestLon = destLon
        if (initial) ActionBus.navStartMs = navStartMs
        if (initial) {
            val mi = String.format(Locale.US, "%.1f", Units.miles(res.distanceM.toDouble()))
            toast("Route: $mi mi · ${res.steps.size} turns")
        }
        lastLocation?.let { updateNav(it.latitude, it.longitude) }
        onNavUpdate?.invoke()
    }

    private fun updateNav(lat: Double, lon: Double) {
        if (!navigating || navPoints.isEmpty()) return
        val n = navPoints.size
        // Track progress forward so a retracing path (out-and-back) doesn't snap back to an earlier
        // coincident point. Search a forward window from the furthest point reached.
        val lo = progressIdx.coerceIn(0, n - 1)
        val hi = minOf(n - 1, lo + 80)
        var nearIdx = lo
        var nearD = Double.MAX_VALUE
        for (i in lo..hi) {
            val d = hav(lat, lon, navPoints[i][1], navPoints[i][0])
            if (d < nearD) {
                nearD = d
                nearIdx = i
            }
        }
        if (nearD > 60.0) {
            for (i in navPoints.indices) {
                val d = hav(lat, lon, navPoints[i][1], navPoints[i][0])
                if (d < nearD) {
                    nearD = d
                    nearIdx = i
                }
            }
        }
        if (nearIdx > progressIdx) progressIdx = nearIdx
        val dest = navPoints.last()
        val distToDest = hav(lat, lon, dest[1], dest[0])
        navRemainingM = remainingAlong(nearIdx)
        ActionBus.navRemainingM = navRemainingM
        if (distToDest < 25.0 && progressIdx >= n - 4) {
            navArrow = "◉"
            navInstruction = "Arrived"
            navStreet = "Arrived"
            navDistText = ""
            navStepKey = -999
            navDTurnM = 0.0
            navArrived = true
            navRemainingM = 0.0
            ActionBus.navRemainingM = 0.0
            if (announcedIdx != -999) {
                announcedIdx = -999
                Voice.cue("You have arrived")
                navUi.postDelayed({ if (navigating) cancelNavigation() }, 6000L)
            }
            onNavUpdate?.invoke()
            return
        }
        val offD = minDistToRoute(lat, lon)
        val now = System.currentTimeMillis()
        if (offD > 40.0) {
            if (offRouteSince == 0L) offRouteSince = now
            if (now - offRouteSince > 5000 && now - lastRerouteMs > 12000) {
                offRouteSince = 0L
                reroute(lat, lon)
                return
            }
        } else {
            offRouteSince = 0L
        }
        val next = navSteps.firstOrNull { it.indexInTrack > nearIdx }
        if (next != null) {
            val (arrow, label) = maneuver(next.cmd)
            val instr = if (next.street.isNotEmpty()) "$label onto ${next.street}" else label
            val dTurn = hav(lat, lon, next.lat, next.lon)
            navArrow = arrow
            navInstruction = instr
            navStreet = if (next.street.isNotEmpty()) next.street else label
            navDistText = fmtDistTo(dTurn)
            navDTurnM = dTurn
            navStepKey = next.indexInTrack
            navArrived = false
            if (dTurn in 80.0..220.0 && earlyAnnouncedIdx != next.indexInTrack) {
                earlyAnnouncedIdx = next.indexInTrack
                Voice.cue("In ${fmtDistTo(dTurn)}, $instr")
            }
            if (dTurn < 45.0 && announcedIdx != next.indexInTrack) {
                announcedIdx = next.indexInTrack
                Voice.cue(instr)
            }
        } else {
            navArrow = "↑"
            navInstruction = "Continue"
            navStreet = "Continue"
            navDistText = fmtDistTo(distToDest)
            navDTurnM = distToDest
            navStepKey = -2
            navArrived = false
        }
        onNavUpdate?.invoke()
    }

    private fun remainingAlong(idx: Int): Double {
        if (navPoints.size < 2) return 0.0
        var d = 0.0
        var i = idx.coerceIn(0, navPoints.size - 1)
        while (i + 1 < navPoints.size) {
            d += hav(navPoints[i][1], navPoints[i][0], navPoints[i + 1][1], navPoints[i + 1][0])
            i++
        }
        return d
    }

    private fun minDistToRoute(lat: Double, lon: Double): Double {
        if (navPoints.size < 2) return 0.0
        val mLat = 111320.0
        val mLon = Math.cos(Math.toRadians(lat)) * 111320.0
        val px = lon * mLon
        val py = lat * 111320.0
        var best = Double.MAX_VALUE
        for (i in 1 until navPoints.size) {
            val ax = navPoints[i - 1][0] * mLon
            val ay = navPoints[i - 1][1] * mLat
            val bx = navPoints[i][0] * mLon
            val by = navPoints[i][1] * mLat
            val dx = bx - ax
            val dy = by - ay
            val len2 = dx * dx + dy * dy
            val t = if (len2 == 0.0) 0.0 else (((px - ax) * dx + (py - ay) * dy) / len2).coerceIn(0.0, 1.0)
            val ex = px - (ax + t * dx)
            val ey = py - (ay + t * dy)
            best = minOf(best, Math.sqrt(ex * ex + ey * ey))
        }
        return best
    }

    private fun maneuver(cmd: String): Pair<String, String> = when (cmd.uppercase()) {
        "KL" -> "↖" to "Keep left"
        "KR" -> "↗" to "Keep right"
        "TL" -> "↰" to "Turn left"
        "TR" -> "↱" to "Turn right"
        "TSHL" -> "↰" to "Sharp left"
        "TSHR" -> "↱" to "Sharp right"
        "TSLL" -> "↖" to "Slight left"
        "TSLR" -> "↗" to "Slight right"
        "RNLB", "RNDB" -> "↻" to "Roundabout"
        "TU", "TRU", "TLU" -> "↩" to "U-turn"
        else -> "↑" to "Continue"
    }

    private fun fmtDistTo(meters: Double): String {
        val ft = 3.28084 * meters
        return if (ft < 1000.0) {
            "${(ft / 10).toInt() * 10} ft"
        } else {
            String.format(Locale.US, "%.1f mi", meters / 1609.344)
        }
    }

    private fun hav(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val p1 = Math.toRadians(lat1)
        val p2 = Math.toRadians(lat2)
        val dp = Math.toRadians(lat2 - lat1)
        val dl = Math.toRadians(lon2 - lon1)
        val s1 = Math.sin(dp / 2)
        val c = Math.cos(p1) * Math.cos(p2)
        val s2 = Math.sin(dl / 2)
        val a = s1 * s1 + c * s2 * s2
        return 2 * 6371000.0 * Math.asin(Math.sqrt(a))
    }

    private fun toast(msg: String) {
        try {
            Toast.makeText(this, msg, 0).show()
        } catch (t: Throwable) {
        }
    }

    override fun onLocationChanged(loc: Location) {
        val prevLoc = lastLocation
        lastLocation = loc
        val rawSpd = if (loc.hasSpeed()) loc.speed.coerceAtLeast(0f) else 0f
        val sm = curSpeedMps * 0.6f + 0.4f * rawSpd
        curSpeedMps = if (sm >= 0.3f) sm else 0f
        if (loc.hasAltitude()) curEleM = loc.altitude
        updateGrade(loc, prevLoc)
        autoPauseCheck()
        if (recorder.isRecording) {
            val baro = if (!hasBaro || baroAltM.isNaN()) null else baroAltM
            recorder.add(loc, lastHr, curPower, curCadence, baro)
        }
        val prev = lastTrailLoc
        if (recorder.isRecording && (prev == null || prev.distanceTo(loc) > 4f)) {
            trail.add(doubleArrayOf(loc.longitude, loc.latitude))
            lastTrailLoc = loc
        }
        if (recorder.isRecording && System.currentTimeMillis() - lastNotifMs > 5000) {
            lastNotifMs = System.currentTimeMillis()
            (getSystemService("notification") as NotificationManager).notify(1, buildNotification())
        }
        if (navigating) updateNav(loc.latitude, loc.longitude)
        onUpdate?.invoke()
    }

    override fun onProviderEnabled(p: String) {}
    override fun onProviderDisabled(p: String) {}

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        if (!recorder.isRecording && !navigating) {
            stopSelf()
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        setWifi(true)
        try {
            unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
        }
        try {
            lm.removeUpdates(this)
        } catch (e: Exception) {
        }
        try {
            sensorMgr.unregisterListener(baroListener)
        } catch (e: Exception) {
        }
        led.close()
        if (recorder.isRecording) recorder.stop()
        releaseWake()
        hrSensor?.stop()
        cyclingSensor?.stop()
        navUi.removeCallbacksAndMessages(null)
        Voice.shutdown()
    }

    private fun closeOtherApps() {
        Thread {
            try {
                val keep = setOf(packageName, "com.tailscale.ipn")
                val pkgs = Runtime.getRuntime().exec(arrayOf("su", "-c", "pm list packages -3"))
                    .inputStream.bufferedReader().readText()
                    .lineSequence()
                    .map { it.removePrefix("package:").trim() }
                    .filter { it.isNotEmpty() && it !in keep }
                    .toList()
                if (pkgs.isNotEmpty()) {
                    val cmd = pkgs.joinToString("\n") { "am force-stop $it" }
                    Runtime.getRuntime().exec(arrayOf("su", "-c", cmd)).waitFor()
                }
            } catch (e: Exception) {
            }
        }.start()
    }

    private fun acquireWake() {
        val pm = getSystemService("power") as PowerManager
        wakeLock = pm.newWakeLock(1, "bike:recording").apply { acquire() }
    }

    private fun releaseWake() {
        try {
            wakeLock?.let { if (it.isHeld) it.release() }
        } catch (e: Exception) {
        }
        wakeLock = null
    }

    private fun createChannel() {
        val nm = getSystemService("notification") as NotificationManager
        nm.createNotificationChannel(
            NotificationChannel(CHAN, "Ride recording", 2).apply { setShowBadge(false) },
        )
        nm.createNotificationChannel(
            NotificationChannel(CRIT_CHAN, "Critical battery", 4).apply {
                enableVibration(true)
                setShowBadge(true)
            },
        )
    }

    private fun buildNotification(): Notification {
        val open = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE,
        )
        val mi = String.format(Locale.US, "%.2f mi", Units.miles(recorder.distanceM))
        val title = when {
            recorder.isRecording -> "Recording ride"
            navigating -> "Navigating"
            else -> "Bike Computer"
        }
        val text = when {
            recorder.isRecording -> "${Units.fmtHms(recorder.elapsedMs)} · $mi"
            navigating -> navInstruction
            else -> "Active"
        }
        return Notification.Builder(this, CHAN)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setContentIntent(open)
            .build()
    }
}

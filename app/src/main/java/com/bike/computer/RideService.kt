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
import java.util.Locale

/**
 * Foreground service that owns GPS + BLE sensors + barometer, records the ride, drives the
 * status LED, writes the ongoing notification, and (on critical battery) safely powers off.
 */
class RideService : Service(), LocationListener {
    companion object {
        const val ACTION_CANCEL_SHUTDOWN = "com.bike.computer.CANCEL_SHUTDOWN"
        const val ACTION_START_REC = "com.bike.computer.START_REC"
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
        if (intent?.action == ACTION_START_REC && !recorder.isRecording) {
            trail.clear()
            lastTrailLoc = null
            recorder.maxHrForZones = Prefs.maxHr(this)
            recorder.start()
            startForeground(1, buildNotification())
            acquireWake()
            updateLed()
            if (Prefs.closeApps(this)) closeOtherApps()
            setWifi(false)
        }
        return 1
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
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        return path
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
        onUpdate?.invoke()
    }

    override fun onProviderEnabled(p: String) {}
    override fun onProviderDisabled(p: String) {}

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(p: String?, s: Int, e: Bundle?) {
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onUnbind(intent: Intent?): Boolean {
        if (!recorder.isRecording) {
            stopSelf()
            return false
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
        return Notification.Builder(this, CHAN)
            .setContentTitle("Recording ride")
            .setContentText("${Units.fmtHms(recorder.elapsedMs)} · $mi")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setContentIntent(open)
            .build()
    }
}

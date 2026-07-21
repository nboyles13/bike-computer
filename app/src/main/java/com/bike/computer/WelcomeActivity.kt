package com.bike.computer

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.io.File

/** The launcher / home screen: routes, ride history, sensor status, and Start ride. */
class WelcomeActivity : Activity() {
    companion object {
        @Volatile
        private var autoSynced = false
    }

    private lateinit var container: LinearLayout
    private var gpsDot: TextView? = null
    private var gpsVal: TextView? = null
    private var hrDot: TextView? = null
    private var hrVal: TextView? = null
    private var hrRow: View? = null
    private var pwrDot: TextView? = null
    private var pwrVal: TextView? = null
    private var pwrRow: View? = null
    private var ride: RideService? = null
    private val ROUTES_DIR = "/sdcard/BikeComputer/routes"
    private val RECENT_COUNT = 5
    private val ui = Handler(Looper.getMainLooper())

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(n: ComponentName?, b: IBinder) {
            ride = (b as RideService.LocalBinder).service
            if (Prefs.wifiDisabledByApp(this@WelcomeActivity)) {
                val recording = ride?.recorder?.isRecording ?: false
                if (!recording) {
                    Prefs.setWifiDisabledByApp(this@WelcomeActivity, false)
                    Thread {
                        try {
                            Runtime.getRuntime().exec(arrayOf("su", "-c", "svc wifi enable")).waitFor()
                        } catch (e: Exception) {
                        }
                    }.start()
                }
            }
            refreshStatus()
        }

        override fun onServiceDisconnected(n: ComponentName?) {
            ride = null
        }
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_welcome)
        window.addFlags(128)
        enterImmersive()
        container = findViewById(R.id.welcome_container)
        findViewById<View>(R.id.welcome_settings).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, RideService::class.java), conn, 1)
    }

    override fun onResume() {
        super.onResume()
        if (AppState.onMap) {
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(0, 0)
        } else {
            build()
            tick()
            maybeAutoSync()
        }
    }

    private fun maybeAutoSync() {
        if (autoSynced) return
        autoSynced = true
        Thread {
            try {
                RideHistory.reconcile()
            } catch (t: Throwable) {
            }
            if (Prefs.driveConnected(this)) {
                val n = try {
                    GoogleDriveClient.syncRoutes(this, ROUTES_DIR)
                } catch (t: Throwable) {
                    0
                }
                if (n > 0) {
                    runOnUiThread {
                        if (!isFinishing) {
                            build()
                            toast("Synced $n route${if (n == 1) "" else "s"} from Drive")
                        }
                    }
                }
            }
        }.start()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersive()
    }

    override fun onPause() {
        ui.removeCallbacksAndMessages(null)
        super.onPause()
    }

    override fun onStop() {
        try {
            unbindService(conn)
        } catch (e: Exception) {
        }
        super.onStop()
    }

    private fun dp(v: Int): Int =
        TypedValue.applyDimension(1, v.toFloat(), resources.displayMetrics).toInt()

    private fun toast(s: String) {
        Toast.makeText(this, s, 0).show()
    }

    private fun tick() {
        refreshStatus()
        ui.postDelayed({ tick() }, 1000L)
    }

    private fun build() {
        container.removeAllViews()
        menuCard("Navigate", R.drawable.ic_search) {
            startActivity(Intent(this, DestinationSearchActivity::class.java))
        }
        val all = File(ROUTES_DIR).listFiles { f -> f.name.endsWith(".gpx") }?.toList() ?: emptyList()
        val byName = all.associateBy { it.nameWithoutExtension }
        val starred = Prefs.starredRoutes(this).mapNotNull { byName[it] }
            .sortedBy { it.nameWithoutExtension.lowercase() }
        val starredSet = starred.map { it.nameWithoutExtension }.toSet()
        val recent = all.filter { it.nameWithoutExtension !in starredSet }
            .sortedByDescending { it.lastModified() }.take(RECENT_COUNT)

        if (starred.isNotEmpty()) {
            sectionLabel("STARRED")
            for (f in starred) routeRow(f, true)
        }
        sectionLabel(if (starred.isEmpty()) "ROUTES" else "RECENT")
        if (recent.isEmpty() && starred.isEmpty()) {
            hint("No saved routes yet — add them in Settings ▸ Routes.")
        } else if (recent.isEmpty()) {
            hint("No other recent routes.")
        }
        for (f in recent) routeRow(f, false)

        menuCard("🏁  Ride history") {
            startActivity(Intent(this, RidesActivity::class.java))
        }

        sectionLabel("SENSORS")
        val (_, gDot, gVal) = statusRow("GPS")
        gpsDot = gDot
        gpsVal = gVal
        val (hRow, hDot, hVal) = statusRow("Heart rate")
        hrRow = hRow
        hrDot = hDot
        hrVal = hVal
        val (pRow, pDot, pVal) = statusRow("Power / cadence")
        pwrRow = pRow
        pwrDot = pDot
        pwrVal = pVal
        refreshStatus()
        startButton()
    }

    private fun refreshStatus() {
        val r = ride
        val fix = r?.lastLocation != null
        setStatus(gpsDot, gpsVal, fix, if (fix) "Fix acquired" else "Searching…", !fix)

        val hrOn = r != null && r.hrConnected
        val hrShow = hrOn || (r?.hrDeviceName != null)
        hrRow?.visibility = if (hrShow) View.VISIBLE else View.GONE
        if (hrShow) {
            val text = if (hrOn) "${r?.lastHr ?: 0} bpm" else prettyStatus(r?.hrStatus)
            setStatus(hrDot, hrVal, hrOn, text, false)
        }

        val pwrOn = r != null && r.cyclingConnected
        val pwrShow = pwrOn || (r?.cyclingDeviceName != null)
        pwrRow?.visibility = if (pwrShow) View.VISIBLE else View.GONE
        if (pwrShow) {
            val text = if (pwrOn) {
                "${r?.curPower ?: 0} W · ${r?.curCadence ?: 0} rpm"
            } else {
                prettyStatus(r?.cyclingStatus)
            }
            setStatus(pwrDot, pwrVal, pwrOn, text, false)
        }
    }

    private fun prettyStatus(s: String?): String = when {
        s == null || s.startsWith("connecting") -> "Connecting…"
        s == "live" || s == "connected" -> "Connected"
        s == "BT off" -> "Bluetooth off"
        s.startsWith("scanning") -> "Scanning…"
        s == "disconnected" -> "Disconnected"
        else -> s
    }

    private fun setStatus(dot: TextView?, value: TextView?, on: Boolean, text: String, searching: Boolean) {
        val color = if (on) -13577896 else if (searching) -24822 else -7434605
        dot?.setTextColor(color)
        value?.text = text
        value?.setTextColor(if (on) -1513238 else -6381922)
    }

    private fun sectionLabel(title: String) {
        val t = TextView(this)
        t.text = title
        t.setTextColor(Color.parseColor("#FF8E8E93"))
        t.textSize = 13f
        t.typeface = Typeface.create("sans-serif-medium", 0)
        t.letterSpacing = 0.08f
        t.setPadding(dp(10), dp(18), dp(10), dp(6))
        container.addView(t)
    }

    private fun hint(s: String) {
        val t = TextView(this)
        t.text = s
        t.setTextColor(Color.parseColor("#FF9E9E9E"))
        t.textSize = 14f
        t.setPadding(dp(10), dp(2), dp(10), dp(8))
        container.addView(t)
    }

    private fun card(): LinearLayout {
        val l = LinearLayout(this)
        l.orientation = LinearLayout.HORIZONTAL
        l.gravity = 16
        l.setBackgroundResource(R.drawable.card_solid)
        l.setPadding(dp(16), dp(14), dp(16), dp(14))
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(dp(4), dp(4), dp(4), dp(4))
        l.layoutParams = lp
        container.addView(l)
        return l
    }

    private fun statusRow(label: String): Triple<View, TextView, TextView> {
        val c = card()
        val dot = TextView(this)
        dot.text = "●"
        dot.textSize = 13f
        dot.setTextColor(-7434605)
        dot.setPadding(0, 0, dp(12), 0)
        val t = TextView(this)
        t.text = label
        t.setTextColor(-1)
        t.textSize = 17f
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val v = TextView(this)
        v.text = "…"
        v.setTextColor(Color.parseColor("#FF9E9E9E"))
        v.textSize = 15f
        c.addView(dot)
        c.addView(t)
        c.addView(v)
        return Triple(c, dot, v)
    }

    private fun menuCard(title: String, iconRes: Int = 0, onClick: () -> Unit) {
        val c = card()
        if (iconRes != 0) {
            val ic = ImageView(this)
            ic.setImageResource(iconRes)
            ic.setColorFilter(Color.parseColor("#FFB8B8BD"))
            val lp = LinearLayout.LayoutParams(dp(22), dp(22))
            lp.rightMargin = dp(12)
            ic.layoutParams = lp
            c.addView(ic)
        }
        val t = TextView(this)
        t.text = title
        t.setTextColor(Color.parseColor("#FFE8E8EA"))
        t.textSize = 17f
        t.typeface = Typeface.create("sans-serif-medium", 0)
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val chev = TextView(this)
        chev.text = "›"
        chev.setTextColor(Color.parseColor("#FF6E6E6E"))
        chev.textSize = 22f
        c.addView(t)
        c.addView(chev)
        c.setOnClickListener { onClick() }
    }

    private fun startButton() {
        val bg = GradientDrawable()
        bg.cornerRadius = dp(16).toFloat()
        bg.setColor(Color.parseColor("#FF30D158"))
        val b = TextView(this)
        b.text = "▶   Start ride"
        b.setTextColor(-1)
        b.textSize = 21f
        b.typeface = Typeface.create("sans-serif-medium", 1)
        b.gravity = 17
        b.background = bg
        b.setPadding(0, dp(20), 0, dp(20))
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(dp(4), dp(16), dp(4), dp(6))
        b.layoutParams = lp
        b.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        container.addView(b)
    }

    private fun routeRow(f: File, starredNow: Boolean) {
        val c = card()
        val name = f.nameWithoutExtension
        val t = TextView(this)
        t.text = name
        t.setTextColor(-1)
        t.textSize = 16f
        t.maxLines = 1
        t.ellipsize = TextUtils.TruncateAt.END
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        c.setOnClickListener { rideRoute(f) }
        val star = TextView(this)
        star.text = if (starredNow) "★" else "☆"
        star.setTextColor(Color.parseColor(if (starredNow) "#FFFFD60A" else "#FF6E6E6E"))
        star.textSize = 22f
        star.setPadding(dp(14), dp(2), dp(6), dp(2))
        star.setOnClickListener {
            Prefs.setRouteStarred(this, name, !starredNow)
            build()
        }
        c.addView(t)
        c.addView(star)
    }

    private fun rideRoute(f: File) {
        val gpx = try {
            f.readText()
        } catch (t: Throwable) {
            null
        }
        if (gpx == null) {
            toast("Couldn't read route")
            return
        }
        val list = GpxRoute.parse(gpx)
        if (list.size < 2) {
            toast("No route points found")
            return
        }
        ActionBus.pendingRoute = GpxRoute.toWaypoints(list)
        ActionBus.pendingRouteName = f.nameWithoutExtension
        startActivity(Intent(this, MainActivity::class.java))
    }
}

package com.bike.computer

import android.app.Activity
import android.app.AlertDialog
import android.app.role.RoleManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import java.io.File

/** Multi-page settings: pages, recording, sensors, routes, Google Drive, home/system. */
class SettingsActivity : Activity() {
    private lateinit var container: LinearLayout
    private lateinit var titleView: TextView
    private lateinit var backBtn: View
    private lateinit var target: LinearLayout
    private var currentPage: String? = null
    private var liveRefresh: (() -> Unit)? = null
    private var ride: RideService? = null
    private val ROUTES_DIR = "/sdcard/BikeComputer/routes"
    private val ui = Handler(Looper.getMainLooper())
    private val PAGES = listOf("Pages", "Recording", "Sensors", "Routes", "Google Drive", "Home & system")

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(n: ComponentName?, b: IBinder) {
            ride = (b as RideService.LocalBinder).service
            liveRefresh?.invoke()
        }

        override fun onServiceDisconnected(n: ComponentName?) {
            ride = null
        }
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_settings)
        enterImmersive()
        container = findViewById(R.id.settings_container)
        titleView = findViewById(R.id.settings_title)
        backBtn = findViewById(R.id.back_btn)
        backBtn.setOnClickListener { goMenu() }
        findViewById<View>(R.id.done_btn).setOnClickListener { finish() }
        build()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (currentPage != null) goMenu() else super.onBackPressed()
    }

    private fun goMenu() {
        currentPage = null
        rebuild()
    }

    private fun openPage(title: String) {
        currentPage = title
        rebuild()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersive()
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, RideService::class.java), conn, 1)
    }

    override fun onResume() {
        super.onResume()
        if (liveRefresh != null) startTick()
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

    private fun startTick() {
        ui.removeCallbacksAndMessages(null)
        tick()
    }

    private fun tick() {
        val fn = liveRefresh ?: return
        fn()
        ui.postDelayed({ tick() }, 1000L)
    }

    private fun dp(v: Int): Int =
        TypedValue.applyDimension(1, v.toFloat(), resources.displayMetrics).toInt()

    private fun rebuild() {
        liveRefresh = null
        container.removeAllViews()
        build()
    }

    override fun onActivityResult(req: Int, res: Int, data: Intent?) {
        super.onActivityResult(req, res, data)
        if (req == 43 && res == -1) rebuild()
    }

    private fun build() {
        target = container
        val page = currentPage
        titleView.text = page ?: "Settings"
        backBtn.visibility = if (page == null) View.GONE else View.VISIBLE
        if (page == null) {
            for (p in PAGES) menuRow(p) { openPage(p) }
            menuRow("Rides") { startActivity(Intent(this, RidesActivity::class.java)) }
            return
        }
        when (page) {
            "Pages" -> pagePages()
            "Recording" -> pageRecording()
            "Sensors" -> pageSensors()
            "Routes" -> pageRoutes()
            "Google Drive" -> pageExport()
            "Home & system" -> pageSystem()
        }
    }

    private fun pagePages() {
        text("Reorder or hide the swipe pages, and add data pages. Map can't be hidden.")
        val order = Prefs.pageOrder(this).toMutableList()
        val dataKeys = order.filter { Pages.isData(it) }
        order.forEachIndexed { i, key ->
            val name = when {
                !Pages.isData(key) -> Pages.fixedTitle(key)
                dataKeys.size > 1 -> "Data ${dataKeys.indexOf(key) + 1}"
                else -> "Data"
            }
            pageRow(
                key, name, i, order.size,
                onUp = {
                    order.removeAt(i); order.add(i - 1, key)
                    Prefs.setPageOrder(this, order); rebuild()
                },
                onDown = {
                    order.removeAt(i); order.add(i + 1, key)
                    Prefs.setPageOrder(this, order); rebuild()
                },
                onRemove = { Prefs.removeDataPage(this, key); rebuild() },
            )
        }
        button("＋ Add data page") { Prefs.addDataPage(this); rebuild() }
        text("Long-press a Data page to edit it — drag boxes to move, drag a corner to resize, tap a box to change its metric, and use ＋ Add box.")
    }

    private fun pageRecording() {
        switchRow("Auto-pause when stopped", Prefs.autoPause(this)) { v ->
            Prefs.setAutoPause(this, v)
            ride?.autoPauseEnabled = v
        }
        switchRow("Voice & beep turn cues", Prefs.voice(this)) { v ->
            Prefs.setVoice(this, v)
            Voice.enabled = v
        }
        switchRow("Heart-rate zone LED", Prefs.led(this)) { v ->
            Prefs.setLed(this, v)
            ride?.ledEnabled = v
        }
        switchRow("Close other apps when recording", Prefs.closeApps(this)) { v -> Prefs.setCloseApps(this, v) }
        switchRow("Low-power map (flat, saves battery)", Prefs.lowPowerMap(this)) { v -> Prefs.setLowPowerMap(this, v) }
        switchRow("Turn off Wi-Fi while recording", Prefs.wifiOffOnRide(this)) { v -> Prefs.setWifiOffOnRide(this, v) }
        switchRow("Endurance mode (dim + throttle)", Prefs.endurance(this)) { v -> Prefs.setEndurance(this, v) }
        text("Endurance dims the screen and blanks it after 30s while recording (power button to wake), forces the flat map, drops the framerate, and turns the HR LED off — maximum battery life for long rides.")
    }

    private fun pageSensors() {
        val (hrName, hrVal) = sensorRow("Heart rate")
        val (cycName, cycVal) = sensorRow("Power / cadence")
        liveRefresh = {
            val r = ride
            val hrOn = r != null && r.hrConnected
            val hrDev = r?.hrDeviceName
            hrName.text = if (hrOn && hrDev != null) hrDev else "Heart rate"
            setSensorValue(hrVal, hrOn, "${r?.lastHr ?: 0} bpm", prettyStatus(r?.hrStatus))
            val cycOn = r != null && r.cyclingConnected
            val cycDev = r?.cyclingDeviceName
            cycName.text = if (cycOn && cycDev != null) cycDev else "Power / cadence"
            setSensorValue(
                cycVal, cycOn,
                "${r?.curPower ?: 0} W · ${r?.curCadence ?: 0} rpm", prettyStatus(r?.cyclingStatus),
            )
        }
        liveRefresh?.invoke()
        startTick()
        button("Forget & rescan") {
            ride?.rescanSensor()
            toast("Rescanning…")
            liveRefresh?.invoke()
        }
        text("Max HR sets the 5 training zones (Z1 50% … Z5 90% of max)")
        stepperRow("Max heart rate", Prefs.maxHr(this), 120, 220, 1, " bpm") { v -> Prefs.setMaxHr(this, v) }
    }

    private fun setSensorValue(v: TextView, connected: Boolean, live: String, status: String) {
        v.text = if (connected) live else status
        v.setTextColor(if (connected) -13577896 else -6381922)
    }

    private fun prettyStatus(s: String?): String = when {
        s == null -> "Not connected"
        s.startsWith("connecting") -> "Connecting…"
        s == "live" || s == "connected" -> "Connected"
        s == "scanning" || s == "scanning…" -> "Scanning…"
        s == "BT off" -> "Bluetooth off"
        s == "disconnected" -> "Not connected"
        else -> s
    }

    private fun sensorRow(name: String): Pair<TextView, TextView> {
        val c = card()
        val t = TextView(this)
        t.text = name
        t.setTextColor(-1)
        t.textSize = 17f
        t.maxLines = 1
        t.ellipsize = android.text.TextUtils.TruncateAt.END
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val v = TextView(this)
        v.text = "…"
        v.setTextColor(Color.parseColor("#FF9E9E9E"))
        v.textSize = 18f
        v.setPadding(dp(10), 0, 0, 0)
        c.addView(t)
        c.addView(v)
        return Pair(t, v)
    }

    private fun pageRoutes() {
        if (ActionBus.navigating) {
            button("Stop navigation") {
                ActionBus.stopNav = true
                toast("Navigation stopped")
                rebuild()
            }
        }
        // Home location — enables one-tap offline "Navigate home" (routing is offline; only
        // address *search* needs Wi-Fi, so a saved home avoids the geocoder entirely).
        val home = Prefs.homeLoc(this)
        text(
            if (home != null) {
                "Home is set. Use “🏠 Navigate home” on the main screen to route here — works offline."
            } else {
                "Set a home location for one-tap, offline “Navigate home” (no Wi-Fi needed)."
            },
        )
        button("Set home to current location") {
            val loc = ride?.lastLocation
            if (loc == null) {
                toast("No GPS fix yet — try again outside")
            } else {
                Prefs.setHomeLoc(this, loc.latitude, loc.longitude)
                toast("Home set to current location")
                rebuild()
            }
        }
        if (home != null) {
            button("Clear home location") {
                Prefs.clearHomeLoc(this)
                toast("Home cleared")
                rebuild()
            }
        }
        if (Prefs.driveConnected(this)) {
            val folder = Prefs.driveRoutesFolder(this)
            text("Sync routes from Drive — either drop GPX files into your “$folder” folder, or add Google Maps links to a Sheet (below):")
            button("⟳  Sync routes from Drive") { syncDriveRoutes() }
            val sheetId = Prefs.driveSheetId(this)
            if (sheetId.isEmpty()) {
                text("Prefer a table? Create a Google Sheet with Name + Link columns — add a row per route (a Maps directions link), then Sync pulls them in.")
                button("＋  Create route links sheet") { createLinksSheet() }
            } else {
                text("Your “Harmin Route Links” Sheet is in the “$folder” folder. Add a row — Name + a Google Maps directions link — then tap Sync. Editing or deleting a row updates or removes that route on the next sync.")
                button("⧉  Copy sheet link") { copyText(GoogleDriveClient.sheetUrl(sheetId), "Sheet link copied") }
            }
        }
        text("Or paste a public GPX route URL (Komoot / RideWithGPS / Strava / bikerouter export). Navigate now, or save it to re-ride offline.")
        val urlField = editRow("Route GPX URL", "", "Paste GPX URL")
        button("Download & navigate") {
            withDownloadedRoute(urlField.text.toString().trim()) { gpx -> navigateGpx(gpx) }
        }
        button("Download & save") {
            withDownloadedRoute(urlField.text.toString().trim()) { gpx -> promptSaveRoute(gpx) }
        }
        text("Or paste a Google Maps link (share a place or directions). We build a bike route between the stops — no GPX needed.")
        val gmapField = editRow("Google Maps link", "", "Paste maps.app.goo.gl / google.com/maps link")
        button("Navigate this link") { withGmapsRoute(gmapField.text.toString().trim(), false) }
        button("Save this link") { withGmapsRoute(gmapField.text.toString().trim(), true) }
        text("Saved routes")
        val routes = File(ROUTES_DIR).listFiles { f -> f.name.endsWith(".gpx") }
            ?.sortedBy { it.name.lowercase() } ?: emptyList()
        if (routes.isEmpty()) text("No saved routes yet")
        for (f in routes) savedRouteRow(f)
    }

    private fun createLinksSheet() {
        toast("Creating Sheet in Drive…")
        Thread {
            val outcome = runCatching { GoogleDriveClient.ensureLinksSheet(this) }
            runOnUiThread {
                outcome
                    .onSuccess { id ->
                        copyText(GoogleDriveClient.sheetUrl(id), "Created “Harmin Route Links” — link copied")
                        rebuild()
                    }
                    .onFailure { e ->
                        val m = e.message ?: "failed"
                        toast(
                            if (m.contains("403") || m.contains("scope", true)) {
                                "Reconnect Drive to grant access"
                            } else {
                                "Couldn't create sheet: $m"
                            },
                        )
                    }
            }
        }.start()
    }

    private fun copyText(text: String, done: String) {
        (getSystemService("clipboard") as ClipboardManager)
            .setPrimaryClip(ClipData.newPlainText("Harmin", text))
        toast(done)
    }

    private fun syncDriveRoutes() {
        toast("Syncing routes from Drive…")
        Thread {
            val outcome = runCatching { GoogleDriveClient.syncRoutes(this, ROUTES_DIR) }
            runOnUiThread {
                outcome
                    .onSuccess { n ->
                        toast(if (n > 0) "Added $n route${if (n == 1) "" else "s"} from Drive" else "No new routes in Drive")
                        if (n > 0) rebuild()
                    }
                    .onFailure { e ->
                        val m = e.message ?: "failed"
                        toast(
                            if (m.contains("403") || m.contains("insufficient", true) || m.contains("scope", true)) {
                                "Reconnect Drive to grant route access"
                            } else {
                                "Sync failed: $m"
                            },
                        )
                    }
            }
        }.start()
    }

    private fun pageExport() {
        if (Prefs.driveConnected(this)) {
            valueRow("Google Drive", "Connected")
            switchRow("Auto-upload after each ride", Prefs.driveAutoUpload(this)) { v -> Prefs.setDriveAutoUpload(this, v) }
            text("Folder names in your Drive — rides upload here; routes are read from here (drop .gpx files in it).")
            val ridesFolder = editRow("Rides folder", Prefs.driveRidesFolder(this))
            val routesFolder = editRow("Routes folder", Prefs.driveRoutesFolder(this))
            button("Save folder names") {
                Prefs.setDriveRidesFolder(this, ridesFolder.text.toString())
                Prefs.setDriveRoutesFolder(this, routesFolder.text.toString())
                toast("Saved")
                rebuild()
            }
            text("Reconnect if 'Sync routes from Drive' says it can't read your Drive (grants read access).")
            button("Reconnect Drive") {
                startActivityForResult(Intent(this, DriveAuthActivity::class.java), 43)
            }
            button("Disconnect Drive") {
                Prefs.clearDriveTokens(this)
                toast("Disconnected")
                rebuild()
            }
        } else {
            text("Connect Google Drive to back up rides and load routes from Drive folders. Create an OAuth client (type: Desktop app) in Google Cloud Console with the Drive API enabled, then paste its Client ID & Secret.")
            val idField = editRow("Client ID", Prefs.driveClientId(this))
            val secretField = editRow("Client Secret", Prefs.driveClientSecret(this))
            button("Connect to Google Drive") {
                val id = idField.text.toString().trim()
                val secret = secretField.text.toString().trim()
                if (id.isNotEmpty() && secret.isNotEmpty()) {
                    Prefs.setDriveApp(this, id, secret)
                    startActivityForResult(Intent(this, DriveAuthActivity::class.java), 43)
                } else {
                    toast("Enter Client ID and Secret")
                }
            }
        }
        text("Your recorded rides — with per-ride upload status, map and share — are under Rides.")
        button("Open Rides") { startActivity(Intent(this, RidesActivity::class.java)) }
    }

    private fun pageSystem() {
        if (isDefaultHome()) {
            text("Harmin is your phone's home app — it opens on boot and when you press Home. Jump to the normal Android launcher (Harmin stays the default — press Home to come back):")
            button("Exit to Android launcher") { launchOtherLauncher() }
        } else {
            text("Make Harmin your phone's home app so it opens on boot and when you press Home (choose Harmin in the picker).")
            button("Set Harmin as home app") { openHomeSettings() }
        }
        button("Android system settings") {
            try {
                startActivity(Intent("android.settings.SETTINGS"))
            } catch (t: Throwable) {
                toast("Couldn't open settings")
            }
        }
    }

    private fun isDefaultHome(): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            val rm = getSystemService(RoleManager::class.java)
            if (rm != null && rm.isRoleAvailable(RoleManager.ROLE_HOME)) {
                return rm.isRoleHeld(RoleManager.ROLE_HOME)
            }
        }
        val res = packageManager.resolveActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0,
        )
        return res?.activityInfo?.packageName == packageName
    }

    private fun openHomeSettings() {
        try {
            startActivity(Intent("android.settings.HOME_SETTINGS"))
        } catch (t: Throwable) {
            try {
                startActivity(Intent("android.settings.SETTINGS"))
            } catch (t2: Throwable) {
                toast("Open Settings ▸ Apps ▸ Default apps ▸ Home app")
            }
        }
    }

    private fun launchOtherLauncher() {
        val home = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val cands = packageManager.queryIntentActivities(home, 0)
            .map { it.activityInfo }
            .filter { it.packageName != packageName && it.packageName != "com.android.settings" }
        val t = cands.firstOrNull {
            !it.packageName.contains("launcher", true) && it.packageName.contains("trebuchet", true)
        } ?: cands.firstOrNull()
        if (t == null) {
            toast("No other launcher installed")
            return
        }
        val i = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
            .setClassName(t.packageName, t.name).addFlags(268435456)
        try {
            startActivity(i)
        } catch (th: Throwable) {
            openHomeSettings()
        }
    }

    private fun menuRow(title: String, onClick: () -> Unit) {
        val c = card()
        c.setPadding(dp(18), dp(18), dp(16), dp(18))
        val t = TextView(this)
        t.text = title
        t.setTextColor(Color.parseColor("#FFE8E8EA"))
        t.textSize = 18f
        t.typeface = Typeface.create("sans-serif-medium", 0)
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val chev = TextView(this)
        chev.text = "›"
        chev.setTextColor(Color.parseColor("#FF6E6E6E"))
        chev.textSize = 24f
        c.addView(t)
        c.addView(chev)
        c.setOnClickListener { onClick() }
    }

    private fun text(s: String) {
        val t = TextView(this)
        t.text = s
        t.setTextColor(Color.parseColor("#FF9E9E9E"))
        t.textSize = 14f
        t.setPadding(dp(10), dp(2), dp(10), dp(8))
        target.addView(t)
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
        target.addView(l)
        return l
    }

    private fun switchRow(label: String, initial: Boolean, onChange: (Boolean) -> Unit) {
        val c = card()
        val t = TextView(this)
        t.text = label
        t.setTextColor(-1)
        t.textSize = 17f
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val sw = Switch(this)
        sw.isChecked = initial
        sw.setOnCheckedChangeListener { _, v -> onChange(v) }
        c.addView(t)
        c.addView(sw)
    }

    private fun checkRow(label: String, initial: Boolean, onChange: (Boolean) -> Unit) {
        val c = card()
        val t = TextView(this)
        t.text = label
        t.setTextColor(-1)
        t.textSize = 16f
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val cb = CheckBox(this)
        cb.isChecked = initial
        cb.setOnCheckedChangeListener { _, v -> onChange(v) }
        c.addView(t)
        c.addView(cb)
    }

    private fun stepperRow(label: String, initial: Int, min: Int, max: Int, step: Int, suffix: String, onChange: (Int) -> Unit) {
        val c = card()
        val t = TextView(this)
        t.text = label
        t.setTextColor(-1)
        t.textSize = 17f
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        var value = initial
        val valView = TextView(this)
        valView.text = "$value$suffix"
        valView.setTextColor(Color.parseColor("#FF9E9E9E"))
        valView.textSize = 17f
        valView.gravity = 17
        valView.layoutParams = LinearLayout.LayoutParams(dp(74), -2)

        fun stepBtn(sym: String, delta: Int): TextView {
            val b = TextView(this)
            b.text = sym
            b.setTextColor(Color.parseColor("#FF4C8DFF"))
            b.textSize = 26f
            b.gravity = 17
            b.layoutParams = LinearLayout.LayoutParams(dp(44), dp(40))
            b.setOnClickListener {
                value = (value + delta).coerceIn(min, max)
                valView.text = "$value$suffix"
                onChange(value)
            }
            return b
        }
        c.addView(t)
        c.addView(stepBtn("−", -step))
        c.addView(valView)
        c.addView(stepBtn("+", step))
    }

    private fun button(label: String, onClick: () -> Unit) {
        val c = card()
        val t = TextView(this)
        t.text = label
        t.setTextColor(Color.parseColor("#FF4C8DFF"))
        t.textSize = 17f
        t.layoutParams = LinearLayout.LayoutParams(-1, -2)
        c.addView(t)
        c.setOnClickListener { onClick() }
    }

    private fun valueRow(label: String, value: String): TextView {
        val c = card()
        val t = TextView(this)
        t.text = label
        t.setTextColor(-1)
        t.textSize = 17f
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val v = TextView(this)
        v.text = value
        v.setTextColor(Color.parseColor("#FF9E9E9E"))
        v.textSize = 15f
        c.addView(t)
        c.addView(v)
        return v
    }

    private fun editRow(label: String, initial: String, hint: String = ""): EditText {
        val l = LinearLayout(this)
        l.orientation = LinearLayout.VERTICAL
        l.setBackgroundResource(R.drawable.card_solid)
        l.setPadding(dp(16), dp(10), dp(16), dp(12))
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(dp(4), dp(4), dp(4), dp(4))
        l.layoutParams = lp
        val t = TextView(this)
        t.text = label
        t.setTextColor(Color.parseColor("#FF8E8E93"))
        t.textSize = 13f
        val e = EditText(this)
        e.setText(initial)
        e.setTextColor(-1)
        e.textSize = 16f
        e.isSingleLine = true
        e.setPadding(0, dp(6), 0, 0)
        e.background = null
        if (hint.isNotEmpty()) {
            e.hint = hint
            e.setHintTextColor(Color.parseColor("#FF6E6E6E"))
        }
        e.inputType = 145
        l.addView(t)
        l.addView(e)
        target.addView(l)
        return e
    }

    private fun pageRow(
        key: String,
        name: String,
        index: Int,
        count: Int,
        onUp: () -> Unit,
        onDown: () -> Unit,
        onRemove: () -> Unit,
    ) {
        val c = card()
        val on = Prefs.pageEnabled(this, key)
        val t = TextView(this)
        t.text = name
        t.setTextColor(if (on) -1 else Color.parseColor("#FF6E6E6E"))
        t.textSize = 16f
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        c.addView(t)
        if (Pages.isData(key)) {
            val del = TextView(this)
            del.text = "Delete"
            del.setTextColor(Color.parseColor("#FFE53935"))
            del.textSize = 14f
            del.setPadding(dp(8), 0, dp(10), 0)
            del.setOnClickListener { onRemove() }
            c.addView(del)
        }
        c.addView(arrow("▲", index > 0, onUp))
        c.addView(arrow("▼", index < count - 1, onDown))
        if (key == Pages.MAP) {
            val lock = TextView(this)
            lock.text = "On"
            lock.setTextColor(Color.parseColor("#FF6E6E6E"))
            lock.textSize = 14f
            lock.setPadding(dp(10), 0, dp(6), 0)
            c.addView(lock)
            return
        }
        val sw = Switch(this)
        sw.isChecked = on
        sw.setOnCheckedChangeListener { _, v ->
            Prefs.setPageEnabled(this, key, v)
            rebuild()
        }
        c.addView(sw)
    }

    private fun arrow(sym: String, active: Boolean, onClick: () -> Unit): TextView {
        val b = TextView(this)
        b.text = sym
        b.textSize = 18f
        b.gravity = 17
        b.setTextColor(Color.parseColor(if (active) "#FF4C8DFF" else "#FF3A3A3C"))
        b.layoutParams = LinearLayout.LayoutParams(dp(38), dp(38))
        if (active) b.setOnClickListener { onClick() }
        return b
    }

    private fun withDownloadedRoute(url: String, onReady: (String) -> Unit) {
        if (url.isEmpty()) {
            toast("Enter a route URL")
            return
        }
        Prefs.setLastRouteUrl(this, url)
        toast("Downloading route…")
        Thread {
            val outcome = runCatching { GpxRoute.download(url) }
            runOnUiThread {
                outcome
                    .onSuccess { onReady(it) }
                    .onFailure { toast("Download failed: ${it.message}") }
            }
        }.start()
    }

    private fun navigateGpx(gpx: String, routeName: String? = null) {
        val list = GpxRoute.parse(gpx)
        if (list.size < 2) {
            toast("No route points found")
            return
        }
        ActionBus.pendingRoute = GpxRoute.toWaypoints(list)
        ActionBus.pendingRouteName = routeName
        toast("Snapping route to roads…")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun withGmapsRoute(link: String, save: Boolean) {
        when {
            link.isEmpty() -> toast("Paste a Google Maps link")
            !GmapsRoute.looksLikeLink(link) -> toast("That doesn't look like a Google Maps link")
            else -> {
                toast("Reading Google Maps link…")
                Thread {
                    val outcome = runCatching { GmapsRoute.points(link) }
                    runOnUiThread {
                        outcome
                            .onSuccess { list ->
                                when {
                                    list.isEmpty() -> toast("Couldn't find a location in that link")
                                    list.size == 1 && save ->
                                        toast("That link is a single place — use “Navigate” (a saved route needs a start and end)")
                                    list.size == 1 -> {
                                        ActionBus.pendingDestination = doubleArrayOf(list[0][1], list[0][0])
                                        toast("Routing to your destination…")
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                                    save -> promptSaveRoute(GmapsRoute.toGpx("Maps route", list))
                                    else -> navigateGpx(GmapsRoute.toGpx("Maps route", list))
                                }
                            }
                            .onFailure { toast("Couldn't read that link (need Wi-Fi): ${it.message}") }
                    }
                }.start()
            }
        }
    }

    private fun promptSaveRoute(gpx: String) {
        if (GpxRoute.parse(gpx).size < 2) {
            toast("No route points found")
            return
        }
        val input = EditText(this)
        input.setText(GpxRoute.name(gpx) ?: "Route")
        input.isSingleLine = true
        input.setTextColor(-1)
        input.setPadding(dp(16), dp(12), dp(16), dp(12))
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Save route as")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = Regex("[/\\\\:*?\"<>|]").replace(input.text.toString().trim(), "_").take(60)
                if (name.isEmpty()) {
                    toast("Name required")
                } else {
                    val outcome = runCatching {
                        File(ROUTES_DIR).mkdirs()
                        File(ROUTES_DIR, "$name.gpx").writeText(gpx)
                    }
                    outcome
                        .onSuccess {
                            toast("Saved \"$name\"")
                            rebuild()
                        }
                        .onFailure { toast("Save failed: ${it.message}") }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun savedRouteRow(f: File) {
        val c = card()
        val t = TextView(this)
        t.text = f.nameWithoutExtension
        t.setTextColor(-1)
        t.textSize = 15f
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val rideBtn = TextView(this)
        rideBtn.text = "Ride"
        rideBtn.setTextColor(Color.parseColor("#FF4C8DFF"))
        rideBtn.textSize = 16f
        rideBtn.setPadding(dp(10), 0, dp(10), 0)
        rideBtn.setOnClickListener {
            val gpx = try {
                f.readText()
            } catch (t2: Throwable) {
                ""
            }
            navigateGpx(gpx, f.nameWithoutExtension)
        }
        val del = TextView(this)
        del.text = "Delete"
        del.setTextColor(Color.parseColor("#FFE53935"))
        del.textSize = 15f
        del.setPadding(dp(10), 0, dp(4), 0)
        del.setOnClickListener {
            f.delete()
            rebuild()
        }
        c.addView(t)
        c.addView(rideBtn)
        c.addView(del)
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, 0).show()
    }
}

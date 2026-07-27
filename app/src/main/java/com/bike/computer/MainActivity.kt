package com.bike.computer

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import btools.router.NavHint
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.location.OnCameraTrackingChangedListener
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.module.http.HttpRequestUtil
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.FillExtrusionLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** The main head-unit screen: swipeable pages (Map/Data/HR/Elevation/Summary), map + navigation. */
class MainActivity : Activity() {
    private companion object {
        // Distance (m) below which a turn is "imminent" — the compact banner re-expands to full.
        const val NAV_NEAR_M = 150.0
    }

    private val DATA = "/sdcard/BikeComputer"

    private lateinit var pageMap: View
    private lateinit var pageHr: View
    private lateinit var pageElev: View
    private lateinit var pageSummary: View
    private lateinit var mapView: MapView
    private lateinit var pager: ViewPager2
    private lateinit var recenterBtn: ImageView
    private lateinit var navBanner: View
    private lateinit var navFull: View
    private lateinit var navCompact: View
    private lateinit var navArrow: TextView
    private lateinit var navText: TextView
    private lateinit var navDist: TextView
    private lateinit var navArrowC: TextView
    private lateinit var navDistC: TextView
    private lateinit var navStreetC: TextView
    private lateinit var mSpeed: TextView
    private lateinit var mHr: TextView
    private lateinit var mDist: TextView
    private lateinit var mTime: TextView
    private lateinit var homeBtn: ImageView
    private lateinit var elevView: ElevationView
    private lateinit var eEle: TextView
    private lateinit var eAscent: TextView
    private lateinit var eGrade: TextView
    private lateinit var sDist: TextView
    private lateinit var sTime: TextView
    private lateinit var sAvgSpd: TextView
    private lateinit var sMaxSpd: TextView
    private lateinit var sAvgHr: TextView
    private lateinit var sMaxHr: TextView
    private lateinit var sAscent: TextView
    private lateinit var sStarted: TextView
    private lateinit var hrBig: TextView
    private lateinit var hrZoneLbl: TextView
    private lateinit var hrGraph: HrGraphView
    private lateinit var hrLegend: LinearLayout
    private lateinit var recBtn: TextView
    private lateinit var pauseBtn: TextView
    private lateinit var resumeBtn: TextView
    private lateinit var stopBtn: View
    private lateinit var stopFill: View
    private lateinit var stopLabel: TextView
    private lateinit var recBar: View
    private lateinit var pausedBadge: TextView
    private lateinit var dotsBar: LinearLayout
    private lateinit var bellBtn: ImageView

    private var map: MapLibreMap? = null
    private var loadedStyle: Style? = null
    private var trailSource: GeoJsonSource? = null
    private var routeSource: GeoJsonSource? = null
    private var ride: RideService? = null

    private var currentRouteName: String? = null
    private var lastFocusX = 0f
    private var lastFocusY = 0f
    private var lastTrailSize = 0
    private var lowBattWarned = false
    private var mapPageIndex = 0
    private var screenOffArmed = false
    private var stopHolding = false
    private var twoFingerActive = false
    private var mapBearing = Double.NaN
    private var hrPageMaxHr = 0
    // Nav banner (UI) expand/collapse state; the nav engine itself lives in RideService.
    private var navShownStep = Int.MIN_VALUE
    private var navNearActive = false
    private var navDTurn = Double.MAX_VALUE
    private var lastNavVersion = -1
    private var pageSig = ""
    private var cameraTracking = true
    private var controlsVisible = true
    private val statusBars = ArrayList<Pair<TextView, TextView>>()

    private val dataPages = LinkedHashMap<String, View>()
    private val dashViews = ArrayList<DashboardView>()
    private var dots: List<TextView> = emptyList()
    private val hrTimeLbls = ArrayList<TextView>()
    private val hrPctLbls = ArrayList<TextView>()
    private val ui = Handler(Looper.getMainLooper())
    private val clockFmt = SimpleDateFormat("h:mm", Locale.US)
    private val screenOffMs = 30000L
    private val perms = arrayOf(
        "android.permission.ACCESS_FINE_LOCATION",
        "android.permission.BLUETOOTH_SCAN",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.POST_NOTIFICATIONS",
    )

    private val hideRunnable = Runnable { hideChrome() }
    private val screenOffRunnable = Runnable {
        screenOffArmed = false
        sleepScreen()
    }
    private val stopCompleteRunnable = Runnable {
        if (stopHolding) {
            stopHolding = false
            resetStopButton()
            stopRec()
        }
    }
    private val navCollapseRunnable = Runnable {
        // Keep the banner open if a turn is imminent; otherwise shrink to the compact strip.
        if (ride?.navigating == true && navDTurn >= NAV_NEAR_M) setNavExpanded(false)
    }

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder) {
            ride = (service as RideService.LocalBinder).service
            ride?.onUpdate = { runOnUiThread { onRideUpdate() } }
            ride?.onNavUpdate = { runOnUiThread { onNavState() } }
            ride?.autoPauseEnabled = Prefs.autoPause(this@MainActivity)
            updateRecUi()
            enableLocationDot()
            onRideUpdate()
            onNavState()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            ride = null
        }
    }

    private class PageAdapter(private val pages: List<View>) : RecyclerView.Adapter<PageAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v)

        override fun getItemCount(): Int = pages.size
        override fun getItemViewType(position: Int): Int = position

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = pages[viewType]
            (v.parent as? ViewGroup)?.removeView(v)
            v.layoutParams = ViewGroup.LayoutParams(-1, -1)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapLibre.getInstance(this)
        MapLibre.setConnected(true)
        setContentView(R.layout.activity_main)
        window.addFlags(128)
        enterImmersive()
        val inf = LayoutInflater.from(this)
        pageMap = inf.inflate(R.layout.page_map, null)
        pageHr = inf.inflate(R.layout.page_hr, null)
        pageElev = inf.inflate(R.layout.page_elevation, null)
        pageSummary = inf.inflate(R.layout.page_summary, null)
        bindViews()
        HttpRequestUtil.setOkHttpClient(
            OkHttpClient.Builder()
                .addInterceptor(LocalTiles("$filesDir/california.mbtiles", "$DATA/styles/fonts"))
                .build(),
        )
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { m -> onMapReady(m) }
        pager = findViewById(R.id.pager)
        pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                dots.forEachIndexed { i, d -> d.alpha = if (i == position) 1f else 0.35f }
                updateRecUi()
                syncChrome(false)
            }
        })
        buildPager(true)
        recBtn.setOnClickListener { startRec() }
        bellBtn.setOnClickListener {
            Bell.ring(this)
            showChrome()
        }
        Bell.prewarm()
        pauseBtn.setOnClickListener {
            ride?.togglePause()
            updateRecUi()
        }
        resumeBtn.setOnClickListener {
            ride?.togglePause()
            updateRecUi()
        }
        recenterBtn.setOnClickListener { reCenter() }
        homeBtn.setOnClickListener {
            AppState.onMap = false
            startActivity(Intent(this, WelcomeActivity::class.java).addFlags(603979776))
            finish()
        }
        stopBtn.setOnTouchListener { _, ev ->
            when (ev.actionMasked) {
                MotionEvent.ACTION_DOWN -> beginStopHold()
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> endStopHold()
            }
            true
        }
        navBanner.setOnLongClickListener {
            if (ride?.navigating == true) {
                AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                    .setTitle("Navigation")
                    .setMessage("Stop turn-by-turn navigation?")
                    .setPositiveButton("Stop navigation") { _, _ ->
                        cancelNav()
                        Toast.makeText(this, "Navigation stopped", 0).show()
                    }
                    .setNegativeButton("Keep", null)
                    .show()
            }
            true
        }
        Voice.enabled = Prefs.voice(this)
        val missing = perms.filter { checkSelfPermission(it) != 0 }
        if (missing.isNotEmpty()) requestPermissions(missing.toTypedArray(), 1)
        uiTick()
    }

    private fun onMapReady(m: MapLibreMap) {
        map = m
        m.uiSettings.setAllGesturesEnabled(false)
        m.uiSettings.isZoomGesturesEnabled = true
        m.uiSettings.isQuickZoomGesturesEnabled = false
        m.uiSettings.isLogoEnabled = false
        m.uiSettings.isAttributionEnabled = false
        m.uiSettings.isCompassEnabled = false
        m.setMaxPitchPreference(72.0)
        m.moveCamera(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.Builder().target(LatLng(37.788, -122.4431)).zoom(18.5).tilt(65.0).build(),
            ),
        )
        m.setStyle(Style.Builder().fromJson(buildStyle())) { style ->
            loadedStyle = style
            addBikeLayer(style)
            add3dBuildings(style)
            addTrailLayer(style)
            addRouteLayer(style)
            enableLocationDot()
            applyMapPower()
            // Route source is fresh after a (re)load; force the nav line/banner to redraw.
            lastNavVersion = -1
            onNavState()
        }
        m.addOnMapLongClickListener { ll -> startNavAt(ll.latitude, ll.longitude) }
        m.addOnCameraMoveStartedListener { reason -> if (reason == 1) onUserMovedMap() }
        mapView.setOnTouchListener { _, ev ->
            handleMapTouch(ev)
            false
        }
    }

    override fun onRequestPermissionsResult(rc: Int, p: Array<out String>, results: IntArray) {
        ride?.retryPermissions()
        enableLocationDot()
    }

    private fun bindViews() {
        mapView = pageMap.findViewById(R.id.mapView)
        recenterBtn = pageMap.findViewById(R.id.recenter_btn)
        navBanner = pageMap.findViewById(R.id.nav_banner)
        navFull = pageMap.findViewById(R.id.nav_full)
        navCompact = pageMap.findViewById(R.id.nav_compact)
        navArrow = pageMap.findViewById(R.id.nav_arrow)
        navText = pageMap.findViewById(R.id.nav_text)
        navDist = pageMap.findViewById(R.id.nav_dist)
        navArrowC = pageMap.findViewById(R.id.nav_arrow_c)
        navDistC = pageMap.findViewById(R.id.nav_dist_c)
        navStreetC = pageMap.findViewById(R.id.nav_street_c)
        mSpeed = pageMap.findViewById(R.id.val_speed)
        mHr = pageMap.findViewById(R.id.val_hr)
        mDist = pageMap.findViewById(R.id.val_dist)
        mTime = pageMap.findViewById(R.id.val_time)
        homeBtn = pageMap.findViewById(R.id.home_btn)
        elevView = pageElev.findViewById(R.id.elev_view)
        eEle = pageElev.findViewById(R.id.e_ele)
        eAscent = pageElev.findViewById(R.id.e_ascent)
        eGrade = pageElev.findViewById(R.id.e_grade)
        sDist = pageSummary.findViewById(R.id.s_dist)
        sTime = pageSummary.findViewById(R.id.s_time)
        sAvgSpd = pageSummary.findViewById(R.id.s_avgspd)
        sMaxSpd = pageSummary.findViewById(R.id.s_maxspd)
        sAvgHr = pageSummary.findViewById(R.id.s_avghr)
        sMaxHr = pageSummary.findViewById(R.id.s_maxhr)
        sAscent = pageSummary.findViewById(R.id.s_ascent)
        sStarted = pageSummary.findViewById(R.id.s_started)
        hrBig = pageHr.findViewById(R.id.hr_big)
        hrZoneLbl = pageHr.findViewById(R.id.hr_zone)
        hrGraph = pageHr.findViewById(R.id.hr_graph)
        hrLegend = pageHr.findViewById(R.id.hr_legend)
        buildHrPage()
        recBtn = findViewById(R.id.rec_btn)
        pauseBtn = findViewById(R.id.pause_btn)
        resumeBtn = findViewById(R.id.resume_btn)
        stopBtn = findViewById(R.id.stop_btn)
        stopFill = findViewById(R.id.stop_fill)
        stopLabel = findViewById(R.id.stop_label)
        recBar = findViewById(R.id.rec_bar)
        pausedBadge = findViewById(R.id.paused_badge)
        dotsBar = findViewById(R.id.dots)
        bellBtn = findViewById(R.id.bell_btn)
        registerStatusBar(pageSummary)
        registerStatusBar(pageHr)
        registerStatusBar(pageElev)
    }

    private fun pageViewForKey(key: String): View = when (key) {
        Pages.SUMMARY -> pageSummary
        Pages.HR -> pageHr
        Pages.MAP -> pageMap
        Pages.ELEV -> pageElev
        else -> dataPageView(key)
    }

    private fun dataPageView(key: String): View {
        var v = dataPages[key]
        if (v == null) {
            v = LayoutInflater.from(this).inflate(R.layout.page_data, null)
            val dv = v.findViewById<DashboardView>(R.id.dash_view)
            val toolbar = v.findViewById<View>(R.id.dash_toolbar)
            dv.valueProvider = { m -> metricValue(m) }
            dv.onChanged = { Prefs.setDashTiles(this, key, dv.tiles()) }
            dv.onPickMetric = { tile -> showMetricPicker(dv, tile) }
            dv.onEditModeChanged = { on ->
                toolbar.visibility = if (on) View.VISIBLE else View.GONE
                pager.isUserInputEnabled = !on
            }
            dv.setOnLongClickListener {
                if (!dv.editing) dv.setEditing(true)
                true
            }
            v.findViewById<View>(R.id.dash_add).setOnClickListener { dv.addTile(Metric.SPEED) }
            v.findViewById<View>(R.id.dash_done).setOnClickListener { dv.setEditing(false) }
            dv.setTiles(Prefs.dashTiles(this, key))
            dataPages[key] = v
        }
        val it = v.findViewById<DashboardView>(R.id.dash_view)
        if (it != null && !dashViews.contains(it)) dashViews.add(it)
        // Glanceable time + battery strip; nudge the grid down to make room (shortens every box
        // a touch).
        (it.layoutParams as ViewGroup.MarginLayoutParams).topMargin = dp(28)
        it.requestLayout()
        registerStatusBar(v)
        updateStatusBars()
        return v
    }

    /** Registers a page's time/battery strip (if present) so uiTick keeps it current. */
    private fun registerStatusBar(root: View) {
        val timeTv = root.findViewById<TextView>(R.id.data_status_time) ?: return
        val battTv = root.findViewById<TextView>(R.id.data_status_batt) ?: return
        if (statusBars.none { it.first === timeTv }) statusBars.add(timeTv to battTv)
    }

    private fun updateStatusBars() {
        if (statusBars.isEmpty()) return
        val time = clockFmt.format(Date())
        val (pct, charging) = batteryInfo()
        val batt = when {
            pct !in 0..100 -> "--%"
            charging -> "$pct% ⚡"
            else -> "$pct%"
        }
        for ((timeTv, battTv) in statusBars) {
            timeTv.text = time
            battTv.text = batt
        }
    }

    private fun buildPager(goToMap: Boolean) {
        val enabled = Prefs.enabledPagesInOrder(this)
        pageSig = Prefs.pageSignature(this)
        mapPageIndex = enabled.indexOf(Pages.MAP).coerceAtLeast(0)
        var current = if (!this::pager.isInitialized) {
            mapPageIndex
        } else if (pager.adapter != null) {
            pager.currentItem.coerceIn(0, (enabled.size - 1).coerceAtLeast(0))
        } else {
            0
        }
        dashViews.clear()
        pager.adapter = PageAdapter(enabled.map { pageViewForKey(it) })
        pager.offscreenPageLimit = (enabled.size - 1).coerceAtLeast(1)
        dotsBar.removeAllViews()
        dots = enabled.indices.map {
            TextView(this).apply {
                text = "●"
                setTextColor(-1)
                textSize = 12f
                layoutParams = LinearLayout.LayoutParams(-2, -2).apply {
                    leftMargin = dp(4)
                    rightMargin = dp(4)
                }
            }
        }
        for (d in dots) dotsBar.addView(d)
        val start = if (goToMap) mapPageIndex else current
        pager.setCurrentItem(start, false)
        dots.forEachIndexed { i, d -> d.alpha = if (i == start) 1f else 0.35f }
    }

    private fun buildStyle(): String {
        try {
            val o = JSONObject(File("$DATA/styles/style.json").readText())
            val src = JSONObject().put("type", "vector")
                .put("tiles", JSONArray().put("http://bike.local/tiles/{z}/{x}/{y}.pbf"))
                .put("minzoom", 0).put("maxzoom", 14)
            val sources = o.getJSONObject("sources")
            for (k in sources.keys().asSequence().toList()) sources.put(k, src)
            o.put("glyphs", "http://bike.local/fonts/{fontstack}/{range}.pbf")
            o.remove("sprite")
            return o.toString()
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
    }

    private fun add3dBuildings(style: Style) {
        try {
            if (style.getLayer("3d-buildings") != null) return
            val layer = FillExtrusionLayer("3d-buildings", "openmaptiles")
            layer.sourceLayer = "building"
            layer.minZoom = 15f
            layer.setFilter(Expression.gte(Expression.get("render_height"), Expression.literal(5.0f as Number)))
            layer.setProperties(
                PropertyFactory.fillExtrusionColor(Color.parseColor("#e6e0d2")),
                PropertyFactory.fillExtrusionHeight(Expression.get("render_height")),
                PropertyFactory.fillExtrusionBase(Expression.get("render_min_height")),
                PropertyFactory.fillExtrusionOpacity(1.0f),
                PropertyFactory.fillExtrusionVerticalGradient(false),
            )
            val firstSymbol = style.layers.firstOrNull { it is SymbolLayer }
            if (firstSymbol != null) style.addLayerBelow(layer, firstSymbol.id) else style.addLayer(layer)
        } catch (e: Exception) {
            Log.e("BikeMap", "3d buildings failed", e)
        }
    }

    private fun addBikeLayer(style: Style) {
        try {
            if (style.getLayer("bike-paths") != null) return
            val filter = Expression.any(
                Expression.eq(Expression.get("subclass"), Expression.literal("cycleway")),
                Expression.eq(Expression.get("class"), Expression.literal("cycleway")),
                Expression.eq(Expression.get("bicycle"), Expression.literal("designated")),
            )
            val casing = LineLayer("bike-paths-casing", "openmaptiles")
            casing.sourceLayer = "transportation"
            casing.minZoom = 11f
            casing.setFilter(filter)
            casing.setProperties(
                PropertyFactory.lineColor(Color.parseColor("#FFFFFF")),
                PropertyFactory.lineCap("round"),
                PropertyFactory.lineJoin("round"),
                PropertyFactory.lineOpacity(0.85f),
                PropertyFactory.lineWidth(bikeWidth(1.6f)),
            )
            val line = LineLayer("bike-paths", "openmaptiles")
            line.sourceLayer = "transportation"
            line.minZoom = 11f
            line.setFilter(filter)
            line.setProperties(
                PropertyFactory.lineColor(Color.parseColor("#00C853")),
                PropertyFactory.lineCap("round"),
                PropertyFactory.lineJoin("round"),
                PropertyFactory.lineOpacity(0.95f),
                PropertyFactory.lineWidth(bikeWidth(1.0f)),
            )
            val firstSymbol = style.layers.firstOrNull { it is SymbolLayer }
            if (firstSymbol != null) {
                style.addLayerBelow(casing, firstSymbol.id)
                style.addLayerBelow(line, firstSymbol.id)
            } else {
                style.addLayer(casing)
                style.addLayer(line)
            }
        } catch (e: Exception) {
            Log.e("BikeMap", "bike layer failed", e)
        }
    }

    private fun bikeWidth(mult: Float): Expression = Expression.interpolate(
        Expression.linear(), Expression.zoom(),
        Expression.stop(11, 2.0f * mult),
        Expression.stop(14, 3.6f * mult),
        Expression.stop(16, 6.0f * mult),
        Expression.stop(18, 9.0f * mult),
    )

    private fun addTrailLayer(style: Style) {
        try {
            val src = GeoJsonSource("trail")
            trailSource = src
            style.addSource(src)
            style.addLayer(
                LineLayer("trail-line", "trail").withProperties(
                    PropertyFactory.lineColor(Color.parseColor("#FF6D00")),
                    PropertyFactory.lineWidth(6.0f),
                    PropertyFactory.lineOpacity(0.95f),
                    PropertyFactory.lineCap("round"),
                    PropertyFactory.lineJoin("round"),
                ),
            )
        } catch (e: Exception) {
            Log.e("BikeMap", "trail layer failed", e)
        }
    }

    private fun addRouteLayer(style: Style) {
        try {
            val src = GeoJsonSource("route")
            routeSource = src
            style.addSource(src)
            style.addLayer(
                LineLayer("route-casing", "route").withProperties(
                    PropertyFactory.lineColor(Color.parseColor("#0B3D91")),
                    PropertyFactory.lineWidth(12.0f),
                    PropertyFactory.lineOpacity(0.95f),
                    PropertyFactory.lineCap("round"),
                    PropertyFactory.lineJoin("round"),
                ),
            )
            style.addLayer(
                LineLayer("route-line", "route").withProperties(
                    PropertyFactory.lineColor(Color.parseColor("#4C8DFF")),
                    PropertyFactory.lineWidth(7.0f),
                    PropertyFactory.lineOpacity(1.0f),
                    PropertyFactory.lineCap("round"),
                    PropertyFactory.lineJoin("round"),
                ),
            )
        } catch (e: Exception) {
            Log.e("BikeMap", "route layer failed", e)
        }
    }

    /** Nav now runs in RideService; ensure it's a started foreground service so it survives. */
    private fun beginNavService() {
        startService(Intent(this, RideService::class.java).setAction(RideService.ACTION_START_NAV))
    }

    private fun startNavAt(lat: Double, lon: Double): Boolean {
        val r = ride ?: return true
        currentRouteName = null
        beginNavService()
        r.startNavigation(lat, lon)
        return true
    }

    private fun enableLocationDot() {
        val m = map ?: return
        val style = loadedStyle ?: return
        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != 0) return
        val lc = m.locationComponent
        if (!lc.isLocationComponentActivated) {
            lc.activateLocationComponent(
                LocationComponentActivationOptions.builder(this, style)
                    .useDefaultLocationEngine(false).build(),
            )
        }
        lc.isLocationComponentEnabled = true
        lc.renderMode = 8
        mapView.post {
            map?.setPadding(0, (mapView.height * 0.55f).toInt(), 0, 0)
        }
        ride?.lastLocation?.let { lc.forceLocationUpdate(it) }
        lc.addOnCameraTrackingChangedListener(object : OnCameraTrackingChangedListener {
            override fun onCameraTrackingDismissed() {
                cameraTracking = false
                if (pager.currentItem == mapPageIndex) recenterBtn.visibility = View.VISIBLE
            }

            override fun onCameraTrackingChanged(currentMode: Int) {}
        })
        reCenter()
    }

    private fun handleMapTouch(ev: MotionEvent) {
        when (ev.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                twoFingerActive = false
                mapView.parent?.requestDisallowInterceptTouchEvent(false)
            }
            MotionEvent.ACTION_MOVE -> {
                if (twoFingerActive && ev.pointerCount >= 2) {
                    val fx = (ev.getX(0) + ev.getX(1)) / 2f
                    val fy = (ev.getY(0) + ev.getY(1)) / 2f
                    val dx = fx - lastFocusX
                    val dy = fy - lastFocusY
                    lastFocusX = fx
                    lastFocusY = fy
                    if (dx != 0f || dy != 0f) {
                        onUserMovedMap()
                        map?.scrollBy(dx, dy)
                    }
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                if (ev.pointerCount == 2) {
                    twoFingerActive = true
                    lastFocusX = (ev.getX(0) + ev.getX(1)) / 2f
                    lastFocusY = (ev.getY(0) + ev.getY(1)) / 2f
                    mapView.parent?.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (ev.pointerCount <= 2) twoFingerActive = false
            }
        }
    }

    private fun onUserMovedMap() {
        if (cameraTracking) {
            cameraTracking = false
            map?.locationComponent?.let { if (it.isLocationComponentActivated) it.cameraMode = 8 }
            if (pager.currentItem == mapPageIndex) recenterBtn.visibility = View.VISIBLE
        }
    }

    private fun mapLowPower(): Boolean = Prefs.lowPowerMap(this) || Prefs.endurance(this)

    private fun reCenter() {
        val m = map ?: return
        val lc = m.locationComponent
        if (lc.isLocationComponentActivated) {
            lc.cameraMode = 34
            lc.tiltWhileTracking(if (mapLowPower()) 0.0 else 65.0)
            cameraTracking = true
            recenterBtn.visibility = View.GONE
        }
    }

    private fun applyMapPower() {
        val endurance = Prefs.endurance(this)
        val low = Prefs.lowPowerMap(this) || endurance
        try {
            mapView.setMaximumFps(if (endurance) 10 else if (low) 15 else 30)
        } catch (t: Throwable) {
        }
        loadedStyle?.let { style ->
            val has = style.getLayer("3d-buildings") != null
            if (low && has) {
                try {
                    style.removeLayer("3d-buildings")
                } catch (t: Throwable) {
                }
            } else if (!low && !has) {
                add3dBuildings(style)
            }
        }
        val tilt = if (low) 0.0 else 65.0
        val m = map
        if (m != null && m.locationComponent.isLocationComponentActivated && cameraTracking) {
            val cur = m.cameraPosition
            m.moveCamera(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder().target(cur.target).zoom(cur.zoom)
                        .bearing(cur.bearing).tilt(tilt).build(),
                ),
            )
            reCenter()
        }
        val a = window.attributes
        a.screenBrightness = if (endurance) 0.15f else -1.0f
        window.attributes = a
    }

    private fun mapLoc(raw: Location, speedMps: Float): Location {
        val out = Location(raw)
        if (speedMps >= 1.4f && raw.hasBearing()) {
            mapBearing = if (mapBearing.isNaN()) {
                raw.bearing.toDouble()
            } else {
                val delta = ((raw.bearing - mapBearing + 540) % 360) - 180
                (mapBearing + 0.2 * delta + 360) % 360
            }
        }
        if (!mapBearing.isNaN()) out.bearing = mapBearing.toFloat()
        return out
    }

    private fun speedZoom(mps: Float) {
        val lc = map?.locationComponent ?: return
        if (!lc.isLocationComponentActivated) return
        val z = (18.7f - (Units.mph(mps) / 20.0f) * 2.2f).coerceIn(16.5f, 18.7f)
        lc.zoomWhileTracking(z.toDouble())
    }

    private fun tryConsumePendingRoute() {
        val r = ride ?: return
        val dest = ActionBus.pendingDestination
        if (dest != null) {
            if (r.lastLocation == null) return
            ActionBus.pendingDestination = null
            currentRouteName = null
            beginNavService()
            pager.setCurrentItem(mapPageIndex, false)
            r.startNavigation(dest[0], dest[1])
            return
        }
        val pendingRoute = ActionBus.pendingRoute ?: return
        if (r.lastLocation == null) return
        ActionBus.pendingRoute = null
        currentRouteName = ActionBus.pendingRouteName
        ActionBus.pendingRouteName = null
        beginNavService()
        pager.setCurrentItem(mapPageIndex, false)
        r.startRouteNavigation(pendingRoute, currentRouteName)
    }

    private fun onRideUpdate() {
        val r = ride ?: return
        tryConsumePendingRoute()
        r.lastLocation?.let { loc ->
            map?.locationComponent?.let {
                if (it.isLocationComponentActivated) it.forceLocationUpdate(mapLoc(loc, r.curSpeedMps))
            }
            speedZoom(r.curSpeedMps)
        }
        if (r.trail.size != lastTrailSize) {
            lastTrailSize = r.trail.size
            if (r.trail.size >= 2) {
                trailSource?.setGeoJson(LineString.fromLngLats(r.trail.map { Point.fromLngLat(it[0], it[1]) }))
            } else {
                trailSource?.setGeoJson("{\"type\":\"FeatureCollection\",\"features\":[]}")
            }
        }
        mSpeed.text = Units.fmtSpeed(r.curSpeedMps)
        mHr.text = if (r.lastHr > 0) r.lastHr.toString() else "--"
    }

    /** Render the banner + route line from the nav engine's state (which lives in RideService). */
    private fun onNavState() {
        val r = ride
        if (r == null || !r.navigating) {
            if (navBanner.visibility != View.GONE) {
                navBanner.visibility = View.GONE
                ui.removeCallbacks(navCollapseRunnable)
                setNavExpanded(true)
                navShownStep = Int.MIN_VALUE
                navNearActive = false
                routeSource?.setGeoJson("{\"type\":\"FeatureCollection\",\"features\":[]}")
                recenterBtn.visibility =
                    if (pager.currentItem != mapPageIndex || cameraTracking) View.GONE else View.VISIBLE
            }
            lastNavVersion = r?.navVersionNum ?: -1
            return
        }
        // Redraw the route line + recenter when the route changes (new route / reroute).
        if (r.navVersionNum != lastNavVersion) {
            lastNavVersion = r.navVersionNum
            val pts = r.navPolyline()
            if (pts.size >= 2) {
                routeSource?.setGeoJson(LineString.fromLngLats(pts.map { Point.fromLngLat(it[0], it[1]) }))
            }
            navBanner.visibility = View.VISIBLE
            setNavExpanded(true)
            navShownStep = Int.MIN_VALUE
            navNearActive = false
            reCenter()
        }
        navBanner.visibility = View.VISIBLE
        navArrow.text = r.navArrow
        navText.text = r.navInstruction
        navDist.text = r.navDistText
        navArrowC.text = r.navArrow
        navDistC.text = r.navDistText
        navStreetC.text = r.navStreet
        navDTurn = r.navDTurnM
        if (r.navStepKey != navShownStep) {
            navShownStep = r.navStepKey
            navNearActive = false
            popNavExpanded()
        }
        if (r.navArrived) {
            ui.removeCallbacks(navCollapseRunnable)
            setNavExpanded(true)
        } else if (r.navDTurnM < NAV_NEAR_M) {
            if (!navNearActive) {
                navNearActive = true
                popNavExpanded()
            }
        } else {
            navNearActive = false
        }
    }

    private fun setNavExpanded(expanded: Boolean) {
        navFull.visibility = if (expanded) View.VISIBLE else View.GONE
        navCompact.visibility = if (expanded) View.GONE else View.VISIBLE
    }

    private fun popNavExpanded() {
        setNavExpanded(true)
        ui.removeCallbacks(navCollapseRunnable)
        ui.postDelayed(navCollapseRunnable, 5000L)
    }

    private fun cancelNav() {
        ride?.cancelNavigation()
        onNavState()
    }

    fun startRec() {
        startForegroundService(
            Intent(this, RideService::class.java).setAction(RideService.ACTION_START_REC),
        )
        updateRecUi()
        recBtn.visibility = View.GONE
        controlsVisible = true
        setFaded(homeBtn, pager.currentItem == mapPageIndex, true)
        setFaded(recBar, true, true)
        ui.removeCallbacks(hideRunnable)
        ui.postDelayed(hideRunnable, 5000L)
    }

    fun stopRec() {
        val r = ride ?: return
        val rec = r.recorder
        val startMs = rec.startMs
        val summary = if (startMs > 0 && rec.points >= 2) {
            RideSummary(
                startMs, currentRouteName, rec.distanceM, rec.elapsedMs, rec.avgSpeedMps,
                rec.maxSpeedMps, rec.hrAvg, rec.hrMax, rec.ascentM, rec.powerAvg, rec.powerMax,
                null, false, null,
            )
        } else {
            null
        }
        val path = r.stopRecording()
        currentRouteName = null
        ui.removeCallbacks(hideRunnable)
        updateRecUi()
        syncChrome(false)
        // Drive upload is intentionally NOT attempted here — there's rarely signal when a ride
        // ends. The ride is saved unuploaded and pushed later in the background (Welcome screen,
        // once wifi is back). No failure toast.
        if (summary != null) {
            // NOTE: decompiled copy$default mask was corrupt (dropped the computed .gpx filename,
            // recording gpx=null) — a jadx artifact. Restored to link the saved file.
            val gpxName = if (path != null) path.substringAfterLast('/') else null
            RideHistory.add(
                summary.copy(
                    summary.startMs, summary.route, summary.distanceM, summary.movingMs,
                    summary.avgMps, summary.maxMps, summary.hrAvg, summary.hrMax, summary.ascentM,
                    summary.powerAvg, summary.powerMax, gpxName, summary.uploaded, summary.name,
                ),
            )
            AppState.onMap = false
            startActivity(
                Intent(this, RideSummaryActivity::class.java)
                    .putExtra("startMs", startMs).putExtra("welcomeOnDone", true),
            )
        } else {
            Toast.makeText(this, "Ride too short to save", 0).show()
        }
    }

    private fun updateRecUi() {
        val recorder = ride?.recorder
        val recording = recorder?.isRecording == true
        val paused = recorder?.paused == true
        pauseBtn.visibility = if (recording && !paused) View.VISIBLE else View.GONE
        resumeBtn.visibility = if (recording && paused) View.VISIBLE else View.GONE
        stopBtn.visibility = if (recording && paused) View.VISIBLE else View.GONE
        pausedBadge.visibility = if (recording && paused) View.VISIBLE else View.GONE
        recBtn.visibility = if (recording) View.GONE else View.VISIBLE
        dotsBar.visibility = View.VISIBLE
        recenterBtn.visibility =
            if (pager.currentItem != mapPageIndex || cameraTracking) View.GONE else View.VISIBLE
        updateScreenPolicy()
    }

    private fun showChrome() {
        controlsVisible = true
        syncChrome(true)
        ui.removeCallbacks(hideRunnable)
        ui.postDelayed(hideRunnable, 5000L)
    }

    private fun hideChrome() {
        controlsVisible = false
        syncChrome(true)
    }

    private fun syncChrome(animate: Boolean) {
        val onMap = pager.currentItem == mapPageIndex
        val recording = ride?.recorder?.isRecording == true
        // Home stays available on the map page so the menu is always reachable, even after
        // navigation ends or the rest of the chrome fades out.
        setFaded(homeBtn, onMap, animate)
        setFaded(recBar, controlsVisible && recording, animate)
        setFaded(bellBtn, controlsVisible && (onMap || recording), animate, 0.8f)
    }

    private fun setFaded(v: View, show: Boolean, animate: Boolean, shownAlpha: Float = 1.0f) {
        v.animate().cancel()
        if (show) {
            if (v.visibility != View.VISIBLE) v.alpha = 0f
            v.visibility = View.VISIBLE
            if (animate) {
                v.animate().alpha(shownAlpha).setDuration(200L).start()
            } else {
                v.alpha = shownAlpha
            }
            return
        }
        if (animate && v.visibility == View.VISIBLE && v.alpha > 0f) {
            v.animate().alpha(0f).setDuration(900L).withEndAction {
                v.alpha = 0f
                v.visibility = View.INVISIBLE
            }.start()
        } else {
            v.alpha = 0f
            v.visibility = View.INVISIBLE
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            val onMap = pager.currentItem == mapPageIndex
            val recording = ride?.recorder?.isRecording == true
            if (onMap || recording) showChrome()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun beginStopHold() {
        stopHolding = true
        stopFill.pivotX = 0f
        stopFill.scaleX = 0f
        stopFill.animate().scaleX(1f).setDuration(1500L).setInterpolator(LinearInterpolator()).start()
        ui.removeCallbacks(hideRunnable)
        ui.postDelayed(stopCompleteRunnable, 1500L)
    }

    private fun endStopHold() {
        if (stopHolding) {
            stopHolding = false
            ui.removeCallbacks(stopCompleteRunnable)
            resetStopButton()
            ui.postDelayed(hideRunnable, 5000L)
        }
    }

    private fun resetStopButton() {
        stopFill.animate().cancel()
        stopFill.animate().scaleX(0f).setDuration(150L).start()
        stopLabel.text = "■  Stop"
    }

    private fun dp(v: Int): Int = (v * resources.displayMetrics.density).toInt()

    private fun showMetricPicker(view: DashboardView, tile: DashTile) {
        val metrics = Metric.entries.toTypedArray()
        val labels = metrics.map { it.label + (if (it.unit.isNotEmpty()) "  (${it.unit})" else "") }.toTypedArray()
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Metric")
            .setItems(labels) { _, i -> view.applyMetric(tile, metrics[i]) }
            .show()
    }

    private fun liveZone(hr: Int, maxHr: Int): Int {
        val f = hr.toDouble() / maxHr
        return when {
            f >= 0.9 -> 4
            f >= 0.8 -> 3
            f >= 0.7 -> 2
            f >= 0.6 -> 1
            else -> 0
        }
    }

    private fun fmtZoneTime(ms: Long): String {
        val s = ms / 1000
        return if (s >= 3600) {
            String.format(Locale.US, "%d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60)
        } else {
            String.format(Locale.US, "%d:%02d", s / 60, s % 60)
        }
    }

    private fun buildHrPage() {
        hrPageMaxHr = Prefs.maxHr(this)
        hrLegend.removeAllViews()
        hrTimeLbls.clear()
        hrPctLbls.clear()
        val medium = Typeface.create("sans-serif-medium", 0)

        fun cell(t: String, size: Float, colr: Int, med: Boolean, topPad: Int): TextView {
            val tv = TextView(this)
            tv.text = t
            tv.setTextColor(colr)
            tv.textSize = size
            if (med) tv.typeface = medium
            tv.includeFontPadding = false
            tv.gravity = 17
            tv.setPadding(0, dp(topPad), 0, 0)
            tv.layoutParams = LinearLayout.LayoutParams(-1, -2)
            return tv
        }

        HrZone.entries.forEachIndexed { i, zone ->
            val color = zone.color.toInt()
            val col = LinearLayout(this)
            col.orientation = LinearLayout.VERTICAL
            col.gravity = 1
            col.setBackgroundResource(R.drawable.card_solid)
            col.setPadding(dp(2), dp(7), dp(2), dp(7))
            col.layoutParams = LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(dp(2), 0, dp(2), 0) }
            val swatch = View(this)
            swatch.background = GradientDrawable().apply {
                setColor(color)
                cornerRadius = dp(2).toFloat()
            }
            swatch.layoutParams = LinearLayout.LayoutParams(dp(22), dp(4)).apply { gravity = 1 }
            val zlbl = cell("Z${i + 1}", 11f, Color.parseColor("#FF9E9E9E"), false, 3)
            val pct = cell("0%", 15f, -1, true, 1)
            val time = cell("0:00", 11f, Color.parseColor("#FF8E8E93"), false, 0)
            col.addView(swatch)
            col.addView(zlbl)
            col.addView(pct)
            col.addView(time)
            hrLegend.addView(col)
            hrPctLbls.add(pct)
            hrTimeLbls.add(time)
        }
    }

    private fun updateHrPage() {
        if (hrPctLbls.isEmpty()) return
        val hr = ride?.lastHr ?: 0
        hrBig.text = if (hr > 0) hr.toString() else "--"
        val maxHr = hrPageMaxHr.coerceAtLeast(1)
        if (hr > 0) {
            val zi = liveZone(hr, maxHr)
            val z = HrZone.entries[zi]
            hrZoneLbl.text = "Z${zi + 1} · ${z.label}"
            hrZoneLbl.setTextColor(z.color.toInt())
        } else {
            hrZoneLbl.text = "—"
            hrZoneLbl.setTextColor(Color.parseColor("#FF8E8E93"))
        }
        hrGraph.setData(ride?.recorder?.hrHistMs ?: LongArray(0), maxHr, hr)
        val zoneMs = ride?.recorder?.zoneMs
        val total = zoneMs?.sum() ?: 0L
        for (i in 0 until 5) {
            val ms = zoneMs?.get(i) ?: 0L
            hrTimeLbls[i].text = fmtZoneTime(ms)
            val frac = if (total > 0) ms.toFloat() / total else 0f
            hrPctLbls[i].text = "${(100 * frac).roundToInt()}%"
        }
    }

    private fun metricValue(m: Metric): String {
        val r = ride?.recorder
        val rec = r != null && r.isRecording
        return when (m) {
            Metric.SPEED -> Units.fmtSpeed(ride?.curSpeedMps ?: 0f)
            Metric.AVG_SPEED -> if (rec) Units.fmtSpeed(r!!.avgSpeedMps) else "--"
            Metric.MAX_SPEED -> if ((r?.maxSpeedMps ?: 0f) > 0f) Units.fmtSpeed(r!!.maxSpeedMps) else "--"
            Metric.HR -> (ride?.lastHr ?: 0).let { if (it > 0) it.toString() else "--" }
            Metric.AVG_HR -> if ((r?.hrAvg ?: 0) > 0) r!!.hrAvg.toString() else "--"
            Metric.MAX_HR -> if ((r?.hrMax ?: 0) > 0) r!!.hrMax.toString() else "--"
            Metric.DISTANCE -> Units.fmtDist(r?.distanceM ?: 0.0)
            Metric.RIDE_TIME -> Units.fmtHms(r?.elapsedMs ?: 0L)
            Metric.ELEVATION -> Units.fmtFeet(ride?.curEleM ?: 0.0)
            Metric.ASCENT -> if ((r?.ascentM ?: 0.0) > 0.0) Units.fmtFeet(r!!.ascentM) else "--"
            Metric.GRADE -> ride?.let { String.format(Locale.US, "%.1f", it.curGrade) } ?: "--"
            Metric.CADENCE -> (ride?.curCadence ?: 0).let { if (it > 0) it.toString() else "--" }
            Metric.POWER -> (ride?.curPower ?: 0).let { if (it > 0) it.toString() else "--" }
            Metric.AVG_POWER -> if ((r?.powerAvg ?: 0) > 0) r!!.powerAvg.toString() else "--"
            Metric.MAX_POWER -> if ((r?.powerMax ?: 0) > 0) r!!.powerMax.toString() else "--"
            Metric.CLOCK -> clockFmt.format(Date())
            Metric.BATTERY -> batteryInfo().first.let { if (it in 0..100) it.toString() else "--" }
        }
    }

    private fun batteryInfo(): Pair<Int, Boolean> {
        val i = registerReceiver(null, IntentFilter("android.intent.action.BATTERY_CHANGED"))
            ?: return -1 to false
        val level = i.getIntExtra("level", -1)
        val scale = i.getIntExtra("scale", -1)
        val status = i.getIntExtra("status", -1)
        val charging = status == 2 || status == 5
        val pct = if (level >= 0 && scale > 0) level * 100 / scale else -1
        return pct to charging
    }

    private fun checkLowBattery() {
        val (pct, charging) = batteryInfo()
        if (pct in 0..100) {
            if (pct > 15) lowBattWarned = false
            if (pct in 1..10 && !lowBattWarned && !charging) {
                lowBattWarned = true
                try {
                    AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
                        .setTitle("Battery low — $pct%")
                        .setMessage("Consider wrapping up. Your ride will auto-save and the phone will power off at 2% to protect it.")
                        .setPositiveButton("OK", null)
                        .show()
                } catch (t: Throwable) {
                }
            }
        }
    }

    private fun uiTick() {
        updateRecUi()
        checkLowBattery()
        updateStatusBars()
        val r = ride?.recorder
        val rec = r != null && r.isRecording
        val dist = r?.distanceM ?: 0.0
        val elapsed = r?.elapsedMs ?: 0L
        mDist.text = Units.fmtDist(dist)
        mTime.text = Units.fmtHms(elapsed)
        val eleCur = ride?.curEleM ?: 0.0
        for (dv in dashViews) if (!dv.editing) dv.refresh()
        updateHrPage()
        elevView.setData(r?.eleSamples ?: emptyList())
        eEle.text = Units.fmtFeet(eleCur)
        eAscent.text = if ((r?.ascentM ?: 0.0) > 0.0) Units.fmtFeet(r!!.ascentM) else "--"
        eGrade.text = ride?.let { String.format(Locale.US, "%.1f", it.curGrade) } ?: "--"
        sDist.text = Units.fmtDist(dist)
        sTime.text = Units.fmtHms(elapsed)
        sAvgSpd.text = if (rec) Units.fmtSpeed(r!!.avgSpeedMps) else "--"
        sMaxSpd.text = if ((r?.maxSpeedMps ?: 0f) > 0f) Units.fmtSpeed(r!!.maxSpeedMps) else "--"
        sAvgHr.text = if ((r?.hrAvg ?: 0) > 0) r!!.hrAvg.toString() else "--"
        sMaxHr.text = if ((r?.hrMax ?: 0) > 0) r!!.hrMax.toString() else "--"
        sAscent.text = if ((r?.ascentM ?: 0.0) > 0.0) Units.fmtFeet(r!!.ascentM) else "--"
        sStarted.text = if ((r?.startMs ?: 0L) > 0) clockFmt.format(Date(r!!.startMs)) else "--:--"
        ui.postDelayed({ uiTick() }, 1000L)
    }

    private fun updateScreenPolicy() {
        val autoOff = Prefs.endurance(this) && (ride?.recorder?.isRecording == true)
        if (autoOff) {
            window.clearFlags(128)
            if (!screenOffArmed) {
                screenOffArmed = true
                ui.postDelayed(screenOffRunnable, screenOffMs)
            }
        } else {
            window.addFlags(128)
            screenOffArmed = false
            ui.removeCallbacks(screenOffRunnable)
        }
    }

    private fun sleepScreen() {
        Thread {
            try {
                Runtime.getRuntime().exec(arrayOf("su", "-c", "input keyevent 223")).waitFor()
            } catch (e: Exception) {
            }
        }.start()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        if (Prefs.endurance(this) && (ride?.recorder?.isRecording == true)) {
            screenOffArmed = true
            ui.removeCallbacks(screenOffRunnable)
            ui.postDelayed(screenOffRunnable, screenOffMs)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersive()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        bindService(Intent(this, RideService::class.java), conn, 1)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        AppState.onMap = true
        Voice.enabled = Prefs.voice(this)
        ride?.autoPauseEnabled = Prefs.autoPause(this)
        if (ActionBus.stopNav) {
            ActionBus.stopNav = false
            if (ride?.navigating == true) cancelNav()
        }
        if (Prefs.maxHr(this) != hrPageMaxHr) buildHrPage()
        if (Prefs.pageSignature(this) != pageSig) buildPager(false)
        applyMapPower()
        updateScreenPolicy()
        tryConsumePendingRoute()
        showChrome()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AppState.onMap = false
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        ui.removeCallbacks(screenOffRunnable)
        try {
            ride?.onUpdate = null
            ride?.onNavUpdate = null
            unbindService(conn)
        } catch (e: Exception) {
        }
        mapView.onStop()
        super.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Voice is owned by RideService now (so nav cues persist off the map); don't shut it down.
        mapView.onDestroy()
    }
}

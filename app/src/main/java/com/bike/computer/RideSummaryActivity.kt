package com.bike.computer

import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/** Post-ride detail screen: stats, route thumbnail, HR-zone breakdown, route comparison, menu. */
class RideSummaryActivity : Activity() {
    private lateinit var container: LinearLayout
    private var zoneMaxMs = 0L
    private val dateFmt = SimpleDateFormat("EEE d MMM · h:mm a", Locale.US)
    private val shortDate = SimpleDateFormat("d MMM yyyy", Locale.US)
    private val ridesDir = "/sdcard/BikeComputer/rides"

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_list)
        enterImmersive()
        container = findViewById(R.id.list_container)
        val startMs = intent.getLongExtra("startMs", 0L)
        val toWelcome = intent.getBooleanExtra("welcomeOnDone", false)
        val doneBtn = findViewById<View>(R.id.list_done)
        val back = findViewById<View>(R.id.list_back)
        if (toWelcome) {
            doneBtn.setOnClickListener { done(true) }
        } else {
            doneBtn.visibility = View.GONE
            back.visibility = View.VISIBLE
            back.setOnClickListener { finish() }
        }
        val ride = RideHistory.byStart(startMs)
        val title = findViewById<TextView>(R.id.list_title)
        if (ride == null) {
            title.text = "Ride"
            hint("Ride not found.")
            return
        }
        title.text = ride.name ?: ride.route ?: "Ride summary"
        findViewById<TextView>(R.id.list_menu).apply {
            visibility = View.VISIBLE
            setOnClickListener { showRideMenu(ride, gpxFile(ride)) }
        }
        text(dateFmt.format(Date(ride.startMs)))

        val loading = TextView(this)
        loading.text = "Loading…"
        loading.setTextColor(Color.parseColor("#FF8E8E93"))
        loading.textSize = 15f
        loading.setPadding(dp(4), dp(12), dp(4), dp(12))
        container.addView(loading)

        val f = gpxFile(ride)
        Thread {
            val gpxText = if (f != null) {
                (try {
                    f.readText()
                } catch (t: Throwable) {
                    null
                }) ?: ""
            } else {
                ""
            }
            val pts = try {
                GpxRoute.parse(gpxText)
            } catch (t: Throwable) {
                emptyList()
            }
            val maxHr = Prefs.maxHr(this)
            val zones = if (gpxText.isNotEmpty()) GpxSummary.zoneTimes(gpxText, maxHr) else LongArray(5)
            runOnUiThread {
                if (isFinishing) return@runOnUiThread
                container.removeView(loading)
                if (pts.size >= 2) routeMap(pts)
                stat("Distance", "${Units.fmtDist(ride.distanceM)} mi")
                stat("Moving time", fmtDur(ride.movingMs))
                stat("Avg speed", "${Units.fmtSpeed(ride.avgMps)} mph")
                stat("Fastest speed", "${Units.fmtSpeed(ride.maxMps)} mph")
                if (ride.hrMax > 0) stat("Highest HR", "${ride.hrMax} bpm")
                if (ride.hrAvg > 0) stat("Avg HR", "${ride.hrAvg} bpm")
                if (ride.ascentM > 0.0) stat("Ascent", "${Units.fmtFeet(ride.ascentM)} ft")
                if (ride.powerMax > 0) stat("Max power", "${ride.powerMax} W")
                if (ride.powerAvg > 0) stat("Avg power", "${ride.powerAvg} W")
                hrZonesSection(ride, zones, maxHr)
                ride.route?.let { showComparison(it, ride) }
            }
        }.start()
    }

    private fun gpxFile(ride: RideSummary): File? {
        val name = ride.gpx ?: return null
        val f = File(ridesDir, name)
        return if (f.exists()) f else null
    }

    private fun routeMap(pts: List<DoubleArray>) {
        val card = FrameLayout(this)
        card.setBackgroundResource(R.drawable.card_solid)
        val thumb = RouteThumb(this)
        thumb.setPoints(pts)
        card.addView(thumb, FrameLayout.LayoutParams(-1, -1))
        val lp = LinearLayout.LayoutParams(-1, dp(180))
        lp.setMargins(dp(2), dp(6), dp(2), dp(10))
        card.layoutParams = lp
        container.addView(card)
    }

    private fun showRideMenu(ride: RideSummary, f: File?) {
        val labels = ArrayList<String>()
        val acts = ArrayList<() -> Unit>()
        fun add(label: String, act: () -> Unit) {
            labels.add(label)
            acts.add(act)
        }
        add("Rename ride") { promptRename(ride) }
        if (f != null) {
            add("Save as a route") { promptSaveAsRoute(ride, f) }
            if (Prefs.driveConnected(this)) {
                if (ride.uploaded) {
                    add("Uploaded to Drive ✓") { toast("Already uploaded") }
                } else {
                    add("Upload to Drive") { uploadToDrive(ride, f) }
                }
            }
            add("Share GPX") { shareGpx(f) }
        }
        add("Delete ride") { confirmDelete(ride) }
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Ride options")
            .setItems(labels.toTypedArray()) { _, i -> acts[i]() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun promptRename(ride: RideSummary) {
        val input = EditText(this)
        val current = RideHistory.byStart(ride.startMs)?.name ?: ride.route ?: ""
        input.setText(current)
        input.hint = "Ride name"
        input.isSingleLine = true
        input.setSelectAllOnFocus(true)
        input.setTextColor(-1)
        input.setHintTextColor(Color.parseColor("#FF6E6E6E"))
        input.setPadding(dp(16), dp(12), dp(16), dp(12))
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Rename ride")
            .setMessage("Give this ride its own name. This is separate from any route it followed.")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val n = input.text.toString().trim().take(80).ifBlank { null }
                RideHistory.rename(ride.startMs, n)
                findViewById<TextView>(R.id.list_title).text = n ?: ride.route ?: "Ride summary"
                toast(if (n == null) "Name cleared" else "Renamed")
            }
            .setNeutralButton("Clear name") { _, _ ->
                RideHistory.rename(ride.startMs, null)
                findViewById<TextView>(R.id.list_title).text = ride.route ?: "Ride summary"
                toast("Name cleared")
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDelete(ride: RideSummary) {
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Delete ride?")
            .setMessage("Removes this ride from your history and deletes its GPX track. This can't be undone.")
            .setPositiveButton("Delete") { _, _ ->
                Thread {
                    RideHistory.delete(ride.startMs)
                    runOnUiThread {
                        toast("Ride deleted")
                        done(intent.getBooleanExtra("welcomeOnDone", false))
                    }
                }.start()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun uploadToDrive(ride: RideSummary, f: File) {
        toast("Uploading to Drive…")
        Thread {
            val outcome = runCatching { GoogleDriveClient.uploadGpx(this, f) }
            runOnUiThread {
                outcome
                    .onSuccess { msg ->
                        RideHistory.markUploaded(ride.startMs)
                        toast(msg)
                        recreate()
                    }
                    .onFailure { toast("Upload failed: ${it.message}") }
            }
        }.start()
    }

    private fun shareGpx(f: File) {
        try {
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", f)
            val i = Intent(Intent.ACTION_SEND)
            i.type = "application/gpx+xml"
            i.putExtra(Intent.EXTRA_STREAM, uri)
            i.addFlags(1)
            startActivity(Intent.createChooser(i, "Share ride"))
        } catch (e: Exception) {
            toast("Share failed: ${e.message}")
        }
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, 0).show()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersive()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        done(intent.getBooleanExtra("welcomeOnDone", false))
    }

    private fun done(toWelcome: Boolean) {
        if (toWelcome) {
            startActivity(Intent(this, WelcomeActivity::class.java).addFlags(603979776))
        }
        finish()
    }

    private fun showComparison(route: String, ride: RideSummary) {
        val list = RideHistory.forRoute(route)
        header(if (list.size <= 1) "First time on this route" else "vs your other ${list.size - 1} on “$route”")
        val best = list.first()
        val isPR = best.startMs == ride.startMs
        if (list.size > 1) {
            if (isPR) {
                stat("This ride", "${fmtDur(ride.movingMs)}  🏆 fastest!")
            } else {
                stat("This ride", fmtDur(ride.movingMs))
                stat("Best", "${fmtDur(best.movingMs)} · ${shortDate.format(Date(best.startMs))}")
                stat("Behind best", "+${fmtDur(ride.movingMs - best.movingMs)}")
            }
        }
        for ((i, a) in list.withIndex()) {
            attemptRow(i + 1, a, a.startMs == ride.startMs, i == 0)
        }
    }

    private fun hrZonesSection(ride: RideSummary, zones: LongArray, maxHr: Int) {
        val total = zones.sum()
        if (total <= 0) return
        zoneMaxMs = zones.max()
        header("Heart rate zones")
        text(
            if (ride.hrMax > maxHr) {
                "Based on max HR $maxHr bpm — but this ride peaked at ${ride.hrMax}. Raise it in Settings ▸ Sensors to shift time into the lower zones."
            } else {
                "Based on your max HR of $maxHr bpm (60 / 70 / 80 / 90 %). Change it in Settings ▸ Sensors."
            },
        )
        for (i in 4 downTo 0) {
            zoneBar(i, HrZone.entries[i], zones[i], total, maxHr)
        }
    }

    private fun zoneBar(idx: Int, z: HrZone, ms: Long, total: Long, maxHr: Int) {
        val pct = if (total > 0) ms.toFloat() / total else 0f
        val color = z.color.toInt()
        val dominant = total > 0 && ms == zoneMaxMs
        val range = when (idx) {
            0 -> "below ${maxHr * 60 / 100}"
            4 -> "${maxHr * 90 / 100}+"
            else -> "${z.lowerPct * maxHr / 100}–${(z.lowerPct + 10) * maxHr / 100}"
        }
        val medium = Typeface.create("sans-serif-medium", 0)
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = 16
        row.setPadding(dp(6), dp(9), dp(8), dp(9))
        if (dominant) row.background = roundRect(withAlpha(color, 31), 12)

        val badge = TextView(this)
        badge.text = (idx + 1).toString()
        badge.setTextColor(-1)
        badge.textSize = 15f
        badge.setTypeface(medium, 1)
        badge.gravity = 17
        badge.background = roundRect(color, 9)
        val badgeLp = LinearLayout.LayoutParams(dp(32), dp(32))
        badgeLp.rightMargin = dp(13)
        badge.layoutParams = badgeLp

        val col = LinearLayout(this)
        col.orientation = LinearLayout.VERTICAL
        col.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val lineA = LinearLayout(this)
        lineA.orientation = LinearLayout.HORIZONTAL
        lineA.gravity = 80
        val name = TextView(this)
        name.text = z.label
        name.setTextColor(-1)
        name.textSize = 15f
        name.typeface = medium
        val rng = TextView(this)
        rng.text = "  $range"
        rng.setTextColor(Color.parseColor("#FF7C7C82"))
        rng.textSize = 12f
        rng.setPadding(0, 0, 0, dp(1))
        val nameWrap = LinearLayout(this)
        nameWrap.orientation = LinearLayout.HORIZONTAL
        nameWrap.gravity = 80
        nameWrap.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        nameWrap.addView(name)
        nameWrap.addView(rng)
        val time = TextView(this)
        time.text = fmtDur(ms)
        time.setTextColor(-1)
        time.textSize = 16f
        time.setTypeface(medium, 1)
        val pctv = TextView(this)
        pctv.text = "  ${(100 * pct).roundToInt()}%"
        pctv.textSize = 13f
        pctv.setTextColor(Color.parseColor("#FF9A9AA0"))
        pctv.setPadding(0, 0, 0, dp(1))
        val valWrap = LinearLayout(this)
        valWrap.orientation = LinearLayout.HORIZONTAL
        valWrap.gravity = 80
        valWrap.addView(time)
        valWrap.addView(pctv)
        lineA.addView(nameWrap)
        lineA.addView(valWrap)

        val bar = LinearLayout(this)
        bar.orientation = LinearLayout.HORIZONTAL
        bar.background = roundRect(Color.parseColor("#17FFFFFF"), 5)
        val barLp = LinearLayout.LayoutParams(-1, dp(9))
        barLp.topMargin = dp(9)
        bar.layoutParams = barLp
        val fill = View(this)
        fill.background = roundRect(color, 5)
        fill.layoutParams = LinearLayout.LayoutParams(0, -1, 1.0e-4f)
        val rest = View(this)
        rest.layoutParams = LinearLayout.LayoutParams(0, -1, 1f)
        bar.addView(fill)
        bar.addView(rest)
        col.addView(lineA)
        col.addView(bar)
        row.addView(badge)
        row.addView(col)
        val rlp = LinearLayout.LayoutParams(-1, -2)
        rlp.topMargin = dp(3)
        row.layoutParams = rlp
        container.addView(row)
        animateBar(bar, fill, rest, pct.coerceAtLeast(0.006f), 4 - idx)
    }

    private fun animateBar(bar: View, fill: View, rest: View, target: Float, order: Int) {
        val flp = fill.layoutParams as LinearLayout.LayoutParams
        val rlp = rest.layoutParams as LinearLayout.LayoutParams
        val anim = ValueAnimator.ofFloat(0f, target)
        anim.duration = 620L
        anim.startDelay = order.toLong() * 75 + 140
        anim.interpolator = DecelerateInterpolator(1.4f)
        anim.addUpdateListener { a ->
            val f = a.animatedValue as Float
            flp.weight = f
            rlp.weight = (1f - f).coerceAtLeast(1.0e-4f)
            bar.requestLayout()
        }
        anim.start()
    }

    private fun roundRect(color: Int, radiusDp: Int): GradientDrawable {
        val d = GradientDrawable()
        d.setColor(color)
        d.cornerRadius = dp(radiusDp).toFloat()
        return d
    }

    private fun withAlpha(color: Int, alpha: Int): Int = (color and 0xFFFFFF) or (alpha shl 24)

    private fun promptSaveAsRoute(ride: RideSummary, f: File) {
        val input = EditText(this)
        input.setText(ride.route ?: "Ride ${shortDate.format(Date(ride.startMs))}")
        input.isSingleLine = true
        input.setSelectAllOnFocus(true)
        input.setTextColor(-1)
        input.setPadding(dp(16), dp(12), dp(16), dp(12))
        AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle("Save as route")
            .setMessage("Saves this ride's track as a route you can navigate again (from the home screen or Settings ▸ Routes).")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val name = Regex("[/\\\\:*?\"<>|]")
                    .replace(input.text.toString().trim(), "_").take(60)
                if (name.isEmpty()) {
                    toast("Name required")
                } else {
                    toast("Saving route…")
                    Thread {
                        val outcome = runCatching {
                            val dir = File("/sdcard/BikeComputer/routes")
                            dir.mkdirs()
                            f.copyTo(File(dir, "$name.gpx"), overwrite = true, bufferSize = 8192)
                        }
                        runOnUiThread {
                            outcome
                                .onSuccess { toast("Saved route “$name”") }
                                .onFailure { toast("Save failed: ${it.message}") }
                        }
                    }.start()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun dp(v: Int): Int =
        TypedValue.applyDimension(1, v.toFloat(), resources.displayMetrics).toInt()

    private fun text(s: String) {
        val t = TextView(this)
        t.text = s
        t.setTextColor(Color.parseColor("#FF9E9E9E"))
        t.textSize = 15f
        t.setPadding(dp(4), dp(2), dp(4), dp(10))
        container.addView(t)
    }

    private fun hint(s: String) = text(s)

    private fun header(title: String) {
        val t = TextView(this)
        t.text = title.uppercase()
        t.setTextColor(Color.parseColor("#FF8E8E93"))
        t.textSize = 13f
        t.typeface = Typeface.create("sans-serif-medium", 0)
        t.letterSpacing = 0.06f
        t.setPadding(dp(4), dp(22), dp(4), dp(8))
        container.addView(t)
    }

    private fun stat(label: String, value: String) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = 16
        row.setPadding(dp(4), dp(15), dp(4), dp(15))
        val l = TextView(this)
        l.text = label
        l.setTextColor(Color.parseColor("#FFB8B8BD"))
        l.textSize = 17f
        l.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val v = TextView(this)
        v.text = value
        v.setTextColor(-1)
        v.textSize = 20f
        v.typeface = Typeface.create("sans-serif-medium", 0)
        row.addView(l)
        row.addView(v)
        container.addView(row)
        divider()
    }

    private fun attemptRow(rank: Int, a: RideSummary, isThis: Boolean, isBest: Boolean) {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = 16
        row.setPadding(dp(4), dp(13), dp(4), dp(13))
        val left = TextView(this)
        left.text = "$rank.  ${shortDate.format(Date(a.startMs))}${if (isThis) "  ← this ride" else ""}"
        left.setTextColor(Color.parseColor(if (isThis) "#FF30D158" else "#FFB8B8BD"))
        left.textSize = 16f
        left.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val v = TextView(this)
        v.text = "${fmtDur(a.movingMs)}${if (isBest) "  🏆" else ""}"
        v.setTextColor(if (isBest) Color.parseColor("#FFFFD60A") else -1)
        v.textSize = 18f
        v.typeface = Typeface.create("sans-serif-medium", 0)
        row.addView(left)
        row.addView(v)
        container.addView(row)
        divider()
    }

    private fun divider() {
        val d = View(this)
        d.setBackgroundColor(Color.parseColor("#1FFFFFFF"))
        d.layoutParams = LinearLayout.LayoutParams(-1, 1)
        container.addView(d)
    }

    private fun fmtDur(ms: Long): String {
        val s = (ms / 1000).coerceAtLeast(0L)
        val h = s / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        return if (h > 0) {
            String.format(Locale.US, "%d:%02d:%02d", h, m, sec)
        } else {
            String.format(Locale.US, "%d:%02d", m, sec)
        }
    }
}

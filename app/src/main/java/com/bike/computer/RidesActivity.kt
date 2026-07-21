package com.bike.computer

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Ride history list: per-route best times and all recorded rides. */
class RidesActivity : Activity() {
    private lateinit var container: LinearLayout
    private val dateFmt = SimpleDateFormat("EEE d MMM · h:mm a", Locale.US)

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_list)
        enterImmersive()
        container = findViewById(R.id.list_container)
        findViewById<TextView>(R.id.list_title).text = "Rides"
        findViewById<TextView>(R.id.list_done).visibility = View.GONE
        findViewById<TextView>(R.id.list_back).apply {
            visibility = View.VISIBLE
            setOnClickListener { finish() }
        }
        build()
        Thread {
            if (RideHistory.reconcile()) {
                runOnUiThread { if (!isFinishing) build() }
            }
        }.start()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersive()
    }

    override fun onResume() {
        super.onResume()
        build()
    }

    private fun build() {
        container.removeAllViews()
        val all = RideHistory.all()
        if (all.isEmpty()) {
            hint("No rides yet — finish a ride to see it here.")
            return
        }
        val bests = RideHistory.routeBests()
        if (bests.isNotEmpty()) {
            header("Best times")
            for (route in bests.keys.sortedBy { it.lowercase() }) {
                val best = bests[route]!!
                val n = RideHistory.forRoute(route).size
                row(route, "$n ride${if (n == 1) "" else "s"}", fmtDur(best.movingMs), "🏆") {
                    openSummary(best.startMs)
                }
            }
        }
        header("All rides")
        for (r in all) {
            val title = r.name ?: r.route ?: "Free ride"
            val sub = dateFmt.format(Date(r.startMs))
            val right = "${Units.fmtDist(r.distanceM)} mi · ${fmtDur(r.movingMs)}"
            row(title, sub, right, null) { openSummary(r.startMs) }
        }
    }

    private fun openSummary(startMs: Long) {
        startActivity(Intent(this, RideSummaryActivity::class.java).putExtra("startMs", startMs))
    }

    private fun dp(v: Int): Int =
        TypedValue.applyDimension(1, v.toFloat(), resources.displayMetrics).toInt()

    private fun hint(s: String) {
        val t = TextView(this)
        t.text = s
        t.setTextColor(Color.parseColor("#FF9E9E9E"))
        t.textSize = 15f
        t.setPadding(dp(4), dp(8), dp(4), dp(8))
        container.addView(t)
    }

    private fun header(title: String) {
        val t = TextView(this)
        t.text = title.uppercase()
        t.setTextColor(Color.parseColor("#FF8E8E93"))
        t.textSize = 13f
        t.typeface = Typeface.create("sans-serif-medium", 0)
        t.letterSpacing = 0.06f
        t.setPadding(dp(4), dp(20), dp(4), dp(8))
        container.addView(t)
    }

    private fun row(title: String, sub: String, right: String, badge: String?, onClick: () -> Unit) {
        val c = LinearLayout(this)
        c.orientation = LinearLayout.HORIZONTAL
        c.gravity = 16
        c.setBackgroundResource(R.drawable.card_solid)
        c.setPadding(dp(16), dp(14), dp(16), dp(14))
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(dp(2), dp(4), dp(2), dp(4))
        c.layoutParams = lp
        c.isClickable = true
        c.isFocusable = true
        c.setOnClickListener { onClick() }

        val col = LinearLayout(this)
        col.orientation = LinearLayout.VERTICAL
        col.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val t = TextView(this)
        t.text = title
        t.setTextColor(-1)
        t.textSize = 17f
        t.maxLines = 1
        t.ellipsize = TextUtils.TruncateAt.END
        val subv = TextView(this)
        subv.text = sub
        subv.setTextColor(Color.parseColor("#FF8E8E93"))
        subv.textSize = 13f
        col.addView(t)
        col.addView(subv)

        val r = TextView(this)
        r.text = if (badge != null) "$badge $right" else right
        r.setTextColor(Color.parseColor("#FFE8E8EA"))
        r.textSize = 16f
        r.typeface = Typeface.create("sans-serif-medium", 0)
        r.setPadding(dp(10), 0, 0, 0)
        c.addView(col)
        c.addView(r)
        container.addView(c)
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

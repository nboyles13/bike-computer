package com.bike.computer

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

/** Destination search (Nominatim) → sets a pending navigation target and returns to the map. */
class DestinationSearchActivity : Activity() {
    private lateinit var container: LinearLayout
    private lateinit var results: LinearLayout
    private var ride: RideService? = null

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(n: ComponentName?, b: IBinder) {
            ride = (b as RideService.LocalBinder).service
        }

        override fun onServiceDisconnected(n: ComponentName?) {
            ride = null
        }
    }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setContentView(R.layout.activity_list)
        enterImmersive()
        container = findViewById(R.id.list_container)
        findViewById<TextView>(R.id.list_title).text = "Navigate to…"
        findViewById<View>(R.id.list_done).visibility = View.GONE
        findViewById<View>(R.id.list_back).apply {
            visibility = View.VISIBLE
            setOnClickListener { finish() }
        }

        val field = EditText(this)
        field.setBackgroundResource(R.drawable.card_solid)
        field.setPadding(dp(16), dp(14), dp(16), dp(14))
        field.setTextColor(-1)
        field.textSize = 17f
        field.isSingleLine = true
        field.hint = "Place or address"
        field.setHintTextColor(Color.parseColor("#FF6E6E6E"))
        field.inputType = 8193
        field.imeOptions = 3
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(dp(2), dp(4), dp(2), dp(4))
        field.layoutParams = lp
        field.setOnEditorActionListener { _, id, _ ->
            if (id != 3) {
                false
            } else {
                doSearch(field.text.toString())
                true
            }
        }
        container.addView(field)

        val btn = TextView(this)
        btn.text = "Search"
        btn.setTextColor(Color.parseColor("#FF4C8DFF"))
        btn.textSize = 17f
        btn.setBackgroundResource(R.drawable.card_ripple)
        btn.setPadding(dp(16), dp(14), dp(16), dp(14))
        btn.gravity = 17
        val lp2 = LinearLayout.LayoutParams(-1, -2)
        lp2.setMargins(dp(2), dp(4), dp(2), dp(8))
        btn.layoutParams = lp2
        btn.setOnClickListener { doSearch(field.text.toString()) }
        container.addView(btn)

        // Offline shortcut: search needs Wi-Fi (geocoding), but routing to a saved home does not.
        if (Prefs.hasHome(this)) {
            val homeBtn = TextView(this)
            homeBtn.text = "🏠  Navigate home (offline)"
            homeBtn.setTextColor(-1)
            homeBtn.textSize = 16f
            homeBtn.setBackgroundResource(R.drawable.card_ripple)
            homeBtn.setPadding(dp(16), dp(14), dp(16), dp(14))
            val lp3 = LinearLayout.LayoutParams(-1, -2)
            lp3.setMargins(dp(2), dp(4), dp(2), dp(8))
            homeBtn.layoutParams = lp3
            homeBtn.setOnClickListener {
                val h = Prefs.homeLoc(this)
                if (h != null) {
                    ActionBus.pendingDestination = doubleArrayOf(h[0], h[1])
                    Toast.makeText(this, "Routing home…", 0).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
            container.addView(homeBtn)
        }

        results = LinearLayout(this)
        results.orientation = LinearLayout.VERTICAL
        container.addView(results)
        field.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        bindService(Intent(this, RideService::class.java), conn, 1)
    }

    override fun onStop() {
        try {
            unbindService(conn)
        } catch (e: Exception) {
        }
        super.onStop()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) enterImmersive()
    }

    private fun doSearch(q: String) {
        if (q.trim().isEmpty()) return
        val imm = getSystemService("input_method") as InputMethodManager
        imm.hideSoftInputFromWindow(container.windowToken, 0)
        results.removeAllViews()
        hint("Searching…")
        val near = ride?.lastLocation?.let { doubleArrayOf(it.latitude, it.longitude) }
        Thread {
            val outcome = runCatching { Geocoder.search(q, near) }
            runOnUiThread {
                results.removeAllViews()
                outcome
                    .onSuccess { places ->
                        if (places.isEmpty()) {
                            hint("No places found for “$q”.")
                        } else {
                            for (p in places) resultRow(p)
                        }
                    }
                    .onFailure { hint("Search failed (need Wi-Fi): ${it.message}") }
            }
        }.start()
    }

    private fun resultRow(p: Geocoder.Place) {
        val c = LinearLayout(this)
        c.orientation = LinearLayout.HORIZONTAL
        c.gravity = 16
        c.setBackgroundResource(R.drawable.card_ripple)
        c.setPadding(dp(16), dp(14), dp(16), dp(14))
        val lp = LinearLayout.LayoutParams(-1, -2)
        lp.setMargins(dp(2), dp(4), dp(2), dp(4))
        c.layoutParams = lp
        c.isClickable = true
        c.setOnClickListener { navigateTo(p) }
        c.setOnLongClickListener {
            Prefs.setHomeLoc(this, p.lat, p.lon)
            Toast.makeText(this, "Saved as home — “${p.name.substringBefore(',')}”", 1).show()
            true
        }
        val t = TextView(this)
        t.text = p.name
        t.setTextColor(-1)
        t.textSize = 15f
        t.maxLines = 2
        t.ellipsize = TextUtils.TruncateAt.END
        t.layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        val go = TextView(this)
        go.text = "›"
        go.setTextColor(Color.parseColor("#FF6E6E6E"))
        go.textSize = 22f
        c.addView(t)
        c.addView(go)
        results.addView(c)
    }

    private fun navigateTo(p: Geocoder.Place) {
        ActionBus.pendingDestination = doubleArrayOf(p.lat, p.lon)
        Toast.makeText(this, "Routing to ${p.name.substringBefore(',')}…", 0).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun hint(s: String) {
        val t = TextView(this)
        t.text = s
        t.setTextColor(Color.parseColor("#FF9E9E9E"))
        t.textSize = 15f
        t.setPadding(dp(6), dp(8), dp(6), dp(8))
        results.addView(t)
    }

    private fun dp(v: Int): Int =
        TypedValue.applyDimension(1, v.toFloat(), resources.displayMetrics).toInt()
}

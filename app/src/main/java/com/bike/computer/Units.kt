package com.bike.computer

import java.util.Locale

object Units {
    fun mph(mps: Float): Float = 2.2369363f * mps

    fun miles(meters: Double): Double = meters / 1609.344

    fun feet(meters: Double): Double = 3.2808399 * meters

    fun fmtSpeed(mps: Float): String = String.format(Locale.US, "%.1f", mph(mps))

    fun fmtDist(meters: Double): String = String.format(Locale.US, "%.2f", miles(meters))

    fun fmtFeet(meters: Double): String =
        if (meters == 0.0) "--" else String.format(Locale.US, "%,.0f", feet(meters))

    fun fmtHms(ms: Long): String {
        val s = ms / 1000
        return String.format(Locale.US, "%d:%02d:%02d", s / 3600, (s % 3600) / 60, s % 60)
    }
}

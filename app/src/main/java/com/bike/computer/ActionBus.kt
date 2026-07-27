package com.bike.computer

object ActionBus {
    @Volatile
    var stopNav: Boolean = false

    @Volatile
    var rescanSensor: Boolean = false

    @Volatile
    var navigating: Boolean = false

    // Published by MainActivity so the home screen can show a live nav summary while MainActivity
    // is finished. navDest* are the destination coords (for a direct distance-to-finish estimate).
    @Volatile
    var navRouteName: String? = null

    @Volatile
    var navDestLat: Double = 0.0

    @Volatile
    var navDestLon: Double = 0.0

    @Volatile
    var navStartMs: Long = 0L

    @Volatile
    var pendingRoute: List<DoubleArray>? = null

    @Volatile
    var pendingRouteName: String? = null

    @Volatile
    var pendingDestination: DoubleArray? = null
}

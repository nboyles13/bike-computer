package com.bike.computer

object ActionBus {
    @Volatile
    var stopNav: Boolean = false

    @Volatile
    var rescanSensor: Boolean = false

    @Volatile
    var navigating: Boolean = false

    @Volatile
    var pendingRoute: List<DoubleArray>? = null

    @Volatile
    var pendingRouteName: String? = null

    @Volatile
    var pendingDestination: DoubleArray? = null
}

package com.bike.computer

/** Immutable summary of a completed ride. */
data class RideSummary(
    val startMs: Long,
    val route: String?,
    val distanceM: Double,
    val movingMs: Long,
    val avgMps: Float,
    val maxMps: Float,
    val hrAvg: Int,
    val hrMax: Int,
    val ascentM: Double,
    val powerAvg: Int,
    val powerMax: Int,
    val gpx: String?,
    val uploaded: Boolean = false,
    val name: String? = null,
)

package com.bike.computer

/** One resizable metric tile on a data dashboard page (grid position + span). */
data class DashTile(
    var metric: Metric,
    var col: Int,
    var row: Int,
    var w: Int,
    var h: Int,
)

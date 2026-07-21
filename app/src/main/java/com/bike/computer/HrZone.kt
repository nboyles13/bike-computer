package com.bike.computer

enum class HrZone(val label: String, val lowerPct: Int, val color: Long) {
    Z1("Warm Up", 50, 4287532691L),
    Z2("Easy", 60, 4283207167L),
    Z3("Aerobic", 70, 4281389400L),
    Z4("Threshold", 80, 4294942474L),
    Z5("Maximum", 90, 4294919482L),
}

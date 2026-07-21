package com.bike.computer

enum class DashBlock(
    val label: String,
    val unit: String,
    val primary: Metric,
    val secondary: List<Metric>,
) {
    SPEED("SPEED", "mph", Metric.SPEED, listOf(Metric.AVG_SPEED, Metric.MAX_SPEED)),
    HR("HEART RATE", "bpm", Metric.HR, listOf(Metric.AVG_HR, Metric.MAX_HR)),
    DISTANCE("DISTANCE", "mi", Metric.DISTANCE, emptyList()),
    RIDE_TIME("RIDE TIME", "", Metric.RIDE_TIME, emptyList()),
    ELEVATION("ELEVATION", "ft", Metric.ELEVATION, listOf(Metric.ASCENT)),
    INCLINE("INCLINE", "%", Metric.GRADE, emptyList()),
}

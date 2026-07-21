package com.bike.computer

enum class Metric(val label: String, val unit: String, val short: String) {
    SPEED("SPEED", "mph", "SPD"),
    AVG_SPEED("AVG SPEED", "mph", "AVG"),
    MAX_SPEED("MAX SPEED", "mph", "MAX"),
    HR("HEART RATE", "bpm", Pages.HR),
    AVG_HR("AVG HR", "bpm", "AVG"),
    MAX_HR("MAX HR", "bpm", "MAX"),
    DISTANCE("DISTANCE", "mi", "DIST"),
    RIDE_TIME("RIDE TIME", "", "TIME"),
    ELEVATION("ELEVATION", "ft", Pages.ELEV),
    ASCENT("ASCENT", "ft", "ASC"),
    GRADE("INCLINE", "%", "INC"),
    CADENCE("CADENCE", "rpm", "CAD"),
    POWER("POWER", "W", "PWR"),
    AVG_POWER("AVG POWER", "W", "AVG"),
    MAX_POWER("MAX POWER", "W", "MAX"),
    CLOCK("TIME OF DAY", "", "CLOCK"),
    BATTERY("BATTERY", "%", "BATT"),
}

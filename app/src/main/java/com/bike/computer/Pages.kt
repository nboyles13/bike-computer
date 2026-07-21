package com.bike.computer

object Pages {
    const val MAP = "MAP"
    const val HR = "HR"
    const val ELEV = "ELEV"
    const val SUMMARY = "SUMMARY"

    fun isData(key: String): Boolean = key.startsWith("DATA")

    fun dataId(key: String): Int = key.removePrefix("DATA").toIntOrNull() ?: 0

    fun key(id: Int): String = "DATA$id"

    // The original `when` branch bodies were lost in decompilation (jadx: "Failed to
    // restore switch over string"). The recovered/QA'd behavior is identity — the fixed
    // pages (MAP/HR/ELEV/SUMMARY) are displayed under their own key — so that is preserved.
    fun fixedTitle(key: String): String = key
}

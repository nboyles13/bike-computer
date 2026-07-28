package com.bike.computer

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/** All persisted app settings, backed by the `bike_prefs` SharedPreferences file. */
object Prefs {
    private const val P = "bike_prefs"
    private val DEFAULT: List<DashBlock> = DashBlock.entries.toList()
    private val DEFAULT_PAGE_KEYS: List<String> =
        listOf(Pages.SUMMARY, "DATA0", Pages.MAP, Pages.HR, Pages.ELEV)

    private fun sp(c: Context): SharedPreferences = c.getSharedPreferences(P, 0)

    private fun defaultTiles(): List<DashTile> = listOf(
        DashTile(Metric.SPEED, 0, 0, 2, 1),
        DashTile(Metric.HR, 2, 0, 2, 1),
        DashTile(Metric.DISTANCE, 0, 1, 2, 1),
        DashTile(Metric.RIDE_TIME, 2, 1, 2, 1),
        DashTile(Metric.ELEVATION, 0, 2, 2, 1),
        DashTile(Metric.GRADE, 2, 2, 2, 1),
    )

    fun dashTiles(c: Context, pageKey: String): List<DashTile> {
        var s = sp(c).getString("dash_tiles_$pageKey", null)
        if (s == null) {
            s = if (pageKey == "DATA0") sp(c).getString("dash_tiles", null) else null
            if (s == null) return defaultTiles()
        }
        val list = try {
            val arr = JSONArray(s)
            (0 until arr.length()).map {
                val o = arr.getJSONObject(it)
                DashTile(Metric.valueOf(o.getString("m")), o.getInt("c"), o.getInt("r"), o.getInt("w"), o.getInt("h"))
            }
        } catch (t: Throwable) {
            null
        }
        return if (list.isNullOrEmpty()) defaultTiles() else list
    }

    fun setDashTiles(c: Context, pageKey: String, tiles: List<DashTile>) {
        val arr = JSONArray()
        for (it in tiles) {
            try {
                arr.put(
                    JSONObject().put("m", it.metric.name).put("c", it.col)
                        .put("r", it.row).put("w", it.w).put("h", it.h),
                )
            } catch (e: JSONException) {
                throw RuntimeException(e)
            }
        }
        sp(c).edit().putString("dash_tiles_$pageKey", arr.toString()).apply()
    }

    fun autoPause(c: Context): Boolean = sp(c).getBoolean("auto_pause", true)
    fun setAutoPause(c: Context, v: Boolean) { sp(c).edit().putBoolean("auto_pause", v).apply() }

    fun voice(c: Context): Boolean = sp(c).getBoolean("voice", true)
    fun setVoice(c: Context, v: Boolean) { sp(c).edit().putBoolean("voice", v).apply() }

    fun maxHr(c: Context): Int = sp(c).getInt("max_hr", 185)
    fun setMaxHr(c: Context, v: Int) { sp(c).edit().putInt("max_hr", v.coerceIn(120, 220)).apply() }

    fun led(c: Context): Boolean = sp(c).getBoolean("led", true)
    fun setLed(c: Context, v: Boolean) { sp(c).edit().putBoolean("led", v).apply() }

    fun closeApps(c: Context): Boolean = sp(c).getBoolean("close_apps", true)
    fun setCloseApps(c: Context, v: Boolean) { sp(c).edit().putBoolean("close_apps", v).apply() }

    fun lowPowerMap(c: Context): Boolean = sp(c).getBoolean("low_power_map", false)
    fun setLowPowerMap(c: Context, v: Boolean) { sp(c).edit().putBoolean("low_power_map", v).apply() }

    fun wifiOffOnRide(c: Context): Boolean = sp(c).getBoolean("wifi_off_ride", true)
    fun setWifiOffOnRide(c: Context, v: Boolean) { sp(c).edit().putBoolean("wifi_off_ride", v).apply() }

    fun wifiDisabledByApp(c: Context): Boolean = sp(c).getBoolean("wifi_disabled_by_app", false)
    fun setWifiDisabledByApp(c: Context, v: Boolean) { sp(c).edit().putBoolean("wifi_disabled_by_app", v).apply() }

    fun endurance(c: Context): Boolean = sp(c).getBoolean("endurance", false)
    fun setEndurance(c: Context, v: Boolean) { sp(c).edit().putBoolean("endurance", v).apply() }

    /** Saved "home" location (lat/lon) for offline navigate-home; null if unset. */
    fun homeLoc(c: Context): DoubleArray? {
        val s = sp(c).getString("home_loc", null) ?: return null
        val parts = s.split(",")
        val lat = parts.getOrNull(0)?.toDoubleOrNull() ?: return null
        val lon = parts.getOrNull(1)?.toDoubleOrNull() ?: return null
        return doubleArrayOf(lat, lon)
    }

    fun hasHome(c: Context): Boolean = homeLoc(c) != null

    /** Human-readable label for the saved home (e.g. the address typed/searched); "" if none. */
    fun homeLabel(c: Context): String = sp(c).getString("home_label", "") ?: ""

    fun setHomeLoc(c: Context, lat: Double, lon: Double, label: String = "") {
        sp(c).edit().putString("home_loc", "$lat,$lon").putString("home_label", label).apply()
    }

    fun clearHomeLoc(c: Context) {
        sp(c).edit().remove("home_loc").remove("home_label").apply()
    }

    fun starredRoutes(c: Context): Set<String> {
        val s = sp(c).getString("starred_routes", null) ?: return emptySet()
        return try {
            val a = JSONArray(s)
            (0 until a.length()).map { a.getString(it) }.toSet()
        } catch (t: Throwable) {
            emptySet()
        }
    }

    fun isRouteStarred(c: Context, name: String): Boolean = starredRoutes(c).contains(name)

    fun setRouteStarred(c: Context, name: String, on: Boolean) {
        val set = starredRoutes(c).toMutableSet()
        if (on) set.add(name) else set.remove(name)
        val a = JSONArray()
        for (it in set) a.put(it)
        sp(c).edit().putString("starred_routes", a.toString()).apply()
    }

    fun pageOrder(c: Context): List<String> {
        val string = sp(c).getString("page_order", null) ?: return DEFAULT_PAGE_KEYS
        val saved = string.split(",").filter { it.isNotEmpty() }
        return if (saved.contains(Pages.MAP)) saved else saved + Pages.MAP
    }

    fun setPageOrder(c: Context, list: List<String>) {
        sp(c).edit().putString("page_order", list.joinToString(",")).apply()
    }

    fun pageEnabled(c: Context, key: String): Boolean {
        if (key == Pages.MAP) return true
        val string = sp(c).getString("pages_disabled", "") ?: ""
        return !string.split(",").contains(key)
    }

    fun setPageEnabled(c: Context, key: String, on: Boolean) {
        if (key == Pages.MAP) return
        val string = sp(c).getString("pages_disabled", "") ?: ""
        val dis = string.split(",").filter { it.isNotEmpty() }.toMutableSet()
        if (on) dis.remove(key) else dis.add(key)
        sp(c).edit().putString("pages_disabled", dis.joinToString(",")).apply()
    }

    fun enabledPagesInOrder(c: Context): List<String> = pageOrder(c).filter { pageEnabled(c, it) }

    fun pageSignature(c: Context): String = enabledPagesInOrder(c).joinToString(",")

    fun addDataPage(c: Context): String {
        val order = pageOrder(c).toMutableList()
        val id = generateSequence(0) { it + 1 }.first { !order.contains(Pages.key(it)) }
        val key = Pages.key(id)
        setDashTiles(c, key, defaultTiles())
        var idx = order.indexOf(Pages.MAP)
        if (idx < 0) idx = order.size
        order.add(idx, key)
        setPageOrder(c, order)
        return key
    }

    fun removeDataPage(c: Context, key: String) {
        setPageOrder(c, pageOrder(c).filter { it != key })
        setPageEnabled(c, key, true)
        sp(c).edit().remove("dash_tiles_$key").apply()
    }

    fun driveClientId(c: Context): String = sp(c).getString("drive_cid", "") ?: ""
    fun driveClientSecret(c: Context): String = sp(c).getString("drive_secret", "") ?: ""

    fun setDriveApp(c: Context, id: String, secret: String) {
        sp(c).edit().putString("drive_cid", id.trim()).putString("drive_secret", secret.trim()).apply()
    }

    fun driveAccessToken(c: Context): String = sp(c).getString("drive_access", "") ?: ""
    fun driveRefreshToken(c: Context): String = sp(c).getString("drive_refresh", "") ?: ""
    fun driveExpiresAt(c: Context): Long = sp(c).getLong("drive_expires", 0L)
    fun driveFolderId(c: Context): String = sp(c).getString("drive_folder", "") ?: ""
    fun setDriveFolderId(c: Context, id: String) { sp(c).edit().putString("drive_folder", id).apply() }

    fun driveRidesFolder(c: Context): String =
        sp(c).getString("drive_rides_folder", null)?.takeIf { it.isNotBlank() } ?: "BikeComputer"

    fun setDriveRidesFolder(c: Context, v: String) {
        val name = v.trim().ifEmpty { "BikeComputer" }
        if (name != driveRidesFolder(c)) sp(c).edit().remove("drive_folder").apply()
        sp(c).edit().putString("drive_rides_folder", name).apply()
    }

    fun driveRoutesFolder(c: Context): String =
        sp(c).getString("drive_routes_folder", null)?.takeIf { it.isNotBlank() } ?: "Harmin Routes"

    fun setDriveRoutesFolder(c: Context, v: String) {
        val name = v.trim().ifEmpty { "Harmin Routes" }
        sp(c).edit().putString("drive_routes_folder", name).apply()
    }

    fun driveSheetId(c: Context): String = sp(c).getString("drive_sheet", "") ?: ""
    fun setDriveSheetId(c: Context, id: String) { sp(c).edit().putString("drive_sheet", id).apply() }

    fun setDriveTokens(c: Context, access: String, refresh: String?, expiresAt: Long) {
        val e = sp(c).edit().putString("drive_access", access).putLong("drive_expires", expiresAt)
        if (!refresh.isNullOrEmpty()) e.putString("drive_refresh", refresh)
        e.apply()
    }

    fun driveConnected(c: Context): Boolean = driveRefreshToken(c).isNotEmpty()

    fun clearDriveTokens(c: Context) {
        sp(c).edit().remove("drive_access").remove("drive_refresh")
            .remove("drive_expires").remove("drive_folder").apply()
    }

    fun driveAutoUpload(c: Context): Boolean = sp(c).getBoolean("drive_auto", false)
    fun setDriveAutoUpload(c: Context, v: Boolean) { sp(c).edit().putBoolean("drive_auto", v).apply() }

    fun dashboard(c: Context): List<DashBlock> {
        val s = sp(c).getString("dash_blocks", null) ?: return DEFAULT
        val list = s.split(",").mapNotNull {
            try {
                DashBlock.valueOf(it)
            } catch (t: Throwable) {
                null
            }
        }
        return if (list.isEmpty()) DEFAULT else list
    }

    fun setDashboard(c: Context, list: List<DashBlock>) {
        sp(c).edit().putString("dash_blocks", list.joinToString(",") { it.name }).apply()
    }
}

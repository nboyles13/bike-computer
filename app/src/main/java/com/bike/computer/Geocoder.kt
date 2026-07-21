package com.bike.computer

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/** Forward geocoding via OpenStreetMap Nominatim. */
object Geocoder {
    private val http = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    data class Place(val name: String, val lat: Double, val lon: Double)

    @Throws(IOException::class)
    fun search(query: String, near: DoubleArray?): List<Place> {
        val q = URLEncoder.encode(query.trim(), "UTF-8")
        var url =
            "https://nominatim.openstreetmap.org/search?q=$q&format=jsonv2&limit=10&countrycodes=us"
        if (near != null) {
            url += "&viewbox=${near[1] - 1.0},${near[0] - 0.8},${near[1] + 1.0},${near[0] + 0.8}&bounded=0"
        }
        val req = Request.Builder().url(url)
            .header("User-Agent", "Harmin/1.0 (personal bike computer)")
            .build()
        http.newCall(req).execute().use { r ->
            val body = r.body?.string() ?: ""
            if (!r.isSuccessful) throw RuntimeException("HTTP ${r.code}")
            val arr = JSONArray(body)
            return (0 until arr.length()).map { i ->
                val o = arr.getJSONObject(i)
                Place(o.getString("display_name"), o.getDouble("lat"), o.getDouble("lon"))
            }
        }
    }
}

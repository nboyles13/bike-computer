package com.bike.computer

import android.database.sqlite.SQLiteDatabase
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.sqrt

/**
 * Looks up the nearest street name for a coordinate by reading the z14 vector tile
 * (transportation_name layer) straight out of the offline .mbtiles, hand-parsing the
 * Mapbox Vector Tile protobuf.
 */
object StreetNames {
    private var db: SQLiteDatabase? = null

    fun open(mbtilesPath: String) {
        if (db == null) {
            try {
                db = SQLiteDatabase.openDatabase(mbtilesPath, null, 1)
            } catch (e: Exception) {
                Log.e("BikeRoute", "mbtiles open failed", e)
            }
        }
    }

    fun nameAt(lat: Double, lon: Double): String {
        val d = db ?: return ""
        val xf = (lon + 180.0) / 360.0 * 16384
        val latRad = Math.toRadians(lat)
        val yf = (1.0 - ln(Math.tan(latRad) + 1.0 / Math.cos(latRad)) / Math.PI) / 2.0 * 16384
        val tx = floor(xf).toInt()
        val ty = floor(yf).toInt()
        val tmsY = (16384 - 1) - ty
        val blob: ByteArray? = try {
            d.rawQuery(
                "SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?",
                arrayOf("14", tx.toString(), tmsY.toString()),
            ).use { c -> if (c.moveToFirst()) c.getBlob(0) else null }
        } catch (e: Exception) {
            null
        }
        if (blob == null) return ""
        val data = gunzip(blob)
        return try {
            parseTile(data, xf - tx, yf - ty)
        } catch (e: Exception) {
            ""
        }
    }

    private fun gunzip(b: ByteArray): ByteArray {
        if (b.size < 2 || (b[0].toInt() and 0xFF) != 31) return b
        GZIPInputStream(ByteArrayInputStream(b)).use { g ->
            val o = ByteArrayOutputStream(b.size * 5)
            g.copyTo(o, 8192)
            return o.toByteArray()
        }
    }

    /** Minimal protobuf reader over a byte buffer. */
    private class PB(private val b: ByteArray) {
        var p = 0

        fun more(): Boolean = p < b.size

        fun varint(): Long {
            var r = 0L
            var s = 0
            while (true) {
                val x = b[p++].toInt() and 0xFF
                r = r or ((x and 127).toLong() shl s)
                if (x < 128) return r
                s += 7
            }
        }

        fun bytes(): ByteArray {
            val len = varint().toInt()
            val r = b.copyOfRange(p, p + len)
            p += len
            return r
        }

        fun str(): String = String(bytes(), Charsets.UTF_8)

        fun skip(wt: Int) {
            when (wt) {
                0 -> varint()
                1 -> p += 8
                2 -> p += varint().toInt()
                5 -> p += 4
                else -> {}
            }
        }
    }

    private fun zz(n: Int): Int = (n ushr 1) xor -(n and 1)

    private fun parseTile(data: ByteArray, fx: Double, fy: Double): String {
        val pb = PB(data)
        while (pb.more()) {
            val t = pb.varint().toInt()
            val field = t ushr 3
            val wt = t and 7
            if (field == 3 && wt == 2) {
                val name = parseLayer(pb.bytes(), fx, fy)
                if (name.isNotEmpty()) return name
            } else {
                pb.skip(wt)
            }
        }
        return ""
    }

    private fun parseLayer(bytes: ByteArray, fx: Double, fy: Double): String {
        val pb = PB(bytes)
        val keys = ArrayList<String>()
        val values = ArrayList<String>()
        val features = ArrayList<ByteArray>()
        var lname = ""
        var extent = 4096
        while (pb.more()) {
            val t = pb.varint().toInt()
            val f = t ushr 3
            val wt = t and 7
            when (f) {
                1 -> lname = pb.str()
                2 -> features.add(pb.bytes())
                3 -> keys.add(pb.str())
                4 -> values.add(parseValue(pb.bytes()))
                5 -> extent = pb.varint().toInt()
                else -> pb.skip(wt)
            }
        }
        if (lname != "transportation_name") return ""

        val nameKeys = listOf("name", "name:latin", "name:en")
            .map { keys.indexOf(it) }.filter { it >= 0 }.toHashSet()
        if (nameKeys.isEmpty()) {
            Log.i("BikeName", "no name key; keys=$keys")
            return ""
        }

        val qx = fx * extent
        val qy = fy * extent
        val maxD = extent * 0.1
        var bestD = Double.MAX_VALUE
        var best = ""
        for (fb in features) {
            val (nm, geom) = parseFeature(fb, values, nameKeys)
            if (nm.isNotEmpty()) {
                val dd = minDist(qx, qy, geom)
                if (dd < bestD) {
                    bestD = dd
                    best = nm
                }
            }
        }
        Log.i(
            "BikeName",
            "tn feats=${features.size} best='$best' bestD=${bestD.toInt()} maxD=${maxD.toInt()}",
        )
        return if (bestD <= maxD) best else ""
    }

    private fun parseValue(bytes: ByteArray): String {
        val pb = PB(bytes)
        while (pb.more()) {
            val t = pb.varint().toInt()
            val f = t ushr 3
            val wt = t and 7
            if (f == 1 && wt == 2) return pb.str()
            pb.skip(wt)
        }
        return ""
    }

    private fun parseFeature(
        bytes: ByteArray,
        values: List<String>,
        nameKeys: HashSet<Int>,
    ): Pair<String, IntArray> {
        val pb = PB(bytes)
        var name = ""
        var geom = IntArray(0)
        while (pb.more()) {
            val t = pb.varint().toInt()
            val f = t ushr 3
            val wt = t and 7
            if (f == 2 && wt == 2) {
                val tp = PB(pb.bytes())
                while (tp.more()) {
                    val k = tp.varint().toInt()
                    val v = tp.varint().toInt()
                    if (nameKeys.contains(k) && v < values.size && name.isEmpty()) {
                        name = values[v]
                    }
                }
            } else if (f == 4 && wt == 2) {
                val gp = PB(pb.bytes())
                val g = ArrayList<Int>()
                while (gp.more()) g.add(gp.varint().toInt())
                geom = g.toIntArray()
            } else {
                pb.skip(wt)
            }
        }
        return Pair(name, geom)
    }

    private fun minDist(qx: Double, qy: Double, g: IntArray): Double {
        var cx = 0
        var cy = 0
        var px = 0.0
        var py = 0.0
        var have = false
        var best = Double.MAX_VALUE
        var i = 0
        while (i < g.size) {
            val cmd = g[i]; i++
            val id = cmd and 7
            val cnt = cmd ushr 3
            when (id) {
                1 -> { // MoveTo
                    var k = 0
                    while (k < cnt) {
                        cx += zz(g[i]); cy += zz(g[i + 1]); i += 2
                        px = cx.toDouble(); py = cy.toDouble()
                        have = true
                        k++
                    }
                }
                2 -> { // LineTo
                    var k = 0
                    while (k < cnt) {
                        cx += zz(g[i]); cy += zz(g[i + 1]); i += 2
                        if (have) best = minOf(best, segDist(qx, qy, px, py, cx.toDouble(), cy.toDouble()))
                        px = cx.toDouble(); py = cy.toDouble()
                        k++
                    }
                }
                else -> {}
            }
        }
        return best
    }

    private fun segDist(px: Double, py: Double, ax: Double, ay: Double, bx: Double, by: Double): Double {
        val dx = bx - ax
        val dy = by - ay
        val len2 = dx * dx + dy * dy
        val t = if (len2 == 0.0) 0.0 else (((px - ax) * dx + (py - ay) * dy) / len2).coerceIn(0.0, 1.0)
        val cx = ax + t * dx
        val cy = ay + t * dy
        val ex = px - cx
        val ey = py - cy
        return sqrt(ex * ex + ey * ey)
    }
}

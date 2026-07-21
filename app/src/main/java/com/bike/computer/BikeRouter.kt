package com.bike.computer

import android.util.Log
import btools.router.HintAccess
import btools.router.NavHint
import btools.router.OsmNodeNamed
import btools.router.OsmTrack
import btools.router.RoutingContext
import btools.router.RoutingEngine
import java.io.File

/** Thin wrapper over the vendored BRouter engine that produces a [RouteResult]. */
object BikeRouter {
    fun route(
        segmentDir: String,
        profilePath: String,
        fromLat: Double,
        fromLon: Double,
        toLat: Double,
        toLon: Double,
    ): RouteResult? = route(
        segmentDir,
        profilePath,
        listOf(doubleArrayOf(fromLon, fromLat), doubleArrayOf(toLon, toLat)),
    )

    fun route(segmentDir: String, profilePath: String, waypoints: List<DoubleArray>): RouteResult? {
        if (waypoints.size < 2) return null
        return try {
            val rc = RoutingContext()
            rc.localFunction = profilePath
            rc.turnInstructionMode = 2
            rc.processUnusedTags = true
            val wps = ArrayList<OsmNodeNamed>()
            waypoints.forEachIndexed { i, p ->
                val name = when (i) {
                    0 -> "from"
                    waypoints.lastIndex -> "to"
                    else -> "via$i"
                }
                wps.add(waypoint(p[0], p[1], name))
            }
            val engine = RoutingEngine(null, null, File(segmentDir), wps, rc)
            engine.quite = true
            engine.doRun(120000L)
            val err = engine.errorMessage
            if (err != null) {
                Log.e("BikeRoute", "route failed: $err")
                return null
            }
            val track: OsmTrack = engine.foundTrack ?: return null
            val pts = track.nodes.map {
                doubleArrayOf(it.getILon() / 1000000.0 - 180.0, it.getILat() / 1000000.0 - 90.0)
            }
            val hints: List<NavHint> = HintAccess.read(track)
            Log.i(
                "BikeRoute",
                "route ok: ${pts.size} pts, ${track.distance} m, +${track.ascend} m, ${hints.size} turns",
            )
            for (s in hints) Log.i("BikeRoute", "  turn @${s.indexInTrack}: ${s.cmd}")
            RouteResult(pts, track.distance, track.ascend, hints)
        } catch (e: Throwable) {
            Log.e("BikeRoute", "route exception", e)
            null
        }
    }

    private fun waypoint(lon: Double, lat: Double, name: String): OsmNodeNamed {
        val n = OsmNodeNamed()
        n.ilon = ((180.0 + lon) * 1000000.0 + 0.5).toInt()
        n.ilat = ((90.0 + lat) * 1000000.0 + 0.5).toInt()
        n.name = name
        return n
    }
}

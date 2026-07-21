package com.bike.computer;

import android.util.Log;
import btools.router.HintAccess;
import btools.router.NavHint;
import btools.router.OsmNodeNamed;
import btools.router.OsmPathElement;
import btools.router.OsmTrack;
import btools.router.RoutingContext;
import btools.router.RoutingEngine;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

public final class BikeRouter {
    public static final BikeRouter INSTANCE = new BikeRouter();

    private BikeRouter() {
    }

    public final RouteResult route(String segmentDir, String profilePath, double fromLat, double fromLon, double toLat, double toLon) {
        Intrinsics.checkNotNullParameter(segmentDir, "segmentDir");
        Intrinsics.checkNotNullParameter(profilePath, "profilePath");
        return route(segmentDir, profilePath, CollectionsKt.listOf((double[][]) new double[][]{new double[]{fromLon, fromLat}, new double[]{toLon, toLat}}));
    }

    public final RouteResult route(String segmentDir, String profilePath, List<double[]> waypoints) {
        String str;
        String str2;
        Intrinsics.checkNotNullParameter(segmentDir, "segmentDir");
        Intrinsics.checkNotNullParameter(profilePath, "profilePath");
        Intrinsics.checkNotNullParameter(waypoints, "waypoints");
        if (waypoints.size() < 2) {
            return null;
        }
        try {
            RoutingContext rc = new RoutingContext();
            rc.localFunction = profilePath;
            rc.turnInstructionMode = 2;
            rc.processUnusedTags = true;
            ArrayList wps = new ArrayList();
            List<double[]> $this$forEachIndexed$iv = waypoints;
            int i = 0;
            for (Object item$iv : $this$forEachIndexed$iv) {
                int index$iv = i + 1;
                if (i < 0) {
                    CollectionsKt.throwIndexOverflow();
                }
                double[] p = (double[]) item$iv;
                BikeRouter bikeRouter = INSTANCE;
                double d = p[0];
                double d2 = p[1];
                if (i == 0) {
                    str2 = "from";
                } else if (i == CollectionsKt.getLastIndex(waypoints)) {
                    str2 = "to";
                } else {
                    str2 = "via" + i;
                }
                str = str2;
                wps.add(bikeRouter.waypoint(d, d2, str));
                i = index$iv;
            }
            RoutingEngine engine = new RoutingEngine(null, null, new File(segmentDir), wps, rc);
            engine.quite = true;
            engine.doRun(120000L);
            String it = engine.getErrorMessage();
            if (it != null) {
                Log.e("BikeRoute", "route failed: " + it);
                return null;
            }
            OsmTrack track = engine.getFoundTrack();
            if (track == null) {
                return null;
            }
            Iterable nodes = track.nodes;
            Intrinsics.checkNotNullExpressionValue(nodes, "nodes");
            Iterable $this$map$iv = nodes;
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
            for (Object item$iv$iv : $this$map$iv) {
                OsmPathElement it2 = (OsmPathElement) item$iv$iv;
                destination$iv$iv.add(new double[]{(((double) it2.getILon()) / 1000000.0d) - 180.0d, (((double) it2.getILat()) / 1000000.0d) - 90.0d});
                wps = wps;
                rc = rc;
            }
            List pts = (List) destination$iv$iv;
            List<NavHint> list = HintAccess.read(track);
            Log.i("BikeRoute", "route ok: " + pts.size() + " pts, " + track.distance + " m, +" + track.ascend + " m, " + list.size() + " turns");
            for (NavHint s : list) {
                Log.i("BikeRoute", "  turn @" + s.indexInTrack + ": " + s.cmd);
            }
            return new RouteResult(pts, track.distance, track.ascend, list);
        } catch (Throwable e) {
            Log.e("BikeRoute", "route exception", e);
            return null;
        }
    }

    private final OsmNodeNamed waypoint(double lon, double lat, String name) {
        OsmNodeNamed $this$waypoint_u24lambda_u243 = new OsmNodeNamed();
        $this$waypoint_u24lambda_u243.ilon = (int) (((180.0d + lon) * 1000000.0d) + 0.5d);
        $this$waypoint_u24lambda_u243.ilat = (int) (((90.0d + lat) * 1000000.0d) + 0.5d);
        $this$waypoint_u24lambda_u243.name = name;
        return $this$waypoint_u24lambda_u243;
    }
}

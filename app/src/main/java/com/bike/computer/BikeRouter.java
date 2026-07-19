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
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: BikeRouter.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0010\u0013\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J8\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\nJ&\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fJ \u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\n2\u0006\u0010\u0014\u001a\u00020\n2\u0006\u0010\u0015\u001a\u00020\u0007H\u0002¨\u0006\u0016"}, d2 = {"Lcom/bike/computer/BikeRouter;", "", "<init>", "()V", "route", "Lcom/bike/computer/RouteResult;", "segmentDir", "", "profilePath", "fromLat", "", "fromLon", "toLat", "toLon", "waypoints", "", "", "waypoint", "Lbtools/router/OsmNodeNamed;", "lon", "lat", "name", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
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

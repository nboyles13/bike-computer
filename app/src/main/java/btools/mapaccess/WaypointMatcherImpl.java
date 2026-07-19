package btools.mapaccess;

import btools.codec.WaypointMatcher;
import btools.util.CheapAngleMeter;
import btools.util.CheapRuler;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class WaypointMatcherImpl implements WaypointMatcher {
    private static final int MAX_POINTS = 5;
    private boolean anyUpdate;
    private Comparator<MatchedWaypoint> comparator;
    private OsmNodePairSet islandPairs;
    private int latLast;
    private int latStart;
    private int latTarget;
    private int lonLast;
    private int lonStart;
    private int lonTarget;
    private double maxDistance;
    private int maxWptIdx;
    boolean useAsStartWay = true;
    public boolean useDynamicRange;
    private List<MatchedWaypoint> waypoints;

    public WaypointMatcherImpl(List<MatchedWaypoint> waypoints, double maxDistance, OsmNodePairSet islandPairs) {
        this.useDynamicRange = false;
        this.waypoints = waypoints;
        this.islandPairs = islandPairs;
        MatchedWaypoint last = null;
        this.maxDistance = maxDistance;
        if (maxDistance < 0.0d) {
            this.maxDistance *= -1.0d;
            maxDistance *= -1.0d;
            this.useDynamicRange = true;
        }
        for (MatchedWaypoint mwp : waypoints) {
            mwp.radius = maxDistance;
            if (last != null && mwp.directionToNext == -1.0d) {
                last.directionToNext = CheapAngleMeter.getDirection(last.waypoint.ilon, last.waypoint.ilat, mwp.waypoint.ilon, mwp.waypoint.ilat);
            }
            last = mwp;
        }
        int lastidx = waypoints.size() - 2;
        if (lastidx < 0) {
            last.directionToNext = -1.0d;
        } else {
            last.directionToNext = CheapAngleMeter.getDirection(last.waypoint.ilon, last.waypoint.ilat, waypoints.get(lastidx).waypoint.ilon, waypoints.get(lastidx).waypoint.ilat);
        }
        this.maxWptIdx = waypoints.size() - 1;
        this.comparator = new Comparator<MatchedWaypoint>() { // from class: btools.mapaccess.WaypointMatcherImpl.1
            @Override // java.util.Comparator
            public int compare(MatchedWaypoint mw1, MatchedWaypoint mw2) {
                int cmpDist = Double.compare(mw1.radius, mw2.radius);
                return cmpDist != 0 ? cmpDist : Double.compare(mw1.directionDiff, mw2.directionDiff);
            }
        };
    }

    private void checkSegment(int lon1, int lat1, int lon2, int lat2) {
        double d;
        double dy;
        int i;
        double d2;
        double d3;
        double dlon2m;
        double dlat2m;
        int i2;
        int i3;
        double radius;
        WaypointMatcherImpl waypointMatcherImpl = this;
        int i4 = lon1;
        int i5 = lat1;
        int i6 = lon2;
        int i7 = lat2;
        double[] lonlat2m = CheapRuler.getLonLatToMeterScales((i5 + i7) >> 1);
        double dlon2m2 = lonlat2m[0];
        double dlat2m2 = lonlat2m[1];
        double dx = ((double) (i6 - i4)) * dlon2m2;
        double dy2 = ((double) (i7 - i5)) * dlat2m2;
        double d4 = Math.sqrt((dy2 * dy2) + (dx * dx));
        if (d4 == 0.0d) {
            return;
        }
        int i8 = 0;
        while (true) {
            double[] lonlat2m2 = lonlat2m;
            if (i8 < waypointMatcherImpl.waypoints.size()) {
                if (waypointMatcherImpl.useAsStartWay || i8 != 0) {
                    MatchedWaypoint mwp = waypointMatcherImpl.waypoints.get(i8);
                    d = d4;
                    dy = dy2;
                    if (mwp.wpttype == 3 && (i8 == 0 || waypointMatcherImpl.waypoints.get(i8 - 1).wpttype == 3)) {
                        if (mwp.crosspoint != null) {
                            i2 = i4;
                            i = i8;
                            dlon2m = dlon2m2;
                            dlat2m = dlat2m2;
                            i3 = i5;
                        } else {
                            mwp.crosspoint = new OsmNode();
                            mwp.crosspoint.ilon = mwp.waypoint.ilon;
                            mwp.crosspoint.ilat = mwp.waypoint.ilat;
                            mwp.hasUpdate = true;
                            waypointMatcherImpl.anyUpdate = true;
                            i2 = i4;
                            i = i8;
                            dlon2m = dlon2m2;
                            dlat2m = dlat2m2;
                            i3 = i5;
                        }
                    } else {
                        OsmNode wp = mwp.waypoint;
                        double x1 = ((double) (i4 - wp.ilon)) * dlon2m2;
                        i = i8;
                        int i9 = wp.ilat;
                        double y1 = ((double) (i5 - i9)) * dlat2m2;
                        double x2 = ((double) (i6 - wp.ilon)) * dlon2m2;
                        double y2 = ((double) (i7 - wp.ilat)) * dlat2m2;
                        double r12 = (x1 * x1) + (y1 * y1);
                        double r22 = (x2 * x2) + (y2 * y2);
                        if (r12 < r22) {
                            d2 = y1 * dx;
                            d3 = x1 * dy;
                        } else {
                            d2 = y2 * dx;
                            d3 = x2 * dy;
                        }
                        double radius2 = Math.abs(d2 - d3) / d;
                        dlon2m = dlon2m2;
                        dlat2m = dlat2m2;
                        if (radius2 <= mwp.radius) {
                            double s1 = (x1 * dx) + (y1 * dy);
                            double s2 = (x2 * dx) + (y2 * dy);
                            if (s1 < 0.0d) {
                                s1 = -s1;
                                s2 = -s2;
                            }
                            if (s2 > 0.0d) {
                                double radius3 = Math.sqrt(s1 < s2 ? r12 : r22);
                                if (radius3 <= mwp.radius) {
                                    radius = radius3;
                                } else {
                                    i2 = lon1;
                                    i3 = lat1;
                                    i6 = lon2;
                                    i7 = lat2;
                                }
                            } else {
                                radius = radius2;
                            }
                            mwp.radius = radius;
                            mwp.hasUpdate = true;
                            waypointMatcherImpl.anyUpdate = true;
                            if (mwp.crosspoint == null) {
                                mwp.crosspoint = new OsmNode();
                            }
                            if (s2 < 0.0d) {
                                double wayfraction = (-s2) / (d * d);
                                double xm = x2 - (wayfraction * dx);
                                double ym = y2 - (wayfraction * dy);
                                mwp.crosspoint.ilon = (int) ((xm / dlon2m) + ((double) wp.ilon));
                                mwp.crosspoint.ilat = (int) ((ym / dlat2m) + ((double) wp.ilat));
                                i2 = lon1;
                                i3 = lat1;
                                i6 = lon2;
                                i7 = lat2;
                            } else if (s1 > s2) {
                                i6 = lon2;
                                mwp.crosspoint.ilon = i6;
                                i7 = lat2;
                                mwp.crosspoint.ilat = i7;
                                i2 = lon1;
                                i3 = lat1;
                            } else {
                                i6 = lon2;
                                i7 = lat2;
                                OsmNode osmNode = mwp.crosspoint;
                                i2 = lon1;
                                osmNode.ilon = i2;
                                i3 = lat1;
                                mwp.crosspoint.ilat = i3;
                            }
                        } else {
                            i2 = lon1;
                            i3 = lat1;
                            i6 = lon2;
                            i7 = lat2;
                        }
                    }
                } else {
                    i2 = i4;
                    dlon2m = dlon2m2;
                    dlat2m = dlat2m2;
                    dy = dy2;
                    d = d4;
                    i = i8;
                    i3 = i5;
                }
                i4 = i2;
                i5 = i3;
                lonlat2m = lonlat2m2;
                d4 = d;
                dy2 = dy;
                dlat2m2 = dlat2m;
                dlon2m2 = dlon2m;
                i8 = i + 1;
                waypointMatcherImpl = this;
            } else {
                return;
            }
        }
    }

    @Override // btools.codec.WaypointMatcher
    public boolean start(int ilonStart, int ilatStart, int ilonTarget, int ilatTarget, boolean useAsStartWay) {
        if (this.islandPairs.size() > 0) {
            long n1 = (((long) ilonStart) << 32) | ((long) ilatStart);
            long n2 = (((long) ilonTarget) << 32) | ((long) ilatTarget);
            if (this.islandPairs.hasPair(n1, n2)) {
                return false;
            }
        }
        this.lonStart = ilonStart;
        this.lonLast = ilonStart;
        this.latStart = ilatStart;
        this.latLast = ilatStart;
        this.lonTarget = ilonTarget;
        this.latTarget = ilatTarget;
        this.anyUpdate = false;
        this.useAsStartWay = useAsStartWay;
        return true;
    }

    @Override // btools.codec.WaypointMatcher
    public void transferNode(int ilon, int ilat) {
        checkSegment(this.lonLast, this.latLast, ilon, ilat);
        this.lonLast = ilon;
        this.latLast = ilat;
    }

    @Override // btools.codec.WaypointMatcher
    public void end() {
        checkSegment(this.lonLast, this.latLast, this.lonTarget, this.latTarget);
        if (this.anyUpdate) {
            for (MatchedWaypoint mwp : this.waypoints) {
                if (mwp.hasUpdate) {
                    double angle = CheapAngleMeter.getDirection(this.lonStart, this.latStart, this.lonTarget, this.latTarget);
                    double diff = CheapAngleMeter.getDifferenceFromDirection(mwp.directionToNext, angle);
                    mwp.hasUpdate = false;
                    MatchedWaypoint mw = new MatchedWaypoint();
                    mw.waypoint = new OsmNode();
                    mw.waypoint.ilon = mwp.waypoint.ilon;
                    mw.waypoint.ilat = mwp.waypoint.ilat;
                    mw.crosspoint = new OsmNode();
                    mw.crosspoint.ilon = mwp.crosspoint.ilon;
                    mw.crosspoint.ilat = mwp.crosspoint.ilat;
                    mw.node1 = new OsmNode(this.lonStart, this.latStart);
                    mw.node2 = new OsmNode(this.lonTarget, this.latTarget);
                    mw.name = mwp.name + "_w_" + mwp.crosspoint.hashCode();
                    mw.radius = mwp.radius;
                    mw.directionDiff = diff;
                    mw.directionToNext = mwp.directionToNext;
                    updateWayList(mwp.wayNearest, mw);
                    double angle2 = CheapAngleMeter.getDirection(this.lonTarget, this.latTarget, this.lonStart, this.latStart);
                    double diff2 = CheapAngleMeter.getDifferenceFromDirection(mwp.directionToNext, angle2);
                    MatchedWaypoint mw2 = new MatchedWaypoint();
                    mw2.waypoint = new OsmNode();
                    mw2.waypoint.ilon = mwp.waypoint.ilon;
                    mw2.waypoint.ilat = mwp.waypoint.ilat;
                    mw2.crosspoint = new OsmNode();
                    mw2.crosspoint.ilon = mwp.crosspoint.ilon;
                    mw2.crosspoint.ilat = mwp.crosspoint.ilat;
                    mw2.node1 = new OsmNode(this.lonTarget, this.latTarget);
                    mw2.node2 = new OsmNode(this.lonStart, this.latStart);
                    mw2.name = mwp.name + "_w2_" + mwp.crosspoint.hashCode();
                    mw2.radius = mwp.radius;
                    mw2.directionDiff = diff2;
                    mw2.directionToNext = mwp.directionToNext;
                    updateWayList(mwp.wayNearest, mw2);
                    MatchedWaypoint way = mwp.wayNearest.get(0);
                    mwp.crosspoint.ilon = way.crosspoint.ilon;
                    mwp.crosspoint.ilat = way.crosspoint.ilat;
                    mwp.node1 = new OsmNode(way.node1.ilon, way.node1.ilat);
                    mwp.node2 = new OsmNode(way.node2.ilon, way.node2.ilat);
                    mwp.directionDiff = way.directionDiff;
                    mwp.radius = way.radius;
                }
            }
        }
    }

    @Override // btools.codec.WaypointMatcher
    public boolean hasMatch(int lon, int lat) {
        for (MatchedWaypoint mwp : this.waypoints) {
            if (mwp.waypoint.ilon == lon && mwp.waypoint.ilat == lat && (mwp.radius < this.maxDistance || mwp.crosspoint != null)) {
                return true;
            }
        }
        return false;
    }

    void updateWayList(List<MatchedWaypoint> ways, MatchedWaypoint mw) {
        ways.add(mw);
        Collections.sort(ways, this.comparator);
        if (ways.size() > 5) {
            ways.remove(5);
        }
    }
}

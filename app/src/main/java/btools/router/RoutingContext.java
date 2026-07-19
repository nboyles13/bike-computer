package btools.router;

import androidx.core.os.EnvironmentCompat;
import btools.expressions.BExpressionContext;
import btools.expressions.BExpressionContextNode;
import btools.expressions.BExpressionContextWay;
import btools.mapaccess.GeometryDecoder;
import btools.mapaccess.MatchedWaypoint;
import btools.mapaccess.OsmLink;
import btools.mapaccess.OsmNode;
import btools.util.CheapAngleMeter;
import btools.util.CheapRuler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class RoutingContext {
    public double S_C_x;
    public double additionalcostfactor;
    public AreaInfo ai;
    public boolean allowSamewayback;
    public boolean bikeMode;
    public double bikerPower;
    public double buffertime;
    public boolean buildBeelineOnRange;
    public boolean carMode;
    public double changetime;
    public boolean considerTurnRestrictions;
    public boolean continueStraight;
    public boolean correctMisplacedViaPoints;
    public double correctMisplacedViaPointsDistance;
    public double cost1speed;
    public double defaultC_r;
    public int elevationbufferreduce;
    public int elevationmaxbuffer;
    public int elevationpenaltybuffer;
    public BExpressionContextNode expctxNode;
    public BExpressionContextWay expctxWay;
    public OsmPrePath firstPrePath;
    public boolean footMode;
    public boolean forceSecondaryData;
    public boolean forceUseStartDirection;
    public boolean hasDirectRouting;
    public int ilatshortest;
    public int ilonshortest;
    public double inittimeadjustment;
    public boolean inverseDirection;
    public boolean inverseRouting;
    public Map<String, String> keyValues;
    public String localFunction;
    public double maxSpeed;
    public double pass1coefficient;
    public double pass2coefficient;
    public OsmPathModel pm;
    public List<OsmNodeNamed> poipoints;
    public boolean processUnusedTags;
    public long profileTimestamp;
    public String rawAreaPath;
    public String rawTrackPath;
    public Integer roundTripDirectionAdd;
    public Integer roundTripDistance;
    public Integer roundTripPoints;
    public boolean showSpeedProfile;
    public boolean showTime;
    public boolean showspeed;
    public Integer startDirection;
    public boolean startDirectionValid;
    public double starttimeoffset;
    public double totalMass;
    public boolean transitonly;
    public double turnInstructionCatchingRange;
    public int turnInstructionMode;
    public boolean turnInstructionRoundabouts;
    public boolean useDynamicDistance;
    public double waittimeadjustment;
    public double wayfraction;
    public double waypointCatchingRange;
    public int alternativeIdx = 0;
    public GeometryDecoder geometryDecoder = new GeometryDecoder();
    public int memoryclass = 64;
    public List<OsmNodeNamed> nogopoints = null;
    private List<OsmNodeNamed> nogopoints_all = null;
    private List<OsmNodeNamed> keepnogopoints = null;
    private OsmNodeNamed pendingEndpoint = null;
    public CheapAngleMeter anglemeter = new CheapAngleMeter();
    public double nogoCost = 0.0d;
    public boolean isEndpoint = false;
    public boolean shortestmatch = false;
    public String outputFormat = "gpx";
    public boolean exportWaypoints = false;
    public boolean exportCorrectedWaypoints = false;
    public boolean consider_crossing = false;
    public int crossing_Prio_H = 0;
    public int crossing_Prio_L = 0;
    public int cost_ToLeft_from_H_class1 = 0;
    public int cost_ToLeft_from_H_class2 = 0;
    public int cost_ToLeft_from_H_class3 = 0;
    public int cost_ToLeft_from_H_class4 = 0;
    public int cost_ToLeft_from_H_class5 = 0;
    public int cost_ToLeft_from_H_class6 = 0;
    public int cost_ToRight_from_H_class1 = 0;
    public int cost_ToRight_from_H_class2 = 0;
    public int cost_ToRight_from_H_class3 = 0;
    public int cost_ToRight_from_H_class4 = 0;
    public int cost_ToRight_from_H_class5 = 0;
    public int cost_ToRight_from_H_class6 = 0;

    public void setAlternativeIdx(int idx) {
        this.alternativeIdx = idx;
    }

    public int getAlternativeIdx(int min, int max) {
        return this.alternativeIdx < min ? min : this.alternativeIdx > max ? max : this.alternativeIdx;
    }

    public String getProfileName() {
        String name = this.localFunction == null ? EnvironmentCompat.MEDIA_UNKNOWN : this.localFunction;
        if (name.endsWith(".brf")) {
            name = name.substring(0, this.localFunction.length() - 4);
        }
        int idx = name.lastIndexOf(File.separatorChar);
        return idx >= 0 ? name.substring(idx + 1) : name;
    }

    private void setModel(String className) {
        if (className == null) {
            this.pm = new StdModel();
        } else {
            try {
                Class<?> clazz = Class.forName(className);
                this.pm = (OsmPathModel) clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create path-model: " + String.valueOf(e));
            }
        }
        initModel();
    }

    public void initModel() {
        this.pm.init(this.expctxWay, this.expctxNode, this.keyValues);
    }

    public long getKeyValueChecksum() {
        long s = 0;
        if (this.keyValues != null) {
            for (Map.Entry<String, String> e : this.keyValues.entrySet()) {
                s += (long) (e.getKey().hashCode() + e.getValue().hashCode());
            }
        }
        return s;
    }

    public void readGlobalConfig() {
        BExpressionContext expctxGlobal = this.expctxWay;
        setModel(expctxGlobal._modelClass);
        this.carMode = 0.0f != expctxGlobal.getVariableValue("validForCars", 0.0f);
        this.bikeMode = 0.0f != expctxGlobal.getVariableValue("validForBikes", 0.0f);
        this.footMode = 0.0f != expctxGlobal.getVariableValue("validForFoot", 0.0f);
        this.consider_crossing = 0.0f != expctxGlobal.getVariableValue("consider_crossing", 0.0f);
        this.crossing_Prio_H = (int) expctxGlobal.getVariableValue("crossing_Prio_H", 0.0f);
        this.crossing_Prio_L = (int) expctxGlobal.getVariableValue("crossing_Prio_L", 0.0f);
        this.cost_ToLeft_from_H_class1 = (int) expctxGlobal.getVariableValue("cost_ToLeft_from_H_class1", 0.0f);
        this.cost_ToLeft_from_H_class2 = (int) expctxGlobal.getVariableValue("cost_ToLeft_from_H_class2", 0.0f);
        this.cost_ToLeft_from_H_class3 = (int) expctxGlobal.getVariableValue("cost_ToLeft_from_H_class3", 0.0f);
        this.cost_ToLeft_from_H_class4 = (int) expctxGlobal.getVariableValue("cost_ToLeft_from_H_class4", 0.0f);
        this.cost_ToLeft_from_H_class5 = (int) expctxGlobal.getVariableValue("cost_ToLeft_from_H_class5", 0.0f);
        this.cost_ToLeft_from_H_class6 = (int) expctxGlobal.getVariableValue("cost_ToLeft_from_H_class6", 0.0f);
        this.cost_ToRight_from_H_class1 = (int) expctxGlobal.getVariableValue("cost_ToRight_from_H_class1", 0.0f);
        this.cost_ToRight_from_H_class2 = (int) expctxGlobal.getVariableValue("cost_ToRight_from_H_class2", 0.0f);
        this.cost_ToRight_from_H_class3 = (int) expctxGlobal.getVariableValue("cost_ToRight_from_H_class3", 0.0f);
        this.cost_ToRight_from_H_class4 = (int) expctxGlobal.getVariableValue("cost_ToRight_from_H_class4", 0.0f);
        this.cost_ToRight_from_H_class5 = (int) expctxGlobal.getVariableValue("cost_ToRight_from_H_class5", 0.0f);
        this.cost_ToRight_from_H_class6 = (int) expctxGlobal.getVariableValue("cost_ToRight_from_H_class6", 0.0f);
        this.waypointCatchingRange = expctxGlobal.getVariableValue("waypointCatchingRange", 250.0f);
        this.considerTurnRestrictions = 0.0f != expctxGlobal.getVariableValue("considerTurnRestrictions", this.footMode ? 0.0f : 1.0f);
        this.correctMisplacedViaPoints = 0.0f != expctxGlobal.getVariableValue("correctMisplacedViaPoints", 0.0f);
        this.correctMisplacedViaPointsDistance = expctxGlobal.getVariableValue("correctMisplacedViaPointsDistance", 400.0f);
        this.continueStraight = 0.0f != expctxGlobal.getVariableValue("continueStraight", 0.0f);
        this.processUnusedTags = 0.0f != expctxGlobal.getVariableValue("processUnusedTags", 0.0f);
        this.forceSecondaryData = 0.0f != expctxGlobal.getVariableValue("forceSecondaryData", 0.0f);
        this.pass1coefficient = expctxGlobal.getVariableValue("pass1coefficient", 1.5f);
        this.pass2coefficient = expctxGlobal.getVariableValue("pass2coefficient", 0.0f);
        this.elevationpenaltybuffer = (int) (expctxGlobal.getVariableValue("elevationpenaltybuffer", 5.0f) * 1000000.0f);
        this.elevationmaxbuffer = (int) (expctxGlobal.getVariableValue("elevationmaxbuffer", 10.0f) * 1000000.0f);
        this.elevationbufferreduce = (int) (expctxGlobal.getVariableValue("elevationbufferreduce", 0.0f) * 10000.0f);
        this.cost1speed = expctxGlobal.getVariableValue("cost1speed", 22.0f);
        this.additionalcostfactor = expctxGlobal.getVariableValue("additionalcostfactor", 1.5f);
        this.changetime = expctxGlobal.getVariableValue("changetime", 180.0f);
        this.buffertime = expctxGlobal.getVariableValue("buffertime", 120.0f);
        this.waittimeadjustment = expctxGlobal.getVariableValue("waittimeadjustment", 0.9f);
        this.inittimeadjustment = expctxGlobal.getVariableValue("inittimeadjustment", 0.2f);
        this.starttimeoffset = expctxGlobal.getVariableValue("starttimeoffset", 0.0f);
        this.transitonly = expctxGlobal.getVariableValue("transitonly", 0.0f) != 0.0f;
        this.showspeed = 0.0f != expctxGlobal.getVariableValue("showspeed", 0.0f);
        this.showSpeedProfile = 0.0f != expctxGlobal.getVariableValue("showSpeedProfile", 0.0f);
        this.inverseRouting = 0.0f != expctxGlobal.getVariableValue("inverseRouting", 0.0f);
        this.showTime = 0.0f != expctxGlobal.getVariableValue("showtime", 0.0f);
        int tiMode = (int) expctxGlobal.getVariableValue("turnInstructionMode", 0.0f);
        if (tiMode != 1) {
            this.turnInstructionMode = tiMode;
        }
        this.turnInstructionCatchingRange = expctxGlobal.getVariableValue("turnInstructionCatchingRange", 40.0f);
        this.turnInstructionRoundabouts = expctxGlobal.getVariableValue("turnInstructionRoundabouts", this.footMode ? 0.0f : 1.0f) != 0.0f;
        this.totalMass = expctxGlobal.getVariableValue("totalMass", 90.0f);
        if (this.footMode) {
            this.maxSpeed = ((double) expctxGlobal.getVariableValue("maxSpeed", 6.0f)) / 3.6d;
        } else {
            this.maxSpeed = ((double) expctxGlobal.getVariableValue("maxSpeed", 45.0f)) / 3.6d;
        }
        this.S_C_x = expctxGlobal.getVariableValue("S_C_x", 0.225f);
        this.defaultC_r = expctxGlobal.getVariableValue("C_r", 0.01f);
        this.bikerPower = expctxGlobal.getVariableValue("bikerPower", 100.0f);
        this.useDynamicDistance = expctxGlobal.getVariableValue("use_dynamic_range", 1.0f) == 1.0f;
        this.buildBeelineOnRange = expctxGlobal.getVariableValue("add_beeline", 0.0f) == 1.0f;
        boolean test = expctxGlobal.getVariableValue("check_start_way", 1.0f) == 1.0f;
        if (!test) {
            freeNoWays();
        }
    }

    public void freeNoWays() {
        BExpressionContext expctxGlobal = this.expctxWay;
        if (expctxGlobal != null) {
            expctxGlobal.freeNoWays();
        }
    }

    public static void prepareNogoPoints(List<OsmNodeNamed> nogos) {
        for (OsmNodeNamed nogo : nogos) {
            if (!(nogo instanceof OsmNogoPolygon)) {
                String s = nogo.name;
                int idx = s.indexOf(32);
                if (idx > 0) {
                    s = s.substring(0, idx);
                }
                int ir = 20;
                if (s.length() > 4) {
                    try {
                        ir = Integer.parseInt(s.substring(4));
                    } catch (Exception e) {
                    }
                }
                nogo.radius = ir;
            }
        }
    }

    public void restoreNogoList() {
        this.nogopoints = this.nogopoints_all;
    }

    public void cleanNogoList(List<OsmNode> waypoints) {
        this.nogopoints_all = this.nogopoints;
        if (this.nogopoints == null) {
            return;
        }
        List<OsmNodeNamed> nogos = new ArrayList<>();
        for (OsmNodeNamed nogo : this.nogopoints) {
            boolean goodGuy = true;
            for (OsmNode wp : waypoints) {
                if (wp.calcDistance(nogo) < nogo.radius) {
                    if (nogo instanceof OsmNogoPolygon) {
                        if (((OsmNogoPolygon) nogo).isClosed) {
                            if (((OsmNogoPolygon) nogo).isWithin(wp.ilon, wp.ilat)) {
                            }
                        } else if (((OsmNogoPolygon) nogo).isOnPolyline(wp.ilon, wp.ilat)) {
                        }
                    }
                    goodGuy = false;
                }
            }
            if (goodGuy) {
                nogos.add(nogo);
            }
        }
        this.nogopoints = nogos.isEmpty() ? null : nogos;
    }

    public void checkMatchedWaypointAgainstNogos(List<MatchedWaypoint> matchedWaypoints) {
        int theSize;
        int removed;
        boolean prevMwpIsInside;
        RoutingContext routingContext = this;
        List<MatchedWaypoint> list = matchedWaypoints;
        if (routingContext.nogopoints != null && (theSize = matchedWaypoints.size()) >= 2) {
            int removed2 = 0;
            List<MatchedWaypoint> newMatchedWaypoints = new ArrayList<>();
            MatchedWaypoint prevMwp = null;
            boolean prevMwpIsInside2 = false;
            int i = 0;
            while (i < theSize) {
                MatchedWaypoint mwp = list.get(i);
                boolean isInsideNogo = false;
                OsmNode wp = mwp.crosspoint;
                for (OsmNodeNamed nogo : routingContext.nogopoints) {
                    if (Double.isNaN(nogo.nogoWeight)) {
                        removed = removed2;
                        if (wp.calcDistance(nogo) < nogo.radius) {
                            if (nogo instanceof OsmNogoPolygon) {
                                if (((OsmNogoPolygon) nogo).isClosed) {
                                    if (((OsmNogoPolygon) nogo).isWithin(wp.ilon, wp.ilat)) {
                                    }
                                } else if (((OsmNogoPolygon) nogo).isOnPolyline(wp.ilon, wp.ilat)) {
                                }
                            }
                            isInsideNogo = true;
                            break;
                        }
                        continue;
                    } else {
                        removed = removed2;
                    }
                    removed2 = removed;
                }
                removed = removed2;
                if (isInsideNogo) {
                    boolean useAnyway = false;
                    if (prevMwp == null || mwp.wpttype == 3 || prevMwp.wpttype == 3 || prevMwpIsInside2) {
                        useAnyway = true;
                    } else if (i == theSize - 1) {
                        throw new IllegalArgumentException("last wpt in restricted area ");
                    }
                    if (useAnyway) {
                        prevMwpIsInside = true;
                        newMatchedWaypoints.add(mwp);
                        removed2 = removed;
                    } else {
                        removed2 = removed + 1;
                        prevMwpIsInside = false;
                    }
                    prevMwpIsInside2 = prevMwpIsInside;
                } else {
                    newMatchedWaypoints.add(mwp);
                    prevMwpIsInside2 = false;
                    removed2 = removed;
                }
                prevMwp = mwp;
                i++;
                routingContext = this;
                list = matchedWaypoints;
            }
            int removed3 = removed2;
            if (newMatchedWaypoints.size() < 2) {
                throw new IllegalArgumentException("a wpt in restricted area ");
            }
            if (removed3 > 0) {
                matchedWaypoints.clear();
                matchedWaypoints.addAll(newMatchedWaypoints);
            }
        }
    }

    public boolean allInOneNogo(List<OsmNode> waypoints) {
        if (this.nogopoints == null) {
            return false;
        }
        boolean allInTotal = false;
        for (OsmNodeNamed nogo : this.nogopoints) {
            boolean allIn = Double.isNaN(nogo.nogoWeight);
            for (OsmNode wp : waypoints) {
                int dist = wp.calcDistance(nogo);
                if (dist < nogo.radius) {
                    if (nogo instanceof OsmNogoPolygon) {
                        if (((OsmNogoPolygon) nogo).isClosed) {
                            if (((OsmNogoPolygon) nogo).isWithin(wp.ilon, wp.ilat)) {
                            }
                        } else if (((OsmNogoPolygon) nogo).isOnPolyline(wp.ilon, wp.ilat)) {
                        }
                    }
                }
                allIn = false;
            }
            allInTotal |= allIn;
        }
        return allInTotal;
    }

    public long[] getNogoChecksums() {
        long[] cs = new long[3];
        int n = this.nogopoints == null ? 0 : this.nogopoints.size();
        for (int i = 0; i < n; i++) {
            OsmNodeNamed nogo = this.nogopoints.get(i);
            cs[0] = cs[0] + ((long) nogo.ilon);
            cs[1] = cs[1] + ((long) nogo.ilat);
            cs[2] = cs[2] + ((long) (nogo.radius * 10.0d));
        }
        return cs;
    }

    public void setWaypoint(OsmNodeNamed wp, boolean endpoint) {
        setWaypoint(wp, null, endpoint);
    }

    public void setWaypoint(OsmNodeNamed wp, OsmNodeNamed pendingEndpoint, boolean endpoint) {
        this.keepnogopoints = this.nogopoints;
        this.nogopoints = new ArrayList();
        this.nogopoints.add(wp);
        if (this.keepnogopoints != null) {
            this.nogopoints.addAll(this.keepnogopoints);
        }
        this.isEndpoint = endpoint;
        this.pendingEndpoint = pendingEndpoint;
    }

    public boolean checkPendingEndpoint() {
        if (this.pendingEndpoint == null) {
            return false;
        }
        this.isEndpoint = true;
        this.nogopoints.set(0, this.pendingEndpoint);
        this.pendingEndpoint = null;
        return true;
    }

    public void unsetWaypoint() {
        this.nogopoints = this.keepnogopoints;
        this.pendingEndpoint = null;
        this.isEndpoint = false;
    }

    public int calcDistance(int lon1, int lat1, int lon2, int lat2) {
        double d;
        double d2;
        double d3;
        RoutingContext routingContext;
        int lon22;
        double s2;
        double radius;
        double d4;
        RoutingContext routingContext2 = this;
        double[] lonlat2m = CheapRuler.getLonLatToMeterScales((lat1 + lat2) >> 1);
        double dlon2m = lonlat2m[0];
        double dlat2m = lonlat2m[1];
        double dx = ((double) (lon2 - lon1)) * dlon2m;
        double dy = ((double) (lat2 - lat1)) * dlat2m;
        double d5 = Math.sqrt((dy * dy) + (dx * dx));
        routingContext2.shortestmatch = false;
        if (routingContext2.nogopoints == null || routingContext2.nogopoints.isEmpty() || d5 <= 0.0d) {
            d = d5;
        } else {
            double dy2 = dy;
            d = d5;
            int lat22 = lat2;
            int ngidx = 0;
            double dx2 = dx;
            int lon12 = lon1;
            int lat12 = lat1;
            int lon23 = lon2;
            while (ngidx < routingContext2.nogopoints.size()) {
                OsmNodeNamed nogo = routingContext2.nogopoints.get(ngidx);
                double x1 = ((double) (lon12 - nogo.ilon)) * dlon2m;
                double[] lonlat2m2 = lonlat2m;
                double y1 = ((double) (lat12 - nogo.ilat)) * dlat2m;
                int ngidx2 = ngidx;
                int ngidx3 = nogo.ilon;
                int lat13 = lat12;
                int lon24 = lon23;
                double x2 = ((double) (lon23 - ngidx3)) * dlon2m;
                double dlon2m2 = dlon2m;
                double y2 = ((double) (lat22 - nogo.ilat)) * dlat2m;
                double r12 = (x1 * x1) + (y1 * y1);
                double r22 = (x2 * x2) + (y2 * y2);
                if (r12 < r22) {
                    d2 = y1 * dx2;
                    d3 = x1 * dy2;
                } else {
                    d2 = y2 * dx2;
                    d3 = x2 * dy2;
                }
                double radius2 = Math.abs(d2 - d3) / d;
                double dlat2m2 = dlat2m;
                if (radius2 >= nogo.radius) {
                    routingContext = this;
                    lon22 = lon24;
                    s2 = 0.0d;
                    lat12 = lat13;
                } else {
                    double s1 = (x1 * dx2) + (y1 * dy2);
                    double s22 = (x2 * dx2) + (y2 * dy2);
                    if (s1 < 0.0d) {
                        s1 = -s1;
                        s22 = -s22;
                    }
                    if (s22 > 0.0d) {
                        double radius3 = Math.sqrt(s1 < s22 ? r12 : r22);
                        if (radius3 > nogo.radius) {
                            s2 = 0.0d;
                            routingContext = this;
                            lat12 = lat13;
                            lon22 = lon24;
                        } else {
                            radius = radius3;
                        }
                    } else {
                        radius = radius2;
                    }
                    if (nogo.isNogo) {
                        if (!(nogo instanceof OsmNogoPolygon)) {
                            if (Double.isNaN(nogo.nogoWeight)) {
                                routingContext = this;
                                routingContext.nogoCost = -1.0d;
                                lat12 = lat13;
                                lon22 = lon24;
                                s2 = 0.0d;
                            } else {
                                routingContext = this;
                                routingContext.nogoCost = nogo.distanceWithinRadius(lon12, lat13, lon24, lat22, d) * nogo.nogoWeight;
                                lat12 = lat13;
                                lon22 = lon24;
                                s2 = 0.0d;
                            }
                        } else {
                            routingContext = this;
                            lon22 = lon24;
                            if (!((OsmNogoPolygon) nogo).intersects(lon12, lat13, lon22, lat22)) {
                                lat12 = lat13;
                                s2 = 0.0d;
                            } else if (Double.isNaN(nogo.nogoWeight)) {
                                routingContext.nogoCost = -1.0d;
                                lat12 = lat13;
                                s2 = 0.0d;
                            } else if (((OsmNogoPolygon) nogo).isClosed) {
                                routingContext.nogoCost = ((OsmNogoPolygon) nogo).distanceWithinPolygon(lon12, lat13, lon22, lat22) * nogo.nogoWeight;
                                lat12 = lat13;
                                s2 = 0.0d;
                            } else {
                                routingContext.nogoCost = nogo.nogoWeight;
                                lat12 = lat13;
                                s2 = 0.0d;
                            }
                        }
                    } else {
                        double s12 = s1;
                        routingContext = this;
                        routingContext.shortestmatch = true;
                        nogo.radius = radius;
                        if (s22 < 0.0d) {
                            routingContext.wayfraction = (-s22) / (d * d);
                            double xm = x2 - (routingContext.wayfraction * dx2);
                            double ym = y2 - (routingContext.wayfraction * dy2);
                            double d6 = xm / dlon2m2;
                            double xm2 = nogo.ilon;
                            routingContext.ilonshortest = (int) (d6 + xm2);
                            double d7 = ym / dlat2m2;
                            double ym2 = nogo.ilat;
                            routingContext.ilatshortest = (int) (d7 + ym2);
                            lat12 = lat13;
                            d4 = 1.0d;
                        } else if (s12 > s22) {
                            routingContext.wayfraction = 0.0d;
                            routingContext.ilonshortest = lon24;
                            routingContext.ilatshortest = lat22;
                            lat12 = lat13;
                            d4 = 1.0d;
                        } else {
                            d4 = 1.0d;
                            routingContext.wayfraction = 1.0d;
                            routingContext.ilonshortest = lon12;
                            lat12 = lat13;
                            routingContext.ilatshortest = lat12;
                        }
                        if (routingContext.isEndpoint) {
                            double s23 = routingContext.wayfraction;
                            routingContext.wayfraction = d4 - s23;
                            lon23 = routingContext.ilonshortest;
                            lat22 = routingContext.ilatshortest;
                            s2 = 0.0d;
                        } else {
                            s2 = 0.0d;
                            routingContext.nogoCost = 0.0d;
                            lon12 = routingContext.ilonshortest;
                            lat12 = routingContext.ilatshortest;
                            lon23 = lon24;
                        }
                        dx2 = ((double) (lon23 - lon12)) * dlon2m2;
                        dy2 = ((double) (lat22 - lat12)) * dlat2m2;
                        d = Math.sqrt((dy2 * dy2) + (dx2 * dx2));
                        ngidx = ngidx2 + 1;
                        routingContext2 = routingContext;
                        lonlat2m = lonlat2m2;
                        dlon2m = dlon2m2;
                        dlat2m = dlat2m2;
                    }
                }
                lon23 = lon22;
                ngidx = ngidx2 + 1;
                routingContext2 = routingContext;
                lonlat2m = lonlat2m2;
                dlon2m = dlon2m2;
                dlat2m = dlat2m2;
            }
        }
        return (int) Math.max(1.0d, Math.round(d));
    }

    public OsmPrePath createPrePath(OsmPath origin, OsmLink link) {
        OsmPrePath p = this.pm.createPrePath();
        if (p != null) {
            p.init(origin, link, this);
        }
        return p;
    }

    public OsmPath createPath(OsmLink link) {
        OsmPath p = this.pm.createPath();
        p.init(link);
        return p;
    }

    public OsmPath createPath(OsmPath origin, OsmLink link, OsmTrack refTrack, boolean detailMode) {
        OsmPath p = this.pm.createPath();
        p.init(origin, link, refTrack, detailMode, this);
        return p;
    }
}

package btools.router;

import btools.mapaccess.MatchedWaypoint;
import btools.mapaccess.NodesCache;
import btools.mapaccess.OsmLink;
import btools.mapaccess.OsmLinkHolder;
import btools.mapaccess.OsmNode;
import btools.mapaccess.OsmNodePairSet;
import btools.mapaccess.OsmNodesMap;
import btools.mapaccess.OsmPos;
import btools.router.OsmTrack;
import btools.util.CheapAngleMeter;
import btools.util.CheapRuler;
import btools.util.CompactLongMap;
import btools.util.SortedHeap;
import btools.util.StackSampler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import okhttp3.internal.http2.Http2Connection;
import okhttp3.internal.ws.RealWebSocket;

/* JADX INFO: loaded from: classes.dex */
public class RoutingEngine extends Thread {
    public static final int BROUTER_ENGINEMODE_GETELEV = 2;
    public static final int BROUTER_ENGINEMODE_GETINFO = 3;
    public static final int BROUTER_ENGINEMODE_ROUNDTRIP = 4;
    public static final int BROUTER_ENGINEMODE_ROUTING = 0;
    public static final int BROUTER_ENGINEMODE_SEED = 1;
    private int MAXNODES_ISLAND_CHECK;
    private int MAX_DYNAMIC_RANGE;
    private int MAX_STEPS_CHECK;
    private int ROUNDTRIP_DEFAULT_DIRECTIONADD;
    public double airDistanceCostFactor;
    private int alternativeIndex;
    public SearchBoundary boundary;
    private boolean directWeaving;
    private int engineMode;
    protected String errorMessage;
    List<OsmNodeNamed> extraWaypoints;
    private Object[] extract;
    private boolean finished;
    private OsmTrack foundRawTrack;
    protected OsmTrack foundTrack;
    private OsmTrack guideTrack;
    private boolean infoLogEnabled;
    private Writer infoLogWriter;
    private OsmNodePairSet islandNodePairs;
    public double lastAirDistanceCostFactor;
    private int linksProcessed;
    private String logfileBase;
    private OsmPathElement matchPath;
    protected List<MatchedWaypoint> matchedWaypoints;
    private long maxRunningTime;
    private int nodeLimit;
    private NodesCache nodesCache;
    private SortedHeap<OsmPath> openSet;
    private String outfile;
    private String outfileBase;
    protected String outputMessage;
    public boolean quite;
    protected RoutingContext routingContext;
    protected File segmentDir;
    private StackSampler stackSampler;
    private long startTime;
    private volatile boolean terminated;
    private boolean useNodePoints;
    protected List<OsmNodeNamed> waypoints;

    public RoutingEngine(String outfileBase, String logfileBase, File segmentDir, List<OsmNodeNamed> waypoints, RoutingContext rc) {
        this(outfileBase, logfileBase, segmentDir, waypoints, rc, 0);
    }

    public RoutingEngine(String outfileBase, String logfileBase, File segmentDir, List<OsmNodeNamed> waypoints, RoutingContext rc, int engineMode) {
        this.openSet = new SortedHeap<>();
        this.finished = false;
        this.waypoints = null;
        this.extraWaypoints = null;
        this.linksProcessed = 0;
        this.MAXNODES_ISLAND_CHECK = 500;
        this.islandNodePairs = new OsmNodePairSet(this.MAXNODES_ISLAND_CHECK);
        this.useNodePoints = false;
        this.engineMode = 0;
        this.MAX_STEPS_CHECK = 500;
        this.ROUNDTRIP_DEFAULT_DIRECTIONADD = 45;
        this.MAX_DYNAMIC_RANGE = 60000;
        this.foundTrack = new OsmTrack();
        this.foundRawTrack = null;
        this.alternativeIndex = 0;
        this.outputMessage = null;
        this.errorMessage = null;
        this.quite = false;
        this.directWeaving = !Boolean.getBoolean("disableDirectWeaving");
        this.segmentDir = segmentDir;
        this.outfileBase = outfileBase;
        this.logfileBase = logfileBase;
        this.waypoints = waypoints;
        this.infoLogEnabled = outfileBase != null;
        this.routingContext = rc;
        this.engineMode = engineMode;
        File baseFolder = new File(this.routingContext.localFunction).getParentFile();
        File baseFolder2 = baseFolder != null ? baseFolder.getParentFile() : null;
        if (baseFolder2 != null) {
            try {
                File debugLog = new File(baseFolder2, "debug.txt");
                if (debugLog.exists()) {
                    this.infoLogWriter = new FileWriter(debugLog, true);
                    logInfo("********** start request at ");
                    logInfo("********** " + String.valueOf(new Date()));
                }
                File stackLog = new File(baseFolder2, "stacks.txt");
                if (stackLog.exists()) {
                    this.stackSampler = new StackSampler(stackLog, 1000);
                    this.stackSampler.start();
                    logInfo("********** started stacksampling");
                }
            } catch (IOException ioe) {
                throw new RuntimeException("cannot open debug-log:" + String.valueOf(ioe));
            }
        }
        boolean cachedProfile = ProfileCache.parseProfile(rc);
        if (hasInfo()) {
            logInfo("parsed profile " + rc.localFunction + " cached=" + cachedProfile);
        }
    }

    private boolean hasInfo() {
        return this.infoLogEnabled || this.infoLogWriter != null;
    }

    private void logInfo(String s) {
        if (this.infoLogEnabled) {
            System.out.println(s);
        }
        if (this.infoLogWriter != null) {
            try {
                this.infoLogWriter.write(s);
                this.infoLogWriter.write(10);
                this.infoLogWriter.flush();
            } catch (IOException e) {
                this.infoLogWriter = null;
            }
        }
    }

    private void logThrowable(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        logInfo(sw.toString());
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() throws Throwable {
        doRun(0L);
    }

    public void doRun(long maxRunningTime) throws Throwable {
        switch (this.engineMode) {
            case 0:
                if (this.waypoints.size() < 2) {
                    throw new IllegalArgumentException("we need two lat/lon points at least!");
                }
                doRouting(maxRunningTime);
                return;
            case 1:
                throw new IllegalArgumentException("not a valid engine mode");
            case 2:
            case 3:
                if (this.waypoints.size() < 1) {
                    throw new IllegalArgumentException("we need one lat/lon point at least!");
                }
                doGetInfo();
                return;
            case 4:
                if (this.waypoints.size() < 1) {
                    throw new IllegalArgumentException("we need one lat/lon point at least!");
                }
                doRoundTrip();
                return;
            default:
                throw new IllegalArgumentException("not a valid engine mode");
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:177:0x04dc  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x0507  */
    /* JADX WARN: Removed duplicated region for block: B:205:0x0565  */
    /* JADX WARN: Removed duplicated region for block: B:210:0x0590  */
    /* JADX WARN: Removed duplicated region for block: B:233:0x05eb  */
    /* JADX WARN: Removed duplicated region for block: B:238:0x0616  */
    /* JADX WARN: Removed duplicated region for block: B:259:0x066d  */
    /* JADX WARN: Removed duplicated region for block: B:264:0x0698  */
    /* JADX WARN: Removed duplicated region for block: B:287:0x05d6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:289:0x06de A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:293:0x065c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:295:0x05c8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:298:0x06d0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:302:0x064e A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:304:0x054d A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:306:0x053f A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:316:0x029b A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:328:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:329:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:330:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:331:? A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x027d  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x027e  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x027f  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0280 A[Catch: all -> 0x03a7, Error -> 0x03ad, Exception -> 0x03b3, IllegalArgumentException -> 0x03b9, TryCatch #20 {Error -> 0x03ad, IllegalArgumentException -> 0x03b9, Exception -> 0x03b3, all -> 0x03a7, blocks: (B:52:0x0258, B:65:0x027a, B:72:0x0290, B:75:0x029b, B:76:0x02d3, B:94:0x0303, B:95:0x0306, B:99:0x0334, B:101:0x0338, B:102:0x034d, B:133:0x03d9, B:96:0x030a, B:97:0x0318, B:98:0x0326, B:78:0x02d7, B:81:0x02df, B:84:0x02e7, B:87:0x02ef, B:90:0x02f9, B:69:0x0280, B:55:0x0260, B:58:0x0268, B:61:0x0270, B:117:0x0385, B:129:0x03c3), top: B:309:0x0258 }] */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0290 A[Catch: all -> 0x03a7, Error -> 0x03ad, Exception -> 0x03b3, IllegalArgumentException -> 0x03b9, TryCatch #20 {Error -> 0x03ad, IllegalArgumentException -> 0x03b9, Exception -> 0x03b3, all -> 0x03a7, blocks: (B:52:0x0258, B:65:0x027a, B:72:0x0290, B:75:0x029b, B:76:0x02d3, B:94:0x0303, B:95:0x0306, B:99:0x0334, B:101:0x0338, B:102:0x034d, B:133:0x03d9, B:96:0x030a, B:97:0x0318, B:98:0x0326, B:78:0x02d7, B:81:0x02df, B:84:0x02e7, B:87:0x02ef, B:90:0x02f9, B:69:0x0280, B:55:0x0260, B:58:0x0268, B:61:0x0270, B:117:0x0385, B:129:0x03c3), top: B:309:0x0258 }] */
    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1092)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:390)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:23)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:370)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:85)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:33)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:70)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1117)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1117)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at java.base/java.util.Collections$UnmodifiableCollection.forEach(Collections.java:1117)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.lambda$traverseInternal$0(DepthRegionTraversal.java:68)
        	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:68)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:19)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:23)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void doRouting(long r25) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 1834
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: btools.router.RoutingEngine.doRouting(long):void");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:34:0x01d5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void doGetInfo() {
        OsmNodeNamed n;
        try {
            this.startTime = System.currentTimeMillis();
            this.routingContext.freeNoWays();
            MatchedWaypoint wpt1 = new MatchedWaypoint();
            wpt1.waypoint = this.waypoints.get(0);
            wpt1.name = "wpt_info";
            List<MatchedWaypoint> listOne = new ArrayList<>();
            listOne.add(wpt1);
            matchWaypointsToNodes(listOne);
            resetCache(true);
            this.nodesCache.nodesMap.cleanupMode = 0;
            OsmNode start1 = this.nodesCache.getGraphNode(listOne.get(0).node1);
            this.nodesCache.obtainNonHollowNode(start1);
            this.guideTrack = new OsmTrack();
            this.guideTrack.addNode(OsmPathElement.create(wpt1.node2.ilon, wpt1.node2.ilat, (short) 0, null));
            this.guideTrack.addNode(OsmPathElement.create(wpt1.node1.ilon, wpt1.node1.ilat, (short) 0, null));
            this.matchedWaypoints = new ArrayList();
            MatchedWaypoint wp1 = new MatchedWaypoint();
            wp1.crosspoint = new OsmNode(wpt1.node1.ilon, wpt1.node1.ilat);
            wp1.node1 = new OsmNode(wpt1.node1.ilon, wpt1.node1.ilat);
            wp1.node2 = new OsmNode(wpt1.node2.ilon, wpt1.node2.ilat);
            this.matchedWaypoints.add(wp1);
            MatchedWaypoint wp2 = new MatchedWaypoint();
            wp2.crosspoint = new OsmNode(wpt1.node2.ilon, wpt1.node2.ilat);
            wp2.node1 = new OsmNode(wpt1.node1.ilon, wpt1.node1.ilat);
            wp2.node2 = new OsmNode(wpt1.node2.ilon, wpt1.node2.ilat);
            this.matchedWaypoints.add(wp2);
            OsmTrack t = findTrack("getinfo", wp1, wp2, null, null, false);
            if (t == null) {
                if (this.errorMessage == null) {
                    this.errorMessage = "no track found";
                }
            } else {
                t.messageList = new ArrayList();
                t.matchedWaypoints = this.matchedWaypoints;
                t.name = this.outfileBase == null ? "getinfo" : this.outfileBase;
                int mindist = 99999;
                int minIdx = -1;
                for (int i = 0; i < t.nodes.size(); i++) {
                    OsmPathElement ope = t.nodes.get(i);
                    int dist = ope.calcDistance(listOne.get(0).crosspoint);
                    if (mindist > dist) {
                        mindist = dist;
                        minIdx = i;
                    }
                }
                int otherIdx = minIdx == t.nodes.size() - 1 ? minIdx - 1 : minIdx + 1;
                int otherdist = t.nodes.get(otherIdx).calcDistance(listOne.get(0).crosspoint);
                int minSElev = t.nodes.get(minIdx).getSElev();
                int otherSElev = t.nodes.get(otherIdx).getSElev();
                int diffSElev = otherSElev - minSElev;
                double diff = (((double) mindist) / ((double) (mindist + otherdist))) * ((double) diffSElev);
                n = new OsmNodeNamed(listOne.get(0).crosspoint);
                n.name = wpt1.name;
                n.selev = minIdx != -1 ? (short) (((int) diff) + minSElev) : Short.MIN_VALUE;
                if (this.engineMode == 3) {
                    n.nodeDescription = (start1 == null || start1.firstlink == null) ? null : start1.firstlink.descriptionBitmap;
                    t.pois.add(n);
                    t.matchedWaypoints = listOne;
                    t.exportWaypoints = this.routingContext.exportWaypoints;
                }
                switch (this.routingContext.outputFormat) {
                    case "gpx":
                        if (this.engineMode == 2) {
                            this.outputMessage = new FormatGpx(this.routingContext).formatAsWaypoint(n);
                            break;
                        } else {
                            this.outputMessage = new FormatGpx(this.routingContext).format(t);
                            break;
                        }
                        break;
                    case "geojson":
                    case "json":
                        if (this.engineMode == 2) {
                            this.outputMessage = new FormatJson(this.routingContext).formatAsWaypoint(n);
                            break;
                        } else {
                            this.outputMessage = new FormatJson(this.routingContext).format(t);
                            break;
                        }
                        break;
                    case "kml":
                    case "csv":
                    default:
                        this.outputMessage = null;
                        break;
                }
                if (this.outfileBase == null) {
                    if (!this.quite && this.outputMessage != null) {
                        System.out.println(this.outputMessage);
                    }
                } else {
                    String filename = this.outfileBase + "." + this.routingContext.outputFormat;
                    new File(filename);
                    FileWriter fw = new FileWriter(filename);
                    fw.write(this.outputMessage);
                    fw.close();
                    this.outputMessage = null;
                }
            }
            long endTime = System.currentTimeMillis();
            logInfo("execution time = " + ((endTime - this.startTime) / 1000.0d) + " seconds");
        } catch (Exception e) {
            e.getStackTrace();
            logException(e);
        }
    }

    public void doRoundTrip() throws Throwable {
        try {
            long startTime = System.currentTimeMillis();
            this.routingContext.useDynamicDistance = true;
            double searchRadius = this.routingContext.roundTripDistance == null ? 1500 : this.routingContext.roundTripDistance.intValue();
            double direction = this.routingContext.startDirection == null ? -1 : this.routingContext.startDirection.intValue();
            double dIntValue = this.routingContext.roundTripDirectionAdd == null ? this.ROUNDTRIP_DEFAULT_DIRECTIONADD : this.routingContext.roundTripDirectionAdd.intValue();
            if (direction == -1.0d) {
                direction = getRandomDirectionFromData(this.waypoints.get(0), searchRadius);
            }
            double direction2 = direction;
            if (this.routingContext.allowSamewayback) {
                int[] pos = CheapRuler.destination(this.waypoints.get(0).ilon, this.waypoints.get(0).ilat, searchRadius, direction2);
                MatchedWaypoint wpt2 = new MatchedWaypoint();
                wpt2.waypoint = new OsmNode(pos[0], pos[1]);
                wpt2.name = "rt1_" + direction2;
                OsmNodeNamed onn = new OsmNodeNamed(new OsmNode(pos[0], pos[1]));
                onn.name = "rt1";
                this.waypoints.add(onn);
            } else {
                buildPointsFromCircle(this.waypoints, direction2, searchRadius, this.routingContext.roundTripPoints == null ? 5 : this.routingContext.roundTripPoints.intValue());
            }
            this.routingContext.waypointCatchingRange = 250.0d;
            doRouting(0L);
            long endTime = System.currentTimeMillis();
            logInfo("round trip execution time = " + ((endTime - startTime) / 1000.0d) + " seconds");
        } catch (Exception e) {
            e.getStackTrace();
            logException(e);
        }
    }

    void buildPointsFromCircle(List<OsmNodeNamed> waypoints, double startAngle, double searchRadius, int points) {
        for (int i = 1; i < points; i++) {
            double anAngle = 90.0d - ((((double) i) * 180.0d) / ((double) points));
            int[] pos = CheapRuler.destination(waypoints.get(0).ilon, waypoints.get(0).ilat, searchRadius, startAngle - anAngle);
            OsmNodeNamed onn = new OsmNodeNamed(new OsmNode(pos[0], pos[1]));
            onn.name = "rt" + i;
            waypoints.add(onn);
        }
        OsmNodeNamed onn2 = new OsmNodeNamed(waypoints.get(0));
        onn2.name = "to_rt";
        waypoints.add(onn2);
    }

    int getRandomDirectionFromData(OsmNodeNamed wp, double searchRadius) throws Throwable {
        int preferredRandomType;
        List<AreaInfo> ais;
        long start;
        long start2 = System.currentTimeMillis();
        boolean consider_elevation = this.routingContext.expctxWay.getVariableValue("consider_elevation", 0.0f) == 1.0f;
        boolean consider_forest = this.routingContext.expctxWay.getVariableValue("consider_forest", 0.0f) == 1.0f;
        boolean consider_river = this.routingContext.expctxWay.getVariableValue("consider_river", 0.0f) == 1.0f;
        if (consider_elevation) {
            preferredRandomType = 1;
        } else if (consider_forest) {
            preferredRandomType = 4;
        } else {
            if (!consider_river) {
                return (int) (Math.random() * 360.0d);
            }
            preferredRandomType = 5;
        }
        MatchedWaypoint wpt1 = new MatchedWaypoint();
        wpt1.waypoint = wp;
        wpt1.name = "info";
        wpt1.radius = searchRadius * 1.5d;
        List<AreaInfo> ais2 = new ArrayList<>();
        AreaReader areareader = new AreaReader();
        if (this.routingContext.rawAreaPath != null) {
            File fai = new File(this.routingContext.rawAreaPath);
            if (fai.exists()) {
                areareader.readAreaInfo(fai, wpt1, ais2);
            }
        }
        if (ais2.isEmpty()) {
            List<MatchedWaypoint> listStart = new ArrayList<>();
            listStart.add(wpt1);
            List<OsmNodeNamed> wpliststart = new ArrayList<>();
            wpliststart.add(wp);
            List<OsmNodeNamed> listOne = new ArrayList<>();
            int a = 45;
            while (a < 360) {
                int[] pos = CheapRuler.destination(wp.ilon, wp.ilat, searchRadius * 1.5d, a);
                MatchedWaypoint wpt12 = wpt1;
                OsmNodeNamed onn = new OsmNodeNamed(new OsmNode(pos[0], pos[1]));
                onn.name = "via" + a;
                listOne.add(onn);
                MatchedWaypoint wpt = new MatchedWaypoint();
                wpt.waypoint = onn;
                wpt.name = onn.name;
                listStart.add(wpt);
                a += 90;
                consider_elevation = consider_elevation;
                wpt1 = wpt12;
                consider_forest = consider_forest;
            }
            MatchedWaypoint wpt13 = wpt1;
            RoutingContext rc = new RoutingContext();
            String name = this.routingContext.localFunction;
            int idx = name.lastIndexOf(File.separator);
            rc.localFunction = idx == -1 ? "dummy" : name.substring(0, idx + 1) + "dummy.brf";
            RoutingEngine re = new RoutingEngine(null, null, this.segmentDir, wpliststart, rc, 4);
            rc.useDynamicDistance = true;
            re.matchWaypointsToNodes(listStart);
            re.resetCache(true);
            int numForest = rc.expctxWay.getLookupKey("estimated_forest_class");
            int numRiver = rc.expctxWay.getLookupKey("estimated_river_class");
            OsmNode start1 = re.nodesCache.getStartNode(listStart.get(0).node1.getIdFromPos());
            double elev = start1 == null ? 0.0d : start1.getElev();
            int minlon = Integer.MAX_VALUE;
            start = start2;
            int minlat = Integer.MAX_VALUE;
            int maxlat = Integer.MIN_VALUE;
            int maxlon = Integer.MIN_VALUE;
            for (OsmNodeNamed on : listOne) {
                maxlon = Math.max(on.ilon, maxlon);
                minlon = Math.min(on.ilon, minlon);
                maxlat = Math.max(on.ilat, maxlat);
                minlat = Math.min(on.ilat, minlat);
            }
            OsmNogoPolygon searchRect = new OsmNogoPolygon(true);
            searchRect.addVertex(maxlon, maxlat);
            searchRect.addVertex(maxlon, minlat);
            searchRect.addVertex(minlon, minlat);
            searchRect.addVertex(minlon, maxlat);
            int a2 = 0;
            while (true) {
                int minlon2 = minlon;
                if (a2 >= 4) {
                    break;
                }
                List<MatchedWaypoint> listStart2 = listStart;
                rc.ai = new AreaInfo((a2 * 90) + 90);
                int maxlat2 = maxlat;
                int maxlon2 = maxlon;
                double elev2 = elev;
                rc.ai.elevStart = elev2;
                rc.ai.numForest = numForest;
                rc.ai.numRiver = numRiver;
                int numRiver2 = numRiver;
                rc.ai.polygon = new OsmNogoPolygon(true);
                rc.ai.polygon.addVertex(wp.ilon, wp.ilat);
                rc.ai.polygon.addVertex(listOne.get(a2).ilon, listOne.get(a2).ilat);
                if (a2 == 3) {
                    rc.ai.polygon.addVertex(listOne.get(0).ilon, listOne.get(0).ilat);
                } else {
                    rc.ai.polygon.addVertex(listOne.get(a2 + 1).ilon, listOne.get(a2 + 1).ilat);
                }
                ais2.add(rc.ai);
                a2++;
                minlon = minlon2;
                numRiver = numRiver2;
                listStart = listStart2;
                elev = elev2;
                maxlon = maxlon2;
                maxlat = maxlat2;
            }
            int maxscale = Math.abs(searchRect.points.get(2).x - searchRect.points.get(0).x);
            areareader.getDirectAllData(this.segmentDir, rc, wp, Math.max(1, Math.round((maxscale / 31250.0f) / 2.0f) + 1), rc.expctxWay, searchRect, ais2);
            if (this.routingContext.rawAreaPath == null) {
                ais = ais2;
            } else {
                try {
                    wpt13.radius = searchRadius * 1.5d;
                    ais = ais2;
                    try {
                        areareader.writeAreaInfo(this.routingContext.rawAreaPath, wpt13, ais);
                    } catch (Exception e) {
                    }
                } catch (Exception e2) {
                    ais = ais2;
                }
            }
            rc.ai = null;
        } else {
            ais = ais2;
            start = start2;
        }
        logInfo("round trip execution time = " + ((System.currentTimeMillis() - start) / 1000.0d) + " seconds");
        switch (preferredRandomType) {
            case 1:
                Collections.sort(ais, new Comparator<AreaInfo>() { // from class: btools.router.RoutingEngine.1
                    @Override // java.util.Comparator
                    public int compare(AreaInfo o1, AreaInfo o2) {
                        return o2.getElev50Weight() - o1.getElev50Weight();
                    }
                });
                break;
            case 2:
            case 3:
            default:
                return (int) (Math.random() * 360.0d);
            case 4:
                Collections.sort(ais, new Comparator<AreaInfo>() { // from class: btools.router.RoutingEngine.2
                    @Override // java.util.Comparator
                    public int compare(AreaInfo o1, AreaInfo o2) {
                        return o2.getGreen() - o1.getGreen();
                    }
                });
                break;
            case 5:
                Collections.sort(ais, new Comparator<AreaInfo>() { // from class: btools.router.RoutingEngine.3
                    @Override // java.util.Comparator
                    public int compare(AreaInfo o1, AreaInfo o2) {
                        return o2.getRiver() - o1.getRiver();
                    }
                });
                break;
        }
        int angle = ais.get(0).direction;
        return (angle - 30) + ((int) (Math.random() * 60.0d));
    }

    private void postElevationCheck(OsmTrack track) {
        int ourSize;
        OsmTrack osmTrack;
        short endElev;
        int ourSize2;
        int idx;
        int diffElev;
        int pos;
        OsmTrack osmTrack2 = track;
        int startIdx = 0;
        int dist = 0;
        int ourSize3 = osmTrack2.nodes.size();
        int idx2 = 0;
        OsmPathElement startPt = null;
        short endElev2 = Short.MIN_VALUE;
        short startElev = Short.MIN_VALUE;
        short lastElev = Short.MIN_VALUE;
        OsmPathElement startPt2 = null;
        while (idx2 < ourSize3) {
            OsmPathElement n = osmTrack2.nodes.get(idx2);
            if (n.getSElev() == Short.MIN_VALUE && lastElev != Short.MIN_VALUE && idx2 < ourSize3 - 1) {
                if (idx2 > 1) {
                    startElev = osmTrack2.nodes.get(idx2 - 2).getSElev();
                }
                if (startElev == Short.MIN_VALUE) {
                    startElev = lastElev;
                }
                startIdx = idx2;
                startPt2 = startPt;
                dist = 0;
                if (startPt != null) {
                    dist = 0 + n.calcDistance(startPt);
                    ourSize = ourSize3;
                    osmTrack = osmTrack2;
                } else {
                    ourSize = ourSize3;
                    osmTrack = osmTrack2;
                }
            } else if (n.getSElev() == Short.MIN_VALUE || lastElev != Short.MIN_VALUE || startElev == Short.MIN_VALUE) {
                OsmPathElement lastPt = startPt;
                OsmPathElement startPt3 = startPt2;
                short startElev2 = startElev;
                ourSize = ourSize3;
                int idx3 = idx2;
                if (n.getSElev() == Short.MIN_VALUE || lastElev != Short.MIN_VALUE || startIdx != 0) {
                    osmTrack = track;
                    idx2 = idx3;
                    if (n.getSElev() == Short.MIN_VALUE && idx2 == osmTrack.nodes.size() - 1) {
                        for (int i = idx2; i < osmTrack.nodes.size(); i++) {
                            osmTrack.nodes.get(i).setSElev(lastElev);
                        }
                        startIdx = idx2;
                        startElev = startElev2;
                        startPt2 = startPt3;
                    } else {
                        int startIdx2 = n.getSElev();
                        if (startIdx2 == -32768 && lastPt != null) {
                            dist += n.calcDistance(lastPt);
                            startElev = startElev2;
                            startPt2 = startPt3;
                        }
                    }
                } else {
                    int i2 = 0;
                    while (true) {
                        idx2 = idx3;
                        if (i2 >= idx2) {
                            break;
                        }
                        track.nodes.get(i2).setSElev(n.getSElev());
                        i2++;
                        idx3 = idx2;
                    }
                    osmTrack = track;
                }
                startElev = startElev2;
                startPt2 = startPt3;
            } else {
                if (idx2 + 1 < osmTrack2.nodes.size()) {
                    endElev2 = osmTrack2.nodes.get(idx2 + 1).getSElev();
                }
                if (endElev2 != Short.MIN_VALUE) {
                    endElev = endElev2;
                } else {
                    short endElev3 = n.getSElev();
                    endElev = endElev3;
                }
                int endIdx = idx2;
                OsmPathElement tmpPt = osmTrack2.nodes.get(startIdx > 1 ? startIdx - 2 : startIdx - 1);
                int diffElev2 = endElev - startElev;
                int dist2 = dist + tmpPt.calcDistance(startPt2) + n.calcDistance(startPt);
                OsmPathElement startPt4 = startPt2;
                double incline = ((double) diffElev2) / (((double) dist2) / 100.0d);
                double selev = osmTrack2.nodes.get(startIdx - 2).getSElev();
                boolean hasInclineTags = false;
                short distRest = startElev;
                int distRest2 = dist2;
                int dist3 = startIdx - 1;
                OsmPathElement tmpPt2 = tmpPt;
                String lastMsg = "";
                double startincline = 0.0d;
                double tmpincline = 0.0d;
                double incline2 = incline;
                double selev2 = selev;
                while (true) {
                    boolean hasInclineTags2 = hasInclineTags;
                    if (dist3 >= endIdx + 1) {
                        break;
                    }
                    int endIdx2 = endIdx;
                    OsmPathElement tmp = osmTrack2.nodes.get(dist3);
                    if (tmp.message != null) {
                        ourSize2 = ourSize3;
                        MessageData md = tmp.message.copy();
                        diffElev = diffElev2;
                        String msg = md.wayKeyValues;
                        if (msg.equals(lastMsg)) {
                            idx = idx2;
                            hasInclineTags = hasInclineTags2;
                        } else {
                            boolean revers = msg.contains("reversedirection=yes");
                            int pos2 = msg.indexOf("incline=");
                            if (pos2 != -1) {
                                hasInclineTags2 = true;
                                String s = msg.substring(pos2 + 8);
                                int pos3 = s.indexOf(" ");
                                if (pos3 != -1) {
                                    s = s.substring(0, pos3);
                                }
                                if (s.length() <= 0) {
                                    pos = pos3;
                                } else {
                                    try {
                                        int ind = s.indexOf("%");
                                        pos = pos3;
                                        if (ind != -1) {
                                            try {
                                                s = s.substring(0, ind);
                                            } catch (NumberFormatException e) {
                                                tmpincline = 0.0d;
                                            }
                                        }
                                        int ind2 = s.indexOf("°");
                                        if (ind2 != -1) {
                                            s = s.substring(0, ind2);
                                        }
                                        tmpincline = Double.parseDouble(s.trim());
                                        if (revers) {
                                            tmpincline *= -1.0d;
                                        }
                                    } catch (NumberFormatException e2) {
                                        pos = pos3;
                                    }
                                }
                                pos2 = pos;
                            } else {
                                tmpincline = 0.0d;
                            }
                            if (startincline == 0.0d) {
                                startincline = tmpincline;
                                idx = idx2;
                                hasInclineTags = hasInclineTags2;
                            } else if (startincline < 0.0d && tmpincline > 0.0d) {
                                double diff = ((double) endElev) - selev2;
                                idx = idx2;
                                tmpincline = diff / (((double) distRest2) / 100.0d);
                                hasInclineTags = hasInclineTags2;
                            } else {
                                idx = idx2;
                                hasInclineTags = hasInclineTags2;
                            }
                        }
                        lastMsg = msg;
                    } else {
                        ourSize2 = ourSize3;
                        idx = idx2;
                        diffElev = diffElev2;
                        hasInclineTags = hasInclineTags2;
                    }
                    int tmpdist = tmp.calcDistance(tmpPt2);
                    distRest2 -= tmpdist;
                    if (hasInclineTags) {
                        incline2 = tmpincline;
                    }
                    selev2 += (((double) tmpdist) / 100.0d) * incline2;
                    tmp.setSElev((short) selev2);
                    tmp.message.ele = (short) selev2;
                    tmpPt2 = tmp;
                    dist3++;
                    osmTrack2 = track;
                    endIdx = endIdx2;
                    ourSize3 = ourSize2;
                    diffElev2 = diffElev;
                    idx2 = idx;
                }
                ourSize = ourSize3;
                osmTrack = track;
                dist = 0;
                endElev2 = endElev;
                startElev = distRest;
                startPt2 = startPt4;
            }
            lastElev = n.getSElev();
            idx2++;
            ourSize3 = ourSize;
            OsmTrack osmTrack3 = osmTrack;
            startPt = n;
            osmTrack2 = osmTrack3;
        }
    }

    private void logException(Throwable t) {
        this.errorMessage = t instanceof RuntimeException ? t.getMessage() : t.toString();
        logInfo("Error (linksProcessed=" + this.linksProcessed + " open paths: " + this.openSet.getSize() + "): " + this.errorMessage);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v15 */
    /* JADX WARN: Type inference failed for: r0v17, types: [java.lang.Exception] */
    /* JADX WARN: Type inference failed for: r0v18, types: [java.io.Writer] */
    /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Exception] */
    /* JADX WARN: Type inference failed for: r0v3, types: [java.io.Writer] */
    /* JADX WARN: Type inference failed for: r0v5, types: [java.lang.Exception] */
    /* JADX WARN: Type inference failed for: r0v6, types: [java.io.Writer] */
    /* JADX WARN: Type inference failed for: r0v8, types: [java.lang.Exception] */
    /* JADX WARN: Type inference failed for: r0v9, types: [java.io.Writer] */
    public void doSearch() {
        Writer writer;
        boolean z = 1;
        z = 1;
        try {
            try {
                MatchedWaypoint matchedWaypoint = new MatchedWaypoint();
                matchedWaypoint.waypoint = this.waypoints.get(0);
                ArrayList arrayList = new ArrayList();
                arrayList.add(matchedWaypoint);
                matchWaypointsToNodes(arrayList);
                findTrack("seededSearch", matchedWaypoint, null, null, null, false);
                ProfileCache.releaseProfile(this.routingContext);
                if (this.nodesCache != null) {
                    this.nodesCache.close();
                    this.nodesCache = null;
                }
                this.openSet.clear();
                this.finished = true;
                writer = this.infoLogWriter;
                z = writer;
            } catch (Error e) {
                cleanOnOOM();
                logException(e);
                logThrowable(e);
                ProfileCache.releaseProfile(this.routingContext);
                if (this.nodesCache != null) {
                    this.nodesCache.close();
                    this.nodesCache = null;
                }
                this.openSet.clear();
                this.finished = true;
                Writer writer2 = this.infoLogWriter;
                z = writer2;
                if (writer2 != null) {
                    try {
                        e = this.infoLogWriter;
                        e.close();
                    } catch (Exception e2) {
                        e = e2;
                    }
                }
            } catch (IllegalArgumentException e3) {
                logException(e3);
                ProfileCache.releaseProfile(this.routingContext);
                if (this.nodesCache != null) {
                    this.nodesCache.close();
                    this.nodesCache = null;
                }
                this.openSet.clear();
                this.finished = true;
                Writer writer3 = this.infoLogWriter;
                z = writer3;
                if (writer3 != null) {
                    try {
                        e = this.infoLogWriter;
                        e.close();
                    } catch (Exception e4) {
                        e = e4;
                    }
                }
            } catch (Exception e5) {
                logException(e5);
                logThrowable(e5);
                ProfileCache.releaseProfile(this.routingContext);
                if (this.nodesCache != null) {
                    this.nodesCache.close();
                    this.nodesCache = null;
                }
                this.openSet.clear();
                this.finished = true;
                Writer writer4 = this.infoLogWriter;
                z = writer4;
                if (writer4 != null) {
                    try {
                        e = this.infoLogWriter;
                        e.close();
                    } catch (Exception e6) {
                        e = e6;
                    }
                }
            }
            if (writer != null) {
                try {
                    e = this.infoLogWriter;
                    e.close();
                } catch (Exception e7) {
                    e = e7;
                }
                this.infoLogWriter = null;
                z = e;
            }
        } catch (Throwable th) {
            ProfileCache.releaseProfile(this.routingContext);
            if (this.nodesCache != null) {
                this.nodesCache.close();
                this.nodesCache = null;
            }
            this.openSet.clear();
            this.finished = z;
            if (this.infoLogWriter != null) {
                try {
                    this.infoLogWriter.close();
                } catch (Exception e8) {
                }
                this.infoLogWriter = null;
            }
            throw th;
        }
    }

    public void cleanOnOOM() {
        terminate();
    }

    private OsmTrack findTrack(OsmTrack[] refTracks, OsmTrack[] lastTracks) {
        while (true) {
            try {
                return tryFindTrack(refTracks, lastTracks);
            } catch (RoutingIslandException e) {
                if (this.routingContext.useDynamicDistance) {
                    for (MatchedWaypoint mwp : this.matchedWaypoints) {
                        if (mwp.name.contains("_add")) {
                            long n1 = mwp.node1.getIdFromPos();
                            long n2 = mwp.node2.getIdFromPos();
                            this.islandNodePairs.addTempPair(n1, n2);
                        }
                    }
                }
                this.islandNodePairs.freezeTempPairs();
                this.nodesCache.clean(true);
                this.matchedWaypoints = null;
            }
        }
    }

    private OsmTrack tryFindTrack(OsmTrack[] refTracks, OsmTrack[] lastTracks) throws IOException {
        OsmTrack[] refTracks2;
        OsmTrack seg;
        int wptIndex;
        boolean hasDirectRouting;
        OsmTrack[] lastTracks2;
        OsmTrack[] refTracks3;
        Iterator<MatchedWaypoint> it;
        OsmTrack totaltrack = new OsmTrack();
        int nUnmatched = this.waypoints.size();
        boolean hasDirectRouting2 = false;
        if (this.useNodePoints && this.extraWaypoints != null) {
            for (OsmNodeNamed wp : this.extraWaypoints) {
                if (wp.wpttype == 3) {
                    hasDirectRouting2 = true;
                }
                if (!wp.name.startsWith("from")) {
                    this.waypoints.add(this.waypoints.size() - 1, wp);
                    this.waypoints.get(this.waypoints.size() - 2).wpttype = (byte) 3;
                    nUnmatched++;
                } else {
                    this.waypoints.add(1, wp);
                    this.waypoints.get(0).wpttype = (byte) 3;
                    nUnmatched++;
                }
            }
            this.extraWaypoints = null;
        }
        OsmTrack[] lastTracks3 = lastTracks;
        if (lastTracks3.length >= this.waypoints.size() - 1) {
            refTracks2 = refTracks;
        } else {
            refTracks2 = new OsmTrack[this.waypoints.size() - 1];
            lastTracks3 = new OsmTrack[this.waypoints.size() - 1];
            hasDirectRouting2 = true;
        }
        Iterator<OsmNodeNamed> it2 = this.waypoints.iterator();
        while (true) {
            String str = " via";
            if (!it2.hasNext()) {
                break;
            }
            OsmNodeNamed wp2 = it2.next();
            if (hasInfo()) {
                String strValueOf = String.valueOf(wp2);
                if (wp2.wpttype == 3) {
                    str = " beeline";
                } else if (wp2.wpttype != 2) {
                    str = "";
                }
                logInfo("wp=" + strValueOf + str);
            }
            if (wp2.wpttype == 3) {
                hasDirectRouting2 = true;
            }
        }
        OsmTrack nearbyTrack = null;
        if (!hasDirectRouting2 && lastTracks3[this.waypoints.size() - 2] == null) {
            StringBuilder debugInfo = hasInfo() ? new StringBuilder() : null;
            nearbyTrack = OsmTrack.readBinary(this.routingContext.rawTrackPath, this.waypoints.get(this.waypoints.size() - 1), this.routingContext.getNogoChecksums(), this.routingContext.profileTimestamp, debugInfo);
            if (nearbyTrack != null) {
                nUnmatched--;
            }
            if (hasInfo()) {
                boolean found = nearbyTrack != null;
                boolean dirty = found && nearbyTrack.isDirty;
                logInfo("read referenceTrack, found=" + found + " dirty=" + dirty + " " + String.valueOf(debugInfo));
            }
        }
        int nUnmatched2 = nUnmatched;
        OsmTrack nearbyTrack2 = nearbyTrack;
        if (this.matchedWaypoints == null) {
            this.matchedWaypoints = new ArrayList();
            for (int i = 0; i < nUnmatched2; i++) {
                MatchedWaypoint mwp = new MatchedWaypoint();
                mwp.waypoint = this.waypoints.get(i);
                mwp.name = this.waypoints.get(i).name;
                mwp.wpttype = this.waypoints.get(i).wpttype;
                this.matchedWaypoints.add(mwp);
            }
            int startSize = this.matchedWaypoints.size();
            matchWaypointsToNodes(this.matchedWaypoints);
            if (startSize >= this.matchedWaypoints.size()) {
                hasDirectRouting = hasDirectRouting2;
                lastTracks2 = lastTracks3;
                refTracks3 = refTracks2;
            } else {
                OsmTrack[] refTracks4 = new OsmTrack[this.matchedWaypoints.size() - 1];
                OsmTrack[] lastTracks4 = new OsmTrack[this.matchedWaypoints.size() - 1];
                hasDirectRouting = true;
                lastTracks2 = lastTracks4;
                refTracks3 = refTracks4;
            }
            Iterator<MatchedWaypoint> it3 = this.matchedWaypoints.iterator();
            while (it3.hasNext()) {
                MatchedWaypoint mwp2 = it3.next();
                if (hasInfo() && this.matchedWaypoints.size() != nUnmatched2) {
                    it = it3;
                    logInfo("new wp=" + String.valueOf(mwp2.waypoint) + " " + String.valueOf(mwp2.crosspoint) + (mwp2.wpttype == 3 ? " beeline" : mwp2.wpttype == 2 ? " via" : ""));
                } else {
                    it = it3;
                }
                it3 = it;
            }
            this.routingContext.checkMatchedWaypointAgainstNogos(this.matchedWaypoints);
            this.routingContext.inverseDirection = !this.routingContext.inverseRouting;
            this.airDistanceCostFactor = 0.0d;
            for (int i2 = 0; i2 < this.matchedWaypoints.size() - 1; i2++) {
                this.nodeLimit = this.MAXNODES_ISLAND_CHECK;
                if (this.matchedWaypoints.get(i2).wpttype != 3) {
                    if (this.routingContext.inverseRouting) {
                        if (findTrack("start-island-check", this.matchedWaypoints.get(i2), this.matchedWaypoints.get(i2 + 1), null, null, false) == null && this.nodeLimit > 0) {
                            throw new IllegalArgumentException("start island detected for section " + i2);
                        }
                    } else if (findTrack("target-island-check", this.matchedWaypoints.get(i2 + 1), this.matchedWaypoints.get(i2), null, null, false) == null && this.nodeLimit > 0) {
                        throw new IllegalArgumentException("target island detected for section " + i2);
                    }
                }
            }
            this.routingContext.inverseDirection = false;
            this.nodeLimit = 0;
            if (nearbyTrack2 != null) {
                this.matchedWaypoints.add(nearbyTrack2.endPoint);
            }
            hasDirectRouting2 = hasDirectRouting;
            lastTracks3 = lastTracks2;
            refTracks2 = refTracks3;
        } else if (lastTracks3.length < this.matchedWaypoints.size() - 1) {
            refTracks2 = new OsmTrack[this.matchedWaypoints.size() - 1];
            lastTracks3 = new OsmTrack[this.matchedWaypoints.size() - 1];
            hasDirectRouting2 = true;
        }
        for (MatchedWaypoint matchedWaypoint : this.matchedWaypoints) {
        }
        this.routingContext.hasDirectRouting = hasDirectRouting2;
        OsmPath.seg = 1;
        int i3 = 0;
        for (int i4 = 1; i3 < this.matchedWaypoints.size() - i4; i4 = 1) {
            if (lastTracks3[i3] != null) {
                if (refTracks2[i3] == null) {
                    refTracks2[i3] = new OsmTrack();
                }
                refTracks2[i3].addNodes(lastTracks3[i3]);
            }
            if (!this.routingContext.inverseRouting) {
                seg = searchTrack(this.matchedWaypoints.get(i3), this.matchedWaypoints.get(i3 + 1), i3 == this.matchedWaypoints.size() - 2 ? nearbyTrack2 : null, refTracks2[i3]);
                wptIndex = i3;
                if (this.routingContext.continueStraight && i3 < this.matchedWaypoints.size() - 2) {
                    OsmNode lastPoint = seg.containsNode(this.matchedWaypoints.get(i3 + 1).node1) ? this.matchedWaypoints.get(i3 + 1).node1 : this.matchedWaypoints.get(i3 + 1).node2;
                    OsmNodeNamed nogo = new OsmNodeNamed(lastPoint);
                    nogo.radius = 5.0d;
                    nogo.name = "nogo" + (i3 + 1);
                    nogo.nogoWeight = 9999.0d;
                    nogo.isNogo = true;
                    if (this.routingContext.nogopoints == null) {
                        this.routingContext.nogopoints = new ArrayList();
                    }
                    this.routingContext.nogopoints.add(nogo);
                }
            } else {
                this.routingContext.inverseDirection = true;
                seg = searchTrack(this.matchedWaypoints.get(i3 + 1), this.matchedWaypoints.get(i3), null, refTracks2[i3]);
                this.routingContext.inverseDirection = false;
                wptIndex = i3 + 1;
            }
            if (seg == null || this.routingContext.ai != null) {
                return null;
            }
            if (this.routingContext.correctMisplacedViaPoints && this.matchedWaypoints.get(i3).wpttype != 3 && this.matchedWaypoints.get(i3).wpttype != 2 && !this.routingContext.allowSamewayback) {
                snapPathConnection(totaltrack, seg, this.routingContext.inverseRouting ? this.matchedWaypoints.get(i3 + 1) : this.matchedWaypoints.get(i3));
            }
            if (wptIndex > 0) {
                this.matchedWaypoints.get(wptIndex).indexInTrack = totaltrack.nodes.size() - 1;
            }
            totaltrack.appendTrack(seg);
            lastTracks3[i3] = seg;
            i3++;
        }
        postElevationCheck(totaltrack);
        recalcTrack(totaltrack);
        this.matchedWaypoints.get(this.matchedWaypoints.size() - 1).indexInTrack = totaltrack.nodes.size() - 1;
        totaltrack.matchedWaypoints = this.matchedWaypoints;
        totaltrack.processVoiceHints(this.routingContext);
        totaltrack.prepareSpeedProfile(this.routingContext);
        totaltrack.showTime = this.routingContext.showTime;
        totaltrack.params = this.routingContext.keyValues;
        if (this.routingContext.poipoints != null) {
            totaltrack.pois = this.routingContext.poipoints;
        }
        return totaltrack;
    }

    OsmTrack getExtraSegment(OsmPathElement start, OsmPathElement end) {
        if (start == null || end == null) {
            return null;
        }
        List<MatchedWaypoint> wptlist = new ArrayList<>();
        MatchedWaypoint wpt1 = new MatchedWaypoint();
        wpt1.waypoint = new OsmNode(start.getILon(), start.getILat());
        wpt1.name = "wptx1";
        wpt1.crosspoint = new OsmNode(start.getILon(), start.getILat());
        wpt1.node1 = new OsmNode(start.getILon(), start.getILat());
        wpt1.node2 = new OsmNode(end.getILon(), end.getILat());
        wptlist.add(wpt1);
        MatchedWaypoint wpt2 = new MatchedWaypoint();
        wpt2.waypoint = new OsmNode(end.getILon(), end.getILat());
        wpt2.name = "wptx2";
        wpt2.crosspoint = new OsmNode(end.getILon(), end.getILat());
        wpt2.node2 = new OsmNode(start.getILon(), start.getILat());
        wpt2.node1 = new OsmNode(end.getILon(), end.getILat());
        wptlist.add(wpt2);
        MatchedWaypoint mwp1 = wptlist.get(0);
        MatchedWaypoint mwp2 = wptlist.get(1);
        boolean corr = this.routingContext.correctMisplacedViaPoints;
        this.routingContext.correctMisplacedViaPoints = false;
        this.guideTrack = new OsmTrack();
        this.guideTrack.addNode(start);
        this.guideTrack.addNode(end);
        OsmTrack mid = findTrack("getinfo", mwp1, mwp2, null, null, false);
        this.guideTrack = null;
        this.routingContext.correctMisplacedViaPoints = corr;
        return mid;
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x022e  */
    /* JADX WARN: Removed duplicated region for block: B:121:0x02b6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private int snapRoundaboutConnection(OsmTrack tt, OsmTrack t, int indexStart, int indexEnd, int indexMeeting, MatchedWaypoint startWp) {
        OsmPathElement ptEnd;
        int indexEnd2;
        int indexMeetingFore;
        OsmTrack mid;
        OsmTrack mid2;
        Iterator<OsmPathElement> it;
        OsmPathElement ttend;
        int i = indexEnd;
        int indexMeetingBack = indexMeeting == -1 ? tt.nodes.size() - 1 : indexMeeting;
        int indexMeetingFore2 = 0;
        int indexStartBack = indexStart;
        int indexStartFore = 0;
        OsmPathElement ptStart = tt.nodes.get(indexStartBack);
        OsmPathElement ptMeeting = tt.nodes.get(indexMeetingBack);
        OsmPathElement ptEnd2 = t.nodes.get(i);
        boolean bMeetingIsOnRoundabout = ptMeeting.message.isRoundabout();
        boolean bMeetsRoundaboutStart = false;
        int wayDistance = 0;
        OsmPathElement last_n = null;
        int i2 = 0;
        while (i2 < i) {
            OsmPathElement n = t.nodes.get(i2);
            if (last_n != null) {
                wayDistance += n.calcDistance(last_n);
            }
            last_n = n;
            if (n.positionEquals(ptStart)) {
                indexStartFore = i2;
                bMeetsRoundaboutStart = true;
            }
            if (n.positionEquals(ptMeeting)) {
                indexMeetingFore2 = i2;
            }
            i2++;
            i = indexEnd;
        }
        if (this.routingContext.correctMisplacedViaPointsDistance > 0.0d) {
            ptEnd = ptEnd2;
            if (wayDistance > this.routingContext.correctMisplacedViaPointsDistance) {
                return 0;
            }
        } else {
            ptEnd = ptEnd2;
        }
        if (!bMeetsRoundaboutStart && bMeetingIsOnRoundabout) {
            indexEnd2 = indexMeetingFore2;
        } else {
            indexEnd2 = indexEnd;
        }
        if (bMeetsRoundaboutStart && bMeetingIsOnRoundabout) {
            indexEnd2 = indexStartFore;
        }
        List<OsmPathElement> removeList = new ArrayList<>();
        if (!bMeetsRoundaboutStart) {
            indexStartBack = indexMeetingBack;
            while (!tt.nodes.get(indexStartBack).message.isRoundabout() && indexStartBack - 1 != 2) {
            }
        }
        int i3 = indexStartBack + 1;
        while (i3 < tt.nodes.size()) {
            OsmPathElement n2 = tt.nodes.get(i3);
            int indexMeetingBack2 = indexMeetingBack;
            OsmTrack.OsmPathElementHolder detours = tt.getFromDetourMap(n2.getIdFromPos());
            if (detours != null) {
                for (OsmTrack.OsmPathElementHolder h = detours; h != null; h = h.nextHolder) {
                }
            }
            removeList.add(n2);
            i3++;
            indexMeetingBack = indexMeetingBack2;
        }
        OsmPathElement ttend2 = null;
        if (bMeetingIsOnRoundabout || bMeetsRoundaboutStart) {
            indexMeetingFore = indexMeetingFore2;
        } else {
            OsmPathElement ttend3 = tt.nodes.get(indexStartBack);
            OsmPathElement ttend4 = ttend3;
            indexMeetingFore = indexMeetingFore2;
            if (tt.getFromDetourMap(ttend4.getIdFromPos()) != null) {
                ttend = ttend4;
                tt.registerDetourForId(ttend4.getIdFromPos(), null);
            } else {
                ttend = ttend4;
            }
            ttend2 = ttend;
        }
        Iterator<OsmPathElement> it2 = removeList.iterator();
        while (it2.hasNext()) {
            tt.nodes.remove(it2.next());
        }
        removeList.clear();
        int i4 = 0;
        while (i4 < indexEnd2) {
            OsmPathElement n3 = t.nodes.get(i4);
            if (n3.positionEquals(bMeetsRoundaboutStart ? ptStart : ptEnd) || (!bMeetingIsOnRoundabout && !bMeetsRoundaboutStart && n3.message.isRoundabout())) {
                break;
            }
            OsmTrack.OsmPathElementHolder detours2 = t.getFromDetourMap(n3.getIdFromPos());
            if (detours2 != null) {
                for (OsmTrack.OsmPathElementHolder h2 = detours2; h2 != null; h2 = h2.nextHolder) {
                }
            }
            removeList.add(n3);
            i4++;
        }
        float atime = 0.0f;
        float aenergy = 0.0f;
        int acost = 0;
        if (i4 > 1) {
            atime = t.nodes.get(i4).getTime();
            aenergy = t.nodes.get(i4).getEnergy();
            acost = t.nodes.get(i4).cost;
        }
        for (Iterator<OsmPathElement> it3 = removeList.iterator(); it3.hasNext(); it3 = it3) {
            t.nodes.remove(it3.next());
            indexEnd2 = indexEnd2;
        }
        removeList.clear();
        if (atime > 0.0f) {
            for (Iterator<OsmPathElement> it4 = t.nodes.iterator(); it4.hasNext(); it4 = it4) {
                OsmPathElement e = it4.next();
                e.setTime(e.getTime() - atime);
                e.setEnergy(e.getEnergy() - aenergy);
                e.cost -= acost;
            }
        }
        if (!bMeetingIsOnRoundabout && !bMeetsRoundaboutStart) {
            OsmTrack.OsmPathElementHolder ttend_detours = tt.getFromDetourMap(ttend2.getIdFromPos());
            if (ttend_detours != null) {
                mid = null;
                if (ttend_detours.node != null) {
                    mid2 = getExtraSegment(ttend2, ttend_detours.node);
                }
                OsmPathElement tt_end = tt.nodes.get(tt.nodes.size() - 1);
                int last_cost = tt_end.cost;
                float last_time = tt_end.getTime();
                float last_energy = tt_end.getEnergy();
                int tmp_cost = 0;
                float tmp_time = 0.0f;
                float tmp_energy = 0.0f;
                if (mid2 == null) {
                    boolean start = false;
                    Iterator<OsmPathElement> it5 = mid2.nodes.iterator();
                    while (true) {
                        if (!it5.hasNext()) {
                            break;
                        }
                        OsmTrack mid3 = mid2;
                        OsmPathElement e2 = it5.next();
                        if (!start) {
                            it = it5;
                        } else {
                            it = it5;
                            if (e2.positionEquals(ttend_detours.node)) {
                                int tmp_cost2 = e2.cost;
                                tmp_time = e2.getTime();
                                tmp_energy = e2.getEnergy();
                                tmp_cost = tmp_cost2;
                                break;
                            }
                            int tmp_cost3 = e2.cost;
                            e2.cost = tmp_cost3 + last_cost;
                            e2.setTime(last_time + e2.getTime());
                            e2.setEnergy(last_energy + e2.getEnergy());
                            tt.nodes.add(e2);
                        }
                        if (e2.positionEquals(tt_end)) {
                            start = true;
                        }
                        it5 = it;
                        mid2 = mid3;
                    }
                    ttend_detours.node.cost = last_cost + tmp_cost;
                    ttend_detours.node.setTime(last_time + tmp_time);
                    ttend_detours.node.setEnergy(last_energy + tmp_energy);
                    tt.nodes.add(ttend_detours.node);
                    t.nodes.add(0, ttend_detours.node);
                }
            } else {
                mid = null;
            }
            mid2 = mid;
            OsmPathElement tt_end2 = tt.nodes.get(tt.nodes.size() - 1);
            int last_cost2 = tt_end2.cost;
            float last_time2 = tt_end2.getTime();
            float last_energy2 = tt_end2.getEnergy();
            int tmp_cost4 = 0;
            float tmp_time2 = 0.0f;
            float tmp_energy2 = 0.0f;
            if (mid2 == null) {
            }
        }
        tt.cost = tt.nodes.get(tt.nodes.size() - 1).cost;
        t.cost = t.nodes.get(t.nodes.size() - 1).cost;
        startWp.correctedpoint = new OsmNode(ptStart.getILon(), ptStart.getILat());
        return t.nodes.size();
    }

    /* JADX WARN: Removed duplicated region for block: B:140:0x03cb  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x03fd A[LOOP:6: B:142:0x03f7->B:144:0x03fd, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:148:0x0419 A[LOOP:7: B:146:0x0413->B:148:0x0419, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:152:0x042f A[LOOP:8: B:150:0x0429->B:152:0x042f, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:155:0x044b  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x0490  */
    /* JADX WARN: Removed duplicated region for block: B:169:0x049a  */
    /* JADX WARN: Removed duplicated region for block: B:176:0x01d7 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x0143 A[PHI: r34
      0x0143: PHI (r34v11 'stop' int) = (r34v9 'stop' int), (r34v12 'stop' int) binds: [B:46:0x0141, B:40:0x0130] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:50:0x014d  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x015e  */
    /* JADX WARN: Removed duplicated region for block: B:61:0x01bb  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01c1  */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01c9  */
    /* JADX WARN: Removed duplicated region for block: B:70:0x01d2  */
    /* JADX WARN: Removed duplicated region for block: B:74:0x01df A[LOOP:1: B:24:0x00ee->B:74:0x01df, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean snapPathConnection(OsmTrack tt, OsmTrack t, MatchedWaypoint startWp) {
        int ourSize;
        int stop;
        CompactLongMap<OsmTrack.OsmPathElementHolder> lastJunctions;
        int ourSize2;
        List<OsmPathElement> removeForeList;
        OsmPathElement tmpfore;
        int indexback;
        List<OsmPathElement> removeForeList2;
        float atime;
        Iterator<OsmPathElement> it;
        Iterator<OsmPathElement> it2;
        Iterator<Integer> it3;
        OsmPathElement last;
        OsmPathElement tmpback;
        boolean bBackRoundAbout;
        boolean bForeRoundAbout;
        if ((startWp.name.startsWith("via") || startWp.name.startsWith("rt")) && (ourSize = tt.nodes.size()) > 0) {
            tt.nodes.get(ourSize - 1);
            if (this.routingContext.poipoints != null) {
                Iterator<OsmNodeNamed> it4 = this.routingContext.poipoints.iterator();
                while (it4.hasNext()) {
                    OsmNodeNamed node = it4.next();
                    int lon0 = tt.nodes.get(ourSize - 2).getILon();
                    int lat0 = tt.nodes.get(ourSize - 2).getILat();
                    int lon1 = startWp.crosspoint.ilon;
                    int lat1 = startWp.crosspoint.ilat;
                    int lon2 = node.ilon;
                    int lat2 = node.ilat;
                    this.routingContext.anglemeter.calcAngle(lon0, lat0, lon1, lat1, lon2, lat2);
                    Iterator<OsmNodeNamed> it5 = it4;
                    if (node.calcDistance(startWp.crosspoint) < this.routingContext.waypointCatchingRange) {
                        return false;
                    }
                    it4 = it5;
                }
            }
            List<OsmPathElement> removeBackList = new ArrayList<>();
            List<OsmPathElement> removeForeList3 = new ArrayList<>();
            List<Integer> removeVoiceHintList = new ArrayList<>();
            CompactLongMap<OsmTrack.OsmPathElementHolder> lastJunctions2 = new CompactLongMap<>();
            OsmPathElement tmpback2 = null;
            OsmPathElement tmpfore2 = null;
            OsmPathElement tmpStart = null;
            int indexback2 = ourSize - 1;
            int indexfore = 0;
            int stop2 = indexback2 - this.MAX_STEPS_CHECK > 1 ? indexback2 - this.MAX_STEPS_CHECK : 1;
            double wayDistance = 0.0d;
            boolean bCheckRoundAbout = false;
            boolean bBackRoundAbout2 = false;
            boolean bForeRoundAbout2 = false;
            int indexBackFound = 0;
            int indexForeFound = 0;
            int differentLanePoints = 0;
            int indexMeeting = -1;
            for (int i = 1; indexback2 >= i && indexback2 >= stop2 && indexfore < t.nodes.size(); i = 1) {
                OsmPathElement tmpback3 = tt.nodes.get(indexback2);
                tmpback2 = tmpback3;
                OsmPathElement tmpfore3 = t.nodes.get(indexfore);
                tmpfore2 = tmpfore3;
                if (!bBackRoundAbout2 && tmpback2.message != null && tmpback2.message.isRoundabout()) {
                    bBackRoundAbout = true;
                    indexBackFound = indexfore;
                } else {
                    bBackRoundAbout = bBackRoundAbout2;
                }
                if (bForeRoundAbout2) {
                    stop = stop2;
                } else {
                    stop = stop2;
                    if (tmpfore2.message != null && tmpfore2.message.isRoundabout()) {
                        bForeRoundAbout = true;
                        indexForeFound = indexfore;
                        if (indexfore != 0) {
                            lastJunctions = lastJunctions2;
                            OsmPathElement tmpStart2 = t.nodes.get(0);
                            tmpStart = tmpStart2;
                            ourSize2 = ourSize;
                            removeForeList = removeForeList3;
                        } else {
                            lastJunctions = lastJunctions2;
                            ourSize2 = ourSize;
                            double dirback = CheapAngleMeter.getDirection(tmpStart.getILon(), tmpStart.getILat(), tmpback2.getILon(), tmpback2.getILat());
                            OsmPathElement tmpStart3 = tmpStart;
                            removeForeList = removeForeList3;
                            double dirfore = CheapAngleMeter.getDirection(tmpStart.getILon(), tmpStart.getILat(), tmpfore2.getILon(), tmpfore2.getILat());
                            double dirdiff = CheapAngleMeter.getDifferenceFromDirection(dirback, dirfore);
                            if (dirdiff > 60.0d && !bBackRoundAbout && !bForeRoundAbout) {
                                tmpfore = tmpfore2;
                                break;
                            }
                            tmpStart = tmpStart3;
                        }
                        if (bBackRoundAbout == bForeRoundAbout && indexfore - Math.abs(indexForeFound - indexBackFound) > 8) {
                            tmpfore = tmpfore2;
                            break;
                        }
                        if (!tmpback2.positionEquals(tmpfore2)) {
                            differentLanePoints++;
                        }
                        if (tmpback2.positionEquals(tmpfore2)) {
                            indexMeeting = indexback2;
                        }
                        bCheckRoundAbout = !bBackRoundAbout && bForeRoundAbout;
                        if (!bCheckRoundAbout) {
                            tmpfore = tmpfore2;
                            break;
                        }
                        indexback2--;
                        indexfore++;
                        bForeRoundAbout2 = bForeRoundAbout;
                        bBackRoundAbout2 = bBackRoundAbout;
                        stop2 = stop;
                        lastJunctions2 = lastJunctions;
                        ourSize = ourSize2;
                        removeForeList3 = removeForeList;
                    }
                }
                if (!tmpback2.positionEquals(tmpfore2) || !tmpback2.message.isRoundabout()) {
                    bForeRoundAbout = bForeRoundAbout2;
                }
                if (indexfore != 0) {
                }
                if (bBackRoundAbout == bForeRoundAbout) {
                    if (!tmpback2.positionEquals(tmpfore2)) {
                    }
                    if (tmpback2.positionEquals(tmpfore2)) {
                    }
                    if (bBackRoundAbout) {
                        bCheckRoundAbout = !bBackRoundAbout && bForeRoundAbout;
                        if (!bCheckRoundAbout) {
                        }
                    }
                }
            }
            stop = stop2;
            lastJunctions = lastJunctions2;
            ourSize2 = ourSize;
            removeForeList = removeForeList3;
            tmpfore = tmpfore2;
            if (bCheckRoundAbout) {
                int indexback3 = indexback2 - 1;
                OsmPathElement tmpback4 = tt.nodes.get(indexback3);
                OsmPathElement tmpback5 = tmpback4;
                int indexback4 = indexback3;
                while (tmpback5.message != null && tmpback5.message.isRoundabout()) {
                    indexback4--;
                    OsmPathElement tmpback6 = tt.nodes.get(indexback4);
                    tmpback5 = tmpback6;
                }
                int ifore = indexfore + 1;
                OsmPathElement testfore = t.nodes.get(ifore);
                while (ifore < t.nodes.size() && testfore.message != null && testfore.message.isRoundabout()) {
                    OsmPathElement testfore2 = t.nodes.get(ifore);
                    testfore = testfore2;
                    ifore++;
                }
                snapRoundaboutConnection(tt, t, indexback4, ifore - 1, indexMeeting, startWp);
                removeVoiceHintList.clear();
                removeBackList.clear();
                removeForeList.clear();
                return true;
            }
            int stop3 = stop;
            CompactLongMap<OsmTrack.OsmPathElementHolder> lastJunctions3 = lastJunctions;
            int indexback5 = ourSize2 - 1;
            int indexfore2 = 0;
            for (int i2 = 1; indexback5 >= i2 && indexback5 >= stop3 && indexfore2 < t.nodes.size(); i2 = 1) {
                int junctions = 0;
                OsmPathElement tmpback7 = tt.nodes.get(indexback5);
                OsmPathElement tmpback8 = tmpback7;
                OsmPathElement tmpfore4 = t.nodes.get(indexfore2);
                if (tmpback8.message == null || tmpback8.message.isRoundabout()) {
                }
                if (tmpfore4.message == null || tmpfore4.message.isRoundabout()) {
                }
                int dist = tmpback8.calcDistance(tmpfore4);
                OsmTrack.OsmPathElementHolder detours = tt.getFromDetourMap(tmpback8.getIdFromPos());
                OsmTrack.OsmPathElementHolder h = detours;
                while (h != null) {
                    junctions++;
                    lastJunctions3.put(h.node.getIdFromPos(), h);
                    h = h.nextHolder;
                    stop3 = stop3;
                    indexback5 = indexback5;
                }
                int stop4 = stop3;
                int indexback6 = indexback5;
                if (dist != 1 || indexfore2 <= 0) {
                    tmpback = tmpback8;
                    removeForeList2 = removeForeList;
                } else {
                    if (indexfore2 == 1) {
                        removeBackList.add(tt.nodes.get(tt.nodes.size() - 1));
                        removeForeList2 = removeForeList;
                        removeForeList2.add(t.nodes.get(0));
                        removeBackList.add(tmpback8);
                        removeForeList2.add(tmpfore4);
                        removeVoiceHintList.add(Integer.valueOf(tt.nodes.size() - 1));
                        removeVoiceHintList.add(Integer.valueOf(indexback6));
                    } else {
                        removeForeList2 = removeForeList;
                        removeBackList.add(tmpback8);
                        removeForeList2.add(tmpfore4);
                        removeVoiceHintList.add(Integer.valueOf(indexback6));
                    }
                    tmpback = tmpback8;
                    double nextDist = t.nodes.get(indexfore2 - 1).calcDistance(tmpfore4);
                    wayDistance += nextDist;
                }
                if (dist <= 1) {
                    indexback = indexback6;
                    if (indexback != 1) {
                        int indexback7 = indexback - 1;
                        indexfore2++;
                        if (this.routingContext.correctMisplacedViaPointsDistance > 0.0d && wayDistance > this.routingContext.correctMisplacedViaPointsDistance) {
                            removeVoiceHintList.clear();
                            removeBackList.clear();
                            removeForeList2.clear();
                            return false;
                        }
                        removeForeList = removeForeList2;
                        tmpback2 = tmpback;
                        stop3 = stop4;
                        indexback5 = indexback7;
                    }
                } else {
                    indexback = indexback6;
                }
                if (removeBackList.size() != 0) {
                    removeBackList.remove(removeBackList.get(removeBackList.size() - 1));
                    removeForeList2.remove(removeForeList2.get(removeForeList2.size() - 1));
                    tmpback2 = tmpback;
                    atime = 0.0f;
                    float aenergy = 0.0f;
                    int acost = 0;
                    if (removeForeList2.size() > 1) {
                        atime = t.nodes.get(indexfore2 - 1).getTime();
                        aenergy = t.nodes.get(indexfore2 - 1).getEnergy();
                        acost = t.nodes.get(indexfore2 - 1).cost;
                    }
                    it = removeBackList.iterator();
                    while (it.hasNext()) {
                        tt.nodes.remove(it.next());
                        tmpback2 = tmpback2;
                    }
                    it2 = removeForeList2.iterator();
                    while (it2.hasNext()) {
                        t.nodes.remove(it2.next());
                    }
                    it3 = removeVoiceHintList.iterator();
                    while (it3.hasNext()) {
                        tt.removeVoiceHint(it3.next().intValue());
                    }
                    removeVoiceHintList.clear();
                    removeBackList.clear();
                    removeForeList2.clear();
                    if (atime > 0.0f) {
                        for (OsmPathElement e : t.nodes) {
                            e.setTime(e.getTime() - atime);
                            e.setEnergy(e.getEnergy() - aenergy);
                            e.cost -= acost;
                        }
                    }
                    if (t.nodes.size() < 2 && tt.nodes.size() >= 1) {
                        if (tt.nodes.size() != 1) {
                            last = tt.nodes.get(0);
                        } else {
                            last = tt.nodes.get(tt.nodes.size() - 2);
                        }
                        OsmPathElement newJunction = t.nodes.get(0);
                        t.nodes.get(1);
                        tt.cost = tt.nodes.get(tt.nodes.size() - 1).cost;
                        t.cost = t.nodes.get(t.nodes.size() - 1).cost;
                        int iLon = newJunction.getILon();
                        int indexfore3 = newJunction.getILat();
                        startWp.correctedpoint = new OsmNode(iLon, indexfore3);
                        return true;
                    }
                    return true;
                }
                return false;
            }
            indexback = indexback5;
            removeForeList2 = removeForeList;
            atime = 0.0f;
            float aenergy2 = 0.0f;
            int acost2 = 0;
            if (removeForeList2.size() > 1) {
            }
            it = removeBackList.iterator();
            while (it.hasNext()) {
            }
            it2 = removeForeList2.iterator();
            while (it2.hasNext()) {
            }
            it3 = removeVoiceHintList.iterator();
            while (it3.hasNext()) {
            }
            removeVoiceHintList.clear();
            removeBackList.clear();
            removeForeList2.clear();
            if (atime > 0.0f) {
            }
            if (t.nodes.size() < 2) {
                return true;
            }
            if (tt.nodes.size() != 1) {
            }
            OsmPathElement newJunction2 = t.nodes.get(0);
            t.nodes.get(1);
            tt.cost = tt.nodes.get(tt.nodes.size() - 1).cost;
            t.cost = t.nodes.get(t.nodes.size() - 1).cost;
            int iLon2 = newJunction2.getILon();
            int indexfore32 = newJunction2.getILat();
            startWp.correctedpoint = new OsmNode(iLon2, indexfore32);
            return true;
        }
        return false;
    }

    private void recalcTrack(OsmTrack t) {
        SortedSet<Integer> keys;
        int totaldist;
        short ele_start;
        float lasttime;
        int totaltime;
        Map<Integer, Integer> directMap;
        float speed_min;
        Iterator<Integer> it;
        double addEnergy;
        int ourSize;
        double incline;
        float speed;
        int ourSize2;
        double ascend;
        double ehb;
        short ele_start2;
        int dist;
        double angle;
        short ele_start3;
        double ehb2;
        Map<Integer, Integer> directMap2 = new HashMap<>();
        int ourSize3 = t.nodes.size();
        double eleFactor = this.routingContext.inverseRouting ? 0.25d : -0.25d;
        int i = 0;
        int totaltime2 = 0;
        int totaltime3 = 0;
        short ele_end = Short.MIN_VALUE;
        float lasttime2 = 0.0f;
        float speed_min2 = 0.0f;
        float speed_min3 = 9999.0f;
        float speed2 = 1.0f;
        float speed3 = 1.0f;
        short dist2 = Short.MIN_VALUE;
        double ascend2 = 0.0d;
        double ehb3 = 0.0d;
        while (i < ourSize3) {
            OsmPathElement n = t.nodes.get(i);
            if (n.message == null) {
                n.message = new MessageData();
            }
            OsmPathElement nLast = null;
            if (i == 0) {
                speed = speed3;
                ascend = ascend2;
                ehb = ehb3;
                angle = 0.0d;
                ele_start2 = dist2;
                ourSize2 = ourSize3;
                dist = 0;
            } else if (i == 1) {
                speed = speed3;
                nLast = t.nodes.get(0);
                ascend = ascend2;
                ehb = ehb3;
                angle = 0.0d;
                ele_start2 = dist2;
                ourSize2 = ourSize3;
                dist = nLast.calcDistance(n);
            } else {
                speed = speed3;
                int lon0 = t.nodes.get(i - 2).getILon();
                ourSize2 = ourSize3;
                int ourSize4 = i - 2;
                int lat0 = t.nodes.get(ourSize4).getILat();
                ascend = ascend2;
                int lon1 = t.nodes.get(i - 1).getILon();
                int lat1 = t.nodes.get(i - 1).getILat();
                int lon2 = t.nodes.get(i).getILon();
                ehb = ehb3;
                int lat2 = t.nodes.get(i).getILat();
                ele_start2 = dist2;
                double angle2 = this.routingContext.anglemeter.calcAngle(lon0, lat0, lon1, lat1, lon2, lat2);
                int lon02 = i - 1;
                nLast = t.nodes.get(lon02);
                dist = nLast.calcDistance(n);
                angle = angle2;
            }
            n.message.linkdist = dist;
            n.message.turnangle = (float) angle;
            int totaldist2 = totaltime3 + dist;
            int totaltime4 = (int) (totaltime2 + n.getTime());
            float tmptime = n.getTime() - lasttime2;
            if (dist > 0) {
                float speed4 = (dist / tmptime) * 3.6f;
                speed_min3 = Math.min(speed_min3, speed4);
                speed = speed4;
            }
            if (tmptime == 1.0f) {
                directMap2.put(Integer.valueOf(i), Integer.valueOf(dist));
            }
            float lastenergy = n.getEnergy();
            lasttime2 = n.getTime();
            short ele = n.getSElev();
            if (ele != Short.MIN_VALUE) {
                ele_end = ele;
            }
            short ele_start4 = ele_start2;
            if (ele_start4 == Short.MIN_VALUE) {
                ele_start4 = ele;
            }
            if (nLast == null) {
                ele_start3 = ele_start4;
                ehb2 = ehb;
            } else {
                ele_start3 = ele_start4;
                short ele_last = nLast.getSElev();
                ehb2 = ele_last != Short.MIN_VALUE ? ehb + (((double) (ele_last - ele)) * eleFactor) : ehb;
                double filter = elevationFilter(n);
                if (ehb2 > 0.0d) {
                    double ascend3 = ascend + ehb2;
                    ehb2 = 0.0d;
                    ascend = ascend3;
                } else if (ehb2 < filter) {
                    ehb2 = filter;
                }
            }
            i++;
            speed2 = tmptime;
            speed_min2 = lastenergy;
            dist2 = ele_start3;
            ourSize3 = ourSize2;
            speed3 = speed;
            ascend2 = ascend;
            ehb3 = ehb2;
            totaltime2 = totaltime4;
            totaltime3 = totaldist2;
        }
        int totaltime5 = totaltime2;
        short ele_start5 = dist2;
        int ourSize5 = ourSize3;
        t.ascend = (int) ascend2;
        t.plainAscend = (int) ((((double) (ele_start5 - ele_end)) * eleFactor) + 0.5d);
        t.distance = totaltime3;
        SortedSet<Integer> keys2 = new TreeSet<>(directMap2.keySet());
        Iterator<Integer> it2 = keys2.iterator();
        while (it2.hasNext()) {
            Integer key = it2.next();
            int value = directMap2.get(key).intValue();
            float addTime = value / (speed_min3 / 3.6f);
            if (key.intValue() <= 0) {
                keys = keys2;
                totaldist = totaltime3;
                ele_start = ele_start5;
                lasttime = lasttime2;
                totaltime = totaltime5;
                directMap = directMap2;
                speed_min = speed_min3;
                it = it2;
                addEnergy = 0.0d;
            } else {
                keys = keys2;
                List<OsmPathElement> list = t.nodes;
                totaldist = totaltime3;
                int totaldist3 = key.intValue() - 1;
                if (list.get(totaldist3).getSElev() == Short.MIN_VALUE || t.nodes.get(key.intValue()).getSElev() == Short.MIN_VALUE) {
                    ele_start = ele_start5;
                    lasttime = lasttime2;
                    incline = 0.0d;
                    totaltime = totaltime5;
                    directMap = directMap2;
                    double f_roll = this.routingContext.totalMass * 9.81d * (this.routingContext.defaultC_r + incline);
                    double spd = ((double) speed_min3) / 3.6d;
                    double incline2 = value;
                    speed_min = speed_min3;
                    it = it2;
                    double addEnergy2 = incline2 * ((this.routingContext.S_C_x * spd * spd) + f_roll);
                    addEnergy = addEnergy2;
                } else {
                    ele_start = ele_start5;
                    lasttime = lasttime2;
                    incline = (t.nodes.get(key.intValue() - 1).getElev() - t.nodes.get(key.intValue()).getElev()) / ((double) value);
                    totaltime = totaltime5;
                    directMap = directMap2;
                    double f_roll2 = this.routingContext.totalMass * 9.81d * (this.routingContext.defaultC_r + incline);
                    double spd2 = ((double) speed_min3) / 3.6d;
                    double incline22 = value;
                    speed_min = speed_min3;
                    it = it2;
                    double addEnergy22 = incline22 * ((this.routingContext.S_C_x * spd2 * spd2) + f_roll2);
                    addEnergy = addEnergy22;
                }
            }
            int j = key.intValue();
            while (true) {
                ourSize = ourSize5;
                if (j < ourSize) {
                    OsmPathElement n2 = t.nodes.get(j);
                    n2.setTime(n2.getTime() + addTime);
                    n2.setEnergy(n2.getEnergy() + ((float) addEnergy));
                    j++;
                    ourSize5 = ourSize;
                }
            }
            ourSize5 = ourSize;
            ele_start5 = ele_start;
            keys2 = keys;
            totaltime3 = totaldist;
            lasttime2 = lasttime;
            totaltime5 = totaltime;
            directMap2 = directMap;
            speed_min3 = speed_min;
            it2 = it;
        }
        t.energy = (int) t.nodes.get(t.nodes.size() - 1).getEnergy();
        logInfo("track-length total = " + t.distance);
        logInfo("filtered ascend = " + t.ascend);
    }

    double elevationFilter(OsmPos n) {
        if (this.nodesCache != null) {
            int r = this.nodesCache.getElevationType(n.getILon(), n.getILat());
            return r == 1 ? -5.0d : -10.0d;
        }
        return -10.0d;
    }

    private void matchWaypointsToNodes(List<MatchedWaypoint> unmatchedWaypoints) {
        RoutingEngine routingEngine = this;
        routingEngine.resetCache(false);
        boolean useDynamicDistance = routingEngine.routingContext.useDynamicDistance;
        boolean bAddBeeline = routingEngine.routingContext.buildBeelineOnRange;
        double range = routingEngine.routingContext.waypointCatchingRange;
        boolean ok = routingEngine.nodesCache.matchWaypointsToNodes(unmatchedWaypoints, range, routingEngine.islandNodePairs);
        if (!ok && useDynamicDistance) {
            routingEngine.logInfo("second check for way points");
            routingEngine.resetCache(false);
            double range2 = -routingEngine.MAX_DYNAMIC_RANGE;
            List<MatchedWaypoint> tmp = new ArrayList<>();
            for (MatchedWaypoint mwp : unmatchedWaypoints) {
                if (mwp.crosspoint == null || mwp.radius >= routingEngine.routingContext.waypointCatchingRange) {
                    tmp.add(mwp);
                }
            }
            ok = routingEngine.nodesCache.matchWaypointsToNodes(tmp, range2, routingEngine.islandNodePairs);
        }
        if (!ok) {
            for (MatchedWaypoint mwp2 : unmatchedWaypoints) {
                if (mwp2.crosspoint == null) {
                    throw new IllegalArgumentException(mwp2.name + "-position not mapped in existing datafile");
                }
            }
        }
        if (useDynamicDistance && !routingEngine.useNodePoints && bAddBeeline) {
            List<MatchedWaypoint> waypoints = new ArrayList<>();
            int i = 0;
            while (i < unmatchedWaypoints.size()) {
                MatchedWaypoint wp = unmatchedWaypoints.get(i);
                if (wp.waypoint.calcDistance(wp.crosspoint) > routingEngine.routingContext.waypointCatchingRange) {
                    MatchedWaypoint nmw = new MatchedWaypoint();
                    if (i == 0) {
                        OsmNodeNamed onn = new OsmNodeNamed(wp.waypoint);
                        onn.name = "from";
                        nmw.waypoint = onn;
                        nmw.name = onn.name;
                        nmw.crosspoint = new OsmNode(wp.waypoint.ilon, wp.waypoint.ilat);
                        nmw.wpttype = (byte) 3;
                        OsmNodeNamed onn2 = new OsmNodeNamed(wp.crosspoint);
                        onn2.name = wp.name + "_add";
                        wp.waypoint = onn2;
                        waypoints.add(nmw);
                        wp.name += "_add";
                        waypoints.add(wp);
                    } else {
                        OsmNodeNamed onn3 = new OsmNodeNamed(wp.crosspoint);
                        onn3.name = wp.name + "_add";
                        nmw.waypoint = onn3;
                        nmw.crosspoint = new OsmNode(wp.crosspoint.ilon, wp.crosspoint.ilat);
                        nmw.node1 = new OsmNode(wp.node1.ilon, wp.node1.ilat);
                        nmw.node2 = new OsmNode(wp.node2.ilon, wp.node2.ilat);
                        nmw.wpttype = (byte) 3;
                        if (wp.name != null) {
                            nmw.name = wp.name;
                        }
                        waypoints.add(nmw);
                        wp.name += "_add";
                        waypoints.add(wp);
                        if (wp.name.startsWith("via")) {
                            wp.wpttype = (byte) 3;
                            MatchedWaypoint emw = new MatchedWaypoint();
                            OsmNodeNamed onn22 = new OsmNodeNamed(wp.crosspoint);
                            onn22.name = wp.name + "_2";
                            emw.name = onn22.name;
                            emw.waypoint = onn22;
                            emw.crosspoint = new OsmNode(nmw.crosspoint.ilon, nmw.crosspoint.ilat);
                            emw.node1 = new OsmNode(nmw.node1.ilon, nmw.node1.ilat);
                            emw.node2 = new OsmNode(nmw.node2.ilon, nmw.node2.ilat);
                            emw.wpttype = (byte) 1;
                            waypoints.add(emw);
                        }
                        wp.crosspoint = new OsmNode(wp.waypoint.ilon, wp.waypoint.ilat);
                    }
                } else {
                    waypoints.add(wp);
                }
                i++;
                routingEngine = this;
            }
            unmatchedWaypoints.clear();
            unmatchedWaypoints.addAll(waypoints);
        }
    }

    private OsmTrack searchTrack(MatchedWaypoint startWp, MatchedWaypoint endWp, OsmTrack nearbyTrack, OsmTrack refTrack) {
        try {
            boolean calcBeeline = startWp.wpttype == 3;
            if (!calcBeeline) {
                return searchRoutedTrack(startWp, endWp, nearbyTrack, refTrack);
            }
            OsmPath path = this.routingContext.createPath(new OsmLink(null, startWp.crosspoint));
            return compileTrack(this.routingContext.createPath(path, new OsmLink(startWp.crosspoint, endWp.crosspoint), null, false), false);
        } finally {
            this.routingContext.restoreNogoList();
        }
    }

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 2 */
    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Unreachable block: B:92:0x018f
        	at jadx.core.dex.visitors.blocks.BlockProcessor.checkForUnreachableBlocks(BlockProcessor.java:132)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:58)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:50)
        */
    private btools.router.OsmTrack searchRoutedTrack(btools.mapaccess.MatchedWaypoint r19, btools.mapaccess.MatchedWaypoint r20, btools.router.OsmTrack r21, btools.router.OsmTrack r22) {
        /*
            Method dump skipped, instruction units count: 412
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: btools.router.RoutingEngine.searchRoutedTrack(btools.mapaccess.MatchedWaypoint, btools.mapaccess.MatchedWaypoint, btools.router.OsmTrack, btools.router.OsmTrack):btools.router.OsmTrack");
    }

    private void resetCache(boolean detailed) {
        if (hasInfo() && this.nodesCache != null) {
            logInfo("NodesCache status before reset=" + this.nodesCache.formatStatus());
        }
        long maxmem = ((long) this.routingContext.memoryclass) * RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE * RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE;
        this.nodesCache = new NodesCache(this.segmentDir, this.routingContext.expctxWay, this.routingContext.forceSecondaryData, maxmem, this.nodesCache, detailed);
        this.islandNodePairs.clearTempPairs();
    }

    private OsmPath getStartPath(OsmNode n1, OsmNode n2, MatchedWaypoint mwp, OsmNodeNamed endPos, boolean sameSegmentSearch) throws Throwable {
        if (endPos != null) {
            endPos.radius = 1.5d;
        }
        OsmPath p = getStartPath(n1, n2, new OsmNodeNamed(mwp.crosspoint), endPos, sameSegmentSearch);
        if (p != null && p.cost >= 0 && sameSegmentSearch && endPos != null && endPos.radius < 1.5d) {
            p.treedepth = 0;
        }
        return p;
    }

    private OsmPath getStartPath(OsmNode n1, OsmNode n2, OsmNodeNamed wp, OsmNodeNamed endPos, boolean sameSegmentSearch) throws Throwable {
        OsmTrack osmTrack;
        OsmPath bestPath;
        OsmLink bestLink;
        OsmLink startLink;
        OsmPath startPath;
        double minradius;
        OsmLink link;
        OsmLink startLink2;
        OsmLink osmLink;
        try {
            osmTrack = null;
            this.routingContext.setWaypoint(wp, sameSegmentSearch ? endPos : null, false);
            bestPath = null;
            bestLink = null;
            startLink = new OsmLink(null, n1);
            startPath = this.routingContext.createPath(startLink);
            startLink.addLinkHolder(startPath, null);
            minradius = 1.0E10d;
            link = n1.firstlink;
        } catch (Throwable th) {
            th = th;
        }
        while (true) {
            boolean z = true;
            if (link == null) {
                break;
            }
            OsmNode nextNode = link.getTarget(n1);
            if (nextNode.isHollow() || nextNode.firstlink == null || nextNode == n1 || nextNode != n2) {
                osmLink = bestLink;
                startLink2 = startLink;
            } else {
                startLink2 = startLink;
                try {
                    wp.radius = 1.5d;
                    RoutingContext routingContext = this.routingContext;
                    if (this.guideTrack == null) {
                        z = false;
                    }
                    OsmPath testPath = routingContext.createPath(startPath, link, osmTrack, z);
                    testPath.airdistance = endPos == null ? 0 : nextNode.calcDistance(endPos);
                    osmLink = bestLink;
                    if (wp.radius < minradius) {
                        bestPath = testPath;
                        double minradius2 = wp.radius;
                        bestLink = link;
                        minradius = minradius2;
                    }
                    link = link.getNext(n1);
                    startLink = startLink2;
                    osmTrack = null;
                } catch (Throwable th2) {
                    th = th2;
                }
            }
            bestLink = osmLink;
            link = link.getNext(n1);
            startLink = startLink2;
            osmTrack = null;
            th = th2;
            this.routingContext.unsetWaypoint();
            throw th;
        }
        OsmLink bestLink2 = bestLink;
        if (bestLink2 != null) {
            bestLink2.addLinkHolder(bestPath, n1);
        }
        if (bestPath != null) {
            bestPath.treedepth = 1;
        }
        this.routingContext.unsetWaypoint();
        return bestPath;
    }

    private OsmTrack findTrack(String operationName, MatchedWaypoint startWp, MatchedWaypoint endWp, OsmTrack costCuttingTrack, OsmTrack refTrack, boolean fastPartialRecalc) {
        try {
            List<OsmNode> wpts2 = new ArrayList<>();
            if (startWp != null) {
                wpts2.add(startWp.waypoint);
            }
            if (endWp != null) {
                wpts2.add(endWp.waypoint);
            }
            this.routingContext.cleanNogoList(wpts2);
            int i = 1;
            boolean detailed = this.guideTrack != null;
            resetCache(detailed);
            OsmNodesMap osmNodesMap = this.nodesCache.nodesMap;
            if (detailed) {
                i = 0;
            } else if (this.routingContext.considerTurnRestrictions) {
                i = 2;
            }
            osmNodesMap.cleanupMode = i;
            return _findTrack(operationName, startWp, endWp, costCuttingTrack, refTrack, fastPartialRecalc);
        } finally {
            this.routingContext.restoreNogoList();
            this.nodesCache.clean(false);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:109:0x0273, code lost:
    
        if (r11 >= r57.MAXNODES_ISLAND_CHECK) goto L115;
     */
    /* JADX WARN: Code restructure failed: missing block: B:111:0x027c, code lost:
    
        if (r57.islandNodePairs.getFreezeCount() < 5) goto L113;
     */
    /* JADX WARN: Code restructure failed: missing block: B:114:0x0284, code lost:
    
        throw new btools.router.RoutingIslandException();
     */
    /* JADX WARN: Code restructure failed: missing block: B:115:0x0285, code lost:
    
        return null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x02d5, code lost:
    
        r12 = r31;
        r13 = r32;
        r8 = r33;
        r14 = r35;
        r11 = r39;
     */
    /* JADX WARN: Code restructure failed: missing block: B:183:0x045c, code lost:
    
        r12 = r31;
        r13 = r32;
        r8 = r33;
        r14 = r35;
        r11 = r39;
        r5 = r40;
        r4 = r41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:224:0x0554, code lost:
    
        r5 = r13;
        r12 = r31;
        r13 = r32;
        r8 = r33;
        r14 = r35;
        r4 = r41;
     */
    /* JADX WARN: Code restructure failed: missing block: B:310:0x06ed, code lost:
    
        r10 = r1 == true ? 1 : 0;
        r44 = r13;
        r49 = r14;
        r15 = r28;
        r28 = r11;
     */
    /* JADX WARN: Code restructure failed: missing block: B:654:?, code lost:
    
        return null;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:213:0x051d A[Catch: all -> 0x0528, TRY_ENTER, TryCatch #5 {all -> 0x0528, blocks: (B:198:0x04ad, B:200:0x04b1, B:202:0x04d9, B:204:0x04ea, B:205:0x04f8, B:206:0x04ff, B:213:0x051d, B:215:0x0525), top: B:531:0x04ad }] */
    /* JADX WARN: Removed duplicated region for block: B:439:0x0969 A[Catch: all -> 0x0970, TryCatch #10 {all -> 0x0970, blocks: (B:374:0x084e, B:401:0x08eb, B:432:0x094a, B:433:0x094f, B:439:0x0969, B:441:0x096f, B:451:0x0993, B:462:0x09b8), top: B:540:0x084e }] */
    /* JADX WARN: Removed duplicated region for block: B:523:0x0553 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:564:0x049a A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:611:0x056e A[SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r0v124, types: [btools.router.RoutingContext] */
    /* JADX WARN: Type inference failed for: r10v17 */
    /* JADX WARN: Type inference failed for: r10v18, types: [btools.mapaccess.OsmLink] */
    /* JADX WARN: Type inference failed for: r10v19 */
    /* JADX WARN: Type inference failed for: r10v20 */
    /* JADX WARN: Type inference failed for: r10v21 */
    /* JADX WARN: Type inference failed for: r10v22 */
    /* JADX WARN: Type inference failed for: r10v23 */
    /* JADX WARN: Type inference failed for: r10v30 */
    /* JADX WARN: Type inference failed for: r10v31 */
    /* JADX WARN: Type inference failed for: r10v32 */
    /* JADX WARN: Type inference failed for: r10v33 */
    /* JADX WARN: Type inference failed for: r10v34 */
    /* JADX WARN: Type inference failed for: r10v38 */
    /* JADX WARN: Type inference failed for: r10v39 */
    /* JADX WARN: Type inference failed for: r10v40 */
    /* JADX WARN: Type inference failed for: r10v41 */
    /* JADX WARN: Type inference failed for: r10v42 */
    /* JADX WARN: Type inference failed for: r10v43 */
    /* JADX WARN: Type inference failed for: r10v51 */
    /* JADX WARN: Type inference failed for: r10v53 */
    /* JADX WARN: Type inference failed for: r10v66 */
    /* JADX WARN: Type inference failed for: r13v28, types: [btools.router.RoutingContext] */
    /* JADX WARN: Type inference failed for: r1v19 */
    /* JADX WARN: Type inference failed for: r1v20 */
    /* JADX WARN: Type inference failed for: r1v21 */
    /* JADX WARN: Type inference failed for: r1v22 */
    /* JADX WARN: Type inference failed for: r1v23, types: [int] */
    /* JADX WARN: Type inference failed for: r1v28 */
    /* JADX WARN: Type inference failed for: r1v29 */
    /* JADX WARN: Type inference failed for: r1v30 */
    /* JADX WARN: Type inference failed for: r1v31 */
    /* JADX WARN: Type inference failed for: r50v0 */
    /* JADX WARN: Type inference failed for: r50v1 */
    /* JADX WARN: Type inference failed for: r50v10 */
    /* JADX WARN: Type inference failed for: r50v14 */
    /* JADX WARN: Type inference failed for: r50v15 */
    /* JADX WARN: Type inference failed for: r50v2 */
    /* JADX WARN: Type inference failed for: r50v3 */
    /* JADX WARN: Type inference failed for: r50v4 */
    /* JADX WARN: Type inference failed for: r50v5 */
    /* JADX WARN: Type inference failed for: r50v6 */
    /* JADX WARN: Type inference failed for: r5v21 */
    /* JADX WARN: Type inference failed for: r5v25 */
    /* JADX WARN: Type inference failed for: r5v26 */
    /* JADX WARN: Type inference failed for: r5v27 */
    /* JADX WARN: Type inference failed for: r5v30, types: [btools.mapaccess.OsmLink] */
    /* JADX WARN: Type inference failed for: r5v31 */
    /* JADX WARN: Type inference failed for: r5v32, types: [btools.mapaccess.OsmLink] */
    /* JADX WARN: Type inference failed for: r5v33 */
    /* JADX WARN: Type inference failed for: r5v34 */
    /* JADX WARN: Type inference failed for: r5v35 */
    /* JADX WARN: Type inference failed for: r5v37, types: [btools.mapaccess.OsmLink] */
    /* JADX WARN: Type inference failed for: r5v38 */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private OsmTrack _findTrack(String str, MatchedWaypoint matchedWaypoint, MatchedWaypoint matchedWaypoint2, OsmTrack osmTrack, OsmTrack osmTrack2, boolean z) throws Throwable {
        long j;
        OsmNode osmNode;
        OsmNodeNamed osmNodeNamed;
        int i;
        long j2;
        OsmPath osmPathPopLowestKeyValue;
        int i2;
        long j3;
        boolean z2;
        int i3;
        OsmPath osmPath;
        int i4;
        OsmTrack osmTrack3;
        OsmLink link;
        boolean z3;
        ArrayList arrayList;
        OsmPath osmPath2;
        long j4;
        long j5;
        OsmNode osmNode2;
        boolean z4;
        OsmNode osmNode3;
        OsmNodeNamed osmNodeNamed2;
        int i5;
        int i6;
        boolean z5;
        ?? r10;
        OsmNode osmNode4;
        int i7;
        int size;
        int i8;
        ?? r50;
        boolean z6;
        OsmTrack osmTrack4;
        OsmLinkHolder osmLinkHolder;
        long j6;
        ?? r5;
        int i9;
        ?? r502;
        OsmPath osmPath3;
        OsmPath osmPath4;
        double d;
        long j7;
        Throwable th;
        OsmPrePath osmPrePathCreatePrePath;
        OsmPath osmPath5;
        long j8;
        boolean z7 = this.guideTrack != null;
        int i10 = this.guideTrack != null ? this.guideTrack.cost + 5000 : Http2Connection.DEGRADED_PONG_TIMEOUT_NS;
        int i11 = Http2Connection.DEGRADED_PONG_TIMEOUT_NS;
        logInfo("findtrack with airDistanceCostFactor=" + this.airDistanceCostFactor);
        if (osmTrack != null) {
            logInfo("costCuttingTrack.cost=" + osmTrack.cost);
        }
        this.matchPath = null;
        long idFromPos = matchedWaypoint.node1.getIdFromPos();
        long idFromPos2 = matchedWaypoint.node2.getIdFromPos();
        long idFromPos3 = matchedWaypoint2 == null ? -1L : matchedWaypoint2.node1.getIdFromPos();
        long idFromPos4 = matchedWaypoint2 != null ? matchedWaypoint2.node2.getIdFromPos() : -1L;
        boolean z8 = false;
        OsmNode graphNode = this.nodesCache.getGraphNode(matchedWaypoint.node1);
        OsmNode graphNode2 = this.nodesCache.getGraphNode(matchedWaypoint.node2);
        if (matchedWaypoint2 != null) {
            OsmNode graphNode3 = this.nodesCache.getGraphNode(matchedWaypoint2.node1);
            OsmNode graphNode4 = this.nodesCache.getGraphNode(matchedWaypoint2.node2);
            this.nodesCache.nodesMap.endNode1 = graphNode3;
            this.nodesCache.nodesMap.endNode2 = graphNode4;
            j = idFromPos2;
            OsmNodeNamed osmNodeNamed3 = new OsmNodeNamed(matchedWaypoint2.crosspoint);
            z8 = (graphNode == graphNode3 && graphNode2 == graphNode4) || (graphNode == graphNode4 && graphNode2 == graphNode3);
            osmNode = graphNode3;
            osmNodeNamed = osmNodeNamed3;
        } else {
            j = idFromPos2;
            osmNode = null;
            osmNodeNamed = null;
        }
        if (!this.nodesCache.obtainNonHollowNode(graphNode)) {
            return null;
        }
        this.nodesCache.expandHollowLinkTargets(graphNode);
        if (!this.nodesCache.obtainNonHollowNode(graphNode2)) {
            return null;
        }
        this.nodesCache.expandHollowLinkTargets(graphNode2);
        this.routingContext.startDirectionValid = this.routingContext.forceUseStartDirection || z;
        this.routingContext.startDirectionValid &= (this.routingContext.startDirection == null || this.routingContext.inverseDirection) ? false : true;
        if (this.routingContext.startDirectionValid) {
            logInfo("using start direction " + this.routingContext.startDirection);
        }
        long j9 = j;
        boolean z9 = z7;
        long j10 = idFromPos;
        OsmNodeNamed osmNodeNamed4 = osmNodeNamed;
        OsmPath startPath = getStartPath(graphNode, graphNode2, matchedWaypoint, osmNodeNamed, z8);
        OsmPath startPath2 = getStartPath(graphNode2, graphNode, matchedWaypoint, osmNodeNamed4, z8);
        if (osmTrack != null) {
            OsmPathElement link2 = osmTrack.getLink(j10, j9);
            if (link2 != null) {
                logInfo("initialMatch pe1.cost=" + link2.cost);
                int i12 = startPath.cost - link2.cost;
                if (i12 < 0) {
                    i12 = 0;
                }
                if (i12 < 1000000000) {
                    i11 = i12;
                }
            }
            OsmPathElement link3 = osmTrack.getLink(j9, j10);
            if (link3 != null) {
                logInfo("initialMatch pe2.cost=" + link3.cost);
                int i13 = startPath2.cost - link3.cost;
                if (i13 < 0) {
                    i13 = 0;
                }
                if (i13 < i11) {
                    i11 = i13;
                }
            }
            if (i11 < 1000000000) {
                logInfo("firstMatchCost from initial match=" + i11);
            }
            i = i11;
        } else {
            i = 1000000000;
        }
        if (startPath == null || startPath2 == null) {
            return null;
        }
        synchronized (this.openSet) {
            try {
                this.openSet.clear();
                addToOpenset(startPath);
                addToOpenset(startPath2);
            } catch (Throwable th2) {
                th = th2;
                while (true) {
                    try {
                        throw th;
                    } catch (Throwable th3) {
                        th = th3;
                    }
                }
            }
        }
        ArrayList<OsmPath> arrayList2 = new ArrayList(4096);
        int i14 = i;
        boolean z10 = false;
        ?? r1 = i10;
        int i15 = 0;
        boolean z11 = z;
        boolean z12 = false;
        while (!this.terminated) {
            OsmNode osmNode5 = graphNode;
            OsmNode osmNode6 = graphNode2;
            if (this.maxRunningTime > 0) {
                if (this.matchPath == null && z11) {
                    j2 = j9;
                    j8 = this.maxRunningTime / 3;
                } else {
                    j2 = j9;
                    j8 = this.maxRunningTime;
                }
                if (System.currentTimeMillis() - this.startTime > j8) {
                    throw new IllegalArgumentException(str + " timeout after " + (j8 / 1000) + " seconds");
                }
            } else {
                j2 = j9;
            }
            synchronized (this.openSet) {
                try {
                    osmPathPopLowestKeyValue = this.openSet.popLowestKeyValue();
                } catch (Throwable th4) {
                    th = th4;
                }
                if (osmPathPopLowestKeyValue == null) {
                    try {
                        if (arrayList2.isEmpty()) {
                            try {
                            } catch (Throwable th5) {
                                th = th5;
                            }
                        } else {
                            Iterator it = arrayList2.iterator();
                            while (it.hasNext()) {
                                OsmPath osmPath6 = (OsmPath) it.next();
                                Iterator it2 = it;
                                long j11 = j10;
                                try {
                                    int i16 = i15;
                                    try {
                                        this.openSet.add(osmPath6.cost + ((int) (((double) osmPath6.airdistance) * this.airDistanceCostFactor)), osmPath6);
                                        it = it2;
                                        j10 = j11;
                                        i15 = i16;
                                    } catch (Throwable th6) {
                                        th = th6;
                                    }
                                } catch (Throwable th7) {
                                    th = th7;
                                }
                            }
                            int i17 = i15;
                            long j12 = j10;
                            arrayList2.clear();
                            z12 = false;
                            z10 = true;
                        }
                    } catch (Throwable th8) {
                        th = th8;
                    }
                    while (true) {
                        try {
                            throw th;
                        } catch (Throwable th9) {
                            th = th9;
                        }
                    }
                } else {
                    i2 = i15;
                    j3 = j10;
                    try {
                        if (osmPathPopLowestKeyValue.airdistance != -1) {
                            try {
                                try {
                                    if (this.directWeaving) {
                                        try {
                                            if (this.nodesCache.hasHollowLinkTargets(osmPathPopLowestKeyValue.getTargetNode())) {
                                                if (z12 || this.nodesCache.nodesMap.isInMemoryBounds(this.openSet.getSize(), false)) {
                                                    z2 = z12;
                                                    i3 = i14;
                                                    osmPath5 = osmPathPopLowestKeyValue;
                                                } else {
                                                    int i18 = this.nodesCache.nodesMap.nodesCreated;
                                                    int size2 = this.openSet.getSize();
                                                    this.nodesCache.nodesMap.collectOutreachers();
                                                    while (true) {
                                                        OsmPath osmPathPopLowestKeyValue2 = this.openSet.popLowestKeyValue();
                                                        if (osmPathPopLowestKeyValue2 == null) {
                                                            break;
                                                        }
                                                        boolean z13 = z12;
                                                        int i19 = i14;
                                                        OsmPath osmPath7 = osmPathPopLowestKeyValue;
                                                        if (osmPathPopLowestKeyValue2.airdistance != -1 && this.nodesCache.nodesMap.canEscape(osmPathPopLowestKeyValue2.getTargetNode())) {
                                                            arrayList2.add(osmPathPopLowestKeyValue2);
                                                        }
                                                        osmPathPopLowestKeyValue = osmPath7;
                                                        i14 = i19;
                                                        z12 = z13;
                                                    }
                                                    this.nodesCache.nodesMap.clearTemp();
                                                    for (OsmPath osmPath8 : arrayList2) {
                                                        z2 = z12;
                                                        i3 = i14;
                                                        OsmPath osmPath9 = osmPathPopLowestKeyValue;
                                                        try {
                                                            this.openSet.add(osmPath8.cost + ((int) (((double) osmPath8.airdistance) * this.airDistanceCostFactor)), osmPath8);
                                                            osmPathPopLowestKeyValue = osmPath9;
                                                            i14 = i3;
                                                            z12 = z2;
                                                        } catch (Throwable th10) {
                                                            th = th10;
                                                        }
                                                    }
                                                    z2 = z12;
                                                    i3 = i14;
                                                    osmPath5 = osmPathPopLowestKeyValue;
                                                    arrayList2.clear();
                                                    logInfo("collected, nodes/paths before=" + i18 + "/" + size2 + " after=" + this.nodesCache.nodesMap.nodesCreated + "/" + this.openSet.getSize() + " maxTotalCost=" + (r1 == true ? 1 : 0));
                                                    if (!this.nodesCache.nodesMap.isInMemoryBounds(this.openSet.getSize(), true)) {
                                                        if (r1 < 1000000000 || z10 || z11) {
                                                            throw new IllegalArgumentException("memory limit reached");
                                                        }
                                                        try {
                                                            logInfo("************************ memory limit reached, enabled memory panic mode *************************");
                                                            z2 = true;
                                                        } catch (Throwable th11) {
                                                            th = th11;
                                                        }
                                                    }
                                                }
                                                if (z2) {
                                                    arrayList2.add(osmPath5);
                                                } else {
                                                    osmPath = osmPath5;
                                                    z10 = false;
                                                    if (z11) {
                                                        try {
                                                            if (this.matchPath != null) {
                                                                i4 = i3;
                                                                if (osmPath.cost > ((long) i4) * 30) {
                                                                    osmTrack3 = osmTrack;
                                                                    try {
                                                                        if (!osmTrack3.isDirty) {
                                                                            logInfo("early exit: firstMatchCost=" + i4 + " path.cost=" + osmPath.cost);
                                                                            if (osmPath.cost <= (r1 == true ? 1 : 0) / 2 || System.currentTimeMillis() - this.startTime >= this.maxRunningTime / 3) {
                                                                                throw new IllegalArgumentException("early exit for a close recalc");
                                                                            }
                                                                            logInfo("early exit supressed, running for completion, resetting timeout");
                                                                            this.startTime = System.currentTimeMillis();
                                                                            z11 = false;
                                                                        }
                                                                    } catch (Throwable th12) {
                                                                        th = th12;
                                                                    }
                                                                } else {
                                                                    osmTrack3 = osmTrack;
                                                                }
                                                            }
                                                            if (this.nodeLimit > 0) {
                                                                int i20 = this.nodeLimit - 1;
                                                                this.nodeLimit = i20;
                                                                if (i20 == 0) {
                                                                    return null;
                                                                }
                                                            }
                                                            i15 = i2 + 1;
                                                            this.linksProcessed++;
                                                            link = osmPath.getLink();
                                                            OsmNode sourceNode = osmPath.getSourceNode();
                                                            OsmNode targetNode = osmPath.getTargetNode();
                                                            if (link.isLinkUnused()) {
                                                                try {
                                                                } catch (Throwable th13) {
                                                                    th = th13;
                                                                }
                                                            } else {
                                                                long idFromPos5 = targetNode.getIdFromPos();
                                                                long idFromPos6 = sourceNode.getIdFromPos();
                                                                if (osmPath.didEnterDestinationArea()) {
                                                                    z3 = z11;
                                                                    arrayList = arrayList2;
                                                                    osmPath2 = osmPath;
                                                                    j4 = idFromPos5;
                                                                    j5 = idFromPos6;
                                                                } else {
                                                                    try {
                                                                        z3 = z11;
                                                                        arrayList = arrayList2;
                                                                        osmPath2 = osmPath;
                                                                        j4 = idFromPos5;
                                                                        j5 = idFromPos6;
                                                                        try {
                                                                            this.islandNodePairs.addTempPair(j5, j4);
                                                                        } catch (Throwable th14) {
                                                                            th = th14;
                                                                        }
                                                                    } catch (Throwable th15) {
                                                                        th = th15;
                                                                    }
                                                                }
                                                                OsmPath osmPath10 = osmPath2;
                                                                try {
                                                                    ArrayList arrayList3 = arrayList;
                                                                    if (osmPath10.treedepth != 1) {
                                                                        try {
                                                                            if (osmPath10.treedepth == 0) {
                                                                                try {
                                                                                    osmPath10.treedepth = 1;
                                                                                } catch (Throwable th16) {
                                                                                    th = th16;
                                                                                }
                                                                            }
                                                                            if ((j5 == idFromPos3 && j4 == idFromPos4) || (j5 == idFromPos4 && j4 == idFromPos3)) {
                                                                                logInfo("found track at cost " + osmPath10.cost + " nodesVisited = " + i15);
                                                                                z4 = z9;
                                                                                OsmTrack osmTrackCompileTrack = compileTrack(osmPath10, z4);
                                                                                osmTrackCompileTrack.showspeed = this.routingContext.showspeed;
                                                                                osmTrackCompileTrack.showSpeedProfile = this.routingContext.showSpeedProfile;
                                                                                return osmTrackCompileTrack;
                                                                            }
                                                                            osmNode2 = targetNode;
                                                                            z4 = z9;
                                                                            if (osmTrack3 != null) {
                                                                                try {
                                                                                    OsmPathElement link4 = osmTrack3.getLink(j5, j4);
                                                                                    if (link4 != null) {
                                                                                        int i21 = osmPath10.originElement == null ? 0 : osmPath10.originElement.cost;
                                                                                        int i22 = (osmPath10.cost - i21) - link4.cost;
                                                                                        if (i22 > 0) {
                                                                                            i21 += i22;
                                                                                        }
                                                                                        if (i21 < i4) {
                                                                                            i4 = i21;
                                                                                        }
                                                                                        int iElevationCorrection = osmPath10.cost + osmPath10.elevationCorrection() + (osmTrack3.cost - link4.cost);
                                                                                        if (iElevationCorrection <= r1) {
                                                                                            this.matchPath = OsmPathElement.create(osmPath10);
                                                                                        }
                                                                                        if (iElevationCorrection < r1) {
                                                                                            logInfo("maxcost " + (r1 == true ? 1 : 0) + " -> " + iElevationCorrection);
                                                                                            r1 = iElevationCorrection;
                                                                                        }
                                                                                    }
                                                                                } catch (Throwable th17) {
                                                                                    th = th17;
                                                                                }
                                                                            }
                                                                        } catch (Throwable th18) {
                                                                            th = th18;
                                                                        }
                                                                    } else {
                                                                        osmNode2 = targetNode;
                                                                        z4 = z9;
                                                                    }
                                                                    try {
                                                                        OsmLinkHolder firstLinkHolder = link.getFirstLinkHolder(sourceNode);
                                                                        OsmLinkHolder osmLinkHolder2 = firstLinkHolder;
                                                                        while (firstLinkHolder != null) {
                                                                            ((OsmPath) firstLinkHolder).airdistance = -1;
                                                                            firstLinkHolder = firstLinkHolder.getNextForLink();
                                                                        }
                                                                        if (osmPath10.treedepth > 1) {
                                                                            boolean zIsBidirectional = link.isBidirectional();
                                                                            sourceNode.unlinkLink(link);
                                                                            if (zIsBidirectional) {
                                                                                osmNode3 = osmNode2;
                                                                                if (link.getFirstLinkHolder(osmNode3) == null && !this.routingContext.considerTurnRestrictions) {
                                                                                    osmNode3.unlinkLink(link);
                                                                                }
                                                                            } else {
                                                                                osmNode3 = osmNode2;
                                                                            }
                                                                        } else {
                                                                            osmNode3 = osmNode2;
                                                                        }
                                                                        int i23 = 100;
                                                                        if (osmPath10.cost + osmPath10.airdistance <= (r1 == true ? 1 : 0) + 100) {
                                                                            this.nodesCache.nodesMap.currentMaxCost = r1 == true ? 1 : 0;
                                                                            this.nodesCache.nodesMap.currentPathCost = osmPath10.cost;
                                                                            osmNodeNamed2 = osmNodeNamed4;
                                                                            try {
                                                                                this.nodesCache.nodesMap.destination = osmNodeNamed2;
                                                                                this.routingContext.firstPrePath = null;
                                                                                OsmLink next = osmNode3.firstlink;
                                                                                while (next != null) {
                                                                                    try {
                                                                                        OsmNode target = next.getTarget(osmNode3);
                                                                                        int i24 = i15;
                                                                                        try {
                                                                                            if (this.nodesCache.obtainNonHollowNode(target) && target.firstlink != null && target != sourceNode && (osmPrePathCreatePrePath = this.routingContext.createPrePath(osmPath10, next)) != null) {
                                                                                                osmPrePathCreatePrePath.next = this.routingContext.firstPrePath;
                                                                                                this.routingContext.firstPrePath = osmPrePathCreatePrePath;
                                                                                            }
                                                                                            next = next.getNext(osmNode3);
                                                                                            i15 = i24;
                                                                                        } catch (Throwable th19) {
                                                                                            th = th19;
                                                                                        }
                                                                                    } catch (Throwable th20) {
                                                                                        th = th20;
                                                                                    }
                                                                                }
                                                                                i5 = i15;
                                                                                try {
                                                                                    ?? next2 = osmNode3.firstlink;
                                                                                    while (next2 != 0) {
                                                                                        OsmNode target2 = next2.getTarget(osmNode3);
                                                                                        if (!this.nodesCache.obtainNonHollowNode(target2)) {
                                                                                            osmLinkHolder = osmLinkHolder2;
                                                                                            j6 = j4;
                                                                                            r5 = next2;
                                                                                            osmNode4 = sourceNode;
                                                                                            i7 = i4;
                                                                                            z6 = z4;
                                                                                            next2 = r1;
                                                                                        } else if (target2.firstlink == null) {
                                                                                            osmLinkHolder = osmLinkHolder2;
                                                                                            j6 = j4;
                                                                                            r5 = next2;
                                                                                            osmNode4 = sourceNode;
                                                                                            i7 = i4;
                                                                                            z6 = z4;
                                                                                            next2 = r1;
                                                                                        } else if (target2 == sourceNode) {
                                                                                            osmLinkHolder = osmLinkHolder2;
                                                                                            j6 = j4;
                                                                                            r5 = next2;
                                                                                            osmNode4 = sourceNode;
                                                                                            i7 = i4;
                                                                                            z6 = z4;
                                                                                            next2 = r1;
                                                                                        } else {
                                                                                            if (this.guideTrack != null) {
                                                                                                try {
                                                                                                    int i25 = osmPath10.treedepth + 1;
                                                                                                    osmNode4 = sourceNode;
                                                                                                    if (i25 >= this.guideTrack.nodes.size()) {
                                                                                                        osmLinkHolder = osmLinkHolder2;
                                                                                                        j6 = j4;
                                                                                                        r5 = next2;
                                                                                                        i7 = i4;
                                                                                                        z6 = z4;
                                                                                                        next2 = r1;
                                                                                                    } else {
                                                                                                        List<OsmPathElement> list = this.guideTrack.nodes;
                                                                                                        i7 = i4;
                                                                                                        try {
                                                                                                            if (this.routingContext.inverseRouting) {
                                                                                                                try {
                                                                                                                    size = (this.guideTrack.nodes.size() - 1) - i25;
                                                                                                                } catch (Throwable th21) {
                                                                                                                    th = th21;
                                                                                                                }
                                                                                                            } else {
                                                                                                                size = i25;
                                                                                                            }
                                                                                                            OsmPathElement osmPathElement = list.get(size);
                                                                                                            long idFromPos7 = target2.getIdFromPos();
                                                                                                            if (idFromPos7 == osmPathElement.getIdFromPos()) {
                                                                                                                i8 = i23;
                                                                                                                r50 = next2;
                                                                                                                z6 = z4;
                                                                                                                osmTrack4 = osmTrack2;
                                                                                                            } else if (this.routingContext.turnInstructionMode > 0) {
                                                                                                                try {
                                                                                                                    OsmPath osmPathCreatePath = this.routingContext.createPath(osmPath10, next2, osmTrack2, true);
                                                                                                                    z6 = z4;
                                                                                                                    if (osmPathCreatePath.cost < 0.0d || idFromPos7 == j3 || idFromPos7 == j2) {
                                                                                                                        i9 = i23;
                                                                                                                        r502 = next2;
                                                                                                                    } else {
                                                                                                                        try {
                                                                                                                            r502 = next2;
                                                                                                                            i9 = i23;
                                                                                                                            this.guideTrack.registerDetourForId(osmNode3.getIdFromPos(), OsmPathElement.create(osmPathCreatePath));
                                                                                                                        } catch (Throwable th22) {
                                                                                                                            th = th22;
                                                                                                                        }
                                                                                                                    }
                                                                                                                    next2 = r1;
                                                                                                                    osmLinkHolder = osmLinkHolder2;
                                                                                                                    j6 = j4;
                                                                                                                    r5 = r502;
                                                                                                                    i23 = i9;
                                                                                                                } catch (Throwable th23) {
                                                                                                                    th = th23;
                                                                                                                    while (true) {
                                                                                                                        throw th;
                                                                                                                    }
                                                                                                                }
                                                                                                            } else {
                                                                                                                ?? r503 = next2;
                                                                                                                z6 = z4;
                                                                                                                next2 = r1;
                                                                                                                osmLinkHolder = osmLinkHolder2;
                                                                                                                j6 = j4;
                                                                                                                r5 = r503;
                                                                                                            }
                                                                                                        } catch (Throwable th24) {
                                                                                                            th = th24;
                                                                                                        }
                                                                                                    }
                                                                                                } catch (Throwable th25) {
                                                                                                    th = th25;
                                                                                                }
                                                                                            } else {
                                                                                                i8 = i23;
                                                                                                r50 = next2;
                                                                                                osmNode4 = sourceNode;
                                                                                                i7 = i4;
                                                                                                z6 = z4;
                                                                                                osmTrack4 = osmTrack2;
                                                                                            }
                                                                                            boolean z14 = false;
                                                                                            try {
                                                                                                long idFromPos8 = target2.getIdFromPos();
                                                                                                if ((j4 == idFromPos3 || j4 == idFromPos4) && (idFromPos8 == idFromPos3 || idFromPos8 == idFromPos4)) {
                                                                                                    z14 = true;
                                                                                                }
                                                                                                OsmLinkHolder nextForLink = osmLinkHolder2;
                                                                                                OsmPath osmPath11 = null;
                                                                                                ?? r504 = r50;
                                                                                                while (nextForLink != null) {
                                                                                                    OsmPath osmPath12 = (OsmPath) nextForLink;
                                                                                                    if (z14) {
                                                                                                        j7 = j4;
                                                                                                        try {
                                                                                                            osmNodeNamed2.radius = 1.5d;
                                                                                                            this.routingContext.setWaypoint(osmNodeNamed2, true);
                                                                                                        } catch (Throwable th26) {
                                                                                                            th = th26;
                                                                                                            if (z14) {
                                                                                                                this.routingContext.unsetWaypoint();
                                                                                                            }
                                                                                                            throw th;
                                                                                                        }
                                                                                                    } else {
                                                                                                        j7 = j4;
                                                                                                    }
                                                                                                    try {
                                                                                                        ?? r52 = r504;
                                                                                                        OsmLinkHolder osmLinkHolder3 = osmLinkHolder2;
                                                                                                        try {
                                                                                                            OsmPath osmPathCreatePath2 = this.routingContext.createPath(osmPath12, r52, osmTrack4, this.guideTrack != null);
                                                                                                            if (osmPathCreatePath2.cost >= 0) {
                                                                                                                if (osmPath11 != null) {
                                                                                                                    try {
                                                                                                                        if (osmPathCreatePath2.cost < osmPath11.cost) {
                                                                                                                        }
                                                                                                                    } catch (Throwable th27) {
                                                                                                                        th = th27;
                                                                                                                        if (z14) {
                                                                                                                        }
                                                                                                                        throw th;
                                                                                                                    }
                                                                                                                }
                                                                                                                if (osmPathCreatePath2.sourceNode.getIdFromPos() != osmPathCreatePath2.targetNode.getIdFromPos()) {
                                                                                                                    osmPath11 = osmPathCreatePath2;
                                                                                                                }
                                                                                                            }
                                                                                                            if (z14) {
                                                                                                                this.routingContext.unsetWaypoint();
                                                                                                            }
                                                                                                            nextForLink = nextForLink.getNextForLink();
                                                                                                            r504 = r52;
                                                                                                            osmLinkHolder2 = osmLinkHolder3;
                                                                                                            j4 = j7;
                                                                                                        } catch (Throwable th28) {
                                                                                                            th = th28;
                                                                                                        }
                                                                                                    } catch (Throwable th29) {
                                                                                                        th = th29;
                                                                                                    }
                                                                                                }
                                                                                                osmLinkHolder = osmLinkHolder2;
                                                                                                j6 = j4;
                                                                                                r5 = r504;
                                                                                                if (osmPath11 != null) {
                                                                                                    osmPath11.airdistance = z14 ? 0 : target2.calcDistance(osmNodeNamed2);
                                                                                                    if (this.boundary == null || this.boundary.isInBoundary(target2, osmPath11.cost)) {
                                                                                                        if (z14) {
                                                                                                            osmPath3 = osmPath11;
                                                                                                            i23 = i8;
                                                                                                            next2 = r1;
                                                                                                        } else {
                                                                                                            double d2 = osmPath11.cost + osmPath11.airdistance;
                                                                                                            if (this.lastAirDistanceCostFactor != 0.0d) {
                                                                                                                osmPath3 = osmPath11;
                                                                                                                d = ((double) r1) * this.lastAirDistanceCostFactor;
                                                                                                            } else {
                                                                                                                osmPath3 = osmPath11;
                                                                                                                d = (double) r1;
                                                                                                            }
                                                                                                            next2 = r1;
                                                                                                            i23 = i8;
                                                                                                            next2 = next2;
                                                                                                            if (d2 <= d + ((double) i23)) {
                                                                                                            }
                                                                                                        }
                                                                                                        try {
                                                                                                            OsmLinkHolder firstLinkHolder2 = r5.getFirstLinkHolder(osmNode3);
                                                                                                            while (true) {
                                                                                                                if (firstLinkHolder2 == null) {
                                                                                                                    osmPath4 = osmPath3;
                                                                                                                    break;
                                                                                                                }
                                                                                                                OsmPath osmPath13 = (OsmPath) firstLinkHolder2;
                                                                                                                if (osmPath13.airdistance != -1) {
                                                                                                                    osmPath4 = osmPath3;
                                                                                                                    if (osmPath4.definitlyWorseThan(osmPath13)) {
                                                                                                                        break;
                                                                                                                    }
                                                                                                                } else {
                                                                                                                    osmPath4 = osmPath3;
                                                                                                                }
                                                                                                                firstLinkHolder2 = firstLinkHolder2.getNextForLink();
                                                                                                                osmPath3 = osmPath4;
                                                                                                            }
                                                                                                            if (firstLinkHolder2 == null) {
                                                                                                                osmPath4.treedepth = osmPath10.treedepth + 1;
                                                                                                                r5.addLinkHolder(osmPath4, osmNode3);
                                                                                                                addToOpenset(osmPath4);
                                                                                                            }
                                                                                                        } catch (Throwable th30) {
                                                                                                            th = th30;
                                                                                                        }
                                                                                                    } else {
                                                                                                        i23 = i8;
                                                                                                        next2 = r1;
                                                                                                    }
                                                                                                } else {
                                                                                                    i23 = i8;
                                                                                                    next2 = r1;
                                                                                                }
                                                                                            } catch (Throwable th31) {
                                                                                                th = th31;
                                                                                            }
                                                                                        }
                                                                                        r1 = next2;
                                                                                        sourceNode = osmNode4;
                                                                                        i4 = i7;
                                                                                        osmLinkHolder2 = osmLinkHolder;
                                                                                        j4 = j6;
                                                                                        z4 = z6;
                                                                                        next2 = r5.getNext(osmNode3);
                                                                                    }
                                                                                    i6 = i4;
                                                                                    z5 = z4;
                                                                                    r10 = r1;
                                                                                } catch (Throwable th32) {
                                                                                    th = th32;
                                                                                    boolean z15 = r1 == true ? 1 : 0;
                                                                                }
                                                                            } catch (Throwable th33) {
                                                                                th = th33;
                                                                                boolean z16 = r1 == true ? 1 : 0;
                                                                            }
                                                                        }
                                                                        r1 = r10;
                                                                        z11 = z3;
                                                                        i15 = i5;
                                                                        graphNode = osmNode5;
                                                                        graphNode2 = osmNode6;
                                                                        j9 = j2;
                                                                        z10 = false;
                                                                        arrayList2 = arrayList3;
                                                                        z12 = z2;
                                                                        i14 = i6;
                                                                        z9 = z5;
                                                                        osmNodeNamed4 = osmNodeNamed2;
                                                                        j10 = j3;
                                                                    } catch (Throwable th34) {
                                                                        th = th34;
                                                                        boolean z17 = r1 == true ? 1 : 0;
                                                                    }
                                                                } catch (Throwable th35) {
                                                                    th = th35;
                                                                }
                                                            }
                                                        } catch (Throwable th36) {
                                                            th = th36;
                                                        }
                                                    }
                                                    osmTrack3 = osmTrack;
                                                    i4 = i3;
                                                    if (this.nodeLimit > 0) {
                                                    }
                                                    i15 = i2 + 1;
                                                    this.linksProcessed++;
                                                    link = osmPath.getLink();
                                                    OsmNode sourceNode2 = osmPath.getSourceNode();
                                                    OsmNode targetNode2 = osmPath.getTargetNode();
                                                    if (link.isLinkUnused()) {
                                                    }
                                                }
                                            }
                                        } catch (Throwable th37) {
                                            th = th37;
                                        }
                                    }
                                    this.linksProcessed++;
                                    link = osmPath.getLink();
                                    OsmNode sourceNode22 = osmPath.getSourceNode();
                                    OsmNode targetNode22 = osmPath.getTargetNode();
                                    if (link.isLinkUnused()) {
                                    }
                                } catch (Throwable th38) {
                                    th = th38;
                                }
                                if (this.nodeLimit > 0) {
                                }
                                i15 = i2 + 1;
                            } catch (Throwable th39) {
                                th = th39;
                            }
                            z2 = z12;
                            i3 = i14;
                            osmPath = osmPathPopLowestKeyValue;
                            z10 = false;
                            if (z11) {
                            }
                            osmTrack3 = osmTrack;
                            i4 = i3;
                        }
                    } catch (Throwable th40) {
                        th = th40;
                    }
                }
            }
            graphNode = osmNode5;
            graphNode2 = osmNode6;
            j9 = j2;
            j10 = j3;
            i15 = i2;
        }
        throw new IllegalArgumentException("operation killed by thread-priority-watchdog after " + ((System.currentTimeMillis() - this.startTime) / 1000) + " seconds");
    }

    private void addToOpenset(OsmPath path) {
        if (path.cost >= 0) {
            this.openSet.add(path.cost + ((int) (((double) path.airdistance) * this.airDistanceCostFactor)), path);
        }
    }

    private OsmTrack compileTrack(OsmPath path, boolean verbose) {
        OsmPathElement element = OsmPathElement.create(path);
        if (this.guideTrack != null && element.origin != null) {
            element = element.origin;
        }
        float totalTime = element.getTime();
        float totalEnergy = element.getEnergy();
        OsmTrack track = new OsmTrack();
        track.cost = path.cost;
        track.energy = (int) path.getTotalEnergy();
        int distance = 0;
        if (this.routingContext.inverseRouting) {
        }
        while (element != null) {
            if (this.guideTrack != null && element.message == null) {
                element.message = new MessageData();
            }
            OsmPathElement nextElement = element.origin;
            if (nextElement != null && nextElement.positionEquals(element)) {
                element = nextElement;
            } else {
                if (this.routingContext.inverseRouting) {
                    element.setTime(totalTime - element.getTime());
                    element.setEnergy(totalEnergy - element.getEnergy());
                    track.nodes.add(element);
                } else {
                    track.nodes.add(0, element);
                }
                if (nextElement != null) {
                    distance += element.calcDistance(nextElement);
                }
                element = nextElement;
            }
        }
        track.distance = distance;
        logInfo("track-length = " + track.distance);
        track.buildMap();
        if (this.guideTrack != null) {
            track.copyDetours(this.guideTrack);
        }
        return track;
    }

    private OsmTrack mergeTrack(OsmPathElement match, OsmTrack oldTrack) {
        logInfo("**************** merging match=" + match.cost + " with oldTrack=" + oldTrack.cost);
        OsmTrack track = new OsmTrack();
        track.cost = oldTrack.cost;
        for (OsmPathElement element = match; element != null; element = element.origin) {
            track.addNode(element);
        }
        long lastId = 0;
        long id1 = match.getIdFromPos();
        long id0 = match.origin == null ? 0L : match.origin.getIdFromPos();
        boolean appending = false;
        for (OsmPathElement n : oldTrack.nodes) {
            if (appending) {
                track.nodes.add(n);
            }
            long id = n.getIdFromPos();
            if (id == id1 && lastId == id0) {
                appending = true;
            }
            lastId = id;
        }
        track.buildMap();
        return track;
    }

    public int getPathPeak() {
        int peakSize;
        synchronized (this.openSet) {
            peakSize = this.openSet.getPeakSize();
        }
        return peakSize;
    }

    public int[] getOpenSet() {
        if (this.extract == null) {
            this.extract = new Object[500];
        }
        synchronized (this.openSet) {
            if (this.guideTrack != null) {
                List<OsmPathElement> nodes = this.guideTrack.nodes;
                int[] res = new int[nodes.size() * 2];
                int i = 0;
                for (OsmPathElement n : nodes) {
                    int i2 = i + 1;
                    res[i] = n.getILon();
                    i = i2 + 1;
                    res[i2] = n.getILat();
                }
                return res;
            }
            int size = this.openSet.getExtract(this.extract);
            int[] res2 = new int[size * 2];
            int j = 0;
            for (int i3 = 0; i3 < size; i3++) {
                OsmPath p = (OsmPath) this.extract[i3];
                this.extract[i3] = null;
                OsmNode n2 = p.getTargetNode();
                int j2 = j + 1;
                res2[j] = n2.ilon;
                j = j2 + 1;
                res2[j2] = n2.ilat;
            }
            return res2;
        }
    }

    public boolean isFinished() {
        return this.finished;
    }

    public int getLinksProcessed() {
        return this.linksProcessed;
    }

    public int getDistance() {
        return this.foundTrack.distance;
    }

    public int getAscend() {
        return this.foundTrack.ascend;
    }

    public int getPlainAscend() {
        return this.foundTrack.plainAscend;
    }

    public String getTime() {
        return Formatter.getFormattedTime2(this.foundTrack.getTotalSeconds());
    }

    public OsmTrack getFoundTrack() {
        return this.foundTrack;
    }

    public String getFoundInfo() {
        return this.outputMessage;
    }

    public int getAlternativeIndex() {
        return this.alternativeIndex;
    }

    public OsmTrack getFoundRawTrack() {
        return this.foundRawTrack;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void terminate() {
        this.terminated = true;
    }

    public boolean isTerminated() {
        return this.terminated;
    }

    public String getOutfile() {
        return this.outfile;
    }
}

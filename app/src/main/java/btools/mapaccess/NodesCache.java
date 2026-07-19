package btools.mapaccess;

import btools.codec.DataBuffers;
import btools.codec.MicroCache;
import btools.codec.WaypointMatcher;
import btools.expressions.BExpressionContextWay;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes.dex */
public final class NodesCache {
    static final int RETRY_RANGE = 250;
    private String currentFileName;
    private DataBuffers dataBuffers;
    private boolean detailed;
    private BExpressionContextWay expCtxWay;
    private Map<String, PhysicalFile> fileCache;
    private OsmFile[][] fileRows;
    public boolean first_file_access_failed;
    public String first_file_access_name;
    private boolean forceSecondaryData;
    private long ghostSum;
    private int lookupMinorVersion;
    private int lookupVersion;
    private long maxmemtiles;
    private File secondarySegmentsDir;
    private File segmentDir;
    public WaypointMatcher waypointMatcher;
    private int MAX_DYNAMIC_CATCHES = 20;
    private long cacheSum = 0;
    private boolean garbageCollectionEnabled = false;
    private boolean ghostCleaningDone = false;
    private long cacheSumClean = 0;
    private long ghostWakeup = 0;
    private boolean directWeaving = !Boolean.getBoolean("disableDirectWeaving");
    public OsmNodesMap nodesMap = new OsmNodesMap();

    public String formatStatus() {
        return "collecting=" + this.garbageCollectionEnabled + " noGhosts=" + this.ghostCleaningDone + " cacheSum=" + this.cacheSum + " cacheSumClean=" + this.cacheSumClean + " ghostSum=" + this.ghostSum + " ghostWakeup=" + this.ghostWakeup;
    }

    public NodesCache(File segmentDir, BExpressionContextWay ctxWay, boolean forceSecondaryData, long maxmem, NodesCache oldCache, boolean detailed) {
        this.secondarySegmentsDir = null;
        this.first_file_access_failed = false;
        this.ghostSum = 0L;
        this.maxmemtiles = maxmem / 8;
        this.segmentDir = segmentDir;
        this.nodesMap.maxmem = (2 * maxmem) / 3;
        this.expCtxWay = ctxWay;
        this.lookupVersion = ctxWay.meta.lookupVersion;
        this.lookupMinorVersion = ctxWay.meta.lookupMinorVersion;
        this.forceSecondaryData = forceSecondaryData;
        this.detailed = detailed;
        if (ctxWay != null) {
            ctxWay.setDecodeForbidden(detailed);
        }
        this.first_file_access_failed = false;
        this.first_file_access_name = null;
        if (this.segmentDir.isDirectory()) {
            if (oldCache != null) {
                this.fileCache = oldCache.fileCache;
                this.dataBuffers = oldCache.dataBuffers;
                this.secondarySegmentsDir = oldCache.secondarySegmentsDir;
                if (oldCache.detailed == detailed) {
                    this.fileRows = oldCache.fileRows;
                    for (OsmFile[] fileRow : this.fileRows) {
                        if (fileRow != null) {
                            for (OsmFile osmf : fileRow) {
                                this.cacheSum += osmf.setGhostState();
                            }
                        }
                    }
                } else {
                    this.fileRows = new OsmFile[180][];
                }
            } else {
                this.fileCache = new HashMap(4);
                this.fileRows = new OsmFile[180][];
                this.dataBuffers = new DataBuffers();
                this.secondarySegmentsDir = StorageConfigHelper.getSecondarySegmentDir(segmentDir);
            }
            this.ghostSum = this.cacheSum;
            return;
        }
        throw new RuntimeException("segment directory " + segmentDir.getAbsolutePath() + " does not exist");
    }

    public void clean(boolean all) {
        for (OsmFile[] fileRow : this.fileRows) {
            if (fileRow != null) {
                for (OsmFile osmf : fileRow) {
                    osmf.clean(all);
                }
            }
        }
    }

    private void checkEnableCacheCleaning() {
        if (this.cacheSum < this.maxmemtiles) {
            return;
        }
        for (int i = 0; i < this.fileRows.length; i++) {
            OsmFile[] fileRow = this.fileRows[i];
            if (fileRow != null) {
                for (OsmFile osmf : fileRow) {
                    if (this.garbageCollectionEnabled && !this.ghostCleaningDone) {
                        this.cacheSum -= osmf.cleanGhosts();
                    } else {
                        this.cacheSum -= osmf.collectAll();
                    }
                }
            }
        }
        if (this.garbageCollectionEnabled) {
            this.ghostCleaningDone = true;
            this.maxmemtiles *= 2;
        } else {
            this.cacheSumClean = this.cacheSum;
            this.garbageCollectionEnabled = true;
        }
    }

    public int loadSegmentFor(int ilon, int ilat) {
        MicroCache mc = getSegmentFor(ilon, ilat);
        if (mc == null) {
            return 0;
        }
        return mc.getSize();
    }

    public MicroCache getSegmentFor(int ilon, int ilat) {
        try {
            int lonDegree = ilon / DurationKt.NANOS_IN_MILLIS;
            int latDegree = ilat / DurationKt.NANOS_IN_MILLIS;
            OsmFile osmf = null;
            OsmFile[] fileRow = this.fileRows[latDegree];
            int ndegrees = fileRow == null ? 0 : fileRow.length;
            int i = 0;
            while (true) {
                if (i >= ndegrees) {
                    break;
                }
                if (fileRow[i].lonDegree != lonDegree) {
                    i++;
                } else {
                    osmf = fileRow[i];
                    break;
                }
            }
            if (osmf == null) {
                osmf = fileForSegment(lonDegree, latDegree);
                OsmFile[] newFileRow = new OsmFile[ndegrees + 1];
                for (int i2 = 0; i2 < ndegrees; i2++) {
                    newFileRow[i2] = fileRow[i2];
                }
                newFileRow[ndegrees] = osmf;
                this.fileRows[latDegree] = newFileRow;
            }
            this.currentFileName = osmf.filename;
            if (!osmf.hasData()) {
                return null;
            }
            MicroCache segment = osmf.getMicroCache(ilon, ilat);
            if (segment != null && (this.waypointMatcher == null || !((WaypointMatcherImpl) this.waypointMatcher).useDynamicRange)) {
                if (!segment.ghost) {
                    return segment;
                }
                segment.unGhost();
                this.ghostWakeup += (long) segment.getDataSize();
                return segment;
            }
            checkEnableCacheCleaning();
            MicroCache segment2 = osmf.createMicroCache(ilon, ilat, this.dataBuffers, this.expCtxWay, this.waypointMatcher, this.directWeaving ? this.nodesMap : null);
            this.cacheSum += (long) segment2.getDataSize();
            return segment2;
        } catch (IOException re) {
            throw new RuntimeException(re.getMessage());
        } catch (RuntimeException re2) {
            throw re2;
        } catch (Exception e) {
            throw new RuntimeException("error reading datafile " + this.currentFileName + ": " + String.valueOf(e), e);
        }
    }

    public boolean obtainNonHollowNode(OsmNode node) {
        if (!node.isHollow()) {
            return true;
        }
        MicroCache segment = getSegmentFor(node.ilon, node.ilat);
        if (segment == null) {
            return false;
        }
        if (!node.isHollow()) {
            return true;
        }
        long id = node.getIdFromPos();
        if (segment.getAndClear(id)) {
            node.parseNodeBody(segment, this.nodesMap, this.expCtxWay);
        }
        if (this.garbageCollectionEnabled) {
            this.cacheSum -= (long) segment.collect(segment.getSize() >> 1);
        }
        return true ^ node.isHollow();
    }

    public void expandHollowLinkTargets(OsmNode n) {
        OsmLink link = n.firstlink;
        while (link != null) {
            obtainNonHollowNode(link.getTarget(n));
            link = link.getNext(n);
        }
    }

    public boolean hasHollowLinkTargets(OsmNode n) {
        OsmLink link = n.firstlink;
        while (link != null) {
            if (!link.getTarget(n).isHollow()) {
                link = link.getNext(n);
            } else {
                return true;
            }
        }
        return false;
    }

    public OsmNode getStartNode(long id) {
        OsmNode n = new OsmNode(id);
        n.setHollow();
        this.nodesMap.put(n);
        if (!obtainNonHollowNode(n)) {
            return null;
        }
        expandHollowLinkTargets(n);
        return n;
    }

    public OsmNode getGraphNode(OsmNode template) {
        OsmNode graphNode = new OsmNode(template.ilon, template.ilat);
        graphNode.setHollow();
        OsmNode existing = this.nodesMap.put(graphNode);
        if (existing == null) {
            return graphNode;
        }
        this.nodesMap.put(existing);
        return existing;
    }

    public boolean matchWaypointsToNodes(List<MatchedWaypoint> unmatchedWaypoints, double maxDistance, OsmNodePairSet islandNodePairs) {
        this.waypointMatcher = new WaypointMatcherImpl(unmatchedWaypoints, maxDistance, islandNodePairs);
        Iterator<MatchedWaypoint> it = unmatchedWaypoints.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            MatchedWaypoint mwp = it.next();
            preloadPosition(mwp.waypoint, 12500, 1, false);
            if (mwp.crosspoint == null || mwp.radius > 250.0d) {
                preloadPosition(mwp.waypoint, 31250, maxDistance < 0.0d ? this.MAX_DYNAMIC_CATCHES : 2, maxDistance < 0.0d);
            }
        }
        if (this.first_file_access_failed) {
            throw new IllegalArgumentException("datafile " + this.first_file_access_name + " not found");
        }
        int len = unmatchedWaypoints.size();
        for (int i = 0; i < len; i++) {
            MatchedWaypoint mwp2 = unmatchedWaypoints.get(i);
            if (mwp2.crosspoint == null) {
                if (unmatchedWaypoints.size() <= 1 || i != unmatchedWaypoints.size() - 1 || unmatchedWaypoints.get(i - 1).wpttype != 3) {
                    return false;
                }
                mwp2.crosspoint = new OsmNode(mwp2.waypoint.ilon, mwp2.waypoint.ilat);
                mwp2.wpttype = (byte) 3;
            }
            if (unmatchedWaypoints.size() > 1 && i == unmatchedWaypoints.size() - 1 && unmatchedWaypoints.get(i - 1).wpttype == 3) {
                mwp2.crosspoint = new OsmNode(mwp2.waypoint.ilon, mwp2.waypoint.ilat);
                mwp2.wpttype = (byte) 3;
            }
        }
        return true;
    }

    private void preloadPosition(OsmNode n, int d, int maxscale, boolean bUseDynamicRange) {
        this.first_file_access_failed = false;
        this.first_file_access_name = null;
        loadSegmentFor(n.ilon, n.ilat);
        if (this.first_file_access_failed) {
            throw new IllegalArgumentException("datafile " + this.first_file_access_name + " not found");
        }
        for (int scale = 1; scale < maxscale; scale++) {
            for (int idxLat = -scale; idxLat <= scale; idxLat++) {
                for (int idxLon = -scale; idxLon <= scale; idxLon++) {
                    if (idxLon != 0 || idxLat != 0) {
                        loadSegmentFor(n.ilon + (d * idxLon), n.ilat + (d * idxLat));
                    }
                }
            }
            if (bUseDynamicRange && this.waypointMatcher.hasMatch(n.ilon, n.ilat)) {
                return;
            }
        }
    }

    private OsmFile fileForSegment(int lonDegree, int latDegree) throws Exception {
        int lonMod5 = lonDegree % 5;
        int latMod5 = latDegree % 5;
        int lon = (lonDegree - 180) - lonMod5;
        String slon = (lon < 0 ? new StringBuilder().append("W").append(-lon) : new StringBuilder().append("E").append(lon)).toString();
        int lat = (latDegree - 90) - latMod5;
        String slat = (lat < 0 ? new StringBuilder().append("S").append(-lat) : new StringBuilder().append("N").append(lat)).toString();
        String filenameBase = slon + "_" + slat;
        this.currentFileName = filenameBase + ".rd5";
        PhysicalFile ra = null;
        if (!this.fileCache.containsKey(filenameBase)) {
            File f = null;
            if (!this.forceSecondaryData) {
                File primary = new File(this.segmentDir, filenameBase + ".rd5");
                if (primary.exists()) {
                    f = primary;
                }
            }
            if (f == null) {
                File secondary = new File(this.secondarySegmentsDir, filenameBase + ".rd5");
                if (secondary.exists()) {
                    f = secondary;
                }
            }
            if (f != null) {
                this.currentFileName = f.getName();
                ra = new PhysicalFile(f, this.dataBuffers, this.lookupVersion, this.lookupMinorVersion);
            }
            this.fileCache.put(filenameBase, ra);
        }
        PhysicalFile ra2 = this.fileCache.get(filenameBase);
        OsmFile osmf = new OsmFile(ra2, lonDegree, latDegree, this.dataBuffers);
        if (this.first_file_access_name == null) {
            this.first_file_access_name = this.currentFileName;
            this.first_file_access_failed = osmf.filename == null;
        }
        return osmf;
    }

    public void close() {
        for (PhysicalFile f : this.fileCache.values()) {
            if (f != null) {
                try {
                    f.ra.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public int getElevationType(int ilon, int ilat) {
        int lonDegree = ilon / DurationKt.NANOS_IN_MILLIS;
        int latDegree = ilat / DurationKt.NANOS_IN_MILLIS;
        OsmFile[] fileRow = this.fileRows[latDegree];
        int ndegrees = fileRow == null ? 0 : fileRow.length;
        for (int i = 0; i < ndegrees; i++) {
            if (fileRow[i].lonDegree == lonDegree) {
                OsmFile osmf = fileRow[i];
                if (osmf != null) {
                    return osmf.elevationType;
                }
                return 3;
            }
        }
        return 3;
    }
}

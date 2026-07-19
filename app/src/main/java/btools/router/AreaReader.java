package btools.router;

import btools.codec.DataBuffers;
import btools.codec.MicroCache;
import btools.expressions.BExpressionContextWay;
import btools.mapaccess.MatchedWaypoint;
import btools.mapaccess.NodesCache;
import btools.mapaccess.OsmFile;
import btools.mapaccess.OsmLink;
import btools.mapaccess.OsmNode;
import btools.mapaccess.OsmNodesMap;
import btools.mapaccess.PhysicalFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import kotlin.time.DurationKt;
import okhttp3.internal.ws.RealWebSocket;

/* JADX INFO: loaded from: classes.dex */
public class AreaReader {
    File segmentFolder;

    /* JADX WARN: Removed duplicated region for block: B:118:0x0479 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x01b4  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x01ba  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x01c4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void getDirectAllData(File folder, RoutingContext rc, OsmNodeNamed wp, int maxscale, BExpressionContextWay expctxWay, OsmNogoPolygon searchRect, List<AreaInfo> ais) throws Throwable {
        Throwable th;
        int count;
        int used;
        Iterator<Map.Entry<Long, String>> it;
        Iterator<Map.Entry<Long, String>> it2;
        PhysicalFile pf;
        String lastFilenameBase;
        DataBuffers dataBuffers;
        List<Map.Entry<Long, String>> list;
        int scale;
        int idxLat;
        int idxLon;
        Map<Long, String> tiles;
        StringBuilder sbAppend;
        int used2;
        StringBuilder sbAppend2;
        int i;
        String slon;
        int lonDegree;
        boolean intersects;
        Map<Long, String> tiles2;
        int tmplon;
        int scale2;
        int scale3;
        AreaReader areaReader = this;
        OsmNodeNamed osmNodeNamed = wp;
        areaReader.segmentFolder = folder;
        int i2 = DurationKt.NANOS_IN_MILLIS;
        int cellsize = DurationKt.NANOS_IN_MILLIS / 32;
        int scale4 = maxscale;
        int used3 = 0;
        boolean checkBorder = maxscale > 7;
        Map<Long, String> tiles3 = new TreeMap<>();
        int idxLat2 = -scale4;
        int count2 = 0;
        while (idxLat2 <= scale4) {
            int count3 = count2;
            int count4 = -scale4;
            while (count4 <= scale4) {
                if (areaReader.ignoreCenter(maxscale, count4, idxLat2)) {
                    i = i2;
                    idxLat = idxLat2;
                    idxLon = count4;
                    tiles2 = tiles3;
                    used2 = used3;
                    scale2 = scale4;
                } else {
                    int tmplon2 = osmNodeNamed.ilon + (cellsize * count4);
                    int tmplat = osmNodeNamed.ilat + (cellsize * idxLat2);
                    int lonDegree2 = tmplon2 / i2;
                    int latDegree = tmplat / i2;
                    int lonMod5 = lonDegree2 % 5;
                    int latMod5 = latDegree % 5;
                    int lon = (lonDegree2 - 180) - lonMod5;
                    if (lon < 0) {
                        idxLat = idxLat2;
                        int idxLat3 = -lon;
                        idxLon = count4;
                        tiles = tiles3;
                        sbAppend = new StringBuilder().append("W").append(idxLat3);
                    } else {
                        idxLat = idxLat2;
                        idxLon = count4;
                        tiles = tiles3;
                        sbAppend = new StringBuilder().append("E").append(lon);
                    }
                    String slon2 = sbAppend.toString();
                    int lat = (latDegree - 90) - latMod5;
                    if (lat < 0) {
                        used2 = used3;
                        sbAppend2 = new StringBuilder().append("S").append(-lat);
                    } else {
                        used2 = used3;
                        sbAppend2 = new StringBuilder().append("N").append(lat);
                    }
                    String slat = sbAppend2.toString();
                    String filenameBase = slon2 + "_" + slat;
                    int lonIdx = tmplon2 / cellsize;
                    int latIdx = tmplat / cellsize;
                    int i3 = ((latIdx - (32 * latDegree)) * 32) + (lonIdx - (32 * lonDegree2));
                    int subLonIdx = lonIdx - (32 * lonDegree2);
                    int subLatIdx = latIdx - (32 * latDegree);
                    OsmNogoPolygon dataRect = new OsmNogoPolygon(true);
                    i = DurationKt.NANOS_IN_MILLIS;
                    int lon2 = lonDegree2 * DurationKt.NANOS_IN_MILLIS;
                    int lat2 = latDegree * DurationKt.NANOS_IN_MILLIS;
                    int tmplon22 = lon2 + (cellsize * subLonIdx);
                    int tmplat2 = lat2 + (cellsize * subLatIdx);
                    dataRect.addVertex(tmplon22, tmplat2);
                    int tmplon23 = lon2 + ((subLonIdx + 1) * cellsize);
                    int tmplat22 = lat2 + (cellsize * subLatIdx);
                    dataRect.addVertex(tmplon23, tmplat22);
                    int tmplon24 = lon2 + ((subLonIdx + 1) * cellsize);
                    int tmplat23 = lat2 + ((subLatIdx + 1) * cellsize);
                    dataRect.addVertex(tmplon24, tmplat23);
                    int tmplon25 = lon2 + (cellsize * subLonIdx);
                    int tmplat24 = lat2 + ((subLatIdx + 1) * cellsize);
                    dataRect.addVertex(tmplon25, tmplat24);
                    if (checkBorder) {
                        slon = slon2;
                        lonDegree = lonDegree2;
                        intersects = dataRect.intersects(searchRect.points.get(0).x, searchRect.points.get(0).y, searchRect.points.get(2).x, searchRect.points.get(2).y);
                        if ((intersects && checkBorder) ? dataRect.intersects(searchRect.points.get(1).x, searchRect.points.get(1).y, searchRect.points.get(2).x, searchRect.points.get(3).y) : intersects) {
                            boolean intersects2 = searchRect.intersects(dataRect.points.get(0).x, dataRect.points.get(0).y, dataRect.points.get(2).x, dataRect.points.get(2).y);
                            boolean intersects3 = !intersects2 ? searchRect.intersects(dataRect.points.get(1).x, dataRect.points.get(1).y, dataRect.points.get(3).x, dataRect.points.get(3).y) : intersects2;
                            if (intersects3) {
                                tiles2 = tiles;
                                tmplon = tmplon2;
                                scale2 = scale4;
                                scale3 = tmplat;
                            } else {
                                tmplon = tmplon2;
                                tiles2 = tiles;
                                scale2 = scale4;
                                scale3 = tmplat;
                                intersects3 = containsRect(searchRect, dataRect.points.get(0).x, dataRect.points.get(0).y, dataRect.points.get(2).x, dataRect.points.get(2).y);
                            }
                            if (intersects3) {
                                tiles2.put(Long.valueOf((((long) tmplon) << 32) | ((long) scale3)), filenameBase);
                                count3++;
                            }
                        } else {
                            scale2 = scale4;
                            tiles2 = tiles;
                        }
                    } else {
                        slon = slon2;
                        lonDegree = lonDegree2;
                    }
                    if (intersects) {
                        if ((intersects && checkBorder) ? dataRect.intersects(searchRect.points.get(1).x, searchRect.points.get(1).y, searchRect.points.get(2).x, searchRect.points.get(3).y) : intersects) {
                        }
                    }
                }
                count4 = idxLon + 1;
                tiles3 = tiles2;
                i2 = i;
                idxLat2 = idxLat;
                used3 = used2;
                scale4 = scale2;
                osmNodeNamed = wp;
            }
            idxLat2++;
            count2 = count3;
            osmNodeNamed = wp;
        }
        int used4 = used3;
        int scale5 = scale4;
        List<Map.Entry<Long, String>> list2 = new ArrayList<>(tiles3.entrySet());
        Collections.sort(list2, new Comparator<Map.Entry<Long, String>>() { // from class: btools.router.AreaReader.1
            @Override // java.util.Comparator
            public int compare(Map.Entry<Long, String> e1, Map.Entry<Long, String> e2) {
                return e1.getValue().compareTo(e2.getValue());
            }
        });
        long maxmem = ((long) rc.memoryclass) * RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE * RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE;
        NodesCache nodesCache = new NodesCache(areaReader.segmentFolder, expctxWay, rc.forceSecondaryData, maxmem, null, false);
        PhysicalFile pf2 = null;
        String lastFilenameBase2 = "";
        DataBuffers dataBuffers2 = null;
        try {
            it = list2.iterator();
        } catch (Exception e) {
            e = e;
            count = count2;
            used = used4;
        } catch (Throwable th2) {
            th = th2;
        }
        while (it.hasNext()) {
            Map.Entry<Long, String> entry = it.next();
            OsmNode n = new OsmNode(entry.getKey().longValue());
            String filenameBase2 = entry.getValue();
            if (!filenameBase2.equals(lastFilenameBase2)) {
                if (pf2 != null) {
                    try {
                        pf2.close();
                        try {
                            it2 = it;
                            try {
                                File file = new File(areaReader.segmentFolder, filenameBase2 + ".rd5");
                                DataBuffers dataBuffers3 = new DataBuffers();
                                dataBuffers = dataBuffers3;
                                lastFilenameBase = filenameBase2;
                                pf = new PhysicalFile(file, dataBuffers3, -1, -1);
                            } catch (Exception e2) {
                                e = e2;
                                count = count2;
                                used = used4;
                                try {
                                    System.err.println("AreaReader: after " + used + "/" + count + " " + e.getMessage());
                                    ais.clear();
                                    if (pf2 != null) {
                                        try {
                                            pf2.close();
                                        } catch (Exception e3) {
                                        }
                                    }
                                    nodesCache.close();
                                    return;
                                } catch (Throwable th3) {
                                    th = th3;
                                }
                            } catch (Throwable th4) {
                                th = th4;
                            }
                        } catch (Exception e4) {
                            e = e4;
                            count = count2;
                            used = used4;
                        } catch (Throwable th5) {
                            th = th5;
                        }
                    } catch (Exception e5) {
                        e = e5;
                        count = count2;
                        used = used4;
                        System.err.println("AreaReader: after " + used + "/" + count + " " + e.getMessage());
                        ais.clear();
                        if (pf2 != null) {
                        }
                        nodesCache.close();
                        return;
                    } catch (Throwable th6) {
                        th = th6;
                    }
                } else {
                    it2 = it;
                    File file2 = new File(areaReader.segmentFolder, filenameBase2 + ".rd5");
                    DataBuffers dataBuffers32 = new DataBuffers();
                    dataBuffers = dataBuffers32;
                    lastFilenameBase = filenameBase2;
                    pf = new PhysicalFile(file2, dataBuffers32, -1, -1);
                }
                if (pf2 != null) {
                    try {
                        pf2.close();
                    } catch (Exception e6) {
                    }
                }
                nodesCache.close();
                throw th;
            }
            it2 = it;
            pf = pf2;
            lastFilenameBase = lastFilenameBase2;
            dataBuffers = dataBuffers2;
            try {
                count = count2;
                list = list2;
                scale = scale5;
            } catch (Exception e7) {
                e = e7;
                count = count2;
                pf2 = pf;
                used = used4;
            } catch (Throwable th7) {
                th = th7;
                pf2 = pf;
            }
            try {
                if (getDirectData(pf, dataBuffers, n.getILon(), n.getILat(), rc, expctxWay, ais)) {
                    used4++;
                }
                count2 = count;
                it = it2;
                pf2 = pf;
                lastFilenameBase2 = lastFilenameBase;
                dataBuffers2 = dataBuffers;
                scale5 = scale;
                list2 = list;
                areaReader = this;
            } catch (Exception e8) {
                e = e8;
                pf2 = pf;
                used = used4;
                System.err.println("AreaReader: after " + used + "/" + count + " " + e.getMessage());
                ais.clear();
                if (pf2 != null) {
                }
                nodesCache.close();
                return;
            } catch (Throwable th8) {
                th = th8;
                pf2 = pf;
            }
        }
        if (pf2 != null) {
            try {
                pf2.close();
            } catch (Exception e9) {
            }
        }
        nodesCache.close();
    }

    public boolean getDirectData(PhysicalFile pf, DataBuffers dataBuffers, int inlon, int inlat, RoutingContext rc, BExpressionContextWay expctxWay, List<AreaInfo> ais) throws Throwable {
        int div;
        OsmFile osmf;
        int lonIdx;
        int latIdx;
        MicroCache segment;
        int size;
        int lonDegree = inlon / DurationKt.NANOS_IN_MILLIS;
        int latDegree = inlat / DurationKt.NANOS_IN_MILLIS;
        OsmNodesMap nodesMap = new OsmNodesMap();
        try {
            div = pf.divisor;
        } catch (Exception e) {
            e = e;
        }
        try {
            OsmFile osmf2 = new OsmFile(pf, lonDegree, latDegree, dataBuffers);
            if (osmf2.hasData()) {
                int cellsize = DurationKt.NANOS_IN_MILLIS / div;
                int lonIdx2 = inlon / cellsize;
                int latIdx2 = inlat / cellsize;
                MicroCache segment2 = osmf2.createMicroCache(lonIdx2, latIdx2, dataBuffers, expctxWay, null, true, null);
                if (segment2 != null) {
                    int size2 = segment2.getSize();
                    int i = 0;
                    while (i < size2) {
                        long id = segment2.getIdForIndex(i);
                        int cellsize2 = cellsize;
                        OsmNode node = new OsmNode(id);
                        if (!segment2.getAndClear(id)) {
                            osmf = osmf2;
                            lonIdx = lonIdx2;
                            latIdx = latIdx2;
                            segment = segment2;
                            size = size2;
                        } else {
                            node.parseNodeBody(segment2, nodesMap, expctxWay);
                            if (!(node.firstlink instanceof OsmLink)) {
                                osmf = osmf2;
                                lonIdx = lonIdx2;
                                latIdx = latIdx2;
                                segment = segment2;
                                size = size2;
                            } else {
                                OsmLink link = node.firstlink;
                                while (true) {
                                    if (link == null) {
                                        osmf = osmf2;
                                        lonIdx = lonIdx2;
                                        latIdx = latIdx2;
                                        segment = segment2;
                                        size = size2;
                                        break;
                                    }
                                    OsmNode nextNode = link.getTarget(node);
                                    osmf = osmf2;
                                    lonIdx = lonIdx2;
                                    if (nextNode.firstlink != null && nextNode.firstlink.descriptionBitmap != null) {
                                        Iterator<AreaInfo> it = ais.iterator();
                                        while (true) {
                                            if (!it.hasNext()) {
                                                latIdx = latIdx2;
                                                segment = segment2;
                                                size = size2;
                                                break;
                                            }
                                            AreaInfo ai = it.next();
                                            OsmLink link2 = link;
                                            Iterator<AreaInfo> it2 = it;
                                            OsmNogoPolygon osmNogoPolygon = ai.polygon;
                                            latIdx = latIdx2;
                                            segment = segment2;
                                            long j = node.ilon;
                                            size = size2;
                                            int size3 = node.ilat;
                                            long id2 = id;
                                            if (osmNogoPolygon.isWithin(j, size3)) {
                                                ai.checkAreaInfo(expctxWay, node.getElev(), nextNode.firstlink.descriptionBitmap);
                                                break;
                                            }
                                            link = link2;
                                            it = it2;
                                            latIdx2 = latIdx;
                                            segment2 = segment;
                                            size2 = size;
                                            id = id2;
                                        }
                                    }
                                    link = link.getNext(node);
                                    osmf2 = osmf;
                                    lonIdx2 = lonIdx;
                                }
                            }
                        }
                        i++;
                        cellsize = cellsize2;
                        osmf2 = osmf;
                        lonIdx2 = lonIdx;
                        latIdx2 = latIdx;
                        segment2 = segment;
                        size2 = size;
                    }
                    return true;
                }
                return true;
            }
            return false;
        } catch (Exception e2) {
            e = e2;
            System.err.println("AreaReader: " + e.getMessage());
            return false;
        }
    }

    boolean ignoreCenter(int maxscale, int idxLon, int idxLat) {
        int centerScale = ((int) Math.round(((double) maxscale) * 0.2d)) - 1;
        if (centerScale < 0) {
            return false;
        }
        return idxLon >= (-centerScale) && idxLon <= centerScale && idxLat >= (-centerScale) && idxLat <= centerScale;
    }

    boolean containsRect(OsmNogoPolygon searchRect, int p1x, int p1y, int p2x, int p2y) {
        return searchRect.isWithin((long) p1x, (long) p1y) && searchRect.isWithin((long) p2x, (long) p2y);
    }

    public void writeAreaInfo(String filename, MatchedWaypoint wp, List<AreaInfo> ais) throws Exception {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
        wp.writeToStream(dos);
        for (AreaInfo ai : ais) {
            dos.writeInt(ai.direction);
            dos.writeDouble(ai.elevStart);
            dos.writeInt(ai.ways);
            dos.writeInt(ai.greenWays);
            dos.writeInt(ai.riverWays);
            dos.writeInt(ai.elev50);
        }
        dos.close();
    }

    public void readAreaInfo(File fai, MatchedWaypoint wp, List<AreaInfo> ais) {
        MatchedWaypoint ep;
        DataInputStream dis = null;
        try {
            try {
                try {
                    dis = new DataInputStream(new BufferedInputStream(new FileInputStream(fai)));
                    ep = MatchedWaypoint.readFromStream(dis);
                } catch (Throwable th) {
                    if (dis != null) {
                        try {
                            dis.close();
                        } catch (IOException e) {
                        }
                    }
                    throw th;
                }
            } catch (IOException e2) {
                ais.clear();
                if (dis == null) {
                    return;
                } else {
                    dis.close();
                }
            }
            if (Math.abs(ep.waypoint.ilon - wp.waypoint.ilon) > 500 && Math.abs(ep.waypoint.ilat - wp.waypoint.ilat) > 500) {
                try {
                    dis.close();
                    return;
                } catch (IOException e3) {
                    return;
                }
            }
            if (Math.abs(ep.radius - wp.radius) > 500.0d) {
                try {
                    dis.close();
                    return;
                } catch (IOException e4) {
                    return;
                }
            }
            for (int i = 0; i < 4; i++) {
                int direction = dis.readInt();
                AreaInfo ai = new AreaInfo(direction);
                ai.elevStart = dis.readDouble();
                ai.ways = dis.readInt();
                ai.greenWays = dis.readInt();
                ai.riverWays = dis.readInt();
                ai.elev50 = dis.readInt();
                ais.add(ai);
            }
            dis.close();
        } catch (IOException e5) {
        }
    }
}

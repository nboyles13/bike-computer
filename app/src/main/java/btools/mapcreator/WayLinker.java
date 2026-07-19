package btools.mapcreator;

import btools.codec.DataBuffers;
import btools.codec.MicroCache;
import btools.codec.MicroCache2;
import btools.codec.StatCoderContext;
import btools.expressions.BExpressionContextWay;
import btools.expressions.BExpressionMetaData;
import btools.util.ByteArrayUnifier;
import btools.util.CompactLongMap;
import btools.util.CompactLongSet;
import btools.util.Crc32;
import btools.util.FrozenLongMap;
import btools.util.FrozenLongSet;
import btools.util.LazyArrayOfLists;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes.dex */
public class WayLinker extends MapCreatorBase implements Runnable {
    private ByteArrayUnifier abUnifier;
    private File borderFileIn;
    private CompactLongSet borderSet;
    private long creationTimeStamp;
    private File dataTilesOut;
    private String dataTilesSuffix;
    private byte elevationType;
    private BExpressionContextWay expctxWay;
    private boolean isSlave;
    private short lookupMinorVersion;
    private short lookupVersion;
    private int minLat;
    private int minLon;
    private File nodeTilesIn;
    private List<OsmNodeP> nodesList;
    private CompactLongMap<OsmNodeP> nodesMap;
    private boolean readingBorder;
    private boolean skipEncodingCheck;
    private ThreadController tc;
    private File wayTilesIn;
    private int microCacheEncoding = 2;
    private int divisor = 32;
    private int cellsize = DurationKt.NANOS_IN_MILLIS / this.divisor;

    public static final class ThreadController {
        long currentSlaveSize;
        long maxFileSize = 0;
        long currentMasterSize = 2000000000;

        synchronized boolean setCurrentMasterSize(long size) {
            try {
                if (size <= this.currentSlaveSize) {
                    this.maxFileSize = Long.MAX_VALUE;
                    return false;
                }
                this.currentMasterSize = size;
                if (this.maxFileSize == 0) {
                    this.maxFileSize = size;
                }
                return true;
            } finally {
                notify();
            }
        }

        synchronized boolean setCurrentSlaveSize(long size) throws Exception {
            if (size >= this.currentMasterSize) {
                return false;
            }
            while (this.currentMasterSize + size + 50000000 > this.maxFileSize) {
                System.out.println("****** slave thread waiting for permission to process file of size " + size + " currentMaster=" + this.currentMasterSize + " maxFileSize=" + this.maxFileSize);
                wait(10000L);
            }
            this.currentSlaveSize = size;
            return true;
        }
    }

    private void reset() {
        this.minLon = -1;
        this.minLat = -1;
        this.nodesMap = new CompactLongMap<>();
        this.borderSet = new CompactLongSet();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("*** WayLinker: Format a region of an OSM map for routing");
        if (args.length != 8) {
            System.out.println("usage: java WayLinker <node-tiles-in> <way-tiles-in> <bordernodes> <restrictions> <lookup-file> <profile-file> <data-tiles-out> <data-tiles-suffix> ");
            return;
        }
        new WayLinker().process(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]), new File(args[4]), new File(args[5]), new File(args[6]), args[7]);
        System.out.println("dumping bad TRs");
        RestrictionData.dumpBadTRs();
    }

    public void process(File nodeTilesIn, File wayTilesIn, File borderFileIn, File restrictionsFileIn, File lookupFile, File profileFile, File dataTilesOut, String dataTilesSuffix) throws Exception {
        WayLinker master = new WayLinker();
        WayLinker slave = new WayLinker();
        slave.isSlave = true;
        master.isSlave = false;
        ThreadController tc = new ThreadController();
        slave.tc = tc;
        master.tc = tc;
        master._process(nodeTilesIn, wayTilesIn, borderFileIn, restrictionsFileIn, lookupFile, profileFile, dataTilesOut, dataTilesSuffix);
        slave._process(nodeTilesIn, wayTilesIn, borderFileIn, restrictionsFileIn, lookupFile, profileFile, dataTilesOut, dataTilesSuffix);
        Thread m = new Thread(master);
        Thread s = new Thread(slave);
        m.start();
        s.start();
        m.join();
        s.join();
    }

    private void _process(File nodeTilesIn, File wayTilesIn, File borderFileIn, File restrictionsFileIn, File lookupFile, File profileFile, File dataTilesOut, String dataTilesSuffix) throws Exception {
        this.nodeTilesIn = nodeTilesIn;
        this.wayTilesIn = wayTilesIn;
        this.dataTilesOut = dataTilesOut;
        this.borderFileIn = borderFileIn;
        this.dataTilesSuffix = dataTilesSuffix;
        BExpressionMetaData meta = new BExpressionMetaData();
        this.expctxWay = new BExpressionContextWay(meta);
        meta.readMetaData(lookupFile);
        this.lookupVersion = meta.lookupVersion;
        this.lookupMinorVersion = meta.lookupMinorVersion;
        this.expctxWay.parseFile(profileFile, "global");
        this.creationTimeStamp = System.currentTimeMillis();
        this.abUnifier = new ByteArrayUnifier(16384, false);
        this.skipEncodingCheck = Boolean.getBoolean("skipEncodingCheck");
    }

    /* JADX WARN: Finally extract failed */
    @Override // java.lang.Runnable
    public void run() {
        try {
            try {
                new WayIterator(this, true, !this.isSlave).processDir(this.wayTilesIn, ".wt5");
                if (!this.isSlave) {
                    this.tc.setCurrentMasterSize(0L);
                }
            } catch (Exception e) {
                System.out.println("******* thread (slave=" + this.isSlave + ") got Exception: " + String.valueOf(e));
                throw new RuntimeException(e);
            }
        } catch (Throwable th) {
            if (!this.isSlave) {
                this.tc.setCurrentMasterSize(0L);
            }
            throw th;
        }
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public boolean wayFileStart(File wayfile) throws Exception {
        long filesize = wayfile.length();
        System.out.println("**** wayFileStart() for isSlave=" + this.isSlave + " size=" + filesize);
        if (this.isSlave) {
            if (!this.tc.setCurrentSlaveSize(filesize)) {
                return false;
            }
        } else if (!this.tc.setCurrentMasterSize(filesize)) {
            return false;
        }
        this.elevationType = (byte) 3;
        File nodeFile = fileFromTemplate(wayfile, this.nodeTilesIn, "u5d_1");
        if (nodeFile.exists()) {
            this.elevationType = (byte) 1;
        } else {
            nodeFile = fileFromTemplate(wayfile, this.nodeTilesIn, "u5d_3");
            if (!nodeFile.exists()) {
                nodeFile = fileFromTemplate(wayfile, this.nodeTilesIn, "u5d");
            }
        }
        if (nodeFile.exists()) {
            reset();
            this.readingBorder = true;
            new NodeIterator(this, false).processFile(this.borderFileIn);
            this.borderSet = new FrozenLongSet(this.borderSet);
            this.readingBorder = false;
            new NodeIterator(this, true).processFile(nodeFile);
            FrozenLongMap<OsmNodeP> nodesMapFrozen = new FrozenLongMap<>(this.nodesMap);
            this.nodesMap = nodesMapFrozen;
            File restrictionFile = fileFromTemplate(wayfile, new File(this.nodeTilesIn.getParentFile(), "restrictions55"), "rt5");
            if (restrictionFile.exists()) {
                DataInputStream di = new DataInputStream(new BufferedInputStream(new FileInputStream(restrictionFile)));
                int ntr = 0;
                while (true) {
                    try {
                        RestrictionData res = new RestrictionData(di);
                        OsmNodeP n = this.nodesMap.get(res.viaNid);
                        if (n != null) {
                            if (!(n instanceof OsmNodePT)) {
                                n = new OsmNodePT(n);
                                this.nodesMap.put(res.viaNid, n);
                            }
                            OsmNodePT nt = (OsmNodePT) n;
                            res.viaLon = nt.ilon;
                            res.viaLat = nt.ilat;
                            res.next = nt.firstRestriction;
                            nt.firstRestriction = res;
                            ntr++;
                        }
                    } catch (EOFException e) {
                        di.close();
                        System.out.println("read " + ntr + " turn-restrictions");
                    }
                }
            }
            this.nodesList = nodesMapFrozen.getValueList();
        }
        return true;
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nextNode(NodeData data) throws Exception {
        OsmNodeP n = data.description == null ? new OsmNodeP() : new OsmNodePT(data.description);
        n.ilon = data.ilon;
        n.ilat = data.ilat;
        n.selev = data.selev;
        if (this.readingBorder || !this.borderSet.contains(data.nid)) {
            this.nodesMap.fastPut(data.nid, n);
        }
        if (this.readingBorder) {
            n.bits = (byte) (n.bits | 4);
            this.borderSet.fastAdd(data.nid);
            return;
        }
        int min_lon = (n.ilon / 5000000) * 5000000;
        int min_lat = (n.ilat / 5000000) * 5000000;
        if (this.minLon == -1) {
            this.minLon = min_lon;
        }
        if (this.minLat == -1) {
            this.minLat = min_lat;
        }
        if (this.minLat != min_lat || this.minLon != min_lon) {
            throw new IllegalArgumentException("inconsistent node: " + n.ilon + " " + n.ilat);
        }
    }

    private void checkRestriction(OsmNodeP n1, OsmNodeP n2, WayData w) {
        checkRestriction(n1, n2, w, true);
        checkRestriction(n2, n1, w, false);
    }

    private void checkRestriction(OsmNodeP n1, OsmNodeP n2, WayData w, boolean checkFrom) {
        for (RestrictionData r = n2.getFirstRestriction(); r != null; r = r.next) {
            if (r.fromWid == w.wid && (r.fromLon == 0 || checkFrom)) {
                r.fromLon = n1.ilon;
                r.fromLat = n1.ilat;
                n1.bits = (byte) (n1.bits | 64);
                if (!isEndNode(n2, w)) {
                    r.badWayMatch = true;
                }
            }
            if (r.toWid == w.wid && (r.toLon == 0 || !checkFrom)) {
                r.toLon = n1.ilon;
                r.toLat = n1.ilat;
                n1.bits = (byte) (n1.bits | 64);
                if (!isEndNode(n2, w)) {
                    r.badWayMatch = true;
                }
            }
        }
    }

    private boolean isEndNode(OsmNodeP n, WayData w) {
        return n == this.nodesMap.get(w.nodes.get(0)) || n == this.nodesMap.get(w.nodes.get(w.nodes.size() - 1));
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public void nextWay(WayData way) throws Exception {
        byte[] description = this.abUnifier.unify(way.description);
        this.expctxWay.evaluate(false, description);
        boolean ok = ((double) this.expctxWay.getCostfactor()) < 10000.0d;
        this.expctxWay.evaluate(true, description);
        if (!(ok | (((double) this.expctxWay.getCostfactor()) < 10000.0d))) {
            return;
        }
        byte wayBits = 0;
        this.expctxWay.decode(description);
        if (!this.expctxWay.getBooleanLookupValue("bridge")) {
            wayBits = (byte) (0 | 1);
        }
        if (!this.expctxWay.getBooleanLookupValue("tunnel")) {
            wayBits = (byte) (wayBits | 2);
        }
        OsmNodeP n2 = null;
        for (int i = 0; i < way.nodes.size(); i++) {
            long nid = way.nodes.get(i);
            OsmNodeP n1 = n2;
            OsmNodeP n22 = this.nodesMap.get(nid);
            n2 = n22;
            if (n1 != null && n2 != null && n1 != n2) {
                checkRestriction(n1, n2, way);
                OsmLinkP link = n2.createLink(n1);
                link.descriptionBitmap = description;
                if (n1.ilon / this.cellsize != n2.ilon / this.cellsize || n1.ilat / this.cellsize != n2.ilat / this.cellsize) {
                    n2.incWayCount();
                }
            }
            if (n2 != null) {
                n2.bits = (byte) (n2.bits | wayBits);
                n2.incWayCount();
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:78:0x0290, code lost:
    
        r11 = r4 + (r8.length + 4);
        r13[r44] = r8;
     */
    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void wayFileEnd(File wayfile) throws Exception {
        int indexsize;
        File outfile;
        byte[] abBuf2;
        int tileIndex;
        int lonIdx;
        LazyArrayOfLists<OsmNodeP> seglists;
        int maxLat;
        int nLonSegs;
        int nLatSegs;
        long[] fileIndex;
        int latIdx;
        DataOutputStream os;
        int indexsize2;
        File outfile2;
        byte[] abBuf22;
        LazyArrayOfLists<OsmNodeP> subs;
        int si;
        int pos;
        int nLonSegs2;
        int nLatSegs2;
        long[] fileIndex2;
        DataOutputStream os2;
        int lonIdx2;
        int latIdx2;
        int ncaches = this.divisor * this.divisor;
        int indexsize3 = ncaches * 4;
        this.nodesMap = null;
        this.borderSet = null;
        byte[] abBuf1 = new byte[10485760];
        byte[] abBuf23 = new byte[10485760];
        int maxLon = this.minLon + 5000000;
        int maxLat2 = this.minLat + 5000000;
        for (OsmNodeP n : this.nodesList) {
            if (n != null && n.getFirstLink() != null && !n.isTransferNode()) {
                n.checkDuplicateTargets();
            }
        }
        int i = maxLon - this.minLon;
        int i2 = DurationKt.NANOS_IN_MILLIS;
        int nLonSegs3 = i / DurationKt.NANOS_IN_MILLIS;
        int nLatSegs3 = (maxLat2 - this.minLat) / DurationKt.NANOS_IN_MILLIS;
        LazyArrayOfLists<OsmNodeP> seglists2 = new LazyArrayOfLists<>(nLonSegs3 * nLatSegs3);
        for (OsmNodeP n2 : this.nodesList) {
            if (n2 == null || n2.getFirstLink() == null) {
                i2 = DurationKt.NANOS_IN_MILLIS;
            } else if (!n2.isTransferNode()) {
                if (n2.ilon < this.minLon || n2.ilon >= maxLon || n2.ilat < this.minLat) {
                    i2 = DurationKt.NANOS_IN_MILLIS;
                } else if (n2.ilat < maxLat2) {
                    int lonIdx3 = (n2.ilon - this.minLon) / i2;
                    int latIdx3 = (n2.ilat - this.minLat) / i2;
                    seglists2.getList((lonIdx3 * nLatSegs3) + latIdx3).add(n2);
                    i2 = DurationKt.NANOS_IN_MILLIS;
                }
            }
        }
        this.nodesList = null;
        seglists2.trimAll();
        File outfile3 = fileFromTemplate(wayfile, this.dataTilesOut, this.dataTilesSuffix);
        DataOutputStream os3 = createOutStream(outfile3);
        long[] fileIndex3 = new long[25];
        int[] fileHeaderCrcs = new int[25];
        int i55 = 0;
        for (int i3 = 25; i55 < i3; i3 = 25) {
            os3.writeLong(0L);
            i55++;
            fileIndex3 = fileIndex3;
        }
        long[] fileIndex4 = fileIndex3;
        long filepos = 200;
        int lonIdx4 = 0;
        while (true) {
            int maxLon2 = maxLon;
            if (lonIdx4 >= nLonSegs3) {
                File outfile4 = outfile3;
                DataOutputStream os4 = os3;
                byte[] abFileIndex = compileFileIndex(fileIndex4, this.lookupVersion, this.lookupMinorVersion);
                os4.writeLong(this.creationTimeStamp);
                os4.writeInt(Crc32.crc(abFileIndex, 0, abFileIndex.length) ^ this.microCacheEncoding);
                for (int i552 = 0; i552 < 25; i552++) {
                    os4.writeInt(fileHeaderCrcs[i552]);
                }
                int i553 = this.elevationType;
                os4.writeByte(i553);
                os4.close();
                RandomAccessFile ra = new RandomAccessFile(outfile4, "rw");
                ra.write(abFileIndex, 0, abFileIndex.length);
                ra.close();
                System.out.println("**** codec stats: *******\n" + StatCoderContext.getBitReport());
                return;
            }
            int latIdx4 = 0;
            long filepos2 = filepos;
            while (latIdx4 < nLatSegs3) {
                int tileIndex2 = (lonIdx4 * nLatSegs3) + latIdx4;
                if (seglists2.getSize(tileIndex2) <= 0) {
                    indexsize = indexsize3;
                    outfile = outfile3;
                    abBuf2 = abBuf23;
                    tileIndex = tileIndex2;
                    lonIdx = lonIdx4;
                    seglists = seglists2;
                    maxLat = maxLat2;
                    nLonSegs = nLonSegs3;
                    nLatSegs = nLatSegs3;
                    fileIndex = fileIndex4;
                    latIdx = latIdx4;
                    os = os3;
                } else {
                    List<OsmNodeP> nlist = seglists2.getList(tileIndex2);
                    tileIndex = tileIndex2;
                    LazyArrayOfLists<OsmNodeP> subs2 = new LazyArrayOfLists<>(ncaches);
                    maxLat = maxLat2;
                    byte[][] subByteArrays = new byte[ncaches][];
                    DataOutputStream os5 = os3;
                    int ni = 0;
                    while (ni < nlist.size()) {
                        OsmNodeP n3 = nlist.get(ni);
                        LazyArrayOfLists<OsmNodeP> seglists3 = seglists2;
                        List<OsmNodeP> nlist2 = nlist;
                        int subLonIdx = ((n3.ilon - this.minLon) / this.cellsize) - (this.divisor * lonIdx4);
                        int i4 = n3.ilat;
                        int lonIdx5 = lonIdx4;
                        int lonIdx6 = this.minLat;
                        int subLatIdx = ((i4 - lonIdx6) / this.cellsize) - (this.divisor * latIdx4);
                        int si2 = (this.divisor * subLatIdx) + subLonIdx;
                        subs2.getList(si2).add(n3);
                        ni++;
                        seglists2 = seglists3;
                        nlist = nlist2;
                        lonIdx4 = lonIdx5;
                        latIdx4 = latIdx4;
                    }
                    int latIdx5 = latIdx4;
                    int lonIdx7 = lonIdx4;
                    seglists = seglists2;
                    subs2.trimAll();
                    int[] posIdx = new int[ncaches];
                    int pos2 = indexsize3;
                    int pos3 = pos2;
                    int si3 = 0;
                    while (si3 < ncaches) {
                        List<OsmNodeP> subList = subs2.getList(si3);
                        int size = subList.size();
                        if (size <= 0) {
                            indexsize2 = indexsize3;
                            outfile2 = outfile3;
                            abBuf22 = abBuf23;
                            subs = subs2;
                            si = si3;
                            pos = pos3;
                            nLonSegs2 = nLonSegs3;
                            nLatSegs2 = nLatSegs3;
                            fileIndex2 = fileIndex4;
                            os2 = os5;
                            lonIdx2 = lonIdx7;
                            latIdx2 = latIdx5;
                        } else {
                            OsmNodeP n0 = subList.get(0);
                            int i5 = n0.ilon;
                            indexsize2 = indexsize3;
                            int indexsize4 = this.cellsize;
                            int lonIdxDiv = i5 / indexsize4;
                            int latIdxDiv = n0.ilat / this.cellsize;
                            subs = subs2;
                            nLonSegs2 = nLonSegs3;
                            fileIndex2 = fileIndex4;
                            latIdx2 = latIdx5;
                            nLatSegs2 = nLatSegs3;
                            lonIdx2 = lonIdx7;
                            byte[] bArr = abBuf23;
                            os2 = os5;
                            si = si3;
                            abBuf22 = abBuf23;
                            pos = pos3;
                            MicroCache mc = new MicroCache2(size, bArr, lonIdxDiv, latIdxDiv, this.divisor);
                            Map<Integer, OsmNodeP> sortedList = new TreeMap<>();
                            Iterator<OsmNodeP> it = subList.iterator();
                            while (it.hasNext()) {
                                OsmNodeP n4 = it.next();
                                long longId = n4.getIdFromPos();
                                Iterator<OsmNodeP> it2 = it;
                                int shrinkid = mc.shrinkId(longId);
                                if (mc.expandId(shrinkid) != longId) {
                                    throw new IllegalArgumentException("inconstistent shrinking: " + longId);
                                }
                                sortedList.put(Integer.valueOf(shrinkid), n4);
                                it = it2;
                                outfile3 = outfile3;
                            }
                            outfile2 = outfile3;
                            Iterator<OsmNodeP> it3 = sortedList.values().iterator();
                            while (it3.hasNext()) {
                                it3.next().writeNodeData(mc);
                            }
                            if (mc.getSize() > 0) {
                                while (true) {
                                    int len = mc.encodeMicroCache(abBuf1);
                                    byte[] subBytes = new byte[len];
                                    System.arraycopy(abBuf1, 0, subBytes, 0, len);
                                    if (this.skipEncodingCheck) {
                                        break;
                                    }
                                    MicroCache mc2 = new MicroCache2(new StatCoderContext(subBytes), new DataBuffers(null), lonIdxDiv, latIdxDiv, this.divisor, null, null);
                                    String diffMessage = mc.compareWith(mc2);
                                    if (diffMessage == null) {
                                        break;
                                    } else {
                                        if (MicroCache.debug) {
                                            throw new RuntimeException("encoding crosscheck failed: " + diffMessage);
                                        }
                                        MicroCache.debug = true;
                                    }
                                }
                            }
                        }
                        pos3 = pos;
                        posIdx[si] = pos3;
                        si3 = si + 1;
                        os5 = os2;
                        lonIdx7 = lonIdx2;
                        latIdx5 = latIdx2;
                        indexsize3 = indexsize2;
                        subs2 = subs;
                        nLatSegs3 = nLatSegs2;
                        abBuf23 = abBuf22;
                        outfile3 = outfile2;
                        fileIndex4 = fileIndex2;
                        nLonSegs3 = nLonSegs2;
                    }
                    indexsize = indexsize3;
                    outfile = outfile3;
                    abBuf2 = abBuf23;
                    int pos4 = pos3;
                    nLonSegs = nLonSegs3;
                    nLatSegs = nLatSegs3;
                    fileIndex = fileIndex4;
                    os = os5;
                    lonIdx = lonIdx7;
                    latIdx = latIdx5;
                    byte[] abSubIndex = compileSubFileIndex(posIdx);
                    fileHeaderCrcs[tileIndex] = Crc32.crc(abSubIndex, 0, abSubIndex.length);
                    os.write(abSubIndex, 0, abSubIndex.length);
                    for (int si4 = 0; si4 < ncaches; si4++) {
                        byte[] ab = subByteArrays[si4];
                        if (ab != null) {
                            os.write(ab);
                            os.writeInt(Crc32.crc(ab, 0, ab.length) ^ this.microCacheEncoding);
                        }
                    }
                    filepos2 += (long) pos4;
                }
                fileIndex[tileIndex] = filepos2;
                latIdx4 = latIdx + 1;
                fileIndex4 = fileIndex;
                os3 = os;
                lonIdx4 = lonIdx;
                maxLat2 = maxLat;
                seglists2 = seglists;
                indexsize3 = indexsize;
                nLonSegs3 = nLonSegs;
                nLatSegs3 = nLatSegs;
                abBuf23 = abBuf2;
                outfile3 = outfile;
            }
            lonIdx4++;
            fileIndex4 = fileIndex4;
            maxLon = maxLon2;
            filepos = filepos2;
            nLonSegs3 = nLonSegs3;
            nLatSegs3 = nLatSegs3;
        }
    }

    private byte[] compileFileIndex(long[] fileIndex, short lookupVersion, short lookupMinorVersion) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        int i55 = 0;
        while (i55 < 25) {
            long versionPrefix = i55 == 1 ? lookupMinorVersion : lookupVersion;
            dos.writeLong(fileIndex[i55] | (versionPrefix << 48));
            i55++;
        }
        dos.close();
        return bos.toByteArray();
    }

    private byte[] compileSubFileIndex(int[] posIdx) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        for (int i : posIdx) {
            dos.writeInt(i);
        }
        dos.close();
        return bos.toByteArray();
    }
}

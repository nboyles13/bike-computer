package btools.mapcreator;

import btools.util.CompactLongSet;
import btools.util.DiffCoderDataOutputStream;
import btools.util.FrozenLongSet;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes.dex */
public class PosUnifier extends MapCreatorBase {
    public static final boolean UseRasterRd5FileName = false;
    private CompactLongSet borderNids;
    private DiffCoderDataOutputStream borderNodesOut;
    private int lastSrtmLatIdx;
    private int lastSrtmLonIdx;
    private ElevationRaster lastSrtmRaster;
    private File nodeTilesOut;
    private DiffCoderDataOutputStream nodesOutStream;
    private File outNodeFile;
    private CompactLongSet[] positionSets;
    private String srtmdir;
    private String srtmfallbackdir;
    private Map<String, ElevationRaster> srtmmap;

    public static void main(String[] args) throws Exception {
        System.out.println("*** PosUnifier: Unify position values and enhance elevation");
        if (args.length == 3) {
            PosUnifier posu = new PosUnifier();
            posu.srtmdir = args[0];
            posu.srtmmap = new HashMap();
            double lon = Double.parseDouble(args[1]);
            double lat = Double.parseDouble(args[2]);
            NodeData n = new NodeData(1L, lon, lat);
            ElevationRaster srtm = null;
            if (0 == 0) {
                srtm = posu.srtmForNode(n.ilon, n.ilat);
            }
            short selev = srtm != null ? srtm.getElevation(n.ilon, n.ilat) : Short.MIN_VALUE;
            posu.resetElevationRaster();
            System.out.println("-----> selv for bef " + lat + ", " + lon + " = " + ((int) selev) + " = " + (((double) selev) / 4.0d));
            return;
        }
        if (args.length != 5 && args.length != 6) {
            System.out.println("usage: java PosUnifier <node-tiles-in> <node-tiles-out> <bordernids-in> <bordernodes-out> <srtm-data-dir> [srtm-fallback-data-dir]");
            System.out.println("or     java PosUnifier <srtm-data-dir> <lon> <lat>");
        } else {
            new PosUnifier().process(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]), args[4], args.length == 6 ? args[5] : null);
        }
    }

    public void process(File nodeTilesIn, File nodeTilesOut, File bordernidsinfile, File bordernodesoutfile, String srtmdir, String srtmfallbackdir) throws Exception {
        this.nodeTilesOut = nodeTilesOut;
        this.srtmdir = srtmdir;
        this.srtmfallbackdir = srtmfallbackdir;
        DataInputStream dis = createInStream(bordernidsinfile);
        this.borderNids = new CompactLongSet();
        while (true) {
            try {
                long nid = readId(dis);
                if (!this.borderNids.contains(nid)) {
                    this.borderNids.fastAdd(nid);
                }
            } catch (EOFException e) {
                dis.close();
                this.borderNids = new FrozenLongSet(this.borderNids);
                this.borderNodesOut = createOutStream(bordernodesoutfile);
                new NodeIterator(this, true).processDir(nodeTilesIn, ".n5d");
                this.borderNodesOut.close();
                return;
            }
        }
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nodeFileStart(File nodefile) throws Exception {
        resetElevationRaster();
        this.outNodeFile = fileFromTemplate(nodefile, this.nodeTilesOut, "u5d");
        this.nodesOutStream = createOutStream(this.outNodeFile);
        this.positionSets = new CompactLongSet[2500];
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nextNode(NodeData n) throws Exception {
        n.selev = Short.MIN_VALUE;
        ElevationRaster srtm = srtmForNode(n.ilon, n.ilat);
        if (srtm != null) {
            n.selev = srtm.getElevation(n.ilon, n.ilat);
        }
        findUniquePos(n);
        n.writeTo(this.nodesOutStream);
        if (this.borderNids.contains(n.nid)) {
            n.writeTo(this.borderNodesOut);
        }
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nodeFileEnd(File nodeFile) throws Exception {
        this.nodesOutStream.close();
        if (this.outNodeFile != null && this.lastSrtmRaster != null) {
            String newName = this.outNodeFile.getAbsolutePath() + (this.lastSrtmRaster.nrows > 6001 ? "_1" : "_3");
            this.outNodeFile.renameTo(new File(newName));
        }
        resetElevationRaster();
    }

    private boolean checkAdd(int lon, int lat) {
        int slot = (((lon % 5000000) / 100000) * 50) + ((lat % 5000000) / 100000);
        long id = (((long) lon) << 32) | ((long) lat);
        CompactLongSet set = this.positionSets[slot];
        if (set == null) {
            CompactLongSet[] compactLongSetArr = this.positionSets;
            CompactLongSet compactLongSet = new CompactLongSet();
            set = compactLongSet;
            compactLongSetArr[slot] = compactLongSet;
        }
        if (!set.contains(id)) {
            set.fastAdd(id);
            return true;
        }
        return false;
    }

    private void findUniquePos(NodeData n) {
        if (!checkAdd(n.ilon, n.ilat)) {
            _findUniquePos(n);
        }
    }

    private void _findUniquePos(NodeData n) {
        int lonmod = n.ilon % DurationKt.NANOS_IN_MILLIS;
        int londelta = lonmod < 500000 ? 1 : -1;
        int latmod = n.ilat % DurationKt.NANOS_IN_MILLIS;
        int latdelta = latmod < 500000 ? 1 : -1;
        for (int latsteps = 0; latsteps < 100; latsteps++) {
            for (int lonsteps = 0; lonsteps <= latsteps; lonsteps++) {
                int lon = n.ilon + (lonsteps * londelta);
                int lat = n.ilat + (latsteps * latdelta);
                if (checkAdd(lon, lat)) {
                    n.ilon = lon;
                    n.ilat = lat;
                    return;
                }
            }
        }
        System.out.println("*** WARNING: cannot unify position for: " + n.ilon + " " + n.ilat);
    }

    private ElevationRaster srtmForNode(int ilon, int ilat) throws Exception {
        int srtmLonIdx = (ilon + 5000000) / 5000000;
        int srtmLatIdx = ((654999999 - ilat) / 5000000) - 100;
        if (srtmLonIdx == this.lastSrtmLonIdx && srtmLatIdx == this.lastSrtmLatIdx) {
            return this.lastSrtmRaster;
        }
        this.lastSrtmLonIdx = srtmLonIdx;
        this.lastSrtmLatIdx = srtmLatIdx;
        String filename = genFilenameXY(srtmLonIdx, srtmLatIdx);
        this.lastSrtmRaster = this.srtmmap.get(filename);
        if (this.lastSrtmRaster == null && !this.srtmmap.containsKey(filename)) {
            File f = new File(new File(this.srtmdir), filename + ".bef");
            if (f.exists()) {
                try {
                    InputStream isc = new BufferedInputStream(new FileInputStream(f));
                    this.lastSrtmRaster = new ElevationRasterCoder().decodeRaster(isc);
                    isc.close();
                } catch (Exception e) {
                    System.out.println("**** ERROR reading " + String.valueOf(f) + " ****");
                }
                System.out.println("*** reading: " + String.valueOf(f) + "  " + this.lastSrtmRaster.ncols);
                this.srtmmap.put(filename, this.lastSrtmRaster);
                return this.lastSrtmRaster;
            }
            if (this.srtmfallbackdir != null) {
                File f2 = new File(new File(this.srtmfallbackdir), filename + ".bef");
                if (f2.exists()) {
                    try {
                        InputStream isc2 = new BufferedInputStream(new FileInputStream(f2));
                        this.lastSrtmRaster = new ElevationRasterCoder().decodeRaster(isc2);
                        isc2.close();
                    } catch (Exception e2) {
                        System.out.println("**** ERROR reading " + String.valueOf(f2) + " ****");
                    }
                    System.out.println("*** reading: " + String.valueOf(f2) + "  " + this.lastSrtmRaster.cellsize);
                    this.srtmmap.put(filename, this.lastSrtmRaster);
                    return this.lastSrtmRaster;
                }
            }
            this.srtmmap.put(filename, this.lastSrtmRaster);
        }
        return this.lastSrtmRaster;
    }

    static String genFilenameXY(int srtmLonIdx, int srtmLatIdx) {
        String slonidx = "0" + srtmLonIdx;
        String slatidx = "0" + srtmLatIdx;
        return "srtm_" + slonidx.substring(slonidx.length() - 2) + "_" + slatidx.substring(slatidx.length() - 2);
    }

    static String genFilenameRd5(int ilon, int ilat) {
        StringBuilder sbAppend;
        StringBuilder sbAppend2;
        int lonDegree = ilon / DurationKt.NANOS_IN_MILLIS;
        int latDegree = ilat / DurationKt.NANOS_IN_MILLIS;
        int lonMod5 = lonDegree % 5;
        int latMod5 = latDegree % 5;
        int lonDegree2 = (lonDegree - 180) - lonMod5;
        int latDegree2 = (latDegree - 90) - latMod5;
        if (lonDegree2 < 0) {
            sbAppend = new StringBuilder().append("W").append(-lonDegree2);
        } else {
            sbAppend = new StringBuilder().append("E").append(lonDegree2);
        }
        String string = sbAppend.toString();
        if (latDegree2 < 0) {
            sbAppend2 = new StringBuilder().append("S").append(-latDegree2);
        } else {
            sbAppend2 = new StringBuilder().append("N").append(latDegree2);
        }
        return String.format("srtm_%s_%s", string, sbAppend2.toString());
    }

    private ElevationRaster hgtForNode(int ilon, int ilat) throws Exception {
        double lon = ((double) (ilon - 180000000)) / 1000000.0d;
        double lat = ((double) (ilat - 90000000)) / 1000000.0d;
        String filename = buildHgtFilename(lat, lon);
        ElevationRaster srtm = this.srtmmap.get(filename);
        if (srtm == null) {
            File f = new File(new File(this.srtmdir), filename + HgtReader.ZIP_EXT);
            if (f.exists()) {
                ElevationRaster srtm2 = new ElevationRasterTileConverter().getRaster(f, lon, lat);
                this.srtmmap.put(filename, srtm2);
                return srtm2;
            }
            File f2 = new File(new File(this.srtmdir), filename + HgtReader.HGT_EXT);
            if (f2.exists()) {
                ElevationRaster srtm3 = new ElevationRasterTileConverter().getRaster(f2, lon, lat);
                this.srtmmap.put(filename, srtm3);
                return srtm3;
            }
        }
        return srtm;
    }

    private String buildHgtFilename(double llat, double llon) {
        int lat = (int) llat;
        int lon = (int) llon;
        String latPref = "N";
        if (lat < 0) {
            latPref = "S";
            lat = (-lat) + 1;
        }
        String lonPref = "E";
        if (lon < 0) {
            lonPref = "W";
            lon = (-lon) + 1;
        }
        return String.format("%s%02d%s%03d", latPref, Integer.valueOf(lat), lonPref, Integer.valueOf(lon));
    }

    private void resetElevationRaster() {
        this.srtmmap = new HashMap();
        this.lastSrtmLonIdx = -1;
        this.lastSrtmLatIdx = -1;
        this.lastSrtmRaster = null;
    }
}

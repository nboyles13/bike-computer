package btools.mapcreator;

import btools.util.DenseLongMap;
import btools.util.TinyDenseLongMap;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;

/* JADX INFO: loaded from: classes.dex */
public class WayCutter5 extends MapCreatorBase {
    private DataOutputStream borderNidsOutStream;
    private int latoffset;
    private int lonoffset;
    public NodeCutter nodeCutter;
    public NodeFilter nodeFilter;
    private File nodeTilesIn;
    public RelationMerger relMerger;
    public RestrictionCutter5 restrictionCutter5;
    private DenseLongMap tileIndexMap;

    public static void main(String[] args) throws Exception {
        System.out.println("*** WayCutter5: Soft-Cut way-data into tiles");
        if (args.length != 4) {
            System.out.println("usage: java WayCutter5 <node-tiles-in> <way-tiles-in> <way-tiles-out> <border-nids-out>");
        } else {
            new WayCutter5().process(new File(args[0]), new File(args[1]), new File(args[2]), new File(args[3]));
        }
    }

    public void process(File nodeTilesIn, File wayTilesIn, File wayTilesOut, File borderNidsOut) throws Exception {
        this.nodeTilesIn = nodeTilesIn;
        this.outTileDir = wayTilesOut;
        this.borderNidsOutStream = createOutStream(borderNidsOut);
        new WayIterator(this, true).processDir(wayTilesIn, ".wtl");
        this.borderNidsOutStream.close();
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public boolean wayFileStart(File wayfile) throws Exception {
        String name = wayfile.getName();
        String nodefilename = name.substring(0, name.length() - 3) + "ntl";
        File nodefile = new File(this.nodeTilesIn, nodefilename);
        this.tileIndexMap = Boolean.getBoolean("useDenseMaps") ? new DenseLongMap() : new TinyDenseLongMap();
        this.lonoffset = -1;
        this.latoffset = -1;
        if (this.nodeCutter != null) {
            this.nodeCutter.nodeFileStart(null);
        }
        new NodeIterator(this, this.nodeCutter != null).processFile(nodefile);
        if (this.restrictionCutter5 != null) {
            String resfilename = name.substring(0, name.length() - 3) + "rtl";
            File resfile = new File("restrictions", resfilename);
            if (resfile.exists()) {
                DataInputStream di = new DataInputStream(new BufferedInputStream(new FileInputStream(resfile)));
                int ntr = 0;
                while (true) {
                    try {
                        RestrictionData res = new RestrictionData(di);
                        this.restrictionCutter5.nextRestriction(res);
                        ntr++;
                    } catch (EOFException e) {
                        di.close();
                        System.out.println("read " + ntr + " turn-restrictions");
                    }
                }
            }
        }
        return true;
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nextNode(NodeData n) throws Exception {
        if (this.nodeFilter != null && !this.nodeFilter.isRelevant(n)) {
            return;
        }
        if (this.nodeCutter != null) {
            this.nodeCutter.nextNode(n);
        }
        this.tileIndexMap.put(n.nid, getTileIndex(n.ilon, n.ilat));
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public void nextWay(WayData data) throws Exception {
        long waytileset = 0;
        int nnodes = data.nodes.size();
        int[] tiForNode = new int[nnodes];
        for (int i = 0; i < nnodes; i++) {
            int tileIndex = this.tileIndexMap.getInt(data.nodes.get(i));
            if (tileIndex != -1) {
                waytileset |= 1 << tileIndex;
            }
            tiForNode[i] = tileIndex;
        }
        if (this.relMerger != null) {
            this.relMerger.nextWay(data);
        }
        for (int tileIndex2 = 0; tileIndex2 < 54; tileIndex2++) {
            if (((1 << tileIndex2) & waytileset) != 0) {
                data.writeTo(getOutStreamForTile(tileIndex2));
            }
        }
        for (int i2 = 0; i2 < nnodes; i2++) {
            int ti = tiForNode[i2];
            if (ti != -1 && ((i2 > 0 && tiForNode[i2 - 1] != ti) || (i2 + 1 < nnodes && tiForNode[i2 + 1] != ti))) {
                writeId(this.borderNidsOutStream, data.nodes.get(i2));
            }
        }
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public void wayFileEnd(File wayFile) throws Exception {
        closeTileOutStreams();
        if (this.nodeCutter != null) {
            this.nodeCutter.nodeFileEnd(null);
        }
        if (this.restrictionCutter5 != null) {
            this.restrictionCutter5.finish();
        }
    }

    public int getTileIndexForNid(long nid) {
        return this.tileIndexMap.getInt(nid);
    }

    private int getTileIndex(int ilon, int ilat) {
        int lonoff = (ilon / 45000000) * 45;
        int latoff = (ilat / 30000000) * 30;
        if (this.lonoffset == -1) {
            this.lonoffset = lonoff;
        }
        if (this.latoffset == -1) {
            this.latoffset = latoff;
        }
        if (lonoff != this.lonoffset || latoff != this.latoffset) {
            throw new IllegalArgumentException("inconsistent node: " + ilon + " " + ilat);
        }
        int lon = (ilon / 5000000) % 9;
        int lat = (ilat / 5000000) % 6;
        if (lon < 0 || lon > 8 || lat < 0 || lat > 5) {
            throw new IllegalArgumentException("illegal pos: " + ilon + "," + ilat);
        }
        return (lon * 6) + lat;
    }

    @Override // btools.mapcreator.MapCreatorBase
    protected String getNameForTile(int tileIndex) {
        StringBuilder sbAppend;
        StringBuilder sbAppend2;
        int lon = (((tileIndex / 6) * 5) + this.lonoffset) - 180;
        int lat = (((tileIndex % 6) * 5) + this.latoffset) - 90;
        if (lon < 0) {
            sbAppend = new StringBuilder().append("W").append(-lon);
        } else {
            sbAppend = new StringBuilder().append("E").append(lon);
        }
        String slon = sbAppend.toString();
        if (lat < 0) {
            sbAppend2 = new StringBuilder().append("S").append(-lat);
        } else {
            sbAppend2 = new StringBuilder().append("N").append(lat);
        }
        String slat = sbAppend2.toString();
        return slon + "_" + slat + ".wt5";
    }
}

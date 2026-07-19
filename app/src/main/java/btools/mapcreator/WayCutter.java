package btools.mapcreator;

import btools.util.DenseLongMap;
import btools.util.TinyDenseLongMap;
import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class WayCutter extends MapCreatorBase {
    private DenseLongMap tileIndexMap;

    public static void main(String[] args) throws Exception {
        System.out.println("*** WayCutter: Soft-Cut way-data into tiles");
        if (args.length != 3) {
            System.out.println("usage: java WayCutter <node-tiles-in> <way-file-in> <way-tiles-out>");
        } else {
            new WayCutter().process(new File(args[0]), new File(args[1]), new File(args[2]));
        }
    }

    public void process(File nodeTilesIn, File wayFileIn, File wayTilesOut) throws Exception {
        init(wayTilesOut);
        new NodeIterator(this, false).processDir(nodeTilesIn, ".tlf");
        new WayIterator(this, true).processFile(wayFileIn);
        finish();
    }

    public void init(File wayTilesOut) throws Exception {
        this.outTileDir = wayTilesOut;
        this.tileIndexMap = Boolean.getBoolean("useDenseMaps") ? new DenseLongMap() : new TinyDenseLongMap();
    }

    public void finish() throws Exception {
        closeTileOutStreams();
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nextNode(NodeData n) throws Exception {
        this.tileIndexMap.put(n.nid, getTileIndex(n.ilon, n.ilat));
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public void nextWay(WayData data) throws Exception {
        long waytileset = 0;
        int nnodes = data.nodes.size();
        for (int i = 0; i < nnodes; i++) {
            int tileIndex = this.tileIndexMap.getInt(data.nodes.get(i));
            if (tileIndex != -1) {
                waytileset |= 1 << tileIndex;
            }
        }
        for (int tileIndex2 = 0; tileIndex2 < 54; tileIndex2++) {
            if (((1 << tileIndex2) & waytileset) != 0) {
                data.writeTo(getOutStreamForTile(tileIndex2));
            }
        }
    }

    public int getTileIndexForNid(long nid) {
        return this.tileIndexMap.getInt(nid);
    }

    private int getTileIndex(int ilon, int ilat) {
        int lon = ilon / 45000000;
        int lat = ilat / 30000000;
        if (lon < 0 || lon > 7 || lat < 0 || lat > 5) {
            throw new IllegalArgumentException("illegal pos: " + ilon + "," + ilat);
        }
        return (lon * 6) + lat;
    }

    @Override // btools.mapcreator.MapCreatorBase
    public String getNameForTile(int tileIndex) {
        StringBuilder sbAppend;
        StringBuilder sbAppend2;
        int lon = ((tileIndex / 6) * 45) - 180;
        int lat = ((tileIndex % 6) * 30) - 90;
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
        return slon + "_" + slat + ".wtl";
    }
}

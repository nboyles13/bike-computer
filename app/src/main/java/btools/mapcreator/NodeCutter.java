package btools.mapcreator;

import java.io.File;

/* JADX INFO: loaded from: classes.dex */
public class NodeCutter extends MapCreatorBase {
    private int latoffset;
    private int lonoffset;

    public static void main(String[] args) throws Exception {
        System.out.println("*** NodeCutter: Cut big node-tiles into 5x5 tiles");
        if (args.length != 2) {
            System.out.println("usage: java NodeCutter <node-tiles-in> <node-tiles-out>");
        } else {
            new NodeCutter().process(new File(args[0]), new File(args[1]));
        }
    }

    public void init(File nodeTilesOut) {
        this.outTileDir = nodeTilesOut;
    }

    public void process(File nodeTilesIn, File nodeTilesOut) throws Exception {
        init(nodeTilesOut);
        new NodeIterator(this, true).processDir(nodeTilesIn, ".tlf");
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nodeFileStart(File nodefile) throws Exception {
        this.lonoffset = -1;
        this.latoffset = -1;
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nextNode(NodeData n) throws Exception {
        n.writeTo(getOutStreamForTile(getTileIndex(n.ilon, n.ilat)));
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nodeFileEnd(File nodeFile) throws Exception {
        closeTileOutStreams();
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
        return slon + "_" + slat + ".n5d";
    }
}

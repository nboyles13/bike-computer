package btools.mapcreator;

import btools.util.DenseLongMap;
import btools.util.DiffCoderDataOutputStream;
import btools.util.TinyDenseLongMap;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class NodeFilter extends MapCreatorBase {
    private File nodeTilesOut;
    protected DenseLongMap nodebitmap;
    private DiffCoderDataOutputStream nodesOutStream;

    public static void main(String[] args) throws Exception {
        System.out.println("*** NodeFilter: Filter way related nodes");
        if (args.length != 3) {
            System.out.println("usage: java NodeFilter <node-tiles-in> <way-file-in> <node-tiles-out>");
        } else {
            new NodeFilter().process(new File(args[0]), new File(args[1]), new File(args[2]));
        }
    }

    public void init() throws Exception {
        this.nodebitmap = Boolean.getBoolean("useDenseMaps") ? new DenseLongMap(512) : new TinyDenseLongMap();
    }

    public void process(File nodeTilesIn, File wayFileIn, File nodeTilesOut) throws Exception {
        init();
        this.nodeTilesOut = nodeTilesOut;
        new WayIterator(this, false).processFile(wayFileIn);
        new NodeIterator(this, true).processDir(nodeTilesIn, ".tls");
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.WayListener
    public void nextWay(WayData data) throws Exception {
        int nnodes = data.nodes.size();
        for (int i = 0; i < nnodes; i++) {
            this.nodebitmap.put(data.nodes.get(i), 0);
        }
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nodeFileStart(File nodefile) throws Exception {
        String filename = nodefile.getName();
        File outfile = new File(this.nodeTilesOut, filename);
        this.nodesOutStream = new DiffCoderDataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile)));
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nextNode(NodeData n) throws Exception {
        if (isRelevant(n)) {
            n.writeTo(this.nodesOutStream);
        }
    }

    public boolean isRelevant(NodeData n) {
        return this.nodebitmap.getInt(n.nid) == 0;
    }

    @Override // btools.mapcreator.MapCreatorBase, btools.mapcreator.NodeListener
    public void nodeFileEnd(File nodeFile) throws Exception {
        this.nodesOutStream.close();
    }
}

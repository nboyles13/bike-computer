package btools.mapcreator;

import btools.util.DiffCoderDataInputStream;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;

/* JADX INFO: loaded from: classes.dex */
public class NodeIterator extends MapCreatorBase {
    private boolean delete;
    private NodeListener listener;

    public NodeIterator(NodeListener nodeListener, boolean deleteAfterReading) {
        this.listener = nodeListener;
        this.delete = deleteAfterReading;
    }

    public void processDir(File indir, String inSuffix) throws Exception {
        if (!indir.isDirectory()) {
            throw new IllegalArgumentException("not a directory: " + String.valueOf(indir));
        }
        File[] af = sortBySizeAsc(indir.listFiles());
        for (File nodefile : af) {
            if (nodefile.getName().endsWith(inSuffix)) {
                processFile(nodefile);
            }
        }
    }

    public void processFile(File nodefile) throws Exception {
        System.out.println("*** NodeIterator reading: " + String.valueOf(nodefile));
        this.listener.nodeFileStart(nodefile);
        DiffCoderDataInputStream di = new DiffCoderDataInputStream(new BufferedInputStream(new FileInputStream(nodefile)));
        while (true) {
            try {
                NodeData n = new NodeData(di);
                this.listener.nextNode(n);
            } catch (EOFException e) {
                di.close();
                this.listener.nodeFileEnd(nodefile);
                if (this.delete && "true".equals(System.getProperty("deletetmpfiles"))) {
                    nodefile.delete();
                    return;
                }
                return;
            }
        }
    }
}

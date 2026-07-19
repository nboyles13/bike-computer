package btools.mapcreator;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;

/* JADX INFO: loaded from: classes.dex */
public class WayIterator extends MapCreatorBase {
    private boolean delete;
    private boolean descendingSize;
    private WayListener listener;

    public WayIterator(WayListener wayListener, boolean deleteAfterReading) {
        this.listener = wayListener;
        this.delete = deleteAfterReading;
    }

    public WayIterator(WayListener wayListener, boolean deleteAfterReading, boolean descendingSize) {
        this(wayListener, deleteAfterReading);
        this.descendingSize = descendingSize;
    }

    public void processDir(File indir, String inSuffix) throws Exception {
        if (!indir.isDirectory()) {
            throw new IllegalArgumentException("not a directory: " + String.valueOf(indir));
        }
        File[] af = sortBySizeAsc(indir.listFiles());
        for (int i = 0; i < af.length; i++) {
            File wayfile = this.descendingSize ? af[(af.length - 1) - i] : af[i];
            if (wayfile.getName().endsWith(inSuffix)) {
                processFile(wayfile);
            }
        }
    }

    public void processFile(File wayfile) throws Exception {
        System.out.println("*** WayIterator reading: " + String.valueOf(wayfile));
        if (!this.listener.wayFileStart(wayfile)) {
            return;
        }
        DataInputStream di = new DataInputStream(new BufferedInputStream(new FileInputStream(wayfile)));
        while (true) {
            try {
                WayData w = new WayData(di);
                this.listener.nextWay(w);
            } catch (EOFException e) {
                di.close();
                this.listener.wayFileEnd(wayfile);
                if (this.delete && "true".equals(System.getProperty("deletetmpfiles"))) {
                    wayfile.delete();
                    return;
                }
                return;
            }
        }
    }
}

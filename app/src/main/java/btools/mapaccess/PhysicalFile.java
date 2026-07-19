package btools.mapaccess;

import androidx.recyclerview.widget.ItemTouchHelper;
import btools.codec.DataBuffers;
import btools.codec.MicroCache;
import btools.util.ByteDataReader;
import btools.util.Crc32;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/* JADX INFO: loaded from: classes.dex */
public final class PhysicalFile {
    public long creationTime;
    public int divisor;
    public byte elevationType;
    int[] fileHeaderCrcs;
    long[] fileIndex = new long[25];
    String fileName;
    RandomAccessFile ra;

    public static void main(String[] args) throws Throwable {
        MicroCache.debug = true;
        try {
            checkFileIntegrity(new File(args[0]));
        } catch (IOException e) {
            System.err.println("************************************");
            e.printStackTrace();
            System.err.println("************************************");
        }
    }

    public static int checkVersionIntegrity(File f) throws IOException {
        int version = -1;
        RandomAccessFile raf = null;
        try {
            byte[] iobuffer = new byte[ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION];
            raf = new RandomAccessFile(f, "r");
            raf.readFully(iobuffer, 0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
            ByteDataReader dis = new ByteDataReader(iobuffer);
            long lv = dis.readLong();
            version = (int) (lv >> 48);
            try {
                raf.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e2) {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e3) {
                    throw new RuntimeException(e3);
                }
            }
        } catch (Throwable e4) {
            if (raf != null) {
                try {
                    raf.close();
                } catch (IOException e5) {
                    throw new RuntimeException(e5);
                }
            }
            throw e4;
        }
        return version;
    }

    public static String checkFileIntegrity(File f) throws Throwable {
        PhysicalFile pf = null;
        try {
            DataBuffers dataBuffers = new DataBuffers();
            try {
                pf = new PhysicalFile(f, dataBuffers, -1, -1);
                int div = pf.divisor;
                for (int lonDegree = 0; lonDegree < 5; lonDegree++) {
                    for (int latDegree = 0; latDegree < 5; latDegree++) {
                        OsmFile osmf = new OsmFile(pf, lonDegree, latDegree, dataBuffers);
                        if (osmf.hasData()) {
                            for (int lonIdx = 0; lonIdx < div; lonIdx++) {
                                int latIdx = 0;
                                while (latIdx < div) {
                                    int latIdx2 = latIdx;
                                    osmf.createMicroCache((lonDegree * div) + lonIdx, (latDegree * div) + latIdx, dataBuffers, null, null, MicroCache.debug, null);
                                    latIdx = latIdx2 + 1;
                                }
                            }
                        }
                    }
                }
                try {
                    pf.ra.close();
                    return null;
                } catch (Exception e) {
                    return null;
                }
            } catch (Throwable th) {
                th = th;
                Throwable th2 = th;
                if (pf != null) {
                    try {
                        pf.ra.close();
                        throw th2;
                    } catch (Exception e2) {
                        throw th2;
                    }
                }
                throw th2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public PhysicalFile(File f, DataBuffers dataBuffers, int lookupVersion, int lookupMinorVersion) throws IOException {
        this.ra = null;
        this.divisor = 80;
        this.elevationType = (byte) 3;
        this.fileName = f.getName();
        byte[] iobuffer = dataBuffers.iobuffer;
        this.ra = new RandomAccessFile(f, "r");
        this.ra.readFully(iobuffer, 0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        int fileIndexCrc = Crc32.crc(iobuffer, 0, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        ByteDataReader dis = new ByteDataReader(iobuffer);
        for (int i = 0; i < 25; i++) {
            long lv = dis.readLong();
            short readVersion = (short) (lv >> 48);
            if (i == 0 && lookupVersion != -1 && readVersion != lookupVersion) {
                throw new IOException("lookup version mismatch (old rd5?) lookups.dat=" + lookupVersion + " " + f.getName() + "=" + ((int) readVersion));
            }
            this.fileIndex[i] = 281474976710655L & lv;
        }
        long len = this.ra.length();
        long pos = this.fileIndex[24];
        if (len == pos) {
            return;
        }
        int extraLen = len - pos > ((long) 112) ? 112 + 1 : 112;
        if (len < ((long) extraLen) + pos) {
            throw new IOException("file of size " + len + " too short, should be " + (((long) extraLen) + pos));
        }
        this.ra.seek(pos);
        this.ra.readFully(iobuffer, 0, extraLen);
        ByteDataReader dis2 = new ByteDataReader(iobuffer);
        this.creationTime = dis2.readLong();
        int crcData = dis2.readInt();
        if (crcData == fileIndexCrc) {
            this.divisor = 80;
        } else if ((crcData ^ 2) == fileIndexCrc) {
            this.divisor = 32;
        } else {
            throw new IOException("top index checksum error");
        }
        this.fileHeaderCrcs = new int[25];
        for (int i2 = 0; i2 < 25; i2++) {
            this.fileHeaderCrcs[i2] = dis2.readInt();
        }
        try {
            this.elevationType = dis2.readByte();
        } catch (Exception e) {
        }
    }

    public void close() {
        if (this.ra != null) {
            try {
                this.ra.close();
            } catch (Exception e) {
            }
        }
    }
}

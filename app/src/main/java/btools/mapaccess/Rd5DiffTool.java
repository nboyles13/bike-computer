package btools.mapaccess;

import btools.codec.DataBuffers;
import btools.codec.MicroCache;
import btools.codec.MicroCache2;
import btools.codec.StatCoderContext;
import btools.util.Crc32;
import btools.util.ProgressListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class Rd5DiffTool implements ProgressListener {
    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            reEncode(new File(args[0]), new File(args[1]));
            return;
        }
        if (args[1].endsWith(".df5")) {
            if (args[0].endsWith(".df5")) {
                addDeltas(new File(args[0]), new File(args[1]), new File(args[2]));
                return;
            } else {
                recoverFromDelta(new File(args[0]), new File(args[1]), new File(args[2]), new Rd5DiffTool());
                return;
            }
        }
        diff2files(new File(args[0]), new File(args[1]), new File(args[2]));
    }

    @Override // btools.util.ProgressListener
    public void updateProgress(String task, int progress) {
        System.out.println(task + ": " + progress + "%");
    }

    @Override // btools.util.ProgressListener
    public boolean isCanceled() {
        return false;
    }

    private static long[] readFileIndex(DataInputStream dis, DataOutputStream dos) throws IOException {
        long[] fileIndex = new long[25];
        for (int i = 0; i < 25; i++) {
            long lv = dis.readLong();
            fileIndex[i] = 281474976710655L & lv;
            if (dos != null) {
                dos.writeLong(lv);
            }
        }
        return fileIndex;
    }

    private static long getTileStart(long[] index, int tileIndex) {
        if (tileIndex > 0) {
            return index[tileIndex - 1];
        }
        return 200L;
    }

    private static long getTileEnd(long[] index, int tileIndex) {
        return index[tileIndex];
    }

    private static int[] readPosIndex(DataInputStream dis, DataOutputStream dos) throws IOException {
        int[] posIndex = new int[1024];
        for (int i = 0; i < 1024; i++) {
            int iv = dis.readInt();
            posIndex[i] = iv;
            if (dos != null) {
                dos.writeInt(iv);
            }
        }
        return posIndex;
    }

    private static int getPosIdx(int[] posIdx, int idx) {
        if (idx == -1) {
            return 4096;
        }
        return posIdx[idx];
    }

    private static byte[] createMicroCache(int[] posIdx, int tileIdx, DataInputStream dis, boolean deltaMode) throws IOException {
        int size;
        if (posIdx == null || (size = getPosIdx(posIdx, tileIdx) - getPosIdx(posIdx, tileIdx - 1)) == 0) {
            return null;
        }
        if (deltaMode) {
            size = dis.readInt();
        }
        byte[] ab = new byte[size];
        dis.readFully(ab);
        return ab;
    }

    private static MicroCache createMicroCache(byte[] ab, DataBuffers dataBuffers) {
        if (ab == null || ab.length == 0) {
            return MicroCache.emptyCache();
        }
        StatCoderContext bc = new StatCoderContext(ab);
        return new MicroCache2(bc, dataBuffers, 0, 0, 32, null, null);
    }

    public static void diff2files(File f1, File f2, File outFile) throws Exception {
        Throwable th;
        DataBuffers dataBuffers;
        long bytesDiff;
        int subFileIdx;
        long bytesDiff2;
        int[] iArr;
        int[] posIndex;
        int[] posIdx1;
        int tileIdx;
        int[] iArr2;
        int subFileIdx2;
        DataBuffers dataBuffers2;
        MicroCache mc;
        long[] fileIndex2;
        byte[] abBuf1 = new byte[10485760];
        byte[] abBuf2 = new byte[10485760];
        int nodesDiff = 0;
        int diffedTiles = 0;
        DataInputStream dis1 = new DataInputStream(new BufferedInputStream(new FileInputStream(f1)));
        DataInputStream dis2 = new DataInputStream(new BufferedInputStream(new FileInputStream(f2)));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        MCOutputStream mcOut = new MCOutputStream(dos, abBuf1);
        long[] fileIndex1 = readFileIndex(dis1, null);
        long[] fileIndex22 = readFileIndex(dis2, dos);
        long t0 = System.currentTimeMillis();
        try {
            dataBuffers = new DataBuffers();
            bytesDiff = 0;
            subFileIdx = 0;
        } catch (Throwable th2) {
            th = th2;
        }
        while (true) {
            boolean z = false;
            if (subFileIdx >= 25) {
                break;
            }
            try {
                boolean hasData1 = getTileStart(fileIndex1, subFileIdx) < getTileEnd(fileIndex1, subFileIdx);
                boolean hasData2 = getTileStart(fileIndex22, subFileIdx) < getTileEnd(fileIndex22, subFileIdx);
                if (hasData1) {
                    iArr = null;
                    try {
                        posIndex = readPosIndex(dis1, null);
                    } catch (Throwable th3) {
                        th = th3;
                    }
                } else {
                    iArr = null;
                    posIndex = null;
                }
                int[] posIdx12 = posIndex;
                int[] posIdx2 = hasData2 ? readPosIndex(dis2, dos) : iArr;
                long bytesDiff3 = bytesDiff;
                int diffedTiles2 = diffedTiles;
                int diffedTiles3 = 0;
                int nodesDiff2 = nodesDiff;
                while (diffedTiles3 < 1024) {
                    int[] posIdx13 = posIdx12;
                    try {
                        byte[] ab1 = createMicroCache(posIdx13, diffedTiles3, dis1, z);
                        long[] fileIndex12 = fileIndex1;
                        int[] posIdx22 = posIdx2;
                        try {
                            byte[] ab2 = createMicroCache(posIdx22, diffedTiles3, dis2, z);
                            if (Arrays.equals(ab1, ab2)) {
                                try {
                                    posIdx1 = posIdx13;
                                    tileIdx = diffedTiles3;
                                    iArr2 = iArr;
                                    subFileIdx2 = subFileIdx;
                                    dataBuffers2 = dataBuffers;
                                    mc = MicroCache.emptyCache();
                                    fileIndex2 = fileIndex22;
                                } catch (Throwable th4) {
                                    th = th4;
                                }
                            } else {
                                fileIndex2 = fileIndex22;
                                DataBuffers dataBuffers3 = dataBuffers;
                                try {
                                    MicroCache mc1 = createMicroCache(ab1, dataBuffers3);
                                    MicroCache mc2 = createMicroCache(ab2, dataBuffers3);
                                    posIdx1 = posIdx13;
                                    tileIdx = diffedTiles3;
                                    iArr2 = iArr;
                                    subFileIdx2 = subFileIdx;
                                    dataBuffers2 = dataBuffers3;
                                    mc = new MicroCache2(mc1.getSize() + mc2.getSize(), abBuf2, 0, 0, 32);
                                    mc.calcDelta(mc1, mc2);
                                } catch (Throwable th5) {
                                    th = th5;
                                }
                            }
                            int len = mcOut.writeMC(mc);
                            if (len > 0) {
                                long bytesDiff4 = bytesDiff3 + ((long) len);
                                try {
                                    nodesDiff2 += mc.getSize();
                                    diffedTiles2++;
                                    bytesDiff3 = bytesDiff4;
                                } catch (Throwable th6) {
                                    th = th6;
                                }
                            }
                            diffedTiles3 = tileIdx + 1;
                            posIdx2 = posIdx22;
                            fileIndex22 = fileIndex2;
                            fileIndex1 = fileIndex12;
                            iArr = iArr2;
                            dataBuffers = dataBuffers2;
                            posIdx12 = posIdx1;
                            subFileIdx = subFileIdx2;
                            z = false;
                        } catch (Throwable th7) {
                            th = th7;
                        }
                    } catch (Throwable th8) {
                        th = th8;
                    }
                }
                int subFileIdx3 = subFileIdx;
                long[] fileIndex13 = fileIndex1;
                DataBuffers dataBuffers4 = dataBuffers;
                long[] fileIndex23 = fileIndex22;
                mcOut.finish();
                subFileIdx = subFileIdx3 + 1;
                nodesDiff = nodesDiff2;
                diffedTiles = diffedTiles2;
                fileIndex22 = fileIndex23;
                bytesDiff = bytesDiff3;
                fileIndex1 = fileIndex13;
                dataBuffers = dataBuffers4;
            } catch (Throwable th9) {
                th = th9;
            }
            try {
                dis1.close();
            } catch (Exception e) {
            }
            try {
                dis2.close();
            } catch (Exception e2) {
            }
            try {
                dos.close();
                throw th;
            } catch (Exception e3) {
                throw th;
            }
        }
        while (true) {
            try {
                int len2 = dis2.read(abBuf1);
                if (len2 < 0) {
                    break;
                }
                byte[] abBuf22 = abBuf2;
                bytesDiff2 = bytesDiff;
                try {
                    dos.write(abBuf1, 0, len2);
                    bytesDiff = bytesDiff2;
                    abBuf2 = abBuf22;
                } catch (Throwable th10) {
                    th = th10;
                }
                th = th10;
            } catch (Throwable th11) {
                th = th11;
            }
            dis1.close();
            dis2.close();
            dos.close();
            throw th;
        }
        long t1 = System.currentTimeMillis();
        bytesDiff2 = bytesDiff;
        System.out.println("nodesDiff=" + nodesDiff + " bytesDiff=" + bytesDiff2 + " diffedTiles=" + diffedTiles + " took " + (t1 - t0) + "ms");
        try {
            dis1.close();
        } catch (Exception e4) {
        }
        try {
            dis2.close();
        } catch (Exception e5) {
        }
        try {
            dos.close();
        } catch (Exception e6) {
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:103:0x0255, code lost:
    
        throw new java.lang.RuntimeException("size mismatch at " + r7 + "/" + r1 + " " + r0 + ">0");
     */
    /* JADX WARN: Code restructure failed: missing block: B:113:0x02cd, code lost:
    
        throw new java.lang.RuntimeException("size mismatch at " + r8 + "/" + r1 + " " + r0 + "<>" + (r7 + 4));
     */
    /* JADX WARN: Code restructure failed: missing block: B:128:0x032d, code lost:
    
        r31 = r7;
        r40 = r9;
        r26 = r10;
        r27 = r13;
        r21 = r6;
        r10 = r8;
        r14 = r12;
        r8 = r5;
     */
    /* JADX WARN: Code restructure failed: missing block: B:129:0x033e, code lost:
    
        r0.finish();
     */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x0359, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:132:0x035a, code lost:
    
        r1 = r40;
        r2 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:135:0x0372, code lost:
    
        r40 = r9;
        r27 = r13;
        r10 = r8;
        r14 = r12;
     */
    /* JADX WARN: Code restructure failed: missing block: B:136:0x037f, code lost:
    
        r1 = r40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:137:0x0381, code lost:
    
        r0 = r1.read(r4);
     */
    /* JADX WARN: Code restructure failed: missing block: B:138:0x0385, code lost:
    
        if (r0 >= 0) goto L154;
     */
    /* JADX WARN: Code restructure failed: missing block: B:139:0x0388, code lost:
    
        r2 = java.lang.System.currentTimeMillis();
        java.lang.System.out.println("recovering from diffs took " + (r2 - r27) + "ms");
     */
    /* JADX WARN: Code restructure failed: missing block: B:140:0x03ad, code lost:
    
        r0.close();
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x03c5, code lost:
    
        r10.write(r4, 0, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:156:0x03c8, code lost:
    
        r40 = r1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:157:0x03cb, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:158:0x03cc, code lost:
    
        r2 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:86:0x0186, code lost:
    
        r32 = r8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:88:0x01b6, code lost:
    
        throw new java.lang.RuntimeException("size mismatch at " + r5 + "/" + r1 + " " + r0 + "!=" + r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:89:0x01b7, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:90:0x01b8, code lost:
    
        r2 = r0;
        r1 = r26;
        r10 = r32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x01c1, code lost:
    
        r0 = move-exception;
     */
    /* JADX WARN: Code restructure failed: missing block: B:92:0x01c2, code lost:
    
        r2 = r0;
        r1 = r26;
        r10 = r8;
     */
    /* JADX WARN: Removed duplicated region for block: B:152:0x03c0  */
    /* JADX WARN: Removed duplicated region for block: B:236:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0138  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x013c A[Catch: all -> 0x02ec, TryCatch #5 {all -> 0x02ec, blocks: (B:66:0x0129, B:70:0x014a, B:69:0x013c), top: B:185:0x0129 }] */
    /* JADX WARN: Removed duplicated region for block: B:73:0x015a  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01db  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void recoverFromDelta(File f1, File f2, File outFile, ProgressListener progress) throws Throwable {
        DataOutputStream dos;
        DataInputStream dis2;
        boolean canceled;
        Throwable th;
        MCInputStream mcIn;
        DataBuffers dataBuffers;
        int lastPct;
        int subFileIdx;
        int[] iArr;
        int[] posIndex;
        int lastPct2;
        long[] fileIndex1;
        DataInputStream dis22;
        DataBuffers dataBuffers2;
        long t0;
        long[] fileIndex2;
        int subFileIdx2;
        int posIdx;
        MicroCache mc2;
        long[] fileIndex22;
        long[] fileIndex12;
        DataInputStream dis23;
        DataBuffers dataBuffers3;
        int subFileIdx3;
        int newTargetSize;
        ProgressListener progressListener = progress;
        if (f2.length() == 0) {
            copyFile(f1, outFile, progressListener);
            return;
        }
        byte[] abBuf1 = new byte[10485760];
        byte[] abBuf2 = new byte[10485760];
        boolean canceled2 = false;
        long t02 = System.currentTimeMillis();
        DataInputStream dis1 = new DataInputStream(new BufferedInputStream(new FileInputStream(f1)));
        DataInputStream dis24 = new DataInputStream(new BufferedInputStream(new FileInputStream(f2)));
        DataOutputStream dos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        long[] fileIndex13 = readFileIndex(dis1, null);
        long[] fileIndex23 = readFileIndex(dis24, dos2);
        try {
            dataBuffers = new DataBuffers();
            mcIn = new MCInputStream(dis24, dataBuffers);
            lastPct = -1;
            subFileIdx = 0;
        } catch (Throwable th2) {
            dos = dos2;
            dis2 = dis24;
            canceled = false;
            th = th2;
        }
        loop0: while (true) {
            if (subFileIdx >= 25) {
                break;
            }
            try {
                boolean hasData1 = getTileStart(fileIndex13, subFileIdx) < getTileEnd(fileIndex13, subFileIdx);
                boolean hasData2 = getTileStart(fileIndex23, subFileIdx) < getTileEnd(fileIndex23, subFileIdx);
                if (hasData1) {
                    iArr = null;
                    try {
                        posIndex = readPosIndex(dis1, null);
                    } catch (Throwable th3) {
                        th = th3;
                        dos = dos2;
                        dis2 = dis24;
                        canceled = canceled2;
                    }
                } else {
                    iArr = null;
                    posIndex = null;
                }
                int[] posIdx1 = posIndex;
                int[] posIdx2 = hasData2 ? readPosIndex(dis24, dos2) : iArr;
                int tileIdx = 0;
                lastPct2 = lastPct;
                while (true) {
                    if (tileIdx >= 1024) {
                        break;
                    }
                    try {
                        if (progress.isCanceled()) {
                            try {
                                dis1.close();
                            } catch (Exception e) {
                            }
                            try {
                                dis24.close();
                            } catch (Exception e2) {
                            }
                            try {
                                dos2.close();
                            } catch (Exception e3) {
                            }
                            if (1 != 0) {
                                outFile.delete();
                                return;
                            }
                            return;
                        }
                        long tileStart = getTileStart(fileIndex13, subFileIdx);
                        DataInputStream dis25 = dis24;
                        int[] posIdx12 = posIdx1;
                        if (posIdx12 == null) {
                            posIdx = 0;
                        } else {
                            try {
                                posIdx = getPosIdx(posIdx12, tileIdx - 1);
                            } catch (Throwable th4) {
                                dos = dos2;
                                canceled = canceled2;
                                dis2 = dis25;
                                th = th4;
                            }
                        }
                        long t03 = t02;
                        canceled = canceled2;
                        double bytesProcessed = tileStart + ((long) posIdx);
                        try {
                            int pct = (int) (((100.0d * bytesProcessed) / getTileEnd(fileIndex13, 24)) + 0.5d);
                            if (pct != lastPct2) {
                                try {
                                    progressListener.updateProgress("Applying delta", pct);
                                    lastPct2 = pct;
                                    try {
                                        byte[] ab1 = createMicroCache(posIdx12, tileIdx, dis1, false);
                                        mc2 = mcIn.readMC();
                                        int[] posIdx13 = posIdx2;
                                        int pct2 = posIdx13 != null ? 0 : getPosIdx(posIdx13, tileIdx) - getPosIdx(posIdx13, tileIdx - 1);
                                        int lastPct3 = lastPct2;
                                        if (mc2.getSize() != 0) {
                                            if (ab1 != null) {
                                                try {
                                                    dos2.write(ab1);
                                                } catch (Throwable th5) {
                                                    th = th5;
                                                    dos = dos2;
                                                    dis2 = dis25;
                                                }
                                            }
                                            if (ab1 == null) {
                                                fileIndex22 = fileIndex23;
                                                newTargetSize = 0;
                                            } else {
                                                fileIndex22 = fileIndex23;
                                                try {
                                                    newTargetSize = ab1.length;
                                                } catch (Throwable th6) {
                                                    th = th6;
                                                    dis2 = dis25;
                                                    dos = dos2;
                                                }
                                            }
                                            if (pct2 != newTargetSize) {
                                                break loop0;
                                            }
                                            fileIndex12 = fileIndex13;
                                            dis23 = dis25;
                                            dataBuffers3 = dataBuffers;
                                            dos = dos2;
                                            subFileIdx3 = subFileIdx;
                                            try {
                                                tileIdx++;
                                                progressListener = progress;
                                                subFileIdx = subFileIdx3;
                                                dos2 = dos;
                                                canceled2 = canceled;
                                                fileIndex23 = fileIndex22;
                                                posIdx2 = posIdx13;
                                                lastPct2 = lastPct3;
                                                dataBuffers = dataBuffers3;
                                                t02 = t03;
                                                fileIndex13 = fileIndex12;
                                                dis24 = dis23;
                                                posIdx1 = posIdx12;
                                            } catch (Throwable th7) {
                                                th = th7;
                                                dis2 = dis23;
                                            }
                                        } else {
                                            fileIndex22 = fileIndex23;
                                            fileIndex12 = fileIndex13;
                                            DataOutputStream dos3 = dos2;
                                            try {
                                                MicroCache mc1 = createMicroCache(ab1, dataBuffers);
                                                int subFileIdx4 = subFileIdx;
                                                dis23 = dis25;
                                                dataBuffers3 = dataBuffers;
                                                try {
                                                    MicroCache mc = new MicroCache2(mc1.getSize() + mc2.getSize(), abBuf2, 0, 0, 32);
                                                    mc.addDelta(mc1, mc2, false);
                                                    if (mc.size() != 0) {
                                                        subFileIdx3 = subFileIdx4;
                                                        int len = mc.encodeMicroCache(abBuf1);
                                                        dos = dos3;
                                                        dos.write(abBuf1, 0, len);
                                                        dos.writeInt(Crc32.crc(abBuf1, 0, len) ^ 2);
                                                        if (pct2 != len + 4) {
                                                            break loop0;
                                                        }
                                                        th = th7;
                                                        dis2 = dis23;
                                                    } else if (pct2 == 0) {
                                                        subFileIdx3 = subFileIdx4;
                                                        dos = dos3;
                                                    } else {
                                                        try {
                                                            break loop0;
                                                        } catch (Throwable th8) {
                                                            th = th8;
                                                            dos = dos3;
                                                            dis2 = dis23;
                                                        }
                                                    }
                                                    tileIdx++;
                                                    progressListener = progress;
                                                    subFileIdx = subFileIdx3;
                                                    dos2 = dos;
                                                    canceled2 = canceled;
                                                    fileIndex23 = fileIndex22;
                                                    posIdx2 = posIdx13;
                                                    lastPct2 = lastPct3;
                                                    dataBuffers = dataBuffers3;
                                                    t02 = t03;
                                                    fileIndex13 = fileIndex12;
                                                    dis24 = dis23;
                                                    posIdx1 = posIdx12;
                                                } catch (Throwable th9) {
                                                    dos = dos3;
                                                    th = th9;
                                                    dis2 = dis23;
                                                }
                                            } catch (Throwable th10) {
                                                dos = dos3;
                                                th = th10;
                                                dis2 = dis25;
                                            }
                                        }
                                    } catch (Throwable th11) {
                                        dos = dos2;
                                        th = th11;
                                        dis2 = dis25;
                                    }
                                } catch (Throwable th12) {
                                    dos = dos2;
                                    dis2 = dis25;
                                    th = th12;
                                }
                            } else {
                                byte[] ab12 = createMicroCache(posIdx12, tileIdx, dis1, false);
                                mc2 = mcIn.readMC();
                                int[] posIdx132 = posIdx2;
                                if (posIdx132 != null) {
                                }
                                int lastPct32 = lastPct2;
                                if (mc2.getSize() != 0) {
                                }
                            }
                        } catch (Throwable th13) {
                            dos = dos2;
                            dis2 = dis25;
                            th = th13;
                        }
                    } catch (Throwable th14) {
                        dos = dos2;
                        canceled = canceled2;
                        dis2 = dis24;
                        th = th14;
                    }
                }
            } catch (Throwable th15) {
                dos = dos2;
                canceled = canceled2;
                th = th15;
                dis2 = dis24;
            }
            try {
                dis1.close();
            } catch (Exception e4) {
            }
            try {
                dis2.close();
            } catch (Exception e5) {
            }
            try {
                dos.close();
            } catch (Exception e6) {
            }
            if (!canceled) {
                throw th;
            }
            outFile.delete();
            throw th;
            subFileIdx = subFileIdx2 + 1;
            progressListener = progress;
            lastPct = lastPct2;
            dos2 = dos;
            canceled2 = canceled;
            fileIndex23 = fileIndex2;
            dataBuffers = dataBuffers2;
            t02 = t0;
            fileIndex13 = fileIndex1;
            dis24 = dis22;
        }
        try {
            dos.close();
        } catch (Exception e7) {
        }
        if (canceled) {
            outFile.delete();
            return;
        }
        return;
        try {
            dis2.close();
        } catch (Exception e8) {
        }
        dos.close();
        if (canceled) {
        }
        if (canceled) {
        }
    }

    public static void copyFile(File f1, File outFile, ProgressListener progress) throws IOException {
        DataInputStream dis1 = new DataInputStream(new BufferedInputStream(new FileInputStream(f1)));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        int lastPct = -1;
        long sizeTotal = f1.length();
        long sizeRead = 0;
        try {
            byte[] buf = new byte[65536];
            while (!progress.isCanceled()) {
                int pct = (int) (((sizeRead * 100.0d) / (1 + sizeTotal)) + 0.5d);
                if (pct != lastPct) {
                    progress.updateProgress("Copying", pct);
                    lastPct = pct;
                }
                int len = dis1.read(buf);
                if (len > 0) {
                    sizeRead += (long) len;
                    dos.write(buf, 0, len);
                } else {
                    try {
                        dis1.close();
                    } catch (Exception e) {
                    }
                    try {
                        dos.close();
                    } catch (Exception e2) {
                    }
                    if (0 != 0) {
                        outFile.delete();
                        return;
                    }
                    return;
                }
            }
            try {
                dis1.close();
            } catch (Exception e3) {
            }
            try {
                dos.close();
            } catch (Exception e4) {
            }
            if (1 == 0) {
                return;
            }
            outFile.delete();
        } catch (Throwable th) {
            try {
                dis1.close();
            } catch (Exception e5) {
            }
            try {
                dos.close();
            } catch (Exception e6) {
            }
            if (0 != 0) {
                outFile.delete();
                throw th;
            }
            throw th;
        }
    }

    public static void addDeltas(File f1, File f2, File outFile) throws Exception {
        Throwable th;
        DataBuffers dataBuffers;
        MCInputStream mcIn1;
        MCInputStream mcIn2;
        MCOutputStream mcOut;
        int subFileIdx;
        Object posIndex;
        int tileIdx;
        int subFileIdx2;
        MCOutputStream mcOut2;
        long[] fileIndex2;
        int subFileIdx3;
        byte[] abBuf1 = new byte[10485760];
        byte[] abBuf2 = new byte[10485760];
        DataInputStream dis1 = new DataInputStream(new BufferedInputStream(new FileInputStream(f1)));
        DataInputStream dis2 = new DataInputStream(new BufferedInputStream(new FileInputStream(f2)));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        DataOutputStream dataOutputStream = null;
        long[] fileIndex1 = readFileIndex(dis1, null);
        long[] fileIndex22 = readFileIndex(dis2, dos);
        long t0 = System.currentTimeMillis();
        try {
            dataBuffers = new DataBuffers();
            mcIn1 = new MCInputStream(dis1, dataBuffers);
            mcIn2 = new MCInputStream(dis2, dataBuffers);
            mcOut = new MCOutputStream(dos, abBuf1);
            subFileIdx = 0;
        } catch (Throwable th2) {
            th = th2;
        }
        while (true) {
            if (subFileIdx >= 25) {
                break;
            }
            DataBuffers dataBuffers2 = dataBuffers;
            int i = 1;
            boolean hasData1 = getTileStart(fileIndex1, subFileIdx) < getTileEnd(fileIndex1, subFileIdx);
            boolean hasData2 = getTileStart(fileIndex22, subFileIdx) < getTileEnd(fileIndex22, subFileIdx);
            if (!hasData1) {
                posIndex = dataOutputStream;
            } else {
                try {
                    posIndex = readPosIndex(dis1, dataOutputStream);
                } catch (Throwable th3) {
                    th = th3;
                }
            }
            Object posIndex2 = hasData2 ? readPosIndex(dis2, dos) : dataOutputStream;
            int tileIdx2 = 0;
            while (tileIdx2 < 1024) {
                MicroCache mc = mcIn1.readMC();
                MicroCache mc2 = mcIn2.readMC();
                if (mc.getSize() == 0 && mc2.getSize() == 0) {
                    tileIdx = tileIdx2;
                    subFileIdx2 = subFileIdx;
                    mcOut2 = mcOut;
                    subFileIdx3 = i;
                    fileIndex2 = fileIndex22;
                } else {
                    int subFileIdx4 = mc.getSize() + mc2.getSize();
                    tileIdx = tileIdx2;
                    subFileIdx2 = subFileIdx;
                    mcOut2 = mcOut;
                    fileIndex2 = fileIndex22;
                    try {
                        mc = new MicroCache2(subFileIdx4, abBuf2, 0, 0, 32);
                        subFileIdx3 = 1;
                        mc.addDelta(mc, mc2, true);
                    } catch (Throwable th4) {
                        th = th4;
                    }
                }
                mcOut = mcOut2;
                mcOut.writeMC(mc);
                tileIdx2 = tileIdx + 1;
                i = subFileIdx3;
                subFileIdx = subFileIdx2;
                fileIndex22 = fileIndex2;
            }
            mcIn1.finish();
            mcIn2.finish();
            mcOut.finish();
            subFileIdx++;
            dataBuffers = dataBuffers2;
            fileIndex22 = fileIndex22;
            dataOutputStream = null;
            th = th4;
            try {
                dis1.close();
            } catch (Exception e) {
            }
            try {
                dis2.close();
            } catch (Exception e2) {
            }
            try {
                dos.close();
                throw th;
            } catch (Exception e3) {
                throw th;
            }
        }
        while (true) {
            int len = dis2.read(abBuf1);
            if (len < 0) {
                break;
            } else {
                dos.write(abBuf1, 0, len);
            }
        }
        long t1 = System.currentTimeMillis();
        System.out.println("adding diffs took " + (t1 - t0) + "ms");
        try {
            dis1.close();
        } catch (Exception e4) {
        }
        try {
            dis2.close();
        } catch (Exception e5) {
        }
        try {
            dos.close();
        } catch (Exception e6) {
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v0 */
    /* JADX WARN: Type inference failed for: r11v5, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r11v7 */
    public static void reEncode(File f1, File outFile) throws Exception {
        DataBuffers dataBuffers;
        byte[] abBuf1 = new byte[10485760];
        DataInputStream dis1 = new DataInputStream(new BufferedInputStream(new FileInputStream(f1)));
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
        long[] fileIndex1 = readFileIndex(dis1, dataOutputStream);
        long t0 = System.currentTimeMillis();
        try {
            DataBuffers dataBuffers2 = new DataBuffers();
            int subFileIdx = 0;
            while (true) {
                ?? r11 = 0;
                if (subFileIdx >= 25) {
                    break;
                }
                boolean hasData1 = getTileStart(fileIndex1, subFileIdx) < getTileEnd(fileIndex1, subFileIdx);
                int[] posIdx1 = hasData1 ? readPosIndex(dis1, dataOutputStream) : null;
                int tileIdx = 0;
                while (tileIdx < 1024) {
                    byte[] ab1 = createMicroCache(posIdx1, tileIdx, dis1, r11);
                    if (ab1 == null) {
                        dataBuffers = dataBuffers2;
                    } else {
                        MicroCache mc1 = createMicroCache(ab1, dataBuffers2);
                        int len = mc1.encodeMicroCache(abBuf1);
                        dataBuffers = dataBuffers2;
                        dataOutputStream.write(abBuf1, r11, len);
                        dataOutputStream.writeInt(Crc32.crc(abBuf1, r11, len) ^ 2);
                    }
                    tileIdx++;
                    dataBuffers2 = dataBuffers;
                    r11 = 0;
                }
                subFileIdx++;
            }
            while (true) {
                int len2 = dis1.read(abBuf1);
                if (len2 < 0) {
                    break;
                } else {
                    dataOutputStream.write(abBuf1, 0, len2);
                }
            }
            long t1 = System.currentTimeMillis();
            System.out.println("re-encoding took " + (t1 - t0) + "ms");
            try {
                dis1.close();
            } catch (Exception e) {
            }
            try {
                dataOutputStream.close();
            } catch (Exception e2) {
            }
        } finally {
        }
    }

    private static class MCOutputStream {
        private byte[] buffer;
        private DataOutputStream dos;
        private short skips = 0;

        public MCOutputStream(DataOutputStream dos, byte[] buffer) {
            this.dos = dos;
            this.buffer = buffer;
        }

        public int writeMC(MicroCache mc) throws Exception {
            if (mc.getSize() == 0) {
                this.skips = (short) (this.skips + 1);
                return 0;
            }
            this.dos.writeShort(this.skips);
            this.skips = (short) 0;
            int len = mc.encodeMicroCache(this.buffer);
            if (len == 0) {
                throw new IllegalArgumentException("encoded buffer of non-empty micro-cache cannot be empty");
            }
            this.dos.writeInt(len);
            this.dos.write(this.buffer, 0, len);
            return len;
        }

        public void finish() throws Exception {
            if (this.skips > 0) {
                this.dos.writeShort(this.skips);
                this.skips = (short) 0;
            }
        }
    }

    private static class MCInputStream {
        private DataBuffers dataBuffers;
        private DataInputStream dis;
        private short skips = -1;
        private MicroCache empty = MicroCache.emptyCache();

        public MCInputStream(DataInputStream dis, DataBuffers dataBuffers) {
            this.dis = dis;
            this.dataBuffers = dataBuffers;
        }

        public MicroCache readMC() throws IOException {
            if (this.skips < 0) {
                this.skips = this.dis.readShort();
            }
            MicroCache mc = this.empty;
            if (this.skips == 0) {
                int size = this.dis.readInt();
                byte[] ab = new byte[size];
                this.dis.readFully(ab);
                StatCoderContext bc = new StatCoderContext(ab);
                mc = new MicroCache2(bc, this.dataBuffers, 0, 0, 32, null, null);
            }
            int size2 = this.skips;
            this.skips = (short) (size2 - 1);
            return mc;
        }

        public void finish() {
            this.skips = (short) -1;
        }
    }
}

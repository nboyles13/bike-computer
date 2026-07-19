package btools.mapaccess;

import btools.codec.DataBuffers;
import btools.codec.MicroCache;
import btools.codec.MicroCache2;
import btools.codec.StatCoderContext;
import btools.codec.TagValueValidator;
import btools.codec.WaypointMatcher;
import btools.util.ByteDataReader;
import btools.util.Crc32;
import java.io.IOException;
import java.io.RandomAccessFile;
import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes.dex */
public final class OsmFile {
    private int cellsize;
    private int divisor;
    protected byte elevationType;
    private long fileOffset;
    public String filename;
    private int indexsize;
    private RandomAccessFile is;
    public int latDegree;
    public int lonDegree;
    private MicroCache[] microCaches;
    private int[] posIdx;

    public OsmFile(PhysicalFile rafile, int lonDegree, int latDegree, DataBuffers dataBuffers) throws IOException {
        this.is = null;
        this.elevationType = (byte) 3;
        this.lonDegree = lonDegree;
        this.latDegree = latDegree;
        int lonMod5 = lonDegree % 5;
        int latMod5 = latDegree % 5;
        int tileIndex = (lonMod5 * 5) + latMod5;
        if (rafile != null) {
            this.divisor = rafile.divisor;
            this.elevationType = rafile.elevationType;
            this.cellsize = DurationKt.NANOS_IN_MILLIS / this.divisor;
            int ncaches = this.divisor * this.divisor;
            this.indexsize = ncaches * 4;
            byte[] iobuffer = dataBuffers.iobuffer;
            this.filename = rafile.fileName;
            long[] index = rafile.fileIndex;
            this.fileOffset = tileIndex > 0 ? index[tileIndex - 1] : 200L;
            if (this.fileOffset == index[tileIndex]) {
                return;
            }
            this.is = rafile.ra;
            this.posIdx = new int[ncaches];
            this.microCaches = new MicroCache[ncaches];
            this.is.seek(this.fileOffset);
            this.is.readFully(iobuffer, 0, this.indexsize);
            if (rafile.fileHeaderCrcs != null) {
                int headerCrc = Crc32.crc(iobuffer, 0, this.indexsize);
                if (rafile.fileHeaderCrcs[tileIndex] != headerCrc) {
                    throw new IOException("sub index checksum error");
                }
            }
            ByteDataReader dis = new ByteDataReader(iobuffer);
            for (int i = 0; i < ncaches; i++) {
                this.posIdx[i] = dis.readInt();
            }
        }
    }

    public boolean hasData() {
        return this.microCaches != null;
    }

    public MicroCache getMicroCache(int ilon, int ilat) {
        int lonIdx = ilon / this.cellsize;
        int latIdx = ilat / this.cellsize;
        int subIdx = ((latIdx - (this.divisor * this.latDegree)) * this.divisor) + (lonIdx - (this.divisor * this.lonDegree));
        return this.microCaches[subIdx];
    }

    public MicroCache createMicroCache(int ilon, int ilat, DataBuffers dataBuffers, TagValueValidator wayValidator, WaypointMatcher waypointMatcher, OsmNodesMap hollowNodes) throws Exception {
        int lonIdx = ilon / this.cellsize;
        int latIdx = ilat / this.cellsize;
        MicroCache segment = createMicroCache(lonIdx, latIdx, dataBuffers, wayValidator, waypointMatcher, true, hollowNodes);
        int subIdx = ((latIdx - (this.divisor * this.latDegree)) * this.divisor) + (lonIdx - (this.divisor * this.lonDegree));
        this.microCaches[subIdx] = segment;
        return segment;
    }

    private int getPosIdx(int idx) {
        return idx == -1 ? this.indexsize : this.posIdx[idx];
    }

    public int getDataInputForSubIdx(int subIdx, byte[] iobuffer) throws IOException {
        int startPos = getPosIdx(subIdx - 1);
        int endPos = getPosIdx(subIdx);
        int size = endPos - startPos;
        if (size > 0) {
            this.is.seek(this.fileOffset + ((long) startPos));
            if (size <= iobuffer.length) {
                this.is.readFully(iobuffer, 0, size);
            }
        }
        return size;
    }

    public MicroCache createMicroCache(int lonIdx, int latIdx, DataBuffers dataBuffers, TagValueValidator wayValidator, WaypointMatcher waypointMatcher, boolean reallyDecode, OsmNodesMap hollowNodes) throws Throwable {
        byte[] ab;
        int asize;
        int i;
        String str;
        String str2;
        int subIdx = ((latIdx - (this.divisor * this.latDegree)) * this.divisor) + (lonIdx - (this.divisor * this.lonDegree));
        byte[] ab2 = dataBuffers.iobuffer;
        int asize2 = getDataInputForSubIdx(subIdx, ab2);
        if (asize2 == 0) {
            return MicroCache.emptyCache();
        }
        if (asize2 > ab2.length) {
            byte[] ab3 = new byte[asize2];
            ab = ab3;
            asize = getDataInputForSubIdx(subIdx, ab3);
        } else {
            ab = ab2;
            asize = asize2;
        }
        StatCoderContext bc = new StatCoderContext(ab);
        if (!reallyDecode) {
            int readBytes = (bc.getReadingBitPosition() + 7) >> 3;
            if (readBytes == asize - 4) {
                return null;
            }
            int crcData = Crc32.crc(ab, 0, asize - 4);
            int crcFooter = new ByteDataReader(ab, asize - 4).readInt();
            if (crcData == crcFooter) {
                throw new IOException("old, unsupported data-format");
            }
            if ((crcData ^ 2) == crcFooter) {
                return null;
            }
            throw new IOException("checkum error");
        }
        try {
            if (hollowNodes == null) {
                MicroCache2 microCache2 = new MicroCache2(bc, dataBuffers, lonIdx, latIdx, this.divisor, wayValidator, waypointMatcher);
                int readBytes2 = (bc.getReadingBitPosition() + 7) >> 3;
                if (readBytes2 != asize - 4) {
                    int crcData2 = Crc32.crc(ab, 0, asize - 4);
                    int crcFooter2 = new ByteDataReader(ab, asize - 4).readInt();
                    if (crcData2 == crcFooter2) {
                        throw new IOException("old, unsupported data-format");
                    }
                    if ((crcData2 ^ 2) != crcFooter2) {
                        throw new IOException("checkum error");
                    }
                }
                return microCache2;
            }
            i = 0;
            try {
                new DirectWeaver(bc, dataBuffers, lonIdx, latIdx, this.divisor, wayValidator, waypointMatcher, hollowNodes);
                MicroCache microCache = MicroCache.emptyNonVirgin;
                int readBytes3 = (bc.getReadingBitPosition() + 7) >> 3;
                if (readBytes3 != asize - 4) {
                    int crcData3 = Crc32.crc(ab, 0, asize - 4);
                    int crcFooter3 = new ByteDataReader(ab, asize - 4).readInt();
                    if (crcData3 == crcFooter3) {
                        throw new IOException("old, unsupported data-format");
                    }
                    if ((crcData3 ^ 2) != crcFooter3) {
                        throw new IOException("checkum error");
                    }
                }
                return microCache;
            } catch (Throwable th) {
                th = th;
                str = "old, unsupported data-format";
                str2 = "checkum error";
                int readBytes4 = (bc.getReadingBitPosition() + 7) >> 3;
                if (readBytes4 != asize - 4) {
                    int crcData4 = Crc32.crc(ab, i, asize - 4);
                    int crcFooter4 = new ByteDataReader(ab, asize - 4).readInt();
                    if (crcData4 == crcFooter4) {
                        throw new IOException(str);
                    }
                    if ((crcData4 ^ 2) != crcFooter4) {
                        throw new IOException(str2);
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            i = 0;
            str = "old, unsupported data-format";
            str2 = "checkum error";
        }
    }

    long setGhostState() {
        long sum = 0;
        int nc = this.microCaches == null ? 0 : this.microCaches.length;
        for (int i = 0; i < nc; i++) {
            MicroCache mc = this.microCaches[i];
            if (mc != null) {
                if (mc.virgin) {
                    mc.ghost = true;
                    sum += (long) mc.getDataSize();
                } else {
                    this.microCaches[i] = null;
                }
            }
        }
        return sum;
    }

    long collectAll() {
        long deleted = 0;
        int nc = this.microCaches == null ? 0 : this.microCaches.length;
        for (int i = 0; i < nc; i++) {
            MicroCache mc = this.microCaches[i];
            if (mc != null && !mc.ghost) {
                deleted += (long) mc.collect(0);
            }
        }
        return deleted;
    }

    long cleanGhosts() {
        int nc = this.microCaches == null ? 0 : this.microCaches.length;
        for (int i = 0; i < nc; i++) {
            MicroCache mc = this.microCaches[i];
            if (mc != null && mc.ghost) {
                this.microCaches[i] = null;
            }
        }
        return 0L;
    }

    void clean(boolean all) {
        int nc = this.microCaches == null ? 0 : this.microCaches.length;
        for (int i = 0; i < nc; i++) {
            MicroCache mc = this.microCaches[i];
            if (mc != null && (all || !mc.virgin)) {
                this.microCaches[i] = null;
            }
        }
    }
}

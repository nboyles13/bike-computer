package btools.util;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public final class DiffCoderDataInputStream extends DataInputStream {
    private long[] lastValues;

    public DiffCoderDataInputStream(InputStream is) {
        super(is);
        this.lastValues = new long[10];
    }

    public long readDiffed(int idx) throws IOException {
        long d = readSigned();
        long v = this.lastValues[idx] + d;
        this.lastValues[idx] = v;
        return v;
    }

    public long readSigned() throws IOException {
        long v = readUnsigned();
        return (1 & v) == 0 ? v >> 1 : -(v >> 1);
    }

    public long readUnsigned() throws IOException {
        long v = 0;
        int shift = 0;
        while (true) {
            long i7 = readByte() & UByte.MAX_VALUE;
            v |= (127 & i7) << shift;
            if ((128 & i7) == 0) {
                return v;
            }
            shift += 7;
        }
    }
}

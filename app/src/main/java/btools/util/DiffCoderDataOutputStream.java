package btools.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public final class DiffCoderDataOutputStream extends DataOutputStream {
    private long[] lastValues;

    public DiffCoderDataOutputStream(OutputStream os) {
        super(os);
        this.lastValues = new long[10];
    }

    public void writeDiffed(long v, int idx) throws IOException {
        long d = v - this.lastValues[idx];
        this.lastValues[idx] = v;
        writeSigned(d);
    }

    public void writeSigned(long v) throws IOException {
        writeUnsigned(v < 0 ? ((-v) << 1) | 1 : v << 1);
    }

    public void writeUnsigned(long v) throws IOException {
        do {
            long i7 = 127 & v;
            v >>= 7;
            if (v != 0) {
                i7 |= 128;
            }
            writeByte((byte) (255 & i7));
        } while (v != 0);
    }
}

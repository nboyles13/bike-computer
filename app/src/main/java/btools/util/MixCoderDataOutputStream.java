package btools.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public final class MixCoderDataOutputStream extends DataOutputStream {
    private int b;
    private int bm;
    private int diffshift;
    private int lastLastValue;
    private int lastValue;
    private int repCount;
    public static int[] diffs = new int[100];
    public static int[] counts = new int[100];

    public MixCoderDataOutputStream(OutputStream os) {
        super(os);
        this.bm = 1;
        this.b = 0;
    }

    public void writeMixed(int v) throws IOException {
        if (v != this.lastValue && this.repCount > 0) {
            int d = this.lastValue - this.lastLastValue;
            this.lastLastValue = this.lastValue;
            encodeBit(d < 0);
            if (d < 0) {
                d = -d;
            }
            encodeVarBits(d - this.diffshift);
            encodeVarBits(this.repCount - 1);
            if (d < 100) {
                int[] iArr = diffs;
                iArr[d] = iArr[d] + 1;
            }
            if (this.repCount < 100) {
                int[] iArr2 = counts;
                int i = this.repCount;
                iArr2[i] = iArr2[i] + 1;
            }
            this.diffshift = 1;
            this.repCount = 0;
        }
        this.lastValue = v;
        this.repCount++;
    }

    @Override // java.io.DataOutputStream, java.io.FilterOutputStream, java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        int v = this.lastValue;
        writeMixed(v + 1);
        this.lastValue = v;
        this.repCount = 0;
        if (this.bm > 1) {
            writeByte((byte) this.b);
        }
    }

    public void encodeBit(boolean value) throws IOException {
        if (this.bm == 256) {
            writeByte((byte) this.b);
            this.bm = 1;
            this.b = 0;
        }
        if (value) {
            this.b |= this.bm;
        }
        this.bm <<= 1;
    }

    public void encodeVarBits(int value) throws IOException {
        int range = 0;
        while (value > range) {
            encodeBit(false);
            value -= range + 1;
            range = (range * 2) + 1;
        }
        encodeBit(true);
        encodeBounded(range, value);
    }

    public void encodeBounded(int max, int value) throws IOException {
        for (int im = 1; im <= max; im <<= 1) {
            if (this.bm == 256) {
                writeByte((byte) this.b);
                this.bm = 1;
                this.b = 0;
            }
            if ((value & im) != 0) {
                this.b |= this.bm;
                max -= im;
            }
            this.bm <<= 1;
        }
    }

    public static void stats() {
        for (int i = 1; i < 100; i++) {
            System.out.println("diff[" + i + "] = " + diffs[i]);
        }
        for (int i2 = 1; i2 < 100; i2++) {
            System.out.println("counts[" + i2 + "] = " + counts[i2]);
        }
    }
}

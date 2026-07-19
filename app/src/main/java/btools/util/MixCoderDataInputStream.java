package btools.util;

import androidx.core.view.ViewCompat;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public final class MixCoderDataInputStream extends DataInputStream {
    private int b;
    private int bits;
    private int diffshift;
    private int lastValue;
    private int repCount;
    private static final int[] vl_values = BitCoderContext.vl_values;
    private static final int[] vl_length = BitCoderContext.vl_length;

    public MixCoderDataInputStream(InputStream is) {
        super(is);
    }

    public int readMixed() throws IOException {
        if (this.repCount == 0) {
            boolean negative = decodeBit();
            int d = decodeVarBits() + this.diffshift;
            this.repCount = decodeVarBits() + 1;
            this.lastValue += negative ? -d : d;
            this.diffshift = 1;
        }
        this.repCount--;
        return this.lastValue;
    }

    public boolean decodeBit() throws IOException {
        fillBuffer();
        boolean value = (this.b & 1) != 0;
        this.b >>>= 1;
        this.bits--;
        return value;
    }

    public int decodeVarBits2() throws IOException {
        int range = 0;
        while (!decodeBit()) {
            range = (range * 2) + 1;
        }
        return decodeBounded(range) + range;
    }

    public int decodeBounded(int max) throws IOException {
        int value = 0;
        for (int im = 1; (value | im) <= max; im <<= 1) {
            if (decodeBit()) {
                value |= im;
            }
        }
        return value;
    }

    public int decodeVarBits() throws IOException {
        fillBuffer();
        int b12 = this.b & 4095;
        int len = vl_length[b12];
        if (len <= 12) {
            this.b >>>= len;
            this.bits -= len;
            return vl_values[b12];
        }
        if (len <= 23) {
            int len2 = len >> 1;
            this.b >>>= len2 + 1;
            int mask = (-1) >>> (32 - len2);
            int mask2 = mask + (this.b & mask);
            this.b >>>= len2;
            this.bits -= len;
            return mask2;
        }
        if ((this.b & ViewCompat.MEASURED_SIZE_MASK) != 0) {
            this.b >>>= 12;
            int len3 = (vl_length[this.b & 4095] >> 1) + 1;
            this.b >>>= len3;
            int len22 = len3 + 11;
            this.bits -= len22 + 1;
            fillBuffer();
            int mask3 = (-1) >>> (32 - len22);
            int mask4 = mask3 + (this.b & mask3);
            this.b >>>= len22;
            this.bits -= len22;
            return mask4;
        }
        return decodeVarBits2();
    }

    private void fillBuffer() throws IOException {
        while (this.bits < 24) {
            int nextByte = read();
            if (nextByte != -1) {
                this.b |= (nextByte & 255) << this.bits;
            }
            this.bits += 8;
        }
    }
}

package btools.util;

import androidx.core.view.ViewCompat;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class BitCoderContext {
    private byte[] ab;
    private int b;
    private int bits;
    private int idx = -1;
    private int idxMax;
    public static final int[] vl_values = new int[4096];
    public static final int[] vl_length = new int[4096];
    private static final int[] vc_values = new int[4096];
    private static final int[] vc_length = new int[4096];
    private static final int[] reverse_byte = new int[256];
    private static final int[] bm2bits = new int[256];

    static {
        BitCoderContext bc = new BitCoderContext(new byte[4]);
        for (int i = 0; i < 4096; i++) {
            bc.reset();
            bc.bits = 14;
            bc.b = i + 4096;
            int b0 = bc.getReadingBitPosition();
            vl_values[i] = bc.decodeVarBits2();
            vl_length[i] = bc.getReadingBitPosition() - b0;
        }
        for (int i2 = 0; i2 < 4096; i2++) {
            bc.reset();
            int b02 = bc.getWritingBitPosition();
            bc.encodeVarBits2(i2);
            vc_values[i2] = bc.b;
            vc_length[i2] = bc.getWritingBitPosition() - b02;
        }
        for (int i3 = 0; i3 < 1024; i3++) {
            bc.reset();
            bc.bits = 14;
            bc.b = i3 + 4096;
            int b03 = bc.getReadingBitPosition();
            vl_values[i3] = bc.decodeVarBits2();
            vl_length[i3] = bc.getReadingBitPosition() - b03;
        }
        for (int b = 0; b < 256; b++) {
            int r = 0;
            for (int i4 = 0; i4 < 8; i4++) {
                if (((1 << i4) & b) != 0) {
                    r |= 1 << (7 - i4);
                }
            }
            reverse_byte[b] = r;
        }
        for (int b2 = 0; b2 < 8; b2++) {
            bm2bits[1 << b2] = b2;
        }
    }

    public BitCoderContext(byte[] ab) {
        this.ab = ab;
        this.idxMax = ab.length - 1;
    }

    public final void reset(byte[] ab) {
        this.ab = ab;
        this.idxMax = ab.length - 1;
        reset();
    }

    public final void reset() {
        this.idx = -1;
        this.bits = 0;
        this.b = 0;
    }

    public final void encodeVarBits2(int value) {
        int range = 0;
        while (value > range) {
            encodeBit(false);
            value -= range + 1;
            range = (range * 2) + 1;
        }
        encodeBit(true);
        encodeBounded(range, value);
    }

    public final void encodeVarBits(int value) {
        if ((value & 4095) == value) {
            flushBuffer();
            this.b |= vc_values[value] << this.bits;
            this.bits += vc_length[value];
            return;
        }
        encodeVarBits2(value);
    }

    public final int decodeVarBits2() {
        int range = 0;
        while (!decodeBit()) {
            range = (range * 2) + 1;
        }
        return decodeBounded(range) + range;
    }

    public final int decodeVarBits() {
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

    public final void encodeBit(boolean value) {
        if (this.bits > 31) {
            byte[] bArr = this.ab;
            int i = this.idx + 1;
            this.idx = i;
            bArr[i] = (byte) (this.b & 255);
            this.b >>>= 8;
            this.bits -= 8;
        }
        if (value) {
            this.b |= 1 << this.bits;
        }
        this.bits++;
    }

    public final boolean decodeBit() {
        if (this.bits == 0) {
            this.bits = 8;
            byte[] bArr = this.ab;
            int i = this.idx + 1;
            this.idx = i;
            this.b = bArr[i] & UByte.MAX_VALUE;
        }
        boolean value = (this.b & 1) != 0;
        this.b >>>= 1;
        this.bits--;
        return value;
    }

    public final void encodeBounded(int max, int value) {
        for (int im = 1; im <= max; im <<= 1) {
            if ((value & im) != 0) {
                encodeBit(true);
                max -= im;
            } else {
                encodeBit(false);
            }
        }
    }

    public final int decodeBounded(int max) {
        int value = 0;
        for (int im = 1; (value | im) <= max; im <<= 1) {
            if (this.bits == 0) {
                this.bits = 8;
                byte[] bArr = this.ab;
                int i = this.idx + 1;
                this.idx = i;
                this.b = bArr[i] & UByte.MAX_VALUE;
            }
            if ((this.b & 1) != 0) {
                value |= im;
            }
            this.b >>>= 1;
            this.bits--;
        }
        return value;
    }

    public final int decodeBits(int count) {
        fillBuffer();
        int mask = (-1) >>> (32 - count);
        int value = this.b & mask;
        this.b >>>= count;
        this.bits -= count;
        return value;
    }

    public final int decodeBitsReverse(int count) {
        fillBuffer();
        int value = 0;
        while (count > 8) {
            value = (value << 8) | reverse_byte[this.b & 255];
            this.b >>= 8;
            count -= 8;
            this.bits -= 8;
            fillBuffer();
        }
        int value2 = (value << count) | (reverse_byte[this.b & 255] >> (8 - count));
        this.bits -= count;
        this.b >>= count;
        return value2;
    }

    private void fillBuffer() {
        while (this.bits < 24) {
            int i = this.idx;
            this.idx = i + 1;
            if (i < this.idxMax) {
                this.b |= (this.ab[this.idx] & UByte.MAX_VALUE) << this.bits;
            }
            this.bits += 8;
        }
    }

    private void flushBuffer() {
        while (this.bits > 7) {
            byte[] bArr = this.ab;
            int i = this.idx + 1;
            this.idx = i;
            bArr[i] = (byte) (this.b & 255);
            this.b >>>= 8;
            this.bits -= 8;
        }
    }

    public final int closeAndGetEncodedLength() {
        flushBuffer();
        if (this.bits > 0) {
            byte[] bArr = this.ab;
            int i = this.idx + 1;
            this.idx = i;
            bArr[i] = (byte) (this.b & 255);
        }
        return this.idx + 1;
    }

    public final int getWritingBitPosition() {
        return (this.idx << 3) + 8 + this.bits;
    }

    public final int getReadingBitPosition() {
        return ((this.idx << 3) + 8) - this.bits;
    }

    public final void setReadingBitPosition(int pos) {
        this.idx = pos >>> 3;
        this.bits = ((this.idx << 3) + 8) - pos;
        this.b = this.ab[this.idx] & UByte.MAX_VALUE;
        this.b >>>= 8 - this.bits;
    }

    public static void main(String[] args) {
        byte[] ab = new byte[581969];
        BitCoderContext ctx = new BitCoderContext(ab);
        for (int i = 0; i < 31; i++) {
            ctx.encodeVarBits((1 << i) + 3);
        }
        for (int i2 = 0; i2 < 100000; i2 += 13) {
            ctx.encodeVarBits(i2);
        }
        ctx.closeAndGetEncodedLength();
        BitCoderContext ctx2 = new BitCoderContext(ab);
        for (int i3 = 0; i3 < 31; i3++) {
            int value = ctx2.decodeVarBits();
            int v0 = (1 << i3) + 3;
            if (v0 != value) {
                throw new RuntimeException("value mismatch value=" + value + "v0=" + v0);
            }
        }
        for (int i4 = 0; i4 < 100000; i4 += 13) {
            int value2 = ctx2.decodeVarBits();
            if (value2 != i4) {
                throw new RuntimeException("value mismatch i=" + i4 + "v=" + value2);
            }
        }
    }
}

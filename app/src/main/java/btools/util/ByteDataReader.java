package btools.util;

import kotlin.UByte;
import kotlin.jvm.internal.ByteCompanionObject;

/* JADX INFO: loaded from: classes.dex */
public class ByteDataReader {
    protected byte[] ab;
    protected int aboffset;
    protected int aboffsetEnd;

    public ByteDataReader(byte[] byteArray) {
        this.ab = byteArray;
        this.aboffsetEnd = this.ab == null ? 0 : this.ab.length;
    }

    public ByteDataReader(byte[] byteArray, int offset) {
        this.ab = byteArray;
        this.aboffset = offset;
        this.aboffsetEnd = this.ab == null ? 0 : this.ab.length;
    }

    public final void reset(byte[] byteArray) {
        this.ab = byteArray;
        this.aboffset = 0;
        this.aboffsetEnd = this.ab != null ? this.ab.length : 0;
    }

    public final int readInt() {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        int i3 = bArr[i] & UByte.MAX_VALUE;
        byte[] bArr2 = this.ab;
        int i2 = this.aboffset;
        this.aboffset = i2 + 1;
        int i22 = bArr2[i2] & UByte.MAX_VALUE;
        byte[] bArr3 = this.ab;
        int i4 = this.aboffset;
        this.aboffset = i4 + 1;
        int i1 = bArr3[i4] & UByte.MAX_VALUE;
        byte[] bArr4 = this.ab;
        int i5 = this.aboffset;
        this.aboffset = i5 + 1;
        int i0 = bArr4[i5] & UByte.MAX_VALUE;
        return (i3 << 24) + (i22 << 16) + (i1 << 8) + i0;
    }

    public final long readLong() {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        long i7 = bArr[i] & UByte.MAX_VALUE;
        byte[] bArr2 = this.ab;
        int i2 = this.aboffset;
        this.aboffset = i2 + 1;
        long i6 = bArr2[i2] & UByte.MAX_VALUE;
        byte[] bArr3 = this.ab;
        int i3 = this.aboffset;
        this.aboffset = i3 + 1;
        long i5 = bArr3[i3] & UByte.MAX_VALUE;
        byte[] bArr4 = this.ab;
        int i4 = this.aboffset;
        this.aboffset = i4 + 1;
        long i42 = bArr4[i4] & UByte.MAX_VALUE;
        byte[] bArr5 = this.ab;
        int i8 = this.aboffset;
        this.aboffset = i8 + 1;
        long i32 = bArr5[i8] & UByte.MAX_VALUE;
        byte[] bArr6 = this.ab;
        int i9 = this.aboffset;
        this.aboffset = i9 + 1;
        long i22 = bArr6[i9] & UByte.MAX_VALUE;
        byte[] bArr7 = this.ab;
        int i10 = this.aboffset;
        this.aboffset = i10 + 1;
        long i1 = bArr7[i10] & UByte.MAX_VALUE;
        byte[] bArr8 = this.ab;
        int i11 = this.aboffset;
        this.aboffset = i11 + 1;
        long i0 = bArr8[i11] & UByte.MAX_VALUE;
        return (i7 << 56) + (i6 << 48) + (i5 << 40) + (i42 << 32) + (i32 << 24) + (i22 << 16) + (i1 << 8) + i0;
    }

    public final boolean readBoolean() {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        int i0 = bArr[i] & 255;
        return i0 != 0;
    }

    public final byte readByte() {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        int i0 = bArr[i] & UByte.MAX_VALUE;
        return (byte) i0;
    }

    public final short readShort() {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        int i1 = bArr[i] & UByte.MAX_VALUE;
        byte[] bArr2 = this.ab;
        int i2 = this.aboffset;
        this.aboffset = i2 + 1;
        int i0 = bArr2[i2] & UByte.MAX_VALUE;
        return (short) ((i1 << 8) | i0);
    }

    public final int getEndPointer() {
        int size = readVarLengthUnsigned();
        return this.aboffset + size;
    }

    public final byte[] readDataUntil(int endPointer) {
        int size = endPointer - this.aboffset;
        if (size == 0) {
            return null;
        }
        byte[] data = new byte[size];
        readFully(data);
        return data;
    }

    public final byte[] readVarBytes() {
        int len = readVarLengthUnsigned();
        if (len == 0) {
            return null;
        }
        byte[] bytes = new byte[len];
        readFully(bytes);
        return bytes;
    }

    public final int readVarLengthSigned() {
        int v = readVarLengthUnsigned();
        return (v & 1) == 0 ? v >> 1 : -(v >> 1);
    }

    public final int readVarLengthUnsigned() {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        byte b = bArr[i];
        int v = b & ByteCompanionObject.MAX_VALUE;
        if (b >= 0) {
            return v;
        }
        byte[] bArr2 = this.ab;
        int i2 = this.aboffset;
        this.aboffset = i2 + 1;
        byte b2 = bArr2[i2];
        int v2 = v | ((b2 & ByteCompanionObject.MAX_VALUE) << 7);
        if (b2 >= 0) {
            return v2;
        }
        byte[] bArr3 = this.ab;
        int i3 = this.aboffset;
        this.aboffset = i3 + 1;
        byte b3 = bArr3[i3];
        int v3 = v2 | ((b3 & ByteCompanionObject.MAX_VALUE) << 14);
        if (b3 >= 0) {
            return v3;
        }
        byte[] bArr4 = this.ab;
        int i4 = this.aboffset;
        this.aboffset = i4 + 1;
        byte b4 = bArr4[i4];
        int v4 = v3 | ((b4 & ByteCompanionObject.MAX_VALUE) << 21);
        if (b4 >= 0) {
            return v4;
        }
        byte[] bArr5 = this.ab;
        int i5 = this.aboffset;
        this.aboffset = i5 + 1;
        return v4 | ((bArr5[i5] & 15) << 28);
    }

    public final void readFully(byte[] ta) {
        System.arraycopy(this.ab, this.aboffset, ta, 0, ta.length);
        this.aboffset += ta.length;
    }

    public final boolean hasMoreData() {
        return this.aboffset < this.aboffsetEnd;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        int i = 0;
        while (i < this.ab.length) {
            sb.append(i == 0 ? " " : ", ").append(Integer.toString(this.ab[i]));
            i++;
        }
        sb.append(" ]");
        return sb.toString();
    }
}

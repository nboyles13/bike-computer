package btools.util;

/* JADX INFO: loaded from: classes.dex */
public class ByteDataWriter extends ByteDataReader {
    public ByteDataWriter(byte[] byteArray) {
        super(byteArray);
    }

    public final void writeInt(int v) {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        bArr[i] = (byte) ((v >> 24) & 255);
        byte[] bArr2 = this.ab;
        int i2 = this.aboffset;
        this.aboffset = i2 + 1;
        bArr2[i2] = (byte) ((v >> 16) & 255);
        byte[] bArr3 = this.ab;
        int i3 = this.aboffset;
        this.aboffset = i3 + 1;
        bArr3[i3] = (byte) ((v >> 8) & 255);
        byte[] bArr4 = this.ab;
        int i4 = this.aboffset;
        this.aboffset = i4 + 1;
        bArr4[i4] = (byte) (v & 255);
    }

    public final void writeLong(long v) {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        bArr[i] = (byte) ((v >> 56) & 255);
        byte[] bArr2 = this.ab;
        int i2 = this.aboffset;
        this.aboffset = i2 + 1;
        bArr2[i2] = (byte) ((v >> 48) & 255);
        byte[] bArr3 = this.ab;
        int i3 = this.aboffset;
        this.aboffset = i3 + 1;
        bArr3[i3] = (byte) ((v >> 40) & 255);
        byte[] bArr4 = this.ab;
        int i4 = this.aboffset;
        this.aboffset = i4 + 1;
        bArr4[i4] = (byte) ((v >> 32) & 255);
        byte[] bArr5 = this.ab;
        int i5 = this.aboffset;
        this.aboffset = i5 + 1;
        bArr5[i5] = (byte) ((v >> 24) & 255);
        byte[] bArr6 = this.ab;
        int i6 = this.aboffset;
        this.aboffset = i6 + 1;
        bArr6[i6] = (byte) ((v >> 16) & 255);
        byte[] bArr7 = this.ab;
        int i7 = this.aboffset;
        this.aboffset = i7 + 1;
        bArr7[i7] = (byte) ((v >> 8) & 255);
        byte[] bArr8 = this.ab;
        int i8 = this.aboffset;
        this.aboffset = i8 + 1;
        bArr8[i8] = (byte) (v & 255);
    }

    public final void writeBoolean(boolean z) {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        bArr[i] = z ? (byte) 1 : (byte) 0;
    }

    public final void writeByte(int v) {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        bArr[i] = (byte) (v & 255);
    }

    public final void writeShort(int v) {
        byte[] bArr = this.ab;
        int i = this.aboffset;
        this.aboffset = i + 1;
        bArr[i] = (byte) ((v >> 8) & 255);
        byte[] bArr2 = this.ab;
        int i2 = this.aboffset;
        this.aboffset = i2 + 1;
        bArr2[i2] = (byte) (v & 255);
    }

    public final void write(byte[] sa) {
        System.arraycopy(sa, 0, this.ab, this.aboffset, sa.length);
        this.aboffset += sa.length;
    }

    public final void write(byte[] sa, int offset, int len) {
        System.arraycopy(sa, offset, this.ab, this.aboffset, len);
        this.aboffset += len;
    }

    public final void writeVarBytes(byte[] sa) {
        if (sa == null) {
            writeVarLengthUnsigned(0);
            return;
        }
        int len = sa.length;
        writeVarLengthUnsigned(len);
        write(sa, 0, len);
    }

    public final void writeModeAndDesc(boolean z, byte[] bArr) {
        int length = bArr == null ? 0 : bArr.length;
        writeVarLengthUnsigned((length << 1) | (z ? 1 : 0));
        if (length > 0) {
            write(bArr, 0, length);
        }
    }

    public final byte[] toByteArray() {
        byte[] c = new byte[this.aboffset];
        System.arraycopy(this.ab, 0, c, 0, this.aboffset);
        return c;
    }

    public final int writeSizePlaceHolder() {
        int i = this.aboffset;
        this.aboffset = i + 1;
        return i;
    }

    public final void injectSize(int sizeoffset) {
        int size = 0;
        int datasize = (this.aboffset - sizeoffset) - 1;
        int v = datasize;
        do {
            v >>= 7;
            size++;
        } while (v != 0);
        if (size > 1) {
            System.arraycopy(this.ab, sizeoffset + 1, this.ab, sizeoffset + size, datasize);
        }
        this.aboffset = sizeoffset;
        writeVarLengthUnsigned(datasize);
        this.aboffset = sizeoffset + size + datasize;
    }

    public final void writeVarLengthSigned(int v) {
        writeVarLengthUnsigned(v < 0 ? ((-v) << 1) | 1 : v << 1);
    }

    public final void writeVarLengthUnsigned(int v) {
        int i7 = v & 127;
        int v2 = v >>> 7;
        if (v2 == 0) {
            byte[] bArr = this.ab;
            int i = this.aboffset;
            this.aboffset = i + 1;
            bArr[i] = (byte) i7;
            return;
        }
        byte[] bArr2 = this.ab;
        int i2 = this.aboffset;
        this.aboffset = i2 + 1;
        bArr2[i2] = (byte) (i7 | 128);
        int i72 = v2 & 127;
        int v3 = v2 >>> 7;
        if (v3 == 0) {
            byte[] bArr3 = this.ab;
            int i3 = this.aboffset;
            this.aboffset = i3 + 1;
            bArr3[i3] = (byte) i72;
            return;
        }
        byte[] bArr4 = this.ab;
        int i4 = this.aboffset;
        this.aboffset = i4 + 1;
        bArr4[i4] = (byte) (i72 | 128);
        int i73 = v3 & 127;
        int v4 = v3 >>> 7;
        if (v4 == 0) {
            byte[] bArr5 = this.ab;
            int i5 = this.aboffset;
            this.aboffset = i5 + 1;
            bArr5[i5] = (byte) i73;
            return;
        }
        byte[] bArr6 = this.ab;
        int i6 = this.aboffset;
        this.aboffset = i6 + 1;
        bArr6[i6] = (byte) (i73 | 128);
        int i74 = v4 & 127;
        int v5 = v4 >>> 7;
        if (v5 == 0) {
            byte[] bArr7 = this.ab;
            int i8 = this.aboffset;
            this.aboffset = i8 + 1;
            bArr7[i8] = (byte) i74;
            return;
        }
        byte[] bArr8 = this.ab;
        int i9 = this.aboffset;
        this.aboffset = i9 + 1;
        bArr8[i9] = (byte) (i74 | 128);
        byte[] bArr9 = this.ab;
        int i10 = this.aboffset;
        this.aboffset = i10 + 1;
        bArr9[i10] = (byte) v5;
    }

    public int size() {
        return this.aboffset;
    }
}

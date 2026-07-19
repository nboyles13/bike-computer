package btools.util;

/* JADX INFO: loaded from: classes.dex */
public final class ByteArrayUnifier implements IByteArrayUnifier {
    private byte[][] byteArrayCache;
    private int[] crcCrosscheck;
    private int size;

    public ByteArrayUnifier(int size, boolean validateImmutability) {
        this.size = size;
        this.byteArrayCache = new byte[size][];
        if (validateImmutability) {
            this.crcCrosscheck = new int[size];
        }
    }

    public byte[] unify(byte[] ab) {
        return unify(ab, 0, ab.length);
    }

    @Override // btools.util.IByteArrayUnifier
    public byte[] unify(byte[] ab, int offset, int len) {
        int crc = Crc32.crc(ab, offset, len);
        int idx = (268435455 & crc) % this.size;
        byte[] abc = this.byteArrayCache[idx];
        if (abc != null && abc.length == len) {
            int i = 0;
            while (i < len && ab[offset + i] == abc[i]) {
                i++;
            }
            if (i == len) {
                return abc;
            }
        }
        if (this.crcCrosscheck != null) {
            if (this.byteArrayCache[idx] != null) {
                byte[] abold = this.byteArrayCache[idx];
                int crcold = Crc32.crc(abold, 0, abold.length);
                if (crcold != this.crcCrosscheck[idx]) {
                    throw new IllegalArgumentException("ByteArrayUnifier: immutablity validation failed!");
                }
            }
            this.crcCrosscheck[idx] = crc;
        }
        byte[] nab = new byte[len];
        System.arraycopy(ab, offset, nab, 0, len);
        this.byteArrayCache[idx] = nab;
        return nab;
    }
}

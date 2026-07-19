package btools.codec;

import btools.util.BitCoderContext;

/* JADX INFO: loaded from: classes.dex */
public final class DataBuffers {
    public int[] alat;
    public int[] alon;
    public byte[] bbuf1;
    public BitCoderContext bctx1;
    public int[] ibuf1;
    public int[] ibuf2;
    public int[] ibuf3;
    public byte[] iobuffer;
    public byte[] tagbuf1;

    public DataBuffers() {
        this(new byte[65636]);
    }

    public DataBuffers(byte[] iobuffer) {
        this.tagbuf1 = new byte[256];
        this.bctx1 = new BitCoderContext(this.tagbuf1);
        this.bbuf1 = new byte[65636];
        this.ibuf1 = new int[4096];
        this.ibuf2 = new int[2048];
        this.ibuf3 = new int[2048];
        this.alon = new int[2048];
        this.alat = new int[2048];
        this.iobuffer = iobuffer;
    }
}

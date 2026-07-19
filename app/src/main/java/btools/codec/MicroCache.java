package btools.codec;

import btools.util.ByteDataWriter;

/* JADX INFO: loaded from: classes.dex */
public class MicroCache extends ByteDataWriter {
    public static boolean debug = false;
    public static final MicroCache emptyNonVirgin = new MicroCache(null);
    private int delbytes;
    private int delcount;
    protected int[] faid;
    protected int[] fapos;
    public boolean ghost;
    private int p2size;
    protected int size;
    public boolean virgin;

    static {
        emptyNonVirgin.virgin = false;
    }

    protected MicroCache(byte[] ab) {
        super(ab);
        this.size = 0;
        this.delcount = 0;
        this.delbytes = 0;
        this.virgin = true;
        this.ghost = false;
    }

    public static MicroCache emptyCache() {
        return new MicroCache(null);
    }

    protected void init(int size) {
        this.size = size;
        this.delcount = 0;
        this.delbytes = 0;
        this.p2size = 1073741824;
        while (this.p2size > size) {
            this.p2size >>= 1;
        }
    }

    public final void finishNode(long id) {
        this.fapos[this.size] = this.aboffset;
        this.faid[this.size] = shrinkId(id);
        this.size++;
    }

    public final void discardNode() {
        this.aboffset = startPos(this.size);
    }

    public final int getSize() {
        return this.size;
    }

    public final int getDataSize() {
        if (this.ab == null) {
            return 0;
        }
        return this.ab.length;
    }

    public final boolean getAndClear(long id64) {
        if (this.size == 0) {
            return false;
        }
        int id = shrinkId(id64);
        int[] a = this.faid;
        int n = 0;
        for (int offset = this.p2size; offset > 0; offset >>= 1) {
            int nn = n + offset;
            if (nn < this.size && a[nn] <= id) {
                n = nn;
            }
        }
        if (a[n] != id || (this.fapos[n] & Integer.MIN_VALUE) != 0) {
            return false;
        }
        this.aboffset = startPos(n);
        this.aboffsetEnd = this.fapos[n];
        int[] iArr = this.fapos;
        iArr[n] = iArr[n] | Integer.MIN_VALUE;
        this.delbytes += this.aboffsetEnd - this.aboffset;
        this.delcount++;
        return true;
    }

    protected final int startPos(int n) {
        if (n > 0) {
            return this.fapos[n - 1] & Integer.MAX_VALUE;
        }
        return 0;
    }

    public final int collect(int threshold) {
        if (this.delcount <= threshold) {
            return 0;
        }
        this.virgin = false;
        int nsize = this.size - this.delcount;
        if (nsize == 0) {
            this.faid = null;
            this.fapos = null;
        } else {
            int[] nfaid = new int[nsize];
            int[] nfapos = new int[nsize];
            int idx = 0;
            byte[] nab = new byte[this.ab.length - this.delbytes];
            int nab_off = 0;
            for (int i = 0; i < this.size; i++) {
                int pos = this.fapos[i];
                if ((Integer.MIN_VALUE & pos) == 0) {
                    int start = startPos(i);
                    int end = this.fapos[i];
                    int len = end - start;
                    System.arraycopy(this.ab, start, nab, nab_off, len);
                    nfaid[idx] = this.faid[i];
                    nab_off += len;
                    nfapos[idx] = nab_off;
                    idx++;
                }
            }
            this.faid = nfaid;
            this.fapos = nfapos;
            this.ab = nab;
        }
        int deleted = this.delbytes;
        init(nsize);
        return deleted;
    }

    public final void unGhost() {
        this.ghost = false;
        this.delcount = 0;
        this.delbytes = 0;
        for (int i = 0; i < this.size; i++) {
            int[] iArr = this.fapos;
            iArr[i] = iArr[i] & Integer.MAX_VALUE;
        }
    }

    public final long getIdForIndex(int i) {
        int id32 = this.faid[i];
        return expandId(id32);
    }

    public long expandId(int id32) {
        throw new IllegalArgumentException("expandId for empty cache");
    }

    public int shrinkId(long id64) {
        throw new IllegalArgumentException("shrinkId for empty cache");
    }

    public boolean isInternal(int ilon, int ilat) {
        throw new IllegalArgumentException("isInternal for empty cache");
    }

    public int encodeMicroCache(byte[] buffer) {
        throw new IllegalArgumentException("encodeMicroCache for empty cache");
    }

    public String compareWith(MicroCache mc) {
        String msg = _compareWith(mc);
        if (msg != null) {
            StringBuilder sb = new StringBuilder(msg);
            sb.append("\nencode cache:\n").append(summary());
            sb.append("\ndecode cache:\n").append(mc.summary());
            return sb.toString();
        }
        return null;
    }

    private String summary() {
        StringBuilder sb = new StringBuilder("size=" + this.size + " aboffset=" + this.aboffset);
        for (int i = 0; i < this.size; i++) {
            sb.append("\nidx=" + i + " faid=" + this.faid[i] + " fapos=" + this.fapos[i]);
        }
        return sb.toString();
    }

    private String _compareWith(MicroCache mc) {
        if (this.size != mc.size) {
            return "size mismatch: " + this.size + "->" + mc.size;
        }
        int i = 0;
        while (i < this.size) {
            if (this.faid[i] != mc.faid[i]) {
                return "faid mismatch at index " + i + ":" + this.faid[i] + "->" + mc.faid[i];
            }
            int start = i > 0 ? this.fapos[i - 1] : 0;
            int end = this.fapos[i] < mc.fapos[i] ? this.fapos[i] : mc.fapos[i];
            int len = end - start;
            for (int offset = 0; offset < len; offset++) {
                if (mc.ab.length <= start + offset) {
                    return "data buffer too small";
                }
                if (this.ab[start + offset] != mc.ab[start + offset]) {
                    return "data mismatch at index " + i + " offset=" + offset;
                }
            }
            if (this.fapos[i] != mc.fapos[i]) {
                return "fapos mismatch at index " + i + ":" + this.fapos[i] + "->" + mc.fapos[i];
            }
            i++;
        }
        int i2 = this.aboffset;
        if (i2 != mc.aboffset) {
            return "datasize mismatch: " + this.aboffset + "->" + mc.aboffset;
        }
        return null;
    }

    public void calcDelta(MicroCache mc1, MicroCache mc2) {
        int id;
        int len1 = 0;
        int len2 = 0;
        while (true) {
            if (len1 < mc1.size || len2 < mc2.size) {
                int id1 = len1 < mc1.size ? mc1.faid[len1] : Integer.MAX_VALUE;
                int id2 = len2 < mc2.size ? mc2.faid[len2] : Integer.MAX_VALUE;
                if (id1 >= id2) {
                    id = id2;
                    int start2 = len2 > 0 ? mc2.fapos[len2 - 1] : 0;
                    int idx2 = len2 + 1;
                    int len22 = mc2.fapos[len2] - start2;
                    if (id1 == id2) {
                        int start1 = len1 > 0 ? mc1.fapos[len1 - 1] : 0;
                        int idx1 = len1 + 1;
                        int len12 = mc1.fapos[len1] - start1;
                        if (len12 == len22) {
                            int i = 0;
                            while (i < len12 && mc1.ab[start1 + i] == mc2.ab[start2 + i]) {
                                i++;
                            }
                            if (i == len12) {
                                len2 = idx2;
                                len1 = idx1;
                            }
                        }
                        len1 = idx1;
                    }
                    write(mc2.ab, start2, len22);
                    len2 = idx2;
                } else {
                    len1++;
                    id = id1;
                }
                this.fapos[this.size] = this.aboffset;
                this.faid[this.size] = id;
                this.size++;
            } else {
                return;
            }
        }
    }

    public void addDelta(MicroCache mc1, MicroCache mc2, boolean keepEmptyNodes) {
        int start1;
        int len1 = 0;
        int idx2 = 0;
        while (true) {
            if (len1 < mc1.size || idx2 < mc2.size) {
                int id1 = len1 < mc1.size ? mc1.faid[len1] : Integer.MAX_VALUE;
                int id2 = idx2 < mc2.size ? mc2.faid[idx2] : Integer.MAX_VALUE;
                if (id1 >= id2) {
                    start1 = idx2 > 0 ? mc2.fapos[idx2 - 1] : 0;
                    int idx22 = idx2 + 1;
                    int len2 = mc2.fapos[idx2] - start1;
                    if (keepEmptyNodes || len2 > 0) {
                        write(mc2.ab, start1, len2);
                        this.fapos[this.size] = this.aboffset;
                        int[] iArr = this.faid;
                        int i = this.size;
                        this.size = i + 1;
                        iArr[i] = id2;
                    }
                    if (id1 == id2) {
                        len1++;
                    }
                    idx2 = idx22;
                } else {
                    start1 = len1 > 0 ? mc1.fapos[len1 - 1] : 0;
                    int idx1 = len1 + 1;
                    int idx12 = mc1.fapos[len1];
                    int len12 = idx12 - start1;
                    write(mc1.ab, start1, len12);
                    this.fapos[this.size] = this.aboffset;
                    int[] iArr2 = this.faid;
                    int i2 = this.size;
                    this.size = i2 + 1;
                    iArr2[i2] = id1;
                    len1 = idx1;
                }
            } else {
                return;
            }
        }
    }
}

package btools.util;

/* JADX INFO: loaded from: classes.dex */
public class TinyDenseLongMap extends DenseLongMap {
    protected static final int MAXLISTS = 31;
    private byte[][] vla;
    private int size = 0;
    private int _maxKeepExponent = 14;
    private int[] pa = new int[31];
    private long[][] al = new long[31][];

    public TinyDenseLongMap() {
        this.al[0] = new long[1];
        this.vla = new byte[31][];
        this.vla[0] = new byte[1];
    }

    private void fillReturnValue(byte[] rv, int idx, int p) {
        rv[0] = this.vla[idx][p];
        if (rv.length == 2) {
            this.vla[idx][p] = rv[1];
        }
    }

    @Override // btools.util.DenseLongMap
    public void put(long id, int value) {
        byte[] rv = {0, (byte) value};
        if (contains(id, rv)) {
            return;
        }
        this.vla[0][0] = (byte) value;
        _add(id);
    }

    @Override // btools.util.DenseLongMap
    public int getInt(long id) {
        byte[] rv = new byte[1];
        if (contains(id, rv)) {
            return rv[0];
        }
        return -1;
    }

    private boolean _add(long id) {
        if (this.size == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("cannot grow beyond size Integer.MAX_VALUE");
        }
        this.al[0][0] = id;
        int bp = this.size;
        this.size = bp + 1;
        int idx = 1;
        int n = 1;
        this.pa[0] = 1;
        this.pa[1] = 1;
        while ((bp & 1) == 1) {
            bp >>= 1;
            this.pa[idx] = n;
            n <<= 1;
            idx++;
        }
        if (this.al[idx] == null) {
            this.al[idx] = new long[n];
            this.vla[idx] = new byte[n];
        }
        while (n > 0) {
            long maxId = 0;
            int maxIdx = -1;
            for (int i = 0; i < idx; i++) {
                int p = this.pa[i];
                if (p > 0) {
                    long currentId = this.al[i][p - 1];
                    if (maxIdx < 0 || currentId > maxId) {
                        maxIdx = i;
                        maxId = currentId;
                    }
                }
            }
            if (n < this.al[idx].length && maxId == this.al[idx][n]) {
                throw new IllegalArgumentException("duplicate key found in late check: " + maxId);
            }
            n--;
            this.al[idx][n] = maxId;
            this.vla[idx][n] = this.vla[maxIdx][this.pa[maxIdx] - 1];
            int[] iArr = this.pa;
            iArr[maxIdx] = iArr[maxIdx] - 1;
        }
        while (true) {
            int idx2 = idx - 1;
            if (idx <= this._maxKeepExponent) {
                return false;
            }
            this.al[idx2] = null;
            this.vla[idx2] = null;
            idx = idx2;
        }
    }

    private boolean contains(long id, byte[] rv) {
        int idx = 1;
        for (int bp = this.size; bp != 0; bp >>= 1) {
            if ((bp & 1) == 1 && contains(idx, id, rv)) {
                return true;
            }
            idx++;
        }
        return false;
    }

    private boolean contains(int idx, long id, byte[] rv) {
        long[] a = this.al[idx];
        int offset = a.length;
        int n = 0;
        while (true) {
            int i = offset >> 1;
            offset = i;
            if (i <= 0) {
                break;
            }
            int nn = n + offset;
            if (a[nn] <= id) {
                n = nn;
            }
        }
        if (a[n] == id) {
            if (rv != null) {
                fillReturnValue(rv, idx, n);
                return true;
            }
            return true;
        }
        return false;
    }
}

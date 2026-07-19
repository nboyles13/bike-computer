package btools.util;

/* JADX INFO: loaded from: classes.dex */
public class CompactLongSet {
    protected static final int MAXLISTS = 31;
    private int size = 0;
    private int _maxKeepExponent = 14;
    private int[] pa = new int[31];
    private long[][] al = new long[31][];

    public CompactLongSet() {
        this.al[0] = new long[1];
    }

    public int size() {
        return this.size;
    }

    public boolean add(long id) {
        if (contains(id)) {
            return true;
        }
        _add(id);
        return false;
    }

    public void fastAdd(long id) {
        _add(id);
    }

    private void _add(long id) {
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
            int[] iArr = this.pa;
            iArr[maxIdx] = iArr[maxIdx] - 1;
        }
        while (true) {
            int idx2 = idx - 1;
            if (idx > this._maxKeepExponent) {
                this.al[idx2] = null;
                idx = idx2;
            } else {
                return;
            }
        }
    }

    public boolean contains(long id) {
        int idx = 1;
        for (int bp = this.size; bp != 0; bp >>= 1) {
            if ((bp & 1) == 1 && contains(idx, id)) {
                return true;
            }
            idx++;
        }
        return false;
    }

    private boolean contains(int idx, long id) {
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
        return a[n] == id;
    }

    protected void moveToFrozenArray(long[] faid) {
        int p;
        for (int i = 1; i < 31; i++) {
            this.pa[i] = 0;
        }
        for (int ti = 0; ti < this.size; ti++) {
            int minIdx = -1;
            long minId = 0;
            int idx = 1;
            for (int bp = this.size; bp != 0; bp >>= 1) {
                if ((bp & 1) == 1 && (p = this.pa[idx]) < this.al[idx].length) {
                    long currentId = this.al[idx][p];
                    if (minIdx < 0 || currentId < minId) {
                        minIdx = idx;
                        minId = currentId;
                    }
                }
                idx++;
            }
            faid[ti] = minId;
            int[] iArr = this.pa;
            iArr[minIdx] = iArr[minIdx] + 1;
            if (ti > 0 && faid[ti - 1] == minId) {
                throw new IllegalArgumentException("duplicate key found in late check: " + minId);
            }
        }
        this.al = null;
    }
}

package btools.util;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class CompactLongMap<V> {
    protected static final int MAXLISTS = 31;
    private static boolean earlyDuplicateCheck;
    protected V value_in;
    protected V value_out;
    private Object[][] vla;
    private int size = 0;
    private int _maxKeepExponent = 14;
    private int[] pa = new int[31];
    private long[][] al = new long[31][];

    public CompactLongMap() {
        this.al[0] = new long[1];
        this.vla = new Object[31][];
        this.vla[0] = new Object[1];
        earlyDuplicateCheck = Boolean.getBoolean("earlyDuplicateCheck");
    }

    public boolean put(long id, V value) {
        try {
            this.value_in = value;
            if (contains(id, true)) {
                return true;
            }
            this.vla[0][0] = value;
            _add(id);
            return false;
        } finally {
            this.value_in = null;
            this.value_out = null;
        }
    }

    public void fastPut(long id, V value) {
        if (earlyDuplicateCheck && contains(id)) {
            throw new IllegalArgumentException("duplicate key found in early check: " + id);
        }
        this.vla[0][0] = value;
        _add(id);
    }

    public V get(long id) {
        try {
            if (contains(id, false)) {
                return this.value_out;
            }
            return null;
        } finally {
            this.value_out = null;
        }
    }

    public int size() {
        return this.size;
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
            this.vla[idx] = new Object[n];
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

    public boolean contains(long id) {
        try {
            return contains(id, false);
        } finally {
            this.value_out = null;
        }
    }

    protected boolean contains(long id, boolean doPut) {
        int idx = 1;
        for (int bp = this.size; bp != 0; bp >>= 1) {
            if ((bp & 1) == 1 && contains(idx, id, doPut)) {
                return true;
            }
            idx++;
        }
        return false;
    }

    private boolean contains(int i, long j, boolean z) {
        long[] jArr = this.al[i];
        int length = jArr.length;
        int i2 = 0;
        while (true) {
            int i3 = length >> 1;
            length = i3;
            if (i3 <= 0) {
                break;
            }
            int i4 = i2 + length;
            if (jArr[i4] <= j) {
                i2 = i4;
            }
        }
        if (jArr[i2] == j) {
            this.value_out = (V) this.vla[i][i2];
            if (z) {
                this.vla[i][i2] = this.value_in;
                return true;
            }
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected void moveToFrozenArrays(long[] faid, List<V> list) {
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
            list.add(this.vla[minIdx][this.pa[minIdx]]);
            int[] iArr = this.pa;
            iArr[minIdx] = iArr[minIdx] + 1;
            if (ti > 0 && faid[ti - 1] == minId) {
                throw new IllegalArgumentException("duplicate key found in late check: " + minId);
            }
        }
        this.al = null;
        this.vla = null;
    }
}

package btools.util;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class FrozenLongMap<V> extends CompactLongMap<V> {
    private long[] faid;
    private List<V> flv;
    private int p2size;
    private int size;

    public FrozenLongMap(CompactLongMap<V> map) {
        this.size = 0;
        this.size = map.size();
        this.faid = new long[this.size];
        this.flv = new ArrayList(this.size);
        map.moveToFrozenArrays(this.faid, this.flv);
        this.p2size = 1073741824;
        while (this.p2size > this.size) {
            this.p2size >>= 1;
        }
    }

    @Override // btools.util.CompactLongMap
    public boolean put(long id, V value) {
        try {
            this.value_in = value;
            if (contains(id, true)) {
                return true;
            }
            throw new RuntimeException("cannot only put on existing key in FrozenLongIntMap");
        } finally {
            this.value_in = null;
            this.value_out = null;
        }
    }

    @Override // btools.util.CompactLongMap
    public void fastPut(long id, V value) {
        throw new RuntimeException("cannot put on FrozenLongIntMap");
    }

    @Override // btools.util.CompactLongMap
    public int size() {
        return this.size;
    }

    @Override // btools.util.CompactLongMap
    protected boolean contains(long id, boolean doPut) {
        if (this.size == 0) {
            return false;
        }
        long[] a = this.faid;
        int n = 0;
        for (int offset = this.p2size; offset > 0; offset >>= 1) {
            int nn = n + offset;
            if (nn < this.size && a[nn] <= id) {
                n = nn;
            }
        }
        if (a[n] != id) {
            return false;
        }
        this.value_out = this.flv.get(n);
        if (doPut) {
            this.flv.set(n, this.value_in);
            return true;
        }
        return true;
    }

    @Override // btools.util.CompactLongMap
    public V get(long id) {
        if (this.size == 0) {
            return null;
        }
        long[] a = this.faid;
        int n = 0;
        for (int offset = this.p2size; offset > 0; offset >>= 1) {
            int nn = n + offset;
            if (nn < this.size && a[nn] <= id) {
                n = nn;
            }
        }
        if (a[n] == id) {
            return this.flv.get(n);
        }
        return null;
    }

    public List<V> getValueList() {
        return this.flv;
    }

    public long[] getKeyArray() {
        return this.faid;
    }
}

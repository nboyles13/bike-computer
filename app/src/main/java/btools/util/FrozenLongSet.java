package btools.util;

/* JADX INFO: loaded from: classes.dex */
public class FrozenLongSet extends CompactLongSet {
    private long[] faid;
    private int p2size;
    private int size;

    public FrozenLongSet(CompactLongSet set) {
        this.size = 0;
        this.size = set.size();
        this.faid = new long[this.size];
        set.moveToFrozenArray(this.faid);
        this.p2size = 1073741824;
        while (this.p2size > this.size) {
            this.p2size >>= 1;
        }
    }

    @Override // btools.util.CompactLongSet
    public boolean add(long id) {
        throw new RuntimeException("cannot add on FrozenLongSet");
    }

    @Override // btools.util.CompactLongSet
    public void fastAdd(long id) {
        throw new RuntimeException("cannot add on FrozenLongSet");
    }

    @Override // btools.util.CompactLongSet
    public int size() {
        return this.size;
    }

    @Override // btools.util.CompactLongSet
    public boolean contains(long id) {
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
        return a[n] == id;
    }
}

package btools.util;

/* JADX INFO: loaded from: classes.dex */
public final class LruMap {
    private LruMapNode[] binArray;
    private int hashbins;
    private LruMapNode lru;
    private int maxsize;
    private LruMapNode mru;
    private int size;

    public LruMap(int bins, int size) {
        this.hashbins = bins;
        this.maxsize = size;
        this.binArray = new LruMapNode[this.hashbins];
    }

    public LruMapNode get(LruMapNode key) {
        int bin = (key.hash & 268435455) % this.hashbins;
        for (LruMapNode e = this.binArray[bin]; e != null; e = e.nextInBin) {
            if (key.equals(e)) {
                return e;
            }
        }
        return null;
    }

    public void touch(LruMapNode e) {
        LruMapNode n = e.next;
        LruMapNode p = e.previous;
        if (n == null) {
            return;
        }
        n.previous = p;
        if (p != null) {
            p.next = n;
        } else {
            this.lru = n;
        }
        this.mru.next = e;
        e.previous = this.mru;
        e.next = null;
        this.mru = e;
    }

    public LruMapNode removeLru() {
        if (this.size < this.maxsize) {
            return null;
        }
        this.size--;
        int bin = (this.lru.hashCode() & 268435455) % this.hashbins;
        LruMapNode e = this.binArray[bin];
        if (e != this.lru) {
            while (true) {
                if (e == null) {
                    break;
                }
                LruMapNode prev = e;
                e = e.nextInBin;
                if (e == this.lru) {
                    prev.nextInBin = this.lru.nextInBin;
                    break;
                }
            }
        } else {
            this.binArray[bin] = this.lru.nextInBin;
        }
        LruMapNode res = this.lru;
        this.lru = this.lru.next;
        this.lru.previous = null;
        return res;
    }

    public void put(LruMapNode val) {
        int bin = (val.hashCode() & 268435455) % this.hashbins;
        val.nextInBin = this.binArray[bin];
        this.binArray[bin] = val;
        val.previous = this.mru;
        val.next = null;
        if (this.mru == null) {
            this.lru = val;
        } else {
            this.mru.next = val;
        }
        this.mru = val;
        this.size++;
    }
}

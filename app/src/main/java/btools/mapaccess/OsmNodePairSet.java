package btools.mapaccess;

import btools.util.CompactLongMap;

/* JADX INFO: loaded from: classes.dex */
public class OsmNodePairSet {
    private CompactLongMap<OsmNodePair> map;
    private int maxTempNodes;
    private long[] n1a;
    private long[] n2a;
    private int tempNodes = 0;
    private int npairs = 0;
    private int freezecount = 0;

    public OsmNodePairSet(int maxTempNodeCount) {
        this.maxTempNodes = 0;
        this.maxTempNodes = maxTempNodeCount;
        this.n1a = new long[this.maxTempNodes];
        this.n2a = new long[this.maxTempNodes];
    }

    private static final class OsmNodePair {
        public OsmNodePair next;
        public long node2;

        private OsmNodePair() {
        }
    }

    public void addTempPair(long n1, long n2) {
        if (this.tempNodes < this.maxTempNodes) {
            this.n1a[this.tempNodes] = n1;
            this.n2a[this.tempNodes] = n2;
            this.tempNodes++;
        }
    }

    public void freezeTempPairs() {
        this.freezecount++;
        for (int i = 0; i < this.tempNodes; i++) {
            addPair(this.n1a[i], this.n2a[i]);
        }
        this.tempNodes = 0;
    }

    public void clearTempPairs() {
        this.tempNodes = 0;
    }

    private void addPair(long n1, long n2) {
        if (this.map == null) {
            this.map = new CompactLongMap<>();
        }
        this.npairs++;
        if (getElement(n1, n2) == null) {
            OsmNodePair e = new OsmNodePair();
            e.node2 = n2;
            OsmNodePair e0 = this.map.get(n1);
            if (e0 != null) {
                while (e0.next != null) {
                    e0 = e0.next;
                }
                e0.next = e;
                return;
            }
            this.map.fastPut(n1, e);
        }
    }

    public int size() {
        return this.npairs;
    }

    public int tempSize() {
        return this.tempNodes;
    }

    public int getMaxTmpNodes() {
        return this.maxTempNodes;
    }

    public int getFreezeCount() {
        return this.freezecount;
    }

    public boolean hasPair(long n1, long n2) {
        return (this.map == null || (getElement(n1, n2) == null && getElement(n2, n1) == null)) ? false : true;
    }

    private OsmNodePair getElement(long n1, long n2) {
        for (OsmNodePair e = this.map.get(n1); e != null; e = e.next) {
            if (e.node2 == n2) {
                return e;
            }
        }
        return null;
    }
}

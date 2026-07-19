package btools.util;

/* JADX INFO: loaded from: classes.dex */
public class LongList {
    private long[] a;
    private int size;

    public LongList(int capacity) {
        this.a = capacity < 4 ? new long[4] : new long[capacity];
    }

    public void add(long value) {
        if (this.size == this.a.length) {
            long[] aa = new long[this.size * 2];
            System.arraycopy(this.a, 0, aa, 0, this.size);
            this.a = aa;
        }
        long[] aa2 = this.a;
        int i = this.size;
        this.size = i + 1;
        aa2[i] = value;
    }

    public long get(int idx) {
        if (idx >= this.size) {
            throw new IndexOutOfBoundsException("list size=" + this.size + " idx=" + idx);
        }
        return this.a[idx];
    }

    public int size() {
        return this.size;
    }
}

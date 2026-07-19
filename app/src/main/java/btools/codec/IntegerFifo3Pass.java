package btools.codec;

/* JADX INFO: loaded from: classes.dex */
public class IntegerFifo3Pass {
    private int[] a;
    private int pass;
    private int pos;
    private int size;

    public IntegerFifo3Pass(int capacity) {
        this.a = capacity < 4 ? new int[4] : new int[capacity];
    }

    public void init() {
        this.pass++;
        this.pos = 0;
    }

    public void add(int value) {
        if (this.pass == 2) {
            if (this.size == this.a.length) {
                int[] aa = new int[this.size * 2];
                System.arraycopy(this.a, 0, aa, 0, this.size);
                this.a = aa;
            }
            int[] aa2 = this.a;
            int i = this.size;
            this.size = i + 1;
            aa2[i] = value;
        }
    }

    public int getNext() {
        if (this.pass != 3) {
            return 1;
        }
        int i = this.pos;
        this.pos = i + 1;
        return get(i);
    }

    private int get(int idx) {
        if (idx >= this.size) {
            throw new IndexOutOfBoundsException("list size=" + this.size + " idx=" + idx);
        }
        return this.a[idx];
    }
}

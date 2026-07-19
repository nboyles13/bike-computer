package btools.codec;

/* JADX INFO: loaded from: classes.dex */
public class LinkedListContainer {
    private int[] ia;
    private int listpointer;
    private int size;
    private int[] startpointer;

    public LinkedListContainer(int nlists, int[] defaultbuffer) {
        this.ia = defaultbuffer == null ? new int[nlists * 4] : defaultbuffer;
        this.startpointer = new int[nlists];
    }

    public void addDataElement(int listNr, int data) {
        if (this.size + 2 > this.ia.length) {
            resize();
        }
        int[] iArr = this.ia;
        int i = this.size;
        this.size = i + 1;
        iArr[i] = this.startpointer[listNr];
        this.startpointer[listNr] = this.size;
        int[] iArr2 = this.ia;
        int i2 = this.size;
        this.size = i2 + 1;
        iArr2[i2] = data;
    }

    public int initList(int listNr) {
        int cnt = 0;
        int lp = this.startpointer[listNr];
        this.listpointer = lp;
        while (lp != 0) {
            lp = this.ia[lp - 1];
            cnt++;
        }
        return cnt;
    }

    public int getDataElement() {
        if (this.listpointer == 0) {
            throw new IllegalArgumentException("no more element!");
        }
        int data = this.ia[this.listpointer];
        this.listpointer = this.ia[this.listpointer - 1];
        return data;
    }

    private void resize() {
        int[] ia2 = new int[this.ia.length * 2];
        System.arraycopy(this.ia, 0, ia2, 0, this.ia.length);
        this.ia = ia2;
    }
}

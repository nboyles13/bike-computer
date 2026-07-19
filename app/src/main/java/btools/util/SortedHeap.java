package btools.util;

/* JADX INFO: loaded from: classes.dex */
public final class SortedHeap<V> {
    private SortedBin first;
    private SortedBin firstNonEmpty;
    private int peaksize;
    private SortedBin second;
    private int size;

    public SortedHeap() {
        clear();
    }

    public V popLowestKeyValue() {
        SortedBin sortedBin = this.firstNonEmpty;
        if (this.firstNonEmpty == null) {
            return null;
        }
        this.size--;
        return (V) this.firstNonEmpty.getMinBin().dropLowest();
    }

    private static final class SortedBin {
        int[] al;
        int binsize;
        int lp;
        int lv;
        SortedBin next;
        SortedBin nextNonEmpty;
        SortedHeap parent;
        Object[] vla;

        SortedBin(int binsize, SortedHeap parent) {
            this.binsize = binsize;
            this.parent = parent;
            this.al = new int[binsize];
            this.vla = new Object[binsize];
            this.lp = binsize;
        }

        SortedBin next() {
            if (this.next == null) {
                this.next = new SortedBin(this.binsize << 1, this.parent);
            }
            return this.next;
        }

        Object dropLowest() {
            int lpOld = this.lp;
            int i = this.lp + 1;
            this.lp = i;
            if (i == this.binsize) {
                unlink();
            } else {
                this.lv = this.al[this.lp];
            }
            Object res = this.vla[lpOld];
            this.vla[lpOld] = null;
            return res;
        }

        void unlink() {
            SortedBin neBin = this.parent.firstNonEmpty;
            if (neBin == this) {
                this.parent.firstNonEmpty = this.nextNonEmpty;
            } else {
                while (true) {
                    SortedBin next = neBin.nextNonEmpty;
                    if (next == this) {
                        neBin.nextNonEmpty = this.nextNonEmpty;
                        return;
                    }
                    neBin = next;
                }
            }
        }

        void add(int key, Object value) {
            int p = this.lp;
            while (p != this.binsize && key >= this.al[p]) {
                this.al[p - 1] = this.al[p];
                this.vla[p - 1] = this.vla[p];
                p++;
            }
            this.al[p - 1] = key;
            this.vla[p - 1] = value;
            int[] iArr = this.al;
            int i = this.lp - 1;
            this.lp = i;
            this.lv = iArr[i];
        }

        void add4(int key, Object value) {
            int p = this.lp;
            this.lp = p - 1;
            if (p == 4 || key < this.al[p]) {
                this.al[p - 1] = key;
                this.lv = key;
                this.vla[p - 1] = value;
                return;
            }
            int i = this.al[p];
            this.al[p - 1] = i;
            this.lv = i;
            this.vla[p - 1] = this.vla[p];
            int p2 = p + 1;
            if (p2 == 4 || key < this.al[p2]) {
                this.al[p2 - 1] = key;
                this.vla[p2 - 1] = value;
                return;
            }
            this.al[p2 - 1] = this.al[p2];
            this.vla[p2 - 1] = this.vla[p2];
            int p3 = p2 + 1;
            if (p3 == 4 || key < this.al[p3]) {
                this.al[p3 - 1] = key;
                this.vla[p3 - 1] = value;
            } else {
                this.al[p3 - 1] = this.al[p3];
                this.vla[p3 - 1] = this.vla[p3];
                this.al[p3] = key;
                this.vla[p3] = value;
            }
        }

        SortedBin getMinBin() {
            SortedBin minBin = this;
            SortedBin bin = this.nextNonEmpty;
            if (bin == null) {
                return minBin;
            }
            if (bin.lv < minBin.lv) {
                minBin = bin;
            }
            SortedBin bin2 = bin.nextNonEmpty;
            if (bin2 == null) {
                return minBin;
            }
            if (bin2.lv < minBin.lv) {
                minBin = bin2;
            }
            SortedBin bin3 = bin2.nextNonEmpty;
            if (bin3 == null) {
                return minBin;
            }
            if (bin3.lv < minBin.lv) {
                minBin = bin3;
            }
            SortedBin bin4 = bin3.nextNonEmpty;
            if (bin4 == null) {
                return minBin;
            }
            if (bin4.lv < minBin.lv) {
                minBin = bin4;
            }
            SortedBin bin5 = bin4.nextNonEmpty;
            if (bin5 == null) {
                return minBin;
            }
            if (bin5.lv < minBin.lv) {
                minBin = bin5;
            }
            SortedBin bin6 = bin5.nextNonEmpty;
            if (bin6 == null) {
                return minBin;
            }
            if (bin6.lv < minBin.lv) {
                minBin = bin6;
            }
            SortedBin bin7 = bin6.nextNonEmpty;
            if (bin7 == null) {
                return minBin;
            }
            if (bin7.lv < minBin.lv) {
                minBin = bin7;
            }
            SortedBin bin8 = bin7.nextNonEmpty;
            if (bin8 == null) {
                return minBin;
            }
            if (bin8.lv < minBin.lv) {
                minBin = bin8;
            }
            SortedBin bin9 = bin8.nextNonEmpty;
            if (bin9 == null) {
                return minBin;
            }
            if (bin9.lv < minBin.lv) {
                minBin = bin9;
            }
            SortedBin bin10 = bin9.nextNonEmpty;
            if (bin10 == null) {
                return minBin;
            }
            if (bin10.lv < minBin.lv) {
                minBin = bin10;
            }
            SortedBin bin11 = bin10.nextNonEmpty;
            if (bin11 == null) {
                return minBin;
            }
            if (bin11.lv < minBin.lv) {
                minBin = bin11;
            }
            SortedBin bin12 = bin11.nextNonEmpty;
            if (bin12 == null) {
                return minBin;
            }
            if (bin12.lv < minBin.lv) {
                minBin = bin12;
            }
            SortedBin bin13 = bin12.nextNonEmpty;
            if (bin13 == null) {
                return minBin;
            }
            if (bin13.lv < minBin.lv) {
                minBin = bin13;
            }
            SortedBin bin14 = bin13.nextNonEmpty;
            if (bin14 == null) {
                return minBin;
            }
            if (bin14.lv < minBin.lv) {
                minBin = bin14;
            }
            SortedBin bin15 = bin14.nextNonEmpty;
            if (bin15 == null) {
                return minBin;
            }
            if (bin15.lv < minBin.lv) {
                minBin = bin15;
            }
            SortedBin bin16 = bin15.nextNonEmpty;
            if (bin16 == null) {
                return minBin;
            }
            if (bin16.lv < minBin.lv) {
                minBin = bin16;
            }
            SortedBin bin17 = bin16.nextNonEmpty;
            if (bin17 == null) {
                return minBin;
            }
            if (bin17.lv < minBin.lv) {
                minBin = bin17;
            }
            SortedBin bin18 = bin17.nextNonEmpty;
            if (bin18 == null) {
                return minBin;
            }
            if (bin18.lv < minBin.lv) {
                minBin = bin18;
            }
            SortedBin bin19 = bin18.nextNonEmpty;
            if (bin19 == null) {
                return minBin;
            }
            if (bin19.lv < minBin.lv) {
                minBin = bin19;
            }
            SortedBin bin20 = bin19.nextNonEmpty;
            if (bin20 == null) {
                return minBin;
            }
            if (bin20.lv < minBin.lv) {
                minBin = bin20;
            }
            SortedBin bin21 = bin20.nextNonEmpty;
            if (bin21 == null) {
                return minBin;
            }
            if (bin21.lv < minBin.lv) {
                minBin = bin21;
            }
            SortedBin bin22 = bin21.nextNonEmpty;
            if (bin22 == null) {
                return minBin;
            }
            if (bin22.lv < minBin.lv) {
                minBin = bin22;
            }
            SortedBin bin23 = bin22.nextNonEmpty;
            if (bin23 == null) {
                return minBin;
            }
            if (bin23.lv < minBin.lv) {
                minBin = bin23;
            }
            SortedBin bin24 = bin23.nextNonEmpty;
            if (bin24 == null) {
                return minBin;
            }
            if (bin24.lv < minBin.lv) {
                minBin = bin24;
            }
            SortedBin bin25 = bin24.nextNonEmpty;
            if (bin25 == null) {
                return minBin;
            }
            if (bin25.lv < minBin.lv) {
                minBin = bin25;
            }
            SortedBin bin26 = bin25.nextNonEmpty;
            if (bin26 == null) {
                return minBin;
            }
            if (bin26.lv < minBin.lv) {
                minBin = bin26;
            }
            SortedBin bin27 = bin26.nextNonEmpty;
            if (bin27 == null) {
                return minBin;
            }
            if (bin27.lv < minBin.lv) {
                minBin = bin27;
            }
            SortedBin bin28 = bin27.nextNonEmpty;
            if (bin28 == null) {
                return minBin;
            }
            if (bin28.lv < minBin.lv) {
                minBin = bin28;
            }
            SortedBin bin29 = bin28.nextNonEmpty;
            if (bin29 == null) {
                return minBin;
            }
            if (bin29.lv < minBin.lv) {
                minBin = bin29;
            }
            SortedBin bin30 = bin29.nextNonEmpty;
            if (bin30 == null) {
                return minBin;
            }
            if (bin30.lv < minBin.lv) {
                minBin = bin30;
            }
            SortedBin bin31 = bin30.nextNonEmpty;
            return (bin31 != null && bin31.lv < minBin.lv) ? bin31 : minBin;
        }
    }

    public void add(int key, V value) {
        this.size++;
        if (this.first.lp == 0 && this.second.lp == 0) {
            sortUp();
        }
        if (this.first.lp > 0) {
            this.first.add4(key, value);
            if (this.firstNonEmpty != this.first) {
                this.first.nextNonEmpty = this.firstNonEmpty;
                this.firstNonEmpty = this.first;
                return;
            }
            return;
        }
        this.second.add4(key, value);
        if (this.first.nextNonEmpty != this.second) {
            this.second.nextNonEmpty = this.first.nextNonEmpty;
            this.first.nextNonEmpty = this.second;
        }
    }

    private void sortUp() {
        if (this.size > this.peaksize) {
            this.peaksize = this.size;
        }
        int cnt = 8;
        SortedBin tbin = this.second;
        SortedBin lastNonEmpty = this.second;
        do {
            tbin = tbin.next();
            int nentries = tbin.binsize - tbin.lp;
            if (nentries > 0) {
                cnt += nentries;
                lastNonEmpty = tbin;
            }
        } while (cnt > tbin.binsize);
        int[] al_t = tbin.al;
        Object[] vla_t = tbin.vla;
        int tp = tbin.binsize - cnt;
        SortedBin otherNonEmpty = lastNonEmpty.nextNonEmpty;
        lastNonEmpty.nextNonEmpty = null;
        while (this.firstNonEmpty != null) {
            SortedBin minBin = this.firstNonEmpty.getMinBin();
            al_t[tp] = minBin.lv;
            vla_t[tp] = minBin.dropLowest();
            tp++;
        }
        int tp2 = tbin.binsize - cnt;
        tbin.lp = tp2;
        tbin.lv = tbin.al[tp2];
        tbin.nextNonEmpty = otherNonEmpty;
        this.firstNonEmpty = tbin;
    }

    public void clear() {
        this.size = 0;
        this.first = new SortedBin(4, this);
        this.second = new SortedBin(4, this);
        this.firstNonEmpty = null;
    }

    public int getSize() {
        return this.size;
    }

    public int getPeakSize() {
        return this.peaksize;
    }

    public int getExtract(Object[] targetArray) {
        int tsize = targetArray.length;
        int div = (this.size / tsize) + 1;
        int tp = 0;
        int lpi = 0;
        for (SortedBin bin = this.firstNonEmpty; bin != null; bin = bin.nextNonEmpty) {
            int lpi2 = lpi + bin.lp;
            Object[] vlai = bin.vla;
            int n = bin.binsize;
            while (lpi2 < n) {
                targetArray[tp] = vlai[lpi2];
                lpi2 += div;
                tp++;
            }
            lpi = lpi2 - n;
        }
        return tp;
    }
}

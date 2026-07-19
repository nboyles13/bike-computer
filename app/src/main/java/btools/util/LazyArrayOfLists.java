package btools.util;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LazyArrayOfLists<E> {
    private List<ArrayList<E>> lists;

    public LazyArrayOfLists(int size) {
        this.lists = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            this.lists.add(null);
        }
    }

    public List<E> getList(int idx) {
        ArrayList<E> list = this.lists.get(idx);
        if (list == null) {
            ArrayList<E> list2 = new ArrayList<>();
            this.lists.set(idx, list2);
            return list2;
        }
        return list;
    }

    public int getSize(int idx) {
        List<E> list = this.lists.get(idx);
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public void trimAll() {
        for (int idx = 0; idx < this.lists.size(); idx++) {
            ArrayList<E> list = this.lists.get(idx);
            if (list != null) {
                list.trimToSize();
            }
        }
    }
}

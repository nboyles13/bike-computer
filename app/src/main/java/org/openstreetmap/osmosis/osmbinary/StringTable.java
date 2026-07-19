package org.openstreetmap.osmosis.osmbinary;

import com.google.protobuf.ByteString;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import org.openstreetmap.osmosis.osmbinary.Osmformat;

/* JADX INFO: loaded from: classes4.dex */
public class StringTable {
    private HashMap<String, Integer> counts;
    private String[] set;
    private HashMap<String, Integer> stringmap;

    public StringTable() {
        clear();
    }

    public void incr(String s) {
        if (this.counts.containsKey(s)) {
            this.counts.put(s, new Integer(this.counts.get(s).intValue() + 1));
        } else {
            this.counts.put(s, new Integer(1));
        }
    }

    public int getIndex(String s) {
        return this.stringmap.get(s).intValue();
    }

    public void finish() {
        Comparator<String> comparator = new Comparator<String>() { // from class: org.openstreetmap.osmosis.osmbinary.StringTable.1
            @Override // java.util.Comparator
            public int compare(String s1, String s2) {
                int diff = ((Integer) StringTable.this.counts.get(s2)).intValue() - ((Integer) StringTable.this.counts.get(s1)).intValue();
                return diff;
            }
        };
        this.set = (String[]) this.counts.keySet().toArray(new String[0]);
        if (this.set.length > 0) {
            Arrays.sort(this.set, comparator);
            Arrays.sort(this.set, Math.min(128, this.set.length - 1), Math.min(16384, this.set.length - 1));
            Arrays.sort(this.set, Math.min(16384, this.set.length - 1), Math.min(2097152, this.set.length - 1), comparator);
        }
        this.stringmap = new HashMap<>(this.set.length * 2);
        for (int i = 0; i < this.set.length; i++) {
            this.stringmap.put(this.set[i], new Integer(i + 1));
        }
        this.counts = null;
    }

    public void clear() {
        this.counts = new HashMap<>(100);
        this.stringmap = null;
        this.set = null;
    }

    public Osmformat.StringTable.Builder serialize() {
        Osmformat.StringTable.Builder builder = Osmformat.StringTable.newBuilder();
        builder.addS(ByteString.copyFromUtf8(""));
        for (int i = 0; i < this.set.length; i++) {
            builder.addS(ByteString.copyFromUtf8(this.set[i]));
        }
        return builder;
    }
}

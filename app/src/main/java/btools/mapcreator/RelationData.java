package btools.mapcreator;

import btools.util.LongList;

/* JADX INFO: loaded from: classes.dex */
public class RelationData extends MapCreatorBase {
    public long description;
    public long rid;
    public LongList ways;

    public RelationData(long id) {
        this.rid = id;
        this.ways = new LongList(16);
    }

    public RelationData(long id, LongList ways) {
        this.rid = id;
        this.ways = ways;
    }
}

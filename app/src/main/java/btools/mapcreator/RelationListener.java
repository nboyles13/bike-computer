package btools.mapcreator;

/* JADX INFO: loaded from: classes.dex */
public interface RelationListener {
    void nextRelation(RelationData relationData) throws Exception;

    void nextRestriction(RelationData relationData, long j, long j2, long j3) throws Exception;
}

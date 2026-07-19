package btools.mapaccess;

/* JADX INFO: loaded from: classes.dex */
public interface OsmPos {
    int calcDistance(OsmPos osmPos);

    double getElev();

    int getILat();

    int getILon();

    long getIdFromPos();

    short getSElev();
}

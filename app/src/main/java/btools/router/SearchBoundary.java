package btools.router;

import btools.mapaccess.OsmNode;
import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes.dex */
public final class SearchBoundary {
    int direction;
    private int maxlat;
    private int maxlat0;
    private int maxlon;
    private int maxlon0;
    private int minlat;
    private int minlat0;
    private int minlon;
    private int minlon0;
    private OsmNode p;
    private int radius;

    public SearchBoundary(OsmNode n, int radius, int direction) {
        this.radius = radius;
        this.direction = direction;
        this.p = new OsmNode(n.ilon, n.ilat);
        int lon = (n.ilon / 5000000) * 5000000;
        int lat = (n.ilat / 5000000) * 5000000;
        this.minlon0 = lon - 5000000;
        this.minlat0 = lat - 5000000;
        this.maxlon0 = lon + 10000000;
        this.maxlat0 = 10000000 + lat;
        this.minlon = lon - DurationKt.NANOS_IN_MILLIS;
        this.minlat = lat - DurationKt.NANOS_IN_MILLIS;
        this.maxlon = lon + 6000000;
        this.maxlat = 6000000 + lat;
    }

    public static String getFileName(OsmNode n) {
        StringBuilder sbAppend;
        StringBuilder sbAppend2;
        int lon = (n.ilon / 5000000) * 5000000;
        int lat = (n.ilat / 5000000) * 5000000;
        int dlon = (lon / DurationKt.NANOS_IN_MILLIS) - 180;
        int dlat = (lat / DurationKt.NANOS_IN_MILLIS) - 90;
        if (dlon < 0) {
            sbAppend = new StringBuilder().append("W").append(-dlon);
        } else {
            sbAppend = new StringBuilder().append("E").append(dlon);
        }
        String slon = sbAppend.toString();
        if (dlat < 0) {
            sbAppend2 = new StringBuilder().append("S").append(-dlat);
        } else {
            sbAppend2 = new StringBuilder().append("N").append(dlat);
        }
        String slat = sbAppend2.toString();
        return slon + "_" + slat + ".trf";
    }

    public boolean isInBoundary(OsmNode n, int cost) {
        return this.radius > 0 ? n.calcDistance(this.p) < this.radius : cost == 0 ? n.ilon > this.minlon0 && n.ilon < this.maxlon0 && n.ilat > this.minlat0 && n.ilat < this.maxlat0 : n.ilon > this.minlon && n.ilon < this.maxlon && n.ilat > this.minlat && n.ilat < this.maxlat;
    }

    public int getBoundaryDistance(OsmNode n) {
        switch (this.direction) {
            case 0:
                return n.calcDistance(new OsmNode(n.ilon, this.minlat));
            case 1:
                return n.calcDistance(new OsmNode(this.minlon, n.ilat));
            case 2:
                return n.calcDistance(new OsmNode(n.ilon, this.maxlat));
            case 3:
                return n.calcDistance(new OsmNode(this.maxlon, n.ilat));
            default:
                throw new IllegalArgumentException("undefined direction: " + this.direction);
        }
    }
}

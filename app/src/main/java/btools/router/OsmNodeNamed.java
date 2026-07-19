package btools.router;

import btools.mapaccess.OsmNode;
import btools.util.CheapRuler;

/* JADX INFO: loaded from: classes.dex */
public class OsmNodeNamed extends OsmNode {
    public boolean isNogo;
    public String name;
    public double nogoWeight;
    public double radius;
    public byte wpttype;

    public OsmNodeNamed() {
        this.isNogo = false;
        this.wpttype = (byte) 1;
    }

    public OsmNodeNamed(OsmNode n) {
        super(n.ilon, n.ilat);
        this.isNogo = false;
        this.wpttype = (byte) 1;
    }

    @Override // btools.mapaccess.OsmNode
    public String toString() {
        if (Double.isNaN(this.nogoWeight)) {
            return this.ilon + "," + this.ilat + "," + this.name;
        }
        return this.ilon + "," + this.ilat + "," + this.name + "," + this.nogoWeight;
    }

    public double distanceWithinRadius(int lon1, int lat1, int lon2, int lat2, double totalSegmentLength) {
        int lat12 = lat1;
        int lat22 = lat2;
        double[] lonlat2m = CheapRuler.getLonLatToMeterScales((lat12 + lat22) >> 1);
        int lon12 = lon1;
        boolean isFirstPointWithinCircle = CheapRuler.distance(lon12, lat12, this.ilon, this.ilat) < this.radius;
        int lon22 = lon2;
        boolean isLastPointWithinCircle = CheapRuler.distance(lon22, lat22, this.ilon, this.ilat) < this.radius;
        if (isFirstPointWithinCircle) {
            if (isLastPointWithinCircle) {
                return totalSegmentLength;
            }
            lon22 = lon1;
            lon12 = lon2;
            lat22 = lat1;
            lat12 = lat2;
            isLastPointWithinCircle = isFirstPointWithinCircle;
        }
        int tmp = lon22 - lon12;
        double initialToProject = (((((double) (tmp * (this.ilon - lon12))) * lonlat2m[0]) * lonlat2m[0]) + ((((double) ((lat22 - lat12) * (this.ilat - lat12))) * lonlat2m[1]) * lonlat2m[1])) / totalSegmentLength;
        double initialToCenter = CheapRuler.distance(this.ilon, this.ilat, lon12, lat12);
        double halfDistanceWithin = Math.sqrt((this.radius * this.radius) - ((initialToCenter * initialToCenter) - (initialToProject * initialToProject)));
        if (isLastPointWithinCircle) {
            return (totalSegmentLength - initialToProject) + halfDistanceWithin;
        }
        return 2.0d * halfDistanceWithin;
    }

    public static OsmNodeNamed decodeNogo(String s) {
        OsmNodeNamed n = new OsmNodeNamed();
        int idx1 = s.indexOf(44);
        n.ilon = Integer.parseInt(s.substring(0, idx1));
        int idx2 = s.indexOf(44, idx1 + 1);
        n.ilat = Integer.parseInt(s.substring(idx1 + 1, idx2));
        int idx3 = s.indexOf(44, idx2 + 1);
        if (idx3 == -1) {
            n.name = s.substring(idx2 + 1);
            n.nogoWeight = Double.NaN;
        } else {
            n.name = s.substring(idx2 + 1, idx3);
            n.nogoWeight = Double.parseDouble(s.substring(idx3 + 1));
        }
        n.isNogo = true;
        return n;
    }
}

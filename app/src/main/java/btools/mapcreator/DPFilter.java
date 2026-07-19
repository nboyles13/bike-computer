package btools.mapcreator;

import btools.util.CheapRuler;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class DPFilter {
    private static double dp_sql_threshold = 0.16000000000000003d;

    public static void doDPFilter(List<OsmNodeP> nodes) {
        int first = 0;
        int last = nodes.size() - 1;
        while (first < last && (nodes.get(first + 1).bits & 64) != 0) {
            first++;
        }
        while (first < last && (nodes.get(last - 1).bits & 64) != 0) {
            last--;
        }
        if (last - first > 1) {
            doDPFilter(nodes, first, last);
        }
    }

    public static void doDPFilter(List<OsmNodeP> nodes, int first, int last) {
        List<OsmNodeP> list;
        double dx;
        List<OsmNodeP> list2 = nodes;
        int i = last;
        int index = -1;
        OsmNodeP p1 = nodes.get(first);
        OsmNodeP p2 = list2.get(i);
        double[] lonlat2m = CheapRuler.getLonLatToMeterScales((p1.ilat + p2.ilat) >> 1);
        double dlon2m = lonlat2m[0];
        double dlat2m = lonlat2m[1];
        double dx2 = ((double) (p2.ilon - p1.ilon)) * dlon2m;
        double maxSqDist = -1.0d;
        double dy = ((double) (p2.ilat - p1.ilat)) * dlat2m;
        double d2 = (dx2 * dx2) + (dy * dy);
        int i2 = first + 1;
        while (i2 < i) {
            double[] lonlat2m2 = lonlat2m;
            OsmNodeP p = list2.get(i2);
            double t = 0.0d;
            double d = 0.0d;
            if (d2 == 0.0d) {
                dx = dx2;
            } else {
                dx = dx2;
                double t2 = (((((double) (p.ilon - p1.ilon)) * dlon2m) * dx2) + ((((double) (p.ilat - p1.ilat)) * dlat2m) * dy)) / d2;
                if (t2 > 1.0d) {
                    d = 1.0d;
                } else if (t2 >= 0.0d) {
                    d = t2;
                }
                t = d;
            }
            double dy2 = dy;
            double dx22 = (((double) p.ilon) - (((double) p1.ilon) + (((double) (p2.ilon - p1.ilon)) * t))) * dlon2m;
            OsmNodeP p22 = p2;
            OsmNodeP p12 = p1;
            double dy22 = (((double) p.ilat) - (((double) p1.ilat) + (((double) (p2.ilat - p1.ilat)) * t))) * dlat2m;
            double sqDist = (dx22 * dx22) + (dy22 * dy22);
            if (sqDist > maxSqDist) {
                int index2 = i2;
                index = index2;
                maxSqDist = sqDist;
            }
            i2++;
            list2 = nodes;
            i = last;
            p2 = p22;
            lonlat2m = lonlat2m2;
            dy = dy2;
            dx2 = dx;
            p1 = p12;
        }
        if (index >= 0) {
            if (index - first <= 1) {
                list = nodes;
            } else {
                list = nodes;
                doDPFilter(list, first, index);
            }
            if (maxSqDist >= dp_sql_threshold) {
                OsmNodeP osmNodeP = list.get(index);
                osmNodeP.bits = (byte) (osmNodeP.bits | 64);
            }
            if (last - index > 1) {
                doDPFilter(list, index, last);
            }
        }
    }
}

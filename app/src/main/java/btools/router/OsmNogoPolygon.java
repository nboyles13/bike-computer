package btools.router;

import btools.util.CheapRuler;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class OsmNogoPolygon extends OsmNodeNamed {
    public final boolean isClosed;
    public final List<Point> points = new ArrayList();

    public static final class Point {
        public final int x;
        public final int y;

        Point(int lon, int lat) {
            this.x = lon;
            this.y = lat;
        }
    }

    public OsmNogoPolygon(boolean closed) {
        this.isClosed = closed;
        this.isNogo = true;
        this.name = "";
    }

    public final void addVertex(int lon, int lat) {
        this.points.add(new Point(lon, lat));
    }

    public void calcBoundingCircle() {
        int cxmin;
        OsmNogoPolygon osmNogoPolygon = this;
        int cxmin2 = Integer.MAX_VALUE;
        int cymin = Integer.MAX_VALUE;
        int cxmax = Integer.MIN_VALUE;
        int cymax = Integer.MIN_VALUE;
        for (int i = 0; i < osmNogoPolygon.points.size(); i++) {
            Point p = osmNogoPolygon.points.get(i);
            if (p.x < cxmin2) {
                cxmin2 = p.x;
            }
            if (p.x > cxmax) {
                cxmax = p.x;
            }
            if (p.y < cymin) {
                cymin = p.y;
            }
            if (p.y > cymax) {
                cymax = p.y;
            }
        }
        int i2 = cxmax + cxmin2;
        int cx = i2 / 2;
        int cy = (cymax + cymin) / 2;
        double[] lonlat2m = CheapRuler.getLonLatToMeterScales(cy);
        double dlon2m = lonlat2m[0];
        double dlat2m = lonlat2m[1];
        double rad = 0.0d;
        double dmax = 0.0d;
        int cx2 = -1;
        while (true) {
            int i_max = cx2;
            int i3 = 0;
            while (true) {
                cxmin = cxmin2;
                if (i3 >= osmNogoPolygon.points.size()) {
                    break;
                }
                Point p2 = osmNogoPolygon.points.get(i3);
                int cymin2 = cymin;
                int cymin3 = p2.x;
                int cxmax2 = cxmax;
                double x1 = ((double) (cx - cymin3)) * dlon2m;
                int cymax2 = cymax;
                int cymax3 = p2.y;
                double dlon2m2 = dlon2m;
                double dlon2m3 = cy - cymax3;
                double y1 = dlon2m3 * dlat2m;
                double dist = Math.sqrt((x1 * x1) + (y1 * y1));
                if (dist > rad && dist > dmax) {
                    dmax = dist;
                    i_max = i3;
                }
                i3++;
                cxmin2 = cxmin;
                cymin = cymin2;
                cxmax = cxmax2;
                cymax = cymax2;
                dlon2m = dlon2m2;
            }
            int cymin4 = cymin;
            int cxmax3 = cxmax;
            int cymax4 = cymax;
            if (i_max >= 0) {
                double dd = (1.0d - (rad / dmax)) * 0.5d;
                Point p3 = osmNogoPolygon.points.get(i_max);
                int cx3 = cx + ((int) ((((double) (p3.x - cx)) * dd) + 0.5d));
                cy += (int) ((((double) (p3.y - cy)) * dd) + 0.5d);
                double[] lonlat2m2 = CheapRuler.getLonLatToMeterScales(cy);
                dlon2m = lonlat2m2[0];
                dlat2m = lonlat2m2[1];
                double x12 = ((double) (cx3 - p3.x)) * dlon2m;
                double y12 = ((double) (cy - p3.y)) * dlat2m;
                double dSqrt = Math.sqrt((x12 * x12) + (y12 * y12));
                rad = dSqrt;
                dmax = dSqrt;
                cx = cx3;
                cxmin2 = cxmin;
                cymin = cymin4;
                cxmax = cxmax3;
                cymax = cymax4;
                cx2 = -1;
                osmNogoPolygon = this;
            } else {
                osmNogoPolygon.ilon = cx;
                osmNogoPolygon.ilat = cy;
                osmNogoPolygon.radius = (1.001d * rad) + 1.0d;
                return;
            }
        }
    }

    public boolean intersects(int i, int i2, int i3, int i4) {
        Point point = new Point(i, i2);
        Point point2 = new Point(i3, i4);
        int size = this.points.size() - 1;
        Point point3 = this.points.get(this.isClosed ? size : 0);
        for (int i5 = !this.isClosed ? 1 : 0; i5 <= size; i5++) {
            Point point4 = this.points.get(i5);
            if (intersect2D_2Segments(point, point2, point3, point4) > 0) {
                return true;
            }
            point3 = point4;
        }
        return false;
    }

    public boolean isOnPolyline(long px, long py) {
        int i_last = this.points.size() - 1;
        Point p1 = this.points.get(0);
        for (int i = 1; i <= i_last; i++) {
            Point p2 = this.points.get(i);
            if (isOnLine(px, py, p1.x, p1.y, p2.x, p2.y)) {
                return true;
            }
            p1 = p2;
        }
        return false;
    }

    public static boolean isOnLine(long px, long py, long p0x, long p0y, long p1x, long p1y) {
        double v10x = px - p0x;
        double v10y = py - p0y;
        double v12x = p1x - p0x;
        double v12y = p1y - p0y;
        if (v10x == 0.0d) {
            if (v10y == 0.0d) {
                return true;
            }
            return v12x == 0.0d && v12y / v10y >= 1.0d;
        }
        if (v10y == 0.0d) {
            return v12y == 0.0d && v12x / v10x >= 1.0d;
        }
        double kx = v12x / v10x;
        return kx >= 1.0d && kx == v12y / v10y;
    }

    public boolean isWithin(long j, long j2) {
        int size = this.points.size() - 1;
        Point point = this.points.get(this.isClosed ? size : 0);
        int i = 0;
        long j3 = point.x;
        long j4 = point.y;
        int i2 = !this.isClosed ? 1 : 0;
        while (i2 <= size) {
            Point point2 = this.points.get(i2);
            long j5 = point2.x;
            long j6 = point2.y;
            Point point3 = point;
            int i3 = i2;
            if (isOnLine(j, j2, j3, j4, j5, j6)) {
                return true;
            }
            if (j4 <= j2) {
                if (j6 > j2 && ((j5 - j3) * (j2 - j4)) - ((j - j3) * (j6 - j4)) > 0) {
                    i++;
                }
            } else if (j6 <= j2 && ((j5 - j3) * (j2 - j4)) - ((j - j3) * (j6 - j4)) < 0) {
                i--;
            }
            j3 = j5;
            j4 = j6;
            i2 = i3 + 1;
            point = point3;
        }
        return i != 0;
    }

    public double distanceWithinPolygon(int i, int i2, int i3, int i4) {
        OsmNogoPolygon osmNogoPolygon;
        Point point;
        int i5;
        Point point2;
        double dMin;
        double dDistance;
        OsmNogoPolygon osmNogoPolygon2 = this;
        int i6 = i;
        int i7 = i2;
        int i8 = i3;
        int i9 = i4;
        double d = 0.0d;
        Point point3 = new Point(i6, i7);
        Point point4 = new Point(i8, i9);
        Point point5 = null;
        if (osmNogoPolygon2.isWithin(i6, i7)) {
            point5 = point3;
        }
        int size = osmNogoPolygon2.points.size() - 1;
        int i10 = !osmNogoPolygon2.isClosed ? 1 : 0;
        int i11 = osmNogoPolygon2.isClosed ? size : 0;
        while (i10 <= size) {
            Point point6 = osmNogoPolygon2.points.get(i11);
            Point point7 = osmNogoPolygon2.points.get(i10);
            int iIntersect2D_2Segments = intersect2D_2Segments(point3, point4, point6, point7);
            int i12 = size;
            if (!osmNogoPolygon2.isClosed || iIntersect2D_2Segments != 1) {
                osmNogoPolygon = osmNogoPolygon2;
                double d2 = d;
                point = point3;
                Point point8 = point4;
                i5 = i10;
                Point point9 = point6;
                Point point10 = point7;
                if (iIntersect2D_2Segments != 2) {
                    point2 = point8;
                    dMin = d2;
                } else {
                    Point point11 = point;
                    point2 = point8;
                    Point point12 = point9;
                    Point point13 = point10;
                    dMin = d2 + Math.min(CheapRuler.distance(point11.x, point11.y, point2.x, point2.y), Math.min(CheapRuler.distance(point12.x, point12.y, point13.x, point13.y), Math.min(CheapRuler.distance(point11.x, point11.y, point13.x, point13.y), CheapRuler.distance(point12.x, point12.y, point2.x, point2.y))));
                    point5 = null;
                }
                i10 = i5 + 1;
                i11 = i5;
                i7 = i2;
                i9 = i4;
                point4 = point2;
                size = i12;
                point3 = point;
                d = dMin;
                osmNogoPolygon2 = osmNogoPolygon;
                i6 = i;
                i8 = i3;
            } else {
                int i13 = i6 - i8;
                i5 = i10;
                int i14 = point6.x - point7.x;
                int i15 = i7 - i9;
                Point point14 = point4;
                point = point3;
                int i16 = point6.y - point7.y;
                int i17 = (i13 * i16) - (i14 * i15);
                double d3 = d;
                long j = (((long) i6) * ((long) i9)) - (((long) i8) * ((long) i7));
                long j2 = (((long) point6.x) * ((long) point7.y)) - (((long) point7.x) * ((long) point6.y));
                Point point15 = new Point((int) (((((long) i14) * j) - (((long) i13) * j2)) / ((long) i17)), (int) (((((long) i16) * j) - (((long) i15) * j2)) / ((long) i17)));
                if (point5 != null) {
                    osmNogoPolygon = this;
                    if (osmNogoPolygon.isWithin((point15.x + point5.x) >> 1, (point15.y + point5.y) >> 1)) {
                        dDistance = d3 + CheapRuler.distance(point5.x, point5.y, point15.x, point15.y);
                    }
                    point5 = point15;
                    dMin = dDistance;
                    point2 = point14;
                    i10 = i5 + 1;
                    i11 = i5;
                    i7 = i2;
                    i9 = i4;
                    point4 = point2;
                    size = i12;
                    point3 = point;
                    d = dMin;
                    osmNogoPolygon2 = osmNogoPolygon;
                    i6 = i;
                    i8 = i3;
                } else {
                    osmNogoPolygon = this;
                }
                dDistance = d3;
                point5 = point15;
                dMin = dDistance;
                point2 = point14;
                i10 = i5 + 1;
                i11 = i5;
                i7 = i2;
                i9 = i4;
                point4 = point2;
                size = i12;
                point3 = point;
                d = dMin;
                osmNogoPolygon2 = osmNogoPolygon;
                i6 = i;
                i8 = i3;
            }
        }
        OsmNogoPolygon osmNogoPolygon3 = osmNogoPolygon2;
        double d4 = d;
        if (point5 != null && osmNogoPolygon3.isWithin(i3, i4)) {
            return d4 + CheapRuler.distance(point5.x, point5.y, i3, i4);
        }
        return d4;
    }

    private static boolean inSegment(Point p, Point seg_p0, Point seg_p1) {
        int sp0x = seg_p0.x;
        int sp1x = seg_p1.x;
        if (sp0x != sp1x) {
            int px = p.x;
            if (sp0x <= px && px <= sp1x) {
                return true;
            }
            if (sp0x >= px && px >= sp1x) {
                return true;
            }
            return false;
        }
        int sp0y = seg_p0.y;
        int sp1y = seg_p1.y;
        int py = p.y;
        if (sp0y <= py && py <= sp1y) {
            return true;
        }
        if (sp0y >= py && py >= sp1y) {
            return true;
        }
        return false;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static int intersect2D_2Segments(Point point, Point point2, Point point3, Point point4) {
        double d;
        double d2;
        long j = point2.x - point.x;
        long j2 = point2.y - point.y;
        long j3 = point4.x - point3.x;
        long j4 = point4.y - point3.y;
        long j5 = point.x - point3.x;
        long j6 = point.y - point3.y;
        double d3 = (j * j4) - (j2 * j3);
        if (d3 != 0.0d) {
            double d4 = ((j3 * j6) - (j4 * j5)) / d3;
            if (d4 < 0.0d || d4 > 1.0d) {
                return 0;
            }
            double d5 = ((j * j6) - (j2 * j5)) / d3;
            return (d5 < 0.0d || d5 > 1.0d) ? 0 : 1;
        }
        if ((j * j6) - (j2 * j5) != 0 || (j3 * j6) - (j4 * j5) != 0) {
            return 0;
        }
        Object[] objArr = j == 0 && j2 == 0;
        Object[] objArr2 = j3 == 0 && j4 == 0;
        if (objArr == true && objArr2 == true) {
            return (j5 == 0 && j6 == 0) ? 0 : 1;
        }
        if (objArr == true) {
            return inSegment(point, point3, point4) ? 1 : 0;
        }
        if (objArr2 == true) {
            return inSegment(point3, point, point2) ? 1 : 0;
        }
        int i = point2.x - point3.x;
        int i2 = point2.y - point3.y;
        if (j3 != 0) {
            d2 = ((long) i) / j3;
            d = j5 / j3;
        } else {
            d = j6 / j4;
            d2 = ((long) i2) / j4;
        }
        if (d > d2) {
            double d6 = d;
            d = d2;
            d2 = d6;
        }
        if (d > 1.0d || d2 < 0.0d) {
            return 0;
        }
        return (d >= 0.0d ? d : 0.0d) == (d2 <= 1.0d ? d2 : 1.0d) ? 1 : 2;
    }
}

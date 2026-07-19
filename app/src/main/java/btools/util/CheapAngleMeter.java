package btools.util;

/* JADX INFO: loaded from: classes.dex */
public final class CheapAngleMeter {
    private double cosangle;

    public double getCosAngle() {
        return this.cosangle;
    }

    public double calcAngle(int lon0, int lat0, int lon1, int lat1, int lon2, int lat2) {
        double[] lonlat2m = CheapRuler.getLonLatToMeterScales(lat1);
        double lon2m = lonlat2m[0];
        double lat2m = lonlat2m[1];
        double dx10 = ((double) (lon1 - lon0)) * lon2m;
        double dy10 = ((double) (lat1 - lat0)) * lat2m;
        double dx21 = ((double) (lon2 - lon1)) * lon2m;
        double dy21 = ((double) (lat2 - lat1)) * lat2m;
        double dd = Math.sqrt(((dx10 * dx10) + (dy10 * dy10)) * ((dx21 * dx21) + (dy21 * dy21)));
        if (dd == 0.0d) {
            this.cosangle = 1.0d;
            return 0.0d;
        }
        double sinp = ((dy10 * dx21) - (dx10 * dy21)) / dd;
        double cosp = ((dy10 * dy21) + (dx10 * dx21)) / dd;
        this.cosangle = cosp;
        double offset = 0.0d;
        double s2 = sinp * sinp;
        if (s2 > 0.5d) {
            if (sinp > 0.0d) {
                sinp = -cosp;
                offset = 90.0d;
            } else {
                sinp = cosp;
                offset = -90.0d;
            }
            s2 = cosp * cosp;
        } else if (cosp < 0.0d) {
            sinp = -sinp;
            offset = sinp > 0.0d ? -180.0d : 180.0d;
        }
        return offset + (((((((2.56491d * s2) + 4.30904d) * s2) + 9.57565d) * s2) + 57.4539d) * sinp);
    }

    public static double getAngle(int lon1, int lat1, int lon2, int lat2) {
        double xdiff = lat2 - lat1;
        double ydiff = lon2 - lon1;
        double res = Math.toDegrees(Math.atan2(ydiff, xdiff));
        return res;
    }

    public static double getDirection(int lon1, int lat1, int lon2, int lat2) {
        double res = getAngle(lon1, lat1, lon2, lat2);
        return normalize(res);
    }

    public static double normalize(double a) {
        if (a >= 360.0d) {
            return a - ((double) (((int) (a / 360.0d)) * 360));
        }
        return a < 0.0d ? a - ((double) ((((int) (a / 360.0d)) - 1) * 360)) : a;
    }

    public static double getDifferenceFromDirection(double b1, double b2) {
        double r = (b2 - b1) % 360.0d;
        if (r < -180.0d) {
            r += 360.0d;
        }
        if (r >= 180.0d) {
            r -= 360.0d;
        }
        return Math.abs(r);
    }
}

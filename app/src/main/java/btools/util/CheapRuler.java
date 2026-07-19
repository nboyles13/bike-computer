package btools.util;

/* JADX INFO: loaded from: classes.dex */
public final class CheapRuler {
    public static final double DEG_TO_RAD = 0.017453292519943295d;
    public static final double ILATLNG_TO_LATLNG = 1.0E-6d;
    public static final int KILOMETERS_TO_METERS = 1000;
    private static final int SCALE_CACHE_INCREMENT = 100000;
    private static final int SCALE_CACHE_LENGTH = 1800;
    private static final double[][] SCALE_CACHE = new double[SCALE_CACHE_LENGTH][];

    static {
        for (int i = 0; i < SCALE_CACHE_LENGTH; i++) {
            SCALE_CACHE[i] = calcKxKyFromILat((SCALE_CACHE_INCREMENT * i) + 50000);
        }
    }

    private static double[] calcKxKyFromILat(int ilat) {
        double lat = ((((double) ilat) * 1.0E-6d) - 90.0d) * 0.017453292519943295d;
        double cos = Math.cos(lat);
        double cos2 = ((cos * 2.0d) * cos) - 1.0d;
        double cos3 = ((cos * 2.0d) * cos2) - cos;
        double cos4 = ((cos * 2.0d) * cos3) - cos2;
        double cos5 = ((2.0d * cos) * cos4) - cos3;
        double[] kxky = {(((111.41513d * cos) - (0.09455d * cos3)) + (1.2E-4d * cos5)) * 1.0E-6d * 1000.0d, ((111.13209d - (0.56605d * cos2)) + (0.0012d * cos4)) * 1.0E-6d * 1000.0d};
        return kxky;
    }

    public static double[] getLonLatToMeterScales(int ilat) {
        return SCALE_CACHE[ilat / SCALE_CACHE_INCREMENT];
    }

    public static double distance(int ilon1, int ilat1, int ilon2, int ilat2) {
        double[] kxky = getLonLatToMeterScales((ilat1 + ilat2) >> 1);
        double dlon = ((double) (ilon1 - ilon2)) * kxky[0];
        double dlat = ((double) (ilat1 - ilat2)) * kxky[1];
        return Math.sqrt((dlat * dlat) + (dlon * dlon));
    }

    public static int[] destination(int lon1, int lat1, double distance, double angle) {
        double[] lonlat2m = getLonLatToMeterScales(lat1);
        double lon2m = lonlat2m[0];
        double lat2m = lonlat2m[1];
        double angle2 = 90.0d - angle;
        double st = Math.sin((angle2 * 3.141592653589793d) / 180.0d);
        double ct = Math.cos((3.141592653589793d * angle2) / 180.0d);
        int lon2 = (int) (((double) lon1) + 0.5d + ((ct * distance) / lon2m));
        int lat2 = (int) (((double) lat1) + 0.5d + ((st * distance) / lat2m));
        int[] ret = {lon2, lat2};
        return ret;
    }
}

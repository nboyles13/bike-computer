package btools.router;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public abstract class Formatter {
    static final String MESSAGES_HEADER = "Longitude\tLatitude\tElevation\tDistance\tCostPerKm\tElevCost\tTurnCost\tNodeCost\tInitialCost\tWayTags\tNodeTags\tTime\tEnergy";
    static final String dateformat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    RoutingContext rc;

    public abstract String format(OsmTrack osmTrack);

    Formatter() {
    }

    Formatter(RoutingContext rc) {
        this.rc = rc;
    }

    public void write(String filename, OsmTrack t) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write(format(t));
        bw.close();
    }

    public OsmTrack read(String filename) throws Exception {
        return null;
    }

    static String formatILon(int ilon) {
        return formatPos(ilon - 180000000);
    }

    static String formatILat(int ilat) {
        return formatPos(ilat - 90000000);
    }

    private static String formatPos(int p) {
        boolean negative = p < 0;
        if (negative) {
            p = -p;
        }
        char[] ac = new char[12];
        int i = 11;
        while (true) {
            if (p == 0 && i <= 3) {
                break;
            }
            int i2 = i - 1;
            ac[i] = (char) ((p % 10) + 48);
            p /= 10;
            if (i2 != 5) {
                i = i2;
            } else {
                i = i2 - 1;
                ac[i2] = '.';
            }
        }
        if (negative) {
            ac[i] = '-';
            i--;
        }
        return new String(ac, i + 1, 11 - i);
    }

    public static String getFormattedTime2(int s) {
        int seconds = (int) (((double) s) + 0.5d);
        int hours = seconds / 3600;
        int minutes = (seconds - (hours * 3600)) / 60;
        int seconds2 = (seconds - (hours * 3600)) - (minutes * 60);
        String time = "";
        if (hours != 0) {
            time = hours + "h ";
        }
        if (minutes != 0) {
            time = time + minutes + "m ";
        }
        if (seconds2 != 0) {
            return time + seconds2 + "s";
        }
        return time;
    }

    public static String getFormattedEnergy(int energy) {
        return format1(((double) energy) / 3600000.0d) + "kwh";
    }

    private static String format1(double n) {
        String s = new StringBuilder().append((long) ((10.0d * n) + 0.5d)).toString();
        int len = s.length();
        return s.substring(0, len - 1) + "." + s.charAt(len - 1);
    }

    public static String getFormattedTime3(float time) {
        SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat(dateformat, Locale.US);
        TIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date d = new Date((long) (1000.0f * time));
        return TIMESTAMP_FORMAT.format(d);
    }
}

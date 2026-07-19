package btools.server;

import androidx.core.view.MotionEventCompat;
import btools.mapaccess.OsmNode;
import btools.router.OsmNodeNamed;
import btools.router.SuspectInfo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/* JADX INFO: loaded from: classes.dex */
public class SuspectManager extends Thread {
    private static SimpleDateFormat dfTimestampZ = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    static NearRecentWps nearRecentWps = new NearRecentWps();
    static NearRecentWps hiddenWps = new NearRecentWps();
    private static Map<String, SuspectList> allSuspectsMap = new HashMap();

    private static String formatZ(Date date) {
        String str;
        synchronized (dfTimestampZ) {
            str = dfTimestampZ.format(date);
        }
        return str;
    }

    private static String formatAge(File f) {
        return formatAge(System.currentTimeMillis() - f.lastModified());
    }

    private static String formatAge(long age) {
        long minutes = age / 60000;
        if (minutes < 60) {
            return minutes + " minutes";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " hours";
        }
        long days = hours / 24;
        return days + " days";
    }

    private static String getLevelDecsription(int level) {
        switch (level) {
            case 22:
                return "tertiary";
            case 23:
            case 25:
            case 27:
            case 29:
            default:
                return "none";
            case 24:
                return "secondary";
            case 26:
                return "primary";
            case MotionEventCompat.AXIS_RELATIVE_Y /* 28 */:
                return "trunk";
            case 30:
                return "motorway";
        }
    }

    private static void markFalsePositive(SuspectList suspects, long id) throws IOException {
        new File("falsepositives/" + id).createNewFile();
        for (int isuspect = 0; isuspect < suspects.cnt; isuspect++) {
            if (id == suspects.ids[isuspect]) {
                suspects.falsePositive[isuspect] = true;
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:52:0x0108  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0124  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0127  */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0130  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x014f  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x0153  */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01e3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void newAndConfirmedJson(SuspectList suspects, BufferedWriter bw, String filter, int level, Area polygon) throws IOException {
        String str;
        File confirmedEntry;
        SuspectList suspectList = suspects;
        String str2 = filter;
        Area area = polygon;
        bw.write("{\n");
        bw.write("\"type\": \"FeatureCollection\",\n");
        bw.write("\"features\": [");
        int n = 0;
        int isuspect = 0;
        while (isuspect < suspectList.cnt) {
            long id = suspectList.ids[isuspect];
            int prio = suspectList.prios[isuspect];
            int nprio = ((prio + 1) / 2) * 2;
            if (nprio >= level && (((!"new".equals(str2) && !"deferred".equals(str2)) || suspectList.newOrConfirmed[isuspect]) && !("fp".equals(str2) ^ suspectList.falsePositive[isuspect]) && (area == null || area.isInBoundingBox(id)))) {
                String dueTime = null;
                if ("deferred".equals(str2)) {
                    File fixedEntry = new File("fixedsuspects/" + id);
                    if (fixedEntry.exists()) {
                        long fixedTs = fixedEntry.lastModified();
                        if (fixedTs >= suspectList.timestamp) {
                            long hideTime = fixedTs - System.currentTimeMillis();
                            if (hideTime >= 0) {
                                str = "new";
                            } else {
                                File confirmedEntry2 = new File("confirmednegatives/" + id);
                                if (confirmedEntry2.exists()) {
                                    str = "new";
                                    if (confirmedEntry2.lastModified() > suspectList.timestamp) {
                                    }
                                } else {
                                    str = "new";
                                }
                            }
                            dueTime = hideTime < 0 ? "(asap)" : formatAge(43200000 + hideTime);
                            if (area != null) {
                                confirmedEntry = new File("confirmednegatives/" + id);
                                String status = !suspectList.newOrConfirmed[isuspect] ? str : "archived";
                                if (confirmedEntry.exists()) {
                                }
                                if (dueTime != null) {
                                }
                                if (n > 0) {
                                }
                                int ilon = (int) (id >> 32);
                                int ilat = (int) ((-1) & id);
                                double dlon = ((double) (ilon - 180000000)) / 1000000.0d;
                                double dlat = ((double) (ilat - 90000000)) / 1000000.0d;
                                String slevel = getLevelDecsription(nprio);
                                bw.write("\n{\n");
                                bw.write("  \"id\": " + n + ",\n");
                                bw.write("  \"type\": \"Feature\",\n");
                                bw.write("  \"properties\": {\n");
                                bw.write("    \"issue_id\": \"" + id + "\",\n");
                                bw.write("    \"Status\": \"" + status + "\",\n");
                                if (dueTime != null) {
                                }
                                bw.write("    \"Level\": \"" + slevel + "\"\n");
                                bw.write("  },\n");
                                bw.write("  \"geometry\": {\n");
                                bw.write("    \"type\": \"Point\",\n");
                                bw.write("    \"coordinates\": [\n");
                                bw.write("      " + dlon + ",\n");
                                bw.write("      " + dlat + "\n");
                                bw.write("    ]\n");
                                bw.write("  }\n");
                                bw.write("}");
                                n++;
                            }
                        }
                    }
                } else if (!isFixed(id, suspectList.timestamp)) {
                    str = "new";
                    if (area != null || area.isInArea(id)) {
                        confirmedEntry = new File("confirmednegatives/" + id);
                        String status2 = !suspectList.newOrConfirmed[isuspect] ? str : "archived";
                        if (confirmedEntry.exists()) {
                            status2 = "confirmed " + formatAge(confirmedEntry) + " ago";
                        }
                        if (dueTime != null) {
                            status2 = "deferred";
                        }
                        if (n > 0) {
                            bw.write(",");
                        }
                        int ilon2 = (int) (id >> 32);
                        int ilat2 = (int) ((-1) & id);
                        double dlon2 = ((double) (ilon2 - 180000000)) / 1000000.0d;
                        double dlat2 = ((double) (ilat2 - 90000000)) / 1000000.0d;
                        String slevel2 = getLevelDecsription(nprio);
                        bw.write("\n{\n");
                        bw.write("  \"id\": " + n + ",\n");
                        bw.write("  \"type\": \"Feature\",\n");
                        bw.write("  \"properties\": {\n");
                        bw.write("    \"issue_id\": \"" + id + "\",\n");
                        bw.write("    \"Status\": \"" + status2 + "\",\n");
                        if (dueTime != null) {
                            bw.write("    \"DueTime\": \"" + dueTime + "\",\n");
                        }
                        bw.write("    \"Level\": \"" + slevel2 + "\"\n");
                        bw.write("  },\n");
                        bw.write("  \"geometry\": {\n");
                        bw.write("    \"type\": \"Point\",\n");
                        bw.write("    \"coordinates\": [\n");
                        bw.write("      " + dlon2 + ",\n");
                        bw.write("      " + dlat2 + "\n");
                        bw.write("    ]\n");
                        bw.write("  }\n");
                        bw.write("}");
                        n++;
                    }
                }
            }
            isuspect++;
            suspectList = suspects;
            str2 = filter;
            area = polygon;
        }
        bw.write("\n  ]\n");
        bw.write("}\n");
        bw.flush();
    }

    public static void process(String url, BufferedWriter bw) throws IOException {
        try {
            _process(url, bw);
        } catch (IllegalArgumentException iae) {
            try {
                bw.write("<br><br>ERROR: " + iae.getMessage() + "<br><br>\n\n");
                bw.write("(press Browser-Back to continue)\n");
                bw.flush();
            } catch (IOException e) {
            }
        }
    }

    private static void _process(String url, BufferedWriter bw) throws IOException {
        String str;
        long id;
        Area polygon;
        boolean showWatchList;
        File suspectFile;
        long id2;
        Area polygon2;
        String str2;
        long id3;
        String str3;
        String str4;
        String message;
        long id4;
        SuspectList suspects;
        String challenge;
        BufferedWriter bufferedWriter;
        String filter;
        String country;
        String message2;
        Area polygon3;
        int isuspect;
        String str5;
        String str6;
        String country2;
        String country3;
        String str7;
        String filter2;
        String challenge2;
        int maxprio;
        SuspectList suspects2;
        long id5;
        String str8;
        String str9;
        String str10;
        String str11;
        String profile;
        String challenge3;
        String country4;
        String message3;
        String message4;
        String param;
        String str12;
        long timeNow;
        String str13;
        String filter3;
        String country5;
        String filter4;
        String challenge4;
        long id6;
        String dueTime;
        String country6;
        String linkSub;
        String filter5;
        String c;
        BufferedWriter bufferedWriter2 = bw;
        StringTokenizer tk = new StringTokenizer(url, "/?");
        tk.nextToken();
        tk.nextToken();
        String filter6 = null;
        String suspectFilename = "worldsuspects.txt";
        String challenge5 = "";
        String country7 = "";
        while (true) {
            str = "/";
            if (!tk.hasMoreTokens()) {
                break;
            }
            c = tk.nextToken();
            if ("all".equals(c) || "new".equals(c) || "confirmed".equals(c) || "fp".equals(c) || "deferred".equals(c)) {
                break;
            }
            if (country7.length() == 0 && !"world".equals(c) && new File(c + "suspects.txt").exists()) {
                suspectFilename = c + "suspects.txt";
                challenge5 = "/" + c;
            } else {
                country7 = country7 + "/" + c;
            }
        }
        filter6 = c;
        SuspectList suspects3 = getAllSuspects(suspectFilename);
        String filter7 = "</body></html>\n";
        if ("/world".equals(country7) || "".equals(country7)) {
            id = 0;
            polygon = null;
        } else {
            id = 0;
            File polyFile = new File("worldpolys" + country7 + ".poly");
            if (!polyFile.exists()) {
                bufferedWriter2.write("polygon file for country '" + country7 + "' not found\n");
                bufferedWriter2.write("</body></html>\n");
                bw.flush();
                return;
            }
            polygon = new Area(polyFile);
        }
        if (url.endsWith(".json")) {
            StringTokenizer tk2 = new StringTokenizer(tk.nextToken(), ".");
            int level = Integer.parseInt(tk2.nextToken());
            newAndConfirmedJson(suspects3, bufferedWriter2, filter6, level, polygon);
            return;
        }
        bufferedWriter2.write("<html><body>\n");
        bufferedWriter2.write("BRouter suspect manager. <a href=\"http://brouter.de/brouter/suspect_manager_help.html\">Help</a><br><br>\n");
        String str14 = "/brouter/suspects";
        if (filter6 != null) {
            String str15 = "/brouter/suspects";
            String challenge6 = filter6;
            String str16 = "/";
            File suspectFile2 = new File("worldsuspects.txt");
            if (!suspectFile2.exists()) {
                bufferedWriter2.write("suspect file worldsuspects.txt not found\n");
                bufferedWriter2.write("</body></html>\n");
                bw.flush();
                return;
            }
            if (!tk.hasMoreTokens()) {
                showWatchList = false;
                suspectFile = suspectFile2;
                id2 = id;
            } else {
                String t = tk.nextToken();
                if ("watchlist".equals(t)) {
                    showWatchList = true;
                    suspectFile = suspectFile2;
                    id2 = id;
                } else {
                    long j = Long.parseLong(t);
                    showWatchList = false;
                    suspectFile = suspectFile2;
                    id2 = j;
                }
            }
            String str17 = ",";
            String str18 = "<br>\n";
            String str19 = "confirmednegatives/";
            String challenge7 = "new";
            if (showWatchList) {
                bufferedWriter2.write("watchlist for " + country7 + "\n");
                bufferedWriter2.write("<br><a href=\"/brouter/suspects" + challenge5 + "\">back to country list</a><br><br>\n");
                long timeNow2 = System.currentTimeMillis();
                int isuspect2 = 0;
                while (isuspect2 < suspects3.cnt) {
                    StringTokenizer tk3 = tk;
                    long id7 = suspects3.ids[isuspect2];
                    if (polygon != null && !polygon.isInBoundingBox(id7)) {
                        timeNow = timeNow2;
                        id6 = id7;
                        country5 = country7;
                        country6 = str18;
                        str12 = filter7;
                        filter3 = challenge6;
                        dueTime = str15;
                        str13 = str16;
                        bufferedWriter2 = bw;
                        filter4 = challenge5;
                        challenge4 = str19;
                    } else {
                        str12 = filter7;
                        if (new File("falsepositives/" + id7).exists()) {
                            timeNow = timeNow2;
                            id6 = id7;
                            country5 = country7;
                            country6 = str18;
                            filter3 = challenge6;
                            dueTime = str15;
                            str13 = str16;
                            bufferedWriter2 = bw;
                            filter4 = challenge5;
                            challenge4 = str19;
                        } else {
                            File fixedEntry = new File("fixedsuspects/" + id7);
                            if (!fixedEntry.exists()) {
                                timeNow = timeNow2;
                                id6 = id7;
                                country5 = country7;
                                country6 = str18;
                                filter3 = challenge6;
                                dueTime = str15;
                                str13 = str16;
                                bufferedWriter2 = bw;
                                filter4 = challenge5;
                                challenge4 = str19;
                            } else {
                                long fixedTs = fixedEntry.lastModified();
                                String filter8 = str18;
                                if (fixedTs < suspects3.timestamp) {
                                    timeNow = timeNow2;
                                    id6 = id7;
                                    country5 = country7;
                                    country6 = filter8;
                                    filter3 = challenge6;
                                    dueTime = str15;
                                    str13 = str16;
                                    bufferedWriter2 = bw;
                                    filter4 = challenge5;
                                    challenge4 = str19;
                                } else {
                                    long hideTime = fixedTs - timeNow2;
                                    if (hideTime >= 0) {
                                        timeNow = timeNow2;
                                    } else {
                                        timeNow = timeNow2;
                                        File confirmedEntry = new File(str19 + id7);
                                        if (confirmedEntry.exists() && confirmedEntry.lastModified() > suspects3.timestamp) {
                                            id6 = id7;
                                            country5 = country7;
                                            country6 = filter8;
                                            filter3 = challenge6;
                                            dueTime = str15;
                                            str13 = str16;
                                            bufferedWriter2 = bw;
                                            filter4 = challenge5;
                                            challenge4 = str19;
                                        }
                                    }
                                    if (polygon != null && !polygon.isInArea(id7)) {
                                        id6 = id7;
                                        country5 = country7;
                                        country6 = filter8;
                                        filter3 = challenge6;
                                        dueTime = str15;
                                        str13 = str16;
                                        bufferedWriter2 = bw;
                                        filter4 = challenge5;
                                        challenge4 = str19;
                                    } else {
                                        str13 = str16;
                                        filter3 = challenge6;
                                        String countryId = challenge5 + country7 + str13 + filter3 + str13 + id7;
                                        String hint = "&nbsp;&nbsp;&nbsp;due in " + (hideTime < 0 ? "(asap)" : formatAge(hideTime + 43200000));
                                        country5 = country7;
                                        filter4 = challenge5;
                                        int ilon = (int) (id7 >> 32);
                                        challenge4 = str19;
                                        int ilat = (int) (id7 & (-1));
                                        id6 = id7;
                                        double dlon = ((double) (ilon - 180000000)) / 1000000.0d;
                                        int ilon2 = ilat - 90000000;
                                        double dlat = ((double) ilon2) / 1000000.0d;
                                        dueTime = str15;
                                        String url2 = dueTime + countryId;
                                        StringBuilder sbAppend = new StringBuilder().append("<a href=\"").append(url2).append("\">").append(dlon).append(",").append(dlat).append("</a>").append(hint);
                                        country6 = filter8;
                                        bufferedWriter2 = bw;
                                        bufferedWriter2.write(sbAppend.append(country6).toString());
                                    }
                                }
                            }
                        }
                    }
                    isuspect2++;
                    str16 = str13;
                    str19 = challenge4;
                    str15 = dueTime;
                    challenge5 = filter4;
                    tk = tk3;
                    timeNow2 = timeNow;
                    str18 = country6;
                    challenge6 = filter3;
                    filter7 = str12;
                    country7 = country5;
                }
                bufferedWriter2.write(filter7);
                bw.flush();
                return;
            }
            long id8 = id2;
            String country8 = country7;
            String str20 = str16;
            String filter9 = challenge5;
            if (!tk.hasMoreTokens()) {
                polygon2 = polygon;
                str2 = "<br><a href=\"/brouter/suspects";
                id3 = id8;
                str3 = "</body></html>\n";
                str4 = str15;
                message = null;
            } else {
                String command = tk.nextToken();
                if (!"falsepositive".equals(command)) {
                    polygon2 = polygon;
                    id3 = id8;
                    str3 = "</body></html>\n";
                    str4 = str15;
                    message3 = null;
                } else {
                    polygon2 = polygon;
                    id3 = id8;
                    int wps = nearRecentWps.count(id3);
                    str3 = "</body></html>\n";
                    if (wps < 8) {
                        str4 = str15;
                        message3 = "marking false-positive requires at least 8 recent nearby waypoints from BRouter-Web, found: " + wps + "<br><br>****** DO SOME MORE TEST-ROUTINGS IN BROUTER-WEB ******* before marking false positive";
                    } else {
                        str4 = str15;
                        markFalsePositive(suspects3, id3);
                        message3 = "Marked issue " + id3 + " as false-positive";
                        id3 = 0;
                    }
                }
                if (!"confirm".equals(command)) {
                    message4 = message3;
                } else {
                    int wps2 = nearRecentWps.count(id3);
                    if (wps2 < 2) {
                        message4 = "marking confirmed requires at least 2 recent nearby waypoints from BRouter-Web, found: " + wps2 + "<br><br>****** DO AT LEAST ONE TEST-ROUTING IN BROUTER-WEB ******* before marking confirmed";
                    } else {
                        message4 = message3;
                        new File("confirmednegatives/" + id3).createNewFile();
                    }
                }
                if ("fixed".equals(command)) {
                    File fixedMarker = new File("fixedsuspects/" + id3);
                    if (!fixedMarker.exists()) {
                        fixedMarker.createNewFile();
                    }
                    int hideDays = 0;
                    if (tk.hasMoreTokens()) {
                        String param2 = tk.nextToken();
                        if (!param2.startsWith("ndays=")) {
                            param = param2;
                        } else {
                            param = param2.substring("ndays=".length());
                        }
                        try {
                            hideDays = Integer.parseInt(param);
                            if (hideDays < 1 || hideDays > 999) {
                                throw new IllegalArgumentException("hideDays must be within 1..999");
                            }
                            str2 = "<br><a href=\"/brouter/suspects";
                            message = "Hide issue " + id3 + " for " + hideDays + " days";
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("not a number: " + param);
                        }
                    } else {
                        str2 = "<br><a href=\"/brouter/suspects";
                        message = "Marked issue " + id3 + " as fixed";
                    }
                    if (hideDays > 0) {
                        OsmNodeNamed nn = new OsmNodeNamed(new OsmNode(id3));
                        nn.name = new StringBuilder().append(hideDays).toString();
                        hiddenWps.add(nn);
                    }
                    long id9 = hideDays;
                    fixedMarker.setLastModified(System.currentTimeMillis() + (id9 * 86400000));
                    id3 = 0;
                } else {
                    str2 = "<br><a href=\"/brouter/suspects";
                    message = message4;
                }
            }
            if (id3 != 0) {
                String countryId2 = filter9 + country8 + str20 + challenge6 + str20 + id3;
                int ilon3 = (int) (id3 >> 32);
                int ilat2 = (int) (id3 & (-1));
                double dlon2 = ((double) (ilon3 - 180000000)) / 1000000.0d;
                double dlat2 = ((double) (ilat2 - 90000000)) / 1000000.0d;
                File configFile = new File("configs/profile.cfg");
                if (!configFile.exists()) {
                    str11 = "<br>\n";
                    profile = "car-eco";
                } else {
                    str11 = "<br>\n";
                    BufferedReader br = new BufferedReader(new FileReader(configFile));
                    String profile2 = br.readLine();
                    br.close();
                    profile = profile2;
                }
                int triggers = suspects3.trigger4Id(id3);
                SuspectList daily = getDailySuspectsIfLoaded();
                if (daily != null && daily != suspects3) {
                    triggers |= daily.trigger4Id(id3);
                }
                String triggerText = SuspectInfo.getTriggerText(triggers);
                long id10 = id3;
                String url1 = "http://brouter.de/brouter-web/#map=18/" + dlat2 + str20 + dlon2 + "/OpenStreetMap&lonlats=" + dlon2 + "," + dlat2 + "&profile=" + profile;
                String url22 = "https://www.openstreetmap.org/?mlat=" + dlat2 + "&mlon=" + dlon2 + "#map=19/" + dlat2 + str20 + dlon2 + "&layers=N";
                String url3 = "http://127.0.0.1:8111/load_and_zoom?left=" + (dlon2 - 0.00156d) + "&bottom=" + (dlat2 - 0.001d) + "&right=" + (dlon2 + 0.00156d) + "&top=" + (dlat2 + 0.001d);
                Date weekAgo = new Date(System.currentTimeMillis() - 604800000);
                String url4a = "https://overpass-turbo.eu/?Q=[date:&quot;" + formatZ(weekAgo) + "Z&quot;];way[highway]({{bbox}});out meta geom;&C=" + dlat2 + ";" + dlon2 + ";18&R";
                String url4b = "https://overpass-turbo.eu/?Q=(node(around%3A1%2C%7B%7Bcenter%7D%7D)-%3E.n%3Bway(bn.n)%5Bhighway%5D%3Brel(bn.n%3A%22via%22)%5Btype%3Drestriction%5D%3B)%3Bout%20meta%3B%3E%3Bout%20skel%20qt%3B&C=" + dlat2 + ";" + dlon2 + ";18&R";
                String url5 = "https://tyrasd.github.io/latest-changes/#16/" + dlat2 + str20 + dlon2;
                String url6 = "https://apps.sentinel-hub.com/sentinel-playground/?source=S2L2A&lat=" + dlat2 + "&lng=" + dlon2 + "&zoom=15";
                if (message == null) {
                    bufferedWriter = bw;
                } else {
                    bufferedWriter = bw;
                    bufferedWriter.write("<strong>" + message + "</strong><br><br>\n");
                }
                bufferedWriter.write("Trigger: " + triggerText + "<br><br>\n");
                bufferedWriter.write("<a href=\"" + url1 + "\">Open in BRouter-Web</a><br><br>\n");
                bufferedWriter.write("<a href=\"" + url22 + "\">Open in OpenStreetmap</a><br><br>\n");
                bufferedWriter.write("<a href=\"" + url3 + "\">Open in JOSM (via remote control)</a><br><br>\n");
                bufferedWriter.write("Overpass: <a href=\"" + url4a + "\">minus one week</a> &nbsp;&nbsp; <a href=\"" + url4b + "\">node context</a><br><br>\n");
                bufferedWriter.write("<a href=\"" + url5 + "\">Open in Latest-Changes / last week</a><br><br>\n");
                bufferedWriter.write("<a href=\"" + url6 + "\">Current Sentinel-2 imagary</a><br><br>\n");
                bufferedWriter.write(str11);
                suspects = suspects3;
                if (isFixed(id10, suspects.timestamp)) {
                    challenge3 = filter9;
                    country4 = country8;
                    filter = challenge6;
                    bufferedWriter.write("<br><br><a href=\"/brouter/suspects/" + challenge3 + country4 + str20 + filter + "/watchlist\">back to watchlist</a><br><br>\n");
                    id4 = id10;
                } else {
                    filter = challenge6;
                    challenge3 = filter9;
                    country4 = country8;
                    bufferedWriter.write("<a href=\"/brouter/suspects" + countryId2 + "/falsepositive\">mark false positive (=not an issue)</a><br><br>\n");
                    if (new File("confirmednegatives/" + id10).exists()) {
                        String prefix = "<a href=\"/brouter/suspects" + countryId2 + "/fixed";
                        String prefix2 = " &nbsp;&nbsp;" + prefix;
                        OsmNodeNamed nc = hiddenWps.closest(id10);
                        String proposal = nc == null ? "" : nc.name;
                        id4 = id10;
                        String prefix2d = "<form action=\"/brouter/suspects" + countryId2 + "/fixed\" method=\"get\">hide for days: &nbsp;&nbsp;<input type=\"text\" name=\"ndays\" value=\"" + proposal + "\" autofocus><button type=\"submit\">OK</button></form>";
                        bufferedWriter.write(prefix + "\">mark as fixed</a><br><br>\n");
                        bufferedWriter.write("hide for:  weeks:");
                        bufferedWriter.write(prefix2 + "/7\">1w</a>");
                        bufferedWriter.write(prefix2 + "/14\">2w</a>");
                        bufferedWriter.write(prefix2 + "/21\">3w</a>");
                        bufferedWriter.write(" &nbsp;&nbsp;&nbsp; months:");
                        bufferedWriter.write(prefix2 + "/30\">1m</a>");
                        bufferedWriter.write(prefix2 + "/61\">2m</a>");
                        bufferedWriter.write(prefix2 + "/91\">3m</a>");
                        bufferedWriter.write(prefix2 + "/122\">4m</a>");
                        bufferedWriter.write(prefix2 + "/152\">5m</a>");
                        bufferedWriter.write(prefix2 + "/183\">6m</a><br><br>\n");
                        bufferedWriter.write(prefix2d + "<br><br>\n");
                    } else {
                        id4 = id10;
                        bufferedWriter.write("<a href=\"/brouter/suspects" + countryId2 + "/confirm\">mark as a confirmed issue</a><br><br>\n");
                    }
                    if (polygon2 != null) {
                        bufferedWriter.write("<br><br><a href=\"/brouter/suspects" + challenge3 + country4 + str20 + filter + "\">back to issue list</a><br><br>\n");
                    }
                }
                challenge = challenge3;
                country = country4;
            } else {
                id4 = id3;
                suspects = suspects3;
                challenge = filter9;
                bufferedWriter = bufferedWriter2;
                String str21 = "<br>\n";
                filter = challenge6;
                country = country8;
                if (polygon2 == null) {
                    bufferedWriter.write(message + str21);
                } else {
                    bufferedWriter.write(filter + " suspect list for " + country + "\n");
                    String str22 = str2;
                    bufferedWriter.write(str22 + challenge + country + str20 + filter + "/watchlist\">see watchlist</a>\n");
                    bufferedWriter.write(str22 + challenge + "\">back to country list</a><br><br>\n");
                    int maxprio2 = 0;
                    int isuspect3 = 0;
                    while (true) {
                        if (isuspect3 >= suspects.cnt) {
                            break;
                        }
                        String message5 = message;
                        String str23 = str20;
                        long id11 = suspects.ids[isuspect3];
                        int prio = ((suspects.prios[isuspect3] + 1) / 2) * 2;
                        if (prio < maxprio2) {
                            if (maxprio2 == 0) {
                                bufferedWriter.write("current level: " + getLevelDecsription(maxprio2) + "<br><br>\n");
                            }
                        } else {
                            if (polygon2 != null) {
                                message2 = message5;
                                polygon3 = polygon2;
                                if (!polygon3.isInBoundingBox(id11)) {
                                    country2 = country;
                                    str5 = str21;
                                    isuspect = isuspect3;
                                    str6 = str17;
                                    country3 = challenge7;
                                }
                                id5 = id11;
                                challenge2 = challenge;
                                maxprio = maxprio2;
                                str8 = country3;
                                suspects2 = suspects;
                                str7 = str23;
                                str10 = str5;
                                str9 = str6;
                                filter2 = filter;
                                isuspect3 = isuspect + 1;
                                str21 = str10;
                                str20 = str7;
                                polygon2 = polygon3;
                                filter = filter2;
                                message = message2;
                                challenge = challenge2;
                                challenge7 = str8;
                                country = country2;
                                id4 = id5;
                                suspects = suspects2;
                                str17 = str9;
                                maxprio2 = maxprio;
                            } else {
                                message2 = message5;
                                polygon3 = polygon2;
                            }
                            isuspect = isuspect3;
                            str5 = str21;
                            str6 = str17;
                            if (new File("falsepositives/" + id11).exists()) {
                                country2 = country;
                                country3 = challenge7;
                            } else {
                                country2 = country;
                                if (isFixed(id11, suspects.timestamp)) {
                                    country3 = challenge7;
                                } else {
                                    country3 = challenge7;
                                    if ((!country3.equals(filter) || !new File("suspectarchive/" + id11).exists()) && ((!"confirmed".equals(filter) || new File("confirmednegatives/" + id11).exists()) && (polygon3 == null || polygon3.isInArea(id11)))) {
                                        if (maxprio2 == 0) {
                                            maxprio2 = prio;
                                            bufferedWriter.write("current level: " + getLevelDecsription(maxprio2) + "<br><br>\n");
                                        }
                                        str7 = str23;
                                        String countryId3 = challenge + country2 + str7 + filter + str7 + id11;
                                        filter2 = filter;
                                        challenge2 = challenge;
                                        File confirmedEntry2 = new File("confirmednegatives/" + id11);
                                        String hint2 = "";
                                        if (confirmedEntry2.exists()) {
                                            String hint3 = formatAge(confirmedEntry2);
                                            maxprio = maxprio2;
                                            hint2 = "&nbsp;&nbsp;&nbsp;confirmed " + hint3 + " ago";
                                        } else {
                                            maxprio = maxprio2;
                                        }
                                        int ilon4 = (int) (id11 >> 32);
                                        suspects2 = suspects;
                                        country2 = country2;
                                        int ilat3 = (int) (id11 & (-1));
                                        id5 = id11;
                                        str8 = country3;
                                        String url23 = str4 + countryId3;
                                        StringBuilder sbAppend2 = new StringBuilder().append("<a href=\"").append(url23).append("\">").append(((double) (ilon4 - 180000000)) / 1000000.0d);
                                        str9 = str6;
                                        str10 = str5;
                                        bufferedWriter.write(sbAppend2.append(str9).append(((double) (ilat3 - 90000000)) / 1000000.0d).append("</a>").append(hint2).append(str10).toString());
                                    }
                                    isuspect3 = isuspect + 1;
                                    str21 = str10;
                                    str20 = str7;
                                    polygon2 = polygon3;
                                    filter = filter2;
                                    message = message2;
                                    challenge = challenge2;
                                    challenge7 = str8;
                                    country = country2;
                                    id4 = id5;
                                    suspects = suspects2;
                                    str17 = str9;
                                    maxprio2 = maxprio;
                                }
                            }
                            id5 = id11;
                            challenge2 = challenge;
                            maxprio = maxprio2;
                            str8 = country3;
                            suspects2 = suspects;
                            str7 = str23;
                            str10 = str5;
                            str9 = str6;
                            filter2 = filter;
                            isuspect3 = isuspect + 1;
                            str21 = str10;
                            str20 = str7;
                            polygon2 = polygon3;
                            filter = filter2;
                            message = message2;
                            challenge = challenge2;
                            challenge7 = str8;
                            country = country2;
                            id4 = id5;
                            suspects = suspects2;
                            str17 = str9;
                            maxprio2 = maxprio;
                        }
                    }
                    bufferedWriter.write(str3);
                    bw.flush();
                    return;
                }
            }
            bufferedWriter.write(str3);
            bw.flush();
            return;
        }
        bufferedWriter2.write("<table>\n");
        File countryParent = new File("worldpolys" + country7);
        File[] files = countryParent.listFiles();
        Set<String> names = new TreeSet<>();
        int length = files.length;
        int i = 0;
        while (i < length) {
            File f = files[i];
            int i2 = length;
            String name = f.getName();
            File[] files2 = files;
            if (!name.endsWith(".poly")) {
                filter5 = filter6;
            } else {
                filter5 = filter6;
                names.add(name.substring(0, name.length() - 5));
            }
            i++;
            length = i2;
            files = files2;
            filter6 = filter5;
        }
        Iterator<String> it = names.iterator();
        while (it.hasNext()) {
            String c2 = it.next();
            String url24 = str14 + challenge5 + country7 + str + c2;
            Iterator<String> it2 = it;
            Set<String> names2 = names;
            String linkNew = "<td>&nbsp;<a href=\"" + url24 + "/new\">new</a>&nbsp;</td>";
            String str24 = str14;
            String linkCnf = "<td>&nbsp;<a href=\"" + url24 + "/confirmed\">confirmed</a>&nbsp;</td>";
            String str25 = str;
            String linkAll = "<td>&nbsp;<a href=\"" + url24 + "/all\">all</a>&nbsp;</td>";
            if (!new File(countryParent, c2).exists()) {
                linkSub = "";
            } else {
                linkSub = "<td>&nbsp;<a href=\"" + url24 + "\">sub-regions</a>&nbsp;</td>";
            }
            bufferedWriter2.write("<tr><td>" + c2 + "</td>" + linkNew + linkCnf + linkAll + linkSub + "\n");
            it = it2;
            names = names2;
            str14 = str24;
            str = str25;
        }
        bufferedWriter2.write("</table>\n");
        bufferedWriter2.write("</body></html>\n");
        bw.flush();
    }

    private static boolean isFixed(long id, long timestamp) {
        File fixedEntry = new File("fixedsuspects/" + id);
        return fixedEntry.exists() && fixedEntry.lastModified() > timestamp;
    }

    private static final class SuspectList {
        int cnt;
        boolean[] falsePositive;
        long[] ids;
        boolean[] newOrConfirmed;
        int[] prios;
        long timestamp;
        int[] triggers;

        SuspectList(int count, long time) {
            this.cnt = count;
            this.ids = new long[this.cnt];
            this.prios = new int[this.cnt];
            this.triggers = new int[this.cnt];
            this.newOrConfirmed = new boolean[this.cnt];
            this.falsePositive = new boolean[this.cnt];
            this.timestamp = time;
        }

        int trigger4Id(long id) {
            for (int i = 0; i < this.cnt; i++) {
                if (id == this.ids[i]) {
                    return this.triggers[i];
                }
            }
            return 0;
        }
    }

    private static SuspectList getDailySuspectsIfLoaded() throws IOException {
        SuspectList suspectList;
        synchronized (allSuspectsMap) {
            suspectList = allSuspectsMap.get("dailysuspects.txt");
        }
        return suspectList;
    }

    private static SuspectList getAllSuspects(String suspectFileName) throws IOException {
        String str = suspectFileName;
        synchronized (allSuspectsMap) {
            SuspectList allSuspects = allSuspectsMap.get(str);
            File suspectFile = new File(str);
            if (allSuspects != null && suspectFile.lastModified() == allSuspects.timestamp) {
                return allSuspects;
            }
            int[] prioCount = new int[100];
            BufferedReader r = new BufferedReader(new FileReader(suspectFile));
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                StringTokenizer tk2 = new StringTokenizer(line);
                tk2.nextToken();
                int nprio = ((Integer.parseInt(tk2.nextToken()) + 1) / 2) * 2;
                prioCount[nprio] = prioCount[nprio] + 1;
                str = suspectFileName;
            }
            r.close();
            int pointer = 0;
            for (int i = 99; i >= 0; i--) {
                int cnt = prioCount[i];
                prioCount[i] = pointer;
                pointer += cnt;
            }
            SuspectList allSuspects2 = new SuspectList(pointer, suspectFile.lastModified());
            BufferedReader r2 = new BufferedReader(new FileReader(suspectFile));
            while (true) {
                String line2 = r2.readLine();
                if (line2 != null) {
                    StringTokenizer tk22 = new StringTokenizer(line2);
                    long id = Long.parseLong(tk22.nextToken());
                    int prio = Integer.parseInt(tk22.nextToken());
                    int nprio2 = ((prio + 1) / 2) * 2;
                    int pointer2 = prioCount[nprio2];
                    prioCount[nprio2] = pointer2 + 1;
                    allSuspects2.ids[pointer2] = id;
                    allSuspects2.prios[pointer2] = prio;
                    boolean z = false;
                    allSuspects2.triggers[pointer2] = tk22.hasMoreTokens() ? Integer.parseInt(tk22.nextToken()) : 0;
                    boolean[] zArr = allSuspects2.newOrConfirmed;
                    if (new File("confirmednegatives/" + id).exists() || !new File("suspectarchive/" + id).exists()) {
                        z = true;
                    }
                    zArr[pointer2] = z;
                    allSuspects2.falsePositive[pointer2] = new File("falsepositives/" + id).exists();
                    str = suspectFileName;
                } else {
                    r2.close();
                    allSuspectsMap.put(str, allSuspects2);
                    return allSuspects2;
                }
            }
        }
    }
}

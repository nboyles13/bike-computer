package btools.router;

import btools.mapaccess.MatchedWaypoint;
import btools.util.StringUtils;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class FormatJson extends Formatter {
    public FormatJson(RoutingContext rc) {
        super(rc);
    }

    @Override // btools.router.Formatter
    public String format(OsmTrack t) {
        String type;
        int turnInstructionMode = t.voiceHints != null ? t.voiceHints.turnInstructionMode : 0;
        StringBuilder sb = new StringBuilder(8192);
        sb.append("{\n");
        sb.append("  \"type\": \"FeatureCollection\",\n");
        sb.append("  \"features\": [\n");
        sb.append("    {\n");
        sb.append("      \"type\": \"Feature\",\n");
        sb.append("      \"properties\": {\n");
        sb.append("        \"creator\": \"BRouter-" + OsmTrack.version + "\",\n");
        sb.append("        \"name\": \"").append(t.name).append("\",\n");
        sb.append("        \"track-length\": \"").append(t.distance).append("\",\n");
        sb.append("        \"filtered ascend\": \"").append(t.ascend).append("\",\n");
        sb.append("        \"plain-ascend\": \"").append(t.plainAscend).append("\",\n");
        sb.append("        \"total-time\": \"").append(t.getTotalSeconds()).append("\",\n");
        sb.append("        \"total-energy\": \"").append(t.energy).append("\",\n");
        sb.append("        \"cost\": \"").append(t.cost).append("\",\n");
        if (t.voiceHints != null && !t.voiceHints.list.isEmpty()) {
            sb.append("        \"voicehints\": [\n");
            for (VoiceHint hint : t.voiceHints.list) {
                sb.append("          [");
                sb.append(hint.indexInTrack);
                sb.append(',').append(hint.getJsonCommandIndex(turnInstructionMode));
                sb.append(',').append(hint.getExitNumber());
                sb.append(',').append(hint.distanceToNext);
                sb.append(',').append((int) hint.angle);
                if (turnInstructionMode == 4 || turnInstructionMode == 9) {
                    sb.append(",\"").append(hint.formatGeometry()).append("\"");
                }
                sb.append("],\n");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("        ],\n");
        }
        if (t.showSpeedProfile) {
            List<String> sp = t.aggregateSpeedProfile();
            if (sp.size() > 0) {
                sb.append("        \"speedprofile\": [\n");
                int i = sp.size() - 1;
                while (i >= 0) {
                    sb.append("          [").append(sp.get(i)).append(i > 0 ? "],\n" : "]\n");
                    i--;
                }
                sb.append("        ],\n");
            }
        }
        sb.append("        \"messages\": [\n");
        sb.append("          [\"").append("Longitude\tLatitude\tElevation\tDistance\tCostPerKm\tElevCost\tTurnCost\tNodeCost\tInitialCost\tWayTags\tNodeTags\tTime\tEnergy".replaceAll("\t", "\", \"")).append("\"],\n");
        for (String m : t.aggregateMessages()) {
            sb.append("          [\"").append(m.replaceAll("\t", "\", \"")).append("\"],\n");
            turnInstructionMode = turnInstructionMode;
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("        ],\n");
        if (t.getTotalSeconds() > 0) {
            sb.append("        \"times\": [");
            DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);
            decimalFormat.applyPattern("0.###");
            Iterator<OsmPathElement> it = t.nodes.iterator();
            while (it.hasNext()) {
                sb.append(decimalFormat.format(it.next().getTime())).append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("]\n");
        } else {
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        sb.append("      },\n");
        if (t.iternity != null) {
            sb.append("      \"iternity\": [\n");
            for (String s : t.iternity) {
                sb.append("        \"").append(s).append("\",\n");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
            sb.append("        ],\n");
        }
        sb.append("      \"geometry\": {\n");
        sb.append("        \"type\": \"LineString\",\n");
        sb.append("        \"coordinates\": [\n");
        OsmPathElement nn = null;
        for (OsmPathElement n : t.nodes) {
            String sele = n.getSElev() == Short.MIN_VALUE ? "" : ", " + n.getElev();
            if (t.showspeed) {
                double speed = 0.0d;
                if (nn != null) {
                    int dist = n.calcDistance(nn);
                    float dt = n.getTime() - nn.getTime();
                    if (dt != 0.0f) {
                        speed = ((double) ((dist * 3.6f) / dt)) + 0.5d;
                    }
                }
                sele = ", " + (((int) (10.0d * speed)) / 10.0f);
            }
            sb.append("          [").append(formatILon(n.getILon())).append(", ").append(formatILat(n.getILat())).append(sele).append("],\n");
            nn = n;
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        sb.append("        ]\n");
        sb.append("      }\n");
        if (t.exportWaypoints || t.exportCorrectedWaypoints || !t.pois.isEmpty()) {
            sb.append("    },\n");
            for (int i2 = 0; i2 <= t.pois.size() - 1; i2++) {
                OsmNodeNamed poi = t.pois.get(i2);
                addFeature(sb, "poi", poi.name, poi.ilat, poi.ilon, poi.getSElev());
                if (i2 < t.pois.size() - 1) {
                    sb.append(",");
                }
                sb.append("    \n");
            }
            if (t.exportWaypoints) {
                if (!t.pois.isEmpty()) {
                    sb.append("    ,\n");
                }
                for (int i3 = 0; i3 <= t.matchedWaypoints.size() - 1; i3++) {
                    MatchedWaypoint wp = t.matchedWaypoints.get(i3);
                    switch (wp.wpttype) {
                        case 2:
                            type = "via";
                            break;
                        case 3:
                            type = "beeline";
                            break;
                        default:
                            type = "shaping";
                            break;
                    }
                    addFeature(sb, type, wp.name, wp.waypoint.ilat, wp.waypoint.ilon, wp.waypoint.getSElev());
                    if (i3 < t.matchedWaypoints.size() - 1) {
                        sb.append(",");
                    }
                    sb.append("    \n");
                }
            }
            if (t.exportCorrectedWaypoints) {
                if (t.exportWaypoints) {
                    sb.append("    ,\n");
                }
                boolean hasCorrPoints = false;
                for (int i4 = 0; i4 <= t.matchedWaypoints.size() - 1; i4++) {
                    MatchedWaypoint wp2 = t.matchedWaypoints.get(i4);
                    if (wp2.correctedpoint != null) {
                        if (hasCorrPoints) {
                            sb.append(",");
                        }
                        addFeature(sb, "via_corr", wp2.name + "_corr", wp2.correctedpoint.ilat, wp2.correctedpoint.ilon, wp2.correctedpoint.getSElev());
                        sb.append("    \n");
                        hasCorrPoints = true;
                    }
                }
            }
        } else {
            sb.append("    }\n");
        }
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private void addFeature(StringBuilder sb, String type, String name, int ilat, int ilon, short selev) {
        String str;
        sb.append("    {\n");
        sb.append("      \"type\": \"Feature\",\n");
        sb.append("      \"properties\": {\n");
        sb.append("        \"name\": \"" + StringUtils.escapeJson(name) + "\",\n");
        sb.append("        \"type\": \"" + type + "\"\n");
        sb.append("      },\n");
        sb.append("      \"geometry\": {\n");
        sb.append("        \"type\": \"Point\",\n");
        sb.append("        \"coordinates\": [\n");
        sb.append("          " + formatILon(ilon) + ",\n");
        String iLat = formatILat(ilat);
        if (selev != Short.MIN_VALUE) {
            str = ",\n          " + (((double) selev) / 4.0d);
        } else {
            str = "";
        }
        sb.append("          " + iLat + str + "\n");
        sb.append("        ]\n");
        sb.append("      }\n");
        sb.append("    }");
    }

    public String formatAsWaypoint(OsmNodeNamed n) {
        try {
            StringWriter sw = new StringWriter(8192);
            BufferedWriter bw = new BufferedWriter(sw);
            addJsonHeader(bw);
            addJsonFeature(bw, "info", "wpinfo", n.ilon, n.ilat, n.getElev(), n.nodeDescription != null ? this.rc.expctxWay.getKeyValueDescription(false, n.nodeDescription) : null);
            addJsonFooter(bw);
            bw.close();
            sw.close();
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addJsonFeature(BufferedWriter sb, String type, String name, int ilon, int ilat, double elev, String desc) {
        try {
            sb.append("    {\n");
            sb.append("      \"type\": \"Feature\",\n");
            sb.append("      \"properties\": {\n");
            sb.append("        \"creator\": \"BRouter-1.7.9\",\n");
            sb.append((CharSequence) ("        \"name\": \"" + StringUtils.escapeJson(name) + "\",\n"));
            sb.append((CharSequence) ("        \"type\": \"" + type + "\""));
            if (desc != null) {
                sb.append((CharSequence) (",\n        \"message\": \"" + desc + "\"\n"));
            } else {
                sb.append("\n");
            }
            sb.append("      },\n");
            sb.append("      \"geometry\": {\n");
            sb.append("        \"type\": \"Point\",\n");
            sb.append("        \"coordinates\": [\n");
            sb.append((CharSequence) ("          " + formatILon(ilon) + ",\n"));
            sb.append((CharSequence) ("          " + formatILat(ilat) + ",\n"));
            sb.append((CharSequence) ("          " + elev + "\n"));
            sb.append("        ]\n");
            sb.append("      }\n");
            sb.append("    }\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addJsonHeader(BufferedWriter sb) {
        try {
            sb.append("{\n");
            sb.append("  \"type\": \"FeatureCollection\",\n");
            sb.append("  \"features\": [\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addJsonFooter(BufferedWriter sb) {
        try {
            sb.append("  ]\n");
            sb.append("}\n");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

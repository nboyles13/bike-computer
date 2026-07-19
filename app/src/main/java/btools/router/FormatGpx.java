package btools.router;

import btools.mapaccess.MatchedWaypoint;
import btools.util.StringUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class FormatGpx extends Formatter {
    public FormatGpx(RoutingContext rc) {
        super(rc);
    }

    @Override // btools.router.Formatter
    public String format(OsmTrack t) {
        try {
            StringWriter sw = new StringWriter(8192);
            BufferedWriter bw = new BufferedWriter(sw);
            formatAsGpx(bw, t);
            bw.close();
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String formatAsGpx(BufferedWriter sb, OsmTrack t) throws IOException {
        CharSequence charSequence;
        CharSequence charSequence2;
        CharSequence charSequence3;
        CharSequence charSequence4;
        CharSequence charSequence5;
        String str;
        String str2;
        String str3;
        String str4;
        int idx;
        String str5;
        String str6;
        String str7;
        int idx2;
        String lastway;
        String str8;
        String str9;
        boolean bNeedHeader;
        String sele;
        boolean bNeedHeader2;
        double speed;
        double speed2;
        String sele2;
        double speed3;
        double speed4;
        CharSequence charSequence6;
        String str10;
        String str11;
        BufferedWriter bufferedWriter = sb;
        int turnInstructionMode = t.voiceHints != null ? t.voiceHints.turnInstructionMode : 0;
        bufferedWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (turnInstructionMode != 9) {
            for (int i = t.messageList.size() - 1; i >= 0; i--) {
                String message = t.messageList.get(i);
                if (i < t.messageList.size() - 1) {
                    message = "(alt-index " + i + ": " + message + " )";
                }
                if (message != null) {
                    bufferedWriter.append("<!-- ").append((CharSequence) message).append((CharSequence) " -->\n");
                }
            }
        }
        if (turnInstructionMode == 4) {
            bufferedWriter.append("<!-- $transport-mode$").append((CharSequence) t.voiceHints.getTransportMode()).append("$ -->\n");
            bufferedWriter.append("<!--          cmd    idx        lon        lat d2next  geometry -->\n");
            bufferedWriter.append("<!-- $turn-instruction-start$\n");
            for (VoiceHint hint : t.voiceHints.list) {
                bufferedWriter.append((CharSequence) String.format("     $turn$%6s;%6d;%10s;%10s;%6d;%s$\n", hint.getCommandString(turnInstructionMode), Integer.valueOf(hint.indexInTrack), formatILon(hint.ilon), formatILat(hint.ilat), Integer.valueOf((int) hint.distanceToNext), hint.formatGeometry()));
            }
            bufferedWriter.append("    $turn-instruction-end$ -->\n");
        }
        bufferedWriter.append("<gpx \n");
        bufferedWriter.append(" xmlns=\"http://www.topografix.com/GPX/1/1\" \n");
        bufferedWriter.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n");
        if (turnInstructionMode == 9) {
            bufferedWriter.append(" xmlns:brouter=\"Not yet documented\" \n");
        }
        if (turnInstructionMode == 7) {
            bufferedWriter.append(" xmlns:locus=\"http://www.locusmap.eu\" \n");
        }
        bufferedWriter.append(" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\" \n");
        if (turnInstructionMode == 3) {
            bufferedWriter.append(" creator=\"OsmAndRouter\" version=\"1.1\">\n");
        } else {
            bufferedWriter.append((CharSequence) (" creator=\"BRouter-" + OsmTrack.version + "\" version=\"1.1\">\n"));
        }
        if (turnInstructionMode == 9) {
            bufferedWriter.append(" <metadata>\n");
            bufferedWriter.append("  <name>").append((CharSequence) t.name).append("</name>\n");
            bufferedWriter.append("  <extensions>\n");
            bufferedWriter.append("   <brouter:info>").append((CharSequence) t.messageList.get(0)).append("</brouter:info>\n");
            if (t.params != null && t.params.size() > 0) {
                bufferedWriter.append("   <brouter:params><![CDATA[");
                int i2 = 0;
                for (Map.Entry<String, String> e : t.params.entrySet()) {
                    int i3 = i2 + 1;
                    if (i2 != 0) {
                        bufferedWriter.append("&");
                    }
                    bufferedWriter.append((CharSequence) e.getKey()).append("=").append((CharSequence) e.getValue());
                    i2 = i3;
                }
                bufferedWriter.append("]]></brouter:params>\n");
            }
            bufferedWriter.append("  </extensions>\n");
            bufferedWriter.append(" </metadata>\n");
        }
        if (turnInstructionMode == 3 || turnInstructionMode == 8) {
            float lastRteTime = 0.0f;
            bufferedWriter.append(" <rte>\n");
            float rteTime = t.getVoiceHintTime(0);
            StringBuffer first = new StringBuffer();
            first.append("  <rtept lat=\"").append(formatILat(t.nodes.get(0).getILat())).append("\" lon=\"").append(formatILon(t.nodes.get(0).getILon())).append("\">\n").append("   <desc>start</desc>\n   <extensions>\n");
            if (rteTime != 0.0f) {
                charSequence = "  <rtept lat=\"";
                double ti = rteTime - 0.0f;
                charSequence2 = "\" lon=\"";
                first.append("    <time>").append(new StringBuilder().append((int) (ti + 0.5d)).toString()).append("</time>\n");
                lastRteTime = rteTime;
            } else {
                charSequence = "  <rtept lat=\"";
                charSequence2 = "\" lon=\"";
            }
            first.append("    <offset>0</offset>\n  </extensions>\n </rtept>\n");
            if (turnInstructionMode != 8 || t.matchedWaypoints.get(0).wpttype != 3 || t.voiceHints.list.get(0).indexInTrack != 0) {
                bufferedWriter.append((CharSequence) first.toString());
            }
            int i4 = 0;
            while (i4 < t.voiceHints.list.size()) {
                VoiceHint hint2 = t.voiceHints.list.get(i4);
                CharSequence charSequence7 = charSequence;
                CharSequence charSequence8 = charSequence2;
                bufferedWriter.append(charSequence7).append((CharSequence) formatILat(hint2.ilat)).append(charSequence8).append((CharSequence) formatILon(hint2.ilon)).append("\">\n").append("   <desc>").append((CharSequence) (turnInstructionMode == 3 ? hint2.getMessageString(turnInstructionMode) : hint2.getCruiserMessageString())).append("</desc>\n   <extensions>\n");
                float rteTime2 = t.getVoiceHintTime(i4 + 1);
                if (rteTime2 == lastRteTime) {
                    charSequence4 = charSequence8;
                } else {
                    double ti2 = rteTime2 - lastRteTime;
                    charSequence4 = charSequence8;
                    bufferedWriter.append("    <time>").append((CharSequence) new StringBuilder().append((int) (ti2 + 0.5d)).toString()).append("</time>\n");
                    lastRteTime = rteTime2;
                }
                bufferedWriter.append("    <turn>").append((CharSequence) (turnInstructionMode == 3 ? hint2.getCommandString(turnInstructionMode) : hint2.getCruiserCommandString())).append("</turn>\n    <turn-angle>").append((CharSequence) new StringBuilder().append((int) hint2.angle).toString()).append("</turn-angle>\n    <offset>").append((CharSequence) new StringBuilder().append(hint2.indexInTrack).toString()).append("</offset>\n  </extensions>\n </rtept>\n");
                i4++;
                charSequence = charSequence7;
                charSequence2 = charSequence4;
            }
            charSequence3 = charSequence2;
            bufferedWriter.append(charSequence).append((CharSequence) formatILat(t.nodes.get(t.nodes.size() - 1).getILat())).append(charSequence3).append((CharSequence) formatILon(t.nodes.get(t.nodes.size() - 1).getILon())).append("\">\n").append("   <desc>destination</desc>\n   <extensions>\n");
            bufferedWriter.append("    <time>0</time>\n");
            bufferedWriter.append("    <offset>").append((CharSequence) new StringBuilder().append(t.nodes.size() - 1).toString()).append("</offset>\n  </extensions>\n </rtept>\n");
            bufferedWriter.append("</rte>\n");
        } else {
            charSequence3 = "\" lon=\"";
        }
        short s = Short.MIN_VALUE;
        String str12 = "\" lat=\"";
        String str13 = "\">";
        String str14 = "</name>";
        if (turnInstructionMode != 7) {
            charSequence5 = charSequence3;
            str = "</name>";
        } else {
            float lastRteTime2 = t.getVoiceHintTime(0);
            int i5 = 0;
            while (i5 < t.voiceHints.list.size()) {
                VoiceHint hint3 = t.voiceHints.list.get(i5);
                Writer writerAppend = bufferedWriter.append(" <wpt lon=\"").append((CharSequence) formatILon(hint3.ilon)).append("\" lat=\"").append((CharSequence) formatILat(hint3.ilat)).append("\">");
                if (hint3.selev == s) {
                    str10 = "";
                    charSequence6 = charSequence3;
                } else {
                    charSequence6 = charSequence3;
                    str10 = "<ele>" + (((double) hint3.selev) / 4.0d) + "</ele>";
                }
                writerAppend.append((CharSequence) str10).append("<name>").append((CharSequence) hint3.getMessageString(turnInstructionMode)).append((CharSequence) str14).append("<extensions><locus:rteDistance>").append((CharSequence) new StringBuilder().append(hint3.distanceToNext).toString()).append("</locus:rteDistance>");
                float rteTime3 = t.getVoiceHintTime(i5 + 1);
                if (rteTime3 == lastRteTime2) {
                    str11 = str14;
                } else {
                    double ti3 = rteTime3 - lastRteTime2;
                    str11 = str14;
                    double speed5 = hint3.distanceToNext / ti3;
                    bufferedWriter.append("<locus:rteTime>").append((CharSequence) new StringBuilder().append(ti3).toString()).append("</locus:rteTime>").append("<locus:rteSpeed>").append((CharSequence) new StringBuilder().append(speed5).toString()).append("</locus:rteSpeed>");
                    lastRteTime2 = rteTime3;
                }
                bufferedWriter.append("<locus:rtePointAction>").append((CharSequence) new StringBuilder().append(hint3.getLocusAction()).toString()).append("</locus:rtePointAction></extensions>").append("</wpt>\n");
                i5++;
                s = Short.MIN_VALUE;
                str14 = str11;
                charSequence3 = charSequence6;
            }
            charSequence5 = charSequence3;
            str = str14;
        }
        String str15 = "<sym>";
        if (turnInstructionMode == 5) {
            for (VoiceHint hint4 : t.voiceHints.list) {
                bufferedWriter.append(" <wpt lon=\"").append((CharSequence) formatILon(hint4.ilon)).append("\" lat=\"").append((CharSequence) formatILat(hint4.ilat)).append("\">").append("<name>").append((CharSequence) hint4.getMessageString(turnInstructionMode)).append((CharSequence) str).append((CharSequence) "<sym>").append((CharSequence) hint4.getSymbolString(turnInstructionMode).toLowerCase()).append((CharSequence) "</sym>").append((CharSequence) "<type>").append((CharSequence) hint4.getSymbolString(turnInstructionMode)).append((CharSequence) "</type>").append((CharSequence) "</wpt>\n");
            }
        }
        if (turnInstructionMode == 6) {
            for (VoiceHint hint5 : t.voiceHints.list) {
                CharSequence charSequence9 = charSequence5;
                bufferedWriter.append(" <wpt lat=\"").append((CharSequence) formatILat(hint5.ilat)).append(charSequence9).append((CharSequence) formatILon(hint5.ilon)).append("\">").append((CharSequence) (hint5.selev == Short.MIN_VALUE ? "" : "<ele>" + (((double) hint5.selev) / 4.0d) + "</ele>")).append("<extensions>\n  <om:oruxmapsextensions xmlns:om=\"http://www.oruxmaps.com/oruxmapsextensions/1/0\">\n   <om:ext type=\"ICON\" subtype=\"0\">").append((CharSequence) new StringBuilder().append(hint5.getOruxAction()).toString()).append("</om:ext>\n  </om:oruxmapsextensions>\n  </extensions>\n </wpt>\n");
                charSequence5 = charSequence9;
            }
        }
        for (int i6 = 0; i6 <= t.pois.size() - 1; i6++) {
            OsmNodeNamed poi = t.pois.get(i6);
            formatWaypointGpx(bufferedWriter, poi, "poi");
        }
        if (t.exportWaypoints) {
            for (int i7 = 0; i7 <= t.matchedWaypoints.size() - 1; i7++) {
                MatchedWaypoint wt = t.matchedWaypoints.get(i7);
                if (i7 == 0) {
                    formatWaypointGpx(bufferedWriter, wt, wt.wpttype == 3 ? "beeline" : "via");
                } else if (i7 == t.matchedWaypoints.size() - 1) {
                    formatWaypointGpx(bufferedWriter, wt, "via");
                } else if (wt.wpttype == 3) {
                    formatWaypointGpx(bufferedWriter, wt, "beeline");
                } else if (wt.wpttype == 2) {
                    formatWaypointGpx(bufferedWriter, wt, "via");
                } else {
                    formatWaypointGpx(bufferedWriter, wt, "shaping");
                }
            }
        }
        if (t.exportCorrectedWaypoints) {
            bufferedWriter.append("\n");
            for (int i8 = 0; i8 <= t.matchedWaypoints.size() - 1; i8++) {
                MatchedWaypoint wt2 = t.matchedWaypoints.get(i8);
                if (wt2.correctedpoint != null) {
                    OsmNodeNamed n = new OsmNodeNamed(wt2.correctedpoint);
                    n.name = wt2.name + "_corr";
                    formatWaypointGpx(bufferedWriter, n, "shaping");
                }
            }
            bufferedWriter.append("\n");
        }
        bufferedWriter.append(" <trk>\n");
        if (turnInstructionMode == 9 || turnInstructionMode == 2 || turnInstructionMode == 8 || turnInstructionMode == 4) {
            bufferedWriter.append("  <src>").append((CharSequence) t.name).append("</src>\n");
            bufferedWriter.append("  <type>").append((CharSequence) t.voiceHints.getTransportMode()).append("</type>\n");
        } else {
            bufferedWriter.append("  <name>").append((CharSequence) t.name).append("</name>\n");
        }
        if (turnInstructionMode == 7) {
            bufferedWriter.append("  <extensions>\n");
            bufferedWriter.append("   <locus:rteComputeType>").append((CharSequence) new StringBuilder().append(t.voiceHints.getLocusRouteType()).toString()).append("</locus:rteComputeType>\n");
            bufferedWriter.append("   <locus:rteSimpleRoundabouts>1</locus:rteSimpleRoundabouts>\n");
            bufferedWriter.append("  </extensions>\n");
        }
        bufferedWriter.append("  <trkseg>\n");
        String lastway2 = "";
        boolean bNextDirect = false;
        OsmPathElement osmPathElement = null;
        int idx3 = 0;
        while (idx3 < t.nodes.size()) {
            OsmPathElement osmPathElement2 = t.nodes.get(idx3);
            boolean bNextDirect2 = bNextDirect;
            if (osmPathElement2.getSElev() == Short.MIN_VALUE) {
                str3 = "";
                str2 = str13;
            } else {
                str2 = str13;
                str3 = "<ele>" + osmPathElement2.getElev() + "</ele>";
            }
            String sele3 = str3;
            VoiceHint hint6 = t.getVoiceHint(idx3);
            MatchedWaypoint mwpt = t.getMatchedWaypoint(idx3);
            if (t.showTime) {
                str4 = str12;
                sele3 = sele3 + "<time>" + getFormattedTime3(osmPathElement2.getTime()) + "</time>";
            } else {
                str4 = str12;
            }
            if (turnInstructionMode != 8 || mwpt == null || mwpt.name.startsWith("via") || mwpt.name.startsWith("from") || mwpt.name.startsWith("to")) {
                idx = idx3;
            } else {
                idx = idx3;
                sele3 = sele3 + "<name>" + mwpt.name + str;
            }
            boolean bNeedHeader3 = false;
            if (turnInstructionMode != 9) {
                str5 = str15;
                str6 = str;
                str7 = "to";
                idx2 = idx;
            } else {
                if (hint6 == null) {
                    bNeedHeader = false;
                    str5 = str15;
                    str6 = str;
                    str7 = "to";
                } else {
                    if (mwpt != null) {
                        bNeedHeader = false;
                        if (mwpt.name.startsWith("via") || mwpt.name.startsWith("from") || mwpt.name.startsWith("to")) {
                            str7 = "to";
                        } else {
                            str7 = "to";
                            sele3 = sele3 + "<name>" + mwpt.name + str;
                        }
                    } else {
                        bNeedHeader = false;
                        str7 = "to";
                    }
                    String sele4 = (sele3 + "<desc>" + hint6.getCruiserMessageString() + "</desc>") + str15 + hint6.getCommandString(hint6.cmd, turnInstructionMode) + "</sym>";
                    if (mwpt != null) {
                        if (mwpt.wpttype == 2) {
                            sele4 = sele4 + "<type>via</type>";
                        } else {
                            sele4 = sele4 + "<type>shaping</type>";
                        }
                    }
                    String sele5 = sele4 + "<extensions>";
                    if (t.showspeed) {
                        if (osmPathElement == null) {
                            str5 = str15;
                            speed3 = 0.0d;
                        } else {
                            speed3 = 0.0d;
                            int dist = osmPathElement2.calcDistance(osmPathElement);
                            float dt = osmPathElement2.getTime() - osmPathElement.getTime();
                            if (dt == 0.0f) {
                                str5 = str15;
                            } else {
                                str5 = str15;
                                speed4 = ((double) ((dist * 3.6f) / dt)) + 0.5d;
                                str6 = str;
                                sele5 = sele5 + "<brouter:speed>" + (((int) (speed4 * 10.0d)) / 10.0f) + "</brouter:speed>";
                            }
                        }
                        speed4 = speed3;
                        str6 = str;
                        sele5 = sele5 + "<brouter:speed>" + (((int) (speed4 * 10.0d)) / 10.0f) + "</brouter:speed>";
                    } else {
                        str5 = str15;
                        str6 = str;
                    }
                    String sele6 = sele5 + "<brouter:voicehint>" + hint6.getCommandString(turnInstructionMode) + ";" + ((int) hint6.distanceToNext) + "," + hint6.formatGeometry() + "</brouter:voicehint>";
                    if (osmPathElement2.message != null && osmPathElement2.message.wayKeyValues != null && !osmPathElement2.message.wayKeyValues.equals(lastway2)) {
                        sele6 = sele6 + "<brouter:way>" + osmPathElement2.message.wayKeyValues + "</brouter:way>";
                        lastway2 = osmPathElement2.message.wayKeyValues;
                    }
                    if (osmPathElement2.message != null && osmPathElement2.message.nodeKeyValues != null) {
                        sele6 = sele6 + "<brouter:node>" + osmPathElement2.message.nodeKeyValues + "</brouter:node>";
                    }
                    sele3 = sele6 + "</extensions>";
                }
                if (idx == 0 && hint6 == null) {
                    if (mwpt != null && mwpt.wpttype == 3) {
                        sele2 = sele3 + "<desc>beeline</desc>";
                    } else {
                        sele2 = sele3 + "<desc>start</desc>";
                    }
                    sele3 = sele2 + "<type>via</type>";
                    idx2 = idx;
                } else {
                    idx2 = idx;
                    if (idx2 == t.nodes.size() - 1 && hint6 == null) {
                        sele3 = (sele3 + "<desc>end</desc>") + "<type>via</type>";
                    } else if (mwpt != null && hint6 == null) {
                        String sele7 = mwpt.wpttype == 3 ? sele3 + "<desc>beeline</desc>" : sele3 + "<desc>" + mwpt.name + "</desc>";
                        if (mwpt.wpttype == 2) {
                            sele = sele7 + "<type>via</type>";
                        } else {
                            sele = sele7 + "<type>shaping</type>";
                        }
                        sele3 = sele;
                        bNextDirect2 = false;
                    }
                }
                if (hint6 == null) {
                    bNeedHeader3 = t.showspeed || !((osmPathElement2.message == null || osmPathElement2.message.wayKeyValues == null || osmPathElement2.message.wayKeyValues.equals(lastway2)) && (osmPathElement2.message == null || osmPathElement2.message.nodeKeyValues == null));
                    if (bNeedHeader3) {
                        String sele8 = sele3 + "<extensions>";
                        if (!t.showspeed) {
                            bNeedHeader2 = bNeedHeader3;
                        } else {
                            if (osmPathElement == null) {
                                bNeedHeader2 = bNeedHeader3;
                                speed = 0.0d;
                            } else {
                                int dist2 = osmPathElement2.calcDistance(osmPathElement);
                                float dt2 = osmPathElement2.getTime() - osmPathElement.getTime();
                                if (dt2 == 0.0f) {
                                    bNeedHeader2 = bNeedHeader3;
                                    speed = 0.0d;
                                } else {
                                    bNeedHeader2 = bNeedHeader3;
                                    double speed6 = (dist2 * 3.6f) / dt2;
                                    speed2 = speed6 + 0.5d;
                                    sele8 = sele8 + "<brouter:speed>" + (((int) (speed2 * 10.0d)) / 10.0f) + "</brouter:speed>";
                                }
                            }
                            speed2 = speed;
                            sele8 = sele8 + "<brouter:speed>" + (((int) (speed2 * 10.0d)) / 10.0f) + "</brouter:speed>";
                        }
                        if (osmPathElement2.message != null && osmPathElement2.message.wayKeyValues != null && !osmPathElement2.message.wayKeyValues.equals(lastway2)) {
                            sele8 = sele8 + "<brouter:way>" + osmPathElement2.message.wayKeyValues + "</brouter:way>";
                            lastway2 = osmPathElement2.message.wayKeyValues;
                        }
                        if (osmPathElement2.message != null && osmPathElement2.message.nodeKeyValues != null) {
                            sele8 = sele8 + "<brouter:node>" + osmPathElement2.message.nodeKeyValues + "</brouter:node>";
                        }
                        sele3 = sele8 + "</extensions>";
                        bNeedHeader3 = bNeedHeader2;
                    }
                } else {
                    bNeedHeader3 = bNeedHeader;
                }
            }
            if (turnInstructionMode != 2) {
                lastway = lastway2;
                str8 = str6;
                str9 = str5;
            } else if (hint6 != null) {
                if (mwpt != null) {
                    if (mwpt.name.startsWith("via") || mwpt.name.startsWith("from") || mwpt.name.startsWith(str7) || mwpt.name.startsWith("rt")) {
                        str8 = str6;
                    } else {
                        str8 = str6;
                        sele3 = sele3 + "<name>" + mwpt.name + str8;
                    }
                    if (mwpt.wpttype == 3 && bNextDirect2) {
                        sele3 = sele3 + "<src>" + hint6.getLocusSymbolString() + "</src><sym>pass_place</sym><type>Shaping</type>";
                        lastway = lastway2;
                        str9 = str5;
                    } else if (mwpt.wpttype == 3) {
                        if (idx2 == 0) {
                            sele3 = sele3 + "<sym>pass_place</sym><type>Via</type>";
                        } else {
                            sele3 = sele3 + "<sym>pass_place</sym><type>Shaping</type>";
                        }
                        lastway = lastway2;
                        bNextDirect2 = true;
                        str9 = str5;
                    } else if (bNextDirect2) {
                        sele3 = sele3 + "<src>beeline</src><sym>" + hint6.getLocusSymbolString() + "</sym><type>Shaping</type>";
                        lastway = lastway2;
                        bNextDirect2 = false;
                        str9 = str5;
                    } else {
                        String str16 = str5;
                        sele3 = sele3 + str16 + hint6.getLocusSymbolString() + "</sym><type>Via</type>";
                        lastway = lastway2;
                        str9 = str16;
                    }
                } else {
                    str8 = str6;
                    String str17 = str5;
                    sele3 = sele3 + str17 + hint6.getLocusSymbolString() + "</sym>";
                    lastway = lastway2;
                    str9 = str17;
                }
            } else {
                String str18 = str7;
                str8 = str6;
                str9 = str5;
                if (idx2 == 0 && hint6 == null) {
                    int pos = sele3.indexOf("<sym");
                    if (pos != -1) {
                        sele3 = sele3.substring(0, pos);
                    }
                    if (mwpt != null && !mwpt.name.startsWith("from")) {
                        sele3 = sele3 + "<name>" + mwpt.name + str8;
                    }
                    if (mwpt != null && mwpt.wpttype == 3) {
                        bNextDirect2 = true;
                    }
                    sele3 = (sele3 + "<sym>pass_place</sym>") + "<type>Via</type>";
                    lastway = lastway2;
                } else if (idx2 == t.nodes.size() - 1 && hint6 == null) {
                    int pos2 = sele3.indexOf("<sym");
                    if (pos2 != -1) {
                        sele3 = sele3.substring(0, pos2);
                    }
                    if (mwpt != null && mwpt.name != null && !mwpt.name.startsWith(str18)) {
                        sele3 = sele3 + "<name>" + mwpt.name + str8;
                    }
                    if (bNextDirect2) {
                        sele3 = sele3 + "<src>beeline</src>";
                    }
                    sele3 = (sele3 + "<sym>pass_place</sym>") + "<type>Via</type>";
                    lastway = lastway2;
                } else if (mwpt == null) {
                    lastway = lastway2;
                } else {
                    if (mwpt.name.startsWith("via") || mwpt.name.startsWith("from") || mwpt.name.startsWith(str18)) {
                        lastway = lastway2;
                    } else {
                        lastway = lastway2;
                        if (!mwpt.name.startsWith("rt")) {
                            sele3 = sele3 + "<name>" + mwpt.name + str8;
                        }
                    }
                    if (mwpt.wpttype == 3 && bNextDirect2) {
                        sele3 = sele3 + "<src>beeline</src><sym>pass_place</sym><type>Shaping</type>";
                    } else if (mwpt.wpttype == 3) {
                        if (idx2 == 0) {
                            sele3 = sele3 + "<sym>pass_place</sym><type>Via</type>";
                        } else {
                            sele3 = sele3 + "<sym>pass_place</sym><type>Shaping</type>";
                        }
                        bNextDirect2 = true;
                    } else if (bNextDirect2) {
                        sele3 = sele3 + "<src>beeline</src><sym>pass_place</sym><type>Shaping</type>";
                        bNextDirect2 = false;
                    } else if (mwpt.name.startsWith("via") || mwpt.name.startsWith("from") || mwpt.name.startsWith(str18) || mwpt.name.startsWith("rt")) {
                        if (bNextDirect2) {
                            sele3 = sele3 + "<src>beeline</src><sym>pass_place</sym><type>Shaping</type>";
                        } else {
                            sele3 = sele3 + "<sym>pass_place</sym><type>Shaping</type>";
                        }
                        bNextDirect2 = false;
                    } else {
                        sele3 = (sele3 + "<name>" + mwpt.name + str8) + "<sym>pass_place</sym><type>Via</type>";
                    }
                }
            }
            String str19 = str4;
            String str20 = str2;
            sb.append("   <trkpt lon=\"").append((CharSequence) formatILon(osmPathElement2.getILon())).append((CharSequence) str19).append((CharSequence) formatILat(osmPathElement2.getILat())).append((CharSequence) str20).append((CharSequence) sele3).append((CharSequence) "</trkpt>\n");
            int i9 = idx2 + 1;
            str15 = str9;
            str13 = str20;
            bNextDirect = bNextDirect2;
            osmPathElement = osmPathElement2;
            str12 = str19;
            str = str8;
            lastway2 = lastway;
            idx3 = i9;
            bufferedWriter = sb;
        }
        BufferedWriter bufferedWriter2 = bufferedWriter;
        bufferedWriter2.append("  </trkseg>\n");
        bufferedWriter2.append(" </trk>\n");
        bufferedWriter2.append("</gpx>\n");
        return sb.toString();
    }

    public String formatAsWaypoint(OsmNodeNamed n) {
        try {
            StringWriter sw = new StringWriter(8192);
            BufferedWriter bw = new BufferedWriter(sw);
            formatGpxHeader(bw);
            formatWaypointGpx(bw, n, (String) null);
            formatGpxFooter(bw);
            bw.close();
            sw.close();
            return sw.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void formatGpxHeader(BufferedWriter sb) throws IOException {
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<gpx \n");
        sb.append(" xmlns=\"http://www.topografix.com/GPX/1/1\" \n");
        sb.append(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n");
        sb.append(" xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\" \n");
        sb.append(" creator=\"BRouter-1.7.9\" version=\"1.1\">\n");
    }

    public void formatGpxFooter(BufferedWriter sb) throws IOException {
        sb.append("</gpx>\n");
    }

    public void formatWaypointGpx(BufferedWriter sb, OsmNodeNamed n, String type) throws IOException {
        sb.append(" <wpt lon=\"").append((CharSequence) formatILon(n.ilon)).append("\" lat=\"").append((CharSequence) formatILat(n.ilat)).append("\">");
        if (n.getSElev() != Short.MIN_VALUE) {
            sb.append("<ele>").append((CharSequence) new StringBuilder().append(n.getElev()).toString()).append("</ele>");
        }
        if (n.name != null) {
            sb.append("<name>").append((CharSequence) StringUtils.escapeXml10(n.name)).append("</name>");
        }
        if (n.nodeDescription != null && this.rc != null) {
            sb.append("<desc>").append((CharSequence) this.rc.expctxWay.getKeyValueDescription(false, n.nodeDescription)).append("</desc>");
        }
        if (type != null) {
            sb.append("<type>").append((CharSequence) type).append("</type>");
        }
        sb.append("</wpt>\n");
    }

    public void formatWaypointGpx(BufferedWriter sb, MatchedWaypoint wp, String type) throws IOException {
        sb.append(" <wpt lon=\"").append((CharSequence) formatILon(wp.waypoint.ilon)).append("\" lat=\"").append((CharSequence) formatILat(wp.waypoint.ilat)).append("\">");
        if (wp.waypoint.getSElev() != Short.MIN_VALUE) {
            sb.append("<ele>").append((CharSequence) new StringBuilder().append(wp.waypoint.getElev()).toString()).append("</ele>");
        }
        if (wp.name != null) {
            sb.append("<name>").append((CharSequence) StringUtils.escapeXml10(wp.name)).append("</name>");
        }
        if (type != null) {
            sb.append("<type>").append((CharSequence) type).append("</type>");
        }
        sb.append("</wpt>\n");
    }

    public static String getWaypoint(int ilon, int ilat, String name, String desc) {
        return "<wpt lon=\"" + formatILon(ilon) + "\" lat=\"" + formatILat(ilat) + "\"><name>" + name + "</name>" + (desc != null ? "<desc>" + desc + "</desc>" : "") + "</wpt>";
    }

    @Override // btools.router.Formatter
    public OsmTrack read(String filename) throws Exception {
        File f = new File(filename);
        if (!f.exists()) {
            return null;
        }
        OsmTrack track = new OsmTrack();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        while (true) {
            String line = br.readLine();
            if (line != null) {
                int idx0 = line.indexOf("<trkpt ");
                if (idx0 >= 0) {
                    int idx02 = line.indexOf(" lon=\"");
                    int idx03 = idx02 + 6;
                    int idx1 = line.indexOf(34, idx03);
                    int ilon = (int) (((Double.parseDouble(line.substring(idx03, idx1)) + 180.0d) * 1000000.0d) + 0.5d);
                    int idx2 = line.indexOf(" lat=\"");
                    if (idx2 >= 0) {
                        int idx22 = idx2 + 6;
                        int idx3 = line.indexOf(34, idx22);
                        int ilat = (int) (((Double.parseDouble(line.substring(idx22, idx3)) + 90.0d) * 1000000.0d) + 0.5d);
                        track.nodes.add(OsmPathElement.create(ilon, ilat, (short) 0, null));
                    }
                }
            } else {
                br.close();
                return track;
            }
        }
    }
}

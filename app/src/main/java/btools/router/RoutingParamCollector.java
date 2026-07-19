package btools.router;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
public class RoutingParamCollector {
    static final boolean DEBUG = false;

    public List<OsmNodeNamed> getWayPointList(String lonLats) {
        if (lonLats == null) {
            throw new IllegalArgumentException("lonlats parameter not set");
        }
        String[] coords = lonLats.split(";|\\|");
        if (coords.length < 1 || !coords[0].contains(",")) {
            throw new IllegalArgumentException("we need one lat/lon point at least!");
        }
        List<OsmNodeNamed> wplist = new ArrayList<>();
        for (int i = 0; i < coords.length; i++) {
            String[] lonLat = coords[i].split(",");
            if (lonLat.length < 1) {
                throw new IllegalArgumentException("we need one lat/lon point at least!");
            }
            wplist.add(readPosition(lonLat[0], lonLat[1], "via" + i));
            if (lonLat.length > 2) {
                if (lonLat[2].equals("d")) {
                    wplist.get(wplist.size() - 1).wpttype = (byte) 3;
                } else if (lonLat[2].equals("m")) {
                    wplist.get(wplist.size() - 1).wpttype = (byte) 2;
                } else {
                    wplist.get(wplist.size() - 1).name = lonLat[2];
                    wplist.get(wplist.size() - 1).wpttype = (byte) 2;
                }
            }
        }
        if (wplist.get(0).name.startsWith("via")) {
            wplist.get(0).name = "from";
        }
        if (wplist.get(wplist.size() - 1).name.startsWith("via")) {
            wplist.get(wplist.size() - 1).name = "to";
        }
        return wplist;
    }

    public List<OsmNodeNamed> readPositions(double[] lons, double[] lats) {
        List<OsmNodeNamed> wplist = new ArrayList<>();
        if (lats == null || lats.length < 2 || lons == null || lons.length < 2) {
            return wplist;
        }
        for (int i = 0; i < lats.length && i < lons.length; i++) {
            OsmNodeNamed n = new OsmNodeNamed();
            n.name = "via" + i;
            n.ilon = (int) (((lons[i] + 180.0d) * 1000000.0d) + 0.5d);
            n.ilat = (int) (((lats[i] + 90.0d) * 1000000.0d) + 0.5d);
            wplist.add(n);
        }
        if (wplist.get(0).name.startsWith("via")) {
            wplist.get(0).name = "from";
        }
        if (wplist.get(wplist.size() - 1).name.startsWith("via")) {
            wplist.get(wplist.size() - 1).name = "to";
        }
        return wplist;
    }

    private OsmNodeNamed readPosition(String vlon, String vlat, String name) {
        if (vlon == null) {
            throw new IllegalArgumentException("lon " + name + " not found in input");
        }
        if (vlat == null) {
            throw new IllegalArgumentException("lat " + name + " not found in input");
        }
        return readPosition(Double.parseDouble(vlon), Double.parseDouble(vlat), name);
    }

    private OsmNodeNamed readPosition(double lon, double lat, String name) {
        OsmNodeNamed n = new OsmNodeNamed();
        n.name = name;
        n.ilon = (int) (((180.0d + lon) * 1000000.0d) + 0.5d);
        n.ilat = (int) (((90.0d + lat) * 1000000.0d) + 0.5d);
        return n;
    }

    public Map<String, String> getUrlParams(String url) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<>();
        String decoded = URLDecoder.decode(url, "UTF-8");
        StringTokenizer tk = new StringTokenizer(decoded, "?&");
        while (tk.hasMoreTokens()) {
            String t = tk.nextToken();
            StringTokenizer tk2 = new StringTokenizer(t, "=");
            if (tk2.hasMoreTokens()) {
                String key = tk2.nextToken();
                if (tk2.hasMoreTokens()) {
                    String value = tk2.nextToken();
                    params.put(key, value);
                }
            }
        }
        return params;
    }

    public void setParams(RoutingContext rctx, List<OsmNodeNamed> wplist, Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return;
        }
        if (params.containsKey("profile")) {
            rctx.localFunction = params.get("profile");
        }
        if (params.containsKey("nogoLats") && params.get("nogoLats").length() > 0) {
            List<OsmNodeNamed> nogoList = readNogos(params.get("nogoLons"), params.get("nogoLats"), params.get("nogoRadi"));
            if (nogoList != null) {
                RoutingContext.prepareNogoPoints(nogoList);
                if (rctx.nogopoints == null) {
                    rctx.nogopoints = nogoList;
                } else {
                    rctx.nogopoints.addAll(nogoList);
                }
            }
            params.remove("nogoLats");
            params.remove("nogoLons");
            params.remove("nogoRadi");
        }
        if (params.containsKey("nogos")) {
            List<OsmNodeNamed> nogoList2 = readNogoList(params.get("nogos"));
            if (nogoList2 != null) {
                RoutingContext.prepareNogoPoints(nogoList2);
                if (rctx.nogopoints == null) {
                    rctx.nogopoints = nogoList2;
                } else {
                    rctx.nogopoints.addAll(nogoList2);
                }
            }
            params.remove("nogos");
        }
        char c = 0;
        if (params.containsKey("polylines")) {
            List<OsmNodeNamed> result = new ArrayList<>();
            parseNogoPolygons(params.get("polylines"), result, false);
            if (rctx.nogopoints == null) {
                rctx.nogopoints = result;
            } else {
                rctx.nogopoints.addAll(result);
            }
            params.remove("polylines");
        }
        if (params.containsKey("polygons")) {
            List<OsmNodeNamed> result2 = new ArrayList<>();
            parseNogoPolygons(params.get("polygons"), result2, true);
            if (rctx.nogopoints == null) {
                rctx.nogopoints = result2;
            } else {
                rctx.nogopoints.addAll(result2);
            }
            params.remove("polygons");
        }
        for (Map.Entry<String, String> e : params.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            if (key.equals("straight")) {
                try {
                    String[] sa = value.split(",");
                    for (String str : sa) {
                        int v = Integer.parseInt(str);
                        if (wplist.size() > v) {
                            try {
                                wplist.get(v).wpttype = (byte) 3;
                            } catch (Exception e2) {
                                ex = e2;
                                System.err.println("error " + ex.getStackTrace()[c].getLineNumber() + " " + String.valueOf(ex.getStackTrace()[c]) + "\n" + String.valueOf(ex));
                                c = 0;
                            }
                        }
                    }
                } catch (Exception e3) {
                    ex = e3;
                }
            } else if (key.equals("pois")) {
                rctx.poipoints = readPoisList(value);
            } else if (key.equals("heading")) {
                rctx.startDirection = Integer.valueOf(value);
                rctx.forceUseStartDirection = true;
            } else if (key.equals("direction")) {
                rctx.startDirection = Integer.valueOf(value);
            } else if (key.equals("roundTripDistance")) {
                rctx.roundTripDistance = Integer.valueOf(value);
            } else if (key.equals("roundTripDirectionAdd")) {
                rctx.roundTripDirectionAdd = Integer.valueOf(value);
            } else if (key.equals("roundTripPoints")) {
                rctx.roundTripPoints = Integer.valueOf(value);
                if (rctx.roundTripPoints == null || rctx.roundTripPoints.intValue() < 3 || rctx.roundTripPoints.intValue() > 20) {
                    rctx.roundTripPoints = 5;
                }
            } else if (key.equals("allowSamewayback")) {
                rctx.allowSamewayback = Integer.parseInt(value) == 1;
            } else if (key.equals("alternativeidx")) {
                rctx.setAlternativeIdx(Integer.parseInt(value));
            } else if (key.equals("turnInstructionMode")) {
                rctx.turnInstructionMode = Integer.parseInt(value);
            } else if (key.equals("timode")) {
                rctx.turnInstructionMode = Integer.parseInt(value);
            } else if (key.equals("turnInstructionFormat")) {
                if ("osmand".equalsIgnoreCase(value)) {
                    rctx.turnInstructionMode = 3;
                } else if ("locus".equalsIgnoreCase(value)) {
                    rctx.turnInstructionMode = 2;
                }
            } else if (key.equals("exportWaypoints")) {
                rctx.exportWaypoints = Integer.parseInt(value) == 1;
            } else if (key.equals("exportCorrectedWaypoints")) {
                rctx.exportCorrectedWaypoints = Integer.parseInt(value) == 1;
            } else if (key.equals("format")) {
                rctx.outputFormat = value.toLowerCase();
            } else if (key.equals("trackFormat")) {
                rctx.outputFormat = value.toLowerCase();
            } else if (key.startsWith("profile:")) {
                if (rctx.keyValues == null) {
                    rctx.keyValues = new HashMap();
                }
                rctx.keyValues.put(key.substring(8), value);
            }
            c = 0;
        }
    }

    public void setProfileParams(RoutingContext rctx, Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return;
        }
        if (rctx.keyValues == null) {
            rctx.keyValues = new HashMap();
        }
        for (Map.Entry<String, String> e : params.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            rctx.keyValues.put(key, value);
        }
    }

    private void parseNogoPolygons(String polygons, List<OsmNodeNamed> result, boolean closed) {
        if (polygons != null) {
            String[] polygonList = polygons.split("\\|");
            for (String str : polygonList) {
                String[] lonLatList = str.split(",");
                if (lonLatList.length > 1) {
                    OsmNogoPolygon polygon = new OsmNogoPolygon(closed);
                    int j = 0;
                    while (j < ((lonLatList.length / 2) * 2) - 1) {
                        int j2 = j + 1;
                        String slon = lonLatList[j];
                        int j3 = j2 + 1;
                        String slat = lonLatList[j2];
                        int lon = (int) (((Double.parseDouble(slon) + 180.0d) * 1000000.0d) + 0.5d);
                        int lat = (int) (((Double.parseDouble(slat) + 90.0d) * 1000000.0d) + 0.5d);
                        polygon.addVertex(lon, lat);
                        j = j3;
                    }
                    String nogoWeight = "NaN";
                    if (j < lonLatList.length) {
                        nogoWeight = lonLatList[j];
                    }
                    polygon.nogoWeight = Double.parseDouble(nogoWeight);
                    if (polygon.points.size() > 0) {
                        polygon.calcBoundingCircle();
                        result.add(polygon);
                    }
                }
            }
        }
    }

    public List<OsmNodeNamed> readPoisList(String pois) {
        if (pois == null) {
            return null;
        }
        String[] lonLatNameList = pois.split("\\|");
        List<OsmNodeNamed> poisList = new ArrayList<>();
        for (String str : lonLatNameList) {
            String[] lonLatName = str.split(",");
            if (lonLatName.length == 3) {
                OsmNodeNamed n = new OsmNodeNamed();
                n.ilon = (int) (((Double.parseDouble(lonLatName[0]) + 180.0d) * 1000000.0d) + 0.5d);
                n.ilat = (int) (((Double.parseDouble(lonLatName[1]) + 90.0d) * 1000000.0d) + 0.5d);
                n.name = lonLatName[2];
                poisList.add(n);
            }
        }
        return poisList;
    }

    public List<OsmNodeNamed> readNogoList(String nogos) {
        if (nogos == null) {
            return null;
        }
        String[] lonLatRadList = nogos.split("\\|");
        List<OsmNodeNamed> nogoList = new ArrayList<>();
        for (String str : lonLatRadList) {
            String[] lonLatRad = str.split(",");
            String nogoWeight = "NaN";
            if (lonLatRad.length > 3) {
                nogoWeight = lonLatRad[3];
            }
            nogoList.add(readNogo(lonLatRad[0], lonLatRad[1], lonLatRad[2], nogoWeight));
        }
        return nogoList;
    }

    public List<OsmNodeNamed> readNogos(String nogoLons, String nogoLats, String nogoRadi) {
        if (nogoLons == null || nogoLats == null || nogoRadi == null) {
            return null;
        }
        List<OsmNodeNamed> nogoList = new ArrayList<>();
        String[] lons = nogoLons.split(",");
        String[] lats = nogoLats.split(",");
        String[] radi = nogoRadi.split(",");
        for (int i = 0; i < lons.length && i < lats.length && i < radi.length; i++) {
            OsmNodeNamed n = readNogo(lons[i].trim(), lats[i].trim(), radi[i].trim(), "undefined");
            nogoList.add(n);
        }
        return nogoList;
    }

    private OsmNodeNamed readNogo(String lon, String lat, String radius, String nogoWeight) {
        double weight = "undefined".equals(nogoWeight) ? Double.NaN : Double.parseDouble(nogoWeight);
        return readNogo(Double.parseDouble(lon), Double.parseDouble(lat), (int) Double.parseDouble(radius), weight);
    }

    private OsmNodeNamed readNogo(double lon, double lat, int radius, double nogoWeight) {
        OsmNodeNamed n = new OsmNodeNamed();
        n.name = "nogo" + radius;
        n.ilon = (int) (((180.0d + lon) * 1000000.0d) + 0.5d);
        n.ilat = (int) (((90.0d + lat) * 1000000.0d) + 0.5d);
        n.isNogo = true;
        n.nogoWeight = nogoWeight;
        return n;
    }
}

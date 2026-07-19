package btools.server;

import btools.router.OsmNodeNamed;
import btools.router.RoutingContext;
import btools.router.RoutingEngine;
import btools.router.RoutingParamCollector;
import java.io.File;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class BRouter {
    public static void main(String[] args) throws Exception {
        int engineMode;
        String exportName;
        long maxRunningTime;
        String moreParams;
        RoutingEngine re;
        if (args.length == 3 || args.length == 4) {
            try {
                System.setProperty("segmentBaseDir", args[0]);
                System.setProperty("profileBaseDir", args[1]);
                String queryString = URLDecoder.decode(args[2], "ISO-8859-1");
                int lonIdx = queryString.indexOf("lonlats=");
                int sepIdx = queryString.indexOf("&", lonIdx);
                String lonlats = queryString.substring(lonIdx + 8, sepIdx);
                RoutingContext rc = new RoutingContext();
                RoutingParamCollector routingParamCollector = new RoutingParamCollector();
                List<OsmNodeNamed> wplist = routingParamCollector.getWayPointList(lonlats);
                Map<String, String> params = routingParamCollector.getUrlParams(queryString);
                if (!params.containsKey("engineMode")) {
                    engineMode = 0;
                } else {
                    engineMode = Integer.parseInt(params.get("engineMode"));
                }
                routingParamCollector.setParams(rc, wplist, params);
                if (args.length == 4) {
                    exportName = args[3];
                } else {
                    System.out.println("Content-type: text/plain");
                    System.out.println();
                    exportName = null;
                }
                String sMaxRunningTime = System.getProperty("maxRunningTime");
                if (sMaxRunningTime == null) {
                    maxRunningTime = 60000;
                } else {
                    maxRunningTime = Integer.parseInt(sMaxRunningTime) * 1000;
                }
                RoutingEngine re2 = new RoutingEngine(exportName, null, new File(args[0]), wplist, rc, engineMode);
                re2.doRun(maxRunningTime);
                if (re2.getErrorMessage() != null) {
                    System.out.println(re2.getErrorMessage());
                }
            } catch (Throwable e) {
                System.out.println("unexpected exception: " + String.valueOf(e));
            }
            System.exit(0);
        }
        System.out.println("BRouter 1.7.9 / 22042026");
        if (args.length < 5) {
            System.out.println("Find routes in an OSM map");
            System.out.println("usage: java -jar brouter.jar <segmentdir> <profiledir> <engineMode> <profile> <lonlats-list> [parameter-list] [profile-parameter-list] ");
            System.out.println("   or: java -cp %CLASSPATH% btools.server.BRouter <segmentdir>> <profiledir> <engineMode> <profile> <lonlats-list> [parameter-list] [profile-parameter-list]");
            System.out.println("   or: java -jar brouter.jar <segmentdir> <profiledir> <parameter-list> [output-filename]");
            System.exit(0);
        }
        int engineMode2 = 0;
        try {
            engineMode2 = Integer.parseInt(args[2]);
        } catch (NumberFormatException e2) {
        }
        RoutingParamCollector routingParamCollector2 = new RoutingParamCollector();
        List<OsmNodeNamed> wplist2 = routingParamCollector2.getWayPointList(args[4]);
        System.setProperty("segmentBaseDir", args[0]);
        System.setProperty("profileBaseDir", args[1]);
        String profileParams = null;
        if (args.length < 6) {
            moreParams = null;
        } else {
            String moreParams2 = args[5];
            moreParams = moreParams2;
        }
        if (args.length == 7) {
            profileParams = args[6];
        }
        RoutingContext rc2 = new RoutingContext();
        rc2.localFunction = args[3];
        if (moreParams != null) {
            routingParamCollector2.setParams(rc2, wplist2, routingParamCollector2.getUrlParams(moreParams));
        }
        if (profileParams != null) {
            routingParamCollector2.setProfileParams(rc2, routingParamCollector2.getUrlParams(profileParams));
        }
        try {
            if (engineMode2 == 2 || engineMode2 == 3) {
                re = new RoutingEngine("testinfo", null, new File(args[0]), wplist2, rc2, engineMode2);
            } else {
                re = new RoutingEngine("testtrack", null, new File(args[0]), wplist2, rc2, engineMode2);
            }
            re.doRun(0L);
        } catch (Exception e3) {
            System.out.println(e3.getMessage());
        }
    }
}

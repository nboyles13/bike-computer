package btools.server.request;

import btools.router.FormatCsv;
import btools.router.FormatGpx;
import btools.router.FormatJson;
import btools.router.FormatKml;
import btools.router.OsmTrack;
import btools.router.RoutingContext;
import btools.server.ServiceContext;
import java.io.File;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class ServerHandler extends RequestHandler {
    private static boolean useRFCMimeType = Boolean.getBoolean("useRFCMimeType");
    private RoutingContext rc;

    public ServerHandler(ServiceContext serviceContext, Map<String, String> params) {
        super(serviceContext, params);
    }

    @Override // btools.server.request.RequestHandler
    public RoutingContext readRoutingContext() {
        this.rc = new RoutingContext();
        this.rc.memoryclass = 128;
        String profile = this.params.get("profile");
        if (profile.startsWith(ProfileUploadHandler.CUSTOM_PREFIX)) {
            String customProfile = profile.substring(ProfileUploadHandler.CUSTOM_PREFIX.length());
            profile = new File(this.serviceContext.customProfileDir, customProfile).getPath();
        } else if (profile.startsWith(ProfileUploadHandler.SHARED_PREFIX)) {
            String customProfile2 = profile.substring(ProfileUploadHandler.SHARED_PREFIX.length());
            profile = new File(this.serviceContext.sharedProfileDir, customProfile2).getPath();
        }
        this.rc.localFunction = profile;
        return this.rc;
    }

    @Override // btools.server.request.RequestHandler
    public String formatTrack(OsmTrack track) {
        String format = this.params.get("format");
        String trackName = getTrackName();
        if (trackName != null) {
            track.name = trackName;
        }
        String exportWaypointsStr = this.params.get("exportWaypoints");
        if (exportWaypointsStr != null && Integer.parseInt(exportWaypointsStr) != 0) {
            track.exportWaypoints = true;
        }
        String exportWaypointsStr2 = this.params.get("exportCorrectedWaypoints");
        if (exportWaypointsStr2 != null && Integer.parseInt(exportWaypointsStr2) != 0) {
            track.exportCorrectedWaypoints = true;
        }
        if (format == null || "gpx".equals(format)) {
            String result = new FormatGpx(this.rc).format(track);
            return result;
        }
        if ("kml".equals(format)) {
            String result2 = new FormatKml(this.rc).format(track);
            return result2;
        }
        if ("geojson".equals(format)) {
            String result3 = new FormatJson(this.rc).format(track);
            return result3;
        }
        if ("csv".equals(format)) {
            String result4 = new FormatCsv(this.rc).format(track);
            return result4;
        }
        System.out.println("unknown track format '" + format + "', using default");
        String result5 = new FormatGpx(this.rc).format(track);
        return result5;
    }

    @Override // btools.server.request.RequestHandler
    public String getMimeType() {
        String format = this.params.get("format");
        if (format == null) {
            return "text/plain";
        }
        if ("gpx".equals(format)) {
            return "application/gpx+xml";
        }
        if ("kml".equals(format)) {
            return "application/vnd.google-earth.kml+xml";
        }
        if ("geojson".equals(format)) {
            if (useRFCMimeType) {
                return "application/geo+json";
            }
            return "application/vnd.geo+json";
        }
        if (!"csv".equals(format)) {
            return "text/plain";
        }
        return "text/tab-separated-values";
    }

    @Override // btools.server.request.RequestHandler
    public String getFileName() {
        String format = this.params.get("format");
        String trackName = getTrackName();
        if (format == null) {
            return null;
        }
        String fileName = (trackName == null ? "brouter" : trackName) + "." + format;
        return fileName;
    }

    private String getTrackName() {
        if (this.params.get("trackname") == null) {
            return null;
        }
        return this.params.get("trackname").replaceAll("[^a-zA-Z0-9 \\._\\-]+", "");
    }
}

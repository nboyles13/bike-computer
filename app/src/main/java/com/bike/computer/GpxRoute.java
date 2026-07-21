package com.bike.computer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class GpxRoute {
    public static final GpxRoute INSTANCE = new GpxRoute();
    private static final OkHttpClient http = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
    private static final Regex ptRe = new Regex("<(?:trkpt|rtept)\\b([^>]*)>", RegexOption.IGNORE_CASE);
    private static final Regex latRe = new Regex("lat=\"([-0-9.]+)\"", RegexOption.IGNORE_CASE);
    private static final Regex lonRe = new Regex("lon=\"([-0-9.]+)\"", RegexOption.IGNORE_CASE);
    private static final Regex nameRe = new Regex("<name>(.*?)</name>", SetsKt.setOf(new RegexOption[]{RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL}));

    private GpxRoute() {
    }

    public final String download(String url) throws IOException {
        String body;
        Intrinsics.checkNotNullParameter(url, "url");
        String u = StringsKt.startsWith(url, "http", false) ? url : "https://" + url;
        Response responseExecute = http.newCall(new Request.Builder().url(u).header("User-Agent", "BikeComputer").build()).execute();
        try {
            Response r = responseExecute;
            ResponseBody responseBodyBody = r.body();
            if (responseBodyBody == null || (body = responseBodyBody.string()) == null) {
                body = "";
            }
            if (!r.isSuccessful()) {
                throw new RuntimeException("HTTP " + r.code());
            }
            CloseableKt.closeFinally(responseExecute, null);
            return body;
        } finally {
        }
    }

    public final String name(String gpx) {
        List<String> groupValues;
        String str;
        String it;
        Intrinsics.checkNotNullParameter(gpx, "gpx");
        MatchResult matchResultFind$default = nameRe.find(gpx, 0);
        if (matchResultFind$default == null || (groupValues = matchResultFind$default.getGroupValues()) == null || (str = groupValues.get(1)) == null || (it = StringsKt.trim((CharSequence) str).toString()) == null) {
            return null;
        }
        if (it.length() > 0) {
            return it;
        }
        return null;
    }

    public final List<double[]> parse(String gpx) {
        List<String> groupValues;
        String str;
        Double doubleOrNull;
        List<String> groupValues2;
        String str2;
        Double doubleOrNull2;
        Intrinsics.checkNotNullParameter(gpx, "gpx");
        ArrayList pts = new ArrayList();
        for (MatchResult m : kotlin.sequences.SequencesKt.asIterable(ptRe.findAll(gpx, 0))) {
            String a = m.getGroupValues().get(1);
            MatchResult matchResultFind$default = latRe.find(a, 0);
            if (matchResultFind$default != null && (groupValues = matchResultFind$default.getGroupValues()) != null && (str = groupValues.get(1)) != null && (doubleOrNull = StringsKt.toDoubleOrNull(str)) != null) {
                double lat = doubleOrNull.doubleValue();
                MatchResult matchResultFind$default2 = lonRe.find(a, 0);
                if (matchResultFind$default2 != null && (groupValues2 = matchResultFind$default2.getGroupValues()) != null && (str2 = groupValues2.get(1)) != null && (doubleOrNull2 = StringsKt.toDoubleOrNull(str2)) != null) {
                    double lon = doubleOrNull2.doubleValue();
                    pts.add(new double[]{lon, lat});
                }
            }
        }
        return pts;
    }

    public static /* synthetic */ List toWaypoints$default(GpxRoute gpxRoute, List list, double d, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            d = 1200.0d;
        }
        if ((i2 & 4) != 0) {
            i = 24;
        }
        return gpxRoute.toWaypoints(list, d, i);
    }

    public final List<double[]> toWaypoints(List<double[]> points, double spacingM, int maxVias) {
        Intrinsics.checkNotNullParameter(points, "points");
        if (points.size() <= 2) {
            return points;
        }
        double total = length(points);
        double spacing = Math.max(spacingM, total / ((double) maxVias));
        ArrayList out = new ArrayList();
        out.add(CollectionsKt.first((List) points));
        double acc = 0.0d;
        int size = points.size() - 1;
        for (int i = 1; i < size; i++) {
            acc += hav(points.get(i - 1), points.get(i));
            if (acc >= spacing) {
                out.add(points.get(i));
                acc = 0.0d;
            }
        }
        out.add(CollectionsKt.last((List) points));
        return out;
    }

    private final double length(List<double[]> p) {
        double s = 0.0d;
        int size = p.size();
        for (int i = 1; i < size; i++) {
            s += hav(p.get(i - 1), p.get(i));
        }
        return s;
    }

    private final double hav(double[] a, double[] b) {
        double p1 = Math.toRadians(a[1]);
        double p2 = Math.toRadians(b[1]);
        double dp = Math.toRadians(b[1] - a[1]);
        double dl = Math.toRadians(b[0] - a[0]);
        double d = 2;
        double it = Math.sin(dp / d);
        double dCos = Math.cos(p1) * Math.cos(p2);
        double it2 = Math.sin(dl / d);
        double h = (it * it) + (dCos * it2 * it2);
        return d * 6371000.0d * Math.asin(Math.sqrt(h));
    }
}

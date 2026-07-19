package com.bike.computer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
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

/* JADX INFO: compiled from: GpxRoute.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0010\u0013\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u0007J\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000f\u001a\u00020\u0007J\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u000f\u001a\u00020\u0007J.\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\b\b\u0002\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u0018J\u0016\u0010\u0019\u001a\u00020\u00162\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u0018\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u001c\u001a\u00020\u00122\u0006\u0010\u001d\u001a\u00020\u0012H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lcom/bike/computer/GpxRoute;", "", "<init>", "()V", "http", "Lokhttp3/OkHttpClient;", "download", "", "url", "ptRe", "Lkotlin/text/Regex;", "latRe", "lonRe", "nameRe", "name", "gpx", "parse", "", "", "toWaypoints", "points", "spacingM", "", "maxVias", "", "length", "p", "hav", "a", "b", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class GpxRoute {
    public static final GpxRoute INSTANCE = new GpxRoute();
    private static final OkHttpClient http = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
    private static final Regex ptRe = new Regex("<(?:trkpt|rtept)\\b([^>]*)>", RegexOption.IGNORE_CASE);
    private static final Regex latRe = new Regex("lat=\"([-0-9.]+)\"", RegexOption.IGNORE_CASE);
    private static final Regex lonRe = new Regex("lon=\"([-0-9.]+)\"", RegexOption.IGNORE_CASE);
    private static final Regex nameRe = new Regex("<name>(.*?)</name>", (Set<? extends RegexOption>) SetsKt.setOf((Object[]) new RegexOption[]{RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL}));

    private GpxRoute() {
    }

    public final String download(String url) throws IOException {
        String body;
        Intrinsics.checkNotNullParameter(url, "url");
        String u = StringsKt.startsWith$default(url, "http", false, 2, (Object) null) ? url : "https://" + url;
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
        MatchResult matchResultFind$default = Regex.find$default(nameRe, gpx, 0, 2, null);
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
        for (MatchResult m : Regex.findAll$default(ptRe, gpx, 0, 2, null)) {
            String a = m.getGroupValues().get(1);
            MatchResult matchResultFind$default = Regex.find$default(latRe, a, 0, 2, null);
            if (matchResultFind$default != null && (groupValues = matchResultFind$default.getGroupValues()) != null && (str = groupValues.get(1)) != null && (doubleOrNull = StringsKt.toDoubleOrNull(str)) != null) {
                double lat = doubleOrNull.doubleValue();
                MatchResult matchResultFind$default2 = Regex.find$default(lonRe, a, 0, 2, null);
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

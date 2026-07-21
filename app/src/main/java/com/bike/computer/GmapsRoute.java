package com.bike.computer;

import com.bike.computer.Geocoder;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.text.Typography;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class GmapsRoute {
    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0 Safari/537.36";
    public static final GmapsRoute INSTANCE = new GmapsRoute();
    private static final OkHttpClient http = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).build();
    private static final Regex coordRe = new Regex("^(-?\\d{1,3}\\.\\d+),(-?\\d{1,3}\\.\\d+)$");
    private static final Regex atRe = new Regex("@(-?\\d{1,3}\\.\\d+),(-?\\d{1,3}\\.\\d+)");
    private static final Regex placeRe = new Regex("!3d(-?\\d{1,3}\\.\\d+)!4d(-?\\d{1,3}\\.\\d+)");
    private static final Regex dblRe = new Regex("!1d(-?\\d{1,3}\\.\\d+)!2d(-?\\d{1,3}\\.\\d+)");
    private static final Regex fullUrlRe = new Regex("https?://(?:www\\.)?(?:google\\.[a-z.]+|maps\\.google\\.[a-z.]+)/maps[^\"'\\\\ ]+");
    private static final Set<String> ignoreNames = SetsKt.setOf(new String[]{"your location", "my location", "current location", ""});

    private GmapsRoute() {
    }

    /* JADX INFO: compiled from: GmapsRoute.kt */
    public static abstract class Stop {
        public /* synthetic */ Stop(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* JADX INFO: compiled from: GmapsRoute.kt */
        public static final /* data */ class At extends Stop {
            private final double lat;
            private final double lon;

            public static /* synthetic */ At copy$default(At at, double d, double d2, int i, Object obj) {
                if ((i & 1) != 0) {
                    d = at.lat;
                }
                if ((i & 2) != 0) {
                    d2 = at.lon;
                }
                return at.copy(d, d2);
            }



            public final At copy(double lat, double lon) {
                return new At(lat, lon);
            }

            public boolean equals(Object other) {
                if (this == other) {
                    return true;
                }
                if (!(other instanceof At)) {
                    return false;
                }
                At at = (At) other;
                return Double.compare(this.lat, at.lat) == 0 && Double.compare(this.lon, at.lon) == 0;
            }

            public int hashCode() {
                return (Double.hashCode(this.lat) * 31) + Double.hashCode(this.lon);
            }

            public String toString() {
                return "At(lat=" + this.lat + ", lon=" + this.lon + ")";
            }

            public At(double lat, double lon) {
                super(null);
                this.lat = lat;
                this.lon = lon;
            }

            public final double getLat() {
                return this.lat;
            }

            public final double getLon() {
                return this.lon;
            }
        }

        private Stop() {
        }

        /* JADX INFO: compiled from: GmapsRoute.kt */
        public static final /* data */ class Named extends Stop {
            private final String q;

            public static /* synthetic */ Named copy$default(Named named, String str, int i, Object obj) {
                if ((i & 1) != 0) {
                    str = named.q;
                }
                return named.copy(str);
            }


            public final Named copy(String q) {
                Intrinsics.checkNotNullParameter(q, "q");
                return new Named(q);
            }

            public boolean equals(Object other) {
                if (this == other) {
                    return true;
                }
                return (other instanceof Named) && Intrinsics.areEqual(this.q, ((Named) other).q);
            }

            public int hashCode() {
                return this.q.hashCode();
            }

            public String toString() {
                return "Named(q=" + this.q + ")";
            }

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public Named(String q) {
                super(null);
                Intrinsics.checkNotNullParameter(q, "q");
                this.q = q;
            }

            public final String getQ() {
                return this.q;
            }
        }
    }

    public final boolean looksLikeLink(String s) {
        Intrinsics.checkNotNullParameter(s, "s");
        String t = StringsKt.trim((CharSequence) s).toString().toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(t, "toLowerCase(...)");
        return StringsKt.contains((CharSequence) t, (CharSequence) "goo.gl", false) || (StringsKt.contains((CharSequence) t, (CharSequence) "google.", false) && StringsKt.contains((CharSequence) t, (CharSequence) "/maps", false)) || StringsKt.contains((CharSequence) t, (CharSequence) "maps.app", false);
    }

    public final List<Stop> resolve(String link) {
        Intrinsics.checkNotNullParameter(link, "link");
        return parse(expand(StringsKt.trim((CharSequence) link).toString()));
    }

    public final List<double[]> toPoints(List<? extends Stop> stops) {
        double[] dArr;
        Intrinsics.checkNotNullParameter(stops, "stops");
        Iterator it = stops.iterator();
        do {
            dArr = null;
            if (!it.hasNext()) {
                break;
            }
            Stop it2 = (Stop) it.next();
            Stop.At at = it2 instanceof Stop.At ? (Stop.At) it2 : null;
            if (at != null) {
                Stop.At a = at;
                dArr = new double[]{a.getLat(), a.getLon()};
            }
        } while (dArr == null);
        double[] near = dArr;
        ArrayList out = new ArrayList();
        for (Stop s : stops) {
            if (s instanceof Stop.At) {
                out.add(new double[]{((Stop.At) s).getLon(), ((Stop.At) s).getLat()});
            } else {
                if (!(s instanceof Stop.Named)) {
                    throw new NoWhenBranchMatchedException();
                }
                Geocoder.Place it3;
                try {
                    it3 = (Geocoder.Place) CollectionsKt.firstOrNull((List) Geocoder.INSTANCE.search(((Stop.Named) s).getQ(), near));
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
                if (it3 != null) {
                    out.add(new double[]{it3.getLon(), it3.getLat()});
                }
            }
        }
        return out;
    }

    public final List<double[]> points(String link) {
        Intrinsics.checkNotNullParameter(link, "link");
        return toPoints(resolve(link));
    }

    private final String expand(String url0) {
        Object objM118constructorimpl;
        Object objM118constructorimpl2;
        String value;
        String strReplace$default;
        String strString;
        String body = "";
        String str = StringsKt.startsWith(url0, "http", false) ? url0 : "https://" + url0;
        try {
            String host = new URI(str).getHost();
            if (host == null) {
                host = "";
            }
            objM118constructorimpl = host;
        } catch (Throwable th) {
            objM118constructorimpl = "";
        }
        String host2 = (String) objM118constructorimpl;
        if (!StringsKt.contains((CharSequence) host2, (CharSequence) "goo.gl", false)) {
            return str;
        }
        try {
            Response responseExecute = http.newCall(new Request.Builder().url(str).header("User-Agent", UA).build()).execute();
            try {
                Response r = responseExecute;
                String finalUrl = r.request().url().toString();
                if (StringsKt.contains((CharSequence) finalUrl, (CharSequence) "goo.gl", false)) {
                    ResponseBody responseBodyBody = r.body();
                    if (responseBodyBody != null && (strString = responseBodyBody.string()) != null) {
                        body = strString;
                    }
                    MatchResult matchResultFind$default = fullUrlRe.find(body, 0);
                    if (matchResultFind$default != null && (value = matchResultFind$default.getValue()) != null && (strReplace$default = StringsKt.replace(value, "\\u003d", "=", false)) != null) {
                        String strReplace$default2 = StringsKt.replace(strReplace$default, "\\u0026", "&", false);
                        if (strReplace$default2 != null) {
                            finalUrl = strReplace$default2;
                        }
                    }
                }
                CloseableKt.closeFinally(responseExecute, null);
                objM118constructorimpl2 = finalUrl;
            } finally {
            }
        } catch (Throwable th2) {
            objM118constructorimpl2 = str;
        }
        return (String) objM118constructorimpl2;
    }

    private final List<Stop> parse(String url) {
        Iterable iterableSplit$default;
        Stop s;
        String it = StringsKt.substringAfter(url, '?', "");
        Map<String, String> query = INSTANCE.parseQuery(it);
        String origin = query.get("origin");
        if (origin == null) {
            origin = query.get("saddr");
        }
        String destination = query.get("destination");
        if (destination == null) {
            destination = query.get("daddr");
        }
        if (origin != null || destination != null) {
            ArrayList out = new ArrayList();
            if (origin != null) {
                String it2 = origin;
                Stop p0 = INSTANCE.stop(it2);
                if (p0 != null) {
                    out.add(p0);
                }
            }
            String str = query.get("waypoints");
            if (str == null) {
                str = query.get("via");
            }
            if (str != null && (iterableSplit$default = StringsKt.split((CharSequence) str, new char[]{'|', '\n'}, false, 0)) != null) {
                Iterable $this$forEach$iv = iterableSplit$default;
                for (Object element$iv : $this$forEach$iv) {
                    String w = (String) element$iv;
                    Stop p02 = INSTANCE.stop(w);
                    if (p02 != null) {
                        out.add(p02);
                    }
                }
            }
            if (destination != null) {
                String it3 = destination;
                Stop p03 = INSTANCE.stop(it3);
                if (p03 != null) {
                    out.add(p03);
                }
            }
            if (!out.isEmpty()) {
                return out;
            }
        }
        int dirIdx = StringsKt.indexOf((CharSequence) url, "/dir/", 0, false);
        if (dirIdx >= 0) {
            List<Stop> list = SequencesKt.toList(SequencesKt.map(dblRe.findAll(url, 0), new Function1() { // from class: com.bike.computer.GmapsRoute$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return GmapsRoute.parse$lambda$12((MatchResult) obj);
                }
            }));
            if (list.size() >= 2) {
                return list;
            }
            String after = url.substring(dirIdx + 5);
            Intrinsics.checkNotNullExpressionValue(after, "substring(...)");
            Iterable $this$filter$iv = CollectionsKt.listOf((Object[]) new Integer[]{Integer.valueOf(StringsKt.indexOf((CharSequence) after, "/@", 0, false)), Integer.valueOf(StringsKt.indexOf((CharSequence) after, "/data=", 0, false)), Integer.valueOf(StringsKt.indexOf((CharSequence) after, '?', 0, false))});
            List<Integer> destination$iv$iv = new ArrayList<Integer>();
            for (Object element$iv$iv : $this$filter$iv) {
                int it4 = ((Number) element$iv$iv).intValue();
                int it5 = it4 >= 0 ? 1 : 0;
                if (it5 != 0) {
                    destination$iv$iv.add((Integer) element$iv$iv);
                }
            }
            Integer num = (Integer) CollectionsKt.minOrNull((Iterable<Integer>) destination$iv$iv);
            int end = num != null ? num.intValue() : after.length();
            ArrayList out2 = new ArrayList();
            String strSubstring = after.substring(0, end);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            for (String seg : StringsKt.split((CharSequence) strSubstring, new char[]{'/'}, false, 0)) {
                if (!StringsKt.isBlank(seg)) {
                    String origin2 = origin;
                    if (StringsKt.startsWith(seg, "@", false) || StringsKt.startsWith(seg, "data=", false)) {
                        origin = origin2;
                    } else {
                        Stop p04 = stop(seg);
                        if (p04 != null) {
                            out2.add(p04);
                            origin = origin2;
                        } else {
                            origin = origin2;
                        }
                    }
                }
            }
            if (!out2.isEmpty()) {
                return out2;
            }
        }
        MatchResult it6 = placeRe.find(url, 0);
        if (it6 != null) {
            return CollectionsKt.listOf(new Stop.At(Double.parseDouble(it6.getGroupValues().get(1)), Double.parseDouble(it6.getGroupValues().get(2))));
        }
        String it7 = query.get("q");
        if (it7 != null && (s = INSTANCE.stop(it7)) != null) {
            return CollectionsKt.listOf(s);
        }
        int placeIdx = StringsKt.indexOf((CharSequence) url, "/place/", 0, false);
        if (placeIdx >= 0) {
            String strSubstring2 = url.substring(placeIdx + 7);
            Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
            String beforeSlash = StringsKt.substringBefore(strSubstring2, '/', strSubstring2);
            String name = StringsKt.substringBefore(beforeSlash, '@', beforeSlash);
            Stop it8 = stop(name);
            if (it8 != null) {
                return CollectionsKt.listOf(it8);
            }
        }
        MatchResult it9 = atRe.find(url, 0);
        return it9 != null ? CollectionsKt.listOf(new Stop.At(Double.parseDouble(it9.getGroupValues().get(1)), Double.parseDouble(it9.getGroupValues().get(2)))) : CollectionsKt.emptyList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Stop.At parse$lambda$12(MatchResult it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return new Stop.At(Double.parseDouble(it.getGroupValues().get(2)), Double.parseDouble(it.getGroupValues().get(1)));
    }

    private final Stop stop(String raw) {
        Object objM118constructorimpl;
        try {
            objM118constructorimpl = URLDecoder.decode(StringsKt.trim((CharSequence) raw).toString(), "UTF-8");
        } catch (Throwable th) {
            objM118constructorimpl = raw;
        }
        Intrinsics.checkNotNullExpressionValue(objM118constructorimpl, "getOrDefault(...)");
        String s = StringsKt.trim((CharSequence) objM118constructorimpl).toString();
        MatchResult it = coordRe.find(StringsKt.replace(s, " ", "", false), 0);
        if (it != null) {
            return new Stop.At(Double.parseDouble(it.getGroupValues().get(1)), Double.parseDouble(it.getGroupValues().get(2)));
        }
        Set<String> set = ignoreNames;
        String lowerCase = s.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        if (set.contains(lowerCase) || StringsKt.startsWith(s, "place_id:", false)) {
            return null;
        }
        return new Stop.Named(s);
    }

    private final Map<String, String> parseQuery(String q) {
        Object objM118constructorimpl;
        if (q.length() == 0) {
            return MapsKt.emptyMap();
        }
        HashMap m = new HashMap();
        for (String kv : StringsKt.split((CharSequence) q, new char[]{Typography.amp}, false, 0)) {
            int i = StringsKt.indexOf((CharSequence) kv, '=', 0, false);
            if (i > 0) {
                String k = kv.substring(0, i);
                Intrinsics.checkNotNullExpressionValue(k, "substring(...)");
                try {
                    String strSubstring = kv.substring(i + 1);
                    Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
                    objM118constructorimpl = URLDecoder.decode(strSubstring, "UTF-8");
                } catch (Throwable th) {
                    String strSubstring2 = kv.substring(i + 1);
                    Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
                    objM118constructorimpl = strSubstring2;
                }
                String v = (String) objM118constructorimpl;
                m.put(k, v);
            }
        }
        return m;
    }

    public final String toGpx(String name, List<double[]> pts) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(pts, "pts");
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<gpx version=\"1.1\" creator=\"Harmin\" xmlns=\"http://www.topografix.com/GPX/1/1\">\n");
        sb.append("<metadata><name>").append(xml(name)).append("</name></metadata>\n<rte>\n");
        for (double[] p : pts) {
            sb.append("<rtept lat=\"").append(p[1]).append("\" lon=\"").append(p[0]).append("\"></rtept>\n");
        }
        sb.append("</rte>\n</gpx>\n");
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    private final String xml(String s) {
        return StringsKt.replace(StringsKt.replace(StringsKt.replace(s, "&", "&amp;", false), "<", "&lt;", false), ">", "&gt;", false);
    }
}

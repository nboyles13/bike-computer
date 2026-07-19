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
import kotlin.Metadata;
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

/* JADX INFO: compiled from: GmapsRoute.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0013\n\u0002\b\t\n\u0002\u0010$\n\u0002\b\u0007\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001(B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0007J\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\u0006\u0010\u0016\u001a\u00020\u0007J\u001a\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00180\u00142\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014J\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00180\u00142\u0006\u0010\u0016\u001a\u00020\u0007J\u0010\u0010\u001b\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u0007H\u0002J\u0016\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\u0006\u0010\u001e\u001a\u00020\u0007H\u0002J\u0012\u0010\u001f\u001a\u0004\u0018\u00010\u00152\u0006\u0010 \u001a\u00020\u0007H\u0002J\u001c\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00070\"2\u0006\u0010#\u001a\u00020\u0007H\u0002J\u001c\u0010$\u001a\u00020\u00072\u0006\u0010%\u001a\u00020\u00072\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00180\u0014J\u0010\u0010'\u001a\u00020\u00072\u0006\u0010\u0012\u001a\u00020\u0007H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00070\u000fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006)"}, d2 = {"Lcom/bike/computer/GmapsRoute;", "", "<init>", "()V", "http", "Lokhttp3/OkHttpClient;", "UA", "", "coordRe", "Lkotlin/text/Regex;", "atRe", "placeRe", "dblRe", "fullUrlRe", "ignoreNames", "", "looksLikeLink", "", "s", "resolve", "", "Lcom/bike/computer/GmapsRoute$Stop;", "link", "toPoints", "", "stops", "points", "expand", "url0", "parse", "url", "stop", "raw", "parseQuery", "", "q", "toGpx", "name", "pts", "xml", "Stop", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class GmapsRoute {
    private static final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0 Safari/537.36";
    public static final GmapsRoute INSTANCE = new GmapsRoute();
    private static final OkHttpClient http = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).build();
    private static final Regex coordRe = new Regex("^(-?\\d{1,3}\\.\\d+),(-?\\d{1,3}\\.\\d+)$");
    private static final Regex atRe = new Regex("@(-?\\d{1,3}\\.\\d+),(-?\\d{1,3}\\.\\d+)");
    private static final Regex placeRe = new Regex("!3d(-?\\d{1,3}\\.\\d+)!4d(-?\\d{1,3}\\.\\d+)");
    private static final Regex dblRe = new Regex("!1d(-?\\d{1,3}\\.\\d+)!2d(-?\\d{1,3}\\.\\d+)");
    private static final Regex fullUrlRe = new Regex("https?://(?:www\\.)?(?:google\\.[a-z.]+|maps\\.google\\.[a-z.]+)/maps[^\"'\\\\ ]+");
    private static final Set<String> ignoreNames = SetsKt.setOf((Object[]) new String[]{"your location", "my location", "current location", ""});

    private GmapsRoute() {
    }

    /* JADX INFO: compiled from: GmapsRoute.kt */
    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0002\u0004\u0005B\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0002\u0006\u0007¨\u0006\b"}, d2 = {"Lcom/bike/computer/GmapsRoute$Stop;", "", "<init>", "()V", "At", "Named", "Lcom/bike/computer/GmapsRoute$Stop$At;", "Lcom/bike/computer/GmapsRoute$Stop$Named;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static abstract class Stop {
        public /* synthetic */ Stop(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* JADX INFO: compiled from: GmapsRoute.kt */
        @Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010HÖ\u0003J\t\u0010\u0011\u001a\u00020\u0012HÖ\u0001J\t\u0010\u0013\u001a\u00020\u0014HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0015"}, d2 = {"Lcom/bike/computer/GmapsRoute$Stop$At;", "Lcom/bike/computer/GmapsRoute$Stop;", "lat", "", "lon", "<init>", "(DD)V", "getLat", "()D", "getLon", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
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

            /* JADX INFO: renamed from: component1, reason: from getter */
            public final double getLat() {
                return this.lat;
            }

            /* JADX INFO: renamed from: component2, reason: from getter */
            public final double getLon() {
                return this.lon;
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
        @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rHÖ\u0003J\t\u0010\u000e\u001a\u00020\u000fHÖ\u0001J\t\u0010\u0010\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011"}, d2 = {"Lcom/bike/computer/GmapsRoute$Stop$Named;", "Lcom/bike/computer/GmapsRoute$Stop;", "q", "", "<init>", "(Ljava/lang/String;)V", "getQ", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
        public static final /* data */ class Named extends Stop {
            private final String q;

            public static /* synthetic */ Named copy$default(Named named, String str, int i, Object obj) {
                if ((i & 1) != 0) {
                    str = named.q;
                }
                return named.copy(str);
            }

            /* JADX INFO: renamed from: component1, reason: from getter */
            public final String getQ() {
                return this.q;
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
        return StringsKt.contains$default((CharSequence) t, (CharSequence) "goo.gl", false, 2, (Object) null) || (StringsKt.contains$default((CharSequence) t, (CharSequence) "google.", false, 2, (Object) null) && StringsKt.contains$default((CharSequence) t, (CharSequence) "/maps", false, 2, (Object) null)) || StringsKt.contains$default((CharSequence) t, (CharSequence) "maps.app", false, 2, (Object) null);
    }

    public final List<Stop> resolve(String link) {
        Intrinsics.checkNotNullParameter(link, "link");
        return parse(expand(StringsKt.trim((CharSequence) link).toString()));
    }

    public final List<double[]> toPoints(List<? extends Stop> stops) {
        double[] dArr;
        Intrinsics.checkNotNullParameter(stops, "stops");
        Iterator<T> it = stops.iterator();
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
                Geocoder.Place it3 = (Geocoder.Place) CollectionsKt.firstOrNull((List) Geocoder.INSTANCE.search(((Stop.Named) s).getQ(), near));
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
        String str = StringsKt.startsWith$default(url0, "http", false, 2, (Object) null) ? url0 : "https://" + url0;
        try {
            Object url = Result.INSTANCE;
            GmapsRoute gmapsRoute = this;
            String host = new URI(str).getHost();
            if (host == null) {
                host = "";
            }
            objM118constructorimpl = Result.m118constructorimpl(host);
        } catch (Throwable th) {
            Result.Companion companion = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m124isFailureimpl(objM118constructorimpl)) {
            objM118constructorimpl = "";
        }
        String host2 = (String) objM118constructorimpl;
        if (!StringsKt.contains$default((CharSequence) host2, (CharSequence) "goo.gl", false, 2, (Object) null)) {
            return str;
        }
        try {
            Result.Companion companion2 = Result.INSTANCE;
            GmapsRoute gmapsRoute2 = this;
            Response responseExecute = http.newCall(new Request.Builder().url(str).header("User-Agent", UA).build()).execute();
            try {
                Response r = responseExecute;
                String finalUrl = r.request().url().getUrl();
                if (StringsKt.contains$default((CharSequence) finalUrl, (CharSequence) "goo.gl", false, 2, (Object) null)) {
                    ResponseBody responseBodyBody = r.body();
                    if (responseBodyBody != null && (strString = responseBodyBody.string()) != null) {
                        body = strString;
                    }
                    MatchResult matchResultFind$default = Regex.find$default(fullUrlRe, body, 0, 2, null);
                    if (matchResultFind$default != null && (value = matchResultFind$default.getValue()) != null && (strReplace$default = StringsKt.replace$default(value, "\\u003d", "=", false, 4, (Object) null)) != null) {
                        String strReplace$default2 = StringsKt.replace$default(strReplace$default, "\\u0026", "&", false, 4, (Object) null);
                        if (strReplace$default2 != null) {
                            finalUrl = strReplace$default2;
                        }
                    }
                }
                CloseableKt.closeFinally(responseExecute, null);
                objM118constructorimpl2 = Result.m118constructorimpl(finalUrl);
            } finally {
            }
        } catch (Throwable th2) {
            Result.Companion companion3 = Result.INSTANCE;
            objM118constructorimpl2 = Result.m118constructorimpl(ResultKt.createFailure(th2));
        }
        if (Result.m124isFailureimpl(objM118constructorimpl2)) {
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
            if (str != null && (iterableSplit$default = StringsKt.split$default((CharSequence) str, new char[]{'|', '\n'}, false, 0, 6, (Object) null)) != null) {
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
        int dirIdx = StringsKt.indexOf$default((CharSequence) url, "/dir/", 0, false, 6, (Object) null);
        if (dirIdx >= 0) {
            List<Stop> list = SequencesKt.toList(SequencesKt.map(Regex.findAll$default(dblRe, url, 0, 2, null), new Function1() { // from class: com.bike.computer.GmapsRoute$$ExternalSyntheticLambda0
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
            Iterable $this$filter$iv = CollectionsKt.listOf((Object[]) new Integer[]{Integer.valueOf(StringsKt.indexOf$default((CharSequence) after, "/@", 0, false, 6, (Object) null)), Integer.valueOf(StringsKt.indexOf$default((CharSequence) after, "/data=", 0, false, 6, (Object) null)), Integer.valueOf(StringsKt.indexOf$default((CharSequence) after, '?', 0, false, 6, (Object) null))});
            Collection destination$iv$iv = new ArrayList();
            for (Object element$iv$iv : $this$filter$iv) {
                int it4 = ((Number) element$iv$iv).intValue();
                int it5 = it4 >= 0 ? 1 : 0;
                if (it5 != 0) {
                    destination$iv$iv.add(element$iv$iv);
                }
            }
            Integer num = (Integer) CollectionsKt.minOrNull(destination$iv$iv);
            int end = num != null ? num.intValue() : after.length();
            ArrayList out2 = new ArrayList();
            String strSubstring = after.substring(0, end);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            for (String seg : StringsKt.split$default((CharSequence) strSubstring, new char[]{'/'}, false, 0, 6, (Object) null)) {
                if (!StringsKt.isBlank(seg)) {
                    String origin2 = origin;
                    if (StringsKt.startsWith$default(seg, "@", false, 2, (Object) null) || StringsKt.startsWith$default(seg, "data=", false, 2, (Object) null)) {
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
        MatchResult it6 = Regex.find$default(placeRe, url, 0, 2, null);
        if (it6 != null) {
            return CollectionsKt.listOf(new Stop.At(Double.parseDouble(it6.getGroupValues().get(1)), Double.parseDouble(it6.getGroupValues().get(2))));
        }
        String it7 = query.get("q");
        if (it7 != null && (s = INSTANCE.stop(it7)) != null) {
            return CollectionsKt.listOf(s);
        }
        int placeIdx = StringsKt.indexOf$default((CharSequence) url, "/place/", 0, false, 6, (Object) null);
        if (placeIdx >= 0) {
            String strSubstring2 = url.substring(placeIdx + 7);
            Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
            String name = StringsKt.substringBefore$default(StringsKt.substringBefore$default(strSubstring2, '/', (String) null, 2, (Object) null), '@', (String) null, 2, (Object) null);
            Stop it8 = stop(name);
            if (it8 != null) {
                return CollectionsKt.listOf(it8);
            }
        }
        MatchResult it9 = Regex.find$default(atRe, url, 0, 2, null);
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
            Result.Companion companion = Result.INSTANCE;
            GmapsRoute gmapsRoute = this;
            objM118constructorimpl = Result.m118constructorimpl(URLDecoder.decode(StringsKt.trim((CharSequence) raw).toString(), "UTF-8"));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m124isFailureimpl(objM118constructorimpl)) {
            objM118constructorimpl = raw;
        }
        Intrinsics.checkNotNullExpressionValue(objM118constructorimpl, "getOrDefault(...)");
        String s = StringsKt.trim((CharSequence) objM118constructorimpl).toString();
        MatchResult it = Regex.find$default(coordRe, StringsKt.replace$default(s, " ", "", false, 4, (Object) null), 0, 2, null);
        if (it != null) {
            return new Stop.At(Double.parseDouble(it.getGroupValues().get(1)), Double.parseDouble(it.getGroupValues().get(2)));
        }
        Set<String> set = ignoreNames;
        String lowerCase = s.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        if (set.contains(lowerCase) || StringsKt.startsWith$default(s, "place_id:", false, 2, (Object) null)) {
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
        for (String kv : StringsKt.split$default((CharSequence) q, new char[]{Typography.amp}, false, 0, 6, (Object) null)) {
            int i = StringsKt.indexOf$default((CharSequence) kv, '=', 0, false, 6, (Object) null);
            if (i > 0) {
                String k = kv.substring(0, i);
                Intrinsics.checkNotNullExpressionValue(k, "substring(...)");
                try {
                    Result.Companion companion = Result.INSTANCE;
                    GmapsRoute gmapsRoute = this;
                    String strSubstring = kv.substring(i + 1);
                    Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
                    objM118constructorimpl = Result.m118constructorimpl(URLDecoder.decode(strSubstring, "UTF-8"));
                } catch (Throwable th) {
                    Result.Companion companion2 = Result.INSTANCE;
                    objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
                }
                String strSubstring2 = kv.substring(i + 1);
                Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
                if (Result.m124isFailureimpl(objM118constructorimpl)) {
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
        return StringsKt.replace$default(StringsKt.replace$default(StringsKt.replace$default(s, "&", "&amp;", false, 4, (Object) null), "<", "&lt;", false, 4, (Object) null), ">", "&gt;", false, 4, (Object) null);
    }
}

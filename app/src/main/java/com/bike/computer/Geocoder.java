package com.bike.computer;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: compiled from: Geocoder.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0013\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001:\u0001\rB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lcom/bike/computer/Geocoder;", "", "<init>", "()V", "http", "Lokhttp3/OkHttpClient;", "search", "", "Lcom/bike/computer/Geocoder$Place;", "query", "", "near", "", "Place", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class Geocoder {
    public static final Geocoder INSTANCE = new Geocoder();
    private static final OkHttpClient http = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();

    private Geocoder() {
    }

    /* JADX INFO: compiled from: Geocoder.kt */
    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0015\u001a\u00020\u0016HÖ\u0001J\t\u0010\u0017\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f¨\u0006\u0018"}, d2 = {"Lcom/bike/computer/Geocoder$Place;", "", "name", "", "lat", "", "lon", "<init>", "(Ljava/lang/String;DD)V", "getName", "()Ljava/lang/String;", "getLat", "()D", "getLon", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final /* data */ class Place {
        private final double lat;
        private final double lon;
        private final String name;

        public static /* synthetic */ Place copy$default(Place place, String str, double d, double d2, int i, Object obj) {
            if ((i & 1) != 0) {
                str = place.name;
            }
            if ((i & 2) != 0) {
                d = place.lat;
            }
            double d3 = d;
            if ((i & 4) != 0) {
                d2 = place.lon;
            }
            return place.copy(str, d3, d2);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final String getName() {
            return this.name;
        }

        /* JADX INFO: renamed from: component2, reason: from getter */
        public final double getLat() {
            return this.lat;
        }

        /* JADX INFO: renamed from: component3, reason: from getter */
        public final double getLon() {
            return this.lon;
        }

        public final Place copy(String name, double lat, double lon) {
            Intrinsics.checkNotNullParameter(name, "name");
            return new Place(name, lat, lon);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Place)) {
                return false;
            }
            Place place = (Place) other;
            return Intrinsics.areEqual(this.name, place.name) && Double.compare(this.lat, place.lat) == 0 && Double.compare(this.lon, place.lon) == 0;
        }

        public int hashCode() {
            return (((this.name.hashCode() * 31) + Double.hashCode(this.lat)) * 31) + Double.hashCode(this.lon);
        }

        public String toString() {
            return "Place(name=" + this.name + ", lat=" + this.lat + ", lon=" + this.lon + ")";
        }

        public Place(String name, double lat, double lon) {
            Intrinsics.checkNotNullParameter(name, "name");
            this.name = name;
            this.lat = lat;
            this.lon = lon;
        }

        public final double getLat() {
            return this.lat;
        }

        public final double getLon() {
            return this.lon;
        }

        public final String getName() {
            return this.name;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x00c1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final List<Place> search(String query, double[] near) throws IOException {
        String url;
        Throwable th;
        Intrinsics.checkNotNullParameter(query, "query");
        String q = URLEncoder.encode(StringsKt.trim((CharSequence) query).toString(), "UTF-8");
        String url2 = "https://nominatim.openstreetmap.org/search?q=" + q + "&format=jsonv2&limit=10&countrycodes=us";
        if (near != null) {
            url = url2 + "&viewbox=" + (near[1] - 1.0d) + "," + (near[0] - 0.8d) + "," + (near[1] + 1.0d) + "," + (near[0] + 0.8d) + "&bounded=0";
        } else {
            url = url2;
        }
        Request req = new Request.Builder().url(url).header("User-Agent", "Harmin/1.0 (personal bike computer)").build();
        Response responseExecute = http.newCall(req).execute();
        try {
            Response r = responseExecute;
            int i = 0;
            ResponseBody responseBodyBody = r.body();
            if (responseBodyBody != null) {
                try {
                    String body = responseBodyBody.string();
                    if (body == null) {
                        body = "";
                    }
                    try {
                        if (!r.isSuccessful()) {
                            throw new RuntimeException("HTTP " + r.code());
                        }
                        JSONArray arr = new JSONArray(body);
                        Iterable $this$map$iv = RangesKt.until(0, arr.length());
                        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                        Iterator<Integer> it = $this$map$iv.iterator();
                        while (it.hasNext()) {
                            int item$iv$iv = ((IntIterator) it).nextInt();
                            JSONObject o = arr.getJSONObject(item$iv$iv);
                            String url3 = url;
                            try {
                                Request req2 = req;
                                String string = o.getString("display_name");
                                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                                destination$iv$iv.add(new Place(string, o.getDouble("lat"), o.getDouble("lon")));
                                url = url3;
                                body = body;
                                req = req2;
                                i = i;
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                        ArrayList arrayList = (List) destination$iv$iv;
                        CloseableKt.closeFinally(responseExecute, null);
                        return arrayList;
                    } catch (Throwable th3) {
                        th = th3;
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
            }
        } catch (Throwable th5) {
            th = th5;
        }
        try {
            throw th;
        } catch (Throwable th6) {
            CloseableKt.closeFinally(responseExecute, th);
            throw th6;
        }
    }
}

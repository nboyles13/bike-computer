package com.bike.computer;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

public final class Geocoder {
    public static final Geocoder INSTANCE = new Geocoder();
    private static final OkHttpClient http = new OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).build();

    private Geocoder() {
    }

    /* JADX INFO: compiled from: Geocoder.kt */
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
        Throwable th = null;
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
                        List arrayList = (List) destination$iv$iv;
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
            throw Sneaky.sneak(th6);
        }
    }
}

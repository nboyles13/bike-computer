package com.bike.computer;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RideHistory {
    private static final String FILE = "/sdcard/BikeComputer/rides.json";
    private static final String GPX_DIR = "/sdcard/BikeComputer/rides";
    public static final RideHistory INSTANCE = new RideHistory();

    private RideHistory() {
    }

    public final List<RideSummary> all() {
        List<RideSummary> objM118constructorimpl;
        File f = new File(FILE);
        if (!f.exists()) {
            return CollectionsKt.emptyList();
        }
        try {
            RideHistory $this$all_u24lambda_u241 = this;
            JSONArray a = new JSONArray(FilesKt.readText(f, kotlin.text.Charsets.UTF_8));
            Iterable $this$map$iv = RangesKt.until(0, a.length());
            Collection<RideSummary> destination$iv$iv = new ArrayList<>(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
            Iterator<Integer> it = $this$map$iv.iterator();
            while (it.hasNext()) {
                int item$iv$iv = ((IntIterator) it).nextInt();
                JSONObject jSONObject = a.getJSONObject(item$iv$iv);
                Intrinsics.checkNotNullExpressionValue(jSONObject, "getJSONObject(...)");
                destination$iv$iv.add($this$all_u24lambda_u241.fromJson(jSONObject));
            }
            objM118constructorimpl = (List<RideSummary>) destination$iv$iv;
        } catch (Throwable th) {
            objM118constructorimpl = CollectionsKt.emptyList();
        }
        Iterable $this$sortedByDescending$iv = (Iterable) objM118constructorimpl;
        return CollectionsKt.sortedWith($this$sortedByDescending$iv, new Comparator() { // from class: com.bike.computer.RideHistory$all$$inlined$sortedByDescending$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(Object t, Object t2) {
                RideSummary it2 = (RideSummary) t2;
                RideSummary it3 = (RideSummary) t;
                return ComparisonsKt.compareValues(Long.valueOf(it2.getStartMs()), Long.valueOf(it3.getStartMs()));
            }
        });
    }

    public final void add(RideSummary s) {
        Intrinsics.checkNotNullParameter(s, "s");
        Iterable $this$filter$iv = all();
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            RideSummary it = (RideSummary) element$iv$iv;
            if (it.getStartMs() != s.getStartMs()) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        writeAll(CollectionsKt.plus((Collection<? extends RideSummary>) destination$iv$iv, s));
    }

    public final RideSummary byStart(long startMs) {
        Object element$iv;
        Iterable $this$firstOrNull$iv = all();
        Iterator it = $this$firstOrNull$iv.iterator();
        while (true) {
            if (it.hasNext()) {
                element$iv = it.next();
                RideSummary it2 = (RideSummary) element$iv;
                if (it2.getStartMs() == startMs) {
                    break;
                }
            } else {
                element$iv = null;
                break;
            }
        }
        return (RideSummary) element$iv;
    }

    public final void rename(long startMs, String name) {
        Iterable $this$map$iv = all();
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            RideSummary it = (RideSummary) item$iv$iv;
            destination$iv$iv.add(it.getStartMs() == startMs ? it.copy((12287 & 1) != 0 ? it.startMs : 0L, (12287 & 2) != 0 ? it.route : null, (12287 & 4) != 0 ? it.distanceM : 0.0d, (12287 & 8) != 0 ? it.movingMs : 0L, (12287 & 16) != 0 ? it.avgMps : 0.0f, (12287 & 32) != 0 ? it.maxMps : 0.0f, (12287 & 64) != 0 ? it.hrAvg : 0, (12287 & 128) != 0 ? it.hrMax : 0, (12287 & 256) != 0 ? it.ascentM : 0.0d, (12287 & 512) != 0 ? it.powerAvg : 0, (12287 & 1024) != 0 ? it.powerMax : 0, (12287 & 2048) != 0 ? it.gpx : null, (12287 & 4096) != 0 ? it.uploaded : false, (12287 & 8192) != 0 ? it.name : name) : it);
        }
        writeAll((List) destination$iv$iv);
    }

    public final void delete(long startMs) {
        Object element$iv;
        String it;
        Iterable $this$firstOrNull$iv = all();
        Iterator it2 = $this$firstOrNull$iv.iterator();
        while (true) {
            if (it2.hasNext()) {
                element$iv = it2.next();
                if (((RideSummary) element$iv).getStartMs() == startMs) {
                    break;
                }
            } else {
                element$iv = null;
                break;
            }
        }
        RideSummary rideSummary = (RideSummary) element$iv;
        if (rideSummary != null && (it = rideSummary.getGpx()) != null) {
            RideHistory rideHistory = INSTANCE;
            try {
                new File(GPX_DIR, it).delete();
            } catch (Throwable th) {
            }
        }
        Iterable $this$filter$iv = all();
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            if (((RideSummary) element$iv$iv).getStartMs() != startMs) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        writeAll((List) destination$iv$iv);
    }

    public final void markUploaded(long startMs) {
        Iterable $this$map$iv = all();
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            RideSummary it = (RideSummary) item$iv$iv;
            destination$iv$iv.add(it.getStartMs() == startMs ? it.copy((12287 & 1) != 0 ? it.startMs : 0L, (12287 & 2) != 0 ? it.route : null, (12287 & 4) != 0 ? it.distanceM : 0.0d, (12287 & 8) != 0 ? it.movingMs : 0L, (12287 & 16) != 0 ? it.avgMps : 0.0f, (12287 & 32) != 0 ? it.maxMps : 0.0f, (12287 & 64) != 0 ? it.hrAvg : 0, (12287 & 128) != 0 ? it.hrMax : 0, (12287 & 256) != 0 ? it.ascentM : 0.0d, (12287 & 512) != 0 ? it.powerAvg : 0, (12287 & 1024) != 0 ? it.powerMax : 0, (12287 & 2048) != 0 ? it.gpx : null, (12287 & 4096) != 0 ? it.uploaded : true, (12287 & 8192) != 0 ? it.name : null) : it);
        }
        List list = (List) destination$iv$iv;
        writeAll(list);
    }

    public final boolean reconcile() {
        boolean z;
        RideSummary s;
        Iterable $this$associateBy$iv = all();
        int capacity$iv = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault($this$associateBy$iv, 10)), 16);
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        for (Object element$iv$iv : $this$associateBy$iv) {
            RideSummary it = (RideSummary) element$iv$iv;
            destination$iv$iv.put(Long.valueOf(it.getStartMs()), element$iv$iv);
        }
        Map byStart = MapsKt.toMutableMap(destination$iv$iv);
        File[] gpx = new File(GPX_DIR).listFiles(new FileFilter() { // from class: com.bike.computer.RideHistory$$ExternalSyntheticLambda0
            @Override // java.io.FileFilter
            public final boolean accept(File file) {
                return RideHistory.reconcile$lambda$12(file);
            }
        });
        if (gpx == null) {
            return false;
        }
        boolean changed = false;
        for (File f : gpx) {
            GpxSummary gpxSummary = GpxSummary.INSTANCE;
            Intrinsics.checkNotNull(f);
            Long lStartMsFromName = gpxSummary.startMsFromName(f);
            if (lStartMsFromName != null) {
                long start = lStartMsFromName.longValue();
                Iterable $this$any$iv = byStart.keySet();
                if (!($this$any$iv instanceof Collection) || !((Collection) $this$any$iv).isEmpty()) {
                    Iterator it2 = $this$any$iv.iterator();
                    while (true) {
                        if (it2.hasNext()) {
                            Object element$iv = it2.next();
                            long it3 = ((Number) element$iv).longValue();
                            z = true;
                            if (Math.abs(it3 - start) < 2000) {
                                break;
                            }
                        } else {
                            z = false;
                            break;
                        }
                    }
                } else {
                    z = false;
                }
                if (!z && (s = GpxSummary.INSTANCE.summarize(f)) != null) {
                    byStart.put(Long.valueOf(s.getStartMs()), s);
                    changed = true;
                }
            }
        }
        if (changed) {
            writeAll(CollectionsKt.toList(byStart.values()));
        }
        return changed;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean reconcile$lambda$12(File f) {
        String name = f.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        if (!StringsKt.startsWith(name, "ride_", false)) {
            return false;
        }
        String name2 = f.getName();
        Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
        return StringsKt.endsWith(name2, ".gpx", false);
    }

    private final void writeAll(List<RideSummary> list) {
        JSONArray arr = new JSONArray();
        List<RideSummary> $this$sortedByDescending$iv = list;
        for (RideSummary s : CollectionsKt.sortedWith($this$sortedByDescending$iv, new Comparator<RideSummary>() { // from class: com.bike.computer.RideHistory$writeAll$$inlined$sortedByDescending$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(RideSummary t, RideSummary t2) {
                RideSummary it = (RideSummary) t2;
                RideSummary it2 = (RideSummary) t;
                return ComparisonsKt.compareValues(Long.valueOf(it.getStartMs()), Long.valueOf(it2.getStartMs()));
            }
        })) {
            try {
                arr.put(toJson(s));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        File f = new File(FILE);
        File parentFile = f.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
        try {
            RideHistory rideHistory = this;
            File tmp = new File("/sdcard/BikeComputer/rides.json.tmp");
            String string = arr.toString();
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
            FilesKt.writeText(tmp, string, kotlin.text.Charsets.UTF_8);
            if (!tmp.renameTo(f)) {
                String string2 = arr.toString();
                Intrinsics.checkNotNullExpressionValue(string2, "toString(...)");
                FilesKt.writeText(f, string2, kotlin.text.Charsets.UTF_8);
                tmp.delete();
            }
        } catch (Throwable th) {
        }
    }

    public final List<RideSummary> forRoute(String route) {
        Intrinsics.checkNotNullParameter(route, "route");
        Iterable $this$filter$iv = all();
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            RideSummary it = (RideSummary) element$iv$iv;
            if (Intrinsics.areEqual(it.getRoute(), route)) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        Iterable $this$sortedBy$iv = (List) destination$iv$iv;
        return CollectionsKt.sortedWith($this$sortedBy$iv, new Comparator() { // from class: com.bike.computer.RideHistory$forRoute$$inlined$sortedBy$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(Object t, Object t2) {
                RideSummary it2 = (RideSummary) t;
                RideSummary it3 = (RideSummary) t2;
                return ComparisonsKt.compareValues(Long.valueOf(it2.getMovingMs()), Long.valueOf(it3.getMovingMs()));
            }
        });
    }

    public final Map<String, RideSummary> routeBests() {
        Object minElem$iv;
        Object answer$iv$iv$iv;
        Iterable $this$filter$iv = all();
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            RideSummary it = (RideSummary) element$iv$iv;
            if (it.getRoute() != null) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        Iterable $this$groupBy$iv = (List) destination$iv$iv;
        Map destination$iv$iv2 = new LinkedHashMap();
        for (Object element$iv$iv2 : $this$groupBy$iv) {
            RideSummary it2 = (RideSummary) element$iv$iv2;
            String route = it2.getRoute();
            Intrinsics.checkNotNull(route);
            Object value$iv$iv$iv = destination$iv$iv2.get(route);
            if (value$iv$iv$iv == null) {
                answer$iv$iv$iv = new ArrayList();
                destination$iv$iv2.put(route, answer$iv$iv$iv);
            } else {
                answer$iv$iv$iv = value$iv$iv$iv;
            }
            List list$iv$iv = (List) answer$iv$iv$iv;
            list$iv$iv.add(element$iv$iv2);
        }
        Map destination$iv$iv3 = new LinkedHashMap(MapsKt.mapCapacity(destination$iv$iv2.size()));
        Iterable $this$associateByTo$iv$iv$iv = destination$iv$iv2.entrySet();
        for (Object element$iv$iv$iv : $this$associateByTo$iv$iv$iv) {
            Map.Entry it$iv$iv = (Map.Entry) element$iv$iv$iv;
            Object key = it$iv$iv.getKey();
            Iterable v = (List) ((Map.Entry) element$iv$iv$iv).getValue();
            Iterable $this$minByOrNull$iv = v;
            Iterator iterator$iv = $this$minByOrNull$iv.iterator();
            if (iterator$iv.hasNext()) {
                minElem$iv = iterator$iv.next();
                if (iterator$iv.hasNext()) {
                    RideSummary it3 = (RideSummary) minElem$iv;
                    long minValue$iv = it3.getMovingMs();
                    do {
                        Object e$iv = iterator$iv.next();
                        RideSummary it4 = (RideSummary) e$iv;
                        long v$iv = it4.getMovingMs();
                        if (minValue$iv > v$iv) {
                            minElem$iv = e$iv;
                            minValue$iv = v$iv;
                        }
                    } while (iterator$iv.hasNext());
                }
            } else {
                minElem$iv = null;
            }
            Intrinsics.checkNotNull(minElem$iv);
            destination$iv$iv3.put(key, (RideSummary) minElem$iv);
        }
        return destination$iv$iv3;
    }

    private final JSONObject toJson(RideSummary s) throws JSONException {
        JSONObject $this$toJson_u24lambda_u2425 = new JSONObject();
        $this$toJson_u24lambda_u2425.put("startMs", s.getStartMs());
        String it = s.getRoute();
        if (it != null) {
            $this$toJson_u24lambda_u2425.put("route", it);
        }
        $this$toJson_u24lambda_u2425.put("distM", s.getDistanceM());
        $this$toJson_u24lambda_u2425.put("movingMs", s.getMovingMs());
        $this$toJson_u24lambda_u2425.put("avgMps", s.getAvgMps());
        $this$toJson_u24lambda_u2425.put("maxMps", s.getMaxMps());
        $this$toJson_u24lambda_u2425.put("hrAvg", s.getHrAvg());
        $this$toJson_u24lambda_u2425.put("hrMax", s.getHrMax());
        $this$toJson_u24lambda_u2425.put("ascentM", s.getAscentM());
        $this$toJson_u24lambda_u2425.put("powerAvg", s.getPowerAvg());
        $this$toJson_u24lambda_u2425.put("powerMax", s.getPowerMax());
        String it2 = s.getGpx();
        if (it2 != null) {
            $this$toJson_u24lambda_u2425.put("gpx", it2);
        }
        if (s.getUploaded()) {
            $this$toJson_u24lambda_u2425.put("uploaded", true);
        }
        String it3 = s.getName();
        if (it3 != null) {
            $this$toJson_u24lambda_u2425.put("name", it3);
        }
        return $this$toJson_u24lambda_u2425;
    }

    private final RideSummary fromJson(JSONObject o) throws JSONException {
        String str;
        long j = o.getLong("startMs");
        String str2 = null;
        if (o.isNull("route")) {
            str = null;
        } else {
            String strOptString = o.optString("route");
            if (StringsKt.isBlank(strOptString)) {
                strOptString = null;
            }
            str = strOptString;
        }
        double dOptDouble = o.optDouble("distM", 0.0d);
        long jOptLong = o.optLong("movingMs", 0L);
        float fOptDouble = (float) o.optDouble("avgMps", 0.0d);
        float fOptDouble2 = (float) o.optDouble("maxMps", 0.0d);
        int iOptInt = o.optInt("hrAvg");
        int iOptInt2 = o.optInt("hrMax");
        double dOptDouble2 = o.optDouble("ascentM", 0.0d);
        int iOptInt3 = o.optInt("powerAvg");
        int iOptInt4 = o.optInt("powerMax");
        String strOptString2 = o.optString("gpx");
        if (StringsKt.isBlank(strOptString2)) {
            strOptString2 = null;
        }
        String str3 = strOptString2;
        boolean zOptBoolean = o.optBoolean("uploaded", false);
        String strOptString3 = o.optString("name");
        if (!StringsKt.isBlank(strOptString3)) {
            str2 = strOptString3;
        }
        return new RideSummary(j, str, dOptDouble, jOptLong, fOptDouble, fOptDouble2, iOptInt, iOptInt2, dOptDouble2, iOptInt3, iOptInt4, str3, zOptBoolean, str2);
    }
}

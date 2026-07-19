package com.bike.computer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.RegexOption;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: GpxSummary.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0016\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u0011¢\u0006\u0002\u0010\u0012J\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0010\u001a\u00020\u0011J\u0016\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0018J\u0016\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u0017\u001a\u00020\u0018J\u0016\u0010\u001b\u001a\u00020\u00182\u0006\u0010\u001c\u001a\u00020\u00182\u0006\u0010\u0017\u001a\u00020\u0018J(\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001e2\u0006\u0010 \u001a\u00020\u001e2\u0006\u0010!\u001a\u00020\u001e2\u0006\u0010\"\u001a\u00020\u001eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006#"}, d2 = {"Lcom/bike/computer/GpxSummary;", "", "<init>", "()V", "fnameFmt", "Ljava/text/SimpleDateFormat;", "iso", "nameRe", "Lkotlin/text/Regex;", "trkptRe", "timeRe", "eleRe", "hrRe", "powRe", "startMsFromName", "", "f", "Ljava/io/File;", "(Ljava/io/File;)Ljava/lang/Long;", "summarize", "Lcom/bike/computer/RideSummary;", "zoneTimes", "", "maxHr", "", "text", "", "zoneOf", "hr", "haversine", "", "la1", "lo1", "la2", "lo2", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class GpxSummary {
    private static final Regex eleRe;
    private static final Regex hrRe;
    private static final SimpleDateFormat iso;
    private static final Regex nameRe;
    private static final Regex powRe;
    private static final Regex timeRe;
    private static final Regex trkptRe;
    public static final GpxSummary INSTANCE = new GpxSummary();
    private static final SimpleDateFormat fnameFmt = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);

    private GpxSummary() {
    }

    static {
        SimpleDateFormat $this$iso_u24lambda_u240 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        $this$iso_u24lambda_u240.setTimeZone(TimeZone.getTimeZone("UTC"));
        iso = $this$iso_u24lambda_u240;
        nameRe = new Regex("ride_(\\d{8}_\\d{6})");
        trkptRe = new Regex("<trkpt[^>]*?lat=\"([-0-9.]+)\"[^>]*?lon=\"([-0-9.]+)\"[^>]*?>(.*?)</trkpt>", RegexOption.DOT_MATCHES_ALL);
        timeRe = new Regex("<time>([^<]+)</time>");
        eleRe = new Regex("<ele>([-0-9.]+)</ele>");
        hrRe = new Regex("<gpxtpx:hr>(\\d+)</gpxtpx:hr>");
        powRe = new Regex("<power>(\\d+)</power>");
    }

    public final Long startMsFromName(File f) {
        Object objM118constructorimpl;
        Intrinsics.checkNotNullParameter(f, "f");
        Regex regex = nameRe;
        String name = f.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        MatchResult m = Regex.find$default(regex, name, 0, 2, null);
        if (m == null) {
            return null;
        }
        try {
            Result.Companion companion = Result.INSTANCE;
            GpxSummary gpxSummary = this;
            Date date = fnameFmt.parse(m.getGroupValues().get(1));
            objM118constructorimpl = Result.m118constructorimpl(date != null ? Long.valueOf(date.getTime()) : null);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        return (Long) (Result.m124isFailureimpl(objM118constructorimpl) ? null : objM118constructorimpl);
    }

    /* JADX WARN: Removed duplicated region for block: B:44:0x013e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final RideSummary summarize(File f) {
        Object objM118constructorimpl;
        float f2;
        int i;
        int hrCnt;
        long start;
        double maxSpd;
        String body;
        List<String> groupValues;
        String str;
        Integer intOrNull;
        double maxSpd2;
        int powCnt;
        int powMax;
        List<String> groupValues2;
        String str2;
        Integer intOrNull2;
        int hrCnt2;
        List<String> groupValues3;
        String str3;
        Double doubleOrNull;
        Object objM118constructorimpl2;
        Intrinsics.checkNotNullParameter(f, "f");
        Long lStartMsFromName = startMsFromName(f);
        if (lStartMsFromName == null) {
            return null;
        }
        long start2 = lStartMsFromName.longValue();
        try {
            Result.Companion companion = Result.INSTANCE;
            GpxSummary gpxSummary = this;
            objM118constructorimpl = Result.m118constructorimpl(FilesKt.readText$default(f, null, 1, null));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m124isFailureimpl(objM118constructorimpl)) {
            objM118constructorimpl = null;
        }
        String text = (String) objM118constructorimpl;
        if (text == null) {
            return null;
        }
        int powCnt2 = 0;
        double lastLat = Double.NaN;
        int hrCnt3 = 2;
        Iterator it = Regex.findAll$default(trkptRe, text, 0, 2, null).iterator();
        int hrCnt4 = 0;
        long lastTime = 0;
        long hrSum = 0;
        long powSum = 0;
        int powMax2 = 0;
        double ascent = 0.0d;
        double eleRef = Double.NaN;
        double dist = 0.0d;
        long lastT = 0;
        long firstT = 0;
        int hrMax = 0;
        long movingMs = 0;
        double maxSpd3 = 0.0d;
        double prevSpd = -1.0d;
        int powCnt3 = 0;
        double lastLon = Double.NaN;
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            MatchResult m = (MatchResult) it.next();
            Double doubleOrNull2 = StringsKt.toDoubleOrNull(m.getGroupValues().get(1));
            if (doubleOrNull2 != null) {
                double lat = doubleOrNull2.doubleValue();
                Double doubleOrNull3 = StringsKt.toDoubleOrNull(m.getGroupValues().get(hrCnt3));
                if (doubleOrNull3 != null) {
                    double lon = doubleOrNull3.doubleValue();
                    int powCnt4 = powCnt3;
                    int powMax3 = powMax2;
                    String body2 = m.getGroupValues().get(3);
                    Iterator it2 = it;
                    int hrMax2 = hrMax;
                    double prevSpd2 = prevSpd;
                    MatchResult it3 = Regex.find$default(timeRe, body2, 0, hrCnt3, null);
                    if (it3 != null) {
                        GpxSummary gpxSummary2 = INSTANCE;
                        try {
                            Result.Companion companion3 = Result.INSTANCE;
                        } catch (Throwable th2) {
                            th = th2;
                        }
                        try {
                            Date date = iso.parse(it3.getGroupValues().get(1));
                            objM118constructorimpl2 = Result.m118constructorimpl(date != null ? Long.valueOf(date.getTime()) : null);
                        } catch (Throwable th3) {
                            th = th3;
                            Result.Companion companion4 = Result.INSTANCE;
                            objM118constructorimpl2 = Result.m118constructorimpl(ResultKt.createFailure(th));
                        }
                        if (Result.m124isFailureimpl(objM118constructorimpl2)) {
                            objM118constructorimpl2 = null;
                        }
                        Long l = (Long) objM118constructorimpl2;
                        long jLongValue = l != null ? l.longValue() : 0L;
                        long t = jLongValue;
                        if (t > 0) {
                            if (firstT == 0) {
                                firstT = t;
                            }
                            lastTime = t;
                        }
                        if (Double.isNaN(lastLat)) {
                            hrCnt = hrCnt4;
                            start = start2;
                            maxSpd = maxSpd3;
                            body = body2;
                            prevSpd = prevSpd2;
                        } else {
                            start = start2;
                            hrCnt = hrCnt4;
                            maxSpd = maxSpd3;
                            double maxSpd4 = lastLon;
                            double d = haversine(lastLat, maxSpd4, lat, lon);
                            dist += d;
                            if (lastT <= 0 || t <= lastT) {
                                body = body2;
                                prevSpd = prevSpd2;
                            } else {
                                long dt = t - lastT;
                                body = body2;
                                double spd = d / (dt / 1000.0d);
                                if (spd <= 30.0d && spd >= 0.5d) {
                                    movingMs += dt;
                                }
                                if (prevSpd2 >= 0.0d) {
                                    double sustained = Math.min(spd, prevSpd2);
                                    if ((0.0d <= sustained && sustained <= 25.0d) && sustained > maxSpd) {
                                        maxSpd = sustained;
                                    }
                                }
                                prevSpd = spd;
                            }
                        }
                        lastLat = lat;
                        lastLon = lon;
                        if (t > 0) {
                            lastT = t;
                        }
                        MatchResult matchResultFind$default = Regex.find$default(eleRe, body, 0, 2, null);
                        if (matchResultFind$default != null && (groupValues3 = matchResultFind$default.getGroupValues()) != null && (str3 = groupValues3.get(1)) != null && (doubleOrNull = StringsKt.toDoubleOrNull(str3)) != null) {
                            double e = doubleOrNull.doubleValue();
                            if (Double.isNaN(eleRef)) {
                                eleRef = e;
                            }
                            if (e - eleRef > 1.0d) {
                                ascent += e - eleRef;
                            } else if (e < eleRef) {
                            }
                            eleRef = e;
                        }
                        MatchResult matchResultFind$default2 = Regex.find$default(hrRe, body, 0, 2, null);
                        if (matchResultFind$default2 == null || (groupValues2 = matchResultFind$default2.getGroupValues()) == null || (str2 = groupValues2.get(1)) == null || (intOrNull2 = StringsKt.toIntOrNull(str2)) == null) {
                            hrMax = hrMax2;
                        } else {
                            int it4 = intOrNull2.intValue();
                            if (it4 > 0) {
                                hrSum += (long) it4;
                                hrCnt2 = hrCnt + 1;
                                hrMax = it4 > hrMax2 ? it4 : hrMax2;
                            } else {
                                hrMax = hrMax2;
                                hrCnt2 = hrCnt;
                            }
                            hrCnt = hrCnt2;
                        }
                        MatchResult matchResultFind$default3 = Regex.find$default(powRe, body, 0, 2, null);
                        if (matchResultFind$default3 == null || (groupValues = matchResultFind$default3.getGroupValues()) == null || (str = groupValues.get(1)) == null || (intOrNull = StringsKt.toIntOrNull(str)) == null) {
                            powCnt3 = powCnt4;
                            powMax2 = powMax3;
                            powCnt2 = 0;
                            hrCnt4 = hrCnt;
                            maxSpd3 = maxSpd;
                            it = it2;
                            hrCnt3 = 2;
                            start2 = start;
                        } else {
                            int it5 = intOrNull.intValue();
                            if (it5 > 0) {
                                maxSpd2 = maxSpd;
                                powSum += (long) it5;
                                powCnt = powCnt4 + 1;
                                powMax = powMax3;
                                if (it5 > powMax) {
                                    powMax = it5;
                                }
                            } else {
                                maxSpd2 = maxSpd;
                                powCnt = powCnt4;
                                powMax = powMax3;
                            }
                            powCnt3 = powCnt;
                            powMax2 = powMax;
                            powCnt2 = 0;
                            hrCnt4 = hrCnt;
                            maxSpd3 = maxSpd2;
                            it = it2;
                            hrCnt3 = 2;
                            start2 = start;
                        }
                    }
                }
            }
            powCnt3 = powCnt3;
            powCnt2 = powCnt2;
            powMax2 = powMax2;
            prevSpd = prevSpd;
            start2 = start2;
            hrCnt3 = hrCnt3;
            hrMax = hrMax;
            maxSpd3 = maxSpd3;
            hrCnt4 = hrCnt4;
            it = it;
        }
        int hrCnt5 = hrCnt4;
        long start3 = start2;
        double maxSpd5 = maxSpd3;
        int powMax4 = powMax2;
        int hrMax3 = hrMax;
        int powCnt5 = powCnt3;
        if (dist <= 0.0d && hrCnt5 == 0) {
            return null;
        }
        long elapsed = lastTime > firstT ? lastTime - firstT : 0L;
        long mv = movingMs > 0 ? movingMs : elapsed;
        double avg = mv > 0 ? dist / (mv / 1000.0d) : 0.0d;
        float f3 = (float) avg;
        float f4 = (float) maxSpd5;
        if (hrCnt5 > 0) {
            f2 = f4;
            i = (int) (hrSum / ((long) hrCnt5));
        } else {
            f2 = f4;
            i = 0;
        }
        return new RideSummary(start3, null, dist, mv, f3, f2, i, hrMax3, ascent, powCnt5 > 0 ? (int) (powSum / ((long) powCnt5)) : 0, powMax4, f.getName(), false, null, 8192, null);
    }

    public final long[] zoneTimes(File f, int maxHr) {
        Object objM118constructorimpl;
        Intrinsics.checkNotNullParameter(f, "f");
        try {
            Result.Companion companion = Result.INSTANCE;
            GpxSummary gpxSummary = this;
            objM118constructorimpl = Result.m118constructorimpl(FilesKt.readText$default(f, null, 1, null));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m124isFailureimpl(objM118constructorimpl)) {
            objM118constructorimpl = "";
        }
        return zoneTimes((String) objM118constructorimpl, maxHr);
    }

    public final long[] zoneTimes(String text, int maxHr) {
        Object objM118constructorimpl;
        List<String> groupValues;
        String str;
        Integer intOrNull;
        Intrinsics.checkNotNullParameter(text, "text");
        long[] zones = new long[5];
        long lastT = 0;
        int i = 0;
        int i2 = 2;
        Long l = null;
        for (MatchResult m : Regex.findAll$default(trkptRe, text, 0, 2, null)) {
            String body = m.getGroupValues().get(3);
            MatchResult it = Regex.find$default(timeRe, body, i, i2, l);
            if (it != null) {
                GpxSummary gpxSummary = INSTANCE;
                try {
                    Result.Companion companion = Result.INSTANCE;
                    Date date = iso.parse(it.getGroupValues().get(1));
                    objM118constructorimpl = Result.m118constructorimpl(date != null ? Long.valueOf(date.getTime()) : l);
                } catch (Throwable th) {
                    Result.Companion companion2 = Result.INSTANCE;
                    objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
                }
                if (Result.m124isFailureimpl(objM118constructorimpl)) {
                    objM118constructorimpl = l;
                }
                Long l2 = (Long) objM118constructorimpl;
                if (l2 != null) {
                    long t = l2.longValue();
                    MatchResult matchResultFind$default = Regex.find$default(hrRe, body, 0, i2, l);
                    int hr = (matchResultFind$default == null || (groupValues = matchResultFind$default.getGroupValues()) == null || (str = groupValues.get(1)) == null || (intOrNull = StringsKt.toIntOrNull(str)) == null) ? 0 : intOrNull.intValue();
                    if (lastT > 0 && t > lastT && hr > 0) {
                        long dt = RangesKt.coerceAtMost(t - lastT, 10000L);
                        int z = zoneOf(hr, maxHr);
                        if (z >= 0) {
                            zones[z] = zones[z] + dt;
                        }
                    }
                    lastT = t;
                    i = 0;
                    i2 = 2;
                    l = null;
                }
            }
            i = 0;
            i2 = 2;
            l = null;
        }
        return zones;
    }

    public final int zoneOf(int hr, int maxHr) {
        if (hr <= 0 || maxHr <= 0) {
            return -1;
        }
        double fr = ((double) hr) / ((double) maxHr);
        if (fr >= 0.9d) {
            return 4;
        }
        if (fr >= 0.8d) {
            return 3;
        }
        if (fr >= 0.7d) {
            return 2;
        }
        return fr >= 0.6d ? 1 : 0;
    }

    private final double haversine(double la1, double lo1, double la2, double lo2) {
        double p1 = Math.toRadians(la1);
        double p2 = Math.toRadians(la2);
        double dp = Math.toRadians(la2 - la1);
        double dl = Math.toRadians(lo2 - lo1);
        double d = 2;
        double it = Math.sin(dp / d);
        double dCos = Math.cos(p1) * Math.cos(p2);
        double it2 = Math.sin(dl / d);
        double h = (it * it) + (dCos * it2 * it2);
        return d * 6371000.0d * Math.asin(Math.sqrt(h));
    }
}

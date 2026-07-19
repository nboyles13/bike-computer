package com.bike.computer;

import java.util.Arrays;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import org.maplibre.turf.TurfConstants;

/* JADX INFO: compiled from: Units.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\n\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0006\u001a\u00020\u0005J\u000e\u0010\r\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\u000e\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\bJ\u000e\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0011¨\u0006\u0012"}, d2 = {"Lcom/bike/computer/Units;", "", "<init>", "()V", "mph", "", "mps", TurfConstants.UNIT_MILES, "", TurfConstants.UNIT_METERS, TurfConstants.UNIT_FEET, "fmtSpeed", "", "fmtDist", "fmtFeet", "fmtHms", "ms", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class Units {
    public static final Units INSTANCE = new Units();

    private Units() {
    }

    public final float mph(float mps) {
        return 2.2369363f * mps;
    }

    public final double miles(double meters) {
        return meters / 1609.344d;
    }

    public final double feet(double meters) {
        return 3.2808399d * meters;
    }

    public final String fmtSpeed(float mps) {
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String str = String.format(Locale.US, "%.1f", Arrays.copyOf(new Object[]{Float.valueOf(mph(mps))}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        return str;
    }

    public final String fmtDist(double meters) {
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String str = String.format(Locale.US, "%.2f", Arrays.copyOf(new Object[]{Double.valueOf(miles(meters))}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        return str;
    }

    public final String fmtFeet(double meters) {
        if (meters == 0.0d) {
            return "--";
        }
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String str = String.format(Locale.US, "%,.0f", Arrays.copyOf(new Object[]{Double.valueOf(feet(meters))}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        return str;
    }

    public final String fmtHms(long ms) {
        long s = ms / ((long) 1000);
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        long j = 3600;
        long j2 = 60;
        String str = String.format(Locale.US, "%d:%02d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(s / j), Long.valueOf((s % j) / j2), Long.valueOf(s % j2)}, 3));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        return str;
    }
}

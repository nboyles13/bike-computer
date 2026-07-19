package com.bike.computer;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: RideHistory.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b+\b\u0086\b\u0018\u00002\u00020\u0001B\u0081\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\r\u0012\u0006\u0010\u000f\u001a\u00020\u0007\u0012\u0006\u0010\u0010\u001a\u00020\r\u0012\u0006\u0010\u0011\u001a\u00020\r\u0012\b\u0010\u0012\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0013\u001a\u00020\u0014\u0012\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0016\u0010\u0017J\t\u0010,\u001a\u00020\u0003HÆ\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u0010.\u001a\u00020\u0007HÆ\u0003J\t\u0010/\u001a\u00020\u0003HÆ\u0003J\t\u00100\u001a\u00020\nHÆ\u0003J\t\u00101\u001a\u00020\nHÆ\u0003J\t\u00102\u001a\u00020\rHÆ\u0003J\t\u00103\u001a\u00020\rHÆ\u0003J\t\u00104\u001a\u00020\u0007HÆ\u0003J\t\u00105\u001a\u00020\rHÆ\u0003J\t\u00106\u001a\u00020\rHÆ\u0003J\u000b\u00107\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u00108\u001a\u00020\u0014HÆ\u0003J\u000b\u00109\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u009b\u0001\u0010:\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\u00072\b\b\u0002\u0010\u0010\u001a\u00020\r2\b\b\u0002\u0010\u0011\u001a\u00020\r2\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0013\u001a\u00020\u00142\n\b\u0002\u0010\u0015\u001a\u0004\u0018\u00010\u0005HÆ\u0001J\u0013\u0010;\u001a\u00020\u00142\b\u0010<\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010=\u001a\u00020\rHÖ\u0001J\t\u0010>\u001a\u00020\u0005HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0019R\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u000b\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b!\u0010 R\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010#R\u0011\u0010\u000e\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b$\u0010#R\u0011\u0010\u000f\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001dR\u0011\u0010\u0010\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b&\u0010#R\u0011\u0010\u0011\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b'\u0010#R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001bR\u0011\u0010\u0013\u001a\u00020\u0014¢\u0006\b\n\u0000\u001a\u0004\b)\u0010*R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b+\u0010\u001b¨\u0006?"}, d2 = {"Lcom/bike/computer/RideSummary;", "", "startMs", "", "route", "", "distanceM", "", "movingMs", "avgMps", "", "maxMps", "hrAvg", "", "hrMax", "ascentM", "powerAvg", "powerMax", "gpx", "uploaded", "", "name", "<init>", "(JLjava/lang/String;DJFFIIDIILjava/lang/String;ZLjava/lang/String;)V", "getStartMs", "()J", "getRoute", "()Ljava/lang/String;", "getDistanceM", "()D", "getMovingMs", "getAvgMps", "()F", "getMaxMps", "getHrAvg", "()I", "getHrMax", "getAscentM", "getPowerAvg", "getPowerMax", "getGpx", "getUploaded", "()Z", "getName", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "copy", "equals", "other", "hashCode", "toString", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final /* data */ class RideSummary {
    final double ascentM;
    final float avgMps;
    final double distanceM;
    final String gpx;
    final int hrAvg;
    final int hrMax;
    final float maxMps;
    final long movingMs;
    final String name;
    final int powerAvg;
    final int powerMax;
    final String route;
    final long startMs;
    final boolean uploaded;















    public final RideSummary copy(long startMs, String route, double distanceM, long movingMs, float avgMps, float maxMps, int hrAvg, int hrMax, double ascentM, int powerAvg, int powerMax, String gpx, boolean uploaded, String name) {
        return new RideSummary(startMs, route, distanceM, movingMs, avgMps, maxMps, hrAvg, hrMax, ascentM, powerAvg, powerMax, gpx, uploaded, name);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RideSummary)) {
            return false;
        }
        RideSummary rideSummary = (RideSummary) other;
        return this.startMs == rideSummary.startMs && Intrinsics.areEqual(this.route, rideSummary.route) && Double.compare(this.distanceM, rideSummary.distanceM) == 0 && this.movingMs == rideSummary.movingMs && Float.compare(this.avgMps, rideSummary.avgMps) == 0 && Float.compare(this.maxMps, rideSummary.maxMps) == 0 && this.hrAvg == rideSummary.hrAvg && this.hrMax == rideSummary.hrMax && Double.compare(this.ascentM, rideSummary.ascentM) == 0 && this.powerAvg == rideSummary.powerAvg && this.powerMax == rideSummary.powerMax && Intrinsics.areEqual(this.gpx, rideSummary.gpx) && this.uploaded == rideSummary.uploaded && Intrinsics.areEqual(this.name, rideSummary.name);
    }

    public int hashCode() {
        return (((((((((((((((((((((((((Long.hashCode(this.startMs) * 31) + (this.route == null ? 0 : this.route.hashCode())) * 31) + Double.hashCode(this.distanceM)) * 31) + Long.hashCode(this.movingMs)) * 31) + Float.hashCode(this.avgMps)) * 31) + Float.hashCode(this.maxMps)) * 31) + Integer.hashCode(this.hrAvg)) * 31) + Integer.hashCode(this.hrMax)) * 31) + Double.hashCode(this.ascentM)) * 31) + Integer.hashCode(this.powerAvg)) * 31) + Integer.hashCode(this.powerMax)) * 31) + (this.gpx == null ? 0 : this.gpx.hashCode())) * 31) + Boolean.hashCode(this.uploaded)) * 31) + (this.name != null ? this.name.hashCode() : 0);
    }

    public String toString() {
        return "RideSummary(startMs=" + this.startMs + ", route=" + this.route + ", distanceM=" + this.distanceM + ", movingMs=" + this.movingMs + ", avgMps=" + this.avgMps + ", maxMps=" + this.maxMps + ", hrAvg=" + this.hrAvg + ", hrMax=" + this.hrMax + ", ascentM=" + this.ascentM + ", powerAvg=" + this.powerAvg + ", powerMax=" + this.powerMax + ", gpx=" + this.gpx + ", uploaded=" + this.uploaded + ", name=" + this.name + ")";
    }

    public RideSummary(long startMs, String route, double distanceM, long movingMs, float avgMps, float maxMps, int hrAvg, int hrMax, double ascentM, int powerAvg, int powerMax, String gpx, boolean uploaded, String name) {
        this.startMs = startMs;
        this.route = route;
        this.distanceM = distanceM;
        this.movingMs = movingMs;
        this.avgMps = avgMps;
        this.maxMps = maxMps;
        this.hrAvg = hrAvg;
        this.hrMax = hrMax;
        this.ascentM = ascentM;
        this.powerAvg = powerAvg;
        this.powerMax = powerMax;
        this.gpx = gpx;
        this.uploaded = uploaded;
        this.name = name;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public /* synthetic */ RideSummary(long j, String str, double d, long j2, float f, float f2, int i, int i2, double d2, int i3, int i4, String str2, boolean z, String str3, int i5, DefaultConstructorMarker defaultConstructorMarker) {
        this(j, str, d, j2, f, f2, i, i2, d2, i3, i4, str2, (i5 & 4096) == 0 ? z : false, (i5 & 8192) == 0 ? str3 : null);
    }

    public final long getStartMs() {
        return this.startMs;
    }

    public final String getRoute() {
        return this.route;
    }

    public final double getDistanceM() {
        return this.distanceM;
    }

    public final long getMovingMs() {
        return this.movingMs;
    }

    public final float getAvgMps() {
        return this.avgMps;
    }

    public final float getMaxMps() {
        return this.maxMps;
    }

    public final int getHrAvg() {
        return this.hrAvg;
    }

    public final int getHrMax() {
        return this.hrMax;
    }

    public final double getAscentM() {
        return this.ascentM;
    }

    public final int getPowerAvg() {
        return this.powerAvg;
    }

    public final int getPowerMax() {
        return this.powerMax;
    }

    public final String getGpx() {
        return this.gpx;
    }

    public final boolean getUploaded() {
        return this.uploaded;
    }

    public final String getName() {
        return this.name;
    }
}

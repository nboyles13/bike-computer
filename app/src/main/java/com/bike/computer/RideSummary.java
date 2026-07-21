package com.bike.computer;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

// NOTE: still decompiled Java. Consumers (RideHistory/RideService/MainActivity) access its
// package-private fields directly and use the synthetic default-arg constructor; ported to
// idiomatic Kotlin (data class) only after those consumers are Kotlin. See VALIDATION.md.
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

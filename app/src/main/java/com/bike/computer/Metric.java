package com.bike.computer;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX INFO: compiled from: Prefs.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001a\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B!\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\tj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017j\u0002\b\u0018j\u0002\b\u0019j\u0002\b\u001aj\u0002\b\u001bj\u0002\b\u001c¨\u0006\u001d"}, d2 = {"Lcom/bike/computer/Metric;", "", "label", "", "unit", "short", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getLabel", "()Ljava/lang/String;", "getUnit", "getShort", "SPEED", "AVG_SPEED", "MAX_SPEED", Pages.HR, "AVG_HR", "MAX_HR", "DISTANCE", "RIDE_TIME", "ELEVATION", "ASCENT", "GRADE", "CADENCE", "POWER", "AVG_POWER", "MAX_POWER", "CLOCK", "BATTERY", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public enum Metric {
    SPEED("SPEED", "mph", "SPD"),
    AVG_SPEED("AVG SPEED", "mph", "AVG"),
    MAX_SPEED("MAX SPEED", "mph", "MAX"),
    HR("HEART RATE", "bpm", Pages.HR),
    AVG_HR("AVG HR", "bpm", "AVG"),
    MAX_HR("MAX HR", "bpm", "MAX"),
    DISTANCE("DISTANCE", "mi", "DIST"),
    RIDE_TIME("RIDE TIME", "", "TIME"),
    ELEVATION("ELEVATION", "ft", Pages.ELEV),
    ASCENT("ASCENT", "ft", "ASC"),
    GRADE("INCLINE", "%", "INC"),
    CADENCE("CADENCE", "rpm", "CAD"),
    POWER("POWER", "W", "PWR"),
    AVG_POWER("AVG POWER", "W", "AVG"),
    MAX_POWER("MAX POWER", "W", "MAX"),
    CLOCK("TIME OF DAY", "", "CLOCK"),
    BATTERY("BATTERY", "%", "BATT");

    private final String label;
    private final String short;
    private final String unit;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

    Metric(String label, String unit, String str) {
        this.label = label;
        this.unit = unit;
        this.short = str;
    }

    public final String getLabel() {
        return this.label;
    }

    public final String getShort() {
        return this.short;
    }

    public final String getUnit() {
        return this.unit;
    }

    public static EnumEntries<Metric> getEntries() {
        return $ENTRIES;
    }
}

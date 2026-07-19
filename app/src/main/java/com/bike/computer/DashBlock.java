package com.bike.computer;

import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX INFO: compiled from: Prefs.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0010\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B/\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\b¢\u0006\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015j\u0002\b\u0016j\u0002\b\u0017¨\u0006\u0018"}, d2 = {"Lcom/bike/computer/DashBlock;", "", "label", "", "unit", "primary", "Lcom/bike/computer/Metric;", "secondary", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;Lcom/bike/computer/Metric;Ljava/util/List;)V", "getLabel", "()Ljava/lang/String;", "getUnit", "getPrimary", "()Lcom/bike/computer/Metric;", "getSecondary", "()Ljava/util/List;", "SPEED", Pages.HR, "DISTANCE", "RIDE_TIME", "ELEVATION", "INCLINE", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public enum DashBlock {
    SPEED("SPEED", "mph", Metric.SPEED, CollectionsKt.listOf((Object[]) new Metric[]{Metric.AVG_SPEED, Metric.MAX_SPEED})),
    HR("HEART RATE", "bpm", Metric.HR, CollectionsKt.listOf((Object[]) new Metric[]{Metric.AVG_HR, Metric.MAX_HR})),
    DISTANCE("DISTANCE", "mi", Metric.DISTANCE, CollectionsKt.emptyList()),
    RIDE_TIME("RIDE TIME", "", Metric.RIDE_TIME, CollectionsKt.emptyList()),
    ELEVATION("ELEVATION", "ft", Metric.ELEVATION, CollectionsKt.listOf(Metric.ASCENT)),
    INCLINE("INCLINE", "%", Metric.GRADE, CollectionsKt.emptyList());

    private final String label;
    private final Metric primary;
    private final List<Metric> secondary;
    private final String unit;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries($VALUES);

    DashBlock(String label, String unit, Metric primary, List secondary) {
        this.label = label;
        this.unit = unit;
        this.primary = primary;
        this.secondary = secondary;
    }

    public final String getLabel() {
        return this.label;
    }

    public final Metric getPrimary() {
        return this.primary;
    }

    public final List<Metric> getSecondary() {
        return this.secondary;
    }

    public final String getUnit() {
        return this.unit;
    }

    public static EnumEntries<DashBlock> getEntries() {
        return $ENTRIES;
    }
}

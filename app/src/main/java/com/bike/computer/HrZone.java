package com.bike.computer;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX INFO: compiled from: Prefs.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u000e\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B!\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014¨\u0006\u0015"}, d2 = {"Lcom/bike/computer/HrZone;", "", "label", "", "lowerPct", "", "color", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;IJ)V", "getLabel", "()Ljava/lang/String;", "getLowerPct", "()I", "getColor", "()J", "Z1", "Z2", "Z3", "Z4", "Z5", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public enum HrZone {
    Z1("Warm Up", 50, 4287532691L),
    Z2("Easy", 60, 4283207167L),
    Z3("Aerobic", 70, 4281389400L),
    Z4("Threshold", 80, 4294942474L),
    Z5("Maximum", 90, 4294919482L);

    private final long color;
    private final String label;
    private final int lowerPct;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());

    HrZone(String label, int lowerPct, long color) {
        this.label = label;
        this.lowerPct = lowerPct;
        this.color = color;
    }

    public final long getColor() {
        return this.color;
    }

    public final String getLabel() {
        return this.label;
    }

    public final int getLowerPct() {
        return this.lowerPct;
    }

    public static EnumEntries<HrZone> getEntries() {
        return $ENTRIES;
    }
}

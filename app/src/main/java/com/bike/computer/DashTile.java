package com.bike.computer;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Prefs.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u001a\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0005¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0005HÆ\u0003J;\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u00052\b\b\u0002\u0010\b\u001a\u00020\u0005HÆ\u0001J\u0013\u0010\u001f\u001a\u00020 2\b\u0010!\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\"\u001a\u00020\u0005HÖ\u0001J\t\u0010#\u001a\u00020$HÖ\u0001R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001a\u0010\u0006\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0010\"\u0004\b\u0014\u0010\u0012R\u001a\u0010\u0007\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012R\u001a\u0010\b\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0010\"\u0004\b\u0018\u0010\u0012¨\u0006%"}, d2 = {"Lcom/bike/computer/DashTile;", "", "metric", "Lcom/bike/computer/Metric;", "col", "", "row", "w", "h", "<init>", "(Lcom/bike/computer/Metric;IIII)V", "getMetric", "()Lcom/bike/computer/Metric;", "setMetric", "(Lcom/bike/computer/Metric;)V", "getCol", "()I", "setCol", "(I)V", "getRow", "setRow", "getW", "setW", "getH", "setH", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final /* data */ class DashTile {
    private int col;
    private int h;
    private Metric metric;
    private int row;
    private int w;

    public static /* synthetic */ DashTile copy$default(DashTile dashTile, Metric metric, int i, int i2, int i3, int i4, int i5, Object obj) {
        if ((i5 & 1) != 0) {
            metric = dashTile.metric;
        }
        if ((i5 & 2) != 0) {
            i = dashTile.col;
        }
        int i6 = i;
        if ((i5 & 4) != 0) {
            i2 = dashTile.row;
        }
        int i7 = i2;
        if ((i5 & 8) != 0) {
            i3 = dashTile.w;
        }
        int i8 = i3;
        if ((i5 & 16) != 0) {
            i4 = dashTile.h;
        }
        return dashTile.copy(metric, i6, i7, i8, i4);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final Metric getMetric() {
        return this.metric;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final int getCol() {
        return this.col;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final int getRow() {
        return this.row;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final int getW() {
        return this.w;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final int getH() {
        return this.h;
    }

    public final DashTile copy(Metric metric, int col, int row, int w, int h) {
        Intrinsics.checkNotNullParameter(metric, "metric");
        return new DashTile(metric, col, row, w, h);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DashTile)) {
            return false;
        }
        DashTile dashTile = (DashTile) other;
        return this.metric == dashTile.metric && this.col == dashTile.col && this.row == dashTile.row && this.w == dashTile.w && this.h == dashTile.h;
    }

    public int hashCode() {
        return (((((((this.metric.hashCode() * 31) + Integer.hashCode(this.col)) * 31) + Integer.hashCode(this.row)) * 31) + Integer.hashCode(this.w)) * 31) + Integer.hashCode(this.h);
    }

    public String toString() {
        return "DashTile(metric=" + this.metric + ", col=" + this.col + ", row=" + this.row + ", w=" + this.w + ", h=" + this.h + ")";
    }

    public DashTile(Metric metric, int col, int row, int w, int h) {
        Intrinsics.checkNotNullParameter(metric, "metric");
        this.metric = metric;
        this.col = col;
        this.row = row;
        this.w = w;
        this.h = h;
    }

    public final int getCol() {
        return this.col;
    }

    public final int getH() {
        return this.h;
    }

    public final Metric getMetric() {
        return this.metric;
    }

    public final int getRow() {
        return this.row;
    }

    public final int getW() {
        return this.w;
    }

    public final void setCol(int i) {
        this.col = i;
    }

    public final void setH(int i) {
        this.h = i;
    }

    public final void setMetric(Metric metric) {
        Intrinsics.checkNotNullParameter(metric, "<set-?>");
        this.metric = metric;
    }

    public final void setRow(int i) {
        this.row = i;
    }

    public final void setW(int i) {
        this.w = i;
    }
}

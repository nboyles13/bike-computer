package com.bike.computer;

import kotlin.jvm.internal.Intrinsics;

// NOTE: still decompiled Java. Consumers use the synthetic copy$default(...); ported to an
// idiomatic Kotlin data class only after those consumers are Kotlin. See VALIDATION.md.
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

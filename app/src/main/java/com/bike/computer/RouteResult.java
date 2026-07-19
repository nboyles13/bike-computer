package com.bike.computer;

import btools.router.NavHint;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: BikeRouter.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0010\u0013\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B3\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0003¢\u0006\u0004\b\n\u0010\u000bJ\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0006HÆ\u0003J\u000f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\t0\u0003HÆ\u0003J=\u0010\u0016\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0003HÆ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001a\u001a\u00020\u0006HÖ\u0001J\t\u0010\u001b\u001a\u00020\u001cHÖ\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\r¨\u0006\u001d"}, d2 = {"Lcom/bike/computer/RouteResult;", "", "points", "", "", "distanceM", "", "ascendM", "steps", "Lbtools/router/NavHint;", "<init>", "(Ljava/util/List;IILjava/util/List;)V", "getPoints", "()Ljava/util/List;", "getDistanceM", "()I", "getAscendM", "getSteps", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final /* data */ class RouteResult {
    private final int ascendM;
    private final int distanceM;
    private final List<double[]> points;
    private final List<NavHint> steps;

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ RouteResult copy$default(RouteResult routeResult, List list, int i, int i2, List list2, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            list = routeResult.points;
        }
        if ((i3 & 2) != 0) {
            i = routeResult.distanceM;
        }
        if ((i3 & 4) != 0) {
            i2 = routeResult.ascendM;
        }
        if ((i3 & 8) != 0) {
            list2 = routeResult.steps;
        }
        return routeResult.copy(list, i, i2, list2);
    }

    public final List<double[]> component1() {
        return this.points;
    }



    public final List<NavHint> component4() {
        return this.steps;
    }

    public final RouteResult copy(List<double[]> points, int distanceM, int ascendM, List<NavHint> steps) {
        Intrinsics.checkNotNullParameter(points, "points");
        Intrinsics.checkNotNullParameter(steps, "steps");
        return new RouteResult(points, distanceM, ascendM, steps);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RouteResult)) {
            return false;
        }
        RouteResult routeResult = (RouteResult) other;
        return Intrinsics.areEqual(this.points, routeResult.points) && this.distanceM == routeResult.distanceM && this.ascendM == routeResult.ascendM && Intrinsics.areEqual(this.steps, routeResult.steps);
    }

    public int hashCode() {
        return (((((this.points.hashCode() * 31) + Integer.hashCode(this.distanceM)) * 31) + Integer.hashCode(this.ascendM)) * 31) + this.steps.hashCode();
    }

    public String toString() {
        return "RouteResult(points=" + this.points + ", distanceM=" + this.distanceM + ", ascendM=" + this.ascendM + ", steps=" + this.steps + ")";
    }

    public RouteResult(List<double[]> points, int distanceM, int ascendM, List<NavHint> steps) {
        Intrinsics.checkNotNullParameter(points, "points");
        Intrinsics.checkNotNullParameter(steps, "steps");
        this.points = points;
        this.distanceM = distanceM;
        this.ascendM = ascendM;
        this.steps = steps;
    }

    public final int getAscendM() {
        return this.ascendM;
    }

    public final int getDistanceM() {
        return this.distanceM;
    }

    public final List<double[]> getPoints() {
        return this.points;
    }

    public final List<NavHint> getSteps() {
        return this.steps;
    }
}

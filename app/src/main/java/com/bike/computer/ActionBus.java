package com.bike.computer;

import java.util.List;
import kotlin.Metadata;

/* JADX INFO: compiled from: Prefs.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010 \n\u0002\u0010\u0013\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\n\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\u0007\"\u0004\b\f\u0010\tR\u001a\u0010\r\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u0007\"\u0004\b\u000f\u0010\tR\"\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u0012\u0018\u00010\u0011X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u001c\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u001c\u0010\u001d\u001a\u0004\u0018\u00010\u0012X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001e\u0010\u001f\"\u0004\b \u0010!¨\u0006\""}, d2 = {"Lcom/bike/computer/ActionBus;", "", "<init>", "()V", "stopNav", "", "getStopNav", "()Z", "setStopNav", "(Z)V", "rescanSensor", "getRescanSensor", "setRescanSensor", "navigating", "getNavigating", "setNavigating", "pendingRoute", "", "", "getPendingRoute", "()Ljava/util/List;", "setPendingRoute", "(Ljava/util/List;)V", "pendingRouteName", "", "getPendingRouteName", "()Ljava/lang/String;", "setPendingRouteName", "(Ljava/lang/String;)V", "pendingDestination", "getPendingDestination", "()[D", "setPendingDestination", "([D)V", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class ActionBus {
    public static final ActionBus INSTANCE = new ActionBus();
    private static volatile boolean navigating;
    private static volatile double[] pendingDestination;
    private static volatile List<double[]> pendingRoute;
    private static volatile String pendingRouteName;
    private static volatile boolean rescanSensor;
    private static volatile boolean stopNav;

    private ActionBus() {
    }

    public final boolean getStopNav() {
        return stopNav;
    }

    public final void setStopNav(boolean z) {
        stopNav = z;
    }

    public final boolean getRescanSensor() {
        return rescanSensor;
    }

    public final void setRescanSensor(boolean z) {
        rescanSensor = z;
    }

    public final boolean getNavigating() {
        return navigating;
    }

    public final void setNavigating(boolean z) {
        navigating = z;
    }

    public final List<double[]> getPendingRoute() {
        return pendingRoute;
    }

    public final void setPendingRoute(List<double[]> list) {
        pendingRoute = list;
    }

    public final String getPendingRouteName() {
        return pendingRouteName;
    }

    public final void setPendingRouteName(String str) {
        pendingRouteName = str;
    }

    public final double[] getPendingDestination() {
        return pendingDestination;
    }

    public final void setPendingDestination(double[] dArr) {
        pendingDestination = dArr;
    }
}

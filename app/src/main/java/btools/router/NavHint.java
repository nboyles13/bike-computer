package btools.router;

import kotlin.jvm.internal.Intrinsics;

public final class NavHint {
    public final String cmd;
    public final int indexInTrack;
    public final double lat;
    public final double lon;
    public final String street;

    public NavHint(double lat, double lon, int indexInTrack, String cmd, String street) {
        Intrinsics.checkNotNullParameter(cmd, "cmd");
        Intrinsics.checkNotNullParameter(street, "street");
        this.lat = lat;
        this.lon = lon;
        this.indexInTrack = indexInTrack;
        this.cmd = cmd;
        this.street = street;
    }
}

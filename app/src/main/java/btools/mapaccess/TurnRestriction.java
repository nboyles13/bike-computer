package btools.mapaccess;

/* JADX INFO: loaded from: classes.dex */
public final class TurnRestriction {
    public short exceptions;
    public int fromLat;
    public int fromLon;
    public boolean isPositive;
    public TurnRestriction next;
    public int toLat;
    public int toLon;

    public boolean exceptBikes() {
        return (this.exceptions & 1) != 0;
    }

    public boolean exceptMotorcars() {
        return (this.exceptions & 2) != 0;
    }

    public static boolean isTurnForbidden(TurnRestriction first, int fromLon, int fromLat, int toLon, int toLat, boolean bikeMode, boolean carMode) {
        boolean hasAnyPositive = false;
        boolean hasPositive = false;
        boolean hasNegative = false;
        TurnRestriction tr = first;
        while (tr != null) {
            if ((tr.exceptBikes() && bikeMode) || (tr.exceptMotorcars() && carMode)) {
                tr = tr.next;
            } else {
                if (tr.fromLon == fromLon && tr.fromLat == fromLat) {
                    if (tr.isPositive) {
                        hasAnyPositive = true;
                    }
                    if (tr.toLon == toLon && tr.toLat == toLat) {
                        if (tr.isPositive) {
                            hasPositive = true;
                        } else {
                            hasNegative = true;
                        }
                    }
                }
                tr = tr.next;
            }
        }
        return !hasPositive && (hasAnyPositive || hasNegative);
    }

    public String toString() {
        return "pos=" + this.isPositive + " fromLon=" + this.fromLon + " fromLat=" + this.fromLat + " toLon=" + this.toLon + " toLat=" + this.toLat;
    }
}

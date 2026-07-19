package btools.router;

import btools.expressions.BExpressionContext;

/* JADX INFO: loaded from: classes.dex */
public class AreaInfo {
    static final int RESULT_TYPE_ELEV50 = 1;
    static final int RESULT_TYPE_GREEN = 4;
    static final int RESULT_TYPE_NONE = 0;
    static final int RESULT_TYPE_RIVER = 5;
    public int direction;
    public OsmNogoPolygon polygon;
    public int numForest = -1;
    public int numRiver = -1;
    public int ways = 0;
    public int greenWays = 0;
    public int riverWays = 0;
    public double elevStart = 0.0d;
    public int elev50 = 0;

    public AreaInfo(int dir) {
        this.direction = dir;
    }

    void checkAreaInfo(BExpressionContext expctxWay, double elev, byte[] ab) {
        this.ways++;
        double test = this.elevStart - elev;
        if (Math.abs(test) < 50.0d) {
            this.elev50++;
        }
        int[] ld2 = expctxWay.createNewLookupData();
        expctxWay.decode(ld2, false, ab);
        if (this.numForest != -1 && ld2[this.numForest] > 1) {
            this.greenWays++;
        }
        if (this.numRiver != -1 && ld2[this.numRiver] > 1) {
            this.riverWays++;
        }
    }

    public int getElev50Weight() {
        if (this.ways == 0) {
            return 0;
        }
        return (int) ((((double) this.elev50) * 100.0d) / ((double) this.ways));
    }

    public int getGreen() {
        if (this.ways == 0) {
            return 0;
        }
        return (int) ((((double) this.greenWays) * 100.0d) / ((double) this.ways));
    }

    public int getRiver() {
        if (this.ways == 0) {
            return 0;
        }
        return (int) ((((double) this.riverWays) * 100.0d) / ((double) this.ways));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Area ").append(this.direction).append(" ").append(this.elevStart).append("m ways ").append(this.ways);
        if (this.ways > 0) {
            sb.append("\nArea ways <50m  ").append(this.elev50).append(" ").append(getElev50Weight()).append("%");
            sb.append("\nArea ways green ").append(this.greenWays).append(" ").append(getGreen()).append("%");
            sb.append("\nArea ways river ").append(this.riverWays).append(" ").append(getRiver()).append("%");
        }
        return sb.toString();
    }
}

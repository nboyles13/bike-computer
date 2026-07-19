package btools.mapcreator;

import btools.util.ReducedMedianFilter;

/* JADX INFO: loaded from: classes.dex */
public class ElevationRaster {
    private static double filterCenterFraction;
    private static double filterDiscRadius;
    public double cellsize;
    public short[] eval_array;
    public boolean halfcol;
    public int ncols;
    public short noDataValue;
    public int nrows;
    public double xllcorner;
    public double yllcorner;
    private static int gridSteps = 10;
    private static Weights[][][] allShiftWeights = new Weights[17][][];
    public boolean usingWeights = false;
    private boolean missingData = false;
    private ReducedMedianFilter rmf = new ReducedMedianFilter(256);

    public short getElevation(int ilon, int ilat) {
        double lon = (((double) ilon) / 1000000.0d) - 180.0d;
        double lat = (((double) ilat) / 1000000.0d) - 90.0d;
        if (!this.usingWeights) {
            double dcol = ((lon - this.xllcorner) / this.cellsize) - 0.5d;
            double drow = ((lat - this.yllcorner) / this.cellsize) - 0.5d;
            int row = (int) drow;
            int col = (int) dcol;
            if (col < 0) {
                col = 0;
            }
            if (col >= this.ncols - 1) {
                col = this.ncols - 2;
            }
            if (row < 0) {
                row = 0;
            }
            if (row >= this.nrows - 1) {
                row = this.nrows - 2;
            }
            double wrow = drow - ((double) row);
            double wcol = dcol - ((double) col);
            this.missingData = false;
            double eval = ((1.0d - wrow) * (1.0d - wcol) * ((double) get(row, col))) + ((1.0d - wcol) * wrow * ((double) get(row + 1, col))) + ((1.0d - wrow) * wcol * ((double) get(row, col + 1))) + (wrow * wcol * ((double) get(row + 1, col + 1)));
            if (this.missingData) {
                return Short.MIN_VALUE;
            }
            return (short) (4.0d * eval);
        }
        return getElevationFromShiftWeights(lon, lat);
    }

    private short get(int r, int c) {
        short e = this.eval_array[(((this.nrows - 1) - r) * this.ncols) + c];
        if (e == Short.MIN_VALUE) {
            this.missingData = true;
        }
        return e;
    }

    private short getElevationFromShiftWeights(double lon, double lat) {
        double alat = (lat < 0.0d ? -lat : lat) / 5.0d;
        int latIdx = (int) alat;
        double wlat = alat - ((double) latIdx);
        double dcol = (lon - this.xllcorner) / this.cellsize;
        double drow = (lat - this.yllcorner) / this.cellsize;
        int row = (int) drow;
        int col = (int) dcol;
        double dgx = (dcol - ((double) col)) * ((double) gridSteps);
        double dgy = (drow - ((double) row)) * ((double) gridSteps);
        int gx = (int) dgx;
        int gy = (int) dgy;
        double wx = dgx - ((double) gx);
        double wy = dgy - ((double) gy);
        double w00 = (1.0d - wx) * (1.0d - wy);
        double w01 = (1.0d - wx) * wy;
        double w10 = (1.0d - wy) * wx;
        double w11 = wx * wy;
        Weights[][] w0 = getWeights(latIdx);
        Weights[][] w1 = getWeights(latIdx + 1);
        this.missingData = false;
        double m0 = (getElevation(w0[gx][gy], row, col) * w00) + (getElevation(w0[gx][gy + 1], row, col) * w01) + (getElevation(w0[gx + 1][gy], row, col) * w10) + (getElevation(w0[gx + 1][gy + 1], row, col) * w11);
        double m1 = (getElevation(w1[gx][gy], row, col) * w00) + (getElevation(w1[gx][gy + 1], row, col) * w01) + (getElevation(w1[gx + 1][gy], row, col) * w10) + (getElevation(w1[gx + 1][gy + 1], row, col) * w11);
        if (this.missingData) {
            return Short.MIN_VALUE;
        }
        double m = ((1.0d - wlat) * m0) + (wlat * m1);
        return (short) (m * 2.0d);
    }

    private double getElevation(Weights w, int row, int col) {
        if (this.missingData) {
            return 0.0d;
        }
        int nx = w.nx;
        int ny = w.ny;
        int mx = nx / 2;
        int my = ny / 2;
        this.rmf.reset();
        for (int ix = 0; ix < nx; ix++) {
            for (int iy = 0; iy < ny; iy++) {
                short val = get((row + iy) - my, (col + ix) - mx);
                this.rmf.addSample(w.getWeight(ix, iy), val);
            }
        }
        if (this.missingData) {
            return 0.0d;
        }
        return this.rmf.calcEdgeReducedMedian(filterCenterFraction);
    }

    private static class Weights {
        int nx;
        int ny;
        long total = 0;
        double[] weights;

        Weights(int nx, int ny) {
            this.nx = nx;
            this.ny = ny;
            this.weights = new double[nx * ny];
        }

        void inc(int ix, int iy) {
            double[] dArr = this.weights;
            int i = (this.nx * iy) + ix;
            dArr[i] = dArr[i] + 1.0d;
            this.total++;
        }

        void normalize(boolean verbose) {
            for (int iy = 0; iy < this.ny; iy++) {
                StringBuilder sb = verbose ? new StringBuilder() : null;
                for (int ix = 0; ix < this.nx; ix++) {
                    double[] dArr = this.weights;
                    int i = (this.nx * iy) + ix;
                    dArr[i] = dArr[i] / this.total;
                    if (sb != null) {
                        int iweight = (int) ((this.weights[(this.nx * iy) + ix] * 1000.0d) + 0.5d);
                        String sval = "     " + iweight;
                        sb.append(sval.substring(sval.length() - 4));
                    }
                }
                if (sb != null) {
                    System.out.println(sb);
                    System.out.println();
                }
            }
        }

        double getWeight(int ix, int iy) {
            return this.weights[(this.nx * iy) + ix];
        }
    }

    static {
        filterCenterFraction = 0.2d;
        filterDiscRadius = 4.999d;
        String sRadius = System.getProperty("filterDiscRadius");
        if (sRadius != null && sRadius.length() > 0) {
            filterDiscRadius = Integer.parseInt(sRadius);
            System.out.println("using filterDiscRadius = " + filterDiscRadius);
        }
        String sFraction = System.getProperty("filterCenterFraction");
        if (sFraction != null && sFraction.length() > 0) {
            filterCenterFraction = ((double) Integer.parseInt(sFraction)) / 100.0d;
            System.out.println("using filterCenterFraction = " + filterCenterFraction);
        }
    }

    private static Weights[][] getWeights(int latIndex) {
        int idx = latIndex < 16 ? latIndex : 16;
        Weights[][] res = allShiftWeights[idx];
        if (res == null) {
            Weights[][] res2 = calcWeights(idx);
            allShiftWeights[idx] = res2;
            return res2;
        }
        return res;
    }

    private static Weights[][] calcWeights(int latIndex) {
        double ry;
        double coslat = Math.cos((((double) latIndex) * 5.0d) / 57.3d);
        double ry2 = filterDiscRadius;
        double rx = ry2 / coslat;
        int nx = (((int) rx) * 2) + 3;
        int ny = (((int) ry2) * 2) + 3;
        System.out.println("nx=" + nx + " ny=" + ny);
        int mx = nx / 2;
        int my = ny / 2;
        int i = 1;
        Weights[][] shiftWeights = new Weights[gridSteps + 1][];
        int gx = 0;
        while (gx <= gridSteps) {
            shiftWeights[gx] = new Weights[gridSteps + i];
            double coslat2 = coslat;
            int gx2 = gx;
            double x0 = ((double) mx) + (((double) gx) / ((double) gridSteps));
            int gy = 0;
            while (gy <= gridSteps) {
                int mx2 = mx;
                int mx3 = gridSteps;
                int my2 = my;
                double y0 = ((double) my) + (((double) gy) / ((double) mx3));
                Weights weights = new Weights(nx, ny);
                shiftWeights[gx2][gy] = weights;
                double x = (0.001d / 2.0d) - 1.0d;
                while (x < 1.0d) {
                    double mx22 = 1.0d - (x * x);
                    double rx2 = rx;
                    int x_idx = (int) (x0 + (x * rx));
                    double y = (0.001d / 2.0d) - 1.0d;
                    while (y < 1.0d) {
                        if (y * y > mx22) {
                            ry = ry2;
                        } else {
                            ry = ry2;
                            int y_idx = (int) (y0 + (y * ry2));
                            weights.inc(x_idx, y_idx);
                        }
                        y += 0.001d;
                        ry2 = ry;
                    }
                    x += 0.001d;
                    rx = rx2;
                }
                weights.normalize(true);
                gy++;
                mx = mx2;
                my = my2;
                ry2 = ry2;
            }
            gx = gx2 + 1;
            i = 1;
            coslat = coslat2;
            ry2 = ry2;
        }
        return shiftWeights;
    }

    public String toString() {
        return this.ncols + "," + this.nrows + "," + this.halfcol + "," + this.xllcorner + "," + this.yllcorner + "," + this.cellsize + "," + ((int) this.noDataValue) + "," + this.usingWeights;
    }
}

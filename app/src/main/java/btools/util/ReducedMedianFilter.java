package btools.util;

/* JADX INFO: loaded from: classes.dex */
public final class ReducedMedianFilter {
    private int nsamples;
    private int[] values;
    private double[] weights;

    public ReducedMedianFilter(int size) {
        this.weights = new double[size];
        this.values = new int[size];
    }

    public void reset() {
        this.nsamples = 0;
    }

    public void addSample(double weight, int value) {
        if (weight > 0.0d) {
            for (int i = 0; i < this.nsamples; i++) {
                if (this.values[i] == value) {
                    double[] dArr = this.weights;
                    dArr[i] = dArr[i] + weight;
                    return;
                }
            }
            this.weights[this.nsamples] = weight;
            this.values[this.nsamples] = value;
            this.nsamples++;
        }
    }

    public double calcEdgeReducedMedian(double fraction) {
        removeEdgeWeight((1.0d - fraction) / 2.0d, true);
        removeEdgeWeight((1.0d - fraction) / 2.0d, false);
        double totalWeight = 0.0d;
        double totalValue = 0.0d;
        for (int i = 0; i < this.nsamples; i++) {
            double w = this.weights[i];
            totalWeight += w;
            totalValue += ((double) this.values[i]) * w;
        }
        return totalValue / totalWeight;
    }

    private void removeEdgeWeight(double excessWeight, boolean high) {
        while (excessWeight > 0.0d) {
            double totalWeight = 0.0d;
            int minmax = 0;
            for (int i = 0; i < this.nsamples; i++) {
                double w = this.weights[i];
                if (w > 0.0d) {
                    int v = this.values[i];
                    if (totalWeight == 0.0d || (!high ? v < minmax : v > minmax)) {
                        minmax = v;
                    }
                    totalWeight += w;
                }
            }
            if (totalWeight < excessWeight) {
                throw new IllegalArgumentException("ups, not enough weight to remove");
            }
            for (int i2 = 0; i2 < this.nsamples; i2++) {
                if (this.values[i2] == minmax && this.weights[i2] > 0.0d) {
                    if (excessWeight > this.weights[i2]) {
                        excessWeight -= this.weights[i2];
                        this.weights[i2] = 0.0d;
                    } else {
                        double[] dArr = this.weights;
                        dArr[i2] = dArr[i2] - excessWeight;
                        excessWeight = 0.0d;
                    }
                }
            }
        }
    }
}

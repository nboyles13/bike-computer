package btools.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
public class Polygon {
    private int[] ax;
    private int[] ay;
    private int maxx;
    private int maxy;
    private int minx;
    private int miny;

    public Polygon(BufferedReader br) throws IOException {
        this.minx = Integer.MAX_VALUE;
        this.miny = Integer.MAX_VALUE;
        this.maxx = Integer.MIN_VALUE;
        this.maxy = Integer.MIN_VALUE;
        List<String> lines = new ArrayList<>();
        while (true) {
            String line = br.readLine();
            if (line == null || "END".equals(line)) {
                break;
            } else {
                lines.add(line);
            }
        }
        int n = lines.size();
        this.ax = new int[n];
        this.ay = new int[n];
        for (int i = 0; i < n; i++) {
            StringTokenizer tk = new StringTokenizer(lines.get(i));
            double lon = Double.parseDouble(tk.nextToken());
            double lat = Double.parseDouble(tk.nextToken());
            int x = (int) ((lon * 1000000.0d) + 1.8E8d);
            this.ax[i] = x;
            int y = (int) ((1000000.0d * lat) + 9.0E7d);
            this.ay[i] = y;
            if (x < this.minx) {
                this.minx = x;
            }
            if (y < this.miny) {
                this.miny = y;
            }
            if (x > this.maxx) {
                this.maxx = x;
            }
            if (y > this.maxy) {
                this.maxy = y;
            }
        }
    }

    public boolean isInPolygon(long id) {
        int x = (int) (id >> 32);
        int y = (int) (id & (-1));
        if (x < this.minx || x > this.maxx || y < this.miny || y > this.maxy) {
            return false;
        }
        int n = this.ax.length - 1;
        boolean inside = false;
        int j = n - 1;
        for (int i = 0; i < n; i++) {
            if ((this.ay[i] > y) != (this.ay[j] > y)) {
                long v = this.ax[j] - this.ax[i];
                if (x <= ((long) this.ax[i]) + ((v * ((long) (y - this.ay[i]))) / ((long) (this.ay[j] - this.ay[i])))) {
                    inside = !inside;
                }
            }
            j = i;
        }
        return inside;
    }

    public boolean isInBoundingBox(long id) {
        int x = (int) (id >> 32);
        int y = (int) ((-1) & id);
        return x >= this.minx && x <= this.maxx && y >= this.miny && y <= this.maxy;
    }
}

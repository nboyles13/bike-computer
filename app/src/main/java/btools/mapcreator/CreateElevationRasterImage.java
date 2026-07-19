package btools.mapcreator;

import androidx.core.location.LocationRequestCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import kotlin.jvm.internal.ShortCompanionObject;

/* JADX INFO: loaded from: classes.dex */
public class CreateElevationRasterImage {
    static final boolean DEBUG = false;
    Map<Short, Color> colorMap;
    int[] data;
    int lastSrtmLatIdx;
    int lastSrtmLonIdx;
    ElevationRaster lastSrtmRaster;
    short maxElev = Short.MIN_VALUE;
    short minElev = ShortCompanionObject.MAX_VALUE;
    boolean missingData;
    String srtmdir;
    Map<String, ElevationRaster> srtmmap;

    private void createImage(double lon, double lat, String dir, String imageName, int maxX, int maxY, int downscale, String format, String colors) throws Exception {
        int rgb;
        this.srtmdir = dir;
        if (colors != null) {
            loadColors(colors);
        }
        if (format.equals("hgt")) {
            createImageFromHgt(lon, lat, dir, imageName, maxX, maxY);
            return;
        }
        if (format.equals("bef")) {
            this.srtmmap = new HashMap();
            this.lastSrtmLonIdx = -1;
            this.lastSrtmLatIdx = -1;
            this.lastSrtmRaster = null;
            NodeData n = new NodeData(1L, lon, lat);
            ElevationRaster srtm = srtmForNode(n.ilon, n.ilat);
            if (srtm == null) {
                System.out.println("no data");
                return;
            }
            System.out.println("srtm " + srtm.toString());
            double[] pos = getElevationPos(srtm, n.ilon, n.ilat);
            short[] sArr = srtm.eval_array;
            int rasterX = srtm.ncols;
            int rasterY = srtm.nrows;
            int i = 1000 / downscale;
            int[] imgraster = new int[maxX * maxY];
            for (int y = 0; y < maxY; y++) {
                for (int x = 0; x < maxX; x++) {
                    short e = get(srtm, ((int) Math.round(pos[0])) + (maxY - y), ((int) Math.round(pos[1])) + x);
                    if (e != Short.MIN_VALUE && e < this.minElev) {
                        this.minElev = e;
                    }
                    if (e != Short.MIN_VALUE && e > this.maxElev) {
                        this.maxElev = e;
                    }
                    if (e != Short.MIN_VALUE) {
                        imgraster[(maxY * y) + x] = getColorForHeight(e);
                    } else {
                        imgraster[(maxY * y) + x] = 65535;
                    }
                }
            }
            System.out.println("srtm target " + maxX + " " + maxY + " (" + rasterX + " " + rasterY + ")  min " + ((int) this.minElev) + " max " + ((int) this.maxElev));
            BufferedImage argbImage = new BufferedImage(maxX, maxY, 2);
            this.data = argbImage.getRaster().getDataBuffer().getData();
            for (int y2 = 0; y2 < maxY; y2++) {
                for (int x2 = 0; x2 < maxX; x2++) {
                    int v0 = imgraster[(maxX * y2) + x2];
                    if (v0 != 65535) {
                        rgb = (-16777216) | v0;
                    } else {
                        rgb = ViewCompat.MEASURED_STATE_MASK;
                    }
                    this.data[(y2 * maxX) + x2] = rgb;
                }
            }
            ImageIO.write(argbImage, "png", new FileOutputStream(imageName));
            return;
        }
        System.out.println("wrong format (bef|hgt)");
    }

    private void createImageFromHgt(double lon, double lat, String dir, String imageName, int maxX, int maxY) throws Exception {
        int rgb;
        HgtReader rdr = new HgtReader(dir);
        short[] data = HgtReader.getElevationDataFromHgt(lat, lon);
        if (data == null) {
            System.out.println("no data");
            return;
        }
        int size = data != null ? data.length : 0;
        int rowlen = (int) Math.sqrt(size);
        int[] imgraster = new int[maxX * maxY];
        for (int y = 0; y < maxY; y++) {
            int x = 0;
            while (x < maxX) {
                short e = data[(rowlen * y) + x];
                HgtReader rdr2 = rdr;
                if (e != Short.MIN_VALUE && e < this.minElev) {
                    this.minElev = e;
                }
                if (e != Short.MIN_VALUE && e > this.maxElev) {
                    this.maxElev = e;
                }
                if (e == Short.MIN_VALUE) {
                    imgraster[(maxY * y) + x] = 65535;
                } else if (e != 0) {
                    imgraster[(maxY * y) + x] = getColorForHeight(e);
                } else {
                    imgraster[(maxY * y) + x] = 65535;
                }
                x++;
                rdr = rdr2;
            }
        }
        System.out.println("hgt size " + rowlen + " x " + rowlen + "  min " + ((int) this.minElev) + " max " + ((int) this.maxElev));
        BufferedImage argbImage = new BufferedImage(maxX, maxY, 2);
        int[] idata = argbImage.getRaster().getDataBuffer().getData();
        for (int y2 = 0; y2 < maxY; y2++) {
            for (int x2 = 0; x2 < maxX; x2++) {
                int v0 = imgraster[(maxX * y2) + x2];
                if (v0 != 65535) {
                    rgb = v0 | ViewCompat.MEASURED_STATE_MASK;
                } else {
                    rgb = ViewCompat.MEASURED_STATE_MASK;
                }
                idata[(y2 * maxX) + x2] = rgb;
            }
        }
        ImageIO.write(argbImage, "png", new FileOutputStream(imageName));
    }

    private void loadColors(String colors) throws IOException {
        File colFile = new File(colors);
        if (!colFile.exists()) {
            System.out.println("color file " + colors + " not found");
            return;
        }
        BufferedReader reader = null;
        this.colorMap = new TreeMap();
        try {
            try {
                try {
                    reader = new BufferedReader(new FileReader(colors));
                    for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                        String[] sa = line.split(",");
                        if (!line.startsWith("#") && sa.length == 4) {
                            short e = Short.parseShort(sa[0].trim());
                            short r = Short.parseShort(sa[1].trim());
                            short g = Short.parseShort(sa[2].trim());
                            short b = Short.parseShort(sa[3].trim());
                            this.colorMap.put(Short.valueOf(e), new Color(r, g, b));
                        }
                    }
                    reader.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    this.colorMap = null;
                    if (reader != null) {
                        reader.close();
                    }
                }
            } catch (Exception e3) {
                e3.printStackTrace();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e4) {
                    e4.printStackTrace();
                }
            }
            throw th;
        }
    }

    public double[] getElevationPos(ElevationRaster srtm, int ilon, int ilat) {
        double lon = (((double) ilon) / 1000000.0d) - 180.0d;
        double lat = (((double) ilat) / 1000000.0d) - 90.0d;
        double dcol = ((lon - srtm.xllcorner) / srtm.cellsize) - 0.5d;
        double drow = ((lat - srtm.yllcorner) / srtm.cellsize) - 0.5d;
        int row = (int) drow;
        int col = (int) dcol;
        if (col < 0) {
        }
        if (row < 0) {
        }
        return new double[]{drow, dcol};
    }

    private short get(ElevationRaster srtm, int r, int c) {
        short e = srtm.eval_array[(((srtm.nrows - 1) - r) * srtm.ncols) + c];
        if (e == Short.MIN_VALUE) {
            this.missingData = true;
        }
        return e;
    }

    public short getElevationXY(ElevationRaster srtm, double drow, double dcol) {
        int row = (int) drow;
        int col = (int) dcol;
        if (col < 0) {
            col = 0;
        }
        if (col >= srtm.ncols - 1) {
            col = srtm.ncols - 2;
        }
        if (row < 0) {
            row = 0;
        }
        if (row >= srtm.nrows - 1) {
            row = srtm.nrows - 2;
        }
        double wrow = drow - ((double) row);
        double wcol = dcol - ((double) col);
        this.missingData = false;
        double eval = ((1.0d - wrow) * (1.0d - wcol) * ((double) get(srtm, row, col))) + ((1.0d - wcol) * wrow * ((double) get(srtm, row + 1, col))) + ((1.0d - wrow) * wcol * ((double) get(srtm, row, col + 1))) + (wrow * wcol * ((double) get(srtm, row + 1, col + 1)));
        if (this.missingData) {
            return Short.MIN_VALUE;
        }
        return (short) (4.0d * eval);
    }

    int getColorForHeight(short h) {
        if (this.colorMap == null) {
            this.colorMap = new TreeMap();
            this.colorMap.put((short) 0, new Color(LocationRequestCompat.QUALITY_BALANCED_POWER_ACCURACY, 153, 153));
            this.colorMap.put((short) 1, new Color(0, LocationRequestCompat.QUALITY_BALANCED_POWER_ACCURACY, 0));
            this.colorMap.put((short) 500, new Color(251, 255, 128));
            this.colorMap.put((short) 1200, new Color(224, 108, 31));
            this.colorMap.put((short) 2500, new Color(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 55, 55));
            this.colorMap.put((short) 4000, new Color(215, 244, 244));
            this.colorMap.put((short) 8000, new Color(255, 244, 244));
        }
        Color lastColor = null;
        short lastKey = 0;
        for (Map.Entry<Short, Color> entry : this.colorMap.entrySet()) {
            short key = entry.getKey().shortValue();
            Color value = entry.getValue();
            if (key == h) {
                return value.getRGB();
            }
            if (lastColor != null && lastKey < h && key > h) {
                double between = ((double) (h - lastKey)) / ((double) (key - lastKey));
                return mixColors(value, lastColor, between);
            }
            lastColor = value;
            lastKey = key;
        }
        return 0;
    }

    public int mixColors(Color color1, Color color2, double percent) {
        double inverse_percent = 1.0d - percent;
        int redPart = (int) ((((double) color1.getRed()) * percent) + (((double) color2.getRed()) * inverse_percent));
        int greenPart = (int) ((((double) color1.getGreen()) * percent) + (((double) color2.getGreen()) * inverse_percent));
        int bluePart = (int) ((((double) color1.getBlue()) * percent) + (((double) color2.getBlue()) * inverse_percent));
        return new Color(redPart, greenPart, bluePart).getRGB();
    }

    private ElevationRaster srtmForNode(int ilon, int ilat) throws Exception {
        int srtmLonIdx = (ilon + 5000000) / 5000000;
        int srtmLatIdx = ((654999999 - ilat) / 5000000) - 100;
        if (srtmLonIdx == this.lastSrtmLonIdx && srtmLatIdx == this.lastSrtmLatIdx) {
            return this.lastSrtmRaster;
        }
        this.lastSrtmLonIdx = srtmLonIdx;
        this.lastSrtmLatIdx = srtmLatIdx;
        String slonidx = "0" + srtmLonIdx;
        String slatidx = "0" + srtmLatIdx;
        String filename = "srtm_" + slonidx.substring(slonidx.length() - 2) + "_" + slatidx.substring(slatidx.length() - 2);
        this.lastSrtmRaster = this.srtmmap.get(filename);
        if (this.lastSrtmRaster == null && !this.srtmmap.containsKey(filename)) {
            File f = new File(new File(this.srtmdir), filename + ".bef");
            if (f.exists()) {
                System.out.println("*** reading: " + String.valueOf(f));
                try {
                    InputStream isc = new BufferedInputStream(new FileInputStream(f));
                    this.lastSrtmRaster = new ElevationRasterCoder().decodeRaster(isc);
                    isc.close();
                } catch (Exception e) {
                    System.out.println("**** ERROR reading " + String.valueOf(f) + " ****");
                }
                this.srtmmap.put(filename, this.lastSrtmRaster);
                return this.lastSrtmRaster;
            }
            this.srtmmap.put(filename, this.lastSrtmRaster);
        }
        return this.lastSrtmRaster;
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 6) {
            System.out.println("usage: java CreateLidarImage <lon> <lat> <srtm-folder> <imageFileName> <maxX> <maxY> <downscale> [type] [color_file]");
            System.out.println("\nwhere: type = [bef|hgt] downscale = [1|2|4|..]");
        } else {
            String format = args.length >= 8 ? args[7] : "bef";
            String colors = args.length == 9 ? args[8] : null;
            new CreateElevationRasterImage().createImage(Double.parseDouble(args[0]), Double.parseDouble(args[1]), args[2], args[3], Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]), format, colors);
        }
    }
}

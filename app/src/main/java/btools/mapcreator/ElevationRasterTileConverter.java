package btools.mapcreator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* JADX INFO: loaded from: classes.dex */
public class ElevationRasterTileConverter {
    public static final boolean DEBUG = false;
    private static final int HGT_1ASEC_ROWS = 3601;
    private static final int HGT_3ASEC_FILE_SIZE = 2884802;
    private static final int HGT_3ASEC_ROWS = 1201;
    private static final int HGT_BORDER_OVERLAP = 1;
    private static final String HGT_FILE_EXT = ".hgt";
    public static final short NODATA = Short.MIN_VALUE;
    public static final short NODATA2 = -32767;
    private static final int SRTM1_ROW_LENGTH = 3600;
    private static final int SRTM3_ROW_LENGTH = 1200;
    private static final boolean SRTM_NO_ZERO = true;
    private int NCOLS;
    private int NROWS;
    private int ROW_LENGTH;
    private short[] imagePixels;

    public static void main(String[] args) throws Exception {
        int row_length;
        String fallbackdir;
        if (args.length == 3 || args.length == 4 || args.length == 5) {
            String filename90 = args[0];
            if ("all".equals(filename90)) {
                System.out.println("raster convert all ");
                new ElevationRasterTileConverter().doConvertAll(args[1], args[2], args.length > 3 ? args[3] : null, args.length == 5 ? args[4] : null);
                return;
            }
            String filename30 = filename90 + ".bef";
            int srtmLonIdx = Integer.parseInt(filename90.substring(5, 7).toLowerCase());
            int srtmLatIdx = Integer.parseInt(filename90.substring(8, 10).toLowerCase());
            int ilon_base = ((srtmLonIdx - 1) * 5) - 180;
            int ilat_base = (150 - (srtmLatIdx * 5)) - 90;
            if (args.length <= 3) {
                row_length = SRTM3_ROW_LENGTH;
                fallbackdir = null;
            } else {
                int row_length2 = Integer.parseInt(args[3]) == 1 ? SRTM1_ROW_LENGTH : SRTM3_ROW_LENGTH;
                String fallbackdir2 = args.length == 5 ? args[4] : null;
                row_length = row_length2;
                fallbackdir = fallbackdir2;
            }
            System.out.println("raster convert " + ilon_base + " " + ilat_base + " from " + srtmLonIdx + " " + srtmLatIdx + " f: " + filename90 + " rowl " + row_length);
            new ElevationRasterTileConverter().doConvert(args[1], ilon_base, ilat_base, args[2] + "/" + filename30, row_length, fallbackdir);
            return;
        }
        System.out.println("usage: java <srtm-filename> <hgt-data-dir> <srtm-output-dir> [arc seconds (1 or 3,default=3)] [hgt-fallback-data-dir]");
        System.out.println("or     java all <hgt-data-dir> <srtm-output-dir> [arc seconds (1 or 3, default=3)] [hgt-fallback-data-dir]");
    }

    private void doConvertAll(String hgtdata, String outdir, String rlen, String hgtfallbackdata) throws Exception {
        int row_length = SRTM3_ROW_LENGTH;
        if (rlen != null) {
            row_length = Integer.parseInt(rlen) == 1 ? SRTM1_ROW_LENGTH : SRTM3_ROW_LENGTH;
        }
        for (int ilon_base = -180; ilon_base < 180; ilon_base += 5) {
            for (int ilat_base = 85; ilat_base > -90; ilat_base -= 5) {
                String filename30 = genFilenameOld(ilon_base, ilat_base);
                doConvert(hgtdata, ilon_base, ilat_base, outdir + "/" + filename30, row_length, hgtfallbackdata);
            }
        }
    }

    static String genFilenameOld(int ilon_base, int ilat_base) {
        int srtmLonIdx = ((ilon_base + 180) / 5) + 1;
        int srtmLatIdx = (60 - ilat_base) / 5;
        return String.format(Locale.US, "srtm_%02d_%02d.bef", Integer.valueOf(srtmLonIdx), Integer.valueOf(srtmLatIdx));
    }

    static String genFilenameRd5(int ilon_base, int ilat_base) {
        StringBuilder sbAppend;
        StringBuilder sbAppend2;
        if (ilon_base < 0) {
            sbAppend = new StringBuilder().append("W").append(-ilon_base);
        } else {
            sbAppend = new StringBuilder().append("E").append(ilon_base);
        }
        String string = sbAppend.toString();
        if (ilat_base < 0) {
            sbAppend2 = new StringBuilder().append("S").append(-ilat_base);
        } else {
            sbAppend2 = new StringBuilder().append("N").append(ilat_base);
        }
        return String.format("srtm_%s_%s.bef", string, sbAppend2.toString());
    }

    private void readHgtZip(String filename, int rowOffset, int colOffset, int row_length, int scale) throws Exception {
        ZipEntry ze;
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(filename)));
        do {
            try {
                ze = zis.getNextEntry();
                if (ze == null) {
                    return;
                }
            } finally {
                zis.close();
            }
        } while (!ze.getName().toLowerCase().endsWith(".hgt"));
        readHgtFromStream(zis, rowOffset, colOffset, row_length, scale);
    }

    private void readHgtFromStream(InputStream is, int rowOffset, int colOffset, int rowLength, int scale) throws Exception {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
        for (int ir = 0; ir < rowLength; ir++) {
            int row = (ir * scale) + rowOffset;
            for (int ic = 0; ic < rowLength; ic++) {
                int col = (ic * scale) + colOffset;
                int i1 = dis.read();
                int i0 = dis.read();
                if (i0 == -1 || i1 == -1) {
                    throw new RuntimeException("unexpected end of file reading hgt entry!");
                }
                short val = (short) ((i1 << 8) | i0);
                if (val == -32767) {
                    val = Short.MIN_VALUE;
                }
                if (scale == 3) {
                    setPixel(row, col, val);
                    setPixel(row + 1, col, val);
                    setPixel(row + 2, col, val);
                    setPixel(row, col + 1, val);
                    setPixel(row + 1, col + 1, val);
                    setPixel(row + 2, col + 1, val);
                    setPixel(row, col + 2, val);
                    setPixel(row + 1, col + 2, val);
                    setPixel(row + 2, col + 2, val);
                } else {
                    setPixel(row, col, val);
                }
            }
        }
    }

    private void readHgtFile(File file, int rowOffset, int colOffset, int row_length, int scale) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        try {
            readHgtFromStream(fis, rowOffset, colOffset, row_length, scale);
        } finally {
            fis.close();
        }
    }

    private void readAscZip(File file, ElevationRaster raster) throws Exception {
        ZipEntry ze;
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
        do {
            try {
                ze = zis.getNextEntry();
            } finally {
                zis.close();
            }
        } while (!ze.getName().endsWith(".asc"));
        readAscFromStream(zis, raster);
    }

    private String secondToken(String s) {
        StringTokenizer tk = new StringTokenizer(s, " ");
        tk.nextToken();
        return tk.nextToken();
    }

    private void readAscFromStream(InputStream is, ElevationRaster raster) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        int linenr = 0;
        while (true) {
            linenr++;
            if (linenr > 6) {
                break;
            }
            String line = br.readLine();
            if (linenr == 1) {
                raster.ncols = Integer.parseInt(secondToken(line));
            } else if (linenr == 2) {
                raster.nrows = Integer.parseInt(secondToken(line));
            } else if (linenr == 3) {
                raster.xllcorner = Double.parseDouble(secondToken(line));
            } else if (linenr == 4) {
                raster.yllcorner = Double.parseDouble(secondToken(line));
            } else if (linenr == 5) {
                raster.cellsize = Double.parseDouble(secondToken(line));
            } else if (linenr == 6) {
                raster.eval_array = new short[raster.ncols * raster.nrows];
            }
        }
        int row = 0;
        int col = 0;
        int n = 0;
        boolean negative = false;
        while (true) {
            int c = br.read();
            if (c >= 0) {
                if (c == 32) {
                    if (negative) {
                        n = -n;
                    }
                    short val = n < -250 ? Short.MIN_VALUE : (short) n;
                    raster.eval_array[(raster.ncols * row) + col] = val;
                    col++;
                    if (col == raster.ncols) {
                        col = 0;
                        row++;
                    }
                    n = 0;
                    negative = false;
                } else if (c >= 48 && c <= 57) {
                    n = (n * 10) + (c - 48);
                } else if (c == 45) {
                    negative = SRTM_NO_ZERO;
                }
            } else {
                br.close();
                return;
            }
        }
    }

    private void setPixel(int row, int col, short val) {
        if (row >= 0 && row < this.NROWS && col >= 0 && col < this.NCOLS) {
            this.imagePixels[(this.NCOLS * row) + col] = val;
        }
    }

    private short getPixel(int row, int col) {
        if (row >= 0 && row < this.NROWS && col >= 0 && col < this.NCOLS) {
            return this.imagePixels[(this.NCOLS * row) + col];
        }
        return Short.MIN_VALUE;
    }

    /* JADX WARN: Code restructure failed: missing block: B:101:0x039b, code lost:
    
        r10 = r10 + 1;
        r2 = r17;
        r0 = r18;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void doConvert(String inputDir, int lonDegreeStart, int latDegreeStart, String outputFile, int row_length, String hgtfallbackdata) throws Exception {
        String str;
        String str2;
        int lonIdx;
        int latIdx;
        String str3;
        String str4;
        String filename;
        String filename2;
        String filename3;
        String str5 = inputDir;
        int i = latDegreeStart;
        boolean ascfound = false;
        String filename4 = null;
        int latIdx2 = 0;
        boolean hgtfound = false;
        while (true) {
            str = HgtReader.ZIP_EXT;
            str2 = "/";
            int i2 = 5;
            if (latIdx2 >= 5) {
                break;
            }
            int latDegree = i + latIdx2;
            boolean ascfound2 = ascfound;
            int lonIdx2 = 0;
            while (true) {
                if (lonIdx2 < i2) {
                    String lat = formatLat(latDegree);
                    String filename5 = formatLon(lonDegreeStart + lonIdx2);
                    String filename6 = str5 + "/" + lat + filename5 + HgtReader.ZIP_EXT;
                    File f = new File(filename6);
                    if (f.exists() && f.length() > 0) {
                        filename4 = filename6;
                        hgtfound = true;
                        break;
                    }
                    filename4 = filename6.substring(0, filename6.length() - 4) + ".hgt";
                    File f2 = new File(filename4);
                    if (!f2.exists() || f2.length() <= 0) {
                        lonIdx2++;
                        i2 = 5;
                    } else {
                        hgtfound = true;
                        break;
                    }
                }
            }
            latIdx2++;
            ascfound = ascfound2;
        }
        boolean ascfound3 = ascfound;
        if (!hgtfound) {
            filename4 = str5 + "/" + genFilenameOld(lonDegreeStart, latDegreeStart).substring(0, 10) + HgtReader.ZIP_EXT;
            File f3 = new File(filename4);
            if (f3.exists() && f3.length() > 0) {
                ascfound3 = true;
            }
        }
        if (hgtfound) {
            this.NROWS = (row_length * 5) + 1 + (0 * 2);
            this.NCOLS = (row_length * 5) + 1 + (0 * 2);
            this.imagePixels = new short[this.NROWS * this.NCOLS];
            Arrays.fill(this.imagePixels, Short.MIN_VALUE);
        } else if (!ascfound3) {
            return;
        }
        if (hgtfound) {
            String str6 = filename4;
            int latIdx3 = -1;
            String filename7 = str6;
            while (latIdx3 <= 5) {
                int latDegree2 = i + latIdx3;
                int rowOffset = 0 + ((4 - latIdx3) * row_length);
                int lonIdx3 = -1;
                while (lonIdx3 <= 5) {
                    int lonDegree = lonDegreeStart + lonIdx3;
                    int colOffset = 0 + (lonIdx3 * row_length);
                    String filename8 = str5 + str2 + formatLat(latDegree2) + formatLon(lonDegree) + str;
                    File f4 = new File(filename8);
                    if (!f4.exists() || f4.length() <= 0) {
                        lonIdx = lonIdx3;
                        latIdx = latIdx3;
                        str3 = str2;
                        str4 = str;
                        String filename9 = filename8.substring(0, filename8.length() - 4) + ".hgt";
                        File f5 = new File(filename9);
                        if (!f5.exists() || f5.length() <= 0) {
                            filename = filename9;
                            filename2 = null;
                            if (hgtfallbackdata != null) {
                                String filename10 = hgtfallbackdata + str3 + formatLat(latDegree2) + formatLon(lonDegree) + ".hgt";
                                File f6 = new File(filename10);
                                if (f6.exists() && f6.length() > 0) {
                                    readHgtFile(f6, rowOffset, colOffset, 1201, 3);
                                    filename7 = filename10;
                                } else {
                                    String filename11 = filename10.substring(0, filename10.length() - 4) + str4;
                                    File f7 = new File(filename11);
                                    if (!f7.exists() || f7.length() <= 0) {
                                        filename3 = filename11;
                                    } else {
                                        filename3 = filename11;
                                        readHgtZip(filename11, rowOffset, colOffset, 1201, 3);
                                    }
                                    filename7 = filename3;
                                }
                            }
                        } else {
                            filename = filename9;
                            filename2 = null;
                            readHgtFile(f5, rowOffset, colOffset, row_length + 1, 1);
                        }
                        filename7 = filename;
                    } else {
                        lonIdx = lonIdx3;
                        latIdx = latIdx3;
                        str3 = str2;
                        str4 = str;
                        readHgtZip(filename8, rowOffset, colOffset, row_length + 1, 1);
                        filename7 = filename8;
                        filename2 = null;
                    }
                    lonIdx3 = lonIdx + 1;
                    str2 = str3;
                    str = str4;
                    latIdx3 = latIdx;
                    str5 = inputDir;
                }
                latIdx3++;
                str5 = inputDir;
                i = latDegreeStart;
            }
            for (int row = 0; row < this.NROWS; row++) {
                for (int col = 0; col < this.NCOLS; col++) {
                    if (this.imagePixels[(this.NCOLS * row) + col] == 0) {
                        this.imagePixels[(this.NCOLS * row) + col] = Short.MIN_VALUE;
                    }
                }
            }
            filename4 = filename7;
        }
        String filename12 = null;
        ElevationRaster raster = new ElevationRaster();
        if (hgtfound) {
            raster.nrows = this.NROWS;
            raster.ncols = this.NCOLS;
            raster.halfcol = false;
            raster.noDataValue = Short.MIN_VALUE;
            raster.cellsize = 1.0d / ((double) row_length);
            raster.xllcorner = ((double) lonDegreeStart) - ((((double) 0) + 0.5d) * raster.cellsize);
            raster.yllcorner = ((double) latDegreeStart) - ((((double) 0) + 0.5d) * raster.cellsize);
            raster.eval_array = this.imagePixels;
        }
        if (ascfound3) {
            readAscZip(new File(filename4), raster);
        }
        OutputStream os = new BufferedOutputStream(new FileOutputStream(outputFile));
        new ElevationRasterCoder().encodeRaster(raster, os);
        os.close();
        InputStream is = new BufferedInputStream(new FileInputStream(outputFile));
        ElevationRaster raster2 = new ElevationRasterCoder().decodeRaster(is);
        is.close();
        short[] pix2 = raster2.eval_array;
        if (pix2.length != raster.eval_array.length) {
            throw new RuntimeException("length mismatch!");
        }
        int row2 = 0;
        while (row2 < raster.nrows) {
            int colstep = filename12 != null ? 2 : 1;
            String str7 = filename12;
            int col2 = 0;
            while (true) {
                String filename13 = filename4;
                if (col2 < raster.ncols) {
                    int idx = (raster.ncols * row2) + col2;
                    short p2 = pix2[idx];
                    OutputStream os2 = os;
                    if (p2 == raster.eval_array[idx]) {
                        col2 += colstep;
                        filename4 = filename13;
                        os = os2;
                    } else {
                        throw new RuntimeException("content mismatch: p2=" + ((int) p2) + " p1=" + ((int) raster.eval_array[idx]));
                    }
                }
            }
        }
        this.imagePixels = null;
    }

    private static String formatLon(int lon) {
        if (lon >= 180) {
            lon -= 180;
        }
        String s = "E";
        if (lon < 0) {
            lon = -lon;
            s = "W";
        }
        String n = "000" + lon;
        return s + n.substring(n.length() - 3);
    }

    private static String formatLat(int lat) {
        String s = "N";
        if (lat < 0) {
            lat = -lat;
            s = "S";
        }
        String n = "00" + lat;
        return s + n.substring(n.length() - 2);
    }

    public ElevationRaster getRaster(File f, double lon, double lat) throws Exception {
        long fileSize;
        InputStream inputStream;
        int rowLength;
        ZipEntry ze;
        if (f.getName().toLowerCase().endsWith(HgtReader.ZIP_EXT)) {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(f)));
            do {
                ze = zis.getNextEntry();
                if (ze == null) {
                    throw new FileNotFoundException(f.getName() + " doesn't contain a .hgt file.");
                }
            } while (!ze.getName().toLowerCase().endsWith(".hgt"));
            long fileSize2 = ze.getSize();
            fileSize = fileSize2;
            inputStream = zis;
        } else {
            long fileSize3 = f.length();
            fileSize = fileSize3;
            inputStream = new FileInputStream(f);
        }
        if (fileSize > 2884802) {
            rowLength = 3601;
        } else {
            rowLength = 1201;
        }
        this.NROWS = rowLength;
        this.NCOLS = rowLength;
        this.imagePixels = new short[this.NROWS * this.NCOLS];
        Arrays.fill(this.imagePixels, Short.MIN_VALUE);
        readHgtFromStream(inputStream, 0, 0, rowLength, 1);
        inputStream.close();
        ElevationRaster raster = new ElevationRaster();
        raster.nrows = this.NROWS;
        raster.ncols = this.NCOLS;
        raster.halfcol = false;
        raster.noDataValue = Short.MIN_VALUE;
        raster.cellsize = 1.0d / ((double) (rowLength - 1));
        raster.xllcorner = (int) (lon < 0.0d ? lon - 1.0d : lon);
        raster.yllcorner = (int) (lat < 0.0d ? lat - 1.0d : lat);
        raster.eval_array = this.imagePixels;
        return raster;
    }
}

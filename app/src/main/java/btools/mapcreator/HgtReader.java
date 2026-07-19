package btools.mapcreator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/* JADX INFO: loaded from: classes.dex */
public class HgtReader {
    static final boolean DEBUG = false;
    public static final int HGT1_RES = 1;
    public static final int HGT1_ROW_LENGTH = 3601;
    public static final int HGT3_RES = 3;
    public static final int HGT3_ROW_LENGTH = 1201;
    public static final String HGT_EXT = ".hgt";
    public static final int HGT_VOID = -32768;
    private static final int SECONDS_PER_MINUTE = 60;
    public static final String ZIP_EXT = ".zip";
    public static double NO_ELEVATION = Double.NaN;
    private static String srtmFolder = "";
    private static final Map<String, ShortBuffer> cache = new HashMap();

    public HgtReader(String folder) {
        srtmFolder = folder;
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x0095, code lost:
    
        r0 = readHgtStream(r4);
        btools.mapcreator.HgtReader.cache.put(r1, r0);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static double getElevationFromHgt(double lat, double lon) {
        try {
            String file = getHgtFileName(lat, lon);
            if (!cache.containsKey(file)) {
                cache.put(file, null);
                String fullPath = new File(srtmFolder, file + HGT_EXT).getPath();
                File f = new File(fullPath);
                if (f.exists()) {
                    ShortBuffer data = readHgtFile(fullPath);
                    cache.put(file, data);
                } else {
                    f = new File(new File(srtmFolder, file + ZIP_EXT).getPath());
                    if (f.exists()) {
                        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(f)));
                        while (true) {
                            try {
                                ZipEntry ze = zis.getNextEntry();
                                if (ze == null) {
                                    break;
                                }
                                if (ze.getName().toLowerCase().endsWith(HGT_EXT)) {
                                    break;
                                }
                                zis.closeEntry();
                            } finally {
                                zis.close();
                            }
                        }
                    }
                }
                System.out.println("*** reading: " + f.getName() + "  " + String.valueOf(cache.get(file)));
            }
            return readElevation(lat, lon);
        } catch (FileNotFoundException e) {
            System.err.println("HGT Get elevation " + lat + ", " + lon + " failed: => " + e.getMessage());
            return NO_ELEVATION;
        } catch (Exception ioe) {
            ioe.printStackTrace(System.err);
            return NO_ELEVATION;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:21:0x0096, code lost:
    
        r3 = readHgtStream(r6);
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static short[] getElevationDataFromHgt(double lat, double lon) throws Exception {
        if (lon < 0.0d) {
            lon += 1.0d;
        }
        if (lat < 0.0d) {
            lat += 1.0d;
        }
        try {
            String file = getHgtFileName(lat, lon);
            ShortBuffer data = null;
            String fullPath = new File(srtmFolder, file + HGT_EXT).getPath();
            File f = new File(fullPath);
            if (f.exists()) {
                data = readHgtFile(fullPath);
            } else {
                f = new File(new File(srtmFolder, file + ZIP_EXT).getPath());
                if (f.exists()) {
                    ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(f)));
                    while (true) {
                        try {
                            ZipEntry ze = zis.getNextEntry();
                            if (ze == null) {
                                break;
                            }
                            if (ze.getName().toLowerCase().endsWith(HGT_EXT)) {
                                break;
                            }
                            zis.closeEntry();
                        } finally {
                            zis.close();
                        }
                    }
                }
            }
            System.out.println("*** reading: " + f.getName() + "  " + (data != null ? data.limit() : -1));
            if (data == null) {
                return null;
            }
            short[] array = new short[data.limit()];
            data.get(array);
            return array;
        } catch (FileNotFoundException e) {
            System.err.println("HGT Get elevation " + lat + ", " + lon + " failed: => " + e.getMessage());
            return null;
        } catch (Exception ioe) {
            ioe.printStackTrace(System.err);
            return null;
        }
    }

    private static ShortBuffer readHgtFile(String file) throws Exception {
        if (file == null) {
            throw new Exception("no hgt file " + file);
        }
        FileChannel fc = null;
        try {
            fc = new FileInputStream(file).getChannel();
            ByteBuffer bb = ByteBuffer.allocateDirect((int) fc.size());
            while (bb.remaining() > 0) {
                fc.read(bb);
            }
            ShortBuffer sb = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
            return sb;
        } finally {
            if (fc != null) {
                fc.close();
            }
        }
    }

    private static ShortBuffer readHgtStream(InputStream zis) throws Exception {
        if (zis == null) {
            throw new Exception("no hgt stream ");
        }
        byte[] bytes = zis.readAllBytes();
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes, 0, bytes.length);
        ShortBuffer sb = bb.order(ByteOrder.BIG_ENDIAN).asShortBuffer();
        return sb;
    }

    public static double readElevation(double lat, double lon) {
        String tag = getHgtFileName(lat, lon);
        ShortBuffer sb = cache.get(tag);
        if (sb == null) {
            return NO_ELEVATION;
        }
        int rowLength = HGT3_ROW_LENGTH;
        int resolution = 3;
        try {
            if (sb.capacity() > 1442401) {
                rowLength = HGT1_ROW_LENGTH;
                resolution = 1;
            }
            double fLat = frac(lat) * 60.0d;
            double fLon = frac(lon) * 60.0d;
            int row = (int) Math.round((fLat * 60.0d) / ((double) resolution));
            double fLat2 = resolution;
            int col = (int) Math.round((60.0d * fLon) / fLat2);
            if (lon < 0.0d) {
                col = (rowLength - col) - 1;
            }
            if (lat > 0.0d) {
                row = (rowLength - row) - 1;
            }
            int cell = (rowLength * row) + col;
            if (cell < sb.limit()) {
                short ele = sb.get(cell);
                if (ele == Short.MIN_VALUE) {
                    return NO_ELEVATION;
                }
                return ele;
            }
            return NO_ELEVATION;
        } catch (Exception e) {
            System.err.println("error at " + lon + " " + lat + " ");
            e.printStackTrace();
            return NO_ELEVATION;
        }
    }

    public static String getHgtFileName(double llat, double llon) {
        int lat = (int) llat;
        int lon = (int) llon;
        String latPref = "N";
        if (lat < 0) {
            latPref = "S";
            lat = (-lat) + 1;
        }
        String lonPref = "E";
        if (lon < 0) {
            lonPref = "W";
            lon = (-lon) + 1;
        }
        return String.format("%s%02d%s%03d", latPref, Integer.valueOf(lat), lonPref, Integer.valueOf(lon));
    }

    public static double frac(double d) {
        long iPart = (long) d;
        double fPart = d - iPart;
        return Math.abs(fPart);
    }

    public static void clear() {
        if (cache != null) {
            cache.clear();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("*** HGT position values and enhance elevation");
        if (args.length == 3) {
            new HgtReader(args[0]);
            double lon = Double.parseDouble(args[1]);
            double lat = Double.parseDouble(args[2]);
            double elev = getElevationFromHgt(lat, lon);
            System.out.println("-----> elv for hgt " + lat + ", " + lon + " = " + elev);
        }
    }
}

package com.bike.computer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.UByte;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;

public final class StreetNames {
    public static final StreetNames INSTANCE = new StreetNames();
    private static final int Z = 14;
    private static SQLiteDatabase db;

    private StreetNames() {
    }

    public final void open(String mbtilesPath) {
        Intrinsics.checkNotNullParameter(mbtilesPath, "mbtilesPath");
        if (db == null) {
            try {
                db = SQLiteDatabase.openDatabase(mbtilesPath, null, 1);
            } catch (Exception e) {
                Log.e("BikeRoute", "mbtiles open failed", e);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:30:0x0096 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x0097  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final String nameAt(double lat, double lon) {
        byte[] bArr = null;
        byte[] blob = null;
        Throwable th;
        SQLiteDatabase d = db;
        if (d == null) {
            return "";
        }
        double xf = ((lon + 180.0d) / 360.0d) * ((double) 16384);
        double latRad = Math.toRadians(lat);
        double yf = ((1.0d - (Math.log(Math.tan(latRad) + (1.0d / Math.cos(latRad))) / 3.141592653589793d)) / 2.0d) * ((double) 16384);
        int tx = (int) Math.floor(xf);
        int ty = (int) Math.floor(yf);
        int tmsY = (16384 - 1) - ty;
        try {
            try {
                Cursor cursorRawQuery = d.rawQuery("SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?", new String[]{"14", String.valueOf(tx), String.valueOf(tmsY)});
                try {
                    Cursor c = cursorRawQuery;
                    if (c.moveToFirst()) {
                        try {
                            blob = c.getBlob(0);
                        } catch (Throwable th2) {
                            th = th2;
                            bArr = null;
                            try {
                                throw th;
                            } catch (Throwable th3) {
                                CloseableKt.closeFinally(cursorRawQuery, th);
                                throw Sneaky.sneak(th3);
                            }
                        }
                    } else {
                        blob = null;
                    }
                    CloseableKt.closeFinally(cursorRawQuery, null);
                } catch (Throwable th4) {
                    bArr = null;
                    th = th4;
                }
            } catch (Exception e) {
                blob = bArr;
                if (blob == null) {
                }
            }
        } catch (Exception e2) {
            bArr = null;
            blob = bArr;
            if (blob == null) {
            }
        }
        if (blob == null) {
            return "";
        }
        byte[] data = gunzip(blob);
        try {
            return parseTile(data, xf - ((double) tx), yf - ((double) ty));
        } catch (Exception e3) {
            return "";
        }
    }

    private final byte[] gunzip(byte[] b) {
        if (b.length < 2 || (b[0] & UByte.MAX_VALUE) != 31) {
            return b;
        }
        try {
            GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(b));
            GZIPInputStream g = gZIPInputStream;
            ByteArrayOutputStream o = new ByteArrayOutputStream(b.length * 5);
            ByteStreamsKt.copyTo(g, o, 8192);
            byte[] byteArray = o.toByteArray();
            Intrinsics.checkNotNullExpressionValue(byteArray, "toByteArray(...)");
            CloseableKt.closeFinally(gZIPInputStream, null);
            return byteArray;
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    /* JADX INFO: compiled from: StreetNames.kt */
    private static final class PB {
        private final byte[] b;
        private int p;

        public PB(byte[] b) {
            Intrinsics.checkNotNullParameter(b, "b");
            this.b = b;
        }

        public final byte[] getB() {
            return this.b;
        }

        public final int getP() {
            return this.p;
        }

        public final void setP(int i) {
            this.p = i;
        }

        public final boolean more() {
            return this.p < this.b.length;
        }

        public final long varint() {
            long r = 0;
            int s = 0;
            while (true) {
                byte[] bArr = this.b;
                int i = this.p;
                this.p = i + 1;
                int x = bArr[i] & UByte.MAX_VALUE;
                r |= ((long) (x & 127)) << s;
                if (x < 128) {
                    return r;
                }
                s += 7;
            }
        }

        public final byte[] bytes() {
            int len = (int) varint();
            byte[] r = ArraysKt.copyOfRange(this.b, this.p, this.p + len);
            this.p += len;
            return r;
        }

        public final String str() {
            return new String(bytes(), Charsets.UTF_8);
        }

        public final void skip(int wt) {
            int i;
            switch (wt) {
                case 0:
                    varint();
                    return;
                case 1:
                    i = this.p + 8;
                    break;
                case 2:
                    int l = (int) varint();
                    this.p += l;
                    return;
                case 3:
                case 4:
                default:
                    return;
                case 5:
                    i = this.p + 4;
                    break;
            }
            this.p = i;
        }
    }

    private final int zz(int n) {
        return (n >>> 1) ^ (-(n & 1));
    }

    private final String parseTile(byte[] data, double fx, double fy) {
        PB pb = new PB(data);
        while (pb.more()) {
            int t = (int) pb.varint();
            int field = t >>> 3;
            int wt = t & 7;
            if (field == 3 && wt == 2) {
                String name = parseLayer(pb.bytes(), fx, fy);
                if (name.length() > 0) {
                    return name;
                }
            } else {
                pb.skip(wt);
            }
        }
        return "";
    }

    private final String parseLayer(byte[] bytes, double fx, double fy) {
        String best;
        Iterator it;
        String lname;
        int extent;
        double maxD;
        String str;
        StreetNames streetNames = this;
        PB pb = new PB(bytes);
        ArrayList keys = new ArrayList();
        ArrayList values = new ArrayList();
        ArrayList features = new ArrayList();
        String lname2 = "";
        int extent2 = 4096;
        while (pb.more()) {
            int t = (int) pb.varint();
            int f = t >>> 3;
            int wt = t & 7;
            switch (f) {
                case 1:
                    lname2 = pb.str();
                    break;
                case 2:
                    features.add(pb.bytes());
                    break;
                case 3:
                    keys.add(pb.str());
                    break;
                case 4:
                    values.add(streetNames.parseValue(pb.bytes()));
                    break;
                case 5:
                    extent2 = (int) pb.varint();
                    break;
                default:
                    pb.skip(wt);
                    break;
            }
        }
        if (!Intrinsics.areEqual(lname2, "transportation_name")) {
            return "";
        }
        Iterable $this$map$iv = CollectionsKt.listOf((Object[]) new String[]{"name", "name:latin", "name:en"});
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            String it2 = (String) item$iv$iv;
            destination$iv$iv.add(Integer.valueOf(keys.indexOf(it2)));
        }
        Iterable $this$filter$iv = (List) destination$iv$iv;
        Collection destination$iv$iv2 = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            int it3 = ((Number) element$iv$iv).intValue();
            int it4 = it3 >= 0 ? 1 : 0;
            if (it4 != 0) {
                destination$iv$iv2.add(element$iv$iv);
            }
        }
        HashSet<Integer> hashSet = CollectionsKt.toHashSet((List) destination$iv$iv2);
        String str2 = "BikeName";
        if (hashSet.isEmpty()) {
            Log.i("BikeName", "no name key; keys=" + keys);
            return "";
        }
        double qx = fx * ((double) extent2);
        double qy = fy * ((double) extent2);
        double maxD2 = ((double) extent2) * 0.1d;
        Iterator it5 = features.iterator();
        Intrinsics.checkNotNullExpressionValue(it5, "iterator(...)");
        double bestD = Double.MAX_VALUE;
        String best2 = "";
        while (it5.hasNext()) {
            Object next = it5.next();
            Intrinsics.checkNotNullExpressionValue(next, "next(...)");
            byte[] fb = (byte[]) next;
            Pair<String, int[]> feature = streetNames.parseFeature(fb, values, hashSet);
            String nm = feature.component1();
            int[] geom = feature.component2();
            if (nm.length() == 0) {
                best = best2;
                it = it5;
                lname = lname2;
                extent = extent2;
                maxD = maxD2;
                str = str2;
            } else {
                best = best2;
                lname = lname2;
                extent = extent2;
                maxD = maxD2;
                it = it5;
                str = str2;
                double dd = minDist(qx, qy, geom);
                if (dd < bestD) {
                    bestD = dd;
                    best2 = nm;
                    streetNames = this;
                    str2 = str;
                    maxD2 = maxD;
                    extent2 = extent;
                    lname2 = lname;
                    it5 = it;
                }
            }
            best2 = best;
            str2 = str;
            maxD2 = maxD;
            extent2 = extent;
            lname2 = lname;
            it5 = it;
            streetNames = this;
        }
        String best3 = best2;
        double maxD3 = maxD2;
        Log.i(str2, "tn feats=" + features.size() + " best='" + best3 + "' bestD=" + ((int) bestD) + " maxD=" + ((int) maxD3));
        return bestD <= maxD3 ? best3 : "";
    }

    private final String parseValue(byte[] bytes) {
        PB pb = new PB(bytes);
        while (pb.more()) {
            int t = (int) pb.varint();
            int f = t >>> 3;
            int wt = t & 7;
            if (f == 1 && wt == 2) {
                return pb.str();
            }
            pb.skip(wt);
        }
        return "";
    }

    private final Pair<String, int[]> parseFeature(byte[] bytes, List<String> values, HashSet<Integer> nameKeys) {
        PB pb = new PB(bytes);
        String name = "";
        int[] geom = new int[0];
        while (pb.more()) {
            int t = (int) pb.varint();
            int f = t >>> 3;
            int wt = t & 7;
            if (f == 2 && wt == 2) {
                byte[] tb = pb.bytes();
                PB tp = new PB(tb);
                while (tp.more()) {
                    int k = (int) tp.varint();
                    int v = (int) tp.varint();
                    if (nameKeys.contains(Integer.valueOf(k)) && v < values.size()) {
                        if (name.length() == 0) {
                            String name2 = values.get(v);
                            name = name2;
                        }
                    }
                }
            } else if (f == 4 && wt == 2) {
                byte[] gb = pb.bytes();
                PB gp = new PB(gb);
                ArrayList g = new ArrayList();
                while (gp.more()) {
                    g.add(Integer.valueOf((int) gp.varint()));
                }
                geom = CollectionsKt.toIntArray(g);
            } else {
                pb.skip(wt);
            }
        }
        return TuplesKt.to(name, geom);
    }

    private final double minDist(double qx, double qy, int[] g) {
        int cmd;
        int id;
        int cnt;
        int i;
        int cx = 0;
        int cy = 0;
        double px = 0.0d;
        double py = 0.0d;
        boolean have = false;
        double best = Double.MAX_VALUE;
        int i2 = 0;
        while (i2 < g.length) {
            int cmd2 = g[i2];
            int id2 = cmd2 & 7;
            int cnt2 = cmd2 >>> 3;
            i2++;
            int i3 = 0;
            switch (id2) {
                case 1:
                    int cnt3 = cnt2;
                    while (true) {
                        int cmd3 = cnt3;
                        if (i3 < cmd3) {
                            int i4 = i2 + 1;
                            cx += INSTANCE.zz(g[i2]);
                            cy += INSTANCE.zz(g[i4]);
                            px = cx;
                            py = cy;
                            have = true;
                            i3++;
                            cnt3 = cmd3;
                            i2 = i4 + 1;
                        } else {
                            break;
                        }
                    }
                    break;
                case 2:
                    int i5 = 0;
                    while (i5 < cnt2) {
                        int i6 = i2 + 1;
                        cx += INSTANCE.zz(g[i2]);
                        int i7 = i6 + 1;
                        cy += INSTANCE.zz(g[i6]);
                        if (have) {
                            cmd = cmd2;
                            id = id2;
                            cnt = cnt2;
                            i = i5;
                            best = Math.min(best, INSTANCE.segDist(qx, qy, px, py, cx, cy));
                        } else {
                            cmd = cmd2;
                            id = id2;
                            cnt = cnt2;
                            i = i5;
                        }
                        px = cx;
                        py = cy;
                        i5 = i + 1;
                        i2 = i7;
                        cmd2 = cmd;
                        id2 = id;
                        cnt2 = cnt;
                    }
                    break;
                default:
                    i2 += 0;
                    break;
            }
        }
        return best;
    }

    private final double segDist(double px, double py, double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        double len2 = (dx * dx) + (dy * dy);
        double t = (len2 > 0.0d ? 1 : (len2 == 0.0d ? 0 : -1)) == 0 ? 0.0d : RangesKt.coerceIn((((px - ax) * dx) + ((py - ay) * dy)) / len2, 0.0d, 1.0d);
        double cx = ax + (t * dx);
        double cy = ay + (t * dy);
        double ex = px - cx;
        double ey = py - cy;
        return Math.sqrt((ex * ex) + (ey * ey));
    }
}

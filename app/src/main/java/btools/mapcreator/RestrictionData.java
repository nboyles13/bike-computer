package btools.mapcreator;

import btools.util.CheapAngleMeter;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/* JADX INFO: loaded from: classes.dex */
public class RestrictionData extends MapCreatorBase {
    public boolean badWayMatch;
    public short exceptions;
    public int fromLat;
    public int fromLon;
    public long fromWid;
    public RestrictionData next;
    public String restriction;
    public String restrictionKey;
    public int toLat;
    public int toLon;
    public long toWid;
    public int viaLat;
    public int viaLon;
    public long viaNid;
    private static Map<String, String> names = new HashMap();
    private static Set<Long> badTRs = new TreeSet();

    public RestrictionData() {
    }

    public boolean isPositive() {
        return this.restriction.startsWith("only_");
    }

    public boolean isValid() {
        boolean valid = (this.fromLon != 0 && this.toLon != 0 && (this.restriction.startsWith("only_") || this.restriction.startsWith("no_"))) && this.restriction.indexOf("on_red") < 0;
        if (!valid || this.badWayMatch || !checkGeometry()) {
            synchronized (badTRs) {
                badTRs.add(Long.valueOf((((long) this.viaLon) << 32) | ((long) this.viaLat)));
            }
        }
        return valid && "restriction".equals(this.restrictionKey);
    }

    private boolean checkGeometry() {
        String t;
        int idx;
        double a = new CheapAngleMeter().calcAngle(this.fromLon, this.fromLat, this.viaLon, this.viaLat, this.toLon, this.toLat);
        if (this.restriction.startsWith("only_")) {
            t = this.restriction.substring("only_".length());
        } else {
            String t2 = this.restriction;
            if (t2.startsWith("no_")) {
                t = this.restriction.substring("no_".length());
            } else {
                throw new RuntimeException("ups");
            }
        }
        if (this.restrictionKey.endsWith(":conditional") && (idx = t.indexOf(64)) >= 0) {
            t = t.substring(0, idx).trim();
        }
        return "left_turn".equals(t) ? a < -5.0d && a > -175.0d : "right_turn".equals(t) ? a > 5.0d && a < 175.0d : "straight_on".equals(t) ? a > -85.0d && a < 85.0d : "u_turn".equals(t) ? a < -95.0d || a > 95.0d : "entry".equals(t) || "exit".equals(t);
    }

    private static String unifyName(String name) {
        String n;
        synchronized (names) {
            n = names.get(name);
            if (n == null) {
                names.put(name, name);
                n = name;
            }
        }
        return n;
    }

    public static void dumpBadTRs() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter("badtrs.txt"));
            try {
                for (Long id : badTRs) {
                    bw.write(id + " 26\n");
                }
                bw.close();
            } finally {
            }
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public RestrictionData(DataInputStream di) throws Exception {
        this.restrictionKey = unifyName(di.readUTF());
        this.restriction = unifyName(di.readUTF());
        this.exceptions = di.readShort();
        this.fromWid = readId(di);
        this.toWid = readId(di);
        this.viaNid = readId(di);
    }

    public void writeTo(DataOutputStream dos) throws Exception {
        dos.writeUTF(this.restrictionKey);
        dos.writeUTF(this.restriction);
        dos.writeShort(this.exceptions);
        writeId(dos, this.fromWid);
        writeId(dos, this.toWid);
        writeId(dos, this.viaNid);
    }
}

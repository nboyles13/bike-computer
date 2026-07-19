package btools.router;

import btools.mapaccess.OsmNode;
import btools.mapaccess.OsmPos;
import btools.util.CheapRuler;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class OsmPathElement implements OsmPos {
    public int cost;
    private int ilat;
    private int ilon;
    public MessageData message = null;
    public OsmPathElement origin;
    private short selev;

    @Override // btools.mapaccess.OsmPos
    public final int getILat() {
        return this.ilat;
    }

    @Override // btools.mapaccess.OsmPos
    public final int getILon() {
        return this.ilon;
    }

    @Override // btools.mapaccess.OsmPos
    public final short getSElev() {
        return this.selev;
    }

    public final void setSElev(short s) {
        this.selev = s;
    }

    @Override // btools.mapaccess.OsmPos
    public final double getElev() {
        return ((double) this.selev) / 4.0d;
    }

    public final float getTime() {
        if (this.message == null) {
            return 0.0f;
        }
        return this.message.time;
    }

    public final void setTime(float t) {
        if (this.message != null) {
            this.message.time = t;
        }
    }

    public final float getEnergy() {
        if (this.message == null) {
            return 0.0f;
        }
        return this.message.energy;
    }

    public final void setEnergy(float e) {
        if (this.message != null) {
            this.message.energy = e;
        }
    }

    public final void setAngle(float e) {
        if (this.message != null) {
            this.message.turnangle = e;
        }
    }

    @Override // btools.mapaccess.OsmPos
    public final long getIdFromPos() {
        return (((long) this.ilon) << 32) | ((long) this.ilat);
    }

    @Override // btools.mapaccess.OsmPos
    public final int calcDistance(OsmPos p) {
        return (int) Math.max(1.0d, Math.round(CheapRuler.distance(this.ilon, this.ilat, p.getILon(), p.getILat())));
    }

    public static final OsmPathElement create(OsmPath path) {
        OsmNode n = path.getTargetNode();
        OsmPathElement pe = create(n.getILon(), n.getILat(), n.getSElev(), path.originElement);
        pe.cost = path.cost;
        pe.message = path.message;
        return pe;
    }

    public static final OsmPathElement create(int ilon, int ilat, short selev, OsmPathElement origin) {
        OsmPathElement pe = new OsmPathElement();
        pe.ilon = ilon;
        pe.ilat = ilat;
        pe.selev = selev;
        pe.origin = origin;
        return pe;
    }

    protected OsmPathElement() {
    }

    public String toString() {
        return this.ilon + "_" + this.ilat;
    }

    public boolean positionEquals(OsmPathElement e) {
        return this.ilat == e.ilat && this.ilon == e.ilon;
    }

    public void writeToStream(DataOutput dos) throws IOException {
        dos.writeInt(this.ilat);
        dos.writeInt(this.ilon);
        dos.writeShort(this.selev);
        dos.writeInt(this.cost);
    }

    public static OsmPathElement readFromStream(DataInput dis) throws IOException {
        OsmPathElement pe = new OsmPathElement();
        pe.ilat = dis.readInt();
        pe.ilon = dis.readInt();
        pe.selev = dis.readShort();
        pe.cost = dis.readInt();
        return pe;
    }
}

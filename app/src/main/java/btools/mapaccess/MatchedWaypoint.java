package btools.mapaccess;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class MatchedWaypoint {
    public static final byte WAYPOINT_TYPE_DIRECT = 3;
    public static final byte WAYPOINT_TYPE_MEETING = 2;
    public static final byte WAYPOINT_TYPE_SHAPING = 1;
    public OsmNode correctedpoint;
    public OsmNode crosspoint;
    public boolean hasUpdate;
    public String name;
    public OsmNode node1;
    public OsmNode node2;
    public double radius;
    public OsmNode waypoint;
    public byte wpttype = 1;
    public int indexInTrack = 0;
    public double directionToNext = -1.0d;
    public double directionDiff = 361.0d;
    public List<MatchedWaypoint> wayNearest = new ArrayList();

    public void writeToStream(DataOutput dos) throws IOException {
        dos.writeInt(this.node1.ilat);
        dos.writeInt(this.node1.ilon);
        dos.writeInt(this.node2.ilat);
        dos.writeInt(this.node2.ilon);
        dos.writeInt(this.crosspoint.ilat);
        dos.writeInt(this.crosspoint.ilon);
        dos.writeInt(this.waypoint.ilat);
        dos.writeInt(this.waypoint.ilon);
        dos.writeDouble(this.radius);
    }

    public static MatchedWaypoint readFromStream(DataInput dis) throws IOException {
        MatchedWaypoint mwp = new MatchedWaypoint();
        mwp.node1 = new OsmNode();
        mwp.node2 = new OsmNode();
        mwp.crosspoint = new OsmNode();
        mwp.waypoint = new OsmNode();
        mwp.node1.ilat = dis.readInt();
        mwp.node1.ilon = dis.readInt();
        mwp.node2.ilat = dis.readInt();
        mwp.node2.ilon = dis.readInt();
        mwp.crosspoint.ilat = dis.readInt();
        mwp.crosspoint.ilon = dis.readInt();
        mwp.waypoint.ilat = dis.readInt();
        mwp.waypoint.ilon = dis.readInt();
        mwp.radius = dis.readDouble();
        return mwp;
    }
}

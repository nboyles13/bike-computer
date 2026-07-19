package btools.mapcreator;

import btools.util.DiffCoderDataInputStream;
import btools.util.DiffCoderDataOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class NodeData extends MapCreatorBase {
    public byte[] description;
    public int ilat;
    public int ilon;
    public long nid;
    public short selev;

    public NodeData(long id, double lon, double lat) {
        this.selev = Short.MIN_VALUE;
        this.nid = id;
        this.ilat = (int) (((90.0d + lat) * 1000000.0d) + 0.5d);
        this.ilon = (int) (((180.0d + lon) * 1000000.0d) + 0.5d);
    }

    public NodeData(DiffCoderDataInputStream dis) throws Exception {
        this.selev = Short.MIN_VALUE;
        this.nid = dis.readDiffed(0);
        this.ilon = (int) dis.readDiffed(1);
        this.ilat = (int) dis.readDiffed(2);
        int mode = dis.readByte();
        if ((mode & 1) != 0) {
            int dlen = dis.readShort();
            this.description = new byte[dlen];
            dis.readFully(this.description);
        }
        int dlen2 = mode & 2;
        if (dlen2 != 0) {
            this.selev = dis.readShort();
        }
    }

    public void writeTo(DiffCoderDataOutputStream dos) throws Exception {
        dos.writeDiffed(this.nid, 0);
        dos.writeDiffed(this.ilon, 1);
        dos.writeDiffed(this.ilat, 2);
        int mode = (this.description == null ? 0 : 1) | (this.selev != Short.MIN_VALUE ? 2 : 0);
        dos.writeByte((byte) mode);
        if ((mode & 1) != 0) {
            dos.writeShort(this.description.length);
            dos.write(this.description);
        }
        if ((mode & 2) != 0) {
            dos.writeShort(this.selev);
        }
    }
}

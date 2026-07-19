package btools.mapcreator;

import btools.util.LongList;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/* JADX INFO: loaded from: classes.dex */
public class WayData extends MapCreatorBase {
    public byte[] description;
    public LongList nodes;
    public long wid;

    public WayData(long id) {
        this.wid = id;
        this.nodes = new LongList(16);
    }

    public WayData(long id, LongList nodes) {
        this.wid = id;
        this.nodes = nodes;
    }

    public WayData(DataInputStream di) throws Exception {
        this.nodes = new LongList(16);
        this.wid = readId(di);
        int dlen = di.readByte();
        this.description = new byte[dlen];
        di.readFully(this.description);
        while (true) {
            long nid = readId(di);
            if (nid != -1) {
                this.nodes.add(nid);
            } else {
                return;
            }
        }
    }

    public void writeTo(DataOutputStream dos) throws Exception {
        writeId(dos, this.wid);
        dos.writeByte(this.description.length);
        dos.write(this.description);
        int size = this.nodes.size();
        for (int i = 0; i < size; i++) {
            writeId(dos, this.nodes.get(i));
        }
        writeId(dos, -1L);
    }
}

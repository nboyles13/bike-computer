package btools.mapcreator;

import btools.util.DiffCoderDataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class MapCreatorBase implements WayListener, NodeListener, RelationListener {
    protected File outTileDir;
    protected Map<String, String> tags;
    private DiffCoderDataOutputStream[] tileOutStreams;

    public void putTag(String key, String value) {
        if (this.tags == null) {
            this.tags = new HashMap();
        }
        this.tags.put(key, value);
    }

    public String getTag(String key) {
        if (this.tags == null) {
            return null;
        }
        return this.tags.get(key);
    }

    public Map<String, String> getTagsOrNull() {
        return this.tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    protected static long readId(DataInputStream is) throws IOException {
        int offset = is.readByte();
        if (offset == 32) {
            return -1L;
        }
        long i = is.readInt();
        return ((long) offset) | (i << 5);
    }

    protected static void writeId(DataOutputStream o, long id) throws IOException {
        if (id == -1) {
            o.writeByte(32);
            return;
        }
        int offset = (int) (31 & id);
        int i = (int) (id >> 5);
        o.writeByte(offset);
        o.writeInt(i);
    }

    protected static File[] sortBySizeAsc(File[] files) {
        int n = files.length;
        long[] sizes = new long[n];
        File[] sorted = new File[n];
        for (int i = 0; i < n; i++) {
            sizes[i] = files[i].length();
        }
        for (int nf = 0; nf < n; nf++) {
            int idx = -1;
            long min = -1;
            for (int i2 = 0; i2 < n; i2++) {
                if (sizes[i2] != -1 && (idx == -1 || sizes[i2] < min)) {
                    min = sizes[i2];
                    idx = i2;
                }
            }
            sizes[idx] = -1;
            sorted[nf] = files[idx];
        }
        return sorted;
    }

    protected File fileFromTemplate(File template, File dir, String suffix) {
        String filename = template.getName();
        return new File(dir, filename.substring(0, filename.length() - 3) + suffix);
    }

    protected DataInputStream createInStream(File inFile) throws IOException {
        return new DataInputStream(new BufferedInputStream(new FileInputStream(inFile)));
    }

    protected DiffCoderDataOutputStream createOutStream(File outFile) throws IOException {
        return new DiffCoderDataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
    }

    protected DiffCoderDataOutputStream getOutStreamForTile(int tileIndex) throws Exception {
        if (this.tileOutStreams == null) {
            this.tileOutStreams = new DiffCoderDataOutputStream[64];
        }
        if (this.tileOutStreams[tileIndex] == null) {
            this.tileOutStreams[tileIndex] = createOutStream(new File(this.outTileDir, getNameForTile(tileIndex)));
        }
        return this.tileOutStreams[tileIndex];
    }

    protected String getNameForTile(int tileIndex) {
        throw new IllegalArgumentException("getNameForTile not implemented");
    }

    protected void closeTileOutStreams() throws Exception {
        if (this.tileOutStreams == null) {
            return;
        }
        for (int tileIndex = 0; tileIndex < this.tileOutStreams.length; tileIndex++) {
            if (this.tileOutStreams[tileIndex] != null) {
                this.tileOutStreams[tileIndex].close();
            }
            this.tileOutStreams[tileIndex] = null;
        }
    }

    @Override // btools.mapcreator.NodeListener
    public void nodeFileStart(File nodefile) throws Exception {
    }

    @Override // btools.mapcreator.NodeListener
    public void nextNode(NodeData n) throws Exception {
    }

    @Override // btools.mapcreator.NodeListener
    public void nodeFileEnd(File nodefile) throws Exception {
    }

    @Override // btools.mapcreator.WayListener
    public boolean wayFileStart(File wayfile) throws Exception {
        return true;
    }

    @Override // btools.mapcreator.WayListener
    public void nextWay(WayData data) throws Exception {
    }

    @Override // btools.mapcreator.WayListener
    public void wayFileEnd(File wayfile) throws Exception {
    }

    @Override // btools.mapcreator.RelationListener
    public void nextRelation(RelationData data) throws Exception {
    }

    @Override // btools.mapcreator.RelationListener
    public void nextRestriction(RelationData data, long fromWid, long toWid, long viaNid) throws Exception {
    }
}

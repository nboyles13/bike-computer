package btools.mapcreator;

import btools.util.LongList;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import org.openstreetmap.osmosis.osmbinary.Fileformat;

/* JADX INFO: loaded from: classes.dex */
public class OsmParser extends MapCreatorBase {
    private BufferedReader _br;
    private NodeListener nListener;
    private RelationListener rListener;
    private WayListener wListener;

    public void readMap(File mapFile, NodeListener nListener, WayListener wListener, RelationListener rListener) throws Exception {
        this.nListener = nListener;
        this.wListener = wListener;
        this.rListener = rListener;
        System.out.println("*** PBF Parsing: " + String.valueOf(mapFile));
        Boolean avoidMapPolling = Boolean.valueOf(Boolean.getBoolean("avoidMapPolling"));
        if (!avoidMapPolling.booleanValue()) {
            while (!mapFile.exists()) {
                System.out.println("--- waiting for " + String.valueOf(mapFile) + " to become available");
                Thread.sleep(10000L);
            }
        }
        long currentSize = mapFile.length();
        long currentSizeTime = System.currentTimeMillis();
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(mapFile)));
        long currentSizeTime2 = currentSizeTime;
        long currentSize2 = currentSize;
        long currentSize3 = 0;
        int rawBlobCount = 0;
        while (true) {
            if (!avoidMapPolling.booleanValue()) {
                while (currentSize2 - currentSize3 < 100000000) {
                    long newSize = mapFile.length();
                    if (newSize == currentSize2) {
                        if (System.currentTimeMillis() - currentSizeTime2 > 120000) {
                            break;
                        }
                    } else {
                        currentSize2 = newSize;
                        currentSizeTime2 = System.currentTimeMillis();
                    }
                    if (currentSize2 - currentSize3 < 100000000) {
                        System.out.println("--- waiting for more data, currentSize=" + currentSize2 + " bytesRead=" + currentSize3);
                        Thread.sleep(10000L);
                    }
                }
            }
            try {
                int headerLength = dis.readInt();
                long bytesRead = currentSize3 + 4;
                byte[] headerBuffer = new byte[headerLength];
                dis.readFully(headerBuffer);
                Fileformat.BlobHeader blobHeader = Fileformat.BlobHeader.parseFrom(headerBuffer);
                byte[] blobData = new byte[blobHeader.getDatasize()];
                dis.readFully(blobData);
                currentSize3 = bytesRead + ((long) headerLength) + ((long) blobData.length);
                new BPbfBlobDecoder(blobHeader.getType(), blobData, this).process();
                rawBlobCount++;
                avoidMapPolling = avoidMapPolling;
            } catch (EOFException e) {
                dis.close();
                System.out.println("read raw blobs: " + rawBlobCount);
                return;
            }
        }
    }

    public void addNode(long nid, Map<String, String> tags, double lat, double lon) {
        NodeData n = new NodeData(nid, lon, lat);
        n.setTags((HashMap) tags);
        try {
            this.nListener.nextNode(n);
        } catch (Exception e) {
            throw new RuntimeException("error writing node: " + String.valueOf(e), e);
        }
    }

    public void addWay(long wid, Map<String, String> tags, LongList nodes) {
        WayData w = new WayData(wid, nodes);
        w.setTags((HashMap) tags);
        try {
            this.wListener.nextWay(w);
        } catch (Exception e) {
            throw new RuntimeException("error writing way: " + String.valueOf(e), e);
        }
    }

    public void addRelation(long rid, Map<String, String> tags, LongList wayIds, LongList fromWid, LongList toWid, LongList viaNid) {
        RelationData r = new RelationData(rid, wayIds);
        r.setTags((HashMap) tags);
        try {
            this.rListener.nextRelation(r);
            try {
                if (fromWid == null || toWid == null || viaNid == null || viaNid.size() != 1) {
                    RelationData r2 = r;
                    int vi = 0;
                    while (true) {
                        if (vi < (viaNid == null ? 0 : viaNid.size())) {
                            int vi2 = vi;
                            this.rListener.nextRestriction(r2, 0L, 0L, viaNid.get(vi));
                            vi = vi2 + 1;
                        } else {
                            return;
                        }
                    }
                } else {
                    int fi = 0;
                    while (fi < fromWid.size()) {
                        int ti = 0;
                        while (ti < toWid.size()) {
                            int ti2 = ti;
                            RelationData r3 = r;
                            int fi2 = fi;
                            this.rListener.nextRestriction(r, fromWid.get(fi), toWid.get(ti), viaNid.get(0));
                            ti = ti2 + 1;
                            r = r3;
                            fi = fi2;
                        }
                        fi++;
                    }
                    return;
                }
            } catch (Exception e) {
                e = e;
            }
        } catch (Exception e2) {
            e = e2;
        }
        throw new RuntimeException("error writing relation", e);
    }
}

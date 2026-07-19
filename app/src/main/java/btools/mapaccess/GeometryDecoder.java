package btools.mapaccess;

import btools.util.ByteDataReader;

/* JADX INFO: loaded from: classes.dex */
public final class GeometryDecoder {
    private OsmTransferNode firstTransferNode;
    private byte[] lastGeometry;
    private boolean lastReverse;
    private ByteDataReader r = new ByteDataReader(null);
    private int nCachedNodes = 128;
    private OsmTransferNode[] cachedNodes = new OsmTransferNode[this.nCachedNodes];

    public GeometryDecoder() {
        for (int i = 0; i < this.nCachedNodes; i++) {
            this.cachedNodes[i] = new OsmTransferNode();
        }
    }

    public OsmTransferNode decodeGeometry(byte[] geometry, OsmNode sourceNode, OsmNode targetNode, boolean reverseLink) {
        int idx;
        OsmTransferNode trans;
        if (this.lastGeometry == geometry && this.lastReverse == reverseLink) {
            return this.firstTransferNode;
        }
        this.firstTransferNode = null;
        OsmTransferNode lastTransferNode = null;
        OsmNode startnode = reverseLink ? targetNode : sourceNode;
        this.r.reset(geometry);
        int olon = startnode.ilon;
        int olat = startnode.ilat;
        int oselev = startnode.selev;
        int idx2 = 0;
        while (this.r.hasMoreData()) {
            if (idx2 < this.nCachedNodes) {
                idx = idx2 + 1;
                trans = this.cachedNodes[idx2];
            } else {
                idx = idx2;
                trans = new OsmTransferNode();
            }
            trans.ilon = this.r.readVarLengthSigned() + olon;
            trans.ilat = this.r.readVarLengthSigned() + olat;
            trans.selev = (short) (this.r.readVarLengthSigned() + oselev);
            olon = trans.ilon;
            olat = trans.ilat;
            oselev = trans.selev;
            if (reverseLink) {
                trans.next = this.firstTransferNode;
                this.firstTransferNode = trans;
            } else {
                trans.next = null;
                if (lastTransferNode == null) {
                    this.firstTransferNode = trans;
                } else {
                    lastTransferNode.next = trans;
                }
                lastTransferNode = trans;
            }
            idx2 = idx;
        }
        this.lastReverse = reverseLink;
        this.lastGeometry = geometry;
        return this.firstTransferNode;
    }
}

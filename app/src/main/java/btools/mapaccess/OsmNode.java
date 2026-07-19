package btools.mapaccess;

import btools.codec.MicroCache;
import btools.codec.MicroCache2;
import btools.util.ByteArrayUnifier;
import btools.util.CheapRuler;
import btools.util.IByteArrayUnifier;

/* JADX INFO: loaded from: classes.dex */
public class OsmNode extends OsmLink implements OsmPos {
    public TurnRestriction firstRestriction;
    public OsmLink firstlink;
    public int ilat;
    public int ilon;
    public byte[] nodeDescription;
    public short selev = Short.MIN_VALUE;
    public int visitID;

    public void addTurnRestriction(TurnRestriction tr) {
        tr.next = this.firstRestriction;
        this.firstRestriction = tr;
    }

    public OsmNode() {
    }

    public OsmNode(int ilon, int ilat) {
        this.ilon = ilon;
        this.ilat = ilat;
    }

    public OsmNode(long id) {
        this.ilon = (int) (id >> 32);
        this.ilat = (int) ((-1) & id);
    }

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

    @Override // btools.mapaccess.OsmPos
    public final double getElev() {
        return ((double) this.selev) / 4.0d;
    }

    public final void addLink(OsmLink link, boolean isReverse, OsmNode tn) {
        if (link == this.firstlink) {
            throw new IllegalArgumentException("UUUUPS");
        }
        if (isReverse) {
            link.n1 = tn;
            link.n2 = this;
            link.next = tn.firstlink;
            link.previous = this.firstlink;
            tn.firstlink = link;
            this.firstlink = link;
            return;
        }
        link.n1 = this;
        link.n2 = tn;
        link.next = this.firstlink;
        link.previous = tn.firstlink;
        tn.firstlink = link;
        this.firstlink = link;
    }

    @Override // btools.mapaccess.OsmPos
    public final int calcDistance(OsmPos p) {
        return (int) Math.max(1.0d, Math.round(CheapRuler.distance(this.ilon, this.ilat, p.getILon(), p.getILat())));
    }

    public String toString() {
        return "n_" + (this.ilon - 180000000) + "_" + (this.ilat - 90000000);
    }

    public final void parseNodeBody(MicroCache mc, OsmNodesMap hollowNodes, IByteArrayUnifier expCtxWay) {
        if (mc instanceof MicroCache2) {
            parseNodeBody2((MicroCache2) mc, hollowNodes, expCtxWay);
        } else {
            throw new IllegalArgumentException("unknown cache version: " + String.valueOf(mc.getClass()));
        }
    }

    public final void parseNodeBody2(MicroCache2 mc, OsmNodesMap hollowNodes, IByteArrayUnifier expCtxWay) {
        ByteArrayUnifier abUnifier = hollowNodes.getByteArrayUnifier();
        while (mc.readBoolean()) {
            TurnRestriction tr = new TurnRestriction();
            tr.exceptions = mc.readShort();
            tr.isPositive = mc.readBoolean();
            tr.fromLon = mc.readInt();
            tr.fromLat = mc.readInt();
            tr.toLon = mc.readInt();
            tr.toLat = mc.readInt();
            addTurnRestriction(tr);
        }
        this.selev = mc.readShort();
        int nodeDescSize = mc.readVarLengthUnsigned();
        this.nodeDescription = nodeDescSize == 0 ? null : mc.readUnified(nodeDescSize, abUnifier);
        while (mc.hasMoreData()) {
            int endPointer = mc.getEndPointer();
            int linklon = this.ilon + mc.readVarLengthSigned();
            int linklat = this.ilat + mc.readVarLengthSigned();
            int sizecode = mc.readVarLengthUnsigned();
            boolean isReverse = (sizecode & 1) != 0;
            byte[] description = null;
            int descSize = sizecode >> 1;
            if (descSize > 0) {
                description = mc.readUnified(descSize, expCtxWay);
            }
            byte[] geometry = mc.readDataUntil(endPointer);
            addLink(linklon, linklat, description, geometry, hollowNodes, isReverse);
        }
        hollowNodes.remove(this);
    }

    public void addLink(int linklon, int linklat, byte[] description, byte[] geometry, OsmNodesMap hollowNodes, boolean isReverse) {
        if (linklon == this.ilon && linklat == this.ilat) {
            return;
        }
        OsmNode tn = null;
        OsmLink link = null;
        OsmLink l = this.firstlink;
        while (l != null) {
            OsmNode t = l.getTarget(this);
            if (t.ilon == linklon && t.ilat == linklat) {
                tn = t;
                if (isReverse || (l.descriptionBitmap == null && !l.isReverse(this))) {
                    link = l;
                    break;
                }
            }
            l = l.getNext(this);
        }
        if (tn == null && (tn = hollowNodes.get(linklon, linklat)) == null) {
            tn = new OsmNode(linklon, linklat);
            tn.setHollow();
            hollowNodes.put(tn);
            link = tn;
            addLink(tn, isReverse, tn);
        }
        if (link == null) {
            OsmLink osmLink = new OsmLink();
            link = osmLink;
            addLink(osmLink, isReverse, tn);
        }
        if (!isReverse) {
            link.descriptionBitmap = description;
            link.geometry = geometry;
        }
    }

    public final boolean isHollow() {
        return this.selev == -12345;
    }

    public final void setHollow() {
        this.selev = (short) -12345;
    }

    @Override // btools.mapaccess.OsmPos
    public final long getIdFromPos() {
        return (((long) this.ilon) << 32) | ((long) this.ilat);
    }

    public void vanish() {
        if (!isHollow()) {
            OsmLink l = this.firstlink;
            while (l != null) {
                OsmNode target = l.getTarget(this);
                OsmLink nextLink = l.getNext(this);
                if (!target.isHollow()) {
                    unlinkLink(l);
                    if (!l.isLinkUnused()) {
                        target.unlinkLink(l);
                    }
                }
                l = nextLink;
            }
        }
    }

    public final void unlinkLink(OsmLink link) {
        OsmLink n = link.clear(this);
        if (link == this.firstlink) {
            this.firstlink = n;
            return;
        }
        OsmLink l = this.firstlink;
        while (l != null) {
            if (l.n1 != this && l.n1 != null) {
                OsmLink nl = l.previous;
                if (nl == link) {
                    l.previous = n;
                    return;
                }
                l = nl;
            } else if (l.n2 != this && l.n2 != null) {
                OsmLink nl2 = l.next;
                if (nl2 == link) {
                    l.next = n;
                    return;
                }
                l = nl2;
            } else {
                throw new IllegalArgumentException("unlinkLink: unknown source");
            }
        }
    }

    public final boolean equals(Object o) {
        return ((OsmNode) o).ilon == this.ilon && ((OsmNode) o).ilat == this.ilat;
    }

    public final int hashCode() {
        return this.ilon + this.ilat;
    }
}

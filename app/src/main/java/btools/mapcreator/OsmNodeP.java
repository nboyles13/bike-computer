package btools.mapcreator;

import btools.codec.MicroCache;
import btools.codec.MicroCache2;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class OsmNodeP extends OsmLinkP {
    public static final int ANY_WAY_BIT = 16;
    public static final int BORDER_BIT = 4;
    public static final int DP_SURVIVOR_BIT = 64;
    public static final int MULTI_WAY_BIT = 32;
    public static final int NO_BRIDGE_BIT = 1;
    public static final int NO_TUNNEL_BIT = 2;
    public static final int TRAFFIC_BIT = 8;
    public byte bits = 0;
    public int ilat;
    public int ilon;
    public short selev;

    public int getILat() {
        return this.ilat;
    }

    public int getILon() {
        return this.ilon;
    }

    public short getSElev() {
        if ((this.bits & 1) == 0 || (this.bits & 2) == 0) {
            return Short.MIN_VALUE;
        }
        return this.selev;
    }

    public double getElev() {
        return ((double) this.selev) / 4.0d;
    }

    public OsmLinkP createLink(OsmNodeP source) {
        if (this.sourceNode == null && this.targetNode == null) {
            this.sourceNode = source;
            this.targetNode = this;
            source.addLink(this);
            return this;
        }
        OsmLinkP link = new OsmLinkP(source, this);
        addLink(link);
        source.addLink(link);
        return link;
    }

    public void addLink(OsmLinkP link) {
        link.setNext(this.previous, this);
        this.previous = link;
    }

    public OsmLinkP getFirstLink() {
        return (this.sourceNode == null && this.targetNode == null) ? this.previous : this;
    }

    public byte[] getNodeDecsription() {
        return null;
    }

    public RestrictionData getFirstRestriction() {
        return null;
    }

    public void writeNodeData(MicroCache mc) throws IOException {
        if (mc instanceof MicroCache2) {
            boolean valid = writeNodeData2((MicroCache2) mc);
            if (valid) {
                mc.finishNode(getIdFromPos());
                return;
            } else {
                mc.discardNode();
                return;
            }
        }
        throw new IllegalArgumentException("unknown cache version: " + String.valueOf(mc.getClass()));
    }

    public void checkDuplicateTargets() {
        OsmLinkP oldLink;
        Map<OsmNodeP, OsmLinkP> targets = new HashMap<>();
        OsmLinkP link0 = getFirstLink();
        while (link0 != null) {
            OsmLinkP link = link0;
            OsmNodeP origin = this;
            OsmNodeP target = null;
            while (link != null) {
                target = link.getTarget(origin);
                if (!target.isTransferNode()) {
                    break;
                }
                link = target.getFirstLink();
                while (link != null && link.getTarget(target) == origin) {
                    link = link.getNext(target);
                }
                origin = target;
            }
            if (link != null && (oldLink = targets.put(target, link0)) != null) {
                unifyLink(oldLink);
                unifyLink(link0);
            }
            link0 = link0.getNext(this);
        }
    }

    private void unifyLink(OsmLinkP link) {
        if (link.isReverse(this)) {
            return;
        }
        OsmNodeP target = link.getTarget(this);
        if (target.isTransferNode()) {
            target.incWayCount();
        }
    }

    public boolean writeNodeData2(MicroCache2 mc) throws IOException {
        int i;
        boolean hasLinks;
        boolean hasLinks2;
        boolean hasLinks3 = false;
        RestrictionData r = getFirstRestriction();
        while (true) {
            i = 1;
            if (r == null) {
                break;
            }
            if (r.isValid() && r.fromLon != 0 && r.toLon != 0) {
                mc.writeBoolean(true);
                mc.writeShort(r.exceptions);
                mc.writeBoolean(r.isPositive());
                mc.writeInt(r.fromLon);
                mc.writeInt(r.fromLat);
                mc.writeInt(r.toLon);
                mc.writeInt(r.toLat);
            }
            r = r.next;
        }
        mc.writeBoolean(false);
        mc.writeShort(getSElev());
        mc.writeVarBytes(getNodeDecsription());
        List<OsmNodeP> internalReverse = new ArrayList<>();
        OsmLinkP link0 = getFirstLink();
        while (link0 != null) {
            OsmLinkP link = link0;
            OsmNodeP origin = this;
            OsmNodeP target = null;
            List<OsmNodeP> linkNodes = new ArrayList<>();
            linkNodes.add(this);
            while (link != null) {
                target = link.getTarget(origin);
                linkNodes.add(target);
                if (!target.isTransferNode()) {
                    break;
                }
                link = target.getFirstLink();
                while (link != null && link.getTarget(target) == origin) {
                    link = link.getNext(target);
                }
                if (link != null && link.descriptionBitmap != link0.descriptionBitmap) {
                    throw new IllegalArgumentException("assertion failed: description change along transfer nodes");
                }
                origin = target;
            }
            if (link != null && target != this) {
                boolean hasLinks4 = true;
                boolean isReverse = link0.isReverse(this);
                if (isReverse && mc.isInternal(target.ilon, target.ilat)) {
                    internalReverse.add(target);
                    hasLinks = true;
                } else {
                    byte[] description = link0.descriptionBitmap;
                    int sizeoffset = mc.writeSizePlaceHolder();
                    mc.writeVarLengthSigned(target.ilon - this.ilon);
                    mc.writeVarLengthSigned(target.ilat - this.ilat);
                    mc.writeModeAndDesc(isReverse, description);
                    if (isReverse || linkNodes.size() <= 2) {
                        hasLinks = true;
                    } else {
                        DPFilter.doDPFilter(linkNodes);
                        OsmNodeP origin2 = this;
                        int i2 = 1;
                        while (i2 < linkNodes.size() - i) {
                            OsmNodeP tranferNode = linkNodes.get(i2);
                            if ((tranferNode.bits & 64) == 0) {
                                hasLinks2 = hasLinks4;
                            } else {
                                hasLinks2 = hasLinks4;
                                mc.writeVarLengthSigned(tranferNode.ilon - origin2.ilon);
                                mc.writeVarLengthSigned(tranferNode.ilat - origin2.ilat);
                                mc.writeVarLengthSigned(tranferNode.getSElev() - origin2.getSElev());
                                origin2 = tranferNode;
                            }
                            i2++;
                            hasLinks4 = hasLinks2;
                            i = 1;
                        }
                        hasLinks = hasLinks4;
                    }
                    mc.injectSize(sizeoffset);
                }
                hasLinks3 = hasLinks;
            }
            link0 = link0.getNext(this);
            i = 1;
        }
        while (internalReverse.size() > 0) {
            int nextIdx = 0;
            if (internalReverse.size() > 1) {
                int max32 = Integer.MIN_VALUE;
                for (int i3 = 0; i3 < internalReverse.size(); i3++) {
                    int id32 = mc.shrinkId(internalReverse.get(i3).getIdFromPos());
                    if (id32 > max32) {
                        max32 = id32;
                        nextIdx = i3;
                    }
                }
            }
            OsmNodeP target2 = internalReverse.remove(nextIdx);
            int sizeoffset2 = mc.writeSizePlaceHolder();
            mc.writeVarLengthSigned(target2.ilon - this.ilon);
            mc.writeVarLengthSigned(target2.ilat - this.ilat);
            mc.writeModeAndDesc(true, null);
            mc.injectSize(sizeoffset2);
        }
        return hasLinks3;
    }

    public String toString2() {
        return (this.ilon - 180000000) + "_" + (this.ilat - 90000000) + "_" + (this.selev / 4);
    }

    public long getIdFromPos() {
        return (((long) this.ilon) << 32) | ((long) this.ilat);
    }

    public boolean isBorderNode() {
        return (this.bits & 4) != 0;
    }

    public boolean hasTraffic() {
        return (this.bits & 8) != 0;
    }

    public void incWayCount() {
        if ((this.bits & 16) != 0) {
            this.bits = (byte) (this.bits | 32);
        }
        this.bits = (byte) (this.bits | 16);
    }

    public boolean isTransferNode() {
        return (this.bits & 4) == 0 && (this.bits & 32) == 0 && _linkCnt() == 2;
    }

    private int _linkCnt() {
        int cnt = 0;
        OsmLinkP link = getFirstLink();
        while (link != null) {
            cnt++;
            link = link.getNext(this);
        }
        return cnt;
    }
}

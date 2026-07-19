package btools.router;

import btools.mapaccess.MatchedWaypoint;
import btools.mapaccess.OsmPos;
import btools.util.CompactLongMap;
import btools.util.FrozenLongMap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class OsmTrack {
    private static final String MESSAGES_HEADER = "Longitude\tLatitude\tElevation\tDistance\tCostPerKm\tElevCost\tTurnCost\tNodeCost\tInitialCost\tWayTags\tNodeTags\tTime\tEnergy";
    public static final String version = "1.7.9";
    public static final String versionDate = "22042026";
    public int ascend;
    public int cost;
    private CompactLongMap<OsmPathElementHolder> detourMap;
    public int distance;
    public MatchedWaypoint endPoint;
    public int energy;
    public boolean isDirty;
    public List<String> iternity;
    protected List<MatchedWaypoint> matchedWaypoints;
    private CompactLongMap<OsmPathElementHolder> nodesMap;
    public long[] nogoChecksums;
    public Map<String, String> params;
    public int plainAscend;
    public long profileTimestamp;
    public boolean showSpeedProfile;
    public boolean showTime;
    public boolean showspeed;
    public VoiceHintList voiceHints;
    public List<OsmNodeNamed> pois = new ArrayList();
    public List<OsmPathElement> nodes = new ArrayList();
    public String message = null;
    public List<String> messageList = null;
    public String name = "unset";
    public boolean exportWaypoints = false;
    public boolean exportCorrectedWaypoints = false;
    OsmPathElement lastorigin = null;

    public static class OsmPathElementHolder {
        public OsmPathElementHolder nextHolder;
        public OsmPathElement node;
    }

    public void addNode(OsmPathElement node) {
        this.nodes.add(0, node);
    }

    public void registerDetourForId(long id, OsmPathElement detour) {
        if (this.detourMap == null) {
            this.detourMap = new CompactLongMap<>();
        }
        OsmPathElementHolder nh = new OsmPathElementHolder();
        nh.node = detour;
        OsmPathElementHolder h = this.detourMap.get(id);
        if (h != null) {
            while (h.nextHolder != null) {
                h = h.nextHolder;
            }
            h.nextHolder = nh;
            return;
        }
        this.detourMap.fastPut(id, nh);
    }

    public void copyDetours(OsmTrack source) {
        this.detourMap = source.detourMap == null ? null : new FrozenLongMap(source.detourMap);
    }

    public void addDetours(OsmTrack source) {
        if (this.detourMap != null) {
            CompactLongMap<OsmPathElementHolder> tmpDetourMap = new CompactLongMap<>();
            ((FrozenLongMap) this.detourMap).getValueList();
            long[] oldidlist = ((FrozenLongMap) this.detourMap).getKeyArray();
            for (long id : oldidlist) {
                OsmPathElementHolder v = this.detourMap.get(id);
                tmpDetourMap.put(id, v);
            }
            if (source.detourMap != null) {
                long[] idlist = ((FrozenLongMap) source.detourMap).getKeyArray();
                for (long id2 : idlist) {
                    OsmPathElementHolder v2 = source.detourMap.get(id2);
                    if (!tmpDetourMap.contains(id2) && source.nodesMap.contains(id2)) {
                        tmpDetourMap.put(id2, v2);
                    }
                }
            }
            this.detourMap = new FrozenLongMap(tmpDetourMap);
        }
    }

    public void appendDetours(OsmTrack source) {
        if (this.detourMap == null) {
            this.detourMap = source.detourMap == null ? null : new CompactLongMap<>();
        }
        if (source.detourMap != null) {
            int pos = (this.nodes.size() - source.nodes.size()) + 1;
            if (pos > 0) {
                this.nodes.get(pos);
            }
            for (OsmPathElement node : source.nodes) {
                long id = node.getIdFromPos();
                OsmPathElementHolder nh = new OsmPathElementHolder();
                if (node.origin == null && this.lastorigin != null) {
                    node.origin = this.lastorigin;
                }
                nh.node = node;
                this.lastorigin = node;
                OsmPathElementHolder h = this.detourMap.get(id);
                if (h != null) {
                    while (h.nextHolder != null) {
                        h = h.nextHolder;
                    }
                    h.nextHolder = nh;
                } else {
                    this.detourMap.fastPut(id, nh);
                }
            }
        }
    }

    public void buildMap() {
        this.nodesMap = new CompactLongMap<>();
        for (OsmPathElement node : this.nodes) {
            long id = node.getIdFromPos();
            OsmPathElementHolder nh = new OsmPathElementHolder();
            nh.node = node;
            OsmPathElementHolder h = this.nodesMap.get(id);
            if (h != null) {
                while (h.nextHolder != null) {
                    h = h.nextHolder;
                }
                h.nextHolder = nh;
            } else {
                this.nodesMap.fastPut(id, nh);
            }
        }
        this.nodesMap = new FrozenLongMap(this.nodesMap);
    }

    public List<String> aggregateMessages() {
        List<String> res = new ArrayList<>();
        MessageData current = null;
        for (OsmPathElement n : this.nodes) {
            if (n.message != null && n.message.wayKeyValues != null) {
                MessageData md = n.message.copy();
                if (current != null) {
                    if (current.nodeKeyValues != null || !current.wayKeyValues.equals(md.wayKeyValues)) {
                        res.add(current.toMessage());
                    } else {
                        md.add(current);
                    }
                }
                current = md;
            }
        }
        if (current != null) {
            res.add(current.toMessage());
        }
        return res;
    }

    public List<String> aggregateSpeedProfile() {
        List<String> res = new ArrayList<>();
        int vmax = -1;
        int vmaxe = -1;
        int vmin = -1;
        int extraTime = 0;
        for (int i = this.nodes.size() - 1; i > 0; i--) {
            OsmPathElement n = this.nodes.get(i);
            MessageData m = n.message;
            int vnode = getVNode(i);
            if (m != null && (vmax != m.vmax || vmin != m.vmin || vmaxe != m.vmaxExplicit || vnode < m.vmax || extraTime != m.extraTime)) {
                vmax = m.vmax;
                vmin = m.vmin;
                vmaxe = m.vmaxExplicit;
                extraTime = m.extraTime;
                res.add(i + "," + vmaxe + "," + vmax + "," + vmin + "," + vnode + "," + extraTime);
            }
        }
        return res;
    }

    public void writeBinary(String filename) throws Exception {
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
        this.endPoint.writeToStream(dos);
        dos.writeInt(this.nodes.size());
        for (OsmPathElement node : this.nodes) {
            node.writeToStream(dos);
        }
        dos.writeLong(this.nogoChecksums[0]);
        dos.writeLong(this.nogoChecksums[1]);
        dos.writeLong(this.nogoChecksums[2]);
        dos.writeBoolean(this.isDirty);
        dos.writeLong(this.profileTimestamp);
        dos.close();
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x010f  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0113 A[Catch: Exception -> 0x016e, TRY_ENTER, TRY_LEAVE, TryCatch #2 {Exception -> 0x016e, blocks: (B:20:0x0080, B:25:0x00a0, B:39:0x00d7, B:55:0x0113), top: B:80:0x0080 }] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x0162  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static OsmTrack readBinary(String filename, OsmNodeNamed newEp, long[] nogoChecksums, long profileChecksum, StringBuilder debugInfo) throws IOException {
        boolean z;
        boolean nogoCheckOk;
        OsmTrack t;
        OsmTrack t2 = null;
        if (filename != null) {
            File f = new File(filename);
            if (f.exists()) {
                try {
                    DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
                    MatchedWaypoint ep = MatchedWaypoint.readFromStream(dis);
                    int dlon = ep.waypoint.ilon - newEp.ilon;
                    int dlat = ep.waypoint.ilat - newEp.ilat;
                    boolean targetMatch = dlon < 20 && dlon > -20 && dlat < 20 && dlat > -20;
                    if (debugInfo != null) {
                        debugInfo.append("target-delta = " + dlon + "/" + dlat + " targetMatch=" + targetMatch);
                    }
                    if (targetMatch) {
                        t2 = new OsmTrack();
                        try {
                            t2.endPoint = ep;
                            int n = dis.readInt();
                            OsmPathElement last_pe = null;
                            for (int i = 0; i < n; i++) {
                                OsmPathElement pe = OsmPathElement.readFromStream(dis);
                                pe.origin = last_pe;
                                last_pe = pe;
                                t2.nodes.add(pe);
                            }
                            int i2 = last_pe.cost;
                            t2.cost = i2;
                            t2.buildMap();
                            long[] al = new long[3];
                            long pchecksum = 0;
                            try {
                                al[0] = dis.readLong();
                                al[1] = dis.readLong();
                                al[2] = dis.readLong();
                            } catch (EOFException e) {
                            }
                            try {
                                t2.isDirty = dis.readBoolean();
                            } catch (EOFException e2) {
                            }
                            try {
                                pchecksum = dis.readLong();
                            } catch (EOFException e3) {
                            }
                            if (Math.abs(al[0] - nogoChecksums[0]) > 20) {
                                z = true;
                            } else {
                                z = true;
                                if (Math.abs(al[1] - nogoChecksums[1]) <= 20 && Math.abs(al[2] - nogoChecksums[2]) <= 20) {
                                    nogoCheckOk = true;
                                }
                                boolean profileCheckOk = pchecksum == profileChecksum ? z : false;
                                if (debugInfo != null) {
                                    t = t2;
                                } else {
                                    debugInfo.append(" nogoCheckOk=" + nogoCheckOk + " profileCheckOk=" + profileCheckOk);
                                    t = t2;
                                    try {
                                        debugInfo.append(" al=" + formatLongs(al) + " nogoChecksums=" + formatLongs(nogoChecksums));
                                    } catch (Exception e4) {
                                        e = e4;
                                        t2 = t;
                                        if (debugInfo != null) {
                                            debugInfo.append("Error reading rawTrack: " + String.valueOf(e));
                                        }
                                    }
                                }
                                if (!nogoCheckOk && profileCheckOk) {
                                    t2 = t;
                                }
                            }
                            nogoCheckOk = false;
                            if (pchecksum == profileChecksum) {
                            }
                            if (debugInfo != null) {
                            }
                            return !nogoCheckOk ? null : null;
                        } catch (Exception e5) {
                            e = e5;
                        }
                    }
                    dis.close();
                } catch (Exception e6) {
                    e = e6;
                }
            }
        }
        return t2;
    }

    private static String formatLongs(long[] al) {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (long l : al) {
            sb.append(l);
            sb.append(' ');
        }
        sb.append('}');
        return sb.toString();
    }

    public void addNodes(OsmTrack t) {
        for (OsmPathElement n : t.nodes) {
            addNode(n);
        }
        buildMap();
    }

    public boolean containsNode(OsmPos node) {
        return this.nodesMap.contains(node.getIdFromPos());
    }

    public OsmPathElement getLink(long n1, long n2) {
        for (OsmPathElementHolder h = this.nodesMap.get(n2); h != null; h = h.nextHolder) {
            OsmPathElement e1 = h.node.origin;
            if (e1 != null && e1.getIdFromPos() == n1) {
                return h.node;
            }
        }
        return null;
    }

    public void appendTrack(OsmTrack t) {
        int ourSize = this.nodes.size();
        if (ourSize > 0 && t.nodes.size() > 1) {
            OsmPathElement olde = this.nodes.get(ourSize - 1);
            t.nodes.get(1).origin = olde;
        }
        float t0 = ourSize > 0 ? this.nodes.get(ourSize - 1).getTime() : 0.0f;
        float e0 = ourSize > 0 ? this.nodes.get(ourSize - 1).getEnergy() : 0.0f;
        int c0 = ourSize > 0 ? this.nodes.get(ourSize - 1).cost : 0;
        for (int i = 0; i < t.nodes.size(); i++) {
            OsmPathElement e = t.nodes.get(i);
            if (i == 0 && ourSize > 0 && this.nodes.get(ourSize - 1).getSElev() == Short.MIN_VALUE) {
                this.nodes.get(ourSize - 1).setSElev(e.getSElev());
            }
            if (i > 0 || ourSize == 0) {
                e.setTime(e.getTime() + t0);
                e.setEnergy(e.getEnergy() + e0);
                e.cost += c0;
                if (e.message != null && (e.message.lon != e.getILon() || e.message.lat != e.getILat())) {
                    e.message.lon = e.getILon();
                    e.message.lat = e.getILat();
                }
                this.nodes.add(e);
            }
        }
        if (t.voiceHints != null) {
            if (ourSize > 0) {
                for (VoiceHint hint : t.voiceHints.list) {
                    hint.indexInTrack = (hint.indexInTrack + ourSize) - 1;
                }
            }
            if (this.voiceHints == null) {
                this.voiceHints = t.voiceHints;
            } else {
                this.voiceHints.list.addAll(t.voiceHints.list);
            }
        } else if (this.detourMap == null) {
            this.detourMap = t.detourMap;
        } else {
            addDetours(t);
        }
        this.distance += t.distance;
        this.ascend += t.ascend;
        this.plainAscend += t.plainAscend;
        this.cost += t.cost;
        this.energy = (int) this.nodes.get(this.nodes.size() - 1).getEnergy();
        this.showspeed |= t.showspeed;
        this.showSpeedProfile |= t.showSpeedProfile;
    }

    public VoiceHint getVoiceHint(int i) {
        if (this.voiceHints == null) {
            return null;
        }
        for (VoiceHint hint : this.voiceHints.list) {
            if (hint.indexInTrack == i) {
                return hint;
            }
        }
        return null;
    }

    public MatchedWaypoint getMatchedWaypoint(int idx) {
        if (this.matchedWaypoints == null) {
            return null;
        }
        for (MatchedWaypoint wp : this.matchedWaypoints) {
            if (idx == wp.indexInTrack) {
                return wp;
            }
        }
        return null;
    }

    private int getVNode(int i) {
        MessageData m1 = i + 1 < this.nodes.size() ? this.nodes.get(i + 1).message : null;
        MessageData m0 = i < this.nodes.size() ? this.nodes.get(i).message : null;
        int vnode0 = m1 == null ? 999 : m1.vnode0;
        int vnode1 = m0 != null ? m0.vnode1 : 999;
        return vnode0 < vnode1 ? vnode0 : vnode1;
    }

    public int getTotalSeconds() {
        float s = this.nodes.size() < 2 ? 0.0f : this.nodes.get(this.nodes.size() - 1).getTime() - this.nodes.get(0).getTime();
        return (int) (((double) s) + 0.5d);
    }

    public boolean equalsTrack(OsmTrack t) {
        if (this.nodes.size() != t.nodes.size()) {
            return false;
        }
        for (int i = 0; i < this.nodes.size(); i++) {
            OsmPathElement e1 = this.nodes.get(i);
            OsmPathElement e2 = t.nodes.get(i);
            if (e1.getILon() != e2.getILon() || e1.getILat() != e2.getILat()) {
                return false;
            }
        }
        return true;
    }

    public OsmPathElementHolder getFromDetourMap(long id) {
        if (this.detourMap == null) {
            return null;
        }
        return this.detourMap.get(id);
    }

    public void prepareSpeedProfile(RoutingContext rc) {
    }

    public void processVoiceHints(RoutingContext rc) {
        MatchedWaypoint mwpt;
        this.voiceHints = new VoiceHintList();
        this.voiceHints.setTransportMode(rc.carMode, rc.bikeMode);
        this.voiceHints.turnInstructionMode = rc.turnInstructionMode;
        if (this.detourMap == null && !rc.hasDirectRouting) {
            return;
        }
        int nodeNr = this.nodes.size() - 1;
        for (OsmPathElement node = this.nodes.get(nodeNr); node != null; node = node.origin) {
        }
        OsmPathElement node2 = this.nodes.get(nodeNr);
        List<VoiceHint> inputs = new ArrayList<>();
        for (OsmPathElement node3 = node2; node3 != null; node3 = node3.origin) {
            if (node3.origin != null) {
                if (nodeNr == this.nodes.size() - 1) {
                    VoiceHint input = new VoiceHint();
                    inputs.add(0, input);
                    input.ilat = node3.getILat();
                    input.ilon = node3.getILon();
                    input.selev = node3.getSElev();
                    input.goodWay = node3.message;
                    input.oldWay = node3.message;
                    input.indexInTrack = this.nodes.size() - 1;
                    input.cmd = 100;
                }
                VoiceHint input2 = new VoiceHint();
                inputs.add(input2);
                input2.ilat = node3.origin.getILat();
                input2.ilon = node3.origin.getILon();
                input2.selev = node3.origin.getSElev();
                nodeNr--;
                input2.indexInTrack = nodeNr;
                input2.goodWay = node3.message;
                input2.oldWay = node3.origin.message == null ? node3.message : node3.origin.message;
                if ((rc.turnInstructionMode == 8 || rc.turnInstructionMode == 4 || rc.turnInstructionMode == 2 || rc.turnInstructionMode == 9) && (mwpt = getMatchedWaypoint(nodeNr)) != null && mwpt.wpttype == 3) {
                    input2.cmd = 16;
                    input2.angle = (nodeNr == 0 ? node3.origin.message : node3.message).turnangle;
                    input2.distanceToNext = node3.calcDistance(node3.origin);
                }
                if (this.detourMap != null) {
                    OsmPathElementHolder detours = this.detourMap.get(node3.origin.getIdFromPos());
                    if (nodeNr >= 0 && detours != null) {
                        for (OsmPathElementHolder h = detours; h != null; h = h.nextHolder) {
                            OsmPathElement e = h.node;
                            input2.addBadWay(startSection(e, node3.origin));
                        }
                    }
                }
            }
        }
        int transportMode = this.voiceHints.transportMode();
        VoiceHintProcessor vproc = new VoiceHintProcessor(rc.turnInstructionCatchingRange, rc.turnInstructionRoundabouts, transportMode);
        List<VoiceHint> results = vproc.process(inputs);
        double minDistance = getMinDistance();
        List<VoiceHint> resultsLast = vproc.postProcess(results, rc.turnInstructionCatchingRange, minDistance);
        for (VoiceHint hint : resultsLast) {
            this.voiceHints.list.add(hint);
        }
    }

    int getMinDistance() {
        if (this.voiceHints != null) {
            switch (this.voiceHints.transportMode()) {
                case 1:
                    return 3;
                case 2:
                default:
                    return 5;
                case 3:
                    return 20;
            }
        }
        return 2;
    }

    public float getVoiceHintTime(int i) {
        if (!this.voiceHints.list.isEmpty() && i < this.voiceHints.list.size()) {
            return this.voiceHints.list.get(i).getTime();
        }
        if (this.nodes.isEmpty()) {
            return 0.0f;
        }
        return this.nodes.get(this.nodes.size() - 1).getTime();
    }

    public void removeVoiceHint(int i) {
        if (this.voiceHints != null) {
            VoiceHint remove = null;
            for (VoiceHint vh : this.voiceHints.list) {
                if (vh.indexInTrack == i) {
                    remove = vh;
                }
            }
            if (remove != null) {
                this.voiceHints.list.remove(remove);
            }
        }
    }

    private MessageData startSection(OsmPathElement element, OsmPathElement root) {
        OsmPathElement e = element;
        int cnt = 0;
        while (e != null && e.origin != null) {
            if (e.origin.getILat() == root.getILat() && e.origin.getILon() == root.getILon()) {
                return e.message;
            }
            e = e.origin;
            int cnt2 = cnt + 1;
            if (cnt == 1000000) {
                throw new IllegalArgumentException("ups: " + String.valueOf(root) + "->" + String.valueOf(element));
            }
            cnt = cnt2;
        }
        return null;
    }
}

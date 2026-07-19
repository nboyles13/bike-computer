package btools.mapaccess;

import btools.router.OsmNogoPolygon;
import btools.util.ByteArrayUnifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import okhttp3.internal.http2.Http2Connection;

/* JADX INFO: loaded from: classes.dex */
public final class OsmNodesMap {
    public int currentPathCost;
    public OsmNode destination;
    public OsmNode endNode1;
    public OsmNode endNode2;
    public long maxmem;
    private List<OsmNode> nodes2check;
    public int nodesCreated;
    private Map<OsmNode, OsmNode> hmap = new HashMap(4096);
    private ByteArrayUnifier abUnifier = new ByteArrayUnifier(16384, false);
    private OsmNode testKey = new OsmNode();
    private long currentmaxmem = 4000000;
    public int lastVisitID = 1000;
    public int baseID = 1000;
    public int currentMaxCost = Http2Connection.DEGRADED_PONG_TIMEOUT_NS;
    public int cleanupMode = 0;

    public void cleanupAndCount(OsmNode[] nodes) {
        if (this.cleanupMode == 0) {
            justCount(nodes);
        } else {
            cleanupPeninsulas(nodes);
        }
    }

    private void justCount(OsmNode[] nodes) {
        for (OsmNode n : nodes) {
            if (n.firstlink != null) {
                this.nodesCreated++;
            }
        }
    }

    private void cleanupPeninsulas(OsmNode[] nodes) {
        int i = this.lastVisitID;
        this.lastVisitID = i + 1;
        this.baseID = i;
        for (OsmNode n : nodes) {
            if (n.firstlink != null && n.visitID == 1) {
                try {
                    minVisitIdInSubtree(null, n);
                } catch (StackOverflowError e) {
                }
            }
        }
    }

    private int minVisitIdInSubtree(OsmNode source, OsmNode n) {
        if (n.visitID == 1) {
            n.visitID = this.baseID;
        } else {
            int i = this.lastVisitID;
            this.lastVisitID = i + 1;
            n.visitID = i;
        }
        int minId = n.visitID;
        this.nodesCreated++;
        OsmLink l = n.firstlink;
        while (l != null) {
            OsmLink nextLink = l.getNext(n);
            OsmNode t = l.getTarget(n);
            if (t != source && !t.isHollow()) {
                int minIdSub = t.visitID;
                if (minIdSub == 1) {
                    minIdSub = this.baseID;
                } else if (minIdSub == 0) {
                    int nodesCreatedUntilHere = this.nodesCreated;
                    minIdSub = minVisitIdInSubtree(n, t);
                    if (minIdSub > n.visitID) {
                        this.nodesCreated = nodesCreatedUntilHere;
                        n.unlinkLink(l);
                        t.unlinkLink(l);
                    }
                } else if (minIdSub >= this.baseID) {
                    if (this.cleanupMode == 2) {
                        minIdSub = this.baseID;
                    }
                }
                if (minIdSub < minId) {
                    minId = minIdSub;
                }
            }
            l = nextLink;
        }
        return minId;
    }

    public boolean isInMemoryBounds(int npaths, boolean extend) {
        long total = (((long) this.nodesCreated) * 95) + (((long) npaths) * 200);
        if (extend) {
            total += 100000;
            long delta = (1900000 + total) - this.currentmaxmem;
            if (delta > 0) {
                this.currentmaxmem += delta;
                if (this.currentmaxmem > this.maxmem) {
                    this.currentmaxmem = this.maxmem;
                }
            }
        }
        return total <= this.currentmaxmem;
    }

    public boolean canEscape(OsmNode n0) {
        boolean sawLowIDs = false;
        this.lastVisitID++;
        this.nodes2check.clear();
        this.nodes2check.add(n0);
        while (!this.nodes2check.isEmpty()) {
            OsmNode n = this.nodes2check.remove(this.nodes2check.size() - 1);
            if (n.visitID < this.baseID) {
                n.visitID = this.lastVisitID;
                this.nodesCreated++;
                for (OsmLink l = n.firstlink; l != null; l = l.getNext(n)) {
                    OsmNode t = l.getTarget(n);
                    this.nodes2check.add(t);
                }
            } else if (n.visitID < this.lastVisitID) {
                sawLowIDs = true;
            }
        }
        if (sawLowIDs) {
            return true;
        }
        this.nodes2check.add(n0);
        while (!this.nodes2check.isEmpty()) {
            OsmNode n2 = this.nodes2check.remove(this.nodes2check.size() - 1);
            if (n2.visitID == this.lastVisitID) {
                n2.visitID = this.lastVisitID;
                this.nodesCreated--;
                for (OsmLink l2 = n2.firstlink; l2 != null; l2 = l2.getNext(n2)) {
                    OsmNode t2 = l2.getTarget(n2);
                    this.nodes2check.add(t2);
                }
                n2.vanish();
            }
        }
        return false;
    }

    private void addActiveNode(List<OsmNode> nodes2check, OsmNode n) {
        n.visitID = this.lastVisitID;
        this.nodesCreated++;
        nodes2check.add(n);
    }

    public void clearTemp() {
        this.nodes2check = null;
    }

    public void collectOutreachers() {
        this.nodes2check = new ArrayList(this.nodesCreated);
        this.nodesCreated = 0;
        Iterator<OsmNode> it = this.hmap.values().iterator();
        while (it.hasNext()) {
            addActiveNode(this.nodes2check, it.next());
        }
        this.lastVisitID++;
        this.baseID = this.lastVisitID;
        while (!this.nodes2check.isEmpty()) {
            OsmNode n = this.nodes2check.remove(this.nodes2check.size() - 1);
            n.visitID = this.lastVisitID;
            for (OsmLink l = n.firstlink; l != null; l = l.getNext(n)) {
                OsmNode t = l.getTarget(n);
                if (t.visitID != this.lastVisitID) {
                    addActiveNode(this.nodes2check, t);
                }
            }
            OsmLink l2 = this.destination;
            if (l2 != null && this.currentMaxCost < 1000000000) {
                int distance = n.calcDistance(this.destination);
                if (distance > (this.currentMaxCost - this.currentPathCost) + 100) {
                    n.vanish();
                }
            }
            if (n.firstlink == null) {
                this.nodesCreated--;
            }
        }
    }

    public ByteArrayUnifier getByteArrayUnifier() {
        return this.abUnifier;
    }

    public OsmNode get(int ilon, int ilat) {
        this.testKey.ilon = ilon;
        this.testKey.ilat = ilat;
        return this.hmap.get(this.testKey);
    }

    public void remove(OsmNode node) {
        if (node != this.endNode1 && node != this.endNode2) {
            this.hmap.remove(node);
        }
    }

    public OsmNode put(OsmNode node) {
        return this.hmap.put(node, node);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void addLinks(OsmNode[] osmNodeArr, int i, boolean z, int[] iArr) {
        OsmNogoPolygon osmNogoPolygon = osmNodeArr[i];
        osmNogoPolygon.visitID = z ? 1 : 0;
        osmNogoPolygon.selev = (short) i;
        for (int i2 : iArr) {
            OsmNode osmNode = osmNodeArr[i2];
            OsmLink osmLink = osmNogoPolygon.isLinkUnused() ? osmNogoPolygon : osmNode.isLinkUnused() ? osmNode : null;
            if (osmLink == null) {
                osmLink = new OsmLink();
            }
            osmNogoPolygon.addLink(osmLink, false, osmNode);
        }
    }

    public static void main(String[] args) {
        OsmNode[] nodes = new OsmNode[12];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new OsmNode((i + 1000) * 1000, (i + 1000) * 1000);
        }
        addLinks(nodes, 0, true, new int[]{1, 5});
        addLinks(nodes, 1, true, new int[0]);
        addLinks(nodes, 2, false, new int[]{3, 4});
        addLinks(nodes, 3, false, new int[]{4});
        addLinks(nodes, 4, false, new int[0]);
        addLinks(nodes, 5, true, new int[]{6, 9});
        addLinks(nodes, 6, false, new int[]{7, 8});
        addLinks(nodes, 7, false, new int[0]);
        addLinks(nodes, 8, false, new int[0]);
        addLinks(nodes, 9, false, new int[]{10, 11});
        addLinks(nodes, 10, false, new int[]{11});
        addLinks(nodes, 11, false, new int[0]);
        OsmNodesMap nm = new OsmNodesMap();
        nm.cleanupMode = 2;
        nm.cleanupAndCount(nodes);
        System.out.println("nodesCreated=" + nm.nodesCreated);
        nm.cleanupAndCount(nodes);
        System.out.println("nodesCreated=" + nm.nodesCreated);
    }
}

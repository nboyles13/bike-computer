package btools.server;

import btools.mapaccess.OsmNode;
import btools.router.OsmNodeNamed;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class NearRecentWps {
    private OsmNodeNamed[] recentWaypoints = new OsmNodeNamed[2000];
    private int nextRecentIndex = 0;

    public void add(List<OsmNodeNamed> wplist) {
        synchronized (this.recentWaypoints) {
            for (OsmNodeNamed wp : wplist) {
                add(wp);
            }
        }
    }

    public void add(OsmNodeNamed wp) {
        OsmNodeNamed[] osmNodeNamedArr = this.recentWaypoints;
        int i = this.nextRecentIndex;
        this.nextRecentIndex = i + 1;
        osmNodeNamedArr[i] = wp;
        if (this.nextRecentIndex >= this.recentWaypoints.length) {
            this.nextRecentIndex = 0;
        }
    }

    public int count(long id) {
        int cnt = 0;
        OsmNode n = new OsmNode(id);
        synchronized (this.recentWaypoints) {
            for (int i = 0; i < this.recentWaypoints.length; i++) {
                OsmNodeNamed nn = this.recentWaypoints[i];
                if (nn != null && nn.calcDistance(n) < 4000) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    public OsmNodeNamed closest(long id) {
        int d;
        int dmin = 0;
        OsmNodeNamed nc = null;
        OsmNode n = new OsmNode(id);
        synchronized (this.recentWaypoints) {
            for (int i = 0; i < this.recentWaypoints.length; i++) {
                OsmNodeNamed nn = this.recentWaypoints[i];
                if (nn != null && (d = nn.calcDistance(n)) < 4000 && (nc == null || d < dmin)) {
                    dmin = d;
                    nc = nn;
                }
            }
        }
        return nc;
    }
}

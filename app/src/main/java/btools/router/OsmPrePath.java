package btools.router;

import btools.mapaccess.OsmLink;
import btools.mapaccess.OsmNode;

/* JADX INFO: loaded from: classes.dex */
public abstract class OsmPrePath {
    protected OsmLink link;
    public OsmPrePath next;
    protected OsmNode sourceNode;
    protected OsmNode targetNode;

    protected abstract void initPrePath(OsmPath osmPath, RoutingContext routingContext);

    public void init(OsmPath origin, OsmLink link, RoutingContext rc) {
        this.link = link;
        this.sourceNode = origin.getTargetNode();
        this.targetNode = link.getTarget(this.sourceNode);
        initPrePath(origin, rc);
    }
}

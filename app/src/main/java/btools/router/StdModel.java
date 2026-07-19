package btools.router;

import btools.expressions.BExpressionContextNode;
import btools.expressions.BExpressionContextWay;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
final class StdModel extends OsmPathModel {
    protected BExpressionContextNode ctxNode;
    protected BExpressionContextWay ctxWay;

    StdModel() {
    }

    @Override // btools.router.OsmPathModel
    public OsmPrePath createPrePath() {
        return null;
    }

    @Override // btools.router.OsmPathModel
    public OsmPath createPath() {
        return new StdPath();
    }

    @Override // btools.router.OsmPathModel
    public void init(BExpressionContextWay expctxWay, BExpressionContextNode expctxNode, Map<String, String> keyValues) {
        this.ctxWay = expctxWay;
        this.ctxNode = expctxNode;
    }
}

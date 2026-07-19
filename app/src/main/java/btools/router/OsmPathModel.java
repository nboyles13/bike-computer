package btools.router;

import btools.expressions.BExpressionContextNode;
import btools.expressions.BExpressionContextWay;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
abstract class OsmPathModel {
    public abstract OsmPath createPath();

    public abstract OsmPrePath createPrePath();

    public abstract void init(BExpressionContextWay bExpressionContextWay, BExpressionContextNode bExpressionContextNode, Map<String, String> map);

    OsmPathModel() {
    }
}

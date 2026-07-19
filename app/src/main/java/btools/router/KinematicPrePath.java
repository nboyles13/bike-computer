package btools.router;

import btools.mapaccess.OsmNode;
import btools.mapaccess.OsmTransferNode;

/* JADX INFO: loaded from: classes.dex */
final class KinematicPrePath extends OsmPrePath {
    public double angle;
    public int classifiermask;
    public int priorityclassifier;

    KinematicPrePath() {
    }

    @Override // btools.router.OsmPrePath
    protected void initPrePath(OsmPath origin, RoutingContext rc) {
        int lon2;
        int lat2;
        byte[] description = this.link.descriptionBitmap;
        if (description == null) {
            description = this.targetNode.descriptionBitmap != null ? this.targetNode.descriptionBitmap : new byte[]{0, 1, 0};
        }
        int lon0 = origin.originLon;
        int lat0 = origin.originLat;
        OsmNode p1 = this.sourceNode;
        int lon1 = p1.getILon();
        int lat1 = p1.getILat();
        boolean isReverse = this.link.isReverse(this.sourceNode);
        rc.expctxWay.evaluate(rc.inverseDirection ^ isReverse, description);
        OsmTransferNode transferNode = this.link.geometry == null ? null : rc.geometryDecoder.decodeGeometry(this.link.geometry, p1, this.targetNode, isReverse);
        if (transferNode == null) {
            int lon22 = this.targetNode.ilon;
            lon2 = lon22;
            lat2 = this.targetNode.ilat;
        } else {
            int lon23 = transferNode.ilon;
            lon2 = lon23;
            lat2 = transferNode.ilat;
        }
        rc.calcDistance(lon1, lat1, lon2, lat2);
        this.angle = rc.anglemeter.calcAngle(lon0, lat0, lon1, lat1, lon2, lat2);
        this.priorityclassifier = (int) rc.expctxWay.getPriorityClassifier();
        this.classifiermask = (int) rc.expctxWay.getClassifierMask();
    }
}

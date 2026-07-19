package btools.router;

import btools.mapaccess.OsmLink;
import btools.mapaccess.OsmLinkHolder;
import btools.mapaccess.OsmNode;
import btools.mapaccess.OsmTransferNode;
import btools.mapaccess.TurnRestriction;
import btools.util.CheapRuler;

/* JADX INFO: loaded from: classes.dex */
abstract class OsmPath implements OsmLinkHolder {
    private static final int CAN_LEAVE_DESTINATION_BIT = 2;
    private static final int HAD_DESTINATION_START_BIT = 8;
    private static final int IS_ON_DESTINATION_BIT = 4;
    private static final int PATH_START_BIT = 1;
    static int seg = 1;
    protected float lastClassifier;
    protected float lastInitialCost;
    protected OsmLink link;
    public MessageData message;
    public OsmPathElement myElement;
    public OsmPathElement originElement;
    public int originLat;
    public int originLon;
    protected int priorityclassifier;
    public short selev;
    protected OsmNode sourceNode;
    protected OsmNode targetNode;
    public int cost = 0;
    public int airdistance = 0;
    private OsmLinkHolder nextForLink = null;
    public int treedepth = 0;
    protected int bitfield = 1;

    public abstract boolean definitlyWorseThan(OsmPath osmPath);

    public abstract int elevationCorrection();

    protected abstract void init(OsmPath osmPath);

    protected abstract double processTargetNode(RoutingContext routingContext);

    protected abstract double processWaySection(RoutingContext routingContext, double d, double d2, double d3, double d4, double d5, boolean z, int i, int i2);

    protected abstract void resetState();

    OsmPath() {
    }

    private boolean getBit(int mask) {
        return (this.bitfield & mask) != 0;
    }

    private void setBit(int mask, boolean bit) {
        if (getBit(mask) != bit) {
            this.bitfield ^= mask;
        }
    }

    public boolean didEnterDestinationArea() {
        return !getBit(8) && getBit(4);
    }

    public void init(OsmLink link) {
        this.link = link;
        this.targetNode = link.getTarget(null);
        this.selev = this.targetNode.getSElev();
        this.originLon = -1;
        this.originLat = -1;
    }

    public void init(OsmPath origin, OsmLink link, OsmTrack refTrack, boolean detailMode, RoutingContext rc) {
        if (origin.myElement == null) {
            origin.myElement = OsmPathElement.create(origin);
        }
        this.originElement = origin.myElement;
        this.link = link;
        this.sourceNode = origin.targetNode;
        this.targetNode = link.getTarget(this.sourceNode);
        this.cost = origin.cost;
        this.lastClassifier = origin.lastClassifier;
        this.lastInitialCost = origin.lastInitialCost;
        this.bitfield = origin.bitfield;
        this.priorityclassifier = origin.priorityclassifier;
        init(origin);
        addAddionalPenalty(refTrack, detailMode, origin, link, rc);
    }

    /* JADX WARN: Code restructure failed: missing block: B:105:0x026d, code lost:
    
        r15.cost = -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:106:0x0270, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:214:0x0606, code lost:
    
        r2.cost = -1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:215:0x0609, code lost:
    
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected void addAddionalPenalty(OsmTrack refTrack, boolean detailMode, OsmPath origin, OsmLink link, RoutingContext rc) {
        int lon0;
        boolean isReverse;
        int lon1;
        int lastpriorityclassifier;
        int classifiermask;
        boolean isReverse2;
        OsmTransferNode transferNode;
        float newClassifier;
        int lon2;
        int lat2;
        short originEle2;
        boolean isStartpoint;
        boolean isReverse3;
        boolean newDestination;
        double d;
        byte[] description;
        short ele1;
        int lat0;
        short ele12;
        int dist;
        int lat02;
        OsmTransferNode transferNode2;
        boolean z;
        boolean z2;
        int lon02;
        double delta_h;
        OsmPath osmPath;
        RoutingContext routingContext;
        short originEle22;
        int lat22;
        boolean isReverse4;
        int classifiermask2;
        int lon22;
        byte[] description2;
        int lat03;
        int lon03;
        OsmPath osmPath2 = this;
        RoutingContext routingContext2 = rc;
        byte[] description3 = link.descriptionBitmap;
        if (description3 == null) {
            osmPath2.message = new MessageData();
            if (osmPath2.message != null) {
                osmPath2.message.turnangle = 0.0f;
                osmPath2.message.time = 1.0f;
                osmPath2.message.energy = 0.0f;
                osmPath2.message.priorityclassifier = 0;
                osmPath2.message.classifiermask = 0;
                osmPath2.message.lon = osmPath2.targetNode.getILon();
                osmPath2.message.lat = osmPath2.targetNode.getILat();
                osmPath2.message.ele = Short.MIN_VALUE;
                osmPath2.message.linkdist = osmPath2.sourceNode.calcDistance(osmPath2.targetNode);
                osmPath2.message.wayKeyValues = "direct_segment=" + seg;
                seg++;
                return;
            }
            return;
        }
        routingContext2.nogoCost = 0.0d;
        int lon04 = origin.originLon;
        int lat04 = origin.originLat;
        int lon12 = osmPath2.sourceNode.getILon();
        int lat1 = osmPath2.sourceNode.getILat();
        short ele13 = origin.selev;
        osmPath2.message = detailMode ? new MessageData() : null;
        boolean isReverse5 = link.isReverse(osmPath2.sourceNode);
        routingContext2.expctxWay.evaluate(routingContext2.inverseDirection ^ isReverse5, description3);
        if (routingContext2.ai != null) {
            lon0 = lon04;
            isReverse = isReverse5;
            if (routingContext2.ai.polygon.isWithin(lon12, lat1)) {
                routingContext2.ai.checkAreaInfo(routingContext2.expctxWay, ((double) ele13) / 4.0d, description3);
            }
        } else {
            lon0 = lon04;
            isReverse = isReverse5;
        }
        float costfactor = routingContext2.expctxWay.getCostfactor();
        boolean isTrafficBackbone = osmPath2.cost == 0 && routingContext2.expctxWay.getIsTrafficBackbone() > 0.0f;
        int lastpriorityclassifier2 = osmPath2.priorityclassifier;
        osmPath2.priorityclassifier = (int) routingContext2.expctxWay.getPriorityClassifier();
        float newClassifier2 = routingContext2.expctxWay.getInitialClassifier();
        float newInitialCost = routingContext2.expctxWay.getInitialcost();
        float classifierDiff = newClassifier2 - osmPath2.lastClassifier;
        float costfactor2 = costfactor;
        if (newClassifier2 != 0.0d) {
            lon1 = lon12;
            if (osmPath2.lastClassifier != 0.0d && (classifierDiff > 5.0E-4d || classifierDiff < -5.0E-4d)) {
                float initialcost = routingContext2.inverseDirection ? osmPath2.lastInitialCost : newInitialCost;
                if (initialcost >= 1000000.0d) {
                    osmPath2.cost = -1;
                    return;
                }
                int iicost = (int) initialcost;
                if (osmPath2.message != null) {
                    osmPath2.message.linkinitcost += iicost;
                }
                osmPath2.cost += iicost;
            }
        } else {
            lon1 = lon12;
        }
        osmPath2.lastClassifier = newClassifier2;
        osmPath2.lastInitialCost = newInitialCost;
        int classifiermask3 = (int) routingContext2.expctxWay.getClassifierMask();
        boolean newDestination2 = (classifiermask3 & 64) != 0;
        boolean oldDestination = osmPath2.getBit(4);
        if (osmPath2.getBit(1)) {
            osmPath2.setBit(1, false);
            osmPath2.setBit(2, newDestination2);
            osmPath2.setBit(8, newDestination2);
        } else if (oldDestination && !newDestination2) {
            if (osmPath2.getBit(2)) {
                osmPath2.setBit(2, false);
            } else {
                osmPath2.cost = -1;
                return;
            }
        }
        osmPath2.setBit(4, newDestination2);
        if (link.geometry == null) {
            lastpriorityclassifier = lastpriorityclassifier2;
            classifiermask = classifiermask3;
            isReverse2 = isReverse;
            transferNode = null;
        } else {
            lastpriorityclassifier = lastpriorityclassifier2;
            classifiermask = classifiermask3;
            isReverse2 = isReverse;
            transferNode = routingContext2.geometryDecoder.decodeGeometry(link.geometry, osmPath2.sourceNode, osmPath2.targetNode, isReverse2);
        }
        int lat12 = lat1;
        short ele14 = ele13;
        int lat13 = 0;
        OsmTransferNode transferNode3 = transferNode;
        int linkdisttotal = 0;
        int lon05 = lon0;
        int lon06 = lat04;
        int lon07 = lon1;
        while (true) {
            osmPath2.originLon = lon07;
            osmPath2.originLat = lat12;
            if (transferNode3 == null) {
                newClassifier = newClassifier2;
                int lon23 = osmPath2.targetNode.ilon;
                lon2 = lon23;
                int lat23 = osmPath2.targetNode.ilat;
                lat2 = lat23;
                originEle2 = osmPath2.targetNode.selev;
            } else {
                newClassifier = newClassifier2;
                int lon24 = transferNode3.ilon;
                lon2 = lon24;
                int lon25 = transferNode3.ilat;
                lat2 = lon25;
                originEle2 = transferNode3.selev;
            }
            short ele2 = originEle2;
            short originEle23 = originEle2;
            boolean isStartpoint2 = lon05 == -1 && lon06 == -1;
            if (linkdisttotal == 0) {
                isReverse3 = isReverse2;
                if (!routingContext2.considerTurnRestrictions || detailMode || isStartpoint2) {
                    isStartpoint = isStartpoint2;
                } else if (routingContext2.inverseDirection) {
                    isStartpoint = isStartpoint2;
                    if (TurnRestriction.isTurnForbidden(osmPath2.sourceNode.firstRestriction, lon2, lat2, lon05, lon06, routingContext2.bikeMode || routingContext2.footMode, routingContext2.carMode)) {
                        break;
                    }
                } else {
                    isStartpoint = isStartpoint2;
                    if (TurnRestriction.isTurnForbidden(osmPath2.sourceNode.firstRestriction, lon05, lon06, lon2, lat2, routingContext2.bikeMode || routingContext2.footMode, routingContext2.carMode)) {
                        break;
                    }
                }
            } else {
                isStartpoint = isStartpoint2;
                isReverse3 = isReverse2;
            }
            if (osmPath2.message != null && osmPath2.message.wayKeyValues != null) {
                osmPath2.originElement.message = osmPath2.message;
                osmPath2.message = new MessageData();
            }
            int lon26 = lon2;
            int lat24 = lat2;
            int dist2 = routingContext2.calcDistance(lon07, lat12, lon26, lat24);
            boolean stopAtEndpoint = false;
            int lat05 = lon06;
            if (!routingContext2.shortestmatch) {
                newDestination = newDestination2;
                d = 0.0d;
                description = description3;
                ele1 = ele14;
                lat0 = lat05;
                ele12 = ele2;
                dist = dist2;
            } else if (routingContext2.isEndpoint) {
                stopAtEndpoint = true;
                newDestination = newDestination2;
                ele1 = ele14;
                dist = dist2;
                lat0 = lat05;
                lon05 = lon05;
                ele12 = osmPath2.interpolateEle(ele14, ele2, routingContext2.wayfraction);
                d = 0.0d;
                description = description3;
            } else {
                newDestination = newDestination2;
                osmPath2.cost = 0;
                resetState();
                if (!detailMode) {
                    lat03 = -1;
                    lon03 = -1;
                    description = description3;
                    d = 0.0d;
                } else {
                    lat03 = -1;
                    lon03 = -1;
                    d = 0.0d;
                    if (routingContext2.wayfraction > 0.0d) {
                        description = description3;
                        ele14 = osmPath2.interpolateEle(ele14, ele2, 1.0d - routingContext2.wayfraction);
                        osmPath2.originElement = OsmPathElement.create(routingContext2.ilonshortest, routingContext2.ilatshortest, ele14, null);
                    } else {
                        description = description3;
                        osmPath2.originElement = null;
                    }
                }
                if (!rc.checkPendingEndpoint()) {
                    ele1 = ele14;
                    isStartpoint = true;
                    lon05 = lon03;
                    lat0 = lat03;
                    ele12 = ele2;
                    dist = dist2;
                } else {
                    int dist3 = routingContext2.calcDistance(routingContext2.ilonshortest, routingContext2.ilatshortest, lon26, lat24);
                    if (!routingContext2.shortestmatch) {
                        ele1 = ele14;
                        isStartpoint = true;
                        lon05 = lon03;
                        lat0 = lat03;
                        ele12 = ele2;
                        dist = dist3;
                    } else {
                        stopAtEndpoint = true;
                        ele1 = ele14;
                        dist = dist3;
                        isStartpoint = true;
                        lon05 = lon03;
                        ele12 = osmPath2.interpolateEle(ele14, ele2, routingContext2.wayfraction);
                        lat0 = lat03;
                    }
                }
            }
            if (osmPath2.message == null) {
                lat02 = lat0;
            } else {
                MessageData messageData = osmPath2.message;
                lat02 = lat0;
                int lat06 = messageData.linkdist;
                messageData.linkdist = lat06 + dist;
            }
            int linkdisttotal2 = lat13 + dist;
            if (!isStartpoint) {
                transferNode2 = transferNode3;
                z = false;
                z2 = true;
                lon02 = lon05;
            } else if (routingContext2.startDirectionValid) {
                transferNode2 = transferNode3;
                double dir = ((double) routingContext2.startDirection.intValue()) * 0.017453292519943295d;
                double[] lonlat2m = CheapRuler.getLonLatToMeterScales((lon05 + lat12) >> 1);
                z = false;
                int lon08 = lon07 - ((int) ((Math.sin(dir) * 1000.0d) / lonlat2m[0]));
                z2 = true;
                int lat07 = lat12 - ((int) ((Math.cos(dir) * 1000.0d) / lonlat2m[1]));
                lat02 = lat07;
                lon02 = lon08;
            } else {
                transferNode2 = transferNode3;
                z = false;
                z2 = true;
                lat02 = lat12 - (lat24 - lat12);
                lon02 = lon07 - (lon26 - lon07);
            }
            double angle = routingContext2.anglemeter.calcAngle(lon02, lat02, lon07, lat12, lon26, lat24);
            double cosangle = routingContext2.anglemeter.getCosAngle();
            if (ele12 == Short.MIN_VALUE) {
                ele12 = ele1;
            }
            short ele22 = ele12;
            if (ele1 == Short.MIN_VALUE) {
                delta_h = 0.0d;
            } else {
                double delta_h2 = ((double) (ele22 - ele1)) / 4.0d;
                if (!routingContext2.inverseDirection) {
                    delta_h = delta_h2;
                } else {
                    delta_h = -delta_h2;
                }
            }
            double elevation = ele22 == Short.MIN_VALUE ? 100.0d : ((double) ele22) / 4.0d;
            int lat14 = lat12;
            double d2 = d;
            float newClassifier3 = newClassifier;
            int lat15 = lastpriorityclassifier;
            int dist4 = dist;
            float costfactor3 = costfactor2;
            int classifiermask4 = classifiermask;
            boolean isReverse6 = isReverse3;
            boolean newDestination3 = newDestination;
            int lon13 = lon07;
            OsmTransferNode transferNode4 = transferNode2;
            byte[] description4 = description;
            double sectionCost = processWaySection(rc, dist, delta_h, elevation, angle, cosangle, isStartpoint, linkdisttotal, lat15);
            if (sectionCost < d2) {
                osmPath = this;
                break;
            }
            if (costfactor3 > 9998.0d && !detailMode) {
                osmPath = this;
                break;
            }
            osmPath = this;
            if (((double) osmPath.cost) + sectionCost >= 2.0E9d) {
                break;
            }
            if (isTrafficBackbone) {
                sectionCost = 0.0d;
            }
            osmPath.cost += (int) sectionCost;
            computeKinematic(rc, dist4, delta_h, detailMode);
            if (osmPath.message == null) {
                routingContext = rc;
                originEle22 = originEle23;
                lat22 = lat24;
                isReverse4 = isReverse6;
                classifiermask2 = classifiermask4;
                lon22 = lon26;
                description2 = description4;
            } else {
                osmPath.message.turnangle = (float) angle;
                osmPath.message.time = (float) getTotalTime();
                osmPath.message.energy = (float) getTotalEnergy();
                osmPath.message.priorityclassifier = osmPath.priorityclassifier;
                classifiermask2 = classifiermask4;
                osmPath.message.classifiermask = classifiermask2;
                lon22 = lon26;
                osmPath.message.lon = lon22;
                lat22 = lat24;
                osmPath.message.lat = lat22;
                originEle22 = originEle23;
                osmPath.message.ele = originEle22;
                routingContext = rc;
                isReverse4 = isReverse6;
                description2 = description4;
                osmPath.message.wayKeyValues = routingContext.expctxWay.getKeyValueDescription(isReverse4, description2);
            }
            if (stopAtEndpoint) {
                if (detailMode) {
                    osmPath.originElement = OsmPathElement.create(routingContext.ilonshortest, routingContext.ilatshortest, originEle22, osmPath.originElement);
                    osmPath.originElement.cost = osmPath.cost;
                    if (osmPath.message != null) {
                        osmPath.originElement.message = osmPath.message;
                    }
                }
                if (routingContext.nogoCost < d2) {
                    osmPath.cost = -1;
                    return;
                } else {
                    osmPath.cost = (int) (((double) osmPath.cost) + routingContext.nogoCost);
                    return;
                }
            }
            byte[] description5 = description2;
            boolean isReverse7 = isReverse4;
            if (transferNode4 == null) {
                if (refTrack != null && refTrack.containsNode(osmPath.targetNode) && refTrack.containsNode(osmPath.sourceNode)) {
                    osmPath.cost += linkdisttotal2;
                }
                osmPath.selev = ele22;
                if (routingContext.nogoCost < d2) {
                    osmPath.cost = -1;
                    return;
                }
                osmPath.cost = (int) (((double) osmPath.cost) + routingContext.nogoCost);
                double targetCost = osmPath.processTargetNode(routingContext);
                if (targetCost < d2 || ((double) osmPath.cost) + targetCost >= 2.0E9d) {
                    osmPath.cost = -1;
                    return;
                } else {
                    osmPath.cost += (int) targetCost;
                    return;
                }
            }
            OsmTransferNode transferNode5 = transferNode4.next;
            if (detailMode) {
                osmPath.originElement = OsmPathElement.create(lon22, lat22, originEle22, osmPath.originElement);
                osmPath.originElement.cost = osmPath.cost;
            }
            ele14 = ele22;
            linkdisttotal++;
            transferNode3 = transferNode5;
            classifiermask = classifiermask2;
            routingContext2 = routingContext;
            costfactor2 = costfactor3;
            lastpriorityclassifier = lat15;
            lon07 = lon22;
            lat13 = linkdisttotal2;
            isReverse2 = isReverse7;
            description3 = description5;
            lon05 = lon13;
            osmPath2 = osmPath;
            lon06 = lat14;
            newDestination2 = newDestination3;
            newClassifier2 = newClassifier3;
            lat12 = lat22;
        }
    }

    public short interpolateEle(short e1, short e2, double fraction) {
        if (e1 == Short.MIN_VALUE || e2 == Short.MIN_VALUE) {
            return Short.MIN_VALUE;
        }
        return (short) ((((double) e1) * (1.0d - fraction)) + (((double) e2) * fraction));
    }

    protected void computeKinematic(RoutingContext rc, double dist, double delta_h, boolean detailMode) {
    }

    public OsmNode getSourceNode() {
        return this.sourceNode;
    }

    public OsmNode getTargetNode() {
        return this.targetNode;
    }

    public OsmLink getLink() {
        return this.link;
    }

    @Override // btools.mapaccess.OsmLinkHolder
    public void setNextForLink(OsmLinkHolder holder) {
        this.nextForLink = holder;
    }

    @Override // btools.mapaccess.OsmLinkHolder
    public OsmLinkHolder getNextForLink() {
        return this.nextForLink;
    }

    public double getTotalTime() {
        return 0.0d;
    }

    public double getTotalEnergy() {
        return 0.0d;
    }
}

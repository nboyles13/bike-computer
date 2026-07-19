package btools.router;

/* JADX INFO: loaded from: classes.dex */
final class MessageData implements Cloneable {
    int classifiermask;
    float costfactor;
    short ele;
    float energy;
    int lat;
    int lon;
    String nodeKeyValues;
    int priorityclassifier;
    float time;
    float turnangle;
    String wayKeyValues;
    int linkdist = 0;
    int linkelevationcost = 0;
    int linkturncost = 0;
    int linknodecost = 0;
    int linkinitcost = 0;
    int vmaxExplicit = -1;
    int vmax = -1;
    int vmin = -1;
    int vnode0 = 999;
    int vnode1 = 999;
    int extraTime = 0;

    MessageData() {
    }

    String toMessage() {
        if (this.wayKeyValues == null) {
            return null;
        }
        int iCost = (int) ((this.costfactor * 1000.0f) + 0.5f);
        return (this.lon - 180000000) + "\t" + (this.lat - 90000000) + "\t" + (this.ele / 4) + "\t" + this.linkdist + "\t" + iCost + "\t" + this.linkelevationcost + "\t" + this.linkturncost + "\t" + this.linknodecost + "\t" + this.linkinitcost + "\t" + this.wayKeyValues + "\t" + (this.nodeKeyValues == null ? "" : this.nodeKeyValues) + "\t" + ((int) this.time) + "\t" + ((int) this.energy);
    }

    void add(MessageData d) {
        this.linkdist += d.linkdist;
        this.linkelevationcost += d.linkelevationcost;
        this.linkturncost += d.linkturncost;
        this.linknodecost += d.linknodecost;
        this.linkinitcost += d.linkinitcost;
    }

    MessageData copy() {
        try {
            return (MessageData) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public String toString() {
        return "dist=" + this.linkdist + " prio=" + this.priorityclassifier + " turn=" + this.turnangle;
    }

    public int getPrio() {
        return this.priorityclassifier;
    }

    public boolean isBadOneway() {
        return (this.classifiermask & 1) != 0;
    }

    public boolean isGoodOneway() {
        return (this.classifiermask & 2) != 0;
    }

    public boolean isRoundabout() {
        return (this.classifiermask & 4) != 0;
    }

    public boolean isLinktType() {
        return (this.classifiermask & 8) != 0;
    }

    public boolean isGoodForCars() {
        return (this.classifiermask & 16) != 0;
    }
}

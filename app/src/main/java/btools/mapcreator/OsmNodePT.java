package btools.mapcreator;

/* JADX INFO: loaded from: classes.dex */
public class OsmNodePT extends OsmNodeP {
    public byte[] descriptionBits;
    public RestrictionData firstRestriction;

    public OsmNodePT() {
    }

    public OsmNodePT(OsmNodeP n) {
        this.ilat = n.ilat;
        this.ilon = n.ilon;
        this.selev = n.selev;
        this.bits = n.bits;
    }

    public OsmNodePT(byte[] descriptionBits) {
        this.descriptionBits = descriptionBits;
    }

    @Override // btools.mapcreator.OsmNodeP
    public final byte[] getNodeDecsription() {
        return this.descriptionBits;
    }

    @Override // btools.mapcreator.OsmNodeP
    public final RestrictionData getFirstRestriction() {
        return this.firstRestriction;
    }

    @Override // btools.mapcreator.OsmNodeP
    public boolean isTransferNode() {
        return false;
    }
}

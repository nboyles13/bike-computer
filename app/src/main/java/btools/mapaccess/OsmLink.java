package btools.mapaccess;

/* JADX INFO: loaded from: classes.dex */
public class OsmLink {
    public byte[] descriptionBitmap;
    public byte[] geometry;
    protected OsmNode n1;
    protected OsmNode n2;
    protected OsmLink next;
    protected OsmLink previous;
    private OsmLinkHolder reverselinkholder = null;
    private OsmLinkHolder firstlinkholder = null;

    protected OsmLink() {
    }

    public OsmLink(OsmNode source, OsmNode target) {
        this.n1 = source;
        this.n2 = target;
    }

    public final OsmNode getTarget(OsmNode source) {
        return (this.n2 == source || this.n2 == null) ? this.n1 : this.n2;
    }

    public final OsmLink getNext(OsmNode source) {
        return (this.n2 == source || this.n2 == null) ? this.previous : this.next;
    }

    protected final OsmLink clear(OsmNode source) {
        OsmLink n;
        if (this.n2 != null && this.n2 != source) {
            n = this.next;
            this.next = null;
            this.n2 = null;
            this.firstlinkholder = null;
        } else {
            OsmLink n2 = this.n1;
            if (n2 != null && this.n1 != source) {
                n = this.previous;
                this.previous = null;
                this.n1 = null;
                this.reverselinkholder = null;
            } else {
                throw new IllegalArgumentException("internal error: setNext: unknown source");
            }
        }
        if (this.n1 == null && this.n2 == null) {
            this.descriptionBitmap = null;
            this.geometry = null;
        }
        return n;
    }

    public final void setFirstLinkHolder(OsmLinkHolder holder, OsmNode source) {
        if (this.n2 != null && this.n2 != source) {
            this.firstlinkholder = holder;
        } else {
            if (this.n1 != null && this.n1 != source) {
                this.reverselinkholder = holder;
                return;
            }
            throw new IllegalArgumentException("internal error: setFirstLinkHolder: unknown source");
        }
    }

    public final OsmLinkHolder getFirstLinkHolder(OsmNode source) {
        if (this.n2 != null && this.n2 != source) {
            return this.firstlinkholder;
        }
        if (this.n1 != null && this.n1 != source) {
            return this.reverselinkholder;
        }
        throw new IllegalArgumentException("internal error: getFirstLinkHolder: unknown source");
    }

    public final boolean isReverse(OsmNode source) {
        return (this.n1 == source || this.n1 == null) ? false : true;
    }

    public final boolean isBidirectional() {
        return (this.n1 == null || this.n2 == null) ? false : true;
    }

    public final boolean isLinkUnused() {
        return this.n1 == null && this.n2 == null;
    }

    public final void addLinkHolder(OsmLinkHolder holder, OsmNode source) {
        OsmLinkHolder firstHolder = getFirstLinkHolder(source);
        if (firstHolder != null) {
            holder.setNextForLink(firstHolder);
        }
        setFirstLinkHolder(holder, source);
    }
}

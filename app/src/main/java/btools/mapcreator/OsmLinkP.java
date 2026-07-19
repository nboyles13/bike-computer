package btools.mapcreator;

/* JADX INFO: loaded from: classes.dex */
public class OsmLinkP {
    public byte[] descriptionBitmap;
    protected OsmLinkP next;
    protected OsmLinkP previous;
    protected OsmNodeP sourceNode;
    protected OsmNodeP targetNode;

    public OsmLinkP(OsmNodeP source, OsmNodeP target) {
        this.sourceNode = source;
        this.targetNode = target;
    }

    protected OsmLinkP() {
    }

    public final boolean counterLinkWritten() {
        return this.descriptionBitmap == null;
    }

    public void setNext(OsmLinkP link, OsmNodeP source) {
        if (this.sourceNode == source) {
            this.next = link;
        } else {
            if (this.targetNode == source) {
                this.previous = link;
                return;
            }
            throw new IllegalArgumentException("internal error: setNext: unknown source");
        }
    }

    public OsmLinkP getNext(OsmNodeP source) {
        if (this.sourceNode == source) {
            return this.next;
        }
        if (this.targetNode == source) {
            return this.previous;
        }
        throw new IllegalArgumentException("internal error: gextNext: unknown source");
    }

    public OsmNodeP getTarget(OsmNodeP source) {
        if (this.sourceNode == source) {
            return this.targetNode;
        }
        if (this.targetNode == source) {
            return this.sourceNode;
        }
        throw new IllegalArgumentException("internal error: getTarget: unknown source");
    }

    public boolean isReverse(OsmNodeP source) {
        if (this.sourceNode == source) {
            return false;
        }
        if (this.targetNode == source) {
            return true;
        }
        throw new IllegalArgumentException("internal error: isReverse: unknown source");
    }
}

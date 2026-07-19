package btools.expressions;

import btools.util.LruMapNode;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class CacheNode extends LruMapNode {
    byte[] ab;
    float[] vars;

    public int hashCode() {
        return this.hash;
    }

    public boolean equals(Object o) {
        CacheNode n = (CacheNode) o;
        if (this.hash != n.hash) {
            return false;
        }
        if (this.ab == null) {
            return true;
        }
        return Arrays.equals(this.ab, n.ab);
    }
}

package btools.expressions;

import btools.util.LruMapNode;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public final class VarWrapper extends LruMapNode {
    float[] vars;

    public int hashCode() {
        return this.hash;
    }

    public boolean equals(Object o) {
        VarWrapper n = (VarWrapper) o;
        if (this.hash != n.hash) {
            return false;
        }
        return Arrays.equals(this.vars, n.vars);
    }
}

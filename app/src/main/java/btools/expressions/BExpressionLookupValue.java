package btools.expressions;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* JADX INFO: loaded from: classes.dex */
public final class BExpressionLookupValue {
    List<String> aliases;
    String value;

    public String toString() {
        return this.value;
    }

    public BExpressionLookupValue(String value) {
        this.value = value;
    }

    public void addAlias(String alias) {
        if (this.aliases == null) {
            this.aliases = new ArrayList();
        }
        this.aliases.add(alias);
    }

    public boolean equals(Object o) {
        if (o instanceof String) {
            String v = (String) o;
            return this.value.equals(v);
        }
        if (o instanceof BExpressionLookupValue) {
            BExpressionLookupValue v2 = (BExpressionLookupValue) o;
            return this.value.equals(v2.value);
        }
        return false;
    }

    public boolean matches(String s) {
        if (this.value.equals(s)) {
            return true;
        }
        if (this.aliases != null) {
            for (String alias : this.aliases) {
                if (alias.equals(s)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}

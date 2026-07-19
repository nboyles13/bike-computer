package btools.expressions;

/* JADX INFO: loaded from: classes.dex */
public final class BExpressionContextNode extends BExpressionContext {
    private static String[] buildInVariables = {"initialcost"};

    @Override // btools.expressions.BExpressionContext
    protected String[] getBuildInVariableNames() {
        return buildInVariables;
    }

    public float getInitialcost() {
        return getBuildInVariable(0);
    }

    public BExpressionContextNode(BExpressionMetaData meta) {
        super("node", meta);
    }

    public BExpressionContextNode(int hashSize, BExpressionMetaData meta) {
        super("node", hashSize, meta);
    }
}

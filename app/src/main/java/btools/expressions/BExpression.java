package btools.expressions;

import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
final class BExpression {
    private static final int ADD_EXP = 20;
    private static final int AND_EXP = 11;
    private static final int ASSIGN_EXP = 31;
    private static final int DIVIDE_EXP = 22;
    private static final int EQUAL_EXP = 24;
    private static final int FOREIGN_VARIABLE_EXP = 35;
    private static final int GREATER_EXP = 25;
    private static final int LESSER_EXP = 28;
    private static final int LOOKUP_EXP = 32;
    private static final int MAX_EXP = 23;
    private static final int MIN_EXP = 26;
    private static final int MULTIPLY_EXP = 21;
    private static final int NOT_EXP = 12;
    private static final int NUMBER_EXP = 33;
    private static final int OR_EXP = 10;
    private static final int SUB_EXP = 27;
    private static final int SWITCH_EXP = 30;
    private static final int VARIABLE_EXP = 34;
    private static final int VARIABLE_GET_EXP = 36;
    private static final int XOR_EXP = 29;
    private boolean doNotChange;
    private int lookupNameIdx = -1;
    private int[] lookupValueIdxArray;
    private float numberValue;
    private BExpression op1;
    private BExpression op2;
    private BExpression op3;
    private int typ;
    private int variableIdx;

    BExpression() {
    }

    public static BExpression parse(BExpressionContext ctx, int level) throws Exception {
        return parse(ctx, level, null);
    }

    private static BExpression parse(BExpressionContext ctx, int level, String optionalToken) throws Exception {
        BExpression e = parseRaw(ctx, level, optionalToken);
        if (e == null) {
            return null;
        }
        if (31 == e.typ) {
            BExpression assignedBefore = ctx.lastAssignedExpression.get(e.variableIdx);
            if (assignedBefore != null && assignedBefore.doNotChange) {
                e.op1 = assignedBefore;
                e.op1.doNotChange = false;
            }
            ctx.lastAssignedExpression.set(e.variableIdx, e.op1);
        } else if (!ctx.skipConstantExpressionOptimizations) {
            if (34 == e.typ) {
                BExpression ae = ctx.lastAssignedExpression.get(e.variableIdx);
                if (ae != null && ae.typ == 33) {
                    e = ae;
                }
            } else {
                BExpression eCollapsed = e.tryCollapse();
                if (e != eCollapsed) {
                    e = eCollapsed;
                }
                BExpression eEvaluated = e.tryEvaluateConstant();
                if (e != eEvaluated) {
                    e = eEvaluated;
                }
            }
        }
        if (level == 0) {
            int nodeCount = e.markLookupIdxUsed(ctx);
            ctx.expressionNodeCount += nodeCount;
        }
        return e;
    }

    private int markLookupIdxUsed(BExpressionContext ctx) {
        if (this.lookupNameIdx >= 0) {
            ctx.markLookupIdxUsed(this.lookupNameIdx);
        }
        int nodeCount = this.op1 != null ? 1 + this.op1.markLookupIdxUsed(ctx) : 1;
        if (this.op2 != null) {
            nodeCount += this.op2.markLookupIdxUsed(ctx);
        }
        if (this.op3 != null) {
            return nodeCount + this.op3.markLookupIdxUsed(ctx);
        }
        return nodeCount;
    }

    private static BExpression parseRaw(BExpressionContext ctx, int level, String optionalToken) throws Exception {
        String operator;
        boolean brackets;
        String operator2 = ctx.parseToken();
        if (optionalToken != null && optionalToken.equals(operator2)) {
            operator2 = ctx.parseToken();
        }
        if (!"(".equals(operator2)) {
            operator = operator2;
            brackets = false;
        } else {
            operator = ctx.parseToken();
            brackets = true;
        }
        if (operator == null) {
            if (level == 0) {
                return null;
            }
            throw new IllegalArgumentException("unexpected end of file");
        }
        if (level == 0 && !"assign".equals(operator)) {
            throw new IllegalArgumentException("operator " + operator + " is invalid on toplevel (only 'assign' allowed)");
        }
        BExpression exp = new BExpression();
        int nops = 3;
        boolean ifThenElse = false;
        if ("switch".equals(operator)) {
            exp.typ = 30;
        } else if ("if".equals(operator)) {
            exp.typ = 30;
            ifThenElse = true;
        } else {
            nops = 2;
            if ("or".equals(operator)) {
                exp.typ = 10;
            } else if ("and".equals(operator)) {
                exp.typ = 11;
            } else if ("multiply".equals(operator)) {
                exp.typ = 21;
            } else if ("divide".equals(operator)) {
                exp.typ = 22;
            } else if ("add".equals(operator)) {
                exp.typ = 20;
            } else if ("max".equals(operator)) {
                exp.typ = 23;
            } else if ("min".equals(operator)) {
                exp.typ = 26;
            } else if ("equal".equals(operator)) {
                exp.typ = 24;
            } else if ("greater".equals(operator)) {
                exp.typ = 25;
            } else if ("sub".equals(operator)) {
                exp.typ = 27;
            } else if ("lesser".equals(operator)) {
                exp.typ = 28;
            } else if ("xor".equals(operator)) {
                exp.typ = XOR_EXP;
            } else {
                nops = 1;
                if ("assign".equals(operator)) {
                    if (level > 0) {
                        throw new IllegalArgumentException("assign operator within expression");
                    }
                    exp.typ = 31;
                    String variable = ctx.parseToken();
                    if (variable == null) {
                        throw new IllegalArgumentException("unexpected end of file");
                    }
                    if (variable.indexOf(61) >= 0) {
                        throw new IllegalArgumentException("variable name cannot contain '=': " + variable);
                    }
                    if (variable.indexOf(58) >= 0) {
                        throw new IllegalArgumentException("cannot assign context-prefixed variable: " + variable);
                    }
                    exp.variableIdx = ctx.getVariableIdx(variable, true);
                    if (exp.variableIdx < ctx.getMinWriteIdx()) {
                        throw new IllegalArgumentException("cannot assign to readonly variable " + variable);
                    }
                } else if ("not".equals(operator)) {
                    exp.typ = 12;
                } else {
                    nops = 0;
                    int idx = operator.indexOf(61);
                    if (idx >= 0) {
                        exp.typ = 32;
                        String name = operator.substring(0, idx);
                        String values = operator.substring(idx + 1);
                        exp.lookupNameIdx = ctx.getLookupNameIdx(name);
                        if (exp.lookupNameIdx < 0) {
                            throw new IllegalArgumentException("unknown lookup name: " + name);
                        }
                        StringTokenizer tk = new StringTokenizer(values, "|");
                        int nt = tk.countTokens();
                        int nt2 = nt == 0 ? 1 : nt;
                        exp.lookupValueIdxArray = new int[nt2];
                        int ti = 0;
                        while (ti < nt2) {
                            String value = ti < nt ? tk.nextToken() : "";
                            int idx2 = idx;
                            exp.lookupValueIdxArray[ti] = ctx.getLookupValueIdx(exp.lookupNameIdx, value);
                            if (exp.lookupValueIdxArray[ti] >= 0) {
                                ti++;
                                idx = idx2;
                            } else {
                                throw new IllegalArgumentException("unknown lookup value: " + value);
                            }
                        }
                    } else {
                        int idx3 = operator.indexOf(58);
                        if (idx3 >= 0) {
                            if (operator.startsWith("v:")) {
                                String name2 = operator.substring(2);
                                exp.typ = 36;
                                exp.lookupNameIdx = ctx.getLookupNameIdx(name2);
                            } else {
                                String context = operator.substring(0, idx3);
                                String varname = operator.substring(idx3 + 1);
                                exp.typ = 35;
                                exp.variableIdx = ctx.getForeignVariableIdx(context, varname);
                            }
                        } else {
                            int idx4 = ctx.getVariableIdx(operator, false);
                            if (idx4 >= 0) {
                                exp.typ = 34;
                                exp.variableIdx = idx4;
                            } else if ("true".equals(operator)) {
                                exp.numberValue = 1.0f;
                                exp.typ = 33;
                            } else if ("false".equals(operator)) {
                                exp.numberValue = 0.0f;
                                exp.typ = 33;
                            } else {
                                try {
                                    exp.numberValue = Float.parseFloat(operator);
                                    exp.typ = 33;
                                } catch (NumberFormatException e) {
                                    throw new IllegalArgumentException("unknown expression: " + operator);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (nops > 0) {
            exp.op1 = parse(ctx, level + 1, exp.typ == 31 ? "=" : null);
        }
        if (nops > 1) {
            if (ifThenElse) {
                checkExpectedToken(ctx, "then");
            }
            exp.op2 = parse(ctx, level + 1, null);
        }
        if (nops > 2) {
            if (ifThenElse) {
                checkExpectedToken(ctx, "else");
            }
            exp.op3 = parse(ctx, level + 1, null);
        }
        if (brackets) {
            checkExpectedToken(ctx, ")");
        }
        return exp;
    }

    private static void checkExpectedToken(BExpressionContext ctx, String expected) throws Exception {
        String token = ctx.parseToken();
        if (!expected.equals(token)) {
            throw new IllegalArgumentException("unexpected token: " + token + ", expected: " + expected);
        }
    }

    public float evaluate(BExpressionContext ctx) {
        switch (this.typ) {
            case 10:
                return (this.op1.evaluate(ctx) == 0.0f && this.op2.evaluate(ctx) == 0.0f) ? 0.0f : 1.0f;
            case 11:
                return (this.op1.evaluate(ctx) == 0.0f || this.op2.evaluate(ctx) == 0.0f) ? 0.0f : 1.0f;
            case 12:
                return this.op1.evaluate(ctx) == 0.0f ? 1.0f : 0.0f;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            default:
                throw new IllegalArgumentException("unknown op-code: " + this.typ);
            case 20:
                return this.op1.evaluate(ctx) + this.op2.evaluate(ctx);
            case 21:
                return this.op1.evaluate(ctx) * this.op2.evaluate(ctx);
            case 22:
                return divide(this.op1.evaluate(ctx), this.op2.evaluate(ctx));
            case 23:
                return max(this.op1.evaluate(ctx), this.op2.evaluate(ctx));
            case 24:
                return this.op1.evaluate(ctx) == this.op2.evaluate(ctx) ? 1.0f : 0.0f;
            case 25:
                return this.op1.evaluate(ctx) > this.op2.evaluate(ctx) ? 1.0f : 0.0f;
            case 26:
                return min(this.op1.evaluate(ctx), this.op2.evaluate(ctx));
            case 27:
                return this.op1.evaluate(ctx) - this.op2.evaluate(ctx);
            case 28:
                return this.op1.evaluate(ctx) < this.op2.evaluate(ctx) ? 1.0f : 0.0f;
            case XOR_EXP /* 29 */:
                return ((this.op1.evaluate(ctx) > 0.0f ? 1 : (this.op1.evaluate(ctx) == 0.0f ? 0 : -1)) != 0) ^ (this.op2.evaluate(ctx) != 0.0f) ? 1.0f : 0.0f;
            case 30:
                return (this.op1.evaluate(ctx) != 0.0f ? this.op2 : this.op3).evaluate(ctx);
            case 31:
                return ctx.assign(this.variableIdx, this.op1.evaluate(ctx));
            case 32:
                return ctx.getLookupMatch(this.lookupNameIdx, this.lookupValueIdxArray);
            case 33:
                return this.numberValue;
            case 34:
                return ctx.getVariableValue(this.variableIdx);
            case 35:
                return ctx.getForeignVariableValue(this.variableIdx);
            case 36:
                return ctx.getLookupValue(this.lookupNameIdx);
        }
    }

    private BExpression tryCollapse() {
        switch (this.typ) {
            case 10:
                if (33 != this.op1.typ) {
                    if (33 == this.op2.typ) {
                        if (this.op2.numberValue == 0.0f) {
                        }
                    }
                } else if (this.op1.numberValue == 0.0f) {
                }
                break;
            case 11:
                if (33 != this.op1.typ) {
                    if (33 == this.op2.typ) {
                        if (this.op2.numberValue != 0.0f) {
                        }
                    }
                } else if (this.op1.numberValue != 0.0f) {
                }
                break;
            case 20:
                if (33 != this.op1.typ) {
                    if (33 == this.op2.typ && this.op2.numberValue == 0.0f) {
                    }
                } else if (this.op1.numberValue == 0.0f) {
                }
                break;
            case 30:
                if (33 == this.op1.typ) {
                    if (this.op1.numberValue != 0.0f) {
                    }
                }
                break;
        }
        return this;
    }

    private BExpression tryEvaluateConstant() {
        if (this.op1 != null && 33 == this.op1.typ && ((this.op2 == null || 33 == this.op2.typ) && (this.op3 == null || 33 == this.op3.typ))) {
            BExpression exp = new BExpression();
            exp.typ = 33;
            exp.numberValue = evaluate(null);
            return exp;
        }
        return this;
    }

    private float max(float v1, float v2) {
        return v1 > v2 ? v1 : v2;
    }

    private float min(float v1, float v2) {
        return v1 < v2 ? v1 : v2;
    }

    private float divide(float v1, float v2) {
        if (v2 == 0.0f) {
            throw new IllegalArgumentException("div by zero");
        }
        return v1 / v2;
    }

    public String toString() {
        if (this.typ == 33) {
            return new StringBuilder().append(this.numberValue).toString();
        }
        if (this.typ == 34) {
            return "vidx=" + this.variableIdx;
        }
        StringBuilder sb = new StringBuilder("typ=" + this.typ + " ops=(");
        addOp(sb, this.op1);
        addOp(sb, this.op2);
        addOp(sb, this.op3);
        sb.append(')');
        return sb.toString();
    }

    private void addOp(StringBuilder sb, BExpression e) {
        if (e != null) {
            sb.append('[').append(e.toString()).append(']');
        }
    }

    static BExpression createAssignExpressionFromKeyValue(BExpressionContext ctx, String key, String value) {
        BExpression e = new BExpression();
        e.typ = 31;
        e.variableIdx = ctx.getVariableIdx(key, true);
        e.op1 = new BExpression();
        e.op1.typ = 33;
        e.op1.numberValue = Float.parseFloat(value);
        e.op1.doNotChange = true;
        ctx.lastAssignedExpression.set(e.variableIdx, e.op1);
        return e;
    }
}

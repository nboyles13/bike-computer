package btools.expressions;

import androidx.core.app.NotificationManagerCompat;
import androidx.core.os.EnvironmentCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import btools.util.BitCoderContext;
import btools.util.Crc32;
import btools.util.IByteArrayUnifier;
import btools.util.LruMap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeMap;
import kotlin.text.Typography;
import okhttp3.internal.http2.Http2Connection;

/* JADX INFO: loaded from: classes.dex */
public abstract class BExpressionContext implements IByteArrayUnifier {
    private static final String CONTEXT_TAG = "---context:";
    private static final String MODEL_TAG = "---model:";
    private BufferedReader _br;
    private boolean _inOurContext;
    public String _modelClass;
    private boolean _readerDone;
    private byte[] abBuf;
    private int[] buildInVariableIdx;
    private LruMap cache;
    private long cachemisses;
    private String context;
    private BitCoderContext ctxDecode;
    private BitCoderContext ctxEndode;
    private int currentVarOffset;
    private float[] currentVars;
    private List<BExpression> expressionList;
    int expressionNodeCount;
    private boolean fixTagsWritten;
    private BExpressionContext foreignContext;
    List<BExpression> lastAssignedExpression;
    private CacheNode lastCacheNode;
    int[] ld2;
    private int linenr;
    private int[] lookupData;
    private boolean lookupDataFrozen;
    private boolean lookupDataValid;
    private List<int[]> lookupHistograms;
    private boolean[] lookupIdxUsed;
    private List<String> lookupNames;
    private Map<String, Integer> lookupNumbers;
    private List<BExpressionLookupValue[]> lookupValues;
    public BExpressionMetaData meta;
    private int minWriteIdx;
    private int nBuildInVars;
    public int[] noStartWays;
    private int parsedLines;
    private CacheNode probeCacheNode;
    private VarWrapper probeVarSet;
    private long requests;
    private long requests2;
    private LruMap resultVarCache;
    boolean showErrors;
    boolean skipConstantExpressionOptimizations;
    private float[] variableData;
    private Map<String, Integer> variableNumbers;

    abstract String[] getBuildInVariableNames();

    protected void setInverseVars() {
        this.currentVarOffset = this.nBuildInVars;
    }

    public final float getBuildInVariable(int idx) {
        return this.currentVars[this.currentVarOffset + idx];
    }

    protected BExpressionContext(String context, BExpressionMetaData meta) {
        this(context, 4096, meta);
    }

    protected BExpressionContext(String context, int hashSize, BExpressionMetaData meta) {
        this._inOurContext = false;
        this._br = null;
        this._readerDone = false;
        this.lookupNumbers = new HashMap();
        this.lookupValues = new ArrayList();
        this.lookupNames = new ArrayList();
        this.lookupHistograms = new ArrayList();
        this.lookupDataFrozen = false;
        this.lookupData = new int[0];
        this.abBuf = new byte[256];
        this.ctxEndode = new BitCoderContext(this.abBuf);
        this.ctxDecode = new BitCoderContext(new byte[0]);
        this.variableNumbers = new HashMap();
        this.lastAssignedExpression = new ArrayList();
        this.skipConstantExpressionOptimizations = false;
        this.probeCacheNode = new CacheNode();
        this.probeVarSet = new VarWrapper();
        this.noStartWays = new int[0];
        this.showErrors = Boolean.getBoolean("showErrors");
        this.lookupDataValid = false;
        this.parsedLines = 0;
        this.fixTagsWritten = false;
        this.lastCacheNode = new CacheNode();
        this.ld2 = new int[512];
        this.context = context;
        this.meta = meta;
        if (meta != null) {
            meta.registerListener(context, this);
        }
        hashSize = Boolean.getBoolean("disableExpressionCache") ? 1 : hashSize;
        if (hashSize > 0) {
            this.cache = new LruMap(hashSize * 4, hashSize);
            this.resultVarCache = new LruMap(4096, 4096);
        }
    }

    public byte[] encode() {
        if (!this.lookupDataValid) {
            throw new IllegalArgumentException("internal error: encoding undefined data?");
        }
        return encode(this.lookupData);
    }

    public byte[] encode(int[] ld) {
        BitCoderContext ctx = this.ctxEndode;
        ctx.reset();
        int skippedTags = 0;
        int nonNullTags = 0;
        for (int inum = 1; inum < this.lookupValues.size(); inum++) {
            int d = ld[inum];
            if (d == 0) {
                skippedTags++;
            } else {
                ctx.encodeVarBits(skippedTags + 1);
                nonNullTags++;
                skippedTags = 0;
                int dd = d < 2 ? 7 : d < 9 ? d - 2 : d - 1;
                ctx.encodeVarBits(dd);
            }
        }
        ctx.encodeVarBits(0);
        if (nonNullTags == 0) {
            return null;
        }
        int len = ctx.closeAndGetEncodedLength();
        byte[] ab = new byte[len];
        System.arraycopy(this.abBuf, 0, ab, 0, len);
        int[] ld2 = new int[this.lookupValues.size()];
        decode(ld2, false, ab);
        for (int inum2 = 1; inum2 < this.lookupValues.size(); inum2++) {
            if (ld2[inum2] != ld[inum2]) {
                throw new RuntimeException("assertion failed encoding inum=" + inum2 + " val=" + ld[inum2] + " " + getKeyValueDescription(false, ab));
            }
        }
        return ab;
    }

    public void decode(byte[] ab) {
        decode(this.lookupData, false, ab);
        this.lookupDataValid = true;
    }

    /* JADX WARN: Incorrect condition in loop: B:13:0x001b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void decode(int[] ld, boolean inverseDirection, byte[] ab) {
        int d;
        BitCoderContext ctx = this.ctxDecode;
        ctx.reset(ab);
        ld[0] = inverseDirection ? 2 : 0;
        int inum = 1;
        while (true) {
            int inum2 = ctx.decodeVarBits();
            if (inum2 == 0 || inum + inum2 > ld.length) {
                break;
            }
            while (true) {
                int delta = inum2 - 1;
                d = 1;
                if (inum2 <= 1) {
                    break;
                }
                ld[inum] = 0;
                inum++;
                inum2 = delta;
            }
            int dd = ctx.decodeVarBits();
            if (dd != 7) {
                d = dd < 7 ? dd + 2 : dd + 1;
            }
            if (d >= this.lookupValues.get(inum).length && d < 1000) {
                d = 1;
            }
            ld[inum] = d;
            inum++;
        }
        while (inum < ld.length) {
            ld[inum] = 0;
            inum++;
        }
    }

    public String getKeyValueDescription(boolean inverseDirection, byte[] ab) {
        StringBuilder sb = new StringBuilder(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        decode(this.lookupData, inverseDirection, ab);
        for (int inum = 0; inum < this.lookupValues.size(); inum++) {
            BExpressionLookupValue[] va = this.lookupValues.get(inum);
            int val = this.lookupData[inum];
            String value = val >= 1000 ? Float.toString((val + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) / 100.0f) : va[val].toString();
            if (value != null && value.length() > 0) {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(this.lookupNames.get(inum) + "=" + value);
            }
        }
        return sb.toString();
    }

    public List<String> getKeyValueList(boolean inverseDirection, byte[] ab) {
        List<String> res = new ArrayList<>();
        decode(this.lookupData, inverseDirection, ab);
        for (int inum = 0; inum < this.lookupValues.size(); inum++) {
            BExpressionLookupValue[] va = this.lookupValues.get(inum);
            int val = this.lookupData[inum];
            String value = val >= 1000 ? Float.toString((val + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) / 100.0f) : va[val].toString();
            if (value != null && value.length() > 0) {
                res.add(this.lookupNames.get(inum));
                res.add(value);
            }
        }
        return res;
    }

    public int getLookupKey(String name) {
        try {
            int res = this.lookupNumbers.get(name).intValue();
            return res;
        } catch (Exception e) {
            return -1;
        }
    }

    public float getLookupValue(int key) {
        int val = this.lookupData[key];
        if (val == 0) {
            return Float.NaN;
        }
        if (val < 900) {
            try {
                BExpressionLookupValue[] va = this.lookupValues.get(key);
                String sval = va[val].toString();
                float res = Float.parseFloat(sval);
                return res;
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }
        float res2 = (val + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) / 100.0f;
        return res2;
    }

    public float getLookupValue(boolean inverseDirection, byte[] ab, int key) {
        decode(this.lookupData, inverseDirection, ab);
        int val = this.lookupData[key];
        if (val == 0) {
            return Float.NaN;
        }
        float res = (val + NotificationManagerCompat.IMPORTANCE_UNSPECIFIED) / 100.0f;
        return res;
    }

    public void parseMetaLine(String line) {
        this.parsedLines++;
        StringTokenizer tk = new StringTokenizer(line, " ");
        String name = tk.nextToken();
        String value = tk.nextToken();
        int idx = name.indexOf(59);
        if (idx >= 0) {
            name = name.substring(0, idx);
        }
        if (!this.fixTagsWritten) {
            this.fixTagsWritten = true;
            if ("way".equals(this.context)) {
                addLookupValue("reversedirection", "yes", null);
            } else if ("node".equals(this.context)) {
                addLookupValue("nodeaccessgranted", "yes", null);
            }
        }
        if ("reversedirection".equals(name) || "nodeaccessgranted".equals(name)) {
            return;
        }
        BExpressionLookupValue newValue = addLookupValue(name, value, null);
        while (newValue != null && tk.hasMoreTokens()) {
            newValue.addAlias(tk.nextToken());
        }
    }

    public void finishMetaParsing() {
        if (this.parsedLines == 0 && !"global".equals(this.context)) {
            throw new IllegalArgumentException("lookup table does not contain data for context " + this.context + " (old version?)");
        }
        this.lookupDataFrozen = true;
        this.lookupIdxUsed = new boolean[this.lookupValues.size()];
    }

    public final void evaluate(int[] lookupData2) {
        this.lookupData = lookupData2;
        evaluate();
    }

    private void evaluate() {
        int n = this.expressionList.size();
        for (int expidx = 0; expidx < n; expidx++) {
            this.expressionList.get(expidx).evaluate(this);
        }
    }

    public String cacheStats() {
        return "requests=" + this.requests + " requests2=" + this.requests2 + " cachemisses=" + this.cachemisses;
    }

    @Override // btools.util.IByteArrayUnifier
    public final byte[] unify(byte[] ab, int offset, int len) {
        this.probeCacheNode.ab = null;
        this.probeCacheNode.hash = Crc32.crc(ab, offset, len);
        CacheNode cn = (CacheNode) this.cache.get(this.probeCacheNode);
        if (cn != null) {
            byte[] cab = cn.ab;
            if (cab.length == len) {
                int i = 0;
                while (true) {
                    if (i >= len) {
                        break;
                    }
                    if (cab[i] == ab[i + offset]) {
                        i++;
                    } else {
                        cn = null;
                        break;
                    }
                }
                if (cn != null) {
                    this.lastCacheNode = cn;
                    return cn.ab;
                }
            }
        }
        byte[] cab2 = new byte[len];
        System.arraycopy(ab, offset, cab2, 0, len);
        return cab2;
    }

    public final void evaluate(boolean inverseDirection, byte[] ab) {
        CacheNode cn;
        this.requests++;
        this.lookupDataValid = false;
        if (this.cache == null) {
            decode(this.lookupData, inverseDirection, ab);
            if (this.currentVars == null || this.currentVars.length != this.nBuildInVars) {
                this.currentVars = new float[this.nBuildInVars];
            }
            evaluateInto(this.currentVars, 0);
            this.currentVarOffset = 0;
            return;
        }
        if (this.lastCacheNode.ab == ab) {
            cn = this.lastCacheNode;
        } else {
            CacheNode cn2 = this.probeCacheNode;
            cn2.ab = ab;
            this.probeCacheNode.hash = Crc32.crc(ab, 0, ab.length);
            cn = (CacheNode) this.cache.get(this.probeCacheNode);
        }
        if (cn == null) {
            this.cachemisses++;
            cn = (CacheNode) this.cache.removeLru();
            if (cn == null) {
                cn = new CacheNode();
            }
            cn.hash = this.probeCacheNode.hash;
            cn.ab = ab;
            this.cache.put(cn);
            if (this.probeVarSet.vars == null) {
                this.probeVarSet.vars = new float[this.nBuildInVars * 2];
            }
            decode(this.lookupData, false, ab);
            evaluateInto(this.probeVarSet.vars, 0);
            this.lookupData[0] = 2;
            evaluateInto(this.probeVarSet.vars, this.nBuildInVars);
            this.probeVarSet.hash = Arrays.hashCode(this.probeVarSet.vars);
            VarWrapper vw = (VarWrapper) this.resultVarCache.get(this.probeVarSet);
            if (vw == null) {
                vw = (VarWrapper) this.resultVarCache.removeLru();
                if (vw == null) {
                    vw = new VarWrapper();
                }
                vw.hash = this.probeVarSet.hash;
                vw.vars = this.probeVarSet.vars;
                this.probeVarSet.vars = null;
                this.resultVarCache.put(vw);
            }
            cn.vars = vw.vars;
        } else {
            if (ab == cn.ab) {
                this.requests2++;
            }
            this.cache.touch(cn);
        }
        this.currentVars = cn.vars;
        this.currentVarOffset = inverseDirection ? this.nBuildInVars : 0;
    }

    private void evaluateInto(float[] vars, int offset) {
        evaluate();
        for (int vi = 0; vi < this.nBuildInVars; vi++) {
            int idx = this.buildInVariableIdx[vi];
            vars[vi + offset] = idx == -1 ? 0.0f : this.variableData[idx];
        }
    }

    public void dumpStatistics() {
        NavigableMap<String, String> counts = new TreeMap<>();
        for (String name : this.lookupNumbers.keySet()) {
            int cnt = 0;
            int[] histo = this.lookupHistograms.get(this.lookupNumbers.get(name).intValue());
            for (int i = 2; i < histo.length; i++) {
                cnt += histo[i];
            }
            counts.put((Http2Connection.DEGRADED_PONG_TIMEOUT_NS + cnt) + "_" + name, name);
        }
        while (counts.size() > 0) {
            String key = counts.lastEntry().getKey();
            String name2 = (String) counts.get(key);
            counts.remove(key);
            int inum = this.lookupNumbers.get(name2).intValue();
            BExpressionLookupValue[] values = this.lookupValues.get(inum);
            int[] histo2 = this.lookupHistograms.get(inum);
            if (values.length != 1000) {
                String[] svalues = new String[values.length];
                for (int i2 = 0; i2 < values.length; i2++) {
                    String scnt = "0000000000" + histo2[i2];
                    svalues[i2] = scnt.substring(scnt.length() - 10) + " " + values[i2].toString();
                }
                Arrays.sort(svalues);
                for (int i3 = svalues.length - 1; i3 >= 0; i3--) {
                    System.out.println(name2 + ";" + svalues[i3]);
                }
            }
        }
    }

    public int[] createNewLookupData() {
        if (this.lookupDataFrozen) {
            return new int[this.lookupValues.size()];
        }
        return null;
    }

    public int[] generateRandomValues(Random rnd) {
        int[] data = createNewLookupData();
        data[0] = rnd.nextInt(2) * 2;
        for (int inum = 1; inum < data.length; inum++) {
            int nvalues = this.lookupValues.get(inum).length;
            data[inum] = 0;
            if (inum <= 1 || rnd.nextInt(10) <= 0) {
                data[inum] = rnd.nextInt(nvalues);
            }
        }
        this.lookupDataValid = true;
        return data;
    }

    public void assertAllVariablesEqual(BExpressionContext other) {
        int nv = this.variableData.length;
        int nv2 = other.variableData.length;
        if (nv != nv2) {
            throw new RuntimeException("mismatch in variable-count: " + nv + "<->" + nv2);
        }
        for (int i = 0; i < nv; i++) {
            if (this.variableData[i] != other.variableData[i]) {
                throw new RuntimeException("mismatch in variable " + variableName(i) + " " + this.variableData[i] + "<->" + other.variableData[i] + "\ntags = " + getKeyValueDescription(false, encode()));
            }
        }
    }

    public String variableName(int idx) {
        for (Map.Entry<String, Integer> e : this.variableNumbers.entrySet()) {
            if (e.getValue().intValue() == idx) {
                return e.getKey();
            }
        }
        throw new RuntimeException("no variable for index" + idx);
    }

    /* JADX WARN: Failed to analyze thrown exceptions
    java.util.ConcurrentModificationException
    	at java.base/java.util.ArrayList$Itr.checkForComodification(ArrayList.java:1095)
    	at java.base/java.util.ArrayList$Itr.next(ArrayList.java:1049)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.processInstructions(MethodThrowsVisitor.java:130)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.visit(MethodThrowsVisitor.java:68)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.checkInsn(MethodThrowsVisitor.java:178)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.processInstructions(MethodThrowsVisitor.java:131)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.visit(MethodThrowsVisitor.java:68)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.checkInsn(MethodThrowsVisitor.java:178)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.processInstructions(MethodThrowsVisitor.java:131)
    	at jadx.core.dex.visitors.MethodThrowsVisitor.visit(MethodThrowsVisitor.java:68)
     */
    /* JADX WARN: Incorrect condition in loop: B:10:0x00a2 */
    /* JADX WARN: Unreachable blocks removed: 2, instructions: 2 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public BExpressionLookupValue addLookupValue(String name, String value, int[] lookupData2) {
        BExpressionLookupValue newValue;
        int[] histo;
        String value2 = value;
        Integer num = this.lookupNumbers.get(name);
        if (num != null) {
            newValue = null;
        } else {
            if (lookupData2 != null) {
                return null;
            }
            Integer num2 = Integer.valueOf(this.lookupValues.size());
            this.lookupNumbers.put(name, num2);
            this.lookupNames.add(name);
            newValue = null;
            this.lookupValues.add(new BExpressionLookupValue[]{new BExpressionLookupValue(""), new BExpressionLookupValue(EnvironmentCompat.MEDIA_UNKNOWN)});
            this.lookupHistograms.add(new int[2]);
            int[] ndata = new int[this.lookupData.length + 1];
            System.arraycopy(this.lookupData, 0, ndata, 0, this.lookupData.length);
            this.lookupData = ndata;
            num = num2;
        }
        BExpressionLookupValue[] values = this.lookupValues.get(num.intValue());
        int[] histo2 = this.lookupHistograms.get(num.intValue());
        boolean bFoundAsterix = false;
        int i = 0;
        while (i < values.length) {
            BExpressionLookupValue v = values[i];
            if (v.equals("*")) {
                bFoundAsterix = true;
            }
            if (v.matches(value2)) {
                break;
            }
            i++;
        }
        if (i != values.length) {
            histo = histo2;
        } else {
            if (lookupData2 != null) {
                lookupData2[num.intValue()] = 1;
                if (bFoundAsterix) {
                    try {
                        value2 = value2.replaceAll(",", ".");
                    } catch (Exception e) {
                        e = e;
                    }
                    try {
                        String value3 = value2.replaceAll(">", "").replaceAll("_", "").replaceAll(" ", "").replaceAll("~", "").replace(Typography.rightSingleQuote, '\'').replace(Typography.rightDoubleQuote, Typography.quote);
                        if (value3.indexOf("-") == 0) {
                            value3 = value3.substring(1);
                        }
                        if (value3.contains("-")) {
                            String tmp = value3.substring(value3.indexOf("-") + 1).replaceAll("[0-9.,-]", "");
                            value3 = value3.substring(0, value3.indexOf("-"));
                            if (value3.matches("\\d+(\\.\\d+)?")) {
                                value3 = value3 + tmp;
                            }
                        }
                        value2 = value3.toLowerCase(Locale.US);
                        if (value2.contains("ft")) {
                            String[] sa = value2.split("ft");
                            float feet = sa.length >= 1 ? Float.parseFloat(sa[0]) : 0.0f;
                            if (sa.length == 2) {
                                String value4 = sa[1];
                                if (value4.indexOf("in") > 0) {
                                    value4 = value4.substring(0, value4.indexOf("in"));
                                }
                                int inch = Integer.parseInt(value4);
                                feet += inch / 12.0f;
                            }
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(0.3048f * feet));
                        } else if (value2.contains("'")) {
                            String[] sa2 = value2.split("'");
                            float feet2 = sa2.length >= 1 ? Float.parseFloat(sa2[0]) : 0.0f;
                            if (sa2.length == 2) {
                                String value5 = sa2[1];
                                if (value5.indexOf("''") > 0) {
                                    value5 = value5.substring(0, value5.indexOf("''"));
                                }
                                if (value5.indexOf("\"") > 0) {
                                    value5 = value5.substring(0, value5.indexOf("\""));
                                }
                                int inch2 = Integer.parseInt(value5);
                                feet2 += inch2 / 12.0f;
                            }
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(0.3048f * feet2));
                        } else if (value2.contains("in") || value2.contains("\"")) {
                            if (value2.indexOf("in") > 0) {
                                value2 = value2.substring(0, value2.indexOf("in"));
                            }
                            if (value2.indexOf("\"") > 0) {
                                value2 = value2.substring(0, value2.indexOf("\""));
                            }
                            float inch3 = Float.parseFloat(value2);
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(0.0254f * inch3));
                        } else if (value2.contains("feet") || value2.contains("foot")) {
                            String s = value2.substring(0, value2.indexOf("f"));
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(0.3048f * Float.parseFloat(s)));
                        } else if (value2.contains("fathom") || value2.contains("fm")) {
                            String s2 = value2.substring(0, value2.indexOf("f"));
                            float fathom = Float.parseFloat(s2);
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(1.8288f * fathom));
                        } else if (value2.contains("cm")) {
                            String[] sa3 = value2.split("cm");
                            if (sa3.length >= 1) {
                                value2 = sa3[0];
                            }
                            float cm = Float.parseFloat(value2);
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(cm / 100.0f));
                        } else if (value2.contains("metre") || value2.contains("meter")) {
                            value2 = value2.substring(0, value2.indexOf("m"));
                        } else if (value2.contains("mph")) {
                            String[] sa4 = value2.split("mph");
                            if (sa4.length >= 1) {
                                value2 = sa4[0];
                            }
                            float mph = Float.parseFloat(value2);
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(1.609344f * mph));
                        } else if (value2.contains("knot")) {
                            String[] sa5 = value2.split("knot");
                            if (sa5.length >= 1) {
                                value2 = sa5[0];
                            }
                            float nm = Float.parseFloat(value2);
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(1.852f * nm));
                        } else if (value2.contains("kmh") || value2.contains("km/h") || value2.contains("kph")) {
                            String[] sa6 = value2.split("k");
                            if (sa6.length > 1) {
                                value2 = sa6[0];
                            }
                        } else if (value2.contains("m")) {
                            value2 = value2.substring(0, value2.indexOf("m"));
                        } else if (value2.contains("(")) {
                            value2 = value2.substring(0, value2.indexOf("("));
                        } else if (value2.contains("st")) {
                            String[] sa7 = value2.split("st");
                            if (sa7.length >= 1) {
                                value2 = sa7[0];
                            }
                            float st = Float.parseFloat(value2);
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(0.907f * st));
                        } else if (value2.contains("kg")) {
                            String[] sa8 = value2.split("kg");
                            if (sa8.length >= 1) {
                                value2 = sa8[0];
                            }
                            float kg = Float.parseFloat(value2);
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(kg / 1000.0f));
                        } else if (value2.contains("lbs")) {
                            String[] sa9 = value2.split("lbs");
                            if (sa9.length >= 1) {
                                value2 = sa9[0];
                            }
                            float lbs = Float.parseFloat(value2);
                            value2 = String.format(Locale.US, "%3.1f", Float.valueOf(lbs / 2204.0f));
                        } else if (value2.contains("t")) {
                            String[] sa10 = value2.split("t");
                            if (sa10.length >= 1) {
                                value2 = sa10[0];
                            }
                        }
                        lookupData2[num.intValue()] = ((int) (Math.abs(Float.parseFloat(value2)) * 100.0f)) + 1000;
                    } catch (Exception e2) {
                        if (this.showErrors) {
                            System.err.println("error for " + name + "  " + value + " trans " + value2 + " " + e2.getMessage());
                        }
                        lookupData2[num.intValue()] = 0;
                    }
                }
                return newValue;
            }
            if (i == 500) {
                return newValue;
            }
            BExpressionLookupValue[] nvalues = new BExpressionLookupValue[values.length + 1];
            int[] nhisto = new int[values.length + 1];
            System.arraycopy(values, 0, nvalues, 0, values.length);
            System.arraycopy(histo2, 0, nhisto, 0, histo2.length);
            histo = nhisto;
            BExpressionLookupValue newValue2 = new BExpressionLookupValue(value2);
            nvalues[i] = newValue2;
            this.lookupHistograms.set(num.intValue(), histo);
            this.lookupValues.set(num.intValue(), nvalues);
            newValue = newValue2;
        }
        histo[i] = histo[i] + 1;
        if (lookupData2 != null) {
            lookupData2[num.intValue()] = i;
        } else {
            this.lookupData[num.intValue()] = i;
        }
        return newValue;
    }

    public void addLookupValue(String name, int valueIndex) {
        Integer num = this.lookupNumbers.get(name);
        if (num == null) {
            return;
        }
        int nvalues = this.lookupValues.get(num.intValue()).length;
        if (valueIndex < 0 || valueIndex >= nvalues) {
            throw new IllegalArgumentException("value index out of range for name " + name + ": " + valueIndex);
        }
        this.lookupData[num.intValue()] = valueIndex;
    }

    public void addSmallestLookupValue(String name, int valueIndex) {
        Integer num = this.lookupNumbers.get(name);
        if (num == null) {
            return;
        }
        int nvalues = this.lookupValues.get(num.intValue()).length;
        int oldValueIndex = this.lookupData[num.intValue()];
        if (oldValueIndex > 1 && oldValueIndex < valueIndex) {
            return;
        }
        if (valueIndex >= nvalues) {
            valueIndex = nvalues - 1;
        }
        if (valueIndex < 0) {
            throw new IllegalArgumentException("value index out of range for name " + name + ": " + valueIndex);
        }
        this.lookupData[num.intValue()] = valueIndex;
    }

    public boolean getBooleanLookupValue(String name) {
        Integer num = this.lookupNumbers.get(name);
        return num != null && this.lookupData[num.intValue()] == 2;
    }

    public int getOutputVariableIndex(String name, boolean mustExist) {
        int idx = getVariableIdx(name, false);
        if (idx < 0) {
            if (mustExist) {
                throw new IllegalArgumentException("unknown variable: " + name);
            }
        } else if (idx < this.minWriteIdx) {
            throw new IllegalArgumentException("bad access to global variable: " + name);
        }
        for (int i = 0; i < this.nBuildInVars; i++) {
            if (this.buildInVariableIdx[i] == idx) {
                return i;
            }
        }
        int i2 = this.nBuildInVars;
        int[] extended = new int[i2 + 1];
        System.arraycopy(this.buildInVariableIdx, 0, extended, 0, this.nBuildInVars);
        extended[this.nBuildInVars] = idx;
        this.buildInVariableIdx = extended;
        int i3 = this.nBuildInVars;
        this.nBuildInVars = i3 + 1;
        return i3;
    }

    public void setForeignContext(BExpressionContext foreignContext) {
        this.foreignContext = foreignContext;
    }

    public float getForeignVariableValue(int foreignIndex) {
        return this.foreignContext.getBuildInVariable(foreignIndex);
    }

    public int getForeignVariableIdx(String context, String name) {
        if (this.foreignContext == null || !context.equals(this.foreignContext.context)) {
            throw new IllegalArgumentException("unknown foreign context: " + context);
        }
        return this.foreignContext.getOutputVariableIndex(name, true);
    }

    public void parseFile(File file, String readOnlyContext) {
        parseFile(file, readOnlyContext, null);
    }

    public void parseFile(File file, String readOnlyContext, Map<String, String> keyValues) {
        if (!file.exists()) {
            throw new IllegalArgumentException("profile " + file.getName() + " does not exist");
        }
        if (readOnlyContext != null) {
            try {
                this.linenr = 1;
                String realContext = this.context;
                this.context = readOnlyContext;
                this.expressionList = _parseFile(file, keyValues);
                this.variableData = new float[this.variableNumbers.size()];
                evaluate(this.lookupData);
                this.context = realContext;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("ParseException " + file.getName() + " at line " + this.linenr + ": " + e.getMessage());
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        }
        this.linenr = 1;
        this.minWriteIdx = this.variableData == null ? 0 : this.variableData.length;
        try {
            this.expressionList = _parseFile(file, null);
        } catch (Exception e3) {
            throw new RuntimeException(e3);
        }
        this.lastAssignedExpression = null;
        String[] varNames = getBuildInVariableNames();
        this.nBuildInVars = varNames.length;
        this.buildInVariableIdx = new int[this.nBuildInVars];
        for (int vi = 0; vi < varNames.length; vi++) {
            this.buildInVariableIdx[vi] = getVariableIdx(varNames[vi], false);
        }
        float[] readOnlyData = this.variableData;
        this.variableData = new float[this.variableNumbers.size()];
        for (int i = 0; i < this.minWriteIdx; i++) {
            this.variableData[i] = readOnlyData[i];
        }
        if (this.expressionList.size() == 0) {
            throw new IllegalArgumentException(file.getName() + " does not contain expressions for context " + this.context + " (old version?)");
        }
    }

    private List<BExpression> _parseFile(File file, Map<String, String> keyValues) throws Exception {
        this._br = new BufferedReader(new FileReader(file));
        this._readerDone = false;
        List<BExpression> result = new ArrayList<>();
        if (keyValues != null) {
            for (String key : keyValues.keySet()) {
                String value = keyValues.get(key);
                result.add(BExpression.createAssignExpressionFromKeyValue(this, key, value));
            }
        }
        while (true) {
            BExpression exp = BExpression.parse(this, 0);
            if (exp != null) {
                result.add(exp);
            } else {
                this._br.close();
                this._br = null;
                return result;
            }
        }
    }

    public void setVariableValue(String name, float value, boolean create) {
        Integer num = this.variableNumbers.get(name);
        if (num != null) {
            this.variableData[num.intValue()] = value;
            return;
        }
        if (create) {
            Integer num2 = Integer.valueOf(getVariableIdx(name, create));
            float[] readOnlyData = this.variableData;
            int minWriteIdx = readOnlyData.length;
            this.variableData = new float[this.variableNumbers.size()];
            for (int i = 0; i < minWriteIdx; i++) {
                this.variableData[i] = readOnlyData[i];
            }
            this.variableData[num2.intValue()] = value;
        }
    }

    public float getVariableValue(String name, float defaultValue) {
        Integer num = this.variableNumbers.get(name);
        return num == null ? defaultValue : getVariableValue(num.intValue());
    }

    float getVariableValue(int variableIdx) {
        return this.variableData[variableIdx];
    }

    int getVariableIdx(String name, boolean create) {
        Integer num = this.variableNumbers.get(name);
        if (num == null) {
            if (create) {
                num = Integer.valueOf(this.variableNumbers.size());
                this.variableNumbers.put(name, num);
                this.lastAssignedExpression.add(null);
            } else {
                return -1;
            }
        }
        return num.intValue();
    }

    int getMinWriteIdx() {
        return this.minWriteIdx;
    }

    float getLookupMatch(int nameIdx, int[] valueIdxArray) {
        for (int i : valueIdxArray) {
            if (this.lookupData[nameIdx] == i) {
                return 1.0f;
            }
        }
        return 0.0f;
    }

    public int getLookupNameIdx(String name) {
        Integer num = this.lookupNumbers.get(name);
        if (num == null) {
            return -1;
        }
        return num.intValue();
    }

    public final void markLookupIdxUsed(int idx) {
        this.lookupIdxUsed[idx] = true;
    }

    public final boolean isLookupIdxUsed(int idx) {
        return idx < this.lookupIdxUsed.length && this.lookupIdxUsed[idx];
    }

    public final void setAllTagsUsed() {
        for (int i = 0; i < this.lookupIdxUsed.length; i++) {
            this.lookupIdxUsed[i] = true;
        }
    }

    public String usedTagList() {
        StringBuilder sb = new StringBuilder();
        for (int inum = 0; inum < this.lookupValues.size(); inum++) {
            if (this.lookupIdxUsed[inum]) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(this.lookupNames.get(inum));
            }
        }
        return sb.toString();
    }

    int getLookupValueIdx(int nameIdx, String value) {
        BExpressionLookupValue[] values = this.lookupValues.get(nameIdx);
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

    String parseToken() throws Exception {
        while (true) {
            String token = _parseToken();
            if (token == null) {
                return null;
            }
            if (token.startsWith(CONTEXT_TAG)) {
                this._inOurContext = token.substring(CONTEXT_TAG.length()).equals(this.context);
            } else if (token.startsWith(MODEL_TAG)) {
                this._modelClass = token.substring(MODEL_TAG.length()).trim();
            } else if (this._inOurContext) {
                return token;
            }
        }
    }

    private String _parseToken() throws Exception {
        boolean inComment;
        Integer num;
        StringBuilder sb = new StringBuilder(32);
        StringBuilder sbcom = new StringBuilder(32);
        boolean inComment2 = false;
        while (true) {
            int ic = this._readerDone ? -1 : this._br.read();
            char c = 1;
            if (ic < 0) {
                if (sb.length() == 0) {
                    return null;
                }
                this._readerDone = true;
                return sb.toString();
            }
            char c2 = (char) ic;
            if (c2 == '\n') {
                this.linenr++;
            }
            if (inComment2) {
                sbcom.append(c2);
                if (c2 == '\r' || c2 == '\n') {
                    inComment2 = false;
                }
                if (inComment2) {
                    inComment = inComment2;
                } else {
                    Integer num2 = this.variableNumbers.get("check_start_way");
                    char c3 = 0;
                    if (num2 == null || this.noStartWays.length != 0 || !sbcom.toString().contains("noStartWay")) {
                        inComment = inComment2;
                    } else {
                        String var = sbcom.toString().trim();
                        String[] savar = var.split("\\|");
                        if (savar.length == 4) {
                            String var2 = savar[3].substring(savar[3].indexOf("=") + 1).trim();
                            String[] sa = var2.split(";");
                            int length = sa.length;
                            int i = 0;
                            while (true) {
                                if (i >= length) {
                                    inComment = inComment2;
                                    break;
                                }
                                String s = sa[i];
                                String[] sa2 = s.split(",");
                                inComment = inComment2;
                                String name = sa2[c3];
                                String value = sa2[c];
                                int nidx = getLookupNameIdx(name);
                                if (nidx == -1) {
                                    break;
                                }
                                int vidx = getLookupValueIdx(nidx, value);
                                int[] tmp = new int[this.noStartWays.length + 2];
                                int ic2 = ic;
                                if (this.noStartWays.length > 0) {
                                    num = num2;
                                    System.arraycopy(this.noStartWays, 0, tmp, 0, this.noStartWays.length);
                                } else {
                                    num = num2;
                                }
                                this.noStartWays = tmp;
                                this.noStartWays[this.noStartWays.length - 2] = nidx;
                                this.noStartWays[this.noStartWays.length - 1] = vidx;
                                i++;
                                c = 1;
                                inComment2 = inComment;
                                ic = ic2;
                                num2 = num;
                                c3 = 0;
                            }
                        } else {
                            inComment = inComment2;
                        }
                    }
                    sbcom.setLength(0);
                }
                inComment2 = inComment;
            } else if (Character.isWhitespace(c2)) {
                if (sb.length() > 0) {
                    return sb.toString();
                }
            } else if (c2 == '#' && sb.length() == 0) {
                inComment2 = true;
            } else {
                sb.append(c2);
            }
        }
    }

    float assign(int variableIdx, float value) {
        this.variableData[variableIdx] = value;
        return value;
    }

    public boolean checkStartWay(byte[] ab) {
        if (ab == null) {
            return true;
        }
        Arrays.fill(this.ld2, 0);
        decode(this.ld2, false, ab);
        for (int i = 0; i < this.noStartWays.length; i += 2) {
            int key = this.noStartWays[i];
            int value = this.noStartWays[i + 1];
            if (this.ld2[key] == value) {
                return false;
            }
        }
        return true;
    }

    public void freeNoWays() {
        this.noStartWays = new int[0];
    }
}

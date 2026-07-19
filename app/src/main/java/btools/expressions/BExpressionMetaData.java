package btools.expressions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class BExpressionMetaData {
    private static final String CONTEXT_TAG = "---context:";
    private static final String MINOR_VERSION_TAG = "---minorversion:";
    private static final String MIN_APP_VERSION_TAG = "---minappversion:";
    private static final String VARLENGTH_TAG = "---readvarlength";
    private static final String VERSION_TAG = "---lookupversion:";
    public short lookupVersion = -1;
    public short lookupMinorVersion = -1;
    public short minAppVersion = -1;
    private Map<String, BExpressionContext> listeners = new HashMap();

    public void registerListener(String context, BExpressionContext ctx) {
        this.listeners.put(context, ctx);
    }

    public void readMetaData(File lookupsFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(lookupsFile));
            BExpressionContext ctx = null;
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                String line2 = line.trim();
                if (line2.length() != 0 && !line2.startsWith("#")) {
                    if (line2.startsWith(CONTEXT_TAG)) {
                        ctx = this.listeners.get(line2.substring(CONTEXT_TAG.length()));
                    } else if (line2.startsWith(VERSION_TAG)) {
                        this.lookupVersion = Short.parseShort(line2.substring(VERSION_TAG.length()));
                    } else if (line2.startsWith(MINOR_VERSION_TAG)) {
                        this.lookupMinorVersion = Short.parseShort(line2.substring(MINOR_VERSION_TAG.length()));
                    } else if (line2.startsWith(MIN_APP_VERSION_TAG)) {
                        this.minAppVersion = Short.parseShort(line2.substring(MIN_APP_VERSION_TAG.length()));
                    } else if (!line2.startsWith(VARLENGTH_TAG) && ctx != null) {
                        ctx.parseMetaLine(line2);
                    }
                }
            }
            br.close();
            for (BExpressionContext c : this.listeners.values()) {
                c.finishMetaParsing();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

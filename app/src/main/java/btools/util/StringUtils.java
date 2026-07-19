package btools.util;

import kotlin.text.Typography;

/* JADX INFO: loaded from: classes.dex */
public class StringUtils {
    private static char[] xmlChr = {Typography.amp, Typography.less, Typography.greater, '\'', Typography.quote, '\t', '\n', '\r'};
    private static String[] xmlEsc = {"&amp;", "&lt;", "&gt;", "&apos;", "&quot;", "&#x9;", "&#xA;", "&#xD;"};
    private static char[] jsnChr = {'\'', Typography.quote, '\\', '/'};
    private static String[] jsnEsc = {"\\'", "\\\"", "\\\\", "\\/"};

    public static String escapeJson(String s) {
        return escape(s, jsnChr, jsnEsc);
    }

    public static String escapeXml10(String s) {
        return escape(s, xmlChr, xmlEsc);
    }

    private static String escape(String s, char[] chr, String[] esc) {
        StringBuilder sb = null;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int j = 0;
            while (true) {
                if (j >= chr.length) {
                    break;
                }
                if (c == chr[j]) {
                    if (sb == null) {
                        sb = new StringBuilder(s.substring(0, i));
                    }
                    sb.append(esc[j]);
                } else {
                    j++;
                }
            }
            if (sb != null && j == chr.length) {
                sb.append(c);
            }
        }
        return sb == null ? s : sb.toString();
    }
}

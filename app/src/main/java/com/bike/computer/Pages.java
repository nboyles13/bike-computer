package com.bike.computer;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: Prefs.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0005J\u000e\u0010\f\u001a\u00020\r2\u0006\u0010\u000b\u001a\u00020\u0005J\u000e\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\rJ\u000e\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u000b\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/bike/computer/Pages;", "", "<init>", "()V", Pages.MAP, "", Pages.HR, Pages.ELEV, Pages.SUMMARY, "isData", "", "key", "dataId", "", "id", "fixedTitle", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class Pages {
    public static final String ELEV = "ELEV";
    public static final String HR = "HR";
    public static final Pages INSTANCE = new Pages();
    public static final String MAP = "MAP";
    public static final String SUMMARY = "SUMMARY";

    private Pages() {
    }

    public final boolean isData(String key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return StringsKt.startsWith$default(key, "DATA", false, 2, (Object) null);
    }

    public final int dataId(String key) {
        Intrinsics.checkNotNullParameter(key, "key");
        Integer intOrNull = StringsKt.toIntOrNull(StringsKt.removePrefix(key, (CharSequence) "DATA"));
        if (intOrNull != null) {
            return intOrNull.intValue();
        }
        return 0;
    }

    public final String key(int id) {
        return "DATA" + id;
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public final String fixedTitle(String key) {
        Intrinsics.checkNotNullParameter(key, "key");
        switch (key.hashCode()) {
            case -1139657850:
                if (!key.equals(SUMMARY)) {
                }
                break;
            case 2314:
                if (!key.equals(HR)) {
                }
                break;
            case 76092:
                if (!key.equals(MAP)) {
                    break;
                }
                break;
            case 2130840:
                if (!key.equals(ELEV)) {
                }
                break;
        }
        return key;
    }
}

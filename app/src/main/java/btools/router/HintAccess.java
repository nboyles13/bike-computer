package btools.router;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: HintAccess.kt */
/* JADX INFO: loaded from: classes5.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0006\u0010\u0007\u001a\u00020\bH\u0007J\u0012\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\nH\u0002¨\u0006\f"}, d2 = {"Lbtools/router/HintAccess;", "", "<init>", "()V", "read", "", "Lbtools/router/NavHint;", "track", "Lbtools/router/OsmTrack;", "nameFrom", "", "kv", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class HintAccess {
    public static final HintAccess INSTANCE = new HintAccess();

    private HintAccess() {
    }

    @JvmStatic
    public static final List<NavHint> read(OsmTrack track) {
        String cmd;
        Intrinsics.checkNotNullParameter(track, "track");
        VoiceHintList vl = track.voiceHints;
        if (vl == null) {
            return CollectionsKt.emptyList();
        }
        ArrayList out = new ArrayList();
        for (VoiceHint vh : vl.list) {
            try {
                cmd = vh.getCruiserCommandString();
                if (cmd == null) {
                    cmd = "C";
                }
            } catch (Exception e) {
                cmd = "C";
            }
            MessageData messageData = vh.goodWay;
            String kv = messageData != null ? messageData.wayKeyValues : null;
            Log.i("BikeRoute", "hint cmd=" + cmd + " kv=" + kv);
            out.add(new NavHint((((double) vh.ilat) / 1000000.0d) - 90.0d, (((double) vh.ilon) / 1000000.0d) - 180.0d, vh.indexInTrack, cmd, INSTANCE.nameFrom(kv)));
        }
        return out;
    }

    private final String nameFrom(String kv) {
        MatchResult m;
        String str = kv;
        return ((str == null || str.length() == 0) || (m = Regex.find$default(new Regex("(?:^|\\s)name=(.+?)(?:\\s+[\\w:]+=|$)"), kv, 0, 2, null)) == null) ? "" : StringsKt.trim((CharSequence) m.getGroupValues().get(1)).toString();
    }
}

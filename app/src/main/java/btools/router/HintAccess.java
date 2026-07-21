package btools.router;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

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
        return ((str == null || str.length() == 0) || (m = new Regex("(?:^|\\s)name=(.+?)(?:\\s+[\\w:]+=|$)").find(kv, 0)) == null) ? "" : StringsKt.trim((CharSequence) m.getGroupValues().get(1)).toString();
    }
}

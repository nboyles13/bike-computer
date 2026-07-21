package com.bike.computer;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.DebugKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class Prefs {
    private static final String P = "bike_prefs";
    public static final Prefs INSTANCE = new Prefs();
    private static final List<DashBlock> DEFAULT = ArraysKt.toList(DashBlock.values());
    private static final List<String> DEFAULT_PAGE_KEYS = CollectionsKt.listOf(new String[]{Pages.SUMMARY, "DATA0", Pages.MAP, Pages.HR, Pages.ELEV});

    private Prefs() {
    }

    private final SharedPreferences sp(Context c) {
        return c.getSharedPreferences(P, 0);
    }

    private final List<DashTile> defaultTiles() {
        return CollectionsKt.listOf(new DashTile[]{new DashTile(Metric.SPEED, 0, 0, 2, 1), new DashTile(Metric.HR, 2, 0, 2, 1), new DashTile(Metric.DISTANCE, 0, 1, 2, 1), new DashTile(Metric.RIDE_TIME, 2, 1, 2, 1), new DashTile(Metric.ELEVATION, 0, 2, 2, 1), new DashTile(Metric.GRADE, 2, 2, 2, 1)});
    }

    /* JADX WARN: Removed duplicated region for block: B:26:0x00e4  */
    /* JADX WARN: Removed duplicated region for block: B:27:0x00e6  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x00eb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final List<DashTile> dashTiles(Context c, String pageKey) {
        Object objM118constructorimpl;
        List<DashTile> list;
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(pageKey, "pageKey");
        String s = sp(c).getString("dash_tiles_" + pageKey, null);
        if (s == null) {
            s = Intrinsics.areEqual(pageKey, "DATA0") ? sp(c).getString("dash_tiles", null) : null;
            if (s == null) {
                return defaultTiles();
            }
        }
        try {
            JSONArray arr = new JSONArray(s);
            Iterable $this$map$iv = RangesKt.until(0, arr.length());
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
            Iterator<Integer> it = $this$map$iv.iterator();
            while (it.hasNext()) {
                int item$iv$iv = ((IntIterator) it).nextInt();
                JSONObject o = arr.getJSONObject(item$iv$iv);
                String string = o.getString("m");
                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                destination$iv$iv.add(new DashTile(Metric.valueOf(string), o.getInt("c"), o.getInt("r"), o.getInt("w"), o.getInt("h")));
            }
            objM118constructorimpl = (List) destination$iv$iv;
        } catch (Throwable th2) {
            objM118constructorimpl = null;
        }
        list = (List) objM118constructorimpl;
        if (list != null) {
            List<DashTile> listDefaultTiles = list;
            if (listDefaultTiles.isEmpty()) {
                listDefaultTiles = INSTANCE.defaultTiles();
            }
            List<DashTile> list2 = listDefaultTiles;
            if (list2 != null) {
                return list2;
            }
        }
        return defaultTiles();
    }

    public final void setDashTiles(Context c, String pageKey, List<DashTile> tiles) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(pageKey, "pageKey");
        Intrinsics.checkNotNullParameter(tiles, "tiles");
        JSONArray arr = new JSONArray();
        List<DashTile> $this$forEach$iv = tiles;
        for (Object element$iv : $this$forEach$iv) {
            DashTile it = (DashTile) element$iv;
            try {
                arr.put(new JSONObject().put("m", it.getMetric().name()).put("c", it.getCol()).put("r", it.getRow()).put("w", it.getW()).put("h", it.getH()));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        sp(c).edit().putString("dash_tiles_" + pageKey, arr.toString()).apply();
    }

    public final boolean autoPause(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("auto_pause", true);
    }

    public final void setAutoPause(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("auto_pause", v).apply();
    }

    public final boolean voice(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("voice", true);
    }

    public final void setVoice(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("voice", v).apply();
    }

    public final int maxHr(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getInt("max_hr", 185);
    }

    public final void setMaxHr(Context c, int v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putInt("max_hr", RangesKt.coerceIn(v, 120, 220)).apply();
    }

    public final boolean led(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("led", true);
    }

    public final void setLed(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("led", v).apply();
    }

    public final boolean closeApps(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("close_apps", true);
    }

    public final void setCloseApps(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("close_apps", v).apply();
    }

    public final boolean lowPowerMap(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("low_power_map", false);
    }

    public final void setLowPowerMap(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("low_power_map", v).apply();
    }

    public final boolean wifiOffOnRide(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("wifi_off_ride", true);
    }

    public final void setWifiOffOnRide(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("wifi_off_ride", v).apply();
    }

    public final boolean wifiDisabledByApp(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("wifi_disabled_by_app", false);
    }

    public final void setWifiDisabledByApp(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("wifi_disabled_by_app", v).apply();
    }

    public final boolean endurance(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("endurance", false);
    }

    public final void setEndurance(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("endurance", v).apply();
    }

    public final String lastRouteUrl(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("route_url", "");
        return string == null ? "" : string;
    }

    public final void setLastRouteUrl(Context c, String v) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(v, "v");
        sp(c).edit().putString("route_url", StringsKt.trim((CharSequence) v).toString()).apply();
    }

    public final Set<String> starredRoutes(Context c) {
        Object objM118constructorimpl;
        Intrinsics.checkNotNullParameter(c, "c");
        String s = sp(c).getString("starred_routes", null);
        if (s == null) {
            return SetsKt.emptySet();
        }
        try {
            JSONArray a = new JSONArray(s);
            Iterable $this$map$iv = RangesKt.until(0, a.length());
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
            Iterator<Integer> it = $this$map$iv.iterator();
            while (it.hasNext()) {
                int item$iv$iv = ((IntIterator) it).nextInt();
                destination$iv$iv.add(a.getString(item$iv$iv));
            }
            objM118constructorimpl = CollectionsKt.toSet((List) destination$iv$iv);
        } catch (Throwable th) {
            objM118constructorimpl = SetsKt.emptySet();
        }
        return (Set) objM118constructorimpl;
    }

    public final boolean isRouteStarred(Context c, String name) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(name, "name");
        return starredRoutes(c).contains(name);
    }

    public final void setRouteStarred(Context c, String name, boolean on) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(name, "name");
        Set set = CollectionsKt.toMutableSet(starredRoutes(c));
        if (on) {
            set.add(name);
        } else {
            set.remove(name);
        }
        JSONArray a = new JSONArray();
        Set $this$forEach$iv = set;
        for (Object element$iv : $this$forEach$iv) {
            String it = (String) element$iv;
            a.put(it);
        }
        sp(c).edit().putString("starred_routes", a.toString()).apply();
    }

    public final List<String> pageOrder(Context c) {
        Iterable iterableSplit$default;
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("page_order", null);
        if (string == null || (iterableSplit$default = StringsKt.split((CharSequence) string, new String[]{","}, false, 0)) == null) {
            return DEFAULT_PAGE_KEYS;
        }
        Iterable $this$filter$iv = iterableSplit$default;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            String it = (String) element$iv$iv;
            if (it.length() > 0) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        List saved = (List) destination$iv$iv;
        return saved.contains(Pages.MAP) ? saved : CollectionsKt.plus((Collection<? extends String>) saved, Pages.MAP);
    }

    public final void setPageOrder(Context c, List<String> list) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(list, "list");
        sp(c).edit().putString("page_order", CollectionsKt.joinToString(list, ",", "", "", -1, "...", null)).apply();
    }

    public final boolean pageEnabled(Context c, String key) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(key, "key");
        if (Intrinsics.areEqual(key, Pages.MAP)) {
            return true;
        }
        String string = sp(c).getString("pages_disabled", "");
        return !StringsKt.split((CharSequence) (string != null ? string : ""), new String[]{","}, false, 0).contains(key);
    }

    public final void setPageEnabled(Context c, String key, boolean on) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(key, "key");
        if (Intrinsics.areEqual(key, Pages.MAP)) {
            return;
        }
        String string = sp(c).getString("pages_disabled", "");
        Iterable $this$filter$iv = StringsKt.split((CharSequence) (string != null ? string : ""), new String[]{","}, false, 0);
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            String it = (String) element$iv$iv;
            if (it.length() > 0) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        Set dis = CollectionsKt.toMutableSet((List) destination$iv$iv);
        if (on) {
            dis.remove(key);
        } else {
            dis.add(key);
        }
        sp(c).edit().putString("pages_disabled", CollectionsKt.joinToString(dis, ",", "", "", -1, "...", null)).apply();
    }

    public final List<String> enabledPagesInOrder(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        Iterable $this$filter$iv = pageOrder(c);
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            String it = (String) element$iv$iv;
            if (INSTANCE.pageEnabled(c, it)) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        return (List) destination$iv$iv;
    }

    public final String pageSignature(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return CollectionsKt.joinToString(enabledPagesInOrder(c), ",", "", "", -1, "...", null);
    }

    public final String addDataPage(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        List<String> mutableList = CollectionsKt.toMutableList((Collection) pageOrder(c));
        Sequence $this$first$iv = SequencesKt.generateSequence(0, (Function1) new Function1() { // from class: com.bike.computer.Prefs$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Prefs.addDataPage$lambda$10(((Integer) obj).intValue());
            }
        });
        Iterator $first$iter = $this$first$iv.iterator();
        while ($first$iter.hasNext()) {
            Object element$iv = $first$iter.next();
            if (!mutableList.contains(Pages.INSTANCE.key(((Number) element$iv).intValue()))) {
                int id = ((Number) element$iv).intValue();
                String key = Pages.INSTANCE.key(id);
                setDashTiles(c, key, defaultTiles());
                int it = mutableList.indexOf(Pages.MAP);
                if (it < 0) {
                    it = mutableList.size();
                }
                mutableList.add(it, key);
                setPageOrder(c, mutableList);
                return key;
            }
        }
        throw new NoSuchElementException("Sequence contains no element matching the predicate.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Integer addDataPage$lambda$10(int it) {
        return Integer.valueOf(it + 1);
    }

    public final void removeDataPage(Context c, String key) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(key, "key");
        Iterable $this$filter$iv = pageOrder(c);
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            String it = (String) element$iv$iv;
            if (!Intrinsics.areEqual(it, key)) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        setPageOrder(c, (List) destination$iv$iv);
        setPageEnabled(c, key, true);
        sp(c).edit().remove("dash_tiles_" + key).apply();
    }

    public final String driveClientId(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("drive_cid", "");
        return string == null ? "" : string;
    }

    public final String driveClientSecret(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("drive_secret", "");
        return string == null ? "" : string;
    }

    public final void setDriveApp(Context c, String id, String secret) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(id, "id");
        Intrinsics.checkNotNullParameter(secret, "secret");
        sp(c).edit().putString("drive_cid", StringsKt.trim((CharSequence) id).toString()).putString("drive_secret", StringsKt.trim((CharSequence) secret).toString()).apply();
    }

    public final String driveAccessToken(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("drive_access", "");
        return string == null ? "" : string;
    }

    public final String driveRefreshToken(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("drive_refresh", "");
        return string == null ? "" : string;
    }

    public final long driveExpiresAt(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getLong("drive_expires", 0L);
    }

    public final String driveFolderId(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("drive_folder", "");
        return string == null ? "" : string;
    }

    public final void setDriveFolderId(Context c, String id) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(id, "id");
        sp(c).edit().putString("drive_folder", id).apply();
    }

    public final String driveRidesFolder(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("drive_rides_folder", null);
        String str = "BikeComputer";
        if (string == null) {
            string = "BikeComputer";
        }
        String str2 = string;
        if (!StringsKt.isBlank(str2)) {
            str = str2;
        }
        return str;
    }

    public final void setDriveRidesFolder(Context c, String v) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(v, "v");
        String string = StringsKt.trim((CharSequence) v).toString();
        if (string.length() == 0) {
            string = "BikeComputer";
        }
        String name = string;
        if (!Intrinsics.areEqual(name, driveRidesFolder(c))) {
            sp(c).edit().remove("drive_folder").apply();
        }
        sp(c).edit().putString("drive_rides_folder", name).apply();
    }

    public final String driveRoutesFolder(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("drive_routes_folder", null);
        String str = "Harmin Routes";
        if (string == null) {
            string = "Harmin Routes";
        }
        String str2 = string;
        if (!StringsKt.isBlank(str2)) {
            str = str2;
        }
        return str;
    }

    public final void setDriveRoutesFolder(Context c, String v) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(v, "v");
        SharedPreferences.Editor editorEdit = sp(c).edit();
        String string = StringsKt.trim((CharSequence) v).toString();
        if (string.length() == 0) {
            string = "Harmin Routes";
        }
        editorEdit.putString("drive_routes_folder", string).apply();
    }

    public final String driveSheetId(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        String string = sp(c).getString("drive_sheet", "");
        return string == null ? "" : string;
    }

    public final void setDriveSheetId(Context c, String id) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(id, "id");
        sp(c).edit().putString("drive_sheet", id).apply();
    }

    public final void setDriveTokens(Context c, String access, String refresh, long expiresAt) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(access, "access");
        SharedPreferences.Editor e = sp(c).edit().putString("drive_access", access).putLong("drive_expires", expiresAt);
        String str = refresh;
        if (!(str == null || str.length() == 0)) {
            e.putString("drive_refresh", refresh);
        }
        e.apply();
    }

    public final boolean driveConnected(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return driveRefreshToken(c).length() > 0;
    }

    public final void clearDriveTokens(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().remove("drive_access").remove("drive_refresh").remove("drive_expires").remove("drive_folder").apply();
    }

    public final boolean driveAutoUpload(Context c) {
        Intrinsics.checkNotNullParameter(c, "c");
        return sp(c).getBoolean("drive_auto", false);
    }

    public final void setDriveAutoUpload(Context c, boolean v) {
        Intrinsics.checkNotNullParameter(c, "c");
        sp(c).edit().putBoolean("drive_auto", v).apply();
    }

    public final List<DashBlock> dashboard(Context c) {
        Object objM118constructorimpl;
        Intrinsics.checkNotNullParameter(c, "c");
        String s = sp(c).getString("dash_blocks", null);
        if (s == null) {
            return DEFAULT;
        }
        Iterable $this$mapNotNull$iv = StringsKt.split((CharSequence) s, new String[]{","}, false, 0);
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            String it = (String) element$iv$iv$iv;
            Prefs prefs = INSTANCE;
            try {
                objM118constructorimpl = DashBlock.valueOf(it);
            } catch (Throwable th) {
                objM118constructorimpl = null;
            }
            DashBlock dashBlock = (DashBlock) objM118constructorimpl;
            if (dashBlock != null) {
                destination$iv$iv.add(dashBlock);
            }
        }
        List list = (List) destination$iv$iv;
        List list2 = list;
        if (list2.isEmpty()) {
            list2 = DEFAULT;
        }
        return list2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence setDashboard$lambda$21(DashBlock it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return it.name();
    }

    public final void setDashboard(Context c, List<? extends DashBlock> list) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(list, "list");
        sp(c).edit().putString("dash_blocks", CollectionsKt.joinToString(list, ",", "", "", -1, "...", new Function1() { // from class: com.bike.computer.Prefs$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Prefs.setDashboard$lambda$21((DashBlock) obj);
            }
        })).apply();
    }
}

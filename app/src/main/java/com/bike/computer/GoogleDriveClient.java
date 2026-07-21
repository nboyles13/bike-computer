package com.bike.computer;

import android.content.Context;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlin.text.Typography;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class GoogleDriveClient {
    private static final String AUTH = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String SCOPE = "https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/drive.readonly";
    private static final String TOKEN = "https://oauth2.googleapis.com/token";
    public static final GoogleDriveClient INSTANCE = new GoogleDriveClient();
    private static final OkHttpClient http = new OkHttpClient();

    private GoogleDriveClient() {
    }

    private final String enc(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public final String authorizeUrl(String clientId, String redirect) {
        Intrinsics.checkNotNullParameter(clientId, "clientId");
        Intrinsics.checkNotNullParameter(redirect, "redirect");
        return "https://accounts.google.com/o/oauth2/v2/auth?client_id=" + enc(clientId) + "&redirect_uri=" + enc(redirect) + "&response_type=code&scope=" + enc(SCOPE) + "&access_type=offline&prompt=consent";
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void exchangeCode(Context c, String code, String redirect) throws JSONException, IOException {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(code, "code");
        Intrinsics.checkNotNullParameter(redirect, "redirect");
        JSONObject jSONObjectPost = post(TOKEN, new FormBody.Builder().add("client_id", Prefs.INSTANCE.driveClientId(c)).add("client_secret", Prefs.INSTANCE.driveClientSecret(c)).add("code", code).add("redirect_uri", redirect).add("grant_type", "authorization_code").build());
        Prefs prefs = Prefs.INSTANCE;
        String string = jSONObjectPost.getString("access_token");
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        prefs.setDriveTokens(c, string, jSONObjectPost.optString("refresh_token"), now() + jSONObjectPost.getLong("expires_in"));
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final String freshToken(Context c) throws JSONException, IOException {
        if (now() < Prefs.INSTANCE.driveExpiresAt(c) - ((long) 60)) {
            return Prefs.INSTANCE.driveAccessToken(c);
        }
        JSONObject jSONObjectPost = post(TOKEN, new FormBody.Builder().add("client_id", Prefs.INSTANCE.driveClientId(c)).add("client_secret", Prefs.INSTANCE.driveClientSecret(c)).add("grant_type", "refresh_token").add("refresh_token", Prefs.INSTANCE.driveRefreshToken(c)).build());
        Prefs prefs = Prefs.INSTANCE;
        String string = jSONObjectPost.getString("access_token");
        Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
        prefs.setDriveTokens(c, string, null, now() + jSONObjectPost.getLong("expires_in"));
        String string2 = jSONObjectPost.getString("access_token");
        Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
        return string2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String uploadGpx(Context c, File file) throws JSONException, IOException {
        String strString;
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(file, "file");
        String strFreshToken = freshToken(c);
        JSONObject jSONObjectPut = new JSONObject().put("name", file.getName()).put("parents", new JSONArray().put(ensureFolder(c, strFreshToken)));
        MultipartBody.Builder type = new MultipartBody.Builder().setType(MediaType.Companion.get("multipart/related"));
        RequestBody.Companion companion = RequestBody.Companion;
        String string = jSONObjectPut.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        Response responseExecute = http.newCall(new Request.Builder().url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart").header("Authorization", "Bearer " + strFreshToken).post(type.addPart(companion.create(string, MediaType.Companion.get("application/json; charset=UTF-8"))).addPart(RequestBody.Companion.create(file, MediaType.Companion.get("application/gpx+xml"))).build()).build()).execute();
        try {
            Response response = responseExecute;
            ResponseBody responseBodyBody = response.body();
            if (responseBodyBody == null || (strString = responseBodyBody.string()) == null) {
                strString = "";
            }
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP " + response.code() + ": " + strString);
            }
            CloseableKt.closeFinally(responseExecute, null);
            return "Uploaded to Drive";
        } finally {
        }
    }

    private final String ensureFolder(Context c, String token) throws JSONException, IOException {
        String strString;
        String it = Prefs.INSTANCE.driveFolderId(c);
        if (!(it.length() > 0)) {
            it = null;
        }
        if (it != null) {
            return it;
        }
        String name = Prefs.INSTANCE.driveRidesFolder(c);
        String it2 = findFolderId(token, name);
        if (it2 != null) {
            Prefs.INSTANCE.setDriveFolderId(c, it2);
            return it2;
        }
        JSONObject meta = new JSONObject().put("name", name).put("mimeType", "application/vnd.google-apps.folder");
        Request.Builder builderHeader = new Request.Builder().url("https://www.googleapis.com/drive/v3/files?fields=id").header("Authorization", "Bearer " + token);
        RequestBody.Companion companion = RequestBody.Companion;
        String string = meta.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        Request create = builderHeader.post(companion.create(string, MediaType.Companion.get("application/json; charset=UTF-8"))).build();
        Response responseExecute = http.newCall(create).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody == null || (strString = responseBodyBody.string()) == null) {
                strString = "{}";
            }
            String id = new JSONObject(strString).getString("id");
            Prefs prefs = Prefs.INSTANCE;
            Intrinsics.checkNotNull(id);
            prefs.setDriveFolderId(c, id);
            CloseableKt.closeFinally(responseExecute, null);
            return id;
        } finally {
        }
    }

    private final String findFolderId(String token, String name) throws IOException {
        String strString;
        String safe = StringsKt.replace(StringsKt.replace(name, "\\", "\\\\", false), "'", "\\'", false);
        String q = enc("mimeType='application/vnd.google-apps.folder' and name='" + safe + "' and trashed=false");
        Request req = new Request.Builder().url("https://www.googleapis.com/drive/v3/files?q=" + q + "&fields=files(id)").header("Authorization", "Bearer " + token).build();
        Response responseExecute = http.newCall(req).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody == null || (strString = responseBodyBody.string()) == null) {
                strString = "{}";
            }
            JSONArray files;
            String string;
            try {
                files = new JSONObject(strString).optJSONArray("files");
                string = (files == null || files.length() <= 0) ? null : files.getJSONObject(0).getString("id");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            CloseableKt.closeFinally(responseExecute, null);
            return string;
        } finally {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x009f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final List<DriveFile> listRoutes(Context c) throws JSONException, IOException {
        Throwable th = null;
        Intrinsics.checkNotNullParameter(c, "c");
        String token = freshToken(c);
        String name = Prefs.INSTANCE.driveRoutesFolder(c);
        String folder = findFolderId(token, name);
        if (folder == null) {
            throw new RuntimeException("Can't find Drive folder \"" + name + "\" — create it & add .gpx files, or Reconnect Drive to grant read access");
        }
        String q = enc("'" + folder + "' in parents and trashed=false");
        Request req = new Request.Builder().url("https://www.googleapis.com/drive/v3/files?q=" + q + "&fields=files(id,name)&pageSize=300&orderBy=name").header("Authorization", "Bearer " + token).build();
        Response responseExecute = http.newCall(req).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody != null) {
                try {
                    String txt = responseBodyBody.string();
                    if (txt == null) {
                        txt = "";
                    }
                    try {
                        if (!resp.isSuccessful()) {
                            throw new RuntimeException("HTTP " + resp.code() + ": " + txt);
                        }
                        JSONArray arr = new JSONObject(txt).optJSONArray("files");
                        if (arr == null) {
                            List<DriveFile> listEmptyList = CollectionsKt.emptyList();
                            CloseableKt.closeFinally(responseExecute, null);
                            return listEmptyList;
                        }
                        Iterable $this$map$iv = RangesKt.until(0, arr.length());
                        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                        Iterator<Integer> it = $this$map$iv.iterator();
                        while (it.hasNext()) {
                            int item$iv$iv = ((IntIterator) it).nextInt();
                            String token2 = token;
                            String folder2 = folder;
                            try {
                                String q2 = q;
                                String string = arr.getJSONObject(item$iv$iv).getString("id");
                                Intrinsics.checkNotNullExpressionValue(string, "getString(...)");
                                String string2 = arr.getJSONObject(item$iv$iv).getString("name");
                                Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
                                destination$iv$iv.add(new DriveFile(string, string2));
                                folder = folder2;
                                token = token2;
                                q = q2;
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                        Iterable $this$filter$iv = (List) destination$iv$iv;
                        int $i$f$filter = 0;
                        Collection destination$iv$iv2 = new ArrayList();
                        Iterable $this$filterTo$iv$iv = $this$filter$iv;
                        for (Object element$iv$iv : $this$filterTo$iv$iv) {
                            DriveFile it2 = (DriveFile) element$iv$iv;
                            Iterable $this$filter$iv2 = $this$filter$iv;
                            int $i$f$filter2 = $i$f$filter;
                            Iterable $this$filterTo$iv$iv2 = $this$filterTo$iv$iv;
                            if (StringsKt.endsWith(it2.getName(), ".gpx", true)) {
                                destination$iv$iv2.add(element$iv$iv);
                            }
                            $this$filter$iv = $this$filter$iv2;
                            $i$f$filter = $i$f$filter2;
                            $this$filterTo$iv$iv = $this$filterTo$iv$iv2;
                        }
                        List arrayList = (List) destination$iv$iv2;
                        CloseableKt.closeFinally(responseExecute, null);
                        return arrayList;
                    } catch (Throwable th3) {
                        th = th3;
                    }
                } catch (Throwable th4) {
                    th = th4;
                }
            }
        } catch (Throwable th5) {
            th = th5;
        }
        try {
            throw th;
        } catch (Throwable th6) {
            CloseableKt.closeFinally(responseExecute, th);
            throw Sneaky.sneak(th6);
        }
    }

    public final String download(Context c, String fileId) throws JSONException, IOException {
        String txt;
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(fileId, "fileId");
        String token = freshToken(c);
        Request req = new Request.Builder().url("https://www.googleapis.com/drive/v3/files/" + fileId + "?alt=media").header("Authorization", "Bearer " + token).build();
        Response responseExecute = http.newCall(req).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody == null || (txt = responseBodyBody.string()) == null) {
                txt = "";
            }
            if (!resp.isSuccessful()) {
                throw new RuntimeException("HTTP " + resp.code() + ": " + txt);
            }
            CloseableKt.closeFinally(responseExecute, null);
            return txt;
        } finally {
        }
    }

    public final int syncRoutes(Context c, String localDir) throws JSONException, IOException {
        Object objM118constructorimpl;
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(localDir, "localDir");
        File dir = new File(localDir);
        dir.mkdirs();
        try {
            objM118constructorimpl = Integer.valueOf(syncGpxFiles(c, dir));
        } catch (Throwable th) {
            objM118constructorimpl = 0;
        }
        int added = 0 + ((Number) objM118constructorimpl).intValue();
        return added + syncLinkSheet(c, dir);
    }

    private final int syncGpxFiles(Context c, File dir) throws JSONException, IOException {
        Object objM118constructorimpl;
        int added = 0;
        for (DriveFile f : listRoutes(c)) {
            File local = new File(dir, f.getName());
            if (!local.exists()) {
                try {
                    objM118constructorimpl = download(c, f.getId());
                } catch (Throwable th) {
                    objM118constructorimpl = null;
                }
                String gpx = (String) objM118constructorimpl;
                if (gpx != null && GpxRoute.INSTANCE.parse(gpx).size() >= 2) {
                    FilesKt.writeText(local, gpx, kotlin.text.Charsets.UTF_8);
                    added++;
                }
            }
        }
        return added;
    }

    public final String sheetUrl(String id) {
        Intrinsics.checkNotNullParameter(id, "id");
        return "https://docs.google.com/spreadsheets/d/" + id + "/edit";
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final String ensureLinksSheet(Context c) throws JSONException, IOException {
        String strString;
        Intrinsics.checkNotNullParameter(c, "c");
        String strDriveSheetId = Prefs.INSTANCE.driveSheetId(c);
        if (!(strDriveSheetId.length() > 0)) {
            strDriveSheetId = null;
        }
        if (strDriveSheetId != null) {
            return strDriveSheetId;
        }
        String strFreshToken = freshToken(c);
        String strEnsureRoutesFolder = ensureRoutesFolder(c, strFreshToken);
        String strFindSheetInFolder = findSheetInFolder(strFreshToken, strEnsureRoutesFolder);
        if (strFindSheetInFolder != null) {
            Prefs.INSTANCE.setDriveSheetId(c, strFindSheetInFolder);
            return strFindSheetInFolder;
        }
        JSONObject jSONObjectPut = new JSONObject().put("name", "Harmin Route Links").put("mimeType", "application/vnd.google-apps.spreadsheet").put("parents", new JSONArray().put(strEnsureRoutesFolder));
        MultipartBody.Builder type = new MultipartBody.Builder().setType(MediaType.Companion.get("multipart/related"));
        RequestBody.Companion companion = RequestBody.Companion;
        String string = jSONObjectPut.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        Response responseExecute = http.newCall(new Request.Builder().url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id").header("Authorization", "Bearer " + strFreshToken).post(type.addPart(companion.create(string, MediaType.Companion.get("application/json; charset=UTF-8"))).addPart(RequestBody.Companion.create("Name,Google Maps Link\n", MediaType.Companion.get("text/csv"))).build()).build()).execute();
        try {
            Response response = responseExecute;
            ResponseBody responseBodyBody = response.body();
            if (responseBodyBody == null || (strString = responseBodyBody.string()) == null) {
                strString = "";
            }
            if (!response.isSuccessful()) {
                throw new RuntimeException("HTTP " + response.code() + ": " + strString);
            }
            String string2 = new JSONObject(strString).getString("id");
            Prefs prefs = Prefs.INSTANCE;
            Intrinsics.checkNotNull(string2);
            prefs.setDriveSheetId(c, string2);
            CloseableKt.closeFinally(responseExecute, null);
            return string2;
        } finally {
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:32:0x00a4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final int syncLinkSheet(Context c, File dir) throws JSONException, IOException {
        int i;
        int i2 = 0;
        Object objM118constructorimpl;
        String id = ensureLinksSheet(c);
        Iterable $this$filter$iv = parseCsv(exportCsv(c, id));
        Collection destination$iv$iv = new ArrayList();
        Iterator it = $this$filter$iv.iterator();
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            Object element$iv$iv = it.next();
            Iterable r = (List) element$iv$iv;
            Iterable $this$any$iv = r;
            if (!($this$any$iv instanceof Collection) || !((Collection) $this$any$iv).isEmpty()) {
                Iterator it2 = $this$any$iv.iterator();
                while (true) {
                    if (!it2.hasNext()) {
                        break;
                    }
                    Object element$iv = it2.next();
                    String it3 = (String) element$iv;
                    if (!StringsKt.isBlank(it3)) {
                        i = 1;
                        break;
                    }
                }
            }
            if (i != 0) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        List rows = (List) destination$iv$iv;
        if (!rows.isEmpty()) {
            GmapsRoute gmapsRoute = GmapsRoute.INSTANCE;
            List list = (List) rows.get(0);
            i2 = !gmapsRoute.looksLikeLink((String) (1 < list.size() ? list.get(1) : "")) ? 1 : 0;
        }
        int start = i2;
        HashMap<String, String> linkMap = readLinkMap(dir);
        HashSet seen = new HashSet();
        int size = rows.size();
        int i3 = start;
        int added = 0;
        while (i3 < size) {
            List list2 = (List) rows.get(i3);
            String name = StringsKt.take(new Regex("[/\\\\:*?\"<>|]").replace(StringsKt.trim((CharSequence) ((list2.size() > 0 ? 1 : i) != 0 ? list2.get(i) : "")).toString(), "_"), 60);
            List list3 = (List) rows.get(i3);
            String id2 = id;
            String link = StringsKt.trim((CharSequence) (1 < list3.size() ? list3.get(1) : "")).toString();
            if (!(name.length() == 0) && GmapsRoute.INSTANCE.looksLikeLink(link)) {
                seen.add(name);
                File file = new File(dir, name + ".gpx");
                if (!file.exists() || !Intrinsics.areEqual(linkMap.get(name), link)) {
                    List listEmptyList = CollectionsKt.emptyList();
                    try {
                        objM118constructorimpl = GmapsRoute.INSTANCE.points(link);
                    } catch (Throwable th) {
                        objM118constructorimpl = listEmptyList;
                    }
                    List<double[]> list4 = (List) objM118constructorimpl;
                    if (list4.size() >= 2) {
                        FilesKt.writeText(file, GmapsRoute.INSTANCE.toGpx(name, list4), kotlin.text.Charsets.UTF_8);
                        linkMap.put(name, link);
                        added++;
                    }
                }
            }
            i3++;
            id = id2;
            i = 0;
        }
        Set<String> setKeySet = linkMap.keySet();
        Intrinsics.checkNotNullExpressionValue(setKeySet, "<get-keys>(...)");
        for (String n : CollectionsKt.toList(setKeySet)) {
            if (!seen.contains(n)) {
                new File(dir, n + ".gpx").delete();
                linkMap.remove(n);
            }
        }
        writeLinkMap(dir, linkMap);
        return added;
    }

    private final String exportCsv(Context c, String id) throws JSONException, IOException {
        String txt;
        String token = freshToken(c);
        Request req = new Request.Builder().url("https://www.googleapis.com/drive/v3/files/" + id + "/export?mimeType=text%2Fcsv").header("Authorization", "Bearer " + token).build();
        Response responseExecute = http.newCall(req).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody == null || (txt = responseBodyBody.string()) == null) {
                txt = "";
            }
            if (!resp.isSuccessful()) {
                throw new RuntimeException("HTTP " + resp.code() + ": " + txt);
            }
            CloseableKt.closeFinally(responseExecute, null);
            return txt;
        } finally {
        }
    }

    private final String ensureRoutesFolder(Context c, String token) throws JSONException, IOException {
        String strString;
        String name = Prefs.INSTANCE.driveRoutesFolder(c);
        String it = findFolderId(token, name);
        if (it != null) {
            return it;
        }
        JSONObject meta = new JSONObject().put("name", name).put("mimeType", "application/vnd.google-apps.folder");
        Request.Builder builderHeader = new Request.Builder().url("https://www.googleapis.com/drive/v3/files?fields=id").header("Authorization", "Bearer " + token);
        RequestBody.Companion companion = RequestBody.Companion;
        String string = meta.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        Request create = builderHeader.post(companion.create(string, MediaType.Companion.get("application/json; charset=UTF-8"))).build();
        Response responseExecute = http.newCall(create).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody == null || (strString = responseBodyBody.string()) == null) {
                strString = "{}";
            }
            String string2 = new JSONObject(strString).getString("id");
            Intrinsics.checkNotNullExpressionValue(string2, "getString(...)");
            CloseableKt.closeFinally(responseExecute, null);
            return string2;
        } finally {
        }
    }

    private final String findSheetInFolder(String token, String folder) throws IOException {
        String strString;
        String q = enc("'" + folder + "' in parents and mimeType='application/vnd.google-apps.spreadsheet' and trashed=false");
        Request req = new Request.Builder().url("https://www.googleapis.com/drive/v3/files?q=" + q + "&fields=files(id)").header("Authorization", "Bearer " + token).build();
        Response responseExecute = http.newCall(req).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody == null || (strString = responseBodyBody.string()) == null) {
                strString = "{}";
            }
            JSONArray files;
            String string;
            try {
                files = new JSONObject(strString).optJSONArray("files");
                string = (files == null || files.length() <= 0) ? null : files.getJSONObject(0).getString("id");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            CloseableKt.closeFinally(responseExecute, null);
            return string;
        } finally {
        }
    }

    private final HashMap<String, String> readLinkMap(File dir) {
        Object objM118constructorimpl;
        File f = new File(dir, ".link_sync.json");
        if (!f.exists()) {
            return new HashMap<>();
        }
        HashMap map = new HashMap();
        try {
            JSONObject o = new JSONObject(FilesKt.readText(f, kotlin.text.Charsets.UTF_8));
            HashMap m = new HashMap();
            Iterator<String> itKeys = o.keys();
            Intrinsics.checkNotNullExpressionValue(itKeys, "keys(...)");
            while (itKeys.hasNext()) {
                String k = itKeys.next();
                m.put(k, o.getString(k));
            }
            objM118constructorimpl = m;
        } catch (Throwable th) {
            objM118constructorimpl = map;
        }
        return (HashMap) objM118constructorimpl;
    }

    private final void writeLinkMap(File dir, Map<String, String> m) {
        try {
            File file = new File(dir, ".link_sync.json");
            Intrinsics.checkNotNull(m, "null cannot be cast to non-null type kotlin.collections.Map<*, *>");
            String string = new JSONObject(m).toString();
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
            FilesKt.writeText(file, string, kotlin.text.Charsets.UTF_8);
        } catch (Throwable th) {
        }
    }

    private final List<List<String>> parseCsv(String text) {
        ArrayList rows = new ArrayList();
        ArrayList row = new ArrayList();
        StringBuilder field = new StringBuilder();
        int i = 0;
        boolean inQ = false;
        while (true) {
            if (i < text.length()) {
                char ch = text.charAt(i);
                if (inQ) {
                    if (ch == '\"') {
                        if (i + 1 >= text.length() || text.charAt(i + 1) != '\"') {
                            inQ = false;
                        } else {
                            field.append(Typography.quote);
                            i++;
                        }
                    } else {
                        field.append(ch);
                    }
                } else {
                    switch (ch) {
                        case '\n':
                            row.add(field.toString());
                            field.setLength(0);
                            rows.add(row);
                            row = new ArrayList();
                            break;
                        case '\r':
                            break;
                        case '\"':
                            inQ = true;
                            break;
                        case ',':
                            row.add(field.toString());
                            field.setLength(0);
                            break;
                        default:
                            field.append(ch);
                            break;
                    }
                }
                i++;
            } else {
                if ((field.length() > 0) || !row.isEmpty()) {
                    row.add(field.toString());
                    rows.add(row);
                }
                return rows;
            }
        }
    }

    private final long now() {
        return System.currentTimeMillis() / ((long) 1000);
    }

    private final JSONObject post(String url, RequestBody body) throws IOException {
        String txt;
        Request req = new Request.Builder().url(url).post(body).build();
        Response responseExecute = http.newCall(req).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody == null || (txt = responseBodyBody.string()) == null) {
                txt = "";
            }
            if (!resp.isSuccessful()) {
                throw new RuntimeException("HTTP " + resp.code() + ": " + txt);
            }
            JSONObject jSONObject = new JSONObject(txt);
            CloseableKt.closeFinally(responseExecute, null);
            return jSONObject;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                CloseableKt.closeFinally(responseExecute, th);
                throw Sneaky.sneak(th2);
            }
        }
    }
}

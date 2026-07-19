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
import kotlin.Metadata;
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

/* JADX INFO: compiled from: GoogleDriveClient.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001d\u0010\n\u001a\n \u000b*\u0004\u0018\u00010\u00050\u00052\u0006\u0010\f\u001a\u00020\u0005H\u0002¢\u0006\u0002\u0010\rJ\u0016\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0005J\u001e\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u0005J\u0010\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0016\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u0019J\u0018\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u0005H\u0002J\u001a\u0010\u001c\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u001b\u001a\u00020\u00052\u0006\u0010\u001d\u001a\u00020\u0005H\u0002J\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001f2\u0006\u0010\u0013\u001a\u00020\u0014J\u0016\u0010!\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\"\u001a\u00020\u0005J\u0016\u0010#\u001a\u00020$2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u0005J\u0018\u0010&\u001a\u00020$2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010'\u001a\u00020\u0019H\u0002J\u000e\u0010(\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005J\u000e\u0010*\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0014J\u0018\u0010+\u001a\u00020$2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010'\u001a\u00020\u0019H\u0002J\u0018\u0010,\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010)\u001a\u00020\u0005H\u0002J\u0018\u0010-\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u0005H\u0002J\u001a\u0010.\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u001b\u001a\u00020\u00052\u0006\u0010/\u001a\u00020\u0005H\u0002J,\u00100\u001a\u001e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000501j\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005`22\u0006\u0010'\u001a\u00020\u0019H\u0002J$\u00103\u001a\u00020\u00122\u0006\u0010'\u001a\u00020\u00192\u0012\u00104\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000505H\u0002J\u001c\u00106\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u001f0\u001f2\u0006\u00107\u001a\u00020\u0005H\u0002J\b\u00108\u001a\u000209H\u0002J\u0018\u0010:\u001a\u00020;2\u0006\u0010<\u001a\u00020\u00052\u0006\u0010=\u001a\u00020>H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006?"}, d2 = {"Lcom/bike/computer/GoogleDriveClient;", "", "<init>", "()V", "SCOPE", "", "AUTH", "TOKEN", "http", "Lokhttp3/OkHttpClient;", "enc", "kotlin.jvm.PlatformType", "s", "(Ljava/lang/String;)Ljava/lang/String;", "authorizeUrl", "clientId", "redirect", "exchangeCode", "", "c", "Landroid/content/Context;", "code", "freshToken", "uploadGpx", "file", "Ljava/io/File;", "ensureFolder", "token", "findFolderId", "name", "listRoutes", "", "Lcom/bike/computer/DriveFile;", "download", "fileId", "syncRoutes", "", "localDir", "syncGpxFiles", "dir", "sheetUrl", "id", "ensureLinksSheet", "syncLinkSheet", "exportCsv", "ensureRoutesFolder", "findSheetInFolder", "folder", "readLinkMap", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "writeLinkMap", "m", "", "parseCsv", "text", "now", "", "post", "Lorg/json/JSONObject;", "url", "body", "Lokhttp3/RequestBody;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class GoogleDriveClient {
    private static final String AUTH = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String SCOPE = "https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/drive.readonly";
    private static final String TOKEN = "https://oauth2.googleapis.com/token";
    public static final GoogleDriveClient INSTANCE = new GoogleDriveClient();
    private static final OkHttpClient http = new OkHttpClient();

    private GoogleDriveClient() {
    }

    private final String enc(String s) {
        return URLEncoder.encode(s, "UTF-8");
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
        JSONObject jSONObjectPost = post(TOKEN, new FormBody.Builder(null, 1, 0 == true ? 1 : 0).add("client_id", Prefs.INSTANCE.driveClientId(c)).add("client_secret", Prefs.INSTANCE.driveClientSecret(c)).add("code", code).add("redirect_uri", redirect).add("grant_type", "authorization_code").build());
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
        JSONObject jSONObjectPost = post(TOKEN, new FormBody.Builder(null, 1, 0 == true ? 1 : 0).add("client_id", Prefs.INSTANCE.driveClientId(c)).add("client_secret", Prefs.INSTANCE.driveClientSecret(c)).add("grant_type", "refresh_token").add("refresh_token", Prefs.INSTANCE.driveRefreshToken(c)).build());
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
        MultipartBody.Builder type = new MultipartBody.Builder(null, 1, 0 == true ? 1 : 0).setType(MediaType.INSTANCE.get("multipart/related"));
        RequestBody.Companion companion = RequestBody.INSTANCE;
        String string = jSONObjectPut.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        Response responseExecute = http.newCall(new Request.Builder().url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart").header("Authorization", "Bearer " + strFreshToken).post(type.addPart(companion.create(string, MediaType.INSTANCE.get("application/json; charset=UTF-8"))).addPart(RequestBody.INSTANCE.create(file, MediaType.INSTANCE.get("application/gpx+xml"))).build()).build()).execute();
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
        RequestBody.Companion companion = RequestBody.INSTANCE;
        String string = meta.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        Request create = builderHeader.post(companion.create(string, MediaType.INSTANCE.get("application/json; charset=UTF-8"))).build();
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
        String safe = StringsKt.replace$default(StringsKt.replace$default(name, "\\", "\\\\", false, 4, (Object) null), "'", "\\'", false, 4, (Object) null);
        String q = enc("mimeType='application/vnd.google-apps.folder' and name='" + safe + "' and trashed=false");
        Request req = new Request.Builder().url("https://www.googleapis.com/drive/v3/files?q=" + q + "&fields=files(id)").header("Authorization", "Bearer " + token).build();
        Response responseExecute = http.newCall(req).execute();
        try {
            Response resp = responseExecute;
            ResponseBody responseBodyBody = resp.body();
            if (responseBodyBody == null || (strString = responseBodyBody.string()) == null) {
                strString = "{}";
            }
            JSONArray files = new JSONObject(strString).optJSONArray("files");
            String string = (files == null || files.length() <= 0) ? null : files.getJSONObject(0).getString("id");
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
        Throwable th;
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
                        ArrayList arrayList = (List) destination$iv$iv2;
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
            throw th6;
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

    public final int syncRoutes(Context c, String localDir) {
        Object objM118constructorimpl;
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(localDir, "localDir");
        File dir = new File(localDir);
        dir.mkdirs();
        try {
            Result.Companion companion = Result.INSTANCE;
            GoogleDriveClient $this$syncRoutes_u24lambda_u2410 = this;
            objM118constructorimpl = Result.m118constructorimpl(Integer.valueOf($this$syncRoutes_u24lambda_u2410.syncGpxFiles(c, dir)));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m124isFailureimpl(objM118constructorimpl)) {
            objM118constructorimpl = 0;
        }
        int added = 0 + ((Number) objM118constructorimpl).intValue();
        return added + syncLinkSheet(c, dir);
    }

    private final int syncGpxFiles(Context c, File dir) {
        Object objM118constructorimpl;
        int added = 0;
        for (DriveFile f : listRoutes(c)) {
            File local = new File(dir, f.getName());
            if (!local.exists()) {
                try {
                    Result.Companion companion = Result.INSTANCE;
                    GoogleDriveClient $this$syncGpxFiles_u24lambda_u2411 = this;
                    objM118constructorimpl = Result.m118constructorimpl($this$syncGpxFiles_u24lambda_u2411.download(c, f.getId()));
                } catch (Throwable th) {
                    Result.Companion companion2 = Result.INSTANCE;
                    objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
                }
                if (Result.m124isFailureimpl(objM118constructorimpl)) {
                    objM118constructorimpl = null;
                }
                String gpx = (String) objM118constructorimpl;
                if (gpx != null && GpxRoute.INSTANCE.parse(gpx).size() >= 2) {
                    FilesKt.writeText$default(local, gpx, null, 2, null);
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
        int i = 1;
        String str = null;
        Object[] objArr = 0;
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
        MultipartBody.Builder type = new MultipartBody.Builder(str, i, objArr == true ? 1 : 0).setType(MediaType.INSTANCE.get("multipart/related"));
        RequestBody.Companion companion = RequestBody.INSTANCE;
        String string = jSONObjectPut.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        Response responseExecute = http.newCall(new Request.Builder().url("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart&fields=id").header("Authorization", "Bearer " + strFreshToken).post(type.addPart(companion.create(string, MediaType.INSTANCE.get("application/json; charset=UTF-8"))).addPart(RequestBody.INSTANCE.create("Name,Google Maps Link\n", MediaType.INSTANCE.get("text/csv"))).build()).build()).execute();
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
        int i2;
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
                    try {
                        Result.Companion companion = Result.INSTANCE;
                        GoogleDriveClient googleDriveClient = this;
                        objM118constructorimpl = Result.m118constructorimpl(GmapsRoute.INSTANCE.points(link));
                    } catch (Throwable th) {
                        Result.Companion companion2 = Result.INSTANCE;
                        objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
                    }
                    List listEmptyList = CollectionsKt.emptyList();
                    if (Result.m124isFailureimpl(objM118constructorimpl)) {
                        objM118constructorimpl = listEmptyList;
                    }
                    List<double[]> list4 = (List) objM118constructorimpl;
                    if (list4.size() >= 2) {
                        FilesKt.writeText$default(file, GmapsRoute.INSTANCE.toGpx(name, list4), null, 2, null);
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
        RequestBody.Companion companion = RequestBody.INSTANCE;
        String string = meta.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        Request create = builderHeader.post(companion.create(string, MediaType.INSTANCE.get("application/json; charset=UTF-8"))).build();
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
            JSONArray files = new JSONObject(strString).optJSONArray("files");
            String string = (files == null || files.length() <= 0) ? null : files.getJSONObject(0).getString("id");
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
        try {
            Result.Companion companion = Result.INSTANCE;
            GoogleDriveClient googleDriveClient = this;
            JSONObject o = new JSONObject(FilesKt.readText$default(f, null, 1, null));
            HashMap m = new HashMap();
            Iterator<String> itKeys = o.keys();
            Intrinsics.checkNotNullExpressionValue(itKeys, "keys(...)");
            while (itKeys.hasNext()) {
                String k = itKeys.next();
                m.put(k, o.getString(k));
            }
            objM118constructorimpl = Result.m118constructorimpl(m);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        HashMap map = new HashMap();
        if (Result.m124isFailureimpl(objM118constructorimpl)) {
            objM118constructorimpl = map;
        }
        return (HashMap) objM118constructorimpl;
    }

    private final void writeLinkMap(File dir, Map<String, String> m) {
        try {
            Result.Companion companion = Result.INSTANCE;
            GoogleDriveClient googleDriveClient = this;
            File file = new File(dir, ".link_sync.json");
            Intrinsics.checkNotNull(m, "null cannot be cast to non-null type kotlin.collections.Map<*, *>");
            String string = new JSONObject(m).toString();
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
            FilesKt.writeText$default(file, string, null, 2, null);
            Result.m118constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            Result.m118constructorimpl(ResultKt.createFailure(th));
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
                throw th2;
            }
        }
    }
}

package com.bike.computer;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPInputStream;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.collections.CollectionsKt;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/* JADX INFO: compiled from: TileServer.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0016J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u0015H\u0002J\u0010\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0013H\u0002J\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0019\u001a\u00020\u001aH\u0002R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0003X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b"}, d2 = {"Lcom/bike/computer/LocalTiles;", "Lokhttp3/Interceptor;", "mbtilesPath", "", "fontsDir", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "TAG", "PBF", "Lokhttp3/MediaType;", "served", "", "db", "Landroid/database/sqlite/SQLiteDatabase;", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "tile", "", "segs", "", "gunzip", "b", "font", "url", "Lokhttp3/HttpUrl;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class LocalTiles implements Interceptor {
    private final MediaType PBF;
    private final String TAG;
    private final SQLiteDatabase db;
    private final String fontsDir;
    private int served;

    public LocalTiles(String mbtilesPath, String fontsDir) {
        Intrinsics.checkNotNullParameter(mbtilesPath, "mbtilesPath");
        Intrinsics.checkNotNullParameter(fontsDir, "fontsDir");
        this.fontsDir = fontsDir;
        this.TAG = "BikeTiles";
        this.PBF = MediaType.get("application/x-protobuf");
        SQLiteDatabase sQLiteDatabaseOpenDatabase = SQLiteDatabase.openDatabase(mbtilesPath, null, 1);
        Intrinsics.checkNotNullExpressionValue(sQLiteDatabaseOpenDatabase, "openDatabase(...)");
        this.db = sQLiteDatabaseOpenDatabase;
    }

    @Override // okhttp3.Interceptor
    public Response intercept(Interceptor.Chain chain) throws java.io.IOException {
        Intrinsics.checkNotNullParameter(chain, "chain");
        Request req = chain.request();
        if (!Intrinsics.areEqual(req.url().host(), "bike.local")) {
            return chain.proceed(req);
        }
        List<String> listPathSegments = req.url().pathSegments();
        byte[] body = null;
        try {
            String str = (String) CollectionsKt.firstOrNull((List) listPathSegments);
            if (Intrinsics.areEqual(str, "tiles")) {
                body = tile(listPathSegments);
            } else if (Intrinsics.areEqual(str, "fonts")) {
                body = font(req.url());
            }
        } catch (Exception e) {
            Log.e(this.TAG, "intercept " + req.url().encodedPath() + " failed", e);
        }
        int i = this.served;
        this.served = i + 1;
        if (i < 6) {
            Log.i(this.TAG, "serve " + req.url().encodedPath() + " -> " + (body != null ? body.length : -1) + " bytes");
        }
        return new Response.Builder().request(req).protocol(Protocol.HTTP_1_1).code(body != null ? ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION : 404).message(body != null ? "OK" : "Not Found").body(ResponseBody.create(body == null ? new byte[0] : body, this.PBF)).build();
    }

    private final byte[] tile(List<String> segs) {
        int z = Integer.parseInt(segs.get(1));
        int x = Integer.parseInt(segs.get(2));
        int y = Integer.parseInt(StringsKt.removeSuffix(segs.get(3), (CharSequence) ".pbf"));
        int tmsY = ((1 << z) - 1) - y;
        Cursor cursorRawQuery = this.db.rawQuery("SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?", new String[]{String.valueOf(z), String.valueOf(x), String.valueOf(tmsY)});
        try {
            Cursor c = cursorRawQuery;
            if (!c.moveToFirst()) {
                CloseableKt.closeFinally(cursorRawQuery, null);
                return null;
            }
            byte[] blob = c.getBlob(0);
            Intrinsics.checkNotNullExpressionValue(blob, "getBlob(...)");
            byte[] bArrGunzip = gunzip(blob);
            CloseableKt.closeFinally(cursorRawQuery, null);
            return bArrGunzip;
        } finally {
        }
    }

    private final byte[] gunzip(byte[] b) {
        if (b.length < 2 || (b[0] & UByte.MAX_VALUE) != 31 || (b[1] & UByte.MAX_VALUE) != 139) {
            return b;
        }
        try {
            GZIPInputStream gZIPInputStream = new GZIPInputStream(new ByteArrayInputStream(b));
            GZIPInputStream g = gZIPInputStream;
            ByteArrayOutputStream out = new ByteArrayOutputStream(b.length * 5);
            ByteStreamsKt.copyTo(g, out, 8192);
            byte[] byteArray = out.toByteArray();
            Intrinsics.checkNotNullExpressionValue(byteArray, "toByteArray(...)");
            CloseableKt.closeFinally(gZIPInputStream, null);
            return byteArray;
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final byte[] font(HttpUrl url) {
        String stack = url.pathSegments().get(1);
        String range = url.pathSegments().get(2);
        Iterable $this$map$iv = StringsKt.split((CharSequence) stack, new String[]{","}, false, 0);
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            String it = (String) item$iv$iv;
            destination$iv$iv.add(StringsKt.trim((CharSequence) it).toString());
        }
        List<String> candidates = CollectionsKt.plus(destination$iv$iv, (Iterable) CollectionsKt.listOf((Object[]) new String[]{"KlokanTech Noto Sans Regular", "KlokanTech Noto Sans Bold"}));
        for (String name : candidates) {
            File f = new File(new File(this.fontsDir, name), range);
            if (f.exists()) {
                return FilesKt.readBytes(f);
            }
        }
        return null;
    }
}

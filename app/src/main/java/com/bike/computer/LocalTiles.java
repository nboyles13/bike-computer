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

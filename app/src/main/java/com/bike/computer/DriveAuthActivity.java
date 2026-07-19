package com.bike.computer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import androidx.core.view.ViewCompat;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: DriveAuthActivity.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0006\u001a\u00020\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0014J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005H\u0002J\u0010\u0010\r\u001a\u00020\u00072\u0006\u0010\u000e\u001a\u00020\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lcom/bike/computer/DriveAuthActivity;", "Landroid/app/Activity;", "<init>", "()V", "redirect", "", "onCreate", "", "s", "Landroid/os/Bundle;", "handle", "", "url", "toast", "m", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class DriveAuthActivity extends Activity {
    private final String redirect = "http://127.0.0.1:8080";

    @Override // android.app.Activity
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        String cid = Prefs.INSTANCE.driveClientId(this);
        if (cid.length() == 0) {
            toast("Enter Client ID & Secret first");
            finish();
            return;
        }
        WebView web = new WebView(this);
        web.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        WebSettings $this$onCreate_u24lambda_u240 = web.getSettings();
        $this$onCreate_u24lambda_u240.setJavaScriptEnabled(true);
        $this$onCreate_u24lambda_u240.setDomStorageEnabled(true);
        $this$onCreate_u24lambda_u240.setUserAgentString("Mozilla/5.0 (X11; Linux x86_64; rv:128.0) Gecko/20100101 Firefox/128.0");
        web.setWebViewClient(new WebViewClient() { // from class: com.bike.computer.DriveAuthActivity.onCreate.2
            @Override // android.webkit.WebViewClient
            public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                Intrinsics.checkNotNullParameter(v, "v");
                Intrinsics.checkNotNullParameter(r, "r");
                DriveAuthActivity driveAuthActivity = DriveAuthActivity.this;
                String string = r.getUrl().toString();
                Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
                return driveAuthActivity.handle(string);
            }

            @Override // android.webkit.WebViewClient
            @Deprecated(message = "older devices")
            public boolean shouldOverrideUrlLoading(WebView v, String u) {
                Intrinsics.checkNotNullParameter(v, "v");
                Intrinsics.checkNotNullParameter(u, "u");
                return DriveAuthActivity.this.handle(u);
            }
        });
        setContentView(web);
        web.loadUrl(GoogleDriveClient.INSTANCE.authorizeUrl(cid, this.redirect));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean handle(String url) {
        if (!StringsKt.startsWith$default(url, this.redirect, false, 2, (Object) null)) {
            return false;
        }
        Uri uri = Uri.parse(url);
        final String code = uri.getQueryParameter("code");
        String err = uri.getQueryParameter("error");
        if (code == null) {
            toast("Authorization " + (err == null ? "cancelled" : err));
            finish();
            return true;
        }
        new Thread(new Runnable() { // from class: com.bike.computer.DriveAuthActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                DriveAuthActivity.handle$lambda$5(this.f$0, code);
            }
        }).start();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void handle$lambda$5(final DriveAuthActivity this$0, String $code) {
        final Object r;
        try {
            Result.Companion companion = Result.INSTANCE;
            GoogleDriveClient.INSTANCE.exchangeCode(this$0, $code, this$0.redirect);
            r = Result.m118constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            r = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.DriveAuthActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DriveAuthActivity.handle$lambda$5$lambda$4(r, this$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void handle$lambda$5$lambda$4(Object $r, DriveAuthActivity this$0) {
        if (Result.m125isSuccessimpl($r)) {
            this$0.toast("Connected to Google Drive");
            this$0.setResult(-1);
            this$0.finish();
        }
        Throwable it = Result.m121exceptionOrNullimpl($r);
        if (it != null) {
            this$0.toast("Connect failed: " + it.getMessage());
            this$0.finish();
        }
    }

    private final void toast(String m) {
        Toast.makeText(this, m, 1).show();
    }
}

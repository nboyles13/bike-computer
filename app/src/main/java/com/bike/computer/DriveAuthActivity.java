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
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

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
        if (!StringsKt.startsWith(url, this.redirect, false)) {
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
                DriveAuthActivity.handle$lambda$5(DriveAuthActivity.this, code);
            }
        }).start();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void handle$lambda$5(final DriveAuthActivity this$0, String $code) {
        Object r0;
        try {
            GoogleDriveClient.INSTANCE.exchangeCode(this$0, $code, this$0.redirect);
            r0 = Unit.INSTANCE;
        } catch (Throwable th) {
            r0 = th;
        }
        final Object r = r0;
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.DriveAuthActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DriveAuthActivity.handle$lambda$5$lambda$4(r, this$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void handle$lambda$5$lambda$4(Object $r, DriveAuthActivity this$0) {
        if (!($r instanceof Throwable)) {
            this$0.toast("Connected to Google Drive");
            this$0.setResult(-1);
            this$0.finish();
        }
        Throwable it = ($r instanceof Throwable) ? (Throwable) $r : null;
        if (it != null) {
            this$0.toast("Connect failed: " + it.getMessage());
            this$0.finish();
        }
    }

    private final void toast(String m) {
        Toast.makeText(this, m, 1).show();
    }
}

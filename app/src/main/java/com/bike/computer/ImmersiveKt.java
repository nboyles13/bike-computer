package com.bike.computer;

import android.app.Activity;
import android.os.Build;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Immersive.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\f\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\u001a\n\u0010\u0000\u001a\u00020\u0001*\u00020\u0002¨\u0006\u0003"}, d2 = {"enterImmersive", "", "Landroid/app/Activity;", "app_debug"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class ImmersiveKt {
    public static final void enterImmersive(Activity $this$enterImmersive) {
        Intrinsics.checkNotNullParameter($this$enterImmersive, "<this>");
        if (Build.VERSION.SDK_INT >= 30) {
            $this$enterImmersive.getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController $this$enterImmersive_u24lambda_u240 = $this$enterImmersive.getWindow().getInsetsController();
            if ($this$enterImmersive_u24lambda_u240 != null) {
                $this$enterImmersive_u24lambda_u240.hide(WindowInsets.Type.systemBars());
                $this$enterImmersive_u24lambda_u240.setSystemBarsBehavior(2);
            }
        }
    }
}

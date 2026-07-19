package com.bike.computer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.bike.computer.RideService;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: WelcomeActivity.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001f\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u0012\u0010\b\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0016¨\u0006\t"}, d2 = {"com/bike/computer/WelcomeActivity$conn$1", "Landroid/content/ServiceConnection;", "onServiceConnected", "", "n", "Landroid/content/ComponentName;", "b", "Landroid/os/IBinder;", "onServiceDisconnected", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class WelcomeActivity$conn$1 implements ServiceConnection {
    final /* synthetic */ WelcomeActivity this$0;

    WelcomeActivity$conn$1(WelcomeActivity $receiver) {
        this.this$0 = $receiver;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0034  */
    /* JADX WARN: Removed duplicated region for block: B:13:0x0037  */
    @Override // android.content.ServiceConnection
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onServiceConnected(ComponentName n, IBinder b) {
        boolean z;
        RideRecorder recorder;
        WelcomeActivity welcomeActivity = this.this$0;
        Intrinsics.checkNotNull(b, "null cannot be cast to non-null type com.bike.computer.RideService.LocalBinder");
        welcomeActivity.ride = ((RideService.LocalBinder) b).getThis$0();
        if (Prefs.INSTANCE.wifiDisabledByApp(this.this$0)) {
            RideService rideService = this.this$0.ride;
            if (rideService == null || (recorder = rideService.getRecorder()) == null) {
                z = false;
                if (!z) {
                    Prefs.INSTANCE.setWifiDisabledByApp(this.this$0, false);
                    new Thread(new Runnable() { // from class: com.bike.computer.WelcomeActivity$conn$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            WelcomeActivity$conn$1.onServiceConnected$lambda$0();
                        }
                    }).start();
                }
            } else {
                z = true;
                if (!recorder.isRecording()) {
                }
                if (!z) {
                }
            }
        }
        this.this$0.refreshStatus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onServiceConnected$lambda$0() {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "svc wifi enable"}).waitFor();
        } catch (Exception e) {
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName n) {
        this.this$0.ride = null;
    }
}

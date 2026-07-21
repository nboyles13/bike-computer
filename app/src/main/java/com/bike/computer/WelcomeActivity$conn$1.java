package com.bike.computer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.bike.computer.RideService;
import kotlin.jvm.internal.Intrinsics;

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

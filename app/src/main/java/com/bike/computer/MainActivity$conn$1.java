package com.bike.computer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.bike.computer.RideService;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

public final class MainActivity$conn$1 implements ServiceConnection {
    final /* synthetic */ MainActivity this$0;

    MainActivity$conn$1(MainActivity $receiver) {
        this.this$0 = $receiver;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        MainActivity mainActivity = this.this$0;
        Intrinsics.checkNotNull(service, "null cannot be cast to non-null type com.bike.computer.RideService.LocalBinder");
        mainActivity.ride = ((RideService.LocalBinder) service).getService();
        RideService rideService = this.this$0.ride;
        if (rideService != null) {
            final MainActivity mainActivity2 = this.this$0;
            rideService.setOnUpdate(new Function0() { // from class: com.bike.computer.MainActivity$conn$1$$ExternalSyntheticLambda1
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return MainActivity$conn$1.onServiceConnected$lambda$1(mainActivity2);
                }
            });
        }
        RideService rideService2 = this.this$0.ride;
        if (rideService2 != null) {
            rideService2.setAutoPauseEnabled(Prefs.INSTANCE.autoPause(this.this$0));
        }
        this.this$0.updateRecUi();
        this.this$0.enableLocationDot();
        this.this$0.onRideUpdate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit onServiceConnected$lambda$1(final MainActivity this$0) {
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.MainActivity$conn$1$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this$0.onRideUpdate();
            }
        });
        return Unit.INSTANCE;
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        this.this$0.ride = null;
    }
}

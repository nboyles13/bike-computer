package com.bike.computer;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.bike.computer.RideService;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: MainActivity.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u001f\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u0012\u0010\b\u001a\u00020\u00032\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0016¨\u0006\t"}, d2 = {"com/bike/computer/MainActivity$conn$1", "Landroid/content/ServiceConnection;", "onServiceConnected", "", "name", "Landroid/content/ComponentName;", NotificationCompat.CATEGORY_SERVICE, "Landroid/os/IBinder;", "onServiceDisconnected", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class MainActivity$conn$1 implements ServiceConnection {
    final /* synthetic */ MainActivity this$0;

    MainActivity$conn$1(MainActivity $receiver) {
        this.this$0 = $receiver;
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName name, IBinder service) {
        MainActivity mainActivity = this.this$0;
        Intrinsics.checkNotNull(service, "null cannot be cast to non-null type com.bike.computer.RideService.LocalBinder");
        mainActivity.ride = ((RideService.LocalBinder) service).getThis$0();
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
                MainActivity.access$onRideUpdate(this$0);
            }
        });
        return Unit.INSTANCE;
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName name) {
        this.this$0.ride = null;
    }
}

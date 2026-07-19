package com.bike.computer;

import android.util.Log;
import java.io.DataOutputStream;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;
import org.maplibre.android.constants.MapLibreConstants;

/* JADX INFO: compiled from: LedController.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\f\u001a\u00020\rH\u0002J\u001e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000b2\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000bJ\u0006\u0010\u0013\u001a\u00020\u000fJ\u0006\u0010\u0014\u001a\u00020\u000fR\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082D¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"Lcom/bike/computer/LedController;", "", "<init>", "()V", "su", "Ljava/lang/Process;", "out", "Ljava/io/DataOutputStream;", "lastCmd", "", "bright", "", "ensure", "", "set", "", "r", "g", "b", DebugKt.DEBUG_PROPERTY_VALUE_OFF, "close", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class LedController {
    private DataOutputStream out;
    private Process su;
    private String lastCmd = "";
    private final int bright = MapLibreConstants.ANIMATION_DURATION;

    private final boolean ensure() {
        if (this.out != null) {
            return true;
        }
        try {
            Process p = Runtime.getRuntime().exec("su");
            this.su = p;
            this.out = new DataOutputStream(p.getOutputStream());
            return true;
        } catch (Exception e) {
            Log.e("LED", "su unavailable: " + e);
            return false;
        }
    }

    public final synchronized void set(int r, int g, int b) {
        if (ensure()) {
            int lr = (this.bright * r) / 255;
            int lg = (this.bright * g) / 255;
            int lb = (this.bright * b) / 255;
            String cmd = "echo " + lr + " > /sys/class/leds/red/brightness;echo " + lg + " > /sys/class/leds/green/brightness;echo " + lb + " > /sys/class/leds/blue/brightness\n";
            if (Intrinsics.areEqual(cmd, this.lastCmd)) {
                return;
            }
            this.lastCmd = cmd;
            try {
                DataOutputStream dataOutputStream = this.out;
                if (dataOutputStream != null) {
                    dataOutputStream.writeBytes(cmd);
                }
                DataOutputStream dataOutputStream2 = this.out;
                if (dataOutputStream2 != null) {
                    dataOutputStream2.flush();
                }
            } catch (Exception e) {
                Log.e("LED", "write failed: " + e);
                close();
            }
        }
    }

    public final synchronized void off() {
        set(0, 0, 0);
    }

    public final synchronized void close() {
        try {
            DataOutputStream dataOutputStream = this.out;
            if (dataOutputStream != null) {
                dataOutputStream.writeBytes("echo 0 > /sys/class/leds/red/brightness;echo 0 > /sys/class/leds/green/brightness;echo 0 > /sys/class/leds/blue/brightness\nexit\n");
            }
            DataOutputStream dataOutputStream2 = this.out;
            if (dataOutputStream2 != null) {
                dataOutputStream2.flush();
            }
        } catch (Exception e) {
        }
        try {
            Process process = this.su;
            if (process != null) {
                process.destroy();
            }
        } catch (Exception e2) {
        }
        this.out = null;
        this.su = null;
        this.lastCmd = "";
    }
}

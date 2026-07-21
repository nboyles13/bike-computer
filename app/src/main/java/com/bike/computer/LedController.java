package com.bike.computer;

import android.util.Log;
import java.io.DataOutputStream;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;
import org.maplibre.android.constants.MapLibreConstants;

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

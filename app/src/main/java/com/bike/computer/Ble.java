package com.bike.computer;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Runtime-permission guards for BLE. On Android 12+ (API 31) scanning and
 * connecting require the BLUETOOTH_SCAN / BLUETOOTH_CONNECT runtime permissions;
 * calling into the BLE APIs without them throws SecurityException. These helpers
 * let the sensor code degrade to an idle "no permission" state instead of
 * crashing when the app hasn't been granted them yet (e.g. a fresh install).
 */
final class Ble {
    private Ble() {
    }

    static boolean canScan(Context c) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                || c.checkSelfPermission("android.permission.BLUETOOTH_SCAN") == PackageManager.PERMISSION_GRANTED;
    }

    static boolean canConnect(Context c) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                || c.checkSelfPermission("android.permission.BLUETOOTH_CONNECT") == PackageManager.PERMISSION_GRANTED;
    }
}

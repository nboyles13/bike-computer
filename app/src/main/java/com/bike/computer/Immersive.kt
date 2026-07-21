package com.bike.computer

import android.app.Activity
import android.os.Build
import android.view.WindowInsets

fun Activity.enterImmersive() {
    if (Build.VERSION.SDK_INT >= 30) {
        window.setDecorFitsSystemWindows(false)
        window.insetsController?.let {
            it.hide(WindowInsets.Type.systemBars())
            it.setSystemBarsBehavior(2)
        }
    }
}

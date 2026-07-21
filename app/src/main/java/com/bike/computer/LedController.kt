package com.bike.computer

import android.util.Log
import java.io.DataOutputStream

/** Drives the phone's notification RGB LED via root (`su`) sysfs writes. */
class LedController {
    private var out: DataOutputStream? = null
    private var su: Process? = null
    private var lastCmd = ""
    private val bright = 300

    private fun ensure(): Boolean {
        if (out != null) return true
        return try {
            val p = Runtime.getRuntime().exec("su")
            su = p
            out = DataOutputStream(p.outputStream)
            true
        } catch (e: Exception) {
            Log.e("LED", "su unavailable: $e")
            false
        }
    }

    @Synchronized
    fun set(r: Int, g: Int, b: Int) {
        if (!ensure()) return
        val lr = bright * r / 255
        val lg = bright * g / 255
        val lb = bright * b / 255
        val cmd = "echo $lr > /sys/class/leds/red/brightness;echo $lg > /sys/class/leds/green/brightness;echo $lb > /sys/class/leds/blue/brightness\n"
        if (cmd == lastCmd) return
        lastCmd = cmd
        try {
            out?.writeBytes(cmd)
            out?.flush()
        } catch (e: Exception) {
            Log.e("LED", "write failed: $e")
            close()
        }
    }

    @Synchronized
    fun off() = set(0, 0, 0)

    @Synchronized
    fun close() {
        try {
            out?.writeBytes("echo 0 > /sys/class/leds/red/brightness;echo 0 > /sys/class/leds/green/brightness;echo 0 > /sys/class/leds/blue/brightness\nexit\n")
            out?.flush()
        } catch (e: Exception) {
        }
        try {
            su?.destroy()
        } catch (e: Exception) {
        }
        out = null
        su = null
        lastCmd = ""
    }
}

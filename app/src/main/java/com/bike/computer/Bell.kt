package com.bike.computer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack

/** Synthesized bicycle-bell tone, played on the alarm stream at max volume. */
object Bell {
    private const val SR = 44100
    @Volatile
    private var pcm: ShortArray? = null

    fun prewarm() {
        if (pcm == null) {
            Thread { pcm = synth() }.start()
        }
    }

    fun ring(context: Context) {
        val am = context.applicationContext.getSystemService("audio") as? AudioManager
        Thread {
            var prevVol = -1
            if (am != null) {
                try {
                    prevVol = am.getStreamVolume(4)
                    try {
                        am.setStreamVolume(4, am.getStreamMaxVolume(4), 0)
                    } catch (t: Throwable) {
                    }
                } catch (t: Throwable) {
                    if (prevVol >= 0) {
                        try {
                            am.setStreamVolume(4, prevVol, 0)
                        } catch (t2: Throwable) {
                        }
                    }
                    return@Thread
                }
            }

            var buf = pcm
            if (buf == null) {
                buf = synth()
                pcm = buf
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder().setUsage(4).setContentType(4).build(),
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SR)
                        .setEncoding(2)
                        .setChannelMask(4)
                        .build(),
                )
                .setBufferSizeInBytes(buf.size * 2)
                .setTransferMode(0)
                .build()
            track.write(buf, 0, buf.size)
            try {
                track.setVolume(AudioTrack.getMaxVolume())
            } catch (t: Throwable) {
            }
            track.play()
            try {
                Thread.sleep(buf.size.toLong() * 1000 / SR + 300L)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            try {
                track.stop()
            } catch (t: Throwable) {
            }
            track.release()

            if (am != null && prevVol >= 0) {
                try {
                    am.setStreamVolume(4, prevVol, 0)
                } catch (t: Throwable) {
                }
            }
        }.start()
    }

    private fun synth(): ShortArray {
        val n = (2.8 * SR).toInt()
        val buf = DoubleArray(n)
        val freq = doubleArrayOf(3000.0, 3009.0, 6000.0, 9000.0)
        val amp = doubleArrayOf(1.0, 0.8, 0.5, 0.22)
        val tau = doubleArrayOf(1.9, 1.8, 0.55, 0.3)
        for (i in 0 until n) {
            val t = i.toDouble() / SR
            var v = 0.0
            for (p in freq.indices) {
                v += amp[p] * Math.exp(-t / tau[p]) * Math.sin(freq[p] * 6.283185307179586 * t)
            }
            val attack = Math.min(1.0, t / 0.002)
            buf[i] = v * attack
        }
        var peak = 0.0
        for (v in buf) {
            if (Math.abs(v) > peak) peak = Math.abs(v)
        }
        val g = if (peak > 0.0) 0.98 / peak else 1.0
        val out = ShortArray(n)
        for (i in 0 until n) {
            out[i] = (buf[i] * g * 32767).toInt().coerceIn(-32768, 32767).toShort()
        }
        return out
    }
}

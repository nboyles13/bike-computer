package com.bike.computer

import android.content.Context
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import java.util.Locale

object Voice {
    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var tone: ToneGenerator? = null
    var enabled = true

    fun init(ctx: Context) {
        if (tone == null) {
            try {
                tone = ToneGenerator(3, 100)
            } catch (e: Exception) {
            }
        }
        if (tts == null) {
            tts = TextToSpeech(ctx.applicationContext) { status ->
                ttsReady = status == 0
                if (ttsReady) {
                    try {
                        tts?.setLanguage(Locale.US)
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    fun cue(text: String) {
        beep()
        if (enabled && ttsReady) {
            try {
                tts?.speak(text, 0, null, "nav")
            } catch (e: Exception) {
            }
        }
    }

    fun beep() {
        if (enabled) {
            try {
                tone?.startTone(24, 200)
            } catch (e: Exception) {
            }
        }
    }

    fun shutdown() {
        try {
            tts?.shutdown()
        } catch (e: Exception) {
        }
        tts = null
        ttsReady = false
    }
}

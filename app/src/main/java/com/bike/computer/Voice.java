package com.bike.computer;

import android.content.Context;
import android.media.ToneGenerator;
import android.speech.tts.TextToSpeech;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Voice.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u000e\u0010\u0013\u001a\u00020\u00102\u0006\u0010\u0014\u001a\u00020\u0015J\u0006\u0010\u0016\u001a\u00020\u0010J\u0006\u0010\u0017\u001a\u00020\u0010R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e¨\u0006\u0018"}, d2 = {"Lcom/bike/computer/Voice;", "", "<init>", "()V", "tts", "Landroid/speech/tts/TextToSpeech;", "ttsReady", "", "tone", "Landroid/media/ToneGenerator;", "enabled", "getEnabled", "()Z", "setEnabled", "(Z)V", "init", "", "ctx", "Landroid/content/Context;", "cue", "text", "", "beep", "shutdown", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class Voice {
    public static final Voice INSTANCE = new Voice();
    private static boolean enabled = true;
    private static ToneGenerator tone;
    private static TextToSpeech tts;
    private static boolean ttsReady;

    private Voice() {
    }

    public final boolean getEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean z) {
        enabled = z;
    }

    public final void init(Context ctx) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        if (tone == null) {
            try {
                tone = new ToneGenerator(3, 100);
            } catch (Exception e) {
            }
        }
        if (tts == null) {
            tts = new TextToSpeech(ctx.getApplicationContext(), new TextToSpeech.OnInitListener() { // from class: com.bike.computer.Voice$$ExternalSyntheticLambda0
                @Override // android.speech.tts.TextToSpeech.OnInitListener
                public final void onInit(int i) {
                    Voice.init$lambda$0(i);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void init$lambda$0(int status) {
        ttsReady = status == 0;
        if (ttsReady) {
            try {
                TextToSpeech textToSpeech = tts;
                if (textToSpeech != null) {
                    textToSpeech.setLanguage(Locale.US);
                }
            } catch (Exception e) {
            }
        }
    }

    public final void cue(String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        beep();
        if (enabled && ttsReady) {
            try {
                TextToSpeech textToSpeech = tts;
                if (textToSpeech != null) {
                    textToSpeech.speak(text, 0, null, "nav");
                }
            } catch (Exception e) {
            }
        }
    }

    public final void beep() {
        if (enabled) {
            try {
                ToneGenerator toneGenerator = tone;
                if (toneGenerator != null) {
                    toneGenerator.startTone(24, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                }
            } catch (Exception e) {
            }
        }
    }

    public final void shutdown() {
        try {
            TextToSpeech textToSpeech = tts;
            if (textToSpeech != null) {
                textToSpeech.shutdown();
            }
        } catch (Exception e) {
        }
        tts = null;
        ttsReady = false;
    }
}

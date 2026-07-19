package com.bike.computer;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import btools.mapcreator.HgtReader;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import org.maplibre.android.constants.MapLibreConstants;

/* JADX INFO: compiled from: Bell.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0017\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\fJ\b\u0010\r\u001a\u00020\u0007H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lcom/bike/computer/Bell;", "", "<init>", "()V", "SR", "", "pcm", "", "prewarm", "", "ring", "context", "Landroid/content/Context;", "synth", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class Bell {
    public static final Bell INSTANCE = new Bell();
    private static final int SR = 44100;
    private static volatile short[] pcm;

    private Bell() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void prewarm$lambda$0() {
        pcm = INSTANCE.synth();
    }

    public final void prewarm() {
        if (pcm == null) {
            new Thread(new Runnable() { // from class: com.bike.computer.Bell$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Bell.prewarm$lambda$0();
                }
            }).start();
        }
    }

    public final void ring(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        Object systemService = context.getApplicationContext().getSystemService("audio");
        final AudioManager am = systemService instanceof AudioManager ? (AudioManager) systemService : null;
        new Thread(new Runnable() { // from class: com.bike.computer.Bell$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Bell.ring$lambda$6(am);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void ring$lambda$6(AudioManager $am) {
        int prevVol = -1;
        if ($am != null) {
            try {
                prevVol = $am.getStreamVolume(4);
                Bell bell = INSTANCE;
                try {
                    Result.Companion companion = Result.INSTANCE;
                    $am.setStreamVolume(4, $am.getStreamMaxVolume(4), 0);
                    Result.m118constructorimpl(Unit.INSTANCE);
                } catch (Throwable th) {
                    Result.Companion companion2 = Result.INSTANCE;
                    Result.m118constructorimpl(ResultKt.createFailure(th));
                }
            } catch (Throwable th2) {
                if ($am == null || prevVol < 0) {
                    return;
                }
                Bell bell2 = INSTANCE;
                try {
                    Result.Companion companion3 = Result.INSTANCE;
                    $am.setStreamVolume(4, prevVol, 0);
                    Result.m118constructorimpl(Unit.INSTANCE);
                    return;
                } catch (Throwable th3) {
                    th = th3;
                    Result.Companion companion4 = Result.INSTANCE;
                    Result.m118constructorimpl(ResultKt.createFailure(th));
                }
            }
        }
        short[] it = pcm;
        if (it == null) {
            it = INSTANCE.synth();
            pcm = it;
        }
        AudioTrack track = new AudioTrack.Builder().setAudioAttributes(new AudioAttributes.Builder().setUsage(4).setContentType(4).build()).setAudioFormat(new AudioFormat.Builder().setSampleRate(SR).setEncoding(2).setChannelMask(4).build()).setBufferSizeInBytes(it.length * 2).setTransferMode(0).build();
        Intrinsics.checkNotNullExpressionValue(track, "build(...)");
        track.write(it, 0, it.length);
        Bell bell3 = INSTANCE;
        try {
            Result.Companion companion5 = Result.INSTANCE;
            Result.m118constructorimpl(Integer.valueOf(track.setVolume(AudioTrack.getMaxVolume())));
        } catch (Throwable th4) {
            Result.Companion companion6 = Result.INSTANCE;
            Result.m118constructorimpl(ResultKt.createFailure(th4));
        }
        track.play();
        Thread.sleep(((((long) it.length) * 1000) / ((long) SR)) + ((long) MapLibreConstants.ANIMATION_DURATION));
        Bell bell4 = INSTANCE;
        try {
            Result.Companion companion7 = Result.INSTANCE;
            track.stop();
            Result.m118constructorimpl(Unit.INSTANCE);
        } catch (Throwable th5) {
            Result.Companion companion8 = Result.INSTANCE;
            Result.m118constructorimpl(ResultKt.createFailure(th5));
        }
        track.release();
        if ($am == null || prevVol < 0) {
            return;
        }
        Bell bell5 = INSTANCE;
        try {
            Result.Companion companion9 = Result.INSTANCE;
            $am.setStreamVolume(4, prevVol, 0);
            Result.m118constructorimpl(Unit.INSTANCE);
        } catch (Throwable th6) {
            th = th6;
            Result.Companion companion42 = Result.INSTANCE;
            Result.m118constructorimpl(ResultKt.createFailure(th));
        }
    }

    private final short[] synth() {
        double ringOut = 2.8d;
        double d = SR;
        int n = (int) (2.8d * d);
        double[] buf = new double[n];
        double[] freq = {3000.0d, 3009.0d, 6000.0d, 9000.0d};
        double[] amp = {1.0d, 0.8d, 0.5d, 0.22d};
        double[] tau = {1.9d, 1.8d, 0.55d, 0.3d};
        int i = 0;
        while (i < n) {
            double t = ((double) i) / d;
            double v = 0.0d;
            int length = freq.length;
            int p = 0;
            while (p < length) {
                double ringOut2 = ringOut;
                double ringOut3 = -t;
                v += amp[p] * Math.exp(ringOut3 / tau[p]) * Math.sin(freq[p] * 6.283185307179586d * t);
                p++;
                ringOut = ringOut2;
            }
            double ringOut4 = ringOut;
            double attack = Math.min(1.0d, t / 0.002d);
            buf[i] = v * attack;
            i++;
            ringOut = ringOut4;
        }
        double peak = 0.0d;
        for (double v2 : buf) {
            if (Math.abs(v2) > peak) {
                peak = Math.abs(v2);
            }
        }
        double g = peak > 0.0d ? 0.98d / peak : 1.0d;
        short[] out = new short[n];
        for (int i2 = 0; i2 < n; i2++) {
            out[i2] = (short) RangesKt.coerceIn((int) (buf[i2] * g * ((double) 32767)), HgtReader.HGT_VOID, 32767);
        }
        return out;
    }
}

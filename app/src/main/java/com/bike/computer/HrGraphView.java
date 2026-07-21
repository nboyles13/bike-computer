package com.bike.computer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;
import org.maplibre.android.style.layers.Property;

public final class HrGraphView extends View {
    private final Paint axis;
    private final Paint bandPaint;
    private final Paint fill;
    private long[] hist;
    private final Paint label;
    private final Paint line;
    private int liveHr;
    private final Paint marker;
    private int maxHr;

    public /* synthetic */ HrGraphView(Context context, AttributeSet attributeSet, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : attributeSet);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public HrGraphView(Context context) {
        this(context, null);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public HrGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Intrinsics.checkNotNullParameter(context, "context");
        this.hist = new long[0];
        this.maxHr = 185;
        Paint $this$bandPaint_u24lambda_u240 = new Paint(1);
        $this$bandPaint_u24lambda_u240.setStyle(Paint.Style.FILL);
        this.bandPaint = $this$bandPaint_u24lambda_u240;
        Paint $this$fill_u24lambda_u241 = new Paint(1);
        $this$fill_u24lambda_u241.setColor(Color.parseColor("#33FFFFFF"));
        $this$fill_u24lambda_u241.setStyle(Paint.Style.FILL);
        this.fill = $this$fill_u24lambda_u241;
        Paint $this$line_u24lambda_u242 = new Paint(1);
        $this$line_u24lambda_u242.setColor(-1);
        $this$line_u24lambda_u242.setStyle(Paint.Style.STROKE);
        $this$line_u24lambda_u242.setStrokeWidth(4.0f);
        $this$line_u24lambda_u242.setStrokeJoin(Paint.Join.ROUND);
        this.line = $this$line_u24lambda_u242;
        Paint $this$axis_u24lambda_u243 = new Paint(1);
        $this$axis_u24lambda_u243.setColor(Color.parseColor("#3A3A3A"));
        $this$axis_u24lambda_u243.setStrokeWidth(2.0f);
        this.axis = $this$axis_u24lambda_u243;
        Paint $this$label_u24lambda_u244 = new Paint(1);
        $this$label_u24lambda_u244.setColor(Color.parseColor("#9E9E9E"));
        $this$label_u24lambda_u244.setTextSize(28.0f);
        this.label = $this$label_u24lambda_u244;
        Paint $this$marker_u24lambda_u245 = new Paint(1);
        $this$marker_u24lambda_u245.setColor(-1);
        $this$marker_u24lambda_u245.setStrokeWidth(3.0f);
        this.marker = $this$marker_u24lambda_u245;
    }

    public final void setData(long[] hist, int maxHr, int liveHr) {
        Intrinsics.checkNotNullParameter(hist, "hist");
        this.hist = hist;
        this.maxHr = RangesKt.coerceAtLeast(maxHr, 1);
        this.liveHr = liveHr;
        invalidate();
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x018c  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x0194 A[SYNTHETIC] */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    protected void onDraw(Canvas canvas) {
        float h;
        float padR;
        float padT;
        float gh;
        float h2;
        boolean z = false;
        int i;
        HrZone[] zones;
        float padT2;
        Ref.IntRef hi;
        Ref.IntRef lo;
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        float w = getWidth();
        float h3 = getHeight();
        float padR2 = 20.0f;
        float padT3 = 22.0f;
        float gw = (w - 20.0f) - 20.0f;
        float gh2 = (h3 - 22.0f) - 46.0f;
        Ref.IntRef lo2 = new Ref.IntRef();
        lo2.element = this.maxHr / 2;
        Ref.IntRef hi2 = new Ref.IntRef();
        hi2.element = this.maxHr;
        int length = this.hist.length;
        for (int bpm = 0; bpm < length; bpm++) {
            if (this.hist[bpm] > 0) {
                if (bpm < lo2.element) {
                    lo2.element = bpm;
                }
                if (bpm > hi2.element) {
                    hi2.element = bpm;
                }
            }
        }
        int bpm2 = hi2.element;
        if (bpm2 - lo2.element < 10) {
            hi2.element = lo2.element + 10;
        }
        HrZone[] zones2 = HrZone.values();
        int length2 = zones2.length;
        int i2 = 0;
        while (true) {
            if (i2 >= length2) {
                break;
            }
            int zLo = (this.maxHr * zones2[i2].getLowerPct()) / 100;
            int i3 = length2;
            int zHi = i2 < 4 ? (this.maxHr * zones2[i2 + 1].getLowerPct()) / 100 : hi2.element + 1;
            float gh3 = gh2;
            float x0 = onDraw$x(20.0f, lo2, hi2, gw, RangesKt.coerceIn(zLo, lo2.element, hi2.element));
            int i4 = lo2.element;
            int zLo2 = hi2.element;
            float x1 = onDraw$x(20.0f, lo2, hi2, gw, RangesKt.coerceIn(zHi, i4, zLo2));
            if (x1 <= x0) {
                i = i2;
                zones = zones2;
                padT2 = padT3;
                hi = hi2;
                lo = lo2;
            } else {
                this.bandPaint.setColor((int) ((zones2[i2].getColor() & 16777215) | 1073741824));
                float padT4 = h3 - 46.0f;
                i = i2;
                float f = padT3;
                zones = zones2;
                hi = hi2;
                padT2 = padT3;
                lo = lo2;
                canvas.drawRect(x0, f, x1, padT4, this.bandPaint);
            }
            i2 = i + 1;
            hi2 = hi;
            lo2 = lo;
            length2 = i3;
            gh2 = gh3;
            padT3 = padT2;
            zones2 = zones;
        }
        Ref.IntRef hi3 = hi2;
        float gh4 = gh2;
        float padT5 = padT3;
        Ref.IntRef lo3 = lo2;
        canvas.drawLine(20.0f, h3 - 46.0f, w - 20.0f, h3 - 46.0f, this.axis);
        long total = ArraysKt.sum(this.hist);
        if (total <= 0) {
            canvas.drawText("No HR data yet", 20.0f + 20.0f, h3 / 2.0f, this.label);
            drawAxisLabels(canvas, lo3.element, hi3.element, h3, 46.0f, w, 20.0f);
            return;
        }
        float gh5 = gh4;
        Ref.FloatRef peak = new Ref.FloatRef();
        peak.element = 1.0f;
        float[] sm = new float[(hi3.element - lo3.element) + 1];
        int bpm3 = lo3.element;
        int i5 = hi3.element;
        if (bpm3 <= i5) {
            while (true) {
                long s = 0;
                int n = 0;
                int k = -2;
                while (true) {
                    padR = padR2;
                    if (k >= 3) {
                        break;
                    }
                    int b = bpm3 + k;
                    if (b >= 0) {
                        h2 = h3;
                        z = b < this.hist.length;
                        if (!z) {
                            s += this.hist[b];
                            n++;
                        }
                        k++;
                        padR2 = padR;
                        h3 = h2;
                    } else {
                        h2 = h3;
                    }
                    if (!z) {
                    }
                    k++;
                    padR2 = padR;
                    h3 = h2;
                }
                h = h3;
                float v = s / n;
                sm[bpm3 - lo3.element] = v;
                if (v > peak.element) {
                    peak.element = v;
                }
                if (bpm3 == i5) {
                    break;
                }
                bpm3++;
                padR2 = padR;
                h3 = h;
            }
        } else {
            h = h3;
            padR = 20.0f;
        }
        Path area = new Path();
        area.moveTo(onDraw$x(20.0f, lo3, hi3, gw, lo3.element), h - 46.0f);
        int bpm4 = lo3.element;
        int i6 = hi3.element;
        if (bpm4 <= i6) {
            while (true) {
                padT = padT5;
                gh = gh5;
                area.lineTo(onDraw$x(20.0f, lo3, hi3, gw, bpm4), onDraw$y(padT, peak, gh, sm[bpm4 - lo3.element]));
                if (bpm4 == i6) {
                    break;
                }
                bpm4++;
                gh5 = gh;
                padT5 = padT;
            }
        } else {
            padT = padT5;
            gh = gh5;
        }
        int bpm5 = hi3.element;
        area.lineTo(onDraw$x(20.0f, lo3, hi3, gw, bpm5), h - 46.0f);
        area.close();
        canvas.drawPath(area, this.fill);
        Path stroke = new Path();
        stroke.moveTo(onDraw$x(20.0f, lo3, hi3, gw, lo3.element), onDraw$y(padT, peak, gh, sm[0]));
        int bpm6 = lo3.element;
        int i7 = hi3.element;
        if (bpm6 <= i7) {
            while (true) {
                Path area2 = area;
                stroke.lineTo(onDraw$x(20.0f, lo3, hi3, gw, bpm6), onDraw$y(padT, peak, gh, sm[bpm6 - lo3.element]));
                if (bpm6 == i7) {
                    break;
                }
                bpm6++;
                area = area2;
            }
        }
        canvas.drawPath(stroke, this.line);
        int i8 = this.liveHr;
        if (lo3.element <= i8 && i8 <= hi3.element) {
            this.marker.setColor(-1);
            float fOnDraw$x = onDraw$x(20.0f, lo3, hi3, gw, this.liveHr);
            float gh6 = onDraw$x(20.0f, lo3, hi3, gw, this.liveHr);
            canvas.drawLine(fOnDraw$x, padT, gh6, h - 46.0f, this.marker);
        }
        drawAxisLabels(canvas, lo3.element, hi3.element, h, 46.0f, w, padR);
    }

    private static final float onDraw$x(float padL, Ref.IntRef lo, Ref.IntRef hi, float gw, int bpm) {
        return (((bpm - lo.element) / (hi.element - lo.element)) * gw) + padL;
    }

    private static final float onDraw$y(float padT, Ref.FloatRef peak, float gh, float v) {
        return ((1.0f - (v / peak.element)) * gh) + padT;
    }

    private final void drawAxisLabels(Canvas canvas, int lo, int hi, float h, float padB, float w, float padR) {
        this.label.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(String.valueOf(lo), 20.0f, h - 12.0f, this.label);
        this.label.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("bpm", w / 2.0f, h - 12.0f, this.label);
        this.label.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(String.valueOf(hi), w - padR, h - 12.0f, this.label);
        this.label.setTextAlign(Paint.Align.LEFT);
    }
}

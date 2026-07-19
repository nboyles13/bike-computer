package com.bike.computer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import org.maplibre.android.style.layers.Property;

/* JADX INFO: compiled from: ElevationView.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u0014\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u001d\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0014\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\n0\tJ\u0010\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0015H\u0014R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lcom/bike/computer/ElevationView;", "Landroid/view/View;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "<init>", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "samples", "", "", "fill", "Landroid/graphics/Paint;", Property.SYMBOL_PLACEMENT_LINE, "axis", "label", "setData", "", "s", "onDraw", "canvas", "Landroid/graphics/Canvas;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class ElevationView extends View {
    private final Paint axis;
    private final Paint fill;
    private final Paint label;
    private final Paint line;
    private List<float[]> samples;

    public /* synthetic */ ElevationView(Context context, AttributeSet attributeSet, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : attributeSet);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public ElevationView(Context context) {
        this(context, null);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ElevationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Intrinsics.checkNotNullParameter(context, "context");
        this.samples = CollectionsKt.emptyList();
        Paint $this$fill_u24lambda_u240 = new Paint(1);
        $this$fill_u24lambda_u240.setColor(Color.parseColor("#3D7BC0"));
        $this$fill_u24lambda_u240.setStyle(Paint.Style.FILL);
        this.fill = $this$fill_u24lambda_u240;
        Paint $this$line_u24lambda_u241 = new Paint(1);
        $this$line_u24lambda_u241.setColor(Color.parseColor("#8FC7FF"));
        $this$line_u24lambda_u241.setStyle(Paint.Style.STROKE);
        $this$line_u24lambda_u241.setStrokeWidth(4.0f);
        this.line = $this$line_u24lambda_u241;
        Paint $this$axis_u24lambda_u242 = new Paint(1);
        $this$axis_u24lambda_u242.setColor(Color.parseColor("#3A3A3A"));
        $this$axis_u24lambda_u242.setStrokeWidth(2.0f);
        this.axis = $this$axis_u24lambda_u242;
        Paint $this$label_u24lambda_u243 = new Paint(1);
        $this$label_u24lambda_u243.setColor(Color.parseColor("#9E9E9E"));
        $this$label_u24lambda_u243.setTextSize(30.0f);
        this.label = $this$label_u24lambda_u243;
    }

    public final void setData(List<float[]> s) {
        Intrinsics.checkNotNullParameter(s, "s");
        this.samples = s;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        float w = getWidth();
        float h = getHeight();
        canvas.drawLine(90.0f, h - 50.0f, w - 30.0f, h - 50.0f, this.axis);
        canvas.drawLine(90.0f, 30.0f, 90.0f, h - 50.0f, this.axis);
        if (this.samples.size() < 2) {
            canvas.drawText("No elevation yet", 30.0f + 90.0f, h / 2.0f, this.label);
            return;
        }
        float maxD = RangesKt.coerceAtLeast(((float[]) CollectionsKt.last((List) this.samples))[0], 1.0f);
        Ref.FloatRef minE = new Ref.FloatRef();
        minE.element = Float.MAX_VALUE;
        Ref.FloatRef maxE = new Ref.FloatRef();
        maxE.element = -3.4028235E38f;
        for (float[] s : this.samples) {
            minE.element = Math.min(minE.element, s[1]);
            maxE.element = Math.max(maxE.element, s[1]);
        }
        if (maxE.element - minE.element < 5.0f) {
            maxE.element += 5.0f;
            minE.element -= 5.0f;
        }
        float gw = (w - 90.0f) - 30.0f;
        float gh = (h - 30.0f) - 50.0f;
        Path path = new Path();
        path.moveTo(onDraw$x(90.0f, maxD, gw, this.samples.get(0)[0]), h - 50.0f);
        Iterator<float[]> it = this.samples.iterator();
        while (it.hasNext()) {
            float[] s2 = it.next();
            path.lineTo(onDraw$x(90.0f, maxD, gw, s2[0]), onDraw$y(30.0f, minE, maxE, gh, s2[1]));
            it = it;
            w = w;
        }
        float w2 = w;
        path.lineTo(onDraw$x(90.0f, maxD, gw, ((float[]) CollectionsKt.last((List) this.samples))[0]), h - 50.0f);
        path.close();
        canvas.drawPath(path, this.fill);
        Path stroke = new Path();
        stroke.moveTo(onDraw$x(90.0f, maxD, gw, this.samples.get(0)[0]), onDraw$y(30.0f, minE, maxE, gh, this.samples.get(0)[1]));
        for (Iterator<float[]> it2 = this.samples.iterator(); it2.hasNext(); it2 = it2) {
            float[] s3 = it2.next();
            stroke.lineTo(onDraw$x(90.0f, maxD, gw, s3[0]), onDraw$y(30.0f, minE, maxE, gh, s3[1]));
        }
        canvas.drawPath(stroke, this.line);
        canvas.drawText(((int) Units.INSTANCE.feet(maxE.element)) + " ft", 8.0f, 24.0f + 30.0f, this.label);
        canvas.drawText(((int) Units.INSTANCE.feet(minE.element)) + " ft", 8.0f, h - 50.0f, this.label);
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String str = String.format("%.1f mi", Arrays.copyOf(new Object[]{Double.valueOf(Units.INSTANCE.miles(maxD))}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        canvas.drawText(str, (w2 - 30.0f) - 90.0f, h - 12.0f, this.label);
    }

    private static final float onDraw$x(float padL, float maxD, float gw, float d) {
        return ((d / maxD) * gw) + padL;
    }

    private static final float onDraw$y(float padT, Ref.FloatRef minE, Ref.FloatRef maxE, float gh, float e) {
        return ((1.0f - ((e - minE.element) / (maxE.element - minE.element))) * gh) + padT;
    }
}

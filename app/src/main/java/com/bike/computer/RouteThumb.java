package com.bike.computer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;
import org.maplibre.android.style.layers.Property;

/* JADX INFO: compiled from: RouteThumb.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0010\u0013\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0014\u0010\u000f\u001a\u00020\u00102\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\b0\u0007J\u0010\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u0014H\u0014R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"Lcom/bike/computer/RouteThumb;", "Landroid/view/View;", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "pts", "", "", Property.SYMBOL_PLACEMENT_LINE, "Landroid/graphics/Paint;", "startDot", "endDot", "path", "Landroid/graphics/Path;", "setPoints", "", "p", "onDraw", "canvas", "Landroid/graphics/Canvas;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class RouteThumb extends View {
    private final Paint endDot;
    private final Paint line;
    private final Path path;
    private List<double[]> pts;
    private final Paint startDot;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public RouteThumb(Context context) {
        super(context);
        Intrinsics.checkNotNullParameter(context, "context");
        this.pts = CollectionsKt.emptyList();
        Paint $this$line_u24lambda_u240 = new Paint(1);
        $this$line_u24lambda_u240.setColor(-37632);
        $this$line_u24lambda_u240.setStyle(Paint.Style.STROKE);
        $this$line_u24lambda_u240.setStrokeWidth(6.0f);
        $this$line_u24lambda_u240.setStrokeCap(Paint.Cap.ROUND);
        $this$line_u24lambda_u240.setStrokeJoin(Paint.Join.ROUND);
        this.line = $this$line_u24lambda_u240;
        Paint $this$startDot_u24lambda_u241 = new Paint(1);
        $this$startDot_u24lambda_u241.setColor(-13577896);
        this.startDot = $this$startDot_u24lambda_u241;
        Paint $this$endDot_u24lambda_u242 = new Paint(1);
        $this$endDot_u24lambda_u242.setColor(-47814);
        this.endDot = $this$endDot_u24lambda_u242;
        this.path = new Path();
    }

    public final void setPoints(List<double[]> p) {
        Intrinsics.checkNotNullParameter(p, "p");
        this.pts = p;
        invalidate();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        int n = this.pts.size();
        if (n < 2 || getWidth() == 0 || getHeight() == 0) {
            return;
        }
        Ref.DoubleRef minLon = new Ref.DoubleRef();
        minLon.element = Double.MAX_VALUE;
        Ref.DoubleRef maxLat = new Ref.DoubleRef();
        maxLat.element = -1.7976931348623157E308d;
        double maxLon = -1.7976931348623157E308d;
        double minLat = Double.MAX_VALUE;
        for (double[] p : this.pts) {
            if (p[0] < minLon.element) {
                minLon.element = p[0];
            }
            if (p[0] > maxLon) {
                maxLon = p[0];
            }
            if (p[1] < minLat) {
                minLat = p[1];
            }
            if (p[1] > maxLat.element) {
                maxLat.element = p[1];
            }
        }
        double d = 2;
        double latMid = Math.toRadians((minLat + maxLat.element) / d);
        double cos = Math.cos(latMid);
        double spanX = RangesKt.coerceAtLeast((maxLon - minLon.element) * cos, 1.0E-9d);
        double spanY = RangesKt.coerceAtLeast(maxLat.element - minLat, 1.0E-9d);
        float f = 2;
        float w = getWidth() - (f * 24.0f);
        float h = getHeight() - (f * 24.0f);
        double scale = Math.min(((double) w) / spanX, ((double) h) / spanY);
        double offX = ((double) 24.0f) + ((((double) w) - (spanX * scale)) / d);
        double offY = ((double) 24.0f) + ((((double) h) - (spanY * scale)) / d);
        this.path.reset();
        int i = 0;
        this.path.moveTo(onDraw$px(offX, minLon, cos, scale, this.pts.get(0)), onDraw$py(offY, maxLat, scale, this.pts.get(0)));
        for (int i2 = 1; i2 < n; i2++) {
            i = i;
            this.path.lineTo(onDraw$px(offX, minLon, cos, scale, this.pts.get(i2)), onDraw$py(offY, maxLat, scale, this.pts.get(i2)));
        }
        canvas.drawPath(this.path, this.line);
        canvas.drawCircle(onDraw$px(offX, minLon, cos, scale, this.pts.get(i)), onDraw$py(offY, maxLat, scale, this.pts.get(i)), 7.0f, this.startDot);
        canvas.drawCircle(onDraw$px(offX, minLon, cos, scale, this.pts.get(n - 1)), onDraw$py(offY, maxLat, scale, this.pts.get(n - 1)), 7.0f, this.endDot);
    }

    private static final float onDraw$px(double offX, Ref.DoubleRef minLon, double cos, double scale, double[] p) {
        return (float) (((p[0] - minLon.element) * cos * scale) + offX);
    }

    private static final float onDraw$py(double offY, Ref.DoubleRef maxLat, double scale, double[] p) {
        return (float) (((maxLat.element - p[1]) * scale) + offY);
    }
}

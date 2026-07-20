package com.bike.computer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlinx.coroutines.DebugKt;

/* JADX INFO: compiled from: DashboardView.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 e2\u00020\u0001:\u0002efB\u001d\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020.H\u0002J\b\u00100\u001a\u00020.H\u0002J\u0014\u00101\u001a\u00020\"2\f\u00102\u001a\b\u0012\u0004\u0012\u00020\u000e03J\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e03J\u000e\u00105\u001a\u00020\"2\u0006\u00106\u001a\u00020\u0014J\u0006\u00107\u001a\u00020\"J\u000e\u00108\u001a\u00020\"2\u0006\u00109\u001a\u00020\u001aJ\u0016\u0010:\u001a\u00020\"2\u0006\u0010;\u001a\u00020\u000e2\u0006\u0010<\u001a\u00020\u001aJ\u0014\u0010=\u001a\u00020\"2\n\b\u0002\u0010>\u001a\u0004\u0018\u00010\u000eH\u0002J\u001c\u0010?\u001a\u000e\u0012\u0004\u0012\u00020.\u0012\u0004\u0012\u00020.0@2\u0006\u0010<\u001a\u00020\u001aH\u0002J\b\u0010A\u001a\u00020\"H\u0002J\u0018\u0010B\u001a\u00020\"2\u0006\u0010C\u001a\u00020.2\u0006\u0010D\u001a\u00020.H\u0014J0\u0010E\u001a\u00020\"2\u0006\u0010F\u001a\u00020\u00142\u0006\u0010G\u001a\u00020.2\u0006\u0010H\u001a\u00020.2\u0006\u0010I\u001a\u00020.2\u0006\u0010J\u001a\u00020.H\u0014J \u0010K\u001a\u00020\"2\u0006\u0010;\u001a\u00020\u000e2\u0006\u0010L\u001a\u00020M2\u0006\u0010N\u001a\u00020MH\u0002J\b\u0010O\u001a\u00020\"H\u0002J\u0010\u0010^\u001a\u00020\u00142\u0006\u0010_\u001a\u00020`H\u0016J\u0010\u0010a\u001a\u00020\u00142\u0006\u0010_\u001a\u00020`H\u0016J\u001a\u0010b\u001a\u0004\u0018\u00010\u000e2\u0006\u0010c\u001a\u00020M2\u0006\u0010d\u001a\u00020MH\u0002R\u0018\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000bR\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u000e¢\u0006\u0002\n\u0000R*\u0010\u000f\u001a\u001e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u00110\u0010j\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u0011`\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0013\u001a\u00020\u0014@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R(\u0010\u0018\u001a\u0010\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u00020\u001b\u0018\u00010\u0019X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\"\u0010 \u001a\n\u0012\u0004\u0012\u00020\"\u0018\u00010!X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010$\"\u0004\b%\u0010&R(\u0010'\u001a\u0010\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\"\u0018\u00010\u0019X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b(\u0010\u001d\"\u0004\b)\u0010\u001fR(\u0010*\u001a\u0010\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\"\u0018\u00010\u0019X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b+\u0010\u001d\"\u0004\b,\u0010\u001fR\u000e\u00104\u001a\u00020.X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010P\u001a\u0004\u0018\u00010QX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010R\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010S\u001a\u00020.X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010T\u001a\u00020MX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010U\u001a\u00020MX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010V\u001a\u00020.X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010W\u001a\u00020.X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010X\u001a\u00020.X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010Y\u001a\u00020.X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010Z\u001a\u00020\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010[\u001a\u00020MX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\\\u001a\u00020MX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010]\u001a\u00020.X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006g"}, d2 = {"Lcom/bike/computer/DashboardView;", "Landroid/view/ViewGroup;", "context", "Landroid/content/Context;", "attrs", "Landroid/util/AttributeSet;", "<init>", "(Landroid/content/Context;Landroid/util/AttributeSet;)V", "medium", "Landroid/graphics/Typeface;", "kotlin.jvm.PlatformType", "Landroid/graphics/Typeface;", "tiles", "", "Lcom/bike/computer/DashTile;", "holders", "Ljava/util/HashMap;", "Lcom/bike/computer/DashboardView$Holder;", "Lkotlin/collections/HashMap;", "value", "", "editing", "getEditing", "()Z", "valueProvider", "Lkotlin/Function1;", "Lcom/bike/computer/Metric;", "", "getValueProvider", "()Lkotlin/jvm/functions/Function1;", "setValueProvider", "(Lkotlin/jvm/functions/Function1;)V", "onChanged", "Lkotlin/Function0;", "", "getOnChanged", "()Lkotlin/jvm/functions/Function0;", "setOnChanged", "(Lkotlin/jvm/functions/Function0;)V", "onPickMetric", "getOnPickMetric", "setOnPickMetric", "onEditModeChanged", "getOnEditModeChanged", "setOnEditModeChanged", "dp", "", "v", "gridRows", "setTiles", "list", "", "editInsetTop", "setEditing", DebugKt.DEBUG_PROPERTY_VALUE_ON, "refresh", "addTile", "metric", "applyMetric", "tile", "m", "pack", "priority", "metricIcon", "Lkotlin/Pair;", "rebuild", "onMeasure", "w", "h", "onLayout", "changed", "l", "t", "r", "b", "layoutTile", "colW", "", "rowH", "reflowExceptDragged", "draggingCard", "Landroid/widget/FrameLayout;", "dragTile", "mode", "downX", "downY", "origL", "origT", "origR", "origB", "moved", "dragColW", "dragRowH", "dragGridRows", "onInterceptTouchEvent", "ev", "Landroid/view/MotionEvent;", "onTouchEvent", "tileAt", "x", "y", "Companion", "Holder", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class DashboardView extends ViewGroup {
    public static final int COLS = 4;
    private float downX;
    private float downY;
    private float dragColW;
    private int dragGridRows;
    private float dragRowH;
    private DashTile dragTile;
    private FrameLayout draggingCard;
    private int editInsetTop;
    private boolean editing;
    private final HashMap<DashTile, Holder> holders;
    private final Typeface medium;
    private int mode;
    private boolean moved;
    private Function0<Unit> onChanged;
    private Function1<? super Boolean, Unit> onEditModeChanged;
    private Function1<? super DashTile, Unit> onPickMetric;
    private int origB;
    private int origL;
    private int origR;
    private int origT;
    private List<DashTile> tiles;
    private Function1<? super Metric, String> valueProvider;

    /* JADX INFO: compiled from: DashboardView.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Metric.values().length];
            try {
                iArr[Metric.SPEED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Metric.AVG_SPEED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Metric.MAX_SPEED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Metric.HR.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                iArr[Metric.AVG_HR.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                iArr[Metric.MAX_HR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                iArr[Metric.DISTANCE.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                iArr[Metric.RIDE_TIME.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
            try {
                iArr[Metric.ELEVATION.ordinal()] = 9;
            } catch (NoSuchFieldError e9) {
            }
            try {
                iArr[Metric.ASCENT.ordinal()] = 10;
            } catch (NoSuchFieldError e10) {
            }
            try {
                iArr[Metric.GRADE.ordinal()] = 11;
            } catch (NoSuchFieldError e11) {
            }
            try {
                iArr[Metric.CADENCE.ordinal()] = 12;
            } catch (NoSuchFieldError e12) {
            }
            try {
                iArr[Metric.POWER.ordinal()] = 13;
            } catch (NoSuchFieldError e13) {
            }
            try {
                iArr[Metric.AVG_POWER.ordinal()] = 14;
            } catch (NoSuchFieldError e14) {
            }
            try {
                iArr[Metric.MAX_POWER.ordinal()] = 15;
            } catch (NoSuchFieldError e15) {
            }
            try {
                iArr[Metric.CLOCK.ordinal()] = 16;
            } catch (NoSuchFieldError e16) {
            }
            try {
                iArr[Metric.BATTERY.ordinal()] = 17;
            } catch (NoSuchFieldError e17) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public /* synthetic */ DashboardView(Context context, AttributeSet attributeSet, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(context, (i & 2) != 0 ? null : attributeSet);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public DashboardView(Context context) {
        this(context, null);
        Intrinsics.checkNotNullParameter(context, "context");
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DashboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Intrinsics.checkNotNullParameter(context, "context");
        this.medium = Typeface.create("sans-serif-medium", 0);
        this.tiles = new ArrayList();
        this.holders = new HashMap<>();
        this.dragColW = 1.0f;
        this.dragRowH = 1.0f;
        this.dragGridRows = 1;
    }

    public final boolean getEditing() {
        return this.editing;
    }

    public final Function1<Metric, String> getValueProvider() {
        return (Function1<Metric, String>) this.valueProvider;
    }

    public final void setValueProvider(Function1<? super Metric, String> function1) {
        this.valueProvider = function1;
    }

    public final Function0<Unit> getOnChanged() {
        return this.onChanged;
    }

    public final void setOnChanged(Function0<Unit> function0) {
        this.onChanged = function0;
    }

    public final Function1<DashTile, Unit> getOnPickMetric() {
        return (Function1<DashTile, Unit>) this.onPickMetric;
    }

    public final void setOnPickMetric(Function1<? super DashTile, Unit> function1) {
        this.onPickMetric = function1;
    }

    public final Function1<Boolean, Unit> getOnEditModeChanged() {
        return (Function1<Boolean, Unit>) this.onEditModeChanged;
    }

    public final void setOnEditModeChanged(Function1<? super Boolean, Unit> function1) {
        this.onEditModeChanged = function1;
    }

    private final int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private final int gridRows() {
        Integer num;
        Iterator<DashTile> it = this.tiles.iterator();
        if (it.hasNext()) {
            DashTile it2 = (DashTile) it.next();
            Integer numValueOf = Integer.valueOf(it2.getRow() + it2.getH());
            while (it.hasNext()) {
                DashTile it3 = (DashTile) it.next();
                Integer numValueOf2 = Integer.valueOf(it3.getRow() + it3.getH());
                if (numValueOf.compareTo(numValueOf2) < 0) {
                    numValueOf = numValueOf2;
                }
            }
            num = numValueOf;
        } else {
            num = null;
        }
        Integer num2 = num;
        return RangesKt.coerceAtLeast(num2 != null ? num2.intValue() : 1, 1);
    }

    /* JADX INFO: compiled from: DashboardView.kt */
    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\b\u0002\u0018\u00002\u00020\u0001B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\t\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0013R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017¨\u0006\u0018"}, d2 = {"Lcom/bike/computer/DashboardView$Holder;", "", "card", "Landroid/widget/FrameLayout;", "icon", "Landroid/widget/ImageView;", "label", "Landroid/widget/TextView;", "value", "remove", "handle", "Landroid/view/View;", "<init>", "(Landroid/widget/FrameLayout;Landroid/widget/ImageView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/widget/TextView;Landroid/view/View;)V", "getCard", "()Landroid/widget/FrameLayout;", "getIcon", "()Landroid/widget/ImageView;", "getLabel", "()Landroid/widget/TextView;", "getValue", "getRemove", "getHandle", "()Landroid/view/View;", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
    private static final class Holder {
        private final FrameLayout card;
        private final View handle;
        private final ImageView icon;
        private final TextView label;
        private final TextView remove;
        private final TextView value;

        public Holder(FrameLayout card, ImageView icon, TextView label, TextView value, TextView remove, View handle) {
            Intrinsics.checkNotNullParameter(card, "card");
            Intrinsics.checkNotNullParameter(icon, "icon");
            Intrinsics.checkNotNullParameter(label, "label");
            Intrinsics.checkNotNullParameter(value, "value");
            Intrinsics.checkNotNullParameter(remove, "remove");
            Intrinsics.checkNotNullParameter(handle, "handle");
            this.card = card;
            this.icon = icon;
            this.label = label;
            this.value = value;
            this.remove = remove;
            this.handle = handle;
        }

        public final FrameLayout getCard() {
            return this.card;
        }

        public final ImageView getIcon() {
            return this.icon;
        }

        public final TextView getLabel() {
            return this.label;
        }

        public final View getHandle() {
            return this.handle;
        }

        public final TextView getRemove() {
            return this.remove;
        }

        public final TextView getValue() {
            return this.value;
        }
    }

    public final void setTiles(List<DashTile> list) {
        Intrinsics.checkNotNullParameter(list, "list");
        List<DashTile> $this$map$iv = list;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            DashTile it = (DashTile) item$iv$iv;
            destination$iv$iv.add(DashTile.copy$default(it, null, 0, 0, 0, 0, 31, null));
        }
        this.tiles = CollectionsKt.toMutableList(destination$iv$iv);
        pack$default(this, null, 1, null);
        rebuild();
    }

    public final List<DashTile> tiles() {
        Iterable $this$map$iv = this.tiles;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            DashTile it = (DashTile) item$iv$iv;
            destination$iv$iv.add(DashTile.copy$default(it, null, 0, 0, 0, 0, 31, null));
        }
        return (List) destination$iv$iv;
    }

    public final void setEditing(boolean on) {
        this.editing = on;
        this.editInsetTop = on ? dp(52) : 0;
        Iterable iterableValues = this.holders.values();
        Intrinsics.checkNotNullExpressionValue(iterableValues, "<get-values>(...)");
        Iterable $this$forEach$iv = iterableValues;
        for (Object element$iv : $this$forEach$iv) {
            Holder it = (Holder) element$iv;
            int i = 8;
            it.getRemove().setVisibility(on ? 0 : 8);
            View handle = it.getHandle();
            if (on) {
                i = 0;
            }
            handle.setVisibility(i);
            it.getCard().setAlpha(1.0f);
        }
        requestLayout();
        Function1<? super Boolean, Unit> function1 = this.onEditModeChanged;
        if (function1 != null) {
            function1.invoke(Boolean.valueOf(on));
        }
    }

    public final void refresh() {
        Function1<? super Metric, String> function1 = this.valueProvider;
        if (function1 == null) {
            return;
        }
        for (Map.Entry<DashTile, Holder> entry : this.holders.entrySet()) {
            DashTile t = entry.getKey();
            Holder h = entry.getValue();
            h.getValue().setText(function1.invoke(t.getMetric()));
        }
    }

    public final void addTile(Metric metric) {
        Intrinsics.checkNotNullParameter(metric, "metric");
        this.tiles.add(new DashTile(metric, 0, gridRows(), 2, 1));
        pack$default(this, null, 1, null);
        rebuild();
        Function0<Unit> function0 = this.onChanged;
        if (function0 != null) {
            function0.invoke();
        }
    }

    public final void applyMetric(DashTile tile, Metric m) {
        Intrinsics.checkNotNullParameter(tile, "tile");
        Intrinsics.checkNotNullParameter(m, "m");
        tile.setMetric(m);
        rebuild();
        Function0<Unit> function0 = this.onChanged;
        if (function0 != null) {
            function0.invoke();
        }
    }

    static /* synthetic */ void pack$default(DashboardView dashboardView, DashTile dashTile, int i, Object obj) {
        if ((i & 1) != 0) {
            dashTile = null;
        }
        dashboardView.pack(dashTile);
    }

    private final void pack(DashTile priority) {
        HashSet occ = new HashSet();
        if (priority != null) {
            priority.setW(RangesKt.coerceIn(priority.getW(), 1, 4));
            priority.setH(RangesKt.coerceAtLeast(priority.getH(), 1));
            priority.setCol(RangesKt.coerceIn(priority.getCol(), 0, 4 - priority.getW()));
            priority.setRow(RangesKt.coerceAtLeast(priority.getRow(), 0));
            pack$reserve(occ, priority.getCol(), priority.getRow(), priority.getW(), priority.getH());
        }
        for (DashTile t : (List<DashTile>) CollectionsKt.sortedWith(this.tiles, ComparisonsKt.compareBy(new Function1() { // from class: com.bike.computer.DashboardView$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return DashboardView.pack$lambda$5((DashTile) obj);
            }
        }, new Function1() { // from class: com.bike.computer.DashboardView$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return DashboardView.pack$lambda$6((DashTile) obj);
            }
        }))) {
            if (t != priority) {
                int w = RangesKt.coerceIn(t.getW(), 1, 4);
                int h = RangesKt.coerceAtLeast(t.getH(), 1);
                t.setW(w);
                t.setH(h);
                int r = 0;
                int col = 0;
                boolean placed = false;
                while (!placed) {
                    for (int cc = 0; cc <= 4 - w; cc++) {
                        if (pack$fits(occ, cc, r, w, h)) {
                            col = cc;
                            placed = true;
                            break;
                        }
                    }
                    if (!placed) {
                        r++;
                    }
                }
                t.setCol(col);
                t.setRow(r);
                pack$reserve(occ, col, r, w, h);
            }
        }
    }

    private static final void pack$reserve(HashSet<Integer> hashSet, int c, int r, int w, int h) {
        for (int dr = 0; dr < h; dr++) {
            for (int dc = 0; dc < w; dc++) {
                hashSet.add(Integer.valueOf(((r + dr) * 4) + c + dc));
            }
        }
    }

    private static final boolean pack$fits(HashSet<Integer> hashSet, int c, int r, int w, int h) {
        if (c + w > 4) {
            return false;
        }
        for (int dr = 0; dr < h; dr++) {
            for (int dc = 0; dc < w; dc++) {
                if (hashSet.contains(Integer.valueOf(((r + dr) * 4) + c + dc))) {
                    return false;
                }
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable pack$lambda$5(DashTile it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return Integer.valueOf(it.getRow());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Comparable pack$lambda$6(DashTile it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return Integer.valueOf(it.getCol());
    }

    private final Pair<Integer, Integer> metricIcon(Metric m) {
        switch (WhenMappings.$EnumSwitchMapping$0[m.ordinal()]) {
            case 1:
            case 2:
            case 3:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_speed), Integer.valueOf(Color.parseColor("#FF4C8DFF")));
            case 4:
            case 5:
            case 6:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_heart), Integer.valueOf(Color.parseColor("#FFFF453A")));
            case 7:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_distance), Integer.valueOf(Color.parseColor("#FF30D158")));
            case 8:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_time), Integer.valueOf(Color.parseColor("#FFFF9F0A")));
            case 9:
            case 10:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_elevation), Integer.valueOf(Color.parseColor("#FFBF5AF2")));
            case 11:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_incline), Integer.valueOf(Color.parseColor("#FF64D2FF")));
            case 12:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_cadence), Integer.valueOf(Color.parseColor("#FF5AC8FA")));
            case 13:
            case 14:
            case 15:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_power), Integer.valueOf(Color.parseColor("#FFFFD60A")));
            case 16:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_time), Integer.valueOf(Color.parseColor("#FFB8B8BD")));
            case 17:
                return TuplesKt.to(Integer.valueOf(R.drawable.ic_battery), Integer.valueOf(Color.parseColor("#FF30D158")));
            default:
                throw new NoWhenBranchMatchedException();
        }
    }

    private final void rebuild() {
        removeAllViews();
        this.holders.clear();
        for (Iterator<DashTile> it = this.tiles.iterator(); it.hasNext(); it = it) {
            final DashTile t = it.next();
            FrameLayout card = new FrameLayout(getContext());
            card.setBackgroundResource(R.drawable.card_solid);
            LinearLayout body = new LinearLayout(getContext());
            body.setOrientation(1);
            body.setGravity(1);
            body.setPadding(dp(4), dp(4), dp(4), dp(5));
            body.setLayoutParams(new FrameLayout.LayoutParams(-1, -1));
            Pair<Integer, Integer> pairMetricIcon = metricIcon(t.getMetric());
            int iconRes = pairMetricIcon.component1().intValue();
            int accent = pairMetricIcon.component2().intValue();
            LinearLayout $this$rebuild_u24lambda_u249 = new LinearLayout(getContext());
            $this$rebuild_u24lambda_u249.setOrientation(0);
            $this$rebuild_u24lambda_u249.setGravity(17);
            $this$rebuild_u24lambda_u249.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
            ImageView $this$rebuild_u24lambda_u2411 = new ImageView(getContext());
            $this$rebuild_u24lambda_u2411.setImageResource(iconRes);
            $this$rebuild_u24lambda_u2411.setColorFilter(accent);
            LinearLayout.LayoutParams $this$rebuild_u24lambda_u2411_u24lambda_u2410 = new LinearLayout.LayoutParams(dp(15), dp(15));
            $this$rebuild_u24lambda_u2411_u24lambda_u2410.rightMargin = dp(5);
            $this$rebuild_u24lambda_u2411.setLayoutParams($this$rebuild_u24lambda_u2411_u24lambda_u2410);
            TextView $this$rebuild_u24lambda_u2412 = new TextView(getContext());
            $this$rebuild_u24lambda_u2412.setText(t.getMetric().getLabel());
            $this$rebuild_u24lambda_u2412.setTextColor(Color.parseColor("#FF9E9E9E"));
            $this$rebuild_u24lambda_u2412.setTextSize(13.0f);
            $this$rebuild_u24lambda_u2412.setMaxLines(1);
            $this$rebuild_u24lambda_u2412.setIncludeFontPadding(false);
            $this$rebuild_u24lambda_u249.addView($this$rebuild_u24lambda_u2411);
            $this$rebuild_u24lambda_u249.addView($this$rebuild_u24lambda_u2412);
            TextView $this$rebuild_u24lambda_u2413 = new TextView(getContext());
            $this$rebuild_u24lambda_u2413.setText("--");
            $this$rebuild_u24lambda_u2413.setTextColor(-1);
            $this$rebuild_u24lambda_u2413.setTypeface(this.medium);
            $this$rebuild_u24lambda_u2413.setIncludeFontPadding(false);
            $this$rebuild_u24lambda_u2413.setMaxLines(1);
            $this$rebuild_u24lambda_u2413.setGravity(17);
            $this$rebuild_u24lambda_u2413.setAutoSizeTextTypeUniformWithConfiguration(24, 160, 1, 2);
            $this$rebuild_u24lambda_u2413.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1.0f));
            body.addView($this$rebuild_u24lambda_u249);
            body.addView($this$rebuild_u24lambda_u2413);
            card.addView(body);
            TextView $this$rebuild_u24lambda_u2415 = new TextView(getContext());
            $this$rebuild_u24lambda_u2415.setText("✕");
            $this$rebuild_u24lambda_u2415.setTextColor(-1);
            $this$rebuild_u24lambda_u2415.setTextSize(13.0f);
            $this$rebuild_u24lambda_u2415.setGravity(17);
            $this$rebuild_u24lambda_u2415.setBackgroundColor(Color.parseColor("#CCE53935"));
            $this$rebuild_u24lambda_u2415.setLayoutParams(new FrameLayout.LayoutParams(dp(24), dp(24), 8388661));
            $this$rebuild_u24lambda_u2415.setVisibility(8);
            $this$rebuild_u24lambda_u2415.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.DashboardView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    DashboardView.rebuild$lambda$15$lambda$14(DashboardView.this, t, view);
                }
            });
            View $this$rebuild_u24lambda_u2416 = new View(getContext());
            $this$rebuild_u24lambda_u2416.setBackgroundResource(R.drawable.resize_handle);
            $this$rebuild_u24lambda_u2416.setLayoutParams(new FrameLayout.LayoutParams(dp(26), dp(26), 8388693));
            $this$rebuild_u24lambda_u2416.setVisibility(8);
            card.addView($this$rebuild_u24lambda_u2415);
            card.addView($this$rebuild_u24lambda_u2416);
            addView(card);
            this.holders.put(t, new Holder(card, $this$rebuild_u24lambda_u2411, $this$rebuild_u24lambda_u2412, $this$rebuild_u24lambda_u2413, $this$rebuild_u24lambda_u2415, $this$rebuild_u24lambda_u2416));
        }
        setEditing(this.editing);
        refresh();
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void rebuild$lambda$15$lambda$14(DashboardView this$0, DashTile $t, View it) {
        this$0.tiles.remove($t);
        pack$default(this$0, null, 1, null);
        this$0.rebuild();
        Function0<Unit> function0 = this$0.onChanged;
        if (function0 != null) {
            function0.invoke();
        }
    }

    @Override // android.view.View
    protected void onMeasure(int w, int h) {
        setMeasuredDimension(View.MeasureSpec.getSize(w), View.MeasureSpec.getSize(h));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        float colW = getWidth() / 4;
        float rowH = (getHeight() - this.editInsetTop) / gridRows();
        for (DashTile tile : this.tiles) {
            Holder holder = this.holders.get(tile);
            if ((holder != null ? holder.getCard() : null) != this.draggingCard) {
                layoutTile(tile, colW, rowH);
            }
        }
    }

    private final void layoutTile(DashTile tile, float colW, float rowH) {
        Holder h = this.holders.get(tile);
        if (h == null) {
            return;
        }
        int m = dp(2);
        int left = ((int) (tile.getCol() * colW)) + m;
        int top = this.editInsetTop + ((int) (tile.getRow() * rowH)) + m;
        int right = ((int) ((tile.getCol() * colW) + (tile.getW() * colW))) - m;
        int bottom = (this.editInsetTop + ((int) ((tile.getRow() * rowH) + (tile.getH() * rowH)))) - m;
        h.getCard().measure(View.MeasureSpec.makeMeasureSpec(RangesKt.coerceAtLeast(right - left, 0), 1073741824), View.MeasureSpec.makeMeasureSpec(RangesKt.coerceAtLeast(bottom - top, 0), 1073741824));
        h.getCard().layout(left, top, right, bottom);
    }

    private final void reflowExceptDragged() {
        for (DashTile tile : this.tiles) {
            Holder holder = this.holders.get(tile);
            if ((holder != null ? holder.getCard() : null) != this.draggingCard) {
                layoutTile(tile, this.dragColW, this.dragRowH);
            }
        }
        invalidate();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        DashTile tile;
        Intrinsics.checkNotNullParameter(ev, "ev");
        if (!this.editing || ev.getActionMasked() != 0 || (tile = tileAt(ev.getX(), ev.getY())) == null) {
            return false;
        }
        Holder holder = this.holders.get(tile);
        Intrinsics.checkNotNull(holder);
        Holder h = holder;
        if (ev.getX() > h.getCard().getRight() - dp(34) && ev.getY() < h.getCard().getTop() + dp(34)) {
            return false;
        }
        this.dragTile = tile;
        this.draggingCard = h.getCard();
        this.origL = h.getCard().getLeft();
        this.origT = h.getCard().getTop();
        this.origR = h.getCard().getRight();
        this.origB = h.getCard().getBottom();
        this.dragColW = getWidth() / 4;
        this.dragGridRows = gridRows();
        this.dragRowH = (getHeight() - this.editInsetTop) / this.dragGridRows;
        this.downX = ev.getX();
        this.downY = ev.getY();
        this.moved = false;
        this.mode = (ev.getX() <= ((float) (this.origR - dp(30))) || ev.getY() <= ((float) (this.origB - dp(30)))) ? 1 : 2;
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        FrameLayout card;
        Intrinsics.checkNotNullParameter(ev, "ev");
        if (this.editing && (card = this.draggingCard) != null) {
            DashTile tile = this.dragTile;
            if (tile == null) {
                return false;
            }
            switch (ev.getActionMasked()) {
                case 1:
                case 3:
                    if (this.moved) {
                        if (this.mode == 1) {
                            tile.setCol(RangesKt.coerceIn(Math.round(card.getLeft() / this.dragColW), 0, 4 - tile.getW()));
                            tile.setRow(RangesKt.coerceAtLeast(Math.round((card.getTop() - this.editInsetTop) / this.dragRowH), 0));
                        } else {
                            tile.setW(RangesKt.coerceIn(Math.round((card.getRight() - card.getLeft()) / this.dragColW), 1, 4 - tile.getCol()));
                            tile.setH(RangesKt.coerceAtLeast(Math.round((card.getBottom() - card.getTop()) / this.dragRowH), 1));
                        }
                        pack(tile);
                        Function0<Unit> function0 = this.onChanged;
                        if (function0 != null) {
                            function0.invoke();
                        }
                    } else {
                        Function1<? super DashTile, Unit> function1 = this.onPickMetric;
                        if (function1 != null) {
                            function1.invoke(tile);
                        }
                    }
                    this.draggingCard = null;
                    this.dragTile = null;
                    ViewParent parent = getParent();
                    if (parent != null) {
                        parent.requestDisallowInterceptTouchEvent(false);
                    }
                    if (this.moved) {
                        rebuild();
                    } else {
                        requestLayout();
                    }
                    return true;
                case 2:
                    float dx = ev.getX() - this.downX;
                    float dy = ev.getY() - this.downY;
                    if (Math.abs(dx) + Math.abs(dy) > dp(8)) {
                        this.moved = true;
                    }
                    if (this.mode == 1) {
                        card.layout((int) (this.origL + dx), (int) (this.origT + dy), (int) (this.origR + dx), (int) (this.origB + dy));
                        int hc = RangesKt.coerceIn(Math.round(card.getLeft() / this.dragColW), 0, 4 - tile.getW());
                        int hr = RangesKt.coerceIn(Math.round((card.getTop() - this.editInsetTop) / this.dragRowH), 0, RangesKt.coerceAtLeast(this.dragGridRows - tile.getH(), 0));
                        if (hc != tile.getCol() || hr != tile.getRow()) {
                            tile.setCol(hc);
                            tile.setRow(hr);
                            pack(tile);
                            reflowExceptDragged();
                        }
                    } else {
                        card.layout(this.origL, this.origT, RangesKt.coerceAtLeast((int) (this.origR + dx), this.origL + dp(48)), RangesKt.coerceAtLeast((int) (this.origB + dy), this.origT + dp(48)));
                        int nw = RangesKt.coerceIn(Math.round((card.getRight() - card.getLeft()) / this.dragColW), 1, 4 - tile.getCol());
                        int nh = RangesKt.coerceAtLeast(Math.round((card.getBottom() - card.getTop()) / this.dragRowH), 1);
                        if (nw != tile.getW() || nh != tile.getH()) {
                            tile.setW(nw);
                            tile.setH(nh);
                            pack(tile);
                            reflowExceptDragged();
                        }
                    }
                    return true;
                default:
                    return true;
            }
        }
        return super.onTouchEvent(ev);
    }

    private final DashTile tileAt(float x, float y) {
        for (DashTile tile : this.tiles) {
            Holder h = this.holders.get(tile);
            if (h != null && x >= h.getCard().getLeft() && x <= h.getCard().getRight() && y >= h.getCard().getTop() && y <= h.getCard().getBottom()) {
                return tile;
            }
        }
        return null;
    }
}

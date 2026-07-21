package com.bike.computer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;

public final class RidesActivity extends Activity {
    private LinearLayout container;
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("EEE d MMM · h:mm a", Locale.US);

    @Override // android.app.Activity
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list);
        ImmersiveKt.enterImmersive(this);
        this.container = (LinearLayout) findViewById(R.id.list_container);
        ((TextView) findViewById(R.id.list_title)).setText("Rides");
        ((TextView) findViewById(R.id.list_done)).setVisibility(8);
        TextView $this$onCreate_u24lambda_u241 = (TextView) findViewById(R.id.list_back);
        $this$onCreate_u24lambda_u241.setVisibility(0);
        $this$onCreate_u24lambda_u241.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.RidesActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RidesActivity.this.finish();
            }
        });
        build();
        new Thread(new Runnable() { // from class: com.bike.computer.RidesActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                RidesActivity.onCreate$lambda$3(RidesActivity.this);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$3(final RidesActivity this$0) {
        if (RideHistory.INSTANCE.reconcile()) {
            this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.RidesActivity$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    RidesActivity.onCreate$lambda$3$lambda$2(this$0);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$3$lambda$2(RidesActivity this$0) {
        if (this$0.isFinishing()) {
            return;
        }
        this$0.build();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImmersiveKt.enterImmersive(this);
        }
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        build();
    }

    private final void build() {
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.removeAllViews();
        List<RideSummary> listAll = RideHistory.INSTANCE.all();
        if (listAll.isEmpty()) {
            hint("No rides yet — finish a ride to see it here.");
            return;
        }
        Map<String, RideSummary> mapRouteBests = RideHistory.INSTANCE.routeBests();
        if (!mapRouteBests.isEmpty()) {
            header("Best times");
            Iterable<String> $this$sortedBy$iv = mapRouteBests.keySet();
            for (String route : CollectionsKt.sortedWith($this$sortedBy$iv, new Comparator<String>() { // from class: com.bike.computer.RidesActivity$build$$inlined$sortedBy$1
                /* JADX WARN: Multi-variable type inference failed */
                @Override // java.util.Comparator
                public final int compare(String t, String t2) {
                    String it = (String) t;
                    String lowerCase = it.toLowerCase(Locale.ROOT);
                    Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                    String it2 = (String) t2;
                    String lowerCase2 = it2.toLowerCase(Locale.ROOT);
                    Intrinsics.checkNotNullExpressionValue(lowerCase2, "toLowerCase(...)");
                    return ComparisonsKt.compareValues(lowerCase, lowerCase2);
                }
            })) {
                RideSummary rideSummary = mapRouteBests.get(route);
                Intrinsics.checkNotNull(rideSummary);
                final RideSummary best = rideSummary;
                int n = RideHistory.INSTANCE.forRoute(route).size();
                row(route, n + " ride" + (n == 1 ? "" : "s"), fmtDur(best.getMovingMs()), "🏆", new Function0() { // from class: com.bike.computer.RidesActivity$$ExternalSyntheticLambda4
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return RidesActivity.build$lambda$5(RidesActivity.this, best);
                    }
                });
            }
        }
        header("All rides");
        for (final RideSummary r : listAll) {
            String name = r.getName();
            if (name == null && (name = r.getRoute()) == null) {
                name = "Free ride";
            }
            String title = name;
            String sub = this.dateFmt.format(new Date(r.getStartMs()));
            String right = Units.INSTANCE.fmtDist(r.getDistanceM()) + " mi · " + fmtDur(r.getMovingMs());
            Intrinsics.checkNotNull(sub);
            row(title, sub, right, null, new Function0() { // from class: com.bike.computer.RidesActivity$$ExternalSyntheticLambda5
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return RidesActivity.build$lambda$6(RidesActivity.this, r);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit build$lambda$5(RidesActivity this$0, RideSummary $best) {
        this$0.openSummary($best.getStartMs());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit build$lambda$6(RidesActivity this$0, RideSummary $r) {
        this$0.openSummary($r.getStartMs());
        return Unit.INSTANCE;
    }

    private final void openSummary(long startMs) {
        startActivity(new Intent(this, (Class<?>) RideSummaryActivity.class).putExtra("startMs", startMs));
    }

    private final int dp(int v) {
        return (int) TypedValue.applyDimension(1, v, getResources().getDisplayMetrics());
    }

    private final void hint(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(Color.parseColor("#FF9E9E9E"));
        t.setTextSize(15.0f);
        t.setPadding(dp(4), dp(8), dp(4), dp(8));
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(t);
    }

    private final void header(String title) {
        TextView t = new TextView(this);
        String upperCase = title.toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(upperCase, "toUpperCase(...)");
        t.setText(upperCase);
        t.setTextColor(Color.parseColor("#FF8E8E93"));
        t.setTextSize(13.0f);
        t.setTypeface(Typeface.create("sans-serif-medium", 0));
        t.setLetterSpacing(0.06f);
        t.setPadding(dp(4), dp(20), dp(4), dp(8));
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(t);
    }

    private final void row(String title, String sub, String right, String badge, final Function0<Unit> onClick) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(0);
        c.setGravity(16);
        c.setBackgroundResource(R.drawable.card_solid);
        c.setPadding(dp(16), dp(14), dp(16), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(dp(2), dp(4), dp(2), dp(4));
        c.setLayoutParams(lp);
        c.setClickable(true);
        c.setFocusable(true);
        c.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.RidesActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                onClick.invoke();
            }
        });
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(1);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextColor(-1);
        t.setTextSize(17.0f);
        t.setMaxLines(1);
        t.setEllipsize(TextUtils.TruncateAt.END);
        TextView subv = new TextView(this);
        subv.setText(sub);
        subv.setTextColor(Color.parseColor("#FF8E8E93"));
        subv.setTextSize(13.0f);
        col.addView(t);
        col.addView(subv);
        TextView r = new TextView(this);
        r.setText(badge != null ? badge + " " + right : right);
        r.setTextColor(Color.parseColor("#FFE8E8EA"));
        r.setTextSize(16.0f);
        r.setTypeface(Typeface.create("sans-serif-medium", 0));
        r.setPadding(dp(10), 0, 0, 0);
        c.addView(col);
        c.addView(r);
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(c);
    }

    private final String fmtDur(long ms) {
        long s = RangesKt.coerceAtLeast(ms / ((long) 1000), 0L);
        long j = 3600;
        long h = s / j;
        long j2 = 60;
        long m = (s % j) / j2;
        long sec = s % j2;
        if (h > 0) {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            String str = String.format(Locale.US, "%d:%02d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(h), Long.valueOf(m), Long.valueOf(sec)}, 3));
            Intrinsics.checkNotNullExpressionValue(str, "format(...)");
            return str;
        }
        StringCompanionObject stringCompanionObject2 = StringCompanionObject.INSTANCE;
        String str2 = String.format(Locale.US, "%d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(m), Long.valueOf(sec)}, 2));
        Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
        return str2;
    }
}

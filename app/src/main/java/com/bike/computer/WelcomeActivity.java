package com.bike.computer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.DebugKt;

public final class WelcomeActivity extends Activity {
    private static final Companion Companion = new Companion(null);
    private static volatile boolean autoSynced;
    private LinearLayout container;
    private TextView gpsDot;
    private TextView gpsVal;
    private TextView hrDot;
    private View hrRow;
    private TextView hrVal;
    private TextView pwrDot;
    private View pwrRow;
    private TextView pwrVal;
    RideService ride;
    private final String ROUTES_DIR = "/sdcard/BikeComputer/routes";
    private final int RECENT_COUNT = 5;
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final WelcomeActivity$conn$1 conn = new WelcomeActivity$conn$1(this);

    /* JADX INFO: compiled from: WelcomeActivity.kt */
    private static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final boolean getAutoSynced() {
            return WelcomeActivity.autoSynced;
        }

        public final void setAutoSynced(boolean z) {
            WelcomeActivity.autoSynced = z;
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_welcome);
        getWindow().addFlags(128);
        ImmersiveKt.enterImmersive(this);
        this.container = (LinearLayout) findViewById(R.id.welcome_container);
        findViewById(R.id.welcome_settings).setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                WelcomeActivity.onCreate$lambda$0(WelcomeActivity.this, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$0(WelcomeActivity this$0, View it) {
        this$0.startActivity(new Intent(this$0, (Class<?>) SettingsActivity.class));
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, (Class<?>) RideService.class), this.conn, 1);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (AppState.INSTANCE.getOnMap()) {
            startActivity(new Intent(this, (Class<?>) MainActivity.class));
            overridePendingTransition(0, 0);
        } else {
            build();
            tick();
            maybeAutoSync();
        }
    }

    private final void maybeAutoSync() {
        if (autoSynced) {
            return;
        }
        autoSynced = true;
        new Thread(new Runnable() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                WelcomeActivity.maybeAutoSync$lambda$4(WelcomeActivity.this);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void maybeAutoSync$lambda$4(final WelcomeActivity this$0) {
        Object objM118constructorimpl;
        try {
            Boolean.valueOf(RideHistory.INSTANCE.reconcile());
        } catch (Throwable th) {
        }
        if (Prefs.INSTANCE.driveConnected(this$0)) {
            try {
                objM118constructorimpl = Integer.valueOf(GoogleDriveClient.INSTANCE.syncRoutes(this$0, this$0.ROUTES_DIR));
            } catch (Throwable th2) {
                objM118constructorimpl = 0;
            }
            final int n = ((Number) objM118constructorimpl).intValue();
            if (n > 0) {
                this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        WelcomeActivity.maybeAutoSync$lambda$4$lambda$3(this$0, n);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void maybeAutoSync$lambda$4$lambda$3(WelcomeActivity this$0, int $n) {
        if (this$0.isFinishing()) {
            return;
        }
        this$0.build();
        this$0.toast("Synced " + $n + " route" + ($n == 1 ? "" : "s") + " from Drive");
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImmersiveKt.enterImmersive(this);
        }
    }

    @Override // android.app.Activity
    protected void onPause() {
        this.ui.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onStop() {
        try {
            unbindService(this.conn);
        } catch (Exception e) {
        }
        super.onStop();
    }

    private final int dp(int v) {
        return (int) TypedValue.applyDimension(1, v, getResources().getDisplayMetrics());
    }

    private final void toast(String s) {
        Toast.makeText(this, s, 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void tick() {
        refreshStatus();
        this.ui.postDelayed(new Runnable() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WelcomeActivity.this.tick();
            }
        }, 1000L);
    }

    private final void build() {
        Iterable iterableEmptyList;
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.removeAllViews();
        menuCard("Navigate", R.drawable.ic_search, new Function0() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda3
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return WelcomeActivity.build$lambda$6(WelcomeActivity.this);
            }
        });
        File[] fileArrListFiles = new File(this.ROUTES_DIR).listFiles(new FileFilter() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda4
            @Override // java.io.FileFilter
            public final boolean accept(File file) {
                return WelcomeActivity.build$lambda$7(file);
            }
        });
        if (fileArrListFiles == null || (iterableEmptyList = ArraysKt.toList(fileArrListFiles)) == null) {
            iterableEmptyList = CollectionsKt.emptyList();
        }
        Iterable all = iterableEmptyList;
        Iterable $this$associateBy$iv = all;
        int capacity$iv = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault($this$associateBy$iv, 10)), 16);
        Map destination$iv$iv = new LinkedHashMap(capacity$iv);
        for (Object element$iv$iv : $this$associateBy$iv) {
            File it = (File) element$iv$iv;
            Intrinsics.checkNotNull(it);
            destination$iv$iv.put(FilesKt.getNameWithoutExtension(it), element$iv$iv);
        }
        Iterable $this$mapNotNull$iv = Prefs.INSTANCE.starredRoutes(this);
        Collection destination$iv$iv2 = new ArrayList();
        for (Object element$iv$iv$iv : $this$mapNotNull$iv) {
            File file = (File) destination$iv$iv.get((String) element$iv$iv$iv);
            if (file != null) {
                destination$iv$iv2.add(file);
            }
        }
        Iterable $this$sortedBy$iv = (List) destination$iv$iv2;
        List starred = CollectionsKt.sortedWith($this$sortedBy$iv, new Comparator() { // from class: com.bike.computer.WelcomeActivity$build$$inlined$sortedBy$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(Object t, Object t2) {
                File it2 = (File) t;
                String lowerCase = FilesKt.getNameWithoutExtension(it2).toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                File it3 = (File) t2;
                String lowerCase2 = FilesKt.getNameWithoutExtension(it3).toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase2, "toLowerCase(...)");
                return ComparisonsKt.compareValues(lowerCase, lowerCase2);
            }
        });
        List $this$map$iv = starred;
        Collection destination$iv$iv3 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            destination$iv$iv3.add(FilesKt.getNameWithoutExtension((File) item$iv$iv));
        }
        Set starredSet = CollectionsKt.toSet((List) destination$iv$iv3);
        Iterable $this$filter$iv = all;
        Collection destination$iv$iv4 = new ArrayList();
        for (Object element$iv$iv2 : $this$filter$iv) {
            File it2 = (File) element$iv$iv2;
            Intrinsics.checkNotNull(it2);
            if (!starredSet.contains(FilesKt.getNameWithoutExtension(it2))) {
                destination$iv$iv4.add(element$iv$iv2);
            }
        }
        Iterable $this$sortedByDescending$iv = (List) destination$iv$iv4;
        List<File> recent = CollectionsKt.take(CollectionsKt.sortedWith($this$sortedByDescending$iv, new Comparator() { // from class: com.bike.computer.WelcomeActivity$build$$inlined$sortedByDescending$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(Object t, Object t2) {
                File it3 = (File) t2;
                File it4 = (File) t;
                return ComparisonsKt.compareValues(Long.valueOf(it3.lastModified()), Long.valueOf(it4.lastModified()));
            }
        }), this.RECENT_COUNT);
        if (!starred.isEmpty()) {
            sectionLabel("STARRED");
            Iterator it3 = starred.iterator();
            while (it3.hasNext()) {
                routeRow((File) it3.next(), true);
            }
        }
        sectionLabel(starred.isEmpty() ? "ROUTES" : "RECENT");
        if (recent.isEmpty() && starred.isEmpty()) {
            hint("No saved routes yet — add them in Settings ▸ Routes.");
        } else if (recent.isEmpty()) {
            hint("No other recent routes.");
        }
        for (File f : recent) {
            Intrinsics.checkNotNull(f);
            routeRow(f, false);
        }
        menuCard$default(this, "🏁  Ride history", 0, new Function0() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda5
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return WelcomeActivity.build$lambda$14(WelcomeActivity.this);
            }
        }, 2, null);
        sectionLabel("SENSORS");
        Triple<View, TextView, TextView> tripleStatusRow = statusRow("GPS");
        TextView dot = tripleStatusRow.component2();
        TextView value = tripleStatusRow.component3();
        this.gpsDot = dot;
        this.gpsVal = value;
        Triple<View, TextView, TextView> tripleStatusRow2 = statusRow("Heart rate");
        View row = tripleStatusRow2.component1();
        TextView dot2 = tripleStatusRow2.component2();
        TextView value2 = tripleStatusRow2.component3();
        this.hrRow = row;
        this.hrDot = dot2;
        this.hrVal = value2;
        Triple<View, TextView, TextView> tripleStatusRow3 = statusRow("Power / cadence");
        View row2 = tripleStatusRow3.component1();
        TextView dot3 = tripleStatusRow3.component2();
        TextView value3 = tripleStatusRow3.component3();
        this.pwrRow = row2;
        this.pwrDot = dot3;
        this.pwrVal = value3;
        refreshStatus();
        startButton();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit build$lambda$6(WelcomeActivity this$0) {
        this$0.startActivity(new Intent(this$0, (Class<?>) DestinationSearchActivity.class));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean build$lambda$7(File f) {
        String name = f.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        return StringsKt.endsWith(name, ".gpx", false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit build$lambda$14(WelcomeActivity this$0) {
        this$0.startActivity(new Intent(this$0, (Class<?>) RidesActivity.class));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0047  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void refreshStatus() {
        boolean z;
        String strPrettyStatus;
        String strPrettyStatus2;
        RideService r = this.ride;
        boolean pwrShow = true;
        boolean fix = (r != null ? r.getLastLocation() : null) != null;
        setStatus(this.gpsDot, this.gpsVal, fix, fix ? "Fix acquired" : "Searching…", !fix);
        boolean hrOn = r != null && r.getHrConnected();
        if (hrOn) {
            z = true;
        } else {
            if ((r != null ? r.getHrDeviceName() : null) == null) {
                z = false;
            } else {
                z = true;
            }
        }
        boolean hrShow = z;
        View view = this.hrRow;
        if (view != null) {
            view.setVisibility(hrShow ? 0 : 8);
        }
        if (hrShow) {
            TextView textView = this.hrDot;
            TextView textView2 = this.hrVal;
            if (hrOn) {
                strPrettyStatus2 = (r != null ? r.getLastHr() : 0) + " bpm";
            } else {
                strPrettyStatus2 = prettyStatus(r != null ? r.getHrStatus() : null);
            }
            setStatus(textView, textView2, hrOn, strPrettyStatus2, false);
        }
        boolean pwrOn = r != null && r.getCyclingConnected();
        if (!pwrOn) {
            if ((r != null ? r.getCyclingDeviceName() : null) == null) {
                pwrShow = false;
            }
        }
        View view2 = this.pwrRow;
        if (view2 != null) {
            view2.setVisibility(pwrShow ? 0 : 8);
        }
        if (pwrShow) {
            TextView textView3 = this.pwrDot;
            TextView textView4 = this.pwrVal;
            if (pwrOn) {
                strPrettyStatus = (r != null ? r.getCurPower() : 0) + " W · " + (r != null ? r.getCurCadence() : 0) + " rpm";
            } else {
                strPrettyStatus = prettyStatus(r != null ? r.getCyclingStatus() : null);
            }
            setStatus(textView3, textView4, pwrOn, strPrettyStatus, false);
        }
    }

    private final String prettyStatus(String s) {
        if (s == null || StringsKt.startsWith(s, "connecting", false)) {
            return "Connecting…";
        }
        if (Intrinsics.areEqual(s, "live") || Intrinsics.areEqual(s, "connected")) {
            return "Connected";
        }
        return Intrinsics.areEqual(s, "BT off") ? "Bluetooth off" : StringsKt.startsWith(s, "scanning", false) ? "Scanning…" : Intrinsics.areEqual(s, "disconnected") ? "Disconnected" : s;
    }

    private final void setStatus(TextView dot, TextView value, boolean on, String text, boolean searching) {
        int color = on ? -13577896 : searching ? -24822 : -7434605;
        if (dot != null) {
            dot.setTextColor(color);
        }
        if (value != null) {
            value.setText(text);
        }
        if (value != null) {
            value.setTextColor(on ? -1513238 : -6381922);
        }
    }

    private final void sectionLabel(String title) {
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextColor(Color.parseColor("#FF8E8E93"));
        t.setTextSize(13.0f);
        t.setTypeface(Typeface.create("sans-serif-medium", 0));
        t.setLetterSpacing(0.08f);
        t.setPadding(dp(10), dp(18), dp(10), dp(6));
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(t);
    }

    private final void hint(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(Color.parseColor("#FF9E9E9E"));
        t.setTextSize(14.0f);
        t.setPadding(dp(10), dp(2), dp(10), dp(8));
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(t);
    }

    private final LinearLayout card() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(0);
        l.setGravity(16);
        l.setBackgroundResource(R.drawable.card_solid);
        l.setPadding(dp(16), dp(14), dp(16), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        l.setLayoutParams(lp);
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(l);
        return l;
    }

    private final Triple<View, TextView, TextView> statusRow(String label) {
        LinearLayout c = card();
        TextView dot = new TextView(this);
        dot.setText("●");
        dot.setTextSize(13.0f);
        dot.setTextColor(-7434605);
        dot.setPadding(0, 0, dp(12), 0);
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextColor(-1);
        t.setTextSize(17.0f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView v = new TextView(this);
        v.setText("…");
        v.setTextColor(Color.parseColor("#FF9E9E9E"));
        v.setTextSize(15.0f);
        c.addView(dot);
        c.addView(t);
        c.addView(v);
        return new Triple<>(c, dot, v);
    }

    static /* synthetic */ void menuCard$default(WelcomeActivity welcomeActivity, String str, int i, Function0 function0, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        welcomeActivity.menuCard(str, i, function0);
    }

    private final void menuCard(String title, int iconRes, final Function0<Unit> onClick) {
        LinearLayout c = card();
        if (iconRes != 0) {
            ImageView ic = new ImageView(this);
            ic.setImageResource(iconRes);
            ic.setColorFilter(Color.parseColor("#FFB8B8BD"));
            LinearLayout.LayoutParams $this$menuCard_u24lambda_u2418 = new LinearLayout.LayoutParams(dp(22), dp(22));
            $this$menuCard_u24lambda_u2418.rightMargin = dp(12);
            ic.setLayoutParams($this$menuCard_u24lambda_u2418);
            c.addView(ic);
        }
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextColor(Color.parseColor("#FFE8E8EA"));
        t.setTextSize(17.0f);
        t.setTypeface(Typeface.create("sans-serif-medium", 0));
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView chev = new TextView(this);
        chev.setText("›");
        chev.setTextColor(Color.parseColor("#FF6E6E6E"));
        chev.setTextSize(22.0f);
        c.addView(t);
        c.addView(chev);
        c.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                onClick.invoke();
            }
        });
    }

    private final void startButton() {
        GradientDrawable $this$startButton_u24lambda_u2420 = new GradientDrawable();
        $this$startButton_u24lambda_u2420.setCornerRadius(dp(16));
        $this$startButton_u24lambda_u2420.setColor(Color.parseColor("#FF30D158"));
        TextView b = new TextView(this);
        b.setText("▶   Start ride");
        b.setTextColor(-1);
        b.setTextSize(21.0f);
        b.setTypeface(Typeface.create("sans-serif-medium", 1));
        b.setGravity(17);
        b.setBackground($this$startButton_u24lambda_u2420);
        b.setPadding(0, dp(20), 0, dp(20));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(dp(4), dp(16), dp(4), dp(6));
        b.setLayoutParams(lp);
        b.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                WelcomeActivity.startButton$lambda$21(WelcomeActivity.this, view);
            }
        });
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(b);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void startButton$lambda$21(WelcomeActivity this$0, View it) {
        this$0.startActivity(new Intent(this$0, (Class<?>) MainActivity.class));
    }

    private final void routeRow(final File f, final boolean starredNow) {
        LinearLayout c = card();
        final String name = FilesKt.getNameWithoutExtension(f);
        TextView t = new TextView(this);
        t.setText(name);
        t.setTextColor(-1);
        t.setTextSize(16.0f);
        t.setMaxLines(1);
        t.setEllipsize(TextUtils.TruncateAt.END);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        c.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                WelcomeActivity.this.rideRoute(f);
            }
        });
        TextView star = new TextView(this);
        star.setText(starredNow ? "★" : "☆");
        star.setTextColor(Color.parseColor(starredNow ? "#FFFFD60A" : "#FF6E6E6E"));
        star.setTextSize(22.0f);
        star.setPadding(dp(14), dp(2), dp(6), dp(2));
        star.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.WelcomeActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                WelcomeActivity.routeRow$lambda$23(WelcomeActivity.this, name, starredNow, view);
            }
        });
        c.addView(t);
        c.addView(star);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void routeRow$lambda$23(WelcomeActivity this$0, String $name, boolean $starredNow, View it) {
        Prefs.INSTANCE.setRouteStarred(this$0, $name, !$starredNow);
        this$0.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void rideRoute(File f) {
        Object objM118constructorimpl;
        try {
            objM118constructorimpl = FilesKt.readText(f, kotlin.text.Charsets.UTF_8);
        } catch (Throwable th) {
            objM118constructorimpl = null;
        }
        String gpx = (String) objM118constructorimpl;
        if (gpx == null) {
            toast("Couldn't read route");
            return;
        }
        List<double[]> list = GpxRoute.INSTANCE.parse(gpx);
        if (list.size() < 2) {
            toast("No route points found");
            return;
        }
        ActionBus.INSTANCE.setPendingRoute(GpxRoute.toWaypoints$default(GpxRoute.INSTANCE, list, 0.0d, 0, 6, null));
        ActionBus.INSTANCE.setPendingRouteName(FilesKt.getNameWithoutExtension(f));
        startActivity(new Intent(this, (Class<?>) MainActivity.class));
    }
}

package com.bike.computer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.role.RoleManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.bike.computer.RideService;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: SettingsActivity.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000\u008b\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0014\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0018\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u001a\u001a\u00020\u00122\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0014J\b\u0010\u001d\u001a\u00020\u0012H\u0016J\b\u0010\u001e\u001a\u00020\u0012H\u0002J\u0010\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u0005H\u0002J\u0010\u0010!\u001a\u00020\u00122\u0006\u0010\"\u001a\u00020#H\u0016J\b\u0010$\u001a\u00020\u0012H\u0014J\b\u0010%\u001a\u00020\u0012H\u0014J\b\u0010&\u001a\u00020\u0012H\u0014J\b\u0010'\u001a\u00020\u0012H\u0014J\b\u0010(\u001a\u00020\u0012H\u0002J\b\u0010)\u001a\u00020\u0012H\u0002J\u0010\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020+H\u0002J\b\u0010-\u001a\u00020\u0012H\u0002J\"\u0010.\u001a\u00020\u00122\u0006\u0010/\u001a\u00020+2\u0006\u00100\u001a\u00020+2\b\u00101\u001a\u0004\u0018\u000102H\u0014J\b\u00103\u001a\u00020\u0012H\u0002J\b\u00104\u001a\u00020\u0012H\u0002J\b\u00105\u001a\u00020\u0012H\u0002J\b\u00106\u001a\u00020\u0012H\u0002J(\u00107\u001a\u00020\u00122\u0006\u0010,\u001a\u00020\f2\u0006\u00108\u001a\u00020#2\u0006\u00109\u001a\u00020\u00052\u0006\u0010:\u001a\u00020\u0005H\u0002J\u0012\u0010;\u001a\u00020\u00052\b\u0010\u001b\u001a\u0004\u0018\u00010\u0005H\u0002J\u001c\u0010<\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0=2\u0006\u0010>\u001a\u00020\u0005H\u0002J\b\u0010?\u001a\u00020\u0012H\u0002J\b\u0010@\u001a\u00020\u0012H\u0002J\u0018\u0010A\u001a\u00020\u00122\u0006\u0010B\u001a\u00020\u00052\u0006\u0010C\u001a\u00020\u0005H\u0002J\b\u0010D\u001a\u00020\u0012H\u0002J\b\u0010E\u001a\u00020\u0012H\u0002J\b\u0010F\u001a\u00020\u0012H\u0002J\b\u0010G\u001a\u00020#H\u0002J\b\u0010H\u001a\u00020\u0012H\u0002J\b\u0010I\u001a\u00020\u0012H\u0002J\u001e\u0010J\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u00052\f\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u0010\u0010B\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u0005H\u0002J\b\u0010L\u001a\u00020\tH\u0002J,\u0010M\u001a\u00020\u00122\u0006\u0010N\u001a\u00020\u00052\u0006\u0010O\u001a\u00020#2\u0012\u0010P\u001a\u000e\u0012\u0004\u0012\u00020#\u0012\u0004\u0012\u00020\u00120QH\u0002J,\u0010R\u001a\u00020\u00122\u0006\u0010N\u001a\u00020\u00052\u0006\u0010O\u001a\u00020#2\u0012\u0010P\u001a\u000e\u0012\u0004\u0012\u00020#\u0012\u0004\u0012\u00020\u00120QH\u0002JL\u0010S\u001a\u00020\u00122\u0006\u0010N\u001a\u00020\u00052\u0006\u0010O\u001a\u00020+2\u0006\u0010T\u001a\u00020+2\u0006\u0010U\u001a\u00020+2\u0006\u0010V\u001a\u00020+2\u0006\u0010W\u001a\u00020\u00052\u0012\u0010P\u001a\u000e\u0012\u0004\u0012\u00020+\u0012\u0004\u0012\u00020\u00120QH\u0002J\u001e\u0010X\u001a\u00020\u00122\u0006\u0010N\u001a\u00020\u00052\f\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J\u0018\u0010Y\u001a\u00020\f2\u0006\u0010N\u001a\u00020\u00052\u0006\u0010Z\u001a\u00020\u0005H\u0002J\"\u0010[\u001a\u00020\\2\u0006\u0010N\u001a\u00020\u00052\u0006\u0010O\u001a\u00020\u00052\b\b\u0002\u0010]\u001a\u00020\u0005H\u0002JR\u0010^\u001a\u00020\u00122\u0006\u0010_\u001a\u00020\u00052\u0006\u0010>\u001a\u00020\u00052\u0006\u0010`\u001a\u00020+2\u0006\u0010a\u001a\u00020+2\f\u0010b\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010c\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\f\u0010d\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0002J$\u0010e\u001a\u00020\u00122\u0006\u0010f\u001a\u00020\u00052\u0012\u0010g\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00120QH\u0002J\u001c\u0010h\u001a\u00020\u00122\u0006\u0010i\u001a\u00020\u00052\n\b\u0002\u0010j\u001a\u0004\u0018\u00010\u0005H\u0002J\u0018\u0010k\u001a\u00020\u00122\u0006\u0010l\u001a\u00020\u00052\u0006\u0010m\u001a\u00020#H\u0002J\u0010\u0010n\u001a\u00020\u00122\u0006\u0010i\u001a\u00020\u0005H\u0002J\u0010\u0010o\u001a\u00020\u00122\u0006\u0010p\u001a\u00020qH\u0002J\u0010\u0010r\u001a\u00020\u00122\u0006\u0010\u001b\u001a\u00020\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082.¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u0012\u0018\u00010\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00050\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0017\u001a\u00020\u0018X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0019¨\u0006s"}, d2 = {"Lcom/bike/computer/SettingsActivity;", "Landroid/app/Activity;", "<init>", "()V", "ROUTES_DIR", "", "ride", "Lcom/bike/computer/RideService;", "container", "Landroid/widget/LinearLayout;", "target", "titleView", "Landroid/widget/TextView;", "backBtn", "Landroid/view/View;", "currentPage", "liveRefresh", "Lkotlin/Function0;", "", "ui", "Landroid/os/Handler;", "PAGES", "", "conn", "com/bike/computer/SettingsActivity$conn$1", "Lcom/bike/computer/SettingsActivity$conn$1;", "onCreate", "s", "Landroid/os/Bundle;", "onBackPressed", "goMenu", "openPage", "title", "onWindowFocusChanged", "hasFocus", "", "onStart", "onResume", "onPause", "onStop", "startTick", "tick", "dp", "", "v", "rebuild", "onActivityResult", "req", "res", "data", "Landroid/content/Intent;", "build", "pagePages", "pageRecording", "pageSensors", "setSensorValue", "connected", "live", NotificationCompat.CATEGORY_STATUS, "prettyStatus", "sensorRow", "Lkotlin/Pair;", "name", "pageRoutes", "createLinksSheet", "copyText", "text", "done", "syncDriveRoutes", "pageExport", "pageSystem", "isDefaultHome", "openHomeSettings", "launchOtherLauncher", "menuRow", "onClick", "card", "switchRow", "label", "initial", "onChange", "Lkotlin/Function1;", "checkRow", "stepperRow", "min", "max", "step", "suffix", "button", "valueRow", "value", "editRow", "Landroid/widget/EditText;", "hint", "pageRow", "key", "index", "count", "onUp", "onDown", "onRemove", "withDownloadedRoute", "url", "onReady", "navigateGpx", "gpx", "routeName", "withGmapsRoute", "link", "save", "promptSaveRoute", "savedRouteRow", "f", "Ljava/io/File;", "toast", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class SettingsActivity extends Activity {
    private View backBtn;
    private LinearLayout container;
    private String currentPage;
    private Function0<Unit> liveRefresh;
    private RideService ride;
    private LinearLayout target;
    private TextView titleView;
    private final String ROUTES_DIR = "/sdcard/BikeComputer/routes";
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final List<String> PAGES = CollectionsKt.listOf((Object[]) new String[]{"Pages", "Recording", "Sensors", "Routes", "Google Drive", "Home & system"});
    private final SettingsActivity$conn$1 conn = new ServiceConnection() { // from class: com.bike.computer.SettingsActivity$conn$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName n, IBinder b) {
            SettingsActivity settingsActivity = this.this$0;
            Intrinsics.checkNotNull(b, "null cannot be cast to non-null type com.bike.computer.RideService.LocalBinder");
            settingsActivity.ride = ((RideService.LocalBinder) b).getThis$0();
            Function0 function0 = this.this$0.liveRefresh;
            if (function0 != null) {
                function0.invoke();
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName n) {
            this.this$0.ride = null;
        }
    };

    @Override // android.app.Activity
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_settings);
        ImmersiveKt.enterImmersive(this);
        this.container = (LinearLayout) findViewById(R.id.settings_container);
        this.titleView = (TextView) findViewById(R.id.settings_title);
        this.backBtn = findViewById(R.id.back_btn);
        View view = this.backBtn;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("backBtn");
            view = null;
        }
        view.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda34
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.goMenu();
            }
        });
        findViewById(R.id.done_btn).setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda35
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.finish();
            }
        });
        build();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.currentPage != null) {
            goMenu();
        } else {
            super.onBackPressed();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void goMenu() {
        this.currentPage = null;
        rebuild();
    }

    private final void openPage(String title) {
        this.currentPage = title;
        rebuild();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImmersiveKt.enterImmersive(this);
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, (Class<?>) RideService.class), this.conn, 1);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.liveRefresh != null) {
            startTick();
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

    private final void startTick() {
        this.ui.removeCallbacksAndMessages(null);
        tick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void tick() {
        Function0<Unit> function0 = this.liveRefresh;
        if (function0 != null) {
            function0.invoke();
            this.ui.postDelayed(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.tick();
                }
            }, 1000L);
        }
    }

    private final int dp(int v) {
        return (int) TypedValue.applyDimension(1, v, getResources().getDisplayMetrics());
    }

    private final void rebuild() {
        LinearLayout linearLayout = null;
        this.liveRefresh = null;
        LinearLayout linearLayout2 = this.container;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
        } else {
            linearLayout = linearLayout2;
        }
        linearLayout.removeAllViews();
        build();
    }

    @Override // android.app.Activity
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req != 43 || res != -1) {
            return;
        }
        rebuild();
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private final void build() {
        LinearLayout linearLayout = this.container;
        View view = null;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        this.target = linearLayout;
        String page = this.currentPage;
        TextView textView = this.titleView;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("titleView");
            textView = null;
        }
        textView.setText(page != null ? page : "Settings");
        View view2 = this.backBtn;
        if (view2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("backBtn");
        } else {
            view = view2;
        }
        view.setVisibility(page == null ? 8 : 0);
        if (page == null) {
            for (final String p : this.PAGES) {
                menuRow(p, new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda56
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return SettingsActivity.build$lambda$4(this.f$0, p);
                    }
                });
            }
            menuRow("Rides", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda57
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.build$lambda$5(this.f$0);
                }
            });
        }
        switch (page.hashCode()) {
            case -1927763606:
                if (page.equals("Home & system")) {
                    pageSystem();
                    break;
                }
                break;
            case -1841265814:
                if (page.equals("Routes")) {
                    pageRoutes();
                    break;
                }
                break;
            case -1297441327:
                if (page.equals("Recording")) {
                    pageRecording();
                    break;
                }
                break;
            case -649937959:
                if (page.equals("Sensors")) {
                    pageSensors();
                    break;
                }
                break;
            case 76873636:
                if (page.equals("Pages")) {
                    pagePages();
                    break;
                }
                break;
            case 825368803:
                if (page.equals("Google Drive")) {
                    pageExport();
                    break;
                }
                break;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit build$lambda$4(SettingsActivity this$0, String $p) {
        this$0.openPage($p);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit build$lambda$5(SettingsActivity this$0) {
        this$0.startActivity(new Intent(this$0, (Class<?>) RidesActivity.class));
        return Unit.INSTANCE;
    }

    private final void pagePages() {
        String strFixedTitle;
        text("Reorder or hide the swipe pages, and add data pages. Map can't be hidden.");
        final List order = CollectionsKt.toMutableList((Collection) Prefs.INSTANCE.pageOrder(this));
        List $this$filter$iv = order;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$filter$iv) {
            String it = (String) element$iv$iv;
            if (Pages.INSTANCE.isData(it)) {
                destination$iv$iv.add(element$iv$iv);
            }
        }
        List dataKeys = (List) destination$iv$iv;
        List $this$forEachIndexed$iv = order;
        int index$iv = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            int index$iv2 = index$iv + 1;
            if (index$iv < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            final String key = (String) item$iv;
            final int i = index$iv;
            if (!Pages.INSTANCE.isData(key)) {
                strFixedTitle = Pages.INSTANCE.fixedTitle(key);
            } else if (dataKeys.size() > 1) {
                strFixedTitle = "Data " + (dataKeys.indexOf(key) + 1);
            } else {
                strFixedTitle = "Data";
            }
            String name = strFixedTitle;
            pageRow(key, name, i, order.size(), new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda38
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pagePages$lambda$10$lambda$7(order, i, key, this);
                }
            }, new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda39
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pagePages$lambda$10$lambda$8(order, i, key, this);
                }
            }, new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda40
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pagePages$lambda$10$lambda$9(this.f$0, key);
                }
            });
            index$iv = index$iv2;
        }
        button("＋ Add data page", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda41
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pagePages$lambda$11(this.f$0);
            }
        });
        text("Long-press a Data page to edit it — drag boxes to move, drag a corner to resize, tap a box to change its metric, and use ＋ Add box.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pagePages$lambda$10$lambda$7(List $order, int $i, String $key, SettingsActivity this$0) {
        $order.remove($i);
        $order.add($i - 1, $key);
        Prefs.INSTANCE.setPageOrder(this$0, $order);
        this$0.rebuild();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pagePages$lambda$10$lambda$8(List $order, int $i, String $key, SettingsActivity this$0) {
        $order.remove($i);
        $order.add($i + 1, $key);
        Prefs.INSTANCE.setPageOrder(this$0, $order);
        this$0.rebuild();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pagePages$lambda$10$lambda$9(SettingsActivity this$0, String $key) {
        Prefs.INSTANCE.removeDataPage(this$0, $key);
        this$0.rebuild();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pagePages$lambda$11(SettingsActivity this$0) {
        Prefs.INSTANCE.addDataPage(this$0);
        this$0.rebuild();
        return Unit.INSTANCE;
    }

    private final void pageRecording() {
        switchRow("Auto-pause when stopped", Prefs.INSTANCE.autoPause(this), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRecording$lambda$12(this.f$0, ((Boolean) obj).booleanValue());
            }
        });
        switchRow("Voice & beep turn cues", Prefs.INSTANCE.voice(this), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda11
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRecording$lambda$13(this.f$0, ((Boolean) obj).booleanValue());
            }
        });
        switchRow("Heart-rate zone LED", Prefs.INSTANCE.led(this), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda22
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRecording$lambda$14(this.f$0, ((Boolean) obj).booleanValue());
            }
        });
        switchRow("Close other apps when recording", Prefs.INSTANCE.closeApps(this), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda33
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRecording$lambda$15(this.f$0, ((Boolean) obj).booleanValue());
            }
        });
        switchRow("Low-power map (flat, saves battery)", Prefs.INSTANCE.lowPowerMap(this), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda44
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRecording$lambda$16(this.f$0, ((Boolean) obj).booleanValue());
            }
        });
        switchRow("Turn off Wi-Fi while recording", Prefs.INSTANCE.wifiOffOnRide(this), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda53
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRecording$lambda$17(this.f$0, ((Boolean) obj).booleanValue());
            }
        });
        switchRow("Endurance mode (dim + throttle)", Prefs.INSTANCE.endurance(this), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda54
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRecording$lambda$18(this.f$0, ((Boolean) obj).booleanValue());
            }
        });
        text("Endurance dims the screen and blanks it after 30s while recording (power button to wake), forces the flat map, drops the framerate, and turns the HR LED off — maximum battery life for long rides.");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRecording$lambda$12(SettingsActivity this$0, boolean v) {
        Prefs.INSTANCE.setAutoPause(this$0, v);
        RideService rideService = this$0.ride;
        if (rideService != null) {
            rideService.setAutoPauseEnabled(v);
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRecording$lambda$13(SettingsActivity this$0, boolean v) {
        Prefs.INSTANCE.setVoice(this$0, v);
        Voice.INSTANCE.setEnabled(v);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRecording$lambda$14(SettingsActivity this$0, boolean v) {
        Prefs.INSTANCE.setLed(this$0, v);
        RideService rideService = this$0.ride;
        if (rideService != null) {
            rideService.setLedEnabled(v);
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRecording$lambda$15(SettingsActivity this$0, boolean v) {
        Prefs.INSTANCE.setCloseApps(this$0, v);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRecording$lambda$16(SettingsActivity this$0, boolean v) {
        Prefs.INSTANCE.setLowPowerMap(this$0, v);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRecording$lambda$17(SettingsActivity this$0, boolean v) {
        Prefs.INSTANCE.setWifiOffOnRide(this$0, v);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRecording$lambda$18(SettingsActivity this$0, boolean v) {
        Prefs.INSTANCE.setEndurance(this$0, v);
        return Unit.INSTANCE;
    }

    private final void pageSensors() {
        Pair<TextView, TextView> pairSensorRow = sensorRow("Heart rate");
        final TextView hrName = pairSensorRow.component1();
        final TextView hrVal = pairSensorRow.component2();
        Pair<TextView, TextView> pairSensorRow2 = sensorRow("Power / cadence");
        final TextView cycName = pairSensorRow2.component1();
        final TextView cycVal = pairSensorRow2.component2();
        this.liveRefresh = new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda42
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pageSensors$lambda$19(this.f$0, hrName, hrVal, cycName, cycVal);
            }
        };
        Function0<Unit> function0 = this.liveRefresh;
        if (function0 != null) {
            function0.invoke();
        }
        startTick();
        button("Forget & rescan", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda43
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pageSensors$lambda$20(this.f$0);
            }
        });
        text("Max HR sets the 5 training zones (Z1 50% … Z5 90% of max)");
        stepperRow("Max heart rate", Prefs.INSTANCE.maxHr(this), 120, 220, 1, " bpm", new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda45
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageSensors$lambda$21(this.f$0, ((Integer) obj).intValue());
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageSensors$lambda$19(SettingsActivity this$0, TextView $hrName, TextView $hrVal, TextView $cycName, TextView $cycVal) {
        String cyclingDeviceName;
        String hrDeviceName;
        RideService r = this$0.ride;
        boolean hrOn = r != null && r.getHrConnected();
        String str = "Heart rate";
        if (hrOn && r != null && (hrDeviceName = r.getHrDeviceName()) != null) {
            str = hrDeviceName;
        }
        $hrName.setText(str);
        this$0.setSensorValue($hrVal, hrOn, (r != null ? r.getLastHr() : 0) + " bpm", this$0.prettyStatus(r != null ? r.getHrStatus() : null));
        boolean cycOn = r != null && r.getCyclingConnected();
        String str2 = "Power / cadence";
        if (cycOn && r != null && (cyclingDeviceName = r.getCyclingDeviceName()) != null) {
            str2 = cyclingDeviceName;
        }
        $cycName.setText(str2);
        this$0.setSensorValue($cycVal, cycOn, (r != null ? r.getCurPower() : 0) + " W · " + (r != null ? r.getCurCadence() : 0) + " rpm", this$0.prettyStatus(r != null ? r.getCyclingStatus() : null));
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageSensors$lambda$20(SettingsActivity this$0) {
        RideService rideService = this$0.ride;
        if (rideService != null) {
            rideService.rescanSensor();
        }
        this$0.toast("Rescanning…");
        Function0<Unit> function0 = this$0.liveRefresh;
        if (function0 != null) {
            function0.invoke();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageSensors$lambda$21(SettingsActivity this$0, int v) {
        Prefs.INSTANCE.setMaxHr(this$0, v);
        return Unit.INSTANCE;
    }

    private final void setSensorValue(TextView v, boolean connected, String live, String status) {
        v.setText(connected ? live : status);
        v.setTextColor(connected ? -13577896 : -6381922);
    }

    private final String prettyStatus(String s) {
        if (s == null) {
            return "Not connected";
        }
        if (StringsKt.startsWith$default(s, "connecting", false, 2, (Object) null)) {
            return "Connecting…";
        }
        if (Intrinsics.areEqual(s, "live") || Intrinsics.areEqual(s, "connected")) {
            return "Connected";
        }
        if (Intrinsics.areEqual(s, "scanning") || Intrinsics.areEqual(s, "scanning…")) {
            return "Scanning…";
        }
        if (Intrinsics.areEqual(s, "BT off")) {
            return "Bluetooth off";
        }
        if (Intrinsics.areEqual(s, "disconnected")) {
            return "Not connected";
        }
        return s;
    }

    private final Pair<TextView, TextView> sensorRow(String name) {
        LinearLayout c = card();
        TextView t = new TextView(this);
        t.setText(name);
        t.setTextColor(-1);
        t.setTextSize(17.0f);
        t.setMaxLines(1);
        t.setEllipsize(TextUtils.TruncateAt.END);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView v = new TextView(this);
        v.setText("…");
        v.setTextColor(Color.parseColor("#FF9E9E9E"));
        v.setTextSize(18.0f);
        v.setPadding(dp(10), 0, 0, 0);
        c.addView(t);
        c.addView(v);
        return TuplesKt.to(t, v);
    }

    private final void pageRoutes() {
        List<File> routes;
        if (ActionBus.INSTANCE.getNavigating()) {
            button("Stop navigation", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda21
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pageRoutes$lambda$22(this.f$0);
                }
            });
        }
        if (Prefs.INSTANCE.driveConnected(this)) {
            String folder = Prefs.INSTANCE.driveRoutesFolder(this);
            text("Sync routes from Drive — either drop GPX files into your “" + folder + "” folder, or add Google Maps links to a Sheet (below):");
            button("⟳  Sync routes from Drive", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda23
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pageRoutes$lambda$23(this.f$0);
                }
            });
            final String sheetId = Prefs.INSTANCE.driveSheetId(this);
            if (sheetId.length() == 0) {
                text("Prefer a table? Create a Google Sheet with Name + Link columns — add a row per route (a Maps directions link), then Sync pulls them in.");
                button("＋  Create route links sheet", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda24
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return SettingsActivity.pageRoutes$lambda$24(this.f$0);
                    }
                });
            } else {
                text("Your “Harmin Route Links” Sheet is in the “" + folder + "” folder. Add a row — Name + a Google Maps directions link — then tap Sync. Editing or deleting a row updates or removes that route on the next sync.");
                button("⧉  Copy sheet link", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda25
                    @Override // kotlin.jvm.functions.Function0
                    public final Object invoke() {
                        return SettingsActivity.pageRoutes$lambda$25(this.f$0, sheetId);
                    }
                });
            }
        }
        text("Or paste a public GPX route URL (Komoot / RideWithGPS / Strava / bikerouter export). Navigate now, or save it to re-ride offline.");
        final EditText urlField = editRow("Route GPX URL", "", "Paste GPX URL");
        button("Download & navigate", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda26
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pageRoutes$lambda$27(this.f$0, urlField);
            }
        });
        button("Download & save", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda27
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pageRoutes$lambda$29(this.f$0, urlField);
            }
        });
        text("Or paste a Google Maps link (share a place or directions). We build a bike route between the stops — no GPX needed.");
        final EditText gmapField = editRow("Google Maps link", "", "Paste maps.app.goo.gl / google.com/maps link");
        button("Navigate this link", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda28
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pageRoutes$lambda$30(this.f$0, gmapField);
            }
        });
        button("Save this link", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda29
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pageRoutes$lambda$31(this.f$0, gmapField);
            }
        });
        text("Saved routes");
        Object[] $this$sortedBy$iv = new File(this.ROUTES_DIR).listFiles(new FileFilter() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda30
            @Override // java.io.FileFilter
            public final boolean accept(File file) {
                return SettingsActivity.pageRoutes$lambda$32(file);
            }
        });
        if ($this$sortedBy$iv == null || (routes = ArraysKt.sortedWith($this$sortedBy$iv, new Comparator() { // from class: com.bike.computer.SettingsActivity$pageRoutes$$inlined$sortedBy$1
            /* JADX WARN: Multi-variable type inference failed */
            @Override // java.util.Comparator
            public final int compare(T t, T t2) {
                File it = (File) t;
                String name = it.getName();
                Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                String lowerCase = name.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
                File it2 = (File) t2;
                String name2 = it2.getName();
                Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
                String lowerCase2 = name2.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue(lowerCase2, "toLowerCase(...)");
                return ComparisonsKt.compareValues(lowerCase, lowerCase2);
            }
        })) == null) {
            routes = CollectionsKt.emptyList();
        }
        if (routes.isEmpty()) {
            text("No saved routes yet");
        }
        for (File f : routes) {
            Intrinsics.checkNotNull(f);
            savedRouteRow(f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$22(SettingsActivity this$0) {
        ActionBus.INSTANCE.setStopNav(true);
        this$0.toast("Navigation stopped");
        this$0.rebuild();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$23(SettingsActivity this$0) {
        this$0.syncDriveRoutes();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$24(SettingsActivity this$0) {
        this$0.createLinksSheet();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$25(SettingsActivity this$0, String $sheetId) {
        this$0.copyText(GoogleDriveClient.INSTANCE.sheetUrl($sheetId), "Sheet link copied");
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$27(final SettingsActivity this$0, EditText $urlField) {
        this$0.withDownloadedRoute(StringsKt.trim((CharSequence) $urlField.getText().toString()).toString(), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda7
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRoutes$lambda$27$lambda$26(this.f$0, (String) obj);
            }
        });
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$27$lambda$26(SettingsActivity this$0, String gpx) {
        Intrinsics.checkNotNullParameter(gpx, "gpx");
        navigateGpx$default(this$0, gpx, null, 2, null);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$29(final SettingsActivity this$0, EditText $urlField) {
        this$0.withDownloadedRoute(StringsKt.trim((CharSequence) $urlField.getText().toString()).toString(), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda36
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return SettingsActivity.pageRoutes$lambda$29$lambda$28(this.f$0, (String) obj);
            }
        });
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$29$lambda$28(SettingsActivity this$0, String gpx) {
        Intrinsics.checkNotNullParameter(gpx, "gpx");
        this$0.promptSaveRoute(gpx);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$30(SettingsActivity this$0, EditText $gmapField) {
        this$0.withGmapsRoute(StringsKt.trim((CharSequence) $gmapField.getText().toString()).toString(), false);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageRoutes$lambda$31(SettingsActivity this$0, EditText $gmapField) {
        this$0.withGmapsRoute(StringsKt.trim((CharSequence) $gmapField.getText().toString()).toString(), true);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean pageRoutes$lambda$32(File f) {
        String name = f.getName();
        Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
        return StringsKt.endsWith$default(name, ".gpx", false, 2, (Object) null);
    }

    private final void createLinksSheet() {
        toast("Creating Sheet in Drive…");
        new Thread(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda31
            @Override // java.lang.Runnable
            public final void run() {
                SettingsActivity.createLinksSheet$lambda$38(this.f$0);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void createLinksSheet$lambda$38(final SettingsActivity this$0) {
        final Object r;
        try {
            Result.Companion companion = Result.INSTANCE;
            r = Result.m118constructorimpl(GoogleDriveClient.INSTANCE.ensureLinksSheet(this$0));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            r = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda46
            @Override // java.lang.Runnable
            public final void run() {
                SettingsActivity.createLinksSheet$lambda$38$lambda$37(r, this$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void createLinksSheet$lambda$38$lambda$37(Object $r, SettingsActivity this$0) {
        if (Result.m125isSuccessimpl($r)) {
            String id = (String) $r;
            this$0.copyText(GoogleDriveClient.INSTANCE.sheetUrl(id), "Created “Harmin Route Links” — link copied");
            this$0.rebuild();
        }
        Throwable e = Result.m121exceptionOrNullimpl($r);
        if (e != null) {
            String m = e.getMessage();
            if (m == null) {
                m = "failed";
            }
            this$0.toast((StringsKt.contains$default((CharSequence) m, (CharSequence) "403", false, 2, (Object) null) || StringsKt.contains((CharSequence) m, (CharSequence) "scope", true)) ? "Reconnect Drive to grant access" : "Couldn't create sheet: " + m);
        }
    }

    private final void copyText(String text, String done) {
        Object systemService = getSystemService("clipboard");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.content.ClipboardManager");
        ((ClipboardManager) systemService).setPrimaryClip(ClipData.newPlainText("Harmin", text));
        toast(done);
    }

    private final void syncDriveRoutes() {
        toast("Syncing routes from Drive…");
        new Thread(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda55
            @Override // java.lang.Runnable
            public final void run() {
                SettingsActivity.syncDriveRoutes$lambda$43(this.f$0);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void syncDriveRoutes$lambda$43(final SettingsActivity this$0) {
        final Object r;
        try {
            Result.Companion companion = Result.INSTANCE;
            r = Result.m118constructorimpl(Integer.valueOf(GoogleDriveClient.INSTANCE.syncRoutes(this$0, this$0.ROUTES_DIR)));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            r = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                SettingsActivity.syncDriveRoutes$lambda$43$lambda$42(r, this$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void syncDriveRoutes$lambda$43$lambda$42(Object $r, SettingsActivity this$0) {
        String str;
        if (Result.m125isSuccessimpl($r)) {
            int n = ((Number) $r).intValue();
            if (n > 0) {
                str = "Added " + n + " route" + (n == 1 ? "" : "s") + " from Drive";
            } else {
                str = "No new routes in Drive";
            }
            this$0.toast(str);
            if (n > 0) {
                this$0.rebuild();
            }
        }
        Throwable e = Result.m121exceptionOrNullimpl($r);
        if (e != null) {
            String m = e.getMessage();
            if (m == null) {
                m = "failed";
            }
            this$0.toast((StringsKt.contains$default((CharSequence) m, (CharSequence) "403", false, 2, (Object) null) || StringsKt.contains((CharSequence) m, (CharSequence) "insufficient", true) || StringsKt.contains((CharSequence) m, (CharSequence) "scope", true)) ? "Reconnect Drive to grant route access" : "Sync failed: " + m);
        }
    }

    private final void pageExport() {
        if (Prefs.INSTANCE.driveConnected(this)) {
            valueRow("Google Drive", "Connected");
            switchRow("Auto-upload after each ride", Prefs.INSTANCE.driveAutoUpload(this), new Function1() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda1
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return SettingsActivity.pageExport$lambda$44(this.f$0, ((Boolean) obj).booleanValue());
                }
            });
            text("Folder names in your Drive — rides upload here; routes are read from here (drop .gpx files in it).");
            final EditText ridesFolder = editRow$default(this, "Rides folder", Prefs.INSTANCE.driveRidesFolder(this), null, 4, null);
            final EditText routesFolder = editRow$default(this, "Routes folder", Prefs.INSTANCE.driveRoutesFolder(this), null, 4, null);
            button("Save folder names", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda2
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pageExport$lambda$45(this.f$0, ridesFolder, routesFolder);
                }
            });
            text("Reconnect if 'Sync routes from Drive' says it can't read your Drive (grants read access).");
            button("Reconnect Drive", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda3
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pageExport$lambda$46(this.f$0);
                }
            });
            button("Disconnect Drive", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda4
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pageExport$lambda$47(this.f$0);
                }
            });
        } else {
            text("Connect Google Drive to back up rides and load routes from Drive folders. Create an OAuth client (type: Desktop app) in Google Cloud Console with the Drive API enabled, then paste its Client ID & Secret.");
            final EditText idField = editRow$default(this, "Client ID", Prefs.INSTANCE.driveClientId(this), null, 4, null);
            final EditText secretField = editRow$default(this, "Client Secret", Prefs.INSTANCE.driveClientSecret(this), null, 4, null);
            button("Connect to Google Drive", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda5
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pageExport$lambda$48(idField, secretField, this);
                }
            });
        }
        text("Your recorded rides — with per-ride upload status, map and share — are under Rides.");
        button("Open Rides", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda6
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pageExport$lambda$49(this.f$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageExport$lambda$44(SettingsActivity this$0, boolean v) {
        Prefs.INSTANCE.setDriveAutoUpload(this$0, v);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageExport$lambda$45(SettingsActivity this$0, EditText $ridesFolder, EditText $routesFolder) {
        Prefs.INSTANCE.setDriveRidesFolder(this$0, $ridesFolder.getText().toString());
        Prefs.INSTANCE.setDriveRoutesFolder(this$0, $routesFolder.getText().toString());
        this$0.toast("Saved");
        this$0.rebuild();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageExport$lambda$46(SettingsActivity this$0) {
        this$0.startActivityForResult(new Intent(this$0, (Class<?>) DriveAuthActivity.class), 43);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageExport$lambda$47(SettingsActivity this$0) {
        Prefs.INSTANCE.clearDriveTokens(this$0);
        this$0.toast("Disconnected");
        this$0.rebuild();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageExport$lambda$48(EditText $idField, EditText $secretField, SettingsActivity this$0) {
        String id = StringsKt.trim((CharSequence) $idField.getText().toString()).toString();
        String secret = StringsKt.trim((CharSequence) $secretField.getText().toString()).toString();
        if (!(id.length() == 0)) {
            if (!(secret.length() == 0)) {
                Prefs.INSTANCE.setDriveApp(this$0, id, secret);
                this$0.startActivityForResult(new Intent(this$0, (Class<?>) DriveAuthActivity.class), 43);
                return Unit.INSTANCE;
            }
        }
        this$0.toast("Enter Client ID and Secret");
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageExport$lambda$49(SettingsActivity this$0) {
        this$0.startActivity(new Intent(this$0, (Class<?>) RidesActivity.class));
        return Unit.INSTANCE;
    }

    private final void pageSystem() {
        boolean isHome = isDefaultHome();
        if (isHome) {
            text("Harmin is your phone's home app — it opens on boot and when you press Home. Jump to the normal Android launcher (Harmin stays the default — press Home to come back):");
            button("Exit to Android launcher", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda8
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pageSystem$lambda$50(this.f$0);
                }
            });
        } else {
            text("Make Harmin your phone's home app so it opens on boot and when you press Home (choose Harmin in the picker).");
            button("Set Harmin as home app", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda9
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return SettingsActivity.pageSystem$lambda$51(this.f$0);
                }
            });
        }
        button("Android system settings", new Function0() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda10
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return SettingsActivity.pageSystem$lambda$54(this.f$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageSystem$lambda$50(SettingsActivity this$0) {
        this$0.launchOtherLauncher();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageSystem$lambda$51(SettingsActivity this$0) {
        this$0.openHomeSettings();
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit pageSystem$lambda$54(SettingsActivity this$0) {
        Object objM118constructorimpl;
        try {
            Result.Companion companion = Result.INSTANCE;
            this$0.startActivity(new Intent("android.settings.SETTINGS"));
            objM118constructorimpl = Result.m118constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m121exceptionOrNullimpl(objM118constructorimpl) != null) {
            this$0.toast("Couldn't open settings");
        }
        return Unit.INSTANCE;
    }

    private final boolean isDefaultHome() {
        ActivityInfo activityInfo;
        RoleManager rm;
        if (Build.VERSION.SDK_INT >= 29 && (rm = (RoleManager) getSystemService(RoleManager.class)) != null && rm.isRoleAvailable("android.app.role.HOME")) {
            return rm.isRoleHeld("android.app.role.HOME");
        }
        ResolveInfo res = getPackageManager().resolveActivity(new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME"), 0);
        return Intrinsics.areEqual((res == null || (activityInfo = res.activityInfo) == null) ? null : activityInfo.packageName, getPackageName());
    }

    private final void openHomeSettings() {
        Object objM118constructorimpl;
        Object objM118constructorimpl2;
        try {
            Result.Companion companion = Result.INSTANCE;
            SettingsActivity $this$openHomeSettings_u24lambda_u2455 = this;
            $this$openHomeSettings_u24lambda_u2455.startActivity(new Intent("android.settings.HOME_SETTINGS"));
            objM118constructorimpl = Result.m118constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m121exceptionOrNullimpl(objM118constructorimpl) != null) {
            try {
                Result.Companion companion3 = Result.INSTANCE;
                SettingsActivity $this$openHomeSettings_u24lambda_u2458_u24lambda_u2456 = this;
                $this$openHomeSettings_u24lambda_u2458_u24lambda_u2456.startActivity(new Intent("android.settings.SETTINGS"));
                objM118constructorimpl2 = Result.m118constructorimpl(Unit.INSTANCE);
            } catch (Throwable th2) {
                Result.Companion companion4 = Result.INSTANCE;
                objM118constructorimpl2 = Result.m118constructorimpl(ResultKt.createFailure(th2));
            }
            if (Result.m121exceptionOrNullimpl(objM118constructorimpl2) != null) {
                toast("Open Settings ▸ Apps ▸ Default apps ▸ Home app");
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x00d4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final void launchOtherLauncher() {
        Object element$iv;
        Object objM118constructorimpl;
        Intent home = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME");
        Intrinsics.checkNotNullExpressionValue(home, "addCategory(...)");
        Iterable iterableQueryIntentActivities = getPackageManager().queryIntentActivities(home, 0);
        Intrinsics.checkNotNullExpressionValue(iterableQueryIntentActivities, "queryIntentActivities(...)");
        Iterable $this$map$iv = iterableQueryIntentActivities;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            destination$iv$iv.add(((ResolveInfo) item$iv$iv).activityInfo);
        }
        Iterable $this$filter$iv = (List) destination$iv$iv;
        Collection destination$iv$iv2 = new ArrayList();
        Iterator it = $this$filter$iv.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Object element$iv$iv = it.next();
            ActivityInfo it2 = (ActivityInfo) element$iv$iv;
            if ((Intrinsics.areEqual(it2.packageName, getPackageName()) || Intrinsics.areEqual(it2.packageName, "com.android.settings")) ? false : true) {
                destination$iv$iv2.add(element$iv$iv);
            }
        }
        List cands = (List) destination$iv$iv2;
        List $this$firstOrNull$iv = cands;
        Iterator it3 = $this$firstOrNull$iv.iterator();
        while (true) {
            if (!it3.hasNext()) {
                element$iv = null;
                break;
            }
            element$iv = it3.next();
            ActivityInfo it4 = (ActivityInfo) element$iv;
            String packageName = it4.packageName;
            Intrinsics.checkNotNullExpressionValue(packageName, "packageName");
            if (!StringsKt.contains((CharSequence) packageName, (CharSequence) "launcher", true)) {
                String packageName2 = it4.packageName;
                Intrinsics.checkNotNullExpressionValue(packageName2, "packageName");
                boolean z = StringsKt.contains((CharSequence) packageName2, (CharSequence) "trebuchet", true);
                if (z) {
                    break;
                }
            }
        }
        ActivityInfo activityInfo = (ActivityInfo) element$iv;
        if (activityInfo == null) {
            activityInfo = (ActivityInfo) CollectionsKt.firstOrNull(cands);
        }
        ActivityInfo t = activityInfo;
        if (t == null) {
            toast("No other launcher installed");
            return;
        }
        Intent i = new Intent("android.intent.action.MAIN").addCategory("android.intent.category.HOME").setClassName(t.packageName, t.name).addFlags(268435456);
        Intrinsics.checkNotNullExpressionValue(i, "addFlags(...)");
        try {
            Result.Companion companion = Result.INSTANCE;
            SettingsActivity $this$launchOtherLauncher_u24lambda_u2462 = this;
            $this$launchOtherLauncher_u24lambda_u2462.startActivity(i);
            objM118constructorimpl = Result.m118constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m121exceptionOrNullimpl(objM118constructorimpl) != null) {
            openHomeSettings();
        }
    }

    private final void menuRow(String title, final Function0<Unit> onClick) {
        LinearLayout c = card();
        c.setPadding(dp(18), dp(18), dp(16), dp(18));
        TextView t = new TextView(this);
        t.setText(title);
        t.setTextColor(Color.parseColor("#FFE8E8EA"));
        t.setTextSize(18.0f);
        t.setTypeface(Typeface.create("sans-serif-medium", 0));
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView chev = new TextView(this);
        chev.setText("›");
        chev.setTextColor(Color.parseColor("#FF6E6E6E"));
        chev.setTextSize(24.0f);
        c.addView(t);
        c.addView(chev);
        c.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda15
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                onClick.invoke();
            }
        });
    }

    private final void text(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(Color.parseColor("#FF9E9E9E"));
        t.setTextSize(14.0f);
        t.setPadding(dp(10), dp(2), dp(10), dp(8));
        LinearLayout linearLayout = this.target;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("target");
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
        LinearLayout linearLayout = this.target;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("target");
            linearLayout = null;
        }
        linearLayout.addView(l);
        return l;
    }

    private final void switchRow(String label, boolean initial, final Function1<? super Boolean, Unit> onChange) {
        LinearLayout c = card();
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextColor(-1);
        t.setTextSize(17.0f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        Switch sw = new Switch(this);
        sw.setChecked(initial);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda48
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SettingsActivity.switchRow$lambda$65(onChange, compoundButton, z);
            }
        });
        c.addView(t);
        c.addView(sw);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void switchRow$lambda$65(Function1 $onChange, CompoundButton compoundButton, boolean v) {
        $onChange.invoke(Boolean.valueOf(v));
    }

    private final void checkRow(String label, boolean initial, final Function1<? super Boolean, Unit> onChange) {
        LinearLayout c = card();
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextColor(-1);
        t.setTextSize(16.0f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        CheckBox cb = new CheckBox(this);
        cb.setChecked(initial);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda47
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SettingsActivity.checkRow$lambda$66(onChange, compoundButton, z);
            }
        });
        c.addView(t);
        c.addView(cb);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void checkRow$lambda$66(Function1 $onChange, CompoundButton compoundButton, boolean v) {
        $onChange.invoke(Boolean.valueOf(v));
    }

    private final void stepperRow(String label, int initial, int min, int max, int step, String suffix, Function1<? super Integer, Unit> onChange) {
        LinearLayout c = card();
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextColor(-1);
        t.setTextSize(17.0f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        Ref.IntRef value = new Ref.IntRef();
        value.element = initial;
        TextView valView = new TextView(this);
        valView.setText(value.element + suffix);
        valView.setTextColor(Color.parseColor("#FF9E9E9E"));
        valView.setTextSize(17.0f);
        valView.setGravity(17);
        valView.setLayoutParams(new LinearLayout.LayoutParams(dp(74), -2));
        c.addView(t);
        c.addView(stepperRow$stepBtn(this, value, min, max, valView, suffix, onChange, "−", -step));
        c.addView(valView);
        c.addView(stepperRow$stepBtn(this, value, min, max, valView, suffix, onChange, "+", step));
    }

    private static final TextView stepperRow$stepBtn(SettingsActivity this$0, final Ref.IntRef value, final int $min, final int $max, final TextView valView, final String $suffix, final Function1<? super Integer, Unit> function1, String sym, final int delta) {
        TextView b = new TextView(this$0);
        b.setText(sym);
        b.setTextColor(Color.parseColor("#FF4C8DFF"));
        b.setTextSize(26.0f);
        b.setGravity(17);
        b.setLayoutParams(new LinearLayout.LayoutParams(this$0.dp(44), this$0.dp(40)));
        b.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda52
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsActivity.stepperRow$stepBtn$lambda$67(value, delta, $min, $max, valView, $suffix, function1, view);
            }
        });
        return b;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void stepperRow$stepBtn$lambda$67(Ref.IntRef $value, int $delta, int $min, int $max, TextView $valView, String $suffix, Function1 $onChange, View it) {
        $value.element = RangesKt.coerceIn($value.element + $delta, $min, $max);
        $valView.setText($value.element + $suffix);
        $onChange.invoke(Integer.valueOf($value.element));
    }

    private final void button(String label, final Function0<Unit> onClick) {
        LinearLayout c = card();
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextColor(Color.parseColor("#FF4C8DFF"));
        t.setTextSize(17.0f);
        t.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        c.addView(t);
        c.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda37
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                onClick.invoke();
            }
        });
    }

    private final TextView valueRow(String label, String value) {
        LinearLayout c = card();
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextColor(-1);
        t.setTextSize(17.0f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(Color.parseColor("#FF9E9E9E"));
        v.setTextSize(15.0f);
        c.addView(t);
        c.addView(v);
        return v;
    }

    static /* synthetic */ EditText editRow$default(SettingsActivity settingsActivity, String str, String str2, String str3, int i, Object obj) {
        if ((i & 4) != 0) {
            str3 = "";
        }
        return settingsActivity.editRow(str, str2, str3);
    }

    private final EditText editRow(String label, String initial, String hint) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(1);
        l.setBackgroundResource(R.drawable.card_solid);
        l.setPadding(dp(16), dp(10), dp(16), dp(12));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        l.setLayoutParams(lp);
        TextView t = new TextView(this);
        t.setText(label);
        t.setTextColor(Color.parseColor("#FF8E8E93"));
        t.setTextSize(13.0f);
        EditText e = new EditText(this);
        e.setText(initial);
        e.setTextColor(-1);
        e.setTextSize(16.0f);
        e.setSingleLine(true);
        e.setPadding(0, dp(6), 0, 0);
        LinearLayout linearLayout = null;
        e.setBackground(null);
        if (hint.length() > 0) {
            e.setHint(hint);
            e.setHintTextColor(Color.parseColor("#FF6E6E6E"));
        }
        e.setInputType(145);
        l.addView(t);
        l.addView(e);
        LinearLayout linearLayout2 = this.target;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("target");
        } else {
            linearLayout = linearLayout2;
        }
        linearLayout.addView(l);
        return e;
    }

    private final void pageRow(final String key, String name, int index, int count, Function0<Unit> onUp, Function0<Unit> onDown, final Function0<Unit> onRemove) {
        LinearLayout c = card();
        boolean on = Prefs.INSTANCE.pageEnabled(this, key);
        TextView t = new TextView(this);
        t.setText(name);
        t.setTextColor(on ? -1 : Color.parseColor("#FF6E6E6E"));
        t.setTextSize(16.0f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        c.addView(t);
        if (Pages.INSTANCE.isData(key)) {
            TextView del = new TextView(this);
            del.setText("Delete");
            del.setTextColor(Color.parseColor("#FFE53935"));
            del.setTextSize(14.0f);
            del.setPadding(dp(8), 0, dp(10), 0);
            del.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda49
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    onRemove.invoke();
                }
            });
            c.addView(del);
        }
        c.addView(pageRow$arrow(this, "▲", index > 0, onUp));
        c.addView(pageRow$arrow(this, "▼", index < count + (-1), onDown));
        if (Intrinsics.areEqual(key, Pages.MAP)) {
            TextView lock = new TextView(this);
            lock.setText("On");
            lock.setTextColor(Color.parseColor("#FF6E6E6E"));
            lock.setTextSize(14.0f);
            lock.setPadding(dp(10), 0, dp(6), 0);
            c.addView(lock);
            return;
        }
        Switch sw = new Switch(this);
        sw.setChecked(on);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda50
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SettingsActivity.pageRow$lambda$71(this.f$0, key, compoundButton, z);
            }
        });
        c.addView(sw);
    }

    private static final TextView pageRow$arrow(SettingsActivity this$0, String sym, boolean active, final Function0<Unit> function0) {
        TextView b = new TextView(this$0);
        b.setText(sym);
        b.setTextSize(18.0f);
        b.setGravity(17);
        b.setTextColor(Color.parseColor(active ? "#FF4C8DFF" : "#FF3A3A3C"));
        b.setLayoutParams(new LinearLayout.LayoutParams(this$0.dp(38), this$0.dp(38)));
        if (active) {
            b.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda14
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    function0.invoke();
                }
            });
        }
        return b;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void pageRow$lambda$71(SettingsActivity this$0, String $key, CompoundButton compoundButton, boolean v) {
        Prefs.INSTANCE.setPageEnabled(this$0, $key, v);
        this$0.rebuild();
    }

    private final void withDownloadedRoute(final String url, final Function1<? super String, Unit> onReady) {
        if (url.length() == 0) {
            toast("Enter a route URL");
            return;
        }
        Prefs.INSTANCE.setLastRouteUrl(this, url);
        toast("Downloading route…");
        new Thread(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda51
            @Override // java.lang.Runnable
            public final void run() {
                SettingsActivity.withDownloadedRoute$lambda$76(this.f$0, url, onReady);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void withDownloadedRoute$lambda$76(final SettingsActivity this$0, String $url, final Function1 $onReady) {
        final Object r;
        try {
            Result.Companion companion = Result.INSTANCE;
            r = Result.m118constructorimpl(GpxRoute.INSTANCE.download($url));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            r = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                SettingsActivity.withDownloadedRoute$lambda$76$lambda$75(r, $onReady, this$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void withDownloadedRoute$lambda$76$lambda$75(Object $r, Function1 $onReady, SettingsActivity this$0) {
        if (Result.m125isSuccessimpl($r)) {
            $onReady.invoke((String) $r);
        }
        Throwable it = Result.m121exceptionOrNullimpl($r);
        if (it != null) {
            this$0.toast("Download failed: " + it.getMessage());
        }
    }

    static /* synthetic */ void navigateGpx$default(SettingsActivity settingsActivity, String str, String str2, int i, Object obj) {
        if ((i & 2) != 0) {
            str2 = null;
        }
        settingsActivity.navigateGpx(str, str2);
    }

    private final void navigateGpx(String gpx, String routeName) {
        List<double[]> list = GpxRoute.INSTANCE.parse(gpx);
        if (list.size() < 2) {
            toast("No route points found");
            return;
        }
        ActionBus.INSTANCE.setPendingRoute(GpxRoute.toWaypoints$default(GpxRoute.INSTANCE, list, 0.0d, 0, 6, null));
        ActionBus.INSTANCE.setPendingRouteName(routeName);
        toast("Snapping route to roads…");
        startActivity(new Intent(this, (Class<?>) MainActivity.class));
        finish();
    }

    private final void withGmapsRoute(final String link, final boolean save) {
        if (link.length() == 0) {
            toast("Paste a Google Maps link");
        } else if (!GmapsRoute.INSTANCE.looksLikeLink(link)) {
            toast("That doesn't look like a Google Maps link");
        } else {
            toast("Reading Google Maps link…");
            new Thread(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    SettingsActivity.withGmapsRoute$lambda$81(this.f$0, link, save);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void withGmapsRoute$lambda$81(final SettingsActivity this$0, String $link, final boolean $save) {
        final Object res;
        try {
            Result.Companion companion = Result.INSTANCE;
            res = Result.m118constructorimpl(GmapsRoute.INSTANCE.points($link));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            res = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                SettingsActivity.withGmapsRoute$lambda$81$lambda$80(res, this$0, $save);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void withGmapsRoute$lambda$81$lambda$80(Object $res, SettingsActivity this$0, boolean $save) {
        if (Result.m125isSuccessimpl($res)) {
            List<double[]> list = (List) $res;
            if (list.isEmpty()) {
                this$0.toast("Couldn't find a location in that link");
            } else if (list.size() == 1 && $save) {
                this$0.toast("That link is a single place — use “Navigate” (a saved route needs a start and end)");
            } else if (list.size() == 1) {
                ActionBus.INSTANCE.setPendingDestination(new double[]{list.get(0)[1], list.get(0)[0]});
                this$0.toast("Routing to your destination…");
                this$0.startActivity(new Intent(this$0, (Class<?>) MainActivity.class));
                this$0.finish();
            } else if ($save) {
                this$0.promptSaveRoute(GmapsRoute.INSTANCE.toGpx("Maps route", list));
            } else {
                navigateGpx$default(this$0, GmapsRoute.INSTANCE.toGpx("Maps route", list), null, 2, null);
            }
        }
        Throwable it = Result.m121exceptionOrNullimpl($res);
        if (it != null) {
            this$0.toast("Couldn't read that link (need Wi-Fi): " + it.getMessage());
        }
    }

    private final void promptSaveRoute(final String gpx) {
        if (GpxRoute.INSTANCE.parse(gpx).size() < 2) {
            toast("No route points found");
            return;
        }
        String strName = GpxRoute.INSTANCE.name(gpx);
        if (strName == null) {
            strName = "Route";
        }
        final EditText input = new EditText(this);
        input.setText(strName);
        input.setSingleLine(true);
        input.setTextColor(-1);
        input.setPadding(dp(16), dp(12), dp(16), dp(12));
        new AlertDialog.Builder(this, android.R.style.Theme.Material.Dialog.Alert).setTitle("Save route as").setView(input).setPositiveButton("Save", new DialogInterface.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda18
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                SettingsActivity.promptSaveRoute$lambda$85(input, this, gpx, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void promptSaveRoute$lambda$85(EditText $input, SettingsActivity this$0, String $gpx, DialogInterface dialogInterface, int i) {
        Object objM118constructorimpl;
        String name = StringsKt.take(new Regex("[/\\\\:*?\"<>|]").replace(StringsKt.trim((CharSequence) $input.getText().toString()).toString(), "_"), 60);
        if (name.length() == 0) {
            this$0.toast("Name required");
            return;
        }
        try {
            Result.Companion companion = Result.INSTANCE;
            new File(this$0.ROUTES_DIR).mkdirs();
            FilesKt.writeText$default(new File(this$0.ROUTES_DIR, name + ".gpx"), $gpx, null, 2, null);
            objM118constructorimpl = Result.m118constructorimpl(Unit.INSTANCE);
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m125isSuccessimpl(objM118constructorimpl)) {
            this$0.toast("Saved \"" + name + "\"");
            this$0.rebuild();
        }
        Throwable it = Result.m121exceptionOrNullimpl(objM118constructorimpl);
        if (it != null) {
            this$0.toast("Save failed: " + it.getMessage());
        }
    }

    private final void savedRouteRow(final File f) {
        LinearLayout c = card();
        TextView t = new TextView(this);
        t.setText(FilesKt.getNameWithoutExtension(f));
        t.setTextColor(-1);
        t.setTextSize(15.0f);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView ride = new TextView(this);
        ride.setText("Ride");
        ride.setTextColor(Color.parseColor("#FF4C8DFF"));
        ride.setTextSize(16.0f);
        ride.setPadding(dp(10), 0, dp(10), 0);
        ride.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsActivity.savedRouteRow$lambda$87(this.f$0, f, view);
            }
        });
        TextView del = new TextView(this);
        del.setText("Delete");
        del.setTextColor(Color.parseColor("#FFE53935"));
        del.setTextSize(15.0f);
        del.setPadding(dp(10), 0, dp(4), 0);
        del.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.SettingsActivity$$ExternalSyntheticLambda13
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsActivity.savedRouteRow$lambda$88(f, this, view);
            }
        });
        c.addView(t);
        c.addView(ride);
        c.addView(del);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void savedRouteRow$lambda$87(SettingsActivity this$0, File $f, View it) {
        Object objM118constructorimpl;
        try {
            Result.Companion companion = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(FilesKt.readText$default($f, null, 1, null));
        } catch (Throwable th) {
            Result.Companion companion2 = Result.INSTANCE;
            objM118constructorimpl = Result.m118constructorimpl(ResultKt.createFailure(th));
        }
        if (Result.m124isFailureimpl(objM118constructorimpl)) {
            objM118constructorimpl = "";
        }
        this$0.navigateGpx((String) objM118constructorimpl, FilesKt.getNameWithoutExtension($f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void savedRouteRow$lambda$88(File $f, SettingsActivity this$0, View it) {
        $f.delete();
        this$0.rebuild();
    }

    private final void toast(String s) {
        Toast.makeText(this, s, 0).show();
    }
}

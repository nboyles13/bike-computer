package com.bike.computer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import btools.router.NavHint;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import okhttp3.OkHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.maplibre.android.MapLibre;
import org.maplibre.android.camera.CameraPosition;
import org.maplibre.android.camera.CameraUpdateFactory;
import org.maplibre.android.geometry.LatLng;
import org.maplibre.android.location.LocationComponent;
import org.maplibre.android.location.LocationComponentActivationOptions;
import org.maplibre.android.location.OnCameraTrackingChangedListener;
import org.maplibre.android.maps.MapLibreMap;
import org.maplibre.android.maps.MapView;
import org.maplibre.android.maps.OnMapReadyCallback;
import org.maplibre.android.maps.Style;
import org.maplibre.android.module.http.HttpRequestUtil;
import org.maplibre.android.style.expressions.Expression;
import org.maplibre.android.style.layers.FillExtrusionLayer;
import org.maplibre.android.style.layers.Layer;
import org.maplibre.android.style.layers.LineLayer;
import org.maplibre.android.style.layers.PropertyFactory;
import org.maplibre.android.style.layers.SymbolLayer;
import org.maplibre.android.style.sources.GeoJsonSource;
import org.maplibre.geojson.LineString;
import org.maplibre.geojson.Point;

public final class MainActivity extends Activity {
    private ImageView bellBtn;
    private String currentRouteName;
    private double destLat;
    private double destLon;
    private LinearLayout dotsBar;
    private TextView eAscent;
    private TextView eEle;
    private TextView eGrade;
    private ElevationView elevView;
    private ImageView homeBtn;
    private TextView hrBig;
    private HrGraphView hrGraph;
    private LinearLayout hrLegend;
    private int hrPageMaxHr;
    private TextView hrZoneLbl;
    private float lastFocusX;
    private float lastFocusY;
    private long lastRerouteMs;
    private int lastTrailSize;
    private Style loadedStyle;
    private boolean lowBattWarned;
    private TextView mDist;
    private TextView mHr;
    private TextView mSpeed;
    private TextView mTime;
    private MapLibreMap map;
    private int mapPageIndex;
    private MapView mapView;
    private TextView navArrow;
    private View navBanner;
    private TextView navDist;
    private TextView navText;
    private boolean navigating;
    private long offRouteSince;
    private View pageElev;
    private View pageHr;
    private View pageMap;
    private View pageSummary;
    private ViewPager2 pager;
    private TextView pauseBtn;
    private TextView pausedBadge;
    private View recBar;
    private TextView recBtn;
    private ImageView recenterBtn;
    RideService ride;
    private GeoJsonSource routeSource;
    private List<double[]> routeVias;
    private TextView sAscent;
    private TextView sAvgHr;
    private TextView sAvgSpd;
    private TextView sDist;
    private TextView sMaxHr;
    private TextView sMaxSpd;
    private TextView sStarted;
    private TextView sTime;
    private boolean screenOffArmed;
    private View stopBtn;
    private View stopFill;
    private boolean stopHolding;
    private TextView stopLabel;
    private GeoJsonSource trailSource;
    private boolean twoFingerActive;
    private final String DATA = "/sdcard/BikeComputer";
    private final LinkedHashMap<String, View> dataPages = new LinkedHashMap<>();
    private final List<DashboardView> dashViews = new ArrayList();
    private List<? extends TextView> dots = CollectionsKt.emptyList();
    private List<NavHint> navSteps = CollectionsKt.emptyList();
    private List<double[]> navPoints = CollectionsKt.emptyList();
    private int announcedIdx = -1;
    private int earlyAnnouncedIdx = -1;
    private final ArrayList<TextView> hrTimeLbls = new ArrayList<>();
    private final ArrayList<TextView> hrPctLbls = new ArrayList<>();
    private final Handler ui = new Handler(Looper.getMainLooper());
    private final SimpleDateFormat clockFmt = new SimpleDateFormat("h:mm", Locale.US);
    private String pageSig = "";
    private boolean cameraTracking = true;
    private boolean controlsVisible = true;
    private final Runnable hideRunnable = new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda15
        @Override // java.lang.Runnable
        public final void run() {
            MainActivity.this.hideChrome();
        }
    };
    private final long screenOffMs = 30000;
    private final Runnable screenOffRunnable = new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda16
        @Override // java.lang.Runnable
        public final void run() {
            MainActivity.screenOffRunnable$lambda$1(MainActivity.this);
        }
    };
    private final String[] perms = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.BLUETOOTH_SCAN", "android.permission.BLUETOOTH_CONNECT", "android.permission.POST_NOTIFICATIONS"};
    private final MainActivity$conn$1 conn = new MainActivity$conn$1(this);
    private double mapBearing = Double.NaN;
    private final Runnable stopCompleteRunnable = new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda17
        @Override // java.lang.Runnable
        public final void run() {
            MainActivity.stopCompleteRunnable$lambda$64(MainActivity.this);
        }
    };

    /* JADX INFO: compiled from: MainActivity.kt */
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

    /* JADX INFO: Access modifiers changed from: private */
    public static final void screenOffRunnable$lambda$1(MainActivity this$0) {
        this$0.screenOffArmed = false;
        this$0.sleepScreen();
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapLibre.getInstance(this);
        MapLibre.setConnected(true);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(128);
        enterImmersive();
        LayoutInflater inf = LayoutInflater.from(this);
        View view = null;
        this.pageMap = inf.inflate(R.layout.page_map, (ViewGroup) null);
        this.pageHr = inf.inflate(R.layout.page_hr, (ViewGroup) null);
        this.pageElev = inf.inflate(R.layout.page_elevation, (ViewGroup) null);
        this.pageSummary = inf.inflate(R.layout.page_summary, (ViewGroup) null);
        bindViews();
        HttpRequestUtil.setOkHttpClient(new OkHttpClient.Builder().addInterceptor(new LocalTiles(getFilesDir() + "/california.mbtiles", this.DATA + "/styles/fonts")).build());
        MapView mapView = this.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.onCreate(savedInstanceState);
        MapView mapView2 = this.mapView;
        if (mapView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView2 = null;
        }
        mapView2.getMapAsync(new OnMapReadyCallback() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda6
            @Override // org.maplibre.android.maps.OnMapReadyCallback
            public final void onMapReady(MapLibreMap mapLibreMap) {
                MainActivity.onCreate$lambda$6(MainActivity.this, mapLibreMap);
            }
        });
        this.pager = (ViewPager2) findViewById(R.id.pager);
        ViewPager2 viewPager2 = this.pager;
        if (viewPager2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
            viewPager2 = null;
        }
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { // from class: com.bike.computer.MainActivity.onCreate.2
            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageSelected(int position) {
                Iterable $this$forEachIndexed$iv = MainActivity.this.dots;
                int i = 0;
                for (Object item$iv : $this$forEachIndexed$iv) {
                    int index$iv = i + 1;
                    if (i < 0) {
                        CollectionsKt.throwIndexOverflow();
                    }
                    TextView d = (TextView) item$iv;
                    d.setAlpha(i == position ? 1.0f : 0.35f);
                    i = index$iv;
                }
                MainActivity.this.updateRecUi();
                MainActivity.this.syncChrome(false);
            }
        });
        buildPager(true);
        TextView textView = this.recBtn;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recBtn");
            textView = null;
        }
        textView.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MainActivity.this.startRec();
            }
        });
        ImageView imageView = this.bellBtn;
        if (imageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bellBtn");
            imageView = null;
        }
        imageView.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MainActivity.onCreate$lambda$8(MainActivity.this, view2);
            }
        });
        Bell.INSTANCE.prewarm();
        TextView textView2 = this.pauseBtn;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pauseBtn");
            textView2 = null;
        }
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda9
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MainActivity.onCreate$lambda$9(MainActivity.this, view2);
            }
        });
        ImageView imageView2 = this.recenterBtn;
        if (imageView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recenterBtn");
            imageView2 = null;
        }
        imageView2.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda10
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MainActivity.this.reCenter();
            }
        });
        ImageView imageView3 = this.homeBtn;
        if (imageView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("homeBtn");
            imageView3 = null;
        }
        imageView3.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda12
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                MainActivity.onCreate$lambda$11(MainActivity.this, view2);
            }
        });
        View view2 = this.stopBtn;
        if (view2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stopBtn");
            view2 = null;
        }
        view2.setOnTouchListener(new View.OnTouchListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda13
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view3, MotionEvent motionEvent) {
                return MainActivity.onCreate$lambda$12(MainActivity.this, view3, motionEvent);
            }
        });
        View view3 = this.navBanner;
        if (view3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("navBanner");
        } else {
            view = view3;
        }
        view.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda14
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view4) {
                return MainActivity.onCreate$lambda$14(MainActivity.this, view4);
            }
        });
        Voice.INSTANCE.setEnabled(Prefs.INSTANCE.voice(this));
        Voice.INSTANCE.init(this);
        String[] strArr = this.perms;
        Collection destination$iv$iv = new ArrayList();
        for (String str : strArr) {
            if (checkSelfPermission(str) != 0) {
                destination$iv$iv.add(str);
            }
        }
        Collection missing = (List) destination$iv$iv;
        if (!missing.isEmpty()) {
            Collection $this$toTypedArray$iv = missing;
            requestPermissions((String[]) $this$toTypedArray$iv.toArray(new String[0]), 1);
        }
        uiTick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$6(final MainActivity this$0, MapLibreMap m) {
        Intrinsics.checkNotNullParameter(m, "m");
        this$0.map = m;
        m.getUiSettings().setAllGesturesEnabled(false);
        m.getUiSettings().setZoomGesturesEnabled(true);
        m.getUiSettings().setQuickZoomGesturesEnabled(false);
        m.getUiSettings().setLogoEnabled(false);
        m.getUiSettings().setAttributionEnabled(false);
        m.getUiSettings().setCompassEnabled(false);
        m.setMaxPitchPreference(72.0d);
        m.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(37.788d, -122.4431d)).zoom(18.5d).tilt(65.0d).build()));
        m.setStyle(new Style.Builder().fromJson(this$0.buildStyle()), new Style.OnStyleLoaded() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda18
            @Override // org.maplibre.android.maps.Style.OnStyleLoaded
            public final void onStyleLoaded(Style style) {
                MainActivity.onCreate$lambda$6$lambda$2(this$0, style);
            }
        });
        m.addOnMapLongClickListener(new MapLibreMap.OnMapLongClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda19
            @Override // org.maplibre.android.maps.MapLibreMap.OnMapLongClickListener
            public final boolean onMapLongClick(LatLng latLng) {
                return MainActivity.onCreate$lambda$6$lambda$3(this$0, latLng);
            }
        });
        m.addOnCameraMoveStartedListener(new MapLibreMap.OnCameraMoveStartedListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda20
            @Override // org.maplibre.android.maps.MapLibreMap.OnCameraMoveStartedListener
            public final void onCameraMoveStarted(int i) {
                MainActivity.onCreate$lambda$6$lambda$4(this$0, i);
            }
        });
        MapView mapView = this$0.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.setOnTouchListener(new View.OnTouchListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda21
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return MainActivity.onCreate$lambda$6$lambda$5(this$0, view, motionEvent);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$6$lambda$2(MainActivity this$0, Style style) {
        Intrinsics.checkNotNullParameter(style, "style");
        this$0.loadedStyle = style;
        this$0.addBikeLayer(style);
        this$0.add3dBuildings(style);
        this$0.addTrailLayer(style);
        this$0.addRouteLayer(style);
        this$0.enableLocationDot();
        this$0.applyMapPower();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean onCreate$lambda$6$lambda$3(MainActivity this$0, LatLng ll) {
        Intrinsics.checkNotNullParameter(ll, "ll");
        return this$0.startNavigation(ll.getLatitude(), ll.getLongitude());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$6$lambda$4(MainActivity this$0, int reason) {
        if (reason == 1) {
            this$0.onUserMovedMap();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean onCreate$lambda$6$lambda$5(MainActivity this$0, View view, MotionEvent ev) {
        Intrinsics.checkNotNull(ev);
        this$0.handleMapTouch(ev);
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$8(MainActivity this$0, View it) {
        Bell.INSTANCE.ring(this$0);
        this$0.showChrome();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$9(MainActivity this$0, View it) {
        RideService rideService = this$0.ride;
        if (rideService != null) {
            rideService.togglePause();
        }
        this$0.updateRecUi();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$11(MainActivity this$0, View it) {
        AppState.INSTANCE.setOnMap(false);
        this$0.startActivity(new Intent(this$0, (Class<?>) WelcomeActivity.class).addFlags(603979776));
        this$0.finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean onCreate$lambda$12(MainActivity this$0, View view, MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case 0:
                this$0.beginStopHold();
                break;
            case 1:
            case 3:
                this$0.endStopHold();
                break;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean onCreate$lambda$14(final MainActivity this$0, View it) {
        if (this$0.navigating) {
            new AlertDialog.Builder(this$0, android.R.style.Theme_Material_Dialog_Alert).setTitle("Navigation").setMessage("Stop turn-by-turn navigation?").setPositiveButton("Stop navigation", new DialogInterface.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda31
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    MainActivity.onCreate$lambda$14$lambda$13(this$0, dialogInterface, i);
                }
            }).setNegativeButton("Keep", (DialogInterface.OnClickListener) null).show();
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$14$lambda$13(MainActivity this$0, DialogInterface dialogInterface, int i) {
        this$0.cancelNav();
        Toast.makeText(this$0, "Navigation stopped", 0).show();
    }

    @Override // android.app.Activity
    public void onRequestPermissionsResult(int rc, String[] p, int[] results) {
        Intrinsics.checkNotNullParameter(p, "p");
        Intrinsics.checkNotNullParameter(results, "results");
        RideService rideService = this.ride;
        if (rideService != null) {
            rideService.retryPermissions();
        }
        enableLocationDot();
    }

    private final void bindViews() {
        View view = this.pageMap;
        View view2 = null;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view = null;
        }
        this.mapView = (MapView) view.findViewById(R.id.mapView);
        View view3 = this.pageMap;
        if (view3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view3 = null;
        }
        this.recenterBtn = (ImageView) view3.findViewById(R.id.recenter_btn);
        View view4 = this.pageMap;
        if (view4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view4 = null;
        }
        this.navBanner = view4.findViewById(R.id.nav_banner);
        View view5 = this.pageMap;
        if (view5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view5 = null;
        }
        this.navArrow = (TextView) view5.findViewById(R.id.nav_arrow);
        View view6 = this.pageMap;
        if (view6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view6 = null;
        }
        this.navText = (TextView) view6.findViewById(R.id.nav_text);
        View view7 = this.pageMap;
        if (view7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view7 = null;
        }
        this.navDist = (TextView) view7.findViewById(R.id.nav_dist);
        View view8 = this.pageMap;
        if (view8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view8 = null;
        }
        this.mSpeed = (TextView) view8.findViewById(R.id.val_speed);
        View view9 = this.pageMap;
        if (view9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view9 = null;
        }
        this.mHr = (TextView) view9.findViewById(R.id.val_hr);
        View view10 = this.pageMap;
        if (view10 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view10 = null;
        }
        this.mDist = (TextView) view10.findViewById(R.id.val_dist);
        View view11 = this.pageMap;
        if (view11 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view11 = null;
        }
        this.mTime = (TextView) view11.findViewById(R.id.val_time);
        View view12 = this.pageMap;
        if (view12 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageMap");
            view12 = null;
        }
        this.homeBtn = (ImageView) view12.findViewById(R.id.home_btn);
        View view13 = this.pageElev;
        if (view13 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageElev");
            view13 = null;
        }
        this.elevView = (ElevationView) view13.findViewById(R.id.elev_view);
        View view14 = this.pageElev;
        if (view14 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageElev");
            view14 = null;
        }
        this.eEle = (TextView) view14.findViewById(R.id.e_ele);
        View view15 = this.pageElev;
        if (view15 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageElev");
            view15 = null;
        }
        this.eAscent = (TextView) view15.findViewById(R.id.e_ascent);
        View view16 = this.pageElev;
        if (view16 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageElev");
            view16 = null;
        }
        this.eGrade = (TextView) view16.findViewById(R.id.e_grade);
        View view17 = this.pageSummary;
        if (view17 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageSummary");
            view17 = null;
        }
        this.sDist = (TextView) view17.findViewById(R.id.s_dist);
        View view18 = this.pageSummary;
        if (view18 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageSummary");
            view18 = null;
        }
        this.sTime = (TextView) view18.findViewById(R.id.s_time);
        View view19 = this.pageSummary;
        if (view19 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageSummary");
            view19 = null;
        }
        this.sAvgSpd = (TextView) view19.findViewById(R.id.s_avgspd);
        View view20 = this.pageSummary;
        if (view20 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageSummary");
            view20 = null;
        }
        this.sMaxSpd = (TextView) view20.findViewById(R.id.s_maxspd);
        View view21 = this.pageSummary;
        if (view21 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageSummary");
            view21 = null;
        }
        this.sAvgHr = (TextView) view21.findViewById(R.id.s_avghr);
        View view22 = this.pageSummary;
        if (view22 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageSummary");
            view22 = null;
        }
        this.sMaxHr = (TextView) view22.findViewById(R.id.s_maxhr);
        View view23 = this.pageSummary;
        if (view23 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageSummary");
            view23 = null;
        }
        this.sAscent = (TextView) view23.findViewById(R.id.s_ascent);
        View view24 = this.pageSummary;
        if (view24 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageSummary");
            view24 = null;
        }
        this.sStarted = (TextView) view24.findViewById(R.id.s_started);
        View view25 = this.pageHr;
        if (view25 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageHr");
            view25 = null;
        }
        this.hrBig = (TextView) view25.findViewById(R.id.hr_big);
        View view26 = this.pageHr;
        if (view26 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageHr");
            view26 = null;
        }
        this.hrZoneLbl = (TextView) view26.findViewById(R.id.hr_zone);
        View view27 = this.pageHr;
        if (view27 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageHr");
            view27 = null;
        }
        this.hrGraph = (HrGraphView) view27.findViewById(R.id.hr_graph);
        View view28 = this.pageHr;
        if (view28 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pageHr");
        } else {
            view2 = view28;
        }
        this.hrLegend = (LinearLayout) view2.findViewById(R.id.hr_legend);
        buildHrPage();
        this.recBtn = (TextView) findViewById(R.id.rec_btn);
        this.pauseBtn = (TextView) findViewById(R.id.pause_btn);
        this.stopBtn = findViewById(R.id.stop_btn);
        this.stopFill = findViewById(R.id.stop_fill);
        this.stopLabel = (TextView) findViewById(R.id.stop_label);
        this.recBar = findViewById(R.id.rec_bar);
        this.pausedBadge = (TextView) findViewById(R.id.paused_badge);
        this.dotsBar = (LinearLayout) findViewById(R.id.dots);
        this.bellBtn = (ImageView) findViewById(R.id.bell_btn);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private final View pageViewForKey(String key) {
        View view;
        String str;
        switch (key.hashCode()) {
            case -1139657850:
                if (key.equals(Pages.SUMMARY)) {
                    view = this.pageSummary;
                    if (view == null) {
                        str = "pageSummary";
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        return null;
                    }
                    return view;
                }
                return dataPageView(key);
            case 2314:
                if (key.equals(Pages.HR)) {
                    view = this.pageHr;
                    if (view == null) {
                        str = "pageHr";
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        return null;
                    }
                    return view;
                }
                return dataPageView(key);
            case 76092:
                if (key.equals(Pages.MAP)) {
                    view = this.pageMap;
                    if (view == null) {
                        str = "pageMap";
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        return null;
                    }
                    return view;
                }
                return dataPageView(key);
            case 2130840:
                if (key.equals(Pages.ELEV)) {
                    view = this.pageElev;
                    if (view == null) {
                        str = "pageElev";
                        Intrinsics.throwUninitializedPropertyAccessException(str);
                        return null;
                    }
                    return view;
                }
                return dataPageView(key);
            default:
                return dataPageView(key);
        }
    }

    private final View dataPageView(final String key) {
        View v = this.dataPages.get(key);
        if (v == null) {
            v = LayoutInflater.from(this).inflate(R.layout.page_data, (ViewGroup) null);
            final DashboardView dv = (DashboardView) v.findViewById(R.id.dash_view);
            final View toolbar = v.findViewById(R.id.dash_toolbar);
            dv.setValueProvider(new Function1() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return MainActivity.dataPageView$lambda$23$lambda$16(MainActivity.this, (Metric) obj);
                }
            });
            dv.setOnChanged(new Function0() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda11
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return MainActivity.dataPageView$lambda$23$lambda$17(MainActivity.this, key, dv);
                }
            });
            dv.setOnPickMetric(new Function1() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda22
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return MainActivity.dataPageView$lambda$23$lambda$18(MainActivity.this, dv, (DashTile) obj);
                }
            });
            dv.setOnEditModeChanged(new Function1() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda26
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return MainActivity.dataPageView$lambda$23$lambda$19(toolbar, MainActivity.this, ((Boolean) obj).booleanValue());
                }
            });
            dv.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda27
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    return MainActivity.dataPageView$lambda$23$lambda$20(dv, view);
                }
            });
            v.findViewById(R.id.dash_add).setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda28
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    MainActivity.dataPageView$lambda$23$lambda$21(dv, view);
                }
            });
            v.findViewById(R.id.dash_done).setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda29
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    dv.setEditing(false);
                }
            });
            dv.setTiles(Prefs.INSTANCE.dashTiles(this, key));
            this.dataPages.put(key, v);
        }
        DashboardView it = (DashboardView) v.findViewById(R.id.dash_view);
        if (it != null && !this.dashViews.contains(it)) {
            this.dashViews.add(it);
        }
        Intrinsics.checkNotNull(v);
        return v;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String dataPageView$lambda$23$lambda$16(MainActivity this$0, Metric m) {
        Intrinsics.checkNotNullParameter(m, "m");
        return this$0.metricValue(m);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit dataPageView$lambda$23$lambda$17(MainActivity this$0, String $key, DashboardView $dv) {
        Prefs.INSTANCE.setDashTiles(this$0, $key, $dv.tiles());
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit dataPageView$lambda$23$lambda$18(MainActivity this$0, DashboardView $dv, DashTile tile) {
        Intrinsics.checkNotNullParameter(tile, "tile");
        Intrinsics.checkNotNull($dv);
        this$0.showMetricPicker($dv, tile);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit dataPageView$lambda$23$lambda$19(View $toolbar, MainActivity this$0, boolean on) {
        $toolbar.setVisibility(on ? 0 : 8);
        ViewPager2 viewPager2 = this$0.pager;
        if (viewPager2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
            viewPager2 = null;
        }
        viewPager2.setUserInputEnabled(!on);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean dataPageView$lambda$23$lambda$20(DashboardView $dv, View it) {
        if (!$dv.getEditing()) {
            $dv.setEditing(true);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void dataPageView$lambda$23$lambda$21(DashboardView $dv, View it) {
        $dv.addTile(Metric.SPEED);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0052  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final void buildPager(boolean goToMap) {
        int current = 0;
        ViewPager2 viewPager2;
        List<String> listEnabledPagesInOrder = Prefs.INSTANCE.enabledPagesInOrder(this);
        this.pageSig = Prefs.INSTANCE.pageSignature(this);
        this.mapPageIndex = RangesKt.coerceAtLeast(listEnabledPagesInOrder.indexOf(Pages.MAP), 0);
        if (this.pager == null) {
            current = this.mapPageIndex;
        } else {
            ViewPager2 viewPager22 = this.pager;
            if (viewPager22 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("pager");
                viewPager22 = null;
            }
            if (viewPager22.getAdapter() != null) {
                ViewPager2 viewPager23 = this.pager;
                if (viewPager23 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("pager");
                    viewPager23 = null;
                }
                current = RangesKt.coerceIn(viewPager23.getCurrentItem(), 0, RangesKt.coerceAtLeast(listEnabledPagesInOrder.size() - 1, 0));
            }
        }
        this.dashViews.clear();
        ViewPager2 viewPager24 = this.pager;
        if (viewPager24 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
            viewPager24 = null;
        }
        List<String> $this$map$iv = listEnabledPagesInOrder;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            String it = (String) item$iv$iv;
            destination$iv$iv.add(pageViewForKey(it));
        }
        viewPager24.setAdapter(new PageAdapter((List) destination$iv$iv));
        ViewPager2 viewPager25 = this.pager;
        if (viewPager25 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
            viewPager25 = null;
        }
        viewPager25.setOffscreenPageLimit(RangesKt.coerceAtLeast(listEnabledPagesInOrder.size() - 1, 1));
        LinearLayout linearLayout = this.dotsBar;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dotsBar");
            linearLayout = null;
        }
        linearLayout.removeAllViews();
        Iterable $this$map$iv2 = CollectionsKt.getIndices(listEnabledPagesInOrder);
        int $i$f$map = 0;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv2, 10));
        Iterator<Integer> it2 = $this$map$iv2.iterator();
        while (it2.hasNext()) {
            ((IntIterator) it2).nextInt();
            TextView $this$buildPager_u24lambda_u2428_u24lambda_u2427 = new TextView(this);
            List<String> list = listEnabledPagesInOrder;
            $this$buildPager_u24lambda_u2428_u24lambda_u2427.setText("●");
            $this$buildPager_u24lambda_u2428_u24lambda_u2427.setTextColor(-1);
            $this$buildPager_u24lambda_u2428_u24lambda_u2427.setTextSize(12.0f);
            LinearLayout.LayoutParams $this$buildPager_u24lambda_u2428_u24lambda_u2427_u24lambda_u2426 = new LinearLayout.LayoutParams(-2, -2);
            int $i$f$map2 = $i$f$map;
            int $i$f$map3 = dp(4);
            $this$buildPager_u24lambda_u2428_u24lambda_u2427_u24lambda_u2426.leftMargin = $i$f$map3;
            $this$buildPager_u24lambda_u2428_u24lambda_u2427_u24lambda_u2426.rightMargin = dp(4);
            $this$buildPager_u24lambda_u2428_u24lambda_u2427.setLayoutParams($this$buildPager_u24lambda_u2428_u24lambda_u2427_u24lambda_u2426);
            destination$iv$iv2.add($this$buildPager_u24lambda_u2428_u24lambda_u2427);
            current = current;
            listEnabledPagesInOrder = list;
            $this$map$iv2 = $this$map$iv2;
            $i$f$map = $i$f$map2;
        }
        int current2 = current;
        this.dots = (List) destination$iv$iv2;
        Iterable $this$forEach$iv = this.dots;
        for (Object element$iv : $this$forEach$iv) {
            TextView it3 = (TextView) element$iv;
            LinearLayout linearLayout2 = this.dotsBar;
            if (linearLayout2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("dotsBar");
                linearLayout2 = null;
            }
            linearLayout2.addView(it3);
        }
        int start = goToMap ? this.mapPageIndex : current2;
        ViewPager2 viewPager26 = this.pager;
        if (viewPager26 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
            viewPager2 = null;
        } else {
            viewPager2 = viewPager26;
        }
        viewPager2.setCurrentItem(start, false);
        Iterable $this$forEachIndexed$iv = this.dots;
        int i = 0;
        for (Object item$iv : $this$forEachIndexed$iv) {
            int index$iv = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            TextView d = (TextView) item$iv;
            d.setAlpha(i == start ? 1.0f : 0.35f);
            i = index$iv;
        }
    }

    /* JADX INFO: compiled from: MainActivity.kt */
    private static final class PageAdapter extends RecyclerView.Adapter<PageAdapter.VH> {
        private final List<? extends View> pages;

        /* JADX INFO: compiled from: MainActivity.kt */
        public static final class VH extends RecyclerView.ViewHolder {
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public VH(View v) {
                super(v);
                Intrinsics.checkNotNullParameter(v, "v");
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public PageAdapter(List<? extends View> pages) {
            Intrinsics.checkNotNullParameter(pages, "pages");
            this.pages = pages;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.pages.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int position) {
            return position;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            Intrinsics.checkNotNullParameter(parent, "parent");
            View v = this.pages.get(viewType);
            ViewParent parent2 = v.getParent();
            ViewGroup viewGroup = parent2 instanceof ViewGroup ? (ViewGroup) parent2 : null;
            if (viewGroup != null) {
                viewGroup.removeView(v);
            }
            v.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            return new VH(v);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(VH holder, int position) {
            Intrinsics.checkNotNullParameter(holder, "holder");
        }
    }

    private final String buildStyle() {
        try {
            JSONObject o = new JSONObject(FilesKt.readText(new File(this.DATA + "/styles/style.json"), kotlin.text.Charsets.UTF_8));
            JSONObject src = new JSONObject().put("type", "vector").put("tiles", new JSONArray().put("http://bike.local/tiles/{z}/{x}/{y}.pbf")).put("minzoom", 0).put("maxzoom", 14);
            JSONObject sources = o.getJSONObject("sources");
            Iterator<String> itKeys = sources.keys();
            Intrinsics.checkNotNullExpressionValue(itKeys, "keys(...)");
            for (String k : SequencesKt.toList(SequencesKt.asSequence(itKeys))) {
                sources.put(k, src);
            }
            o.put("glyphs", "http://bike.local/fonts/{fontstack}/{range}.pbf");
            o.remove("sprite");
            String string = o.toString();
            Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
            return string;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private final void add3dBuildings(Style style) {
        Object element$iv;
        try {
            if (style.getLayer("3d-buildings") != null) {
                return;
            }
            FillExtrusionLayer $this$add3dBuildings_u24lambda_u2431 = new FillExtrusionLayer("3d-buildings", "openmaptiles");
            $this$add3dBuildings_u24lambda_u2431.setSourceLayer("building");
            $this$add3dBuildings_u24lambda_u2431.setMinZoom(15.0f);
            $this$add3dBuildings_u24lambda_u2431.setFilter(Expression.gte(Expression.get("render_height"), Expression.literal((Number) Float.valueOf(5.0f))));
            $this$add3dBuildings_u24lambda_u2431.setProperties(PropertyFactory.fillExtrusionColor(Color.parseColor("#e6e0d2")), PropertyFactory.fillExtrusionHeight(Expression.get("render_height")), PropertyFactory.fillExtrusionBase(Expression.get("render_min_height")), PropertyFactory.fillExtrusionOpacity(Float.valueOf(1.0f)), PropertyFactory.fillExtrusionVerticalGradient((Boolean) false));
            Iterable layers = style.getLayers();
            Intrinsics.checkNotNullExpressionValue(layers, "getLayers(...)");
            Iterable $this$firstOrNull$iv = layers;
            Iterator it = $this$firstOrNull$iv.iterator();
            while (true) {
                if (!it.hasNext()) {
                    element$iv = null;
                    break;
                }
                element$iv = it.next();
                Layer it2 = (Layer) element$iv;
                if (it2 instanceof SymbolLayer) {
                    break;
                }
            }
            Layer firstSymbol = (Layer) element$iv;
            if (firstSymbol != null) {
                style.addLayerBelow($this$add3dBuildings_u24lambda_u2431, firstSymbol.getId());
            } else {
                style.addLayer($this$add3dBuildings_u24lambda_u2431);
            }
        } catch (Exception e) {
            Log.e("BikeMap", "3d buildings failed", e);
        }
    }

    private final void addBikeLayer(Style style) {
        Object element$iv;
        try {
            if (style.getLayer("bike-paths") != null) {
                return;
            }
            Expression filter = Expression.any(Expression.eq(Expression.get("subclass"), Expression.literal("cycleway")), Expression.eq(Expression.get("class"), Expression.literal("cycleway")), Expression.eq(Expression.get("bicycle"), Expression.literal("designated")));
            LineLayer $this$addBikeLayer_u24lambda_u2433 = new LineLayer("bike-paths-casing", "openmaptiles");
            $this$addBikeLayer_u24lambda_u2433.setSourceLayer("transportation");
            $this$addBikeLayer_u24lambda_u2433.setMinZoom(11.0f);
            $this$addBikeLayer_u24lambda_u2433.setFilter(filter);
            $this$addBikeLayer_u24lambda_u2433.setProperties(PropertyFactory.lineColor(Color.parseColor("#FFFFFF")), PropertyFactory.lineCap("round"), PropertyFactory.lineJoin("round"), PropertyFactory.lineOpacity(Float.valueOf(0.85f)), PropertyFactory.lineWidth(addBikeLayer$width(1.6f)));
            LineLayer $this$addBikeLayer_u24lambda_u2434 = new LineLayer("bike-paths", "openmaptiles");
            $this$addBikeLayer_u24lambda_u2434.setSourceLayer("transportation");
            $this$addBikeLayer_u24lambda_u2434.setMinZoom(11.0f);
            $this$addBikeLayer_u24lambda_u2434.setFilter(filter);
            $this$addBikeLayer_u24lambda_u2434.setProperties(PropertyFactory.lineColor(Color.parseColor("#00C853")), PropertyFactory.lineCap("round"), PropertyFactory.lineJoin("round"), PropertyFactory.lineOpacity(Float.valueOf(0.95f)), PropertyFactory.lineWidth(addBikeLayer$width(1.0f)));
            Iterable layers = style.getLayers();
            Intrinsics.checkNotNullExpressionValue(layers, "getLayers(...)");
            Iterable $this$firstOrNull$iv = layers;
            Iterator it = $this$firstOrNull$iv.iterator();
            while (true) {
                if (!it.hasNext()) {
                    element$iv = null;
                    break;
                }
                element$iv = it.next();
                Layer it2 = (Layer) element$iv;
                if (it2 instanceof SymbolLayer) {
                    break;
                }
            }
            Layer firstSymbol = (Layer) element$iv;
            if (firstSymbol != null) {
                style.addLayerBelow($this$addBikeLayer_u24lambda_u2433, firstSymbol.getId());
                style.addLayerBelow($this$addBikeLayer_u24lambda_u2434, firstSymbol.getId());
            } else {
                style.addLayer($this$addBikeLayer_u24lambda_u2433);
                style.addLayer($this$addBikeLayer_u24lambda_u2434);
            }
        } catch (Exception e) {
            Log.e("BikeMap", "bike layer failed", e);
        }
    }

    private static final Expression addBikeLayer$width(float mult) {
        return Expression.interpolate(Expression.linear(), Expression.zoom(), Expression.stop(11, Float.valueOf(2.0f * mult)), Expression.stop(14, Float.valueOf(3.6f * mult)), Expression.stop(16, Float.valueOf(6.0f * mult)), Expression.stop(18, Float.valueOf(9.0f * mult)));
    }

    private final void addTrailLayer(Style style) {
        try {
            this.trailSource = new GeoJsonSource("trail");
            GeoJsonSource geoJsonSource = this.trailSource;
            Intrinsics.checkNotNull(geoJsonSource);
            style.addSource(geoJsonSource);
            style.addLayer(new LineLayer("trail-line", "trail").withProperties(PropertyFactory.lineColor(Color.parseColor("#FF6D00")), PropertyFactory.lineWidth(Float.valueOf(6.0f)), PropertyFactory.lineOpacity(Float.valueOf(0.95f)), PropertyFactory.lineCap("round"), PropertyFactory.lineJoin("round")));
        } catch (Exception e) {
            Log.e("BikeMap", "trail layer failed", e);
        }
    }

    private final void addRouteLayer(Style style) {
        try {
            this.routeSource = new GeoJsonSource("route");
            GeoJsonSource geoJsonSource = this.routeSource;
            Intrinsics.checkNotNull(geoJsonSource);
            style.addSource(geoJsonSource);
            style.addLayer(new LineLayer("route-casing", "route").withProperties(PropertyFactory.lineColor(Color.parseColor("#0B3D91")), PropertyFactory.lineWidth(Float.valueOf(12.0f)), PropertyFactory.lineOpacity(Float.valueOf(0.95f)), PropertyFactory.lineCap("round"), PropertyFactory.lineJoin("round")));
            style.addLayer(new LineLayer("route-line", "route").withProperties(PropertyFactory.lineColor(Color.parseColor("#4C8DFF")), PropertyFactory.lineWidth(Float.valueOf(7.0f)), PropertyFactory.lineOpacity(Float.valueOf(1.0f)), PropertyFactory.lineCap("round"), PropertyFactory.lineJoin("round")));
        } catch (Exception e) {
            Log.e("BikeMap", "route layer failed", e);
        }
    }

    private final boolean startNavigation(double dLat, double dLon) {
        RideService rideService = this.ride;
        Location from = rideService != null ? rideService.getLastLocation() : null;
        if (from == null) {
            Toast.makeText(this, "No GPS fix yet", 0).show();
            return true;
        }
        this.routeVias = null;
        this.currentRouteName = null;
        this.destLat = dLat;
        this.destLon = dLon;
        this.announcedIdx = -1;
        this.earlyAnnouncedIdx = -1;
        this.offRouteSince = 0L;
        Toast.makeText(this, "Routing…", 0).show();
        computeRoute(from.getLatitude(), from.getLongitude(), true);
        return true;
    }

    private final void startRouteNavigation(List<double[]> vias) {
        RideService rideService = this.ride;
        ViewPager2 viewPager2 = null;
        Location from = rideService != null ? rideService.getLastLocation() : null;
        if (from == null) {
            Toast.makeText(this, "No GPS fix yet — try again in a moment", 1).show();
            return;
        }
        if (vias.size() < 2) {
            Toast.makeText(this, "Route has no points", 0).show();
            return;
        }
        this.routeVias = vias;
        this.destLat = ((double[]) CollectionsKt.last((List) vias))[1];
        this.destLon = ((double[]) CollectionsKt.last((List) vias))[0];
        this.announcedIdx = -1;
        this.earlyAnnouncedIdx = -1;
        this.offRouteSince = 0L;
        ViewPager2 viewPager22 = this.pager;
        if (viewPager22 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
        } else {
            viewPager2 = viewPager22;
        }
        viewPager2.setCurrentItem(this.mapPageIndex, false);
        Toast.makeText(this, "Snapping route to roads…", 0).show();
        computeRoute(from.getLatitude(), from.getLongitude(), true);
    }

    private final void reroute(double fromLat, double fromLon) {
        Voice.INSTANCE.cue("Rerouting");
        boolean z = false;
        Toast.makeText(this, "Off route — rerouting…", 0).show();
        List<double[]> list = this.routeVias;
        if (list != null) {
            int size = list.size();
            int k = 0;
            double best = Double.MAX_VALUE;
            int i = 0;
            while (i < size) {
                int i2 = i;
                double d = hav(fromLat, fromLon, list.get(i)[1], list.get(i)[0]);
                if (d < best) {
                    best = d;
                    k = i2;
                }
                i = i2 + 1;
            }
            if (1 <= k && k < list.size()) {
                z = true;
            }
            if (z) {
                this.routeVias = CollectionsKt.toList(list.subList(k, list.size()));
            }
        }
        computeRoute(fromLat, fromLon, false);
    }

    private final void computeRoute(final double fromLat, final double fromLon, final boolean initial) {
        this.lastRerouteMs = System.currentTimeMillis();
        new Thread(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.computeRoute$lambda$43(MainActivity.this, fromLon, fromLat, initial);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void computeRoute$lambda$43(final MainActivity this$0, double $fromLon, double $fromLat, final boolean $initial) {
        List<double[]> listListOf;
        final List named;
        List<double[]> list;
        int $i$f$map;
        Iterable $this$mapTo$iv$iv;
        List<double[]> list2 = this$0.routeVias;
        if (list2 == null || (listListOf = CollectionsKt.plus((Collection) CollectionsKt.listOf(new double[]{$fromLon, $fromLat}), (Iterable) list2)) == null) {
            listListOf = CollectionsKt.listOf((double[][]) new double[][]{new double[]{$fromLon, $fromLat}, new double[]{this$0.destLon, this$0.destLat}});
        }
        final RouteResult res = BikeRouter.INSTANCE.route(this$0.DATA + "/routing", this$0.DATA + "/routing/trekking.brf", listListOf);
        if (res != null) {
            RouteResult it = res;
            int i = 0;
            StreetNames.INSTANCE.open(this$0.getFilesDir() + "/california.mbtiles");
            Iterable $this$map$iv = it.getSteps();
            int $i$f$map2 = 0;
            Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
            Iterable $this$mapTo$iv$iv2 = $this$map$iv;
            for (Object item$iv$iv : $this$mapTo$iv$iv2) {
                NavHint s = (NavHint) item$iv$iv;
                RouteResult it2 = it;
                int i2 = i;
                Iterable $this$map$iv2 = $this$map$iv;
                String nm = StreetNames.INSTANCE.nameAt(s.lat, s.lon);
                if (nm.length() > 0) {
                    $i$f$map = $i$f$map2;
                    list = listListOf;
                    $this$mapTo$iv$iv = $this$mapTo$iv$iv2;
                    s = new NavHint(s.lat, s.lon, s.indexInTrack, s.cmd, nm);
                } else {
                    list = listListOf;
                    $i$f$map = $i$f$map2;
                    $this$mapTo$iv$iv = $this$mapTo$iv$iv2;
                }
                destination$iv$iv.add(s);
                it = it2;
                i = i2;
                $this$map$iv = $this$map$iv2;
                $i$f$map2 = $i$f$map;
                listListOf = list;
                $this$mapTo$iv$iv2 = $this$mapTo$iv$iv;
            }
            named = (List) destination$iv$iv;
        } else {
            named = null;
        }
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.computeRoute$lambda$43$lambda$42(res, this$0, $initial, named);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void computeRoute$lambda$43$lambda$42(RouteResult $res, MainActivity this$0, boolean $initial, List $named) {
        Location it;
        if ($res != null) {
            GeoJsonSource geoJsonSource = this$0.routeSource;
            char c = 0;
            if (geoJsonSource != null) {
                Iterable $this$map$iv = $res.getPoints();
                Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                for (Object item$iv$iv : $this$map$iv) {
                    double[] it2 = (double[]) item$iv$iv;
                    destination$iv$iv.add(Point.fromLngLat(it2[c], it2[1]));
                    $this$map$iv = $this$map$iv;
                    c = 0;
                }
                geoJsonSource.setGeoJson(LineString.fromLngLats((List<Point>) destination$iv$iv));
            }
            this$0.navPoints = $res.getPoints();
            this$0.navSteps = $named == null ? $res.getSteps() : $named;
            this$0.announcedIdx = -1;
            this$0.earlyAnnouncedIdx = -1;
            this$0.offRouteSince = 0L;
            this$0.navigating = true;
            ActionBus.INSTANCE.setNavigating(true);
            View view = this$0.navBanner;
            if (view == null) {
                Intrinsics.throwUninitializedPropertyAccessException("navBanner");
                view = null;
            }
            view.setVisibility(0);
            this$0.updateRecUi();
            this$0.reCenter();
            if ($initial) {
                StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                String mi = String.format(Locale.US, "%.1f", Arrays.copyOf(new Object[]{Double.valueOf(Units.INSTANCE.miles($res.getDistanceM()))}, 1));
                Intrinsics.checkNotNullExpressionValue(mi, "format(...)");
                Toast.makeText(this$0, "Route: " + mi + " mi · " + $res.getSteps().size() + " turns", 1).show();
            }
            RideService rideService = this$0.ride;
            if (rideService != null && (it = rideService.getLastLocation()) != null) {
                this$0.updateNav(it.getLatitude(), it.getLongitude());
                return;
            }
            return;
        }
        Toast.makeText(this$0, $initial ? "No route found" : "Reroute failed", 1).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void enableLocationDot() {
        Style style;
        Location it;
        MapLibreMap m = this.map;
        if (m == null || (style = this.loadedStyle) == null || checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != 0) {
            return;
        }
        LocationComponent lc = m.getLocationComponent();
        Intrinsics.checkNotNullExpressionValue(lc, "getLocationComponent(...)");
        if (!lc.isLocationComponentActivated()) {
            lc.activateLocationComponent(LocationComponentActivationOptions.builder(this, style).useDefaultLocationEngine(false).build());
        }
        lc.setLocationComponentEnabled(true);
        lc.setRenderMode(8);
        MapView mapView = this.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.post(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda25
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.enableLocationDot$lambda$44(MainActivity.this);
            }
        });
        RideService rideService = this.ride;
        if (rideService != null && (it = rideService.getLastLocation()) != null) {
            lc.forceLocationUpdate(it);
        }
        lc.addOnCameraTrackingChangedListener(new OnCameraTrackingChangedListener() { // from class: com.bike.computer.MainActivity.enableLocationDot.3
            @Override // org.maplibre.android.location.OnCameraTrackingChangedListener
            public void onCameraTrackingDismissed() {
                MainActivity.this.cameraTracking = false;
                ViewPager2 viewPager2 = MainActivity.this.pager;
                ImageView imageView = null;
                if (viewPager2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("pager");
                    viewPager2 = null;
                }
                if (viewPager2.getCurrentItem() == MainActivity.this.mapPageIndex) {
                    ImageView imageView2 = MainActivity.this.recenterBtn;
                    if (imageView2 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("recenterBtn");
                    } else {
                        imageView = imageView2;
                    }
                    imageView.setVisibility(0);
                }
            }

            @Override // org.maplibre.android.location.OnCameraTrackingChangedListener
            public void onCameraTrackingChanged(int currentMode) {
            }
        });
        reCenter();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void enableLocationDot$lambda$44(MainActivity this$0) {
        MapLibreMap mapLibreMap = this$0.map;
        if (mapLibreMap != null) {
            MapView mapView = this$0.mapView;
            if (mapView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mapView");
                mapView = null;
            }
            mapLibreMap.setPadding(0, (int) (mapView.getHeight() * 0.55f), 0, 0);
        }
    }

    private final void handleMapTouch(MotionEvent ev) {
        MapView mapView = null;
        switch (ev.getActionMasked()) {
            case 1:
            case 3:
                this.twoFingerActive = false;
                MapView mapView2 = this.mapView;
                if (mapView2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mapView");
                } else {
                    mapView = mapView2;
                }
                ViewParent parent = mapView.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
                break;
            case 2:
                if (this.twoFingerActive && ev.getPointerCount() >= 2) {
                    float fx = (ev.getX(0) + ev.getX(1)) / 2.0f;
                    float fy = (ev.getY(0) + ev.getY(1)) / 2.0f;
                    float dx = fx - this.lastFocusX;
                    float dy = fy - this.lastFocusY;
                    this.lastFocusX = fx;
                    this.lastFocusY = fy;
                    if (dx == 0.0f) {
                        if (dy == 0.0f) {
                        }
                    }
                    onUserMovedMap();
                    MapLibreMap mapLibreMap = this.map;
                    if (mapLibreMap != null) {
                        mapLibreMap.scrollBy(dx, dy);
                    }
                    break;
                }
                break;
            case 5:
                if (ev.getPointerCount() == 2) {
                    this.twoFingerActive = true;
                    this.lastFocusX = (ev.getX(0) + ev.getX(1)) / 2.0f;
                    this.lastFocusY = (ev.getY(0) + ev.getY(1)) / 2.0f;
                    MapView mapView3 = this.mapView;
                    if (mapView3 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("mapView");
                    } else {
                        mapView = mapView3;
                    }
                    ViewParent parent2 = mapView.getParent();
                    if (parent2 != null) {
                        parent2.requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
            case 6:
                if (ev.getPointerCount() <= 2) {
                    this.twoFingerActive = false;
                }
                break;
        }
    }

    private final void onUserMovedMap() {
        LocationComponent it;
        if (this.cameraTracking) {
            this.cameraTracking = false;
            MapLibreMap mapLibreMap = this.map;
            if (mapLibreMap != null && (it = mapLibreMap.getLocationComponent()) != null && it.isLocationComponentActivated()) {
                it.setCameraMode(8);
            }
            ViewPager2 viewPager2 = this.pager;
            ImageView imageView = null;
            if (viewPager2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("pager");
                viewPager2 = null;
            }
            if (viewPager2.getCurrentItem() == this.mapPageIndex) {
                ImageView imageView2 = this.recenterBtn;
                if (imageView2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("recenterBtn");
                } else {
                    imageView = imageView2;
                }
                imageView.setVisibility(0);
            }
        }
    }

    private final boolean mapLowPower() {
        return Prefs.INSTANCE.lowPowerMap(this) || Prefs.INSTANCE.endurance(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void reCenter() {
        MapLibreMap m = this.map;
        if (m == null) {
            return;
        }
        LocationComponent lc = m.getLocationComponent();
        Intrinsics.checkNotNullExpressionValue(lc, "getLocationComponent(...)");
        if (lc.isLocationComponentActivated()) {
            lc.setCameraMode(34);
            lc.tiltWhileTracking(mapLowPower() ? 0.0d : 65.0d);
            this.cameraTracking = true;
            ImageView imageView = this.recenterBtn;
            if (imageView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("recenterBtn");
                imageView = null;
            }
            imageView.setVisibility(8);
        }
    }

    private final void applyMapPower() {
        boolean endurance = Prefs.INSTANCE.endurance(this);
        boolean low = Prefs.INSTANCE.lowPowerMap(this) || endurance;
        try {
            MapView mapView = this.mapView;
            if (mapView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mapView");
                mapView = null;
            }
            mapView.setMaximumFps(endurance ? 10 : low ? 15 : 30);
        } catch (Throwable th) {
        }
        Style style = this.loadedStyle;
        if (style != null) {
            boolean has = style.getLayer("3d-buildings") != null;
            if (low && has) {
                try {
                    style.removeLayer("3d-buildings");
                } catch (Throwable th2) {
                }
            } else if (!low && !has) {
                add3dBuildings(style);
            }
        }
        double tilt = low ? 0.0d : 65.0d;
        MapLibreMap m = this.map;
        if (m != null && m.getLocationComponent().isLocationComponentActivated() && this.cameraTracking) {
            CameraPosition cur = m.getCameraPosition();
            Intrinsics.checkNotNullExpressionValue(cur, "getCameraPosition(...)");
            m.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(cur.target).zoom(cur.zoom).bearing(cur.bearing).tilt(tilt).build()));
            reCenter();
        }
        WindowManager.LayoutParams a = getWindow().getAttributes();
        a.screenBrightness = endurance ? 0.15f : -1.0f;
        getWindow().setAttributes(a);
    }

    private final Location mapLoc(Location raw, float speedMps) {
        double d;
        Location out = new Location(raw);
        if (speedMps >= 1.4f && raw.hasBearing()) {
            if (Double.isNaN(this.mapBearing)) {
                d = raw.getBearing();
            } else {
                double d2 = 360;
                double d3 = (((((double) raw.getBearing()) - this.mapBearing) + ((double) 540)) % d2) - ((double) 180);
                d = ((this.mapBearing + (0.2d * d3)) + d2) % d2;
            }
            this.mapBearing = d;
        }
        if (!Double.isNaN(this.mapBearing)) {
            out.setBearing((float) this.mapBearing);
        }
        return out;
    }

    private final void speedZoom(float mps) {
        LocationComponent lc;
        MapLibreMap mapLibreMap = this.map;
        if (mapLibreMap == null || (lc = mapLibreMap.getLocationComponent()) == null || !lc.isLocationComponentActivated()) {
            return;
        }
        float z = RangesKt.coerceIn(18.7f - ((Units.INSTANCE.mph(mps) / 20.0f) * 2.2f), 16.5f, 18.7f);
        lc.zoomWhileTracking(z);
    }

    private final void tryConsumePendingRoute() {
        double[] dest = ActionBus.INSTANCE.getPendingDestination();
        ViewPager2 viewPager2 = null;
        if (dest != null) {
            RideService rideService = this.ride;
            if ((rideService != null ? rideService.getLastLocation() : null) == null) {
                return;
            }
            ActionBus.INSTANCE.setPendingDestination(null);
            ViewPager2 viewPager22 = this.pager;
            if (viewPager22 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("pager");
            } else {
                viewPager2 = viewPager22;
            }
            viewPager2.setCurrentItem(this.mapPageIndex, false);
            startNavigation(dest[0], dest[1]);
            return;
        }
        List<double[]> pendingRoute = ActionBus.INSTANCE.getPendingRoute();
        if (pendingRoute == null) {
            return;
        }
        RideService rideService2 = this.ride;
        if ((rideService2 != null ? rideService2.getLastLocation() : null) == null) {
            return;
        }
        ActionBus.INSTANCE.setPendingRoute(null);
        this.currentRouteName = ActionBus.INSTANCE.getPendingRouteName();
        ActionBus.INSTANCE.setPendingRouteName(null);
        startRouteNavigation(pendingRoute);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void onRideUpdate() {
        Location it;
        LocationComponent it2;
        RideService r = this.ride;
        if (r == null) {
            return;
        }
        tryConsumePendingRoute();
        Location loc = r.getLastLocation();
        if (loc != null) {
            MapLibreMap mapLibreMap = this.map;
            if (mapLibreMap != null && (it2 = mapLibreMap.getLocationComponent()) != null && it2.isLocationComponentActivated()) {
                it2.forceLocationUpdate(mapLoc(loc, r.getCurSpeedMps()));
            }
            speedZoom(r.getCurSpeedMps());
        }
        if (r.getTrail().size() != this.lastTrailSize) {
            this.lastTrailSize = r.getTrail().size();
            if (r.getTrail().size() >= 2) {
                GeoJsonSource geoJsonSource = this.trailSource;
                if (geoJsonSource != null) {
                    Iterable $this$map$iv = r.getTrail();
                    Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
                    for (Object item$iv$iv : $this$map$iv) {
                        double[] it3 = (double[]) item$iv$iv;
                        destination$iv$iv.add(Point.fromLngLat(it3[0], it3[1]));
                    }
                    geoJsonSource.setGeoJson(LineString.fromLngLats((List<Point>) destination$iv$iv));
                }
            } else {
                GeoJsonSource geoJsonSource2 = this.trailSource;
                if (geoJsonSource2 != null) {
                    geoJsonSource2.setGeoJson("{\"type\":\"FeatureCollection\",\"features\":[]}");
                }
            }
        }
        TextView textView = this.mSpeed;
        TextView textView2 = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSpeed");
            textView = null;
        }
        textView.setText(Units.INSTANCE.fmtSpeed(r.getCurSpeedMps()));
        TextView textView3 = this.mHr;
        if (textView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mHr");
        } else {
            textView2 = textView3;
        }
        textView2.setText(r.getLastHr() > 0 ? String.valueOf(r.getLastHr()) : "--");
        if (this.navigating && (it = r.getLastLocation()) != null) {
            updateNav(it.getLatitude(), it.getLongitude());
        }
    }

    private final void updateNav(double lat, double lon) {
        Object element$iv;
        if (!this.navigating || this.navPoints.isEmpty()) {
            return;
        }
        int size = this.navPoints.size();
        int nearIdx = 0;
        double nearD = Double.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            double d = hav(lat, lon, this.navPoints.get(i)[1], this.navPoints.get(i)[0]);
            if (d < nearD) {
                nearD = d;
                nearIdx = i;
            }
        }
        double[] dest = (double[]) CollectionsKt.last((List) this.navPoints);
        double distToDest = hav(lat, lon, dest[1], dest[0]);
        if (distToDest < 25.0d) {
            TextView textView = this.navArrow;
            if (textView == null) {
                Intrinsics.throwUninitializedPropertyAccessException("navArrow");
                textView = null;
            }
            textView.setText("◉");
            TextView textView2 = this.navText;
            if (textView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("navText");
                textView2 = null;
            }
            textView2.setText("Arrived");
            TextView textView3 = this.navDist;
            if (textView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("navDist");
                textView3 = null;
            }
            textView3.setText("");
            if (this.announcedIdx != -999) {
                this.announcedIdx = -999;
                Voice.INSTANCE.cue("You have arrived");
                this.ui.postDelayed(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda23
                    @Override // java.lang.Runnable
                    public final void run() {
                        MainActivity.updateNav$lambda$54(MainActivity.this);
                    }
                }, 6000L);
                return;
            }
            return;
        }
        double offD = minDistToRoute(lat, lon);
        long now = System.currentTimeMillis();
        if (offD > 40.0d) {
            if (this.offRouteSince == 0) {
                this.offRouteSince = now;
            }
            if (now - this.offRouteSince > 5000 && now - this.lastRerouteMs > 12000) {
                this.offRouteSince = 0L;
                reroute(lat, lon);
                return;
            }
        } else {
            this.offRouteSince = 0L;
        }
        Iterable $this$firstOrNull$iv = this.navSteps;
        Iterator it = $this$firstOrNull$iv.iterator();
        while (true) {
            if (it.hasNext()) {
                element$iv = it.next();
                NavHint it2 = (NavHint) element$iv;
                if (it2.indexInTrack > nearIdx) {
                    break;
                }
            } else {
                element$iv = null;
                break;
            }
        }
        NavHint next = (NavHint) element$iv;
        if (next != null) {
            Pair<String, String> pairManeuver = maneuver(next.cmd);
            String arrow = pairManeuver.component1();
            String label = pairManeuver.component2();
            TextView textView4 = this.navArrow;
            if (textView4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("navArrow");
                textView4 = null;
            }
            textView4.setText(arrow);
            String instr = next.street.length() > 0 ? label + " onto " + next.street : label;
            TextView textView5 = this.navText;
            if (textView5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("navText");
                textView5 = null;
            }
            textView5.setText(instr);
            double dTurn = hav(lat, lon, next.lat, next.lon);
            TextView textView6 = this.navDist;
            if (textView6 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("navDist");
                textView6 = null;
            }
            textView6.setText(fmtDistTo(dTurn));
            if ((80.0d <= dTurn && dTurn <= 220.0d) && this.earlyAnnouncedIdx != next.indexInTrack) {
                this.earlyAnnouncedIdx = next.indexInTrack;
                Voice.INSTANCE.cue("In " + fmtDistTo(dTurn) + ", " + instr);
            }
            if (dTurn < 45.0d && this.announcedIdx != next.indexInTrack) {
                this.announcedIdx = next.indexInTrack;
                Voice.INSTANCE.cue(instr);
                return;
            }
            return;
        }
        TextView textView7 = this.navArrow;
        if (textView7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("navArrow");
            textView7 = null;
        }
        textView7.setText("↑");
        TextView textView8 = this.navText;
        if (textView8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("navText");
            textView8 = null;
        }
        textView8.setText("Continue");
        TextView textView9 = this.navDist;
        if (textView9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("navDist");
            textView9 = null;
        }
        textView9.setText(fmtDistTo(distToDest));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void updateNav$lambda$54(MainActivity this$0) {
        if (this$0.navigating) {
            this$0.cancelNav();
        }
    }

    private final double minDistToRoute(double lat, double lon) {
        MainActivity mainActivity = this;
        if (mainActivity.navPoints.size() < 2) {
            return 0.0d;
        }
        double mLat = 111320.0d;
        double mLon = Math.cos(Math.toRadians(lat)) * 111320.0d;
        double px = lon * mLon;
        double py = lat * 111320.0d;
        double best = Double.MAX_VALUE;
        int i = 1;
        int size = mainActivity.navPoints.size();
        while (i < size) {
            double ax = mainActivity.navPoints.get(i - 1)[0] * mLon;
            double ay = mainActivity.navPoints.get(i - 1)[1] * mLat;
            double bx = mainActivity.navPoints.get(i)[0] * mLon;
            double by = mainActivity.navPoints.get(i)[1] * mLat;
            double dx = bx - ax;
            double dy = by - ay;
            double len2 = (dx * dx) + (dy * dy);
            double t = len2 == 0.0d ? 0.0d : RangesKt.coerceIn((((px - ax) * dx) + ((py - ay) * dy)) / len2, 0.0d, 1.0d);
            double ex = px - (ax + (t * dx));
            double ey = py - (ay + (t * dy));
            best = Math.min(best, Math.sqrt((ex * ex) + (ey * ey)));
            i++;
            mainActivity = this;
            mLat = mLat;
        }
        return best;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x006a, code lost:
    
        if (r0.equals("RNLB") == false) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0073, code lost:
    
        if (r0.equals("RNDB") == false) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:29:0x0086, code lost:
    
        if (r0.equals("TRU") == false) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x008f, code lost:
    
        if (r0.equals("TLU") == false) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x0098, code lost:
    
        if (r0.equals("TU") == false) goto L58;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:?, code lost:
    
        return kotlin.TuplesKt.to("↻", "Roundabout");
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:?, code lost:
    
        return kotlin.TuplesKt.to("↩", "U-turn");
     */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final Pair<String, String> maneuver(String cmd) {
        String upperCase = cmd.toUpperCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(upperCase, "toUpperCase(...)");
        switch (upperCase.hashCode()) {
            case 67:
                if (upperCase.equals("C")) {
                    return TuplesKt.to("↑", "Continue");
                }
                return TuplesKt.to("↑", "Continue");
            case 2401:
                if (upperCase.equals("KL")) {
                    return TuplesKt.to("↖", "Keep left");
                }
                return TuplesKt.to("↑", "Continue");
            case 2407:
                if (upperCase.equals("KR")) {
                    return TuplesKt.to("↗", "Keep right");
                }
                return TuplesKt.to("↑", "Continue");
            case 2680:
                if (upperCase.equals("TL")) {
                    return TuplesKt.to("↰", "Turn left");
                }
                return TuplesKt.to("↑", "Continue");
            case 2686:
                if (upperCase.equals("TR")) {
                    return TuplesKt.to("↱", "Turn right");
                }
                return TuplesKt.to("↑", "Continue");
            case 2689:
                break;
            case 83165:
                break;
            case 83351:
                break;
            case 2519994:
                break;
            case 2520242:
                break;
            case 2584515:
                if (upperCase.equals("TSHL")) {
                    return TuplesKt.to("↰", "Sharp left");
                }
                return TuplesKt.to("↑", "Continue");
            case 2584521:
                if (upperCase.equals("TSHR")) {
                    return TuplesKt.to("↱", "Sharp right");
                }
                return TuplesKt.to("↑", "Continue");
            case 2584639:
                if (upperCase.equals("TSLL")) {
                    return TuplesKt.to("↖", "Slight left");
                }
                return TuplesKt.to("↑", "Continue");
            case 2584645:
                if (upperCase.equals("TSLR")) {
                    return TuplesKt.to("↗", "Slight right");
                }
                return TuplesKt.to("↑", "Continue");
            default:
                return TuplesKt.to("↑", "Continue");
        }
        return TuplesKt.to("↑", "Continue");
    }

    private final String fmtDistTo(double meters) {
        double ft = 3.28084d * meters;
        if (ft < 1000.0d) {
            return (((int) (ft / ((double) 10))) * 10) + " ft";
        }
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String str = String.format(Locale.US, "%.1f mi", Arrays.copyOf(new Object[]{Double.valueOf(meters / 1609.344d)}, 1));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        return str;
    }

    private final double hav(double lat1, double lon1, double lat2, double lon2) {
        double p1 = Math.toRadians(lat1);
        double p2 = Math.toRadians(lat2);
        double dp = Math.toRadians(lat2 - lat1);
        double dl = Math.toRadians(lon2 - lon1);
        double d = 2;
        double it = Math.sin(dp / d);
        double dCos = Math.cos(p1) * Math.cos(p2);
        double it2 = Math.sin(dl / d);
        double a = (it * it) + (dCos * it2 * it2);
        return d * 6371000.0d * Math.asin(Math.sqrt(a));
    }

    private final void cancelNav() {
        this.navigating = false;
        ActionBus.INSTANCE.setNavigating(false);
        this.navSteps = CollectionsKt.emptyList();
        this.navPoints = CollectionsKt.emptyList();
        ViewPager2 viewPager2 = null;
        this.routeVias = null;
        View view = this.navBanner;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("navBanner");
            view = null;
        }
        view.setVisibility(8);
        ImageView imageView = this.recenterBtn;
        if (imageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recenterBtn");
            imageView = null;
        }
        ViewPager2 viewPager22 = this.pager;
        if (viewPager22 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
        } else {
            viewPager2 = viewPager22;
        }
        imageView.setVisibility((viewPager2.getCurrentItem() != this.mapPageIndex || this.cameraTracking) ? 8 : 0);
        GeoJsonSource geoJsonSource = this.routeSource;
        if (geoJsonSource != null) {
            geoJsonSource.setGeoJson("{\"type\":\"FeatureCollection\",\"features\":[]}");
        }
    }

    public final void startRec() {
        View view;
        startForegroundService(new Intent(this, (Class<?>) RideService.class).setAction(RideService.ACTION_START_REC));
        updateRecUi();
        TextView textView = this.recBtn;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recBtn");
            textView = null;
        }
        textView.setVisibility(8);
        this.controlsVisible = true;
        ImageView imageView = this.homeBtn;
        if (imageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("homeBtn");
            imageView = null;
        }
        ImageView imageView2 = imageView;
        ViewPager2 viewPager2 = this.pager;
        if (viewPager2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
            viewPager2 = null;
        }
        setFaded$default(this, imageView2, viewPager2.getCurrentItem() == this.mapPageIndex, true, 0.0f, 8, null);
        View view2 = this.recBar;
        if (view2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recBar");
            view = null;
        } else {
            view = view2;
        }
        setFaded$default(this, view, true, true, 0.0f, 8, null);
        this.ui.removeCallbacks(this.hideRunnable);
        this.ui.postDelayed(this.hideRunnable, 5000L);
    }

    public final void stopRec() {
        long startMs;
        RideSummary rideSummary;
        final long startMs2;
        RideService r = this.ride;
        if (r == null) {
            return;
        }
        RideRecorder rec = r.getRecorder();
        long startMs3 = rec.getStartMs();
        if (startMs3 > 0 && rec.getPoints() >= 2) {
            startMs = startMs3;
            rideSummary = new RideSummary(startMs3, this.currentRouteName, rec.getDistanceM(), rec.getElapsedMs(), rec.getAvgSpeedMps(), rec.getMaxSpeedMps(), rec.getHrAvg(), rec.getHrMax(), rec.getAscentM(), rec.getPowerAvg(), rec.getPowerMax(), null, false, null, 12288, null);
        } else {
            startMs = startMs3;
            rideSummary = null;
        }
        RideSummary summary = rideSummary;
        String path = r.stopRecording();
        this.currentRouteName = null;
        this.ui.removeCallbacks(this.hideRunnable);
        updateRecUi();
        syncChrome(false);
        if (path == null || !Prefs.INSTANCE.driveAutoUpload(this) || !Prefs.INSTANCE.driveConnected(this)) {
            startMs2 = startMs;
        } else {
            final File f = new File(path);
            startMs2 = startMs;
            new Thread(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.stopRec$lambda$62(MainActivity.this, f, startMs2);
                }
            }).start();
        }
        if (summary != null) {
            RideHistory.INSTANCE.add(summary.copy((12287 & 1) != 0 ? summary.startMs : 0L, (12287 & 2) != 0 ? summary.route : null, (12287 & 4) != 0 ? summary.distanceM : 0.0d, (12287 & 8) != 0 ? summary.movingMs : 0L, (12287 & 16) != 0 ? summary.avgMps : 0.0f, (12287 & 32) != 0 ? summary.maxMps : 0.0f, (12287 & 64) != 0 ? summary.hrAvg : 0, (12287 & 128) != 0 ? summary.hrMax : 0, (12287 & 256) != 0 ? summary.ascentM : 0.0d, (12287 & 512) != 0 ? summary.powerAvg : 0, (12287 & 1024) != 0 ? summary.powerMax : 0, (12287 & 2048) != 0 ? summary.gpx : path != null ? StringsKt.substringAfterLast(path, '/', path) : null, (12287 & 4096) != 0 ? summary.uploaded : false, (12287 & 8192) != 0 ? summary.name : null));
            AppState.INSTANCE.setOnMap(false);
            startActivity(new Intent(this, (Class<?>) RideSummaryActivity.class).putExtra("startMs", startMs2).putExtra("welcomeOnDone", true));
            return;
        }
        Toast.makeText(this, "Ride too short to save", 0).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void stopRec$lambda$62(final MainActivity this$0, File $f, final long $startMs) {
        final Object res;
        Object res$tmp;
        try {
            res$tmp = GoogleDriveClient.INSTANCE.uploadGpx(this$0, $f);
        } catch (Throwable th) {
            res$tmp = th;
        }
        res = res$tmp;
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.stopRec$lambda$62$lambda$61(res, $startMs, this$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void stopRec$lambda$62$lambda$61(Object $res, long $startMs, MainActivity this$0) {
        if (!(($res) instanceof Throwable)) {
            RideHistory.INSTANCE.markUploaded($startMs);
            Toast.makeText(this$0, (String) $res, 1).show();
        }
        Throwable it = ($res) instanceof Throwable ? (Throwable) $res : null;
        if (it != null) {
            Toast.makeText(this$0, "Drive upload failed: " + it.getMessage(), 1).show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void updateRecUi() {
        RideRecorder recorder;
        RideRecorder recorder2;
        RideService rideService = this.ride;
        boolean recording = (rideService == null || (recorder2 = rideService.getRecorder()) == null || !recorder2.isRecording()) ? false : true;
        RideService rideService2 = this.ride;
        boolean paused = (rideService2 == null || (recorder = rideService2.getRecorder()) == null || !recorder.getPaused()) ? false : true;
        TextView textView = this.pauseBtn;
        ViewPager2 viewPager2 = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pauseBtn");
            textView = null;
        }
        textView.setText(paused ? "▶  Resume" : "❚❚  Pause");
        TextView textView2 = this.pausedBadge;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pausedBadge");
            textView2 = null;
        }
        textView2.setVisibility((recording && paused) ? 0 : 8);
        TextView textView3 = this.recBtn;
        if (textView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recBtn");
            textView3 = null;
        }
        textView3.setVisibility(recording ? 8 : 0);
        LinearLayout linearLayout = this.dotsBar;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("dotsBar");
            linearLayout = null;
        }
        linearLayout.setVisibility(0);
        ImageView imageView = this.recenterBtn;
        if (imageView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recenterBtn");
            imageView = null;
        }
        ViewPager2 viewPager22 = this.pager;
        if (viewPager22 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
        } else {
            viewPager2 = viewPager22;
        }
        imageView.setVisibility((viewPager2.getCurrentItem() != this.mapPageIndex || this.cameraTracking) ? 8 : 0);
        updateScreenPolicy();
    }

    private final void showChrome() {
        this.controlsVisible = true;
        syncChrome(true);
        this.ui.removeCallbacks(this.hideRunnable);
        this.ui.postDelayed(this.hideRunnable, 5000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void hideChrome() {
        this.controlsVisible = false;
        syncChrome(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void syncChrome(boolean animate) {
        View view;
        RideRecorder recorder;
        ViewPager2 viewPager2 = this.pager;
        ImageView imageView = null;
        if (viewPager2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("pager");
            viewPager2 = null;
        }
        boolean z = true;
        boolean onMap = viewPager2.getCurrentItem() == this.mapPageIndex;
        RideService rideService = this.ride;
        boolean recording = (rideService == null || (recorder = rideService.getRecorder()) == null || !recorder.isRecording()) ? false : true;
        ImageView imageView2 = this.homeBtn;
        if (imageView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("homeBtn");
            imageView2 = null;
        }
        setFaded$default(this, imageView2, this.controlsVisible && onMap, animate, 0.0f, 8, null);
        View view2 = this.recBar;
        if (view2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("recBar");
            view = null;
        } else {
            view = view2;
        }
        setFaded$default(this, view, this.controlsVisible && recording, animate, 0.0f, 8, null);
        ImageView imageView3 = this.bellBtn;
        if (imageView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("bellBtn");
        } else {
            imageView = imageView3;
        }
        ImageView imageView4 = imageView;
        if (!this.controlsVisible || (!onMap && !recording)) {
            z = false;
        }
        setFaded(imageView4, z, animate, 0.8f);
    }

    static /* synthetic */ void setFaded$default(MainActivity mainActivity, View view, boolean z, boolean z2, float f, int i, Object obj) {
        if ((i & 8) != 0) {
            f = 1.0f;
        }
        mainActivity.setFaded(view, z, z2, f);
    }

    private final void setFaded(final View v, boolean show, boolean animate, float shownAlpha) {
        v.animate().cancel();
        if (show) {
            if (v.getVisibility() != 0) {
                v.setAlpha(0.0f);
            }
            v.setVisibility(0);
            if (animate) {
                v.animate().alpha(shownAlpha).setDuration(200L).start();
                return;
            } else {
                v.setAlpha(shownAlpha);
                return;
            }
        }
        if (animate && v.getVisibility() == 0 && v.getAlpha() > 0.0f) {
            v.animate().alpha(0.0f).setDuration(900L).withEndAction(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    MainActivity.setFaded$lambda$63(v);
                }
            }).start();
        } else {
            v.setAlpha(0.0f);
            v.setVisibility(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void setFaded$lambda$63(View $v) {
        $v.setAlpha(0.0f);
        $v.setVisibility(4);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        RideRecorder recorder;
        Intrinsics.checkNotNullParameter(ev, "ev");
        if (ev.getActionMasked() == 0) {
            ViewPager2 viewPager2 = this.pager;
            if (viewPager2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("pager");
                viewPager2 = null;
            }
            boolean onMap = viewPager2.getCurrentItem() == this.mapPageIndex;
            RideService rideService = this.ride;
            boolean recording = (rideService == null || (recorder = rideService.getRecorder()) == null || !recorder.isRecording()) ? false : true;
            if (onMap || recording) {
                showChrome();
            }
        }
        boolean onMap2 = super.dispatchTouchEvent(ev);
        return onMap2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void stopCompleteRunnable$lambda$64(MainActivity this$0) {
        if (this$0.stopHolding) {
            this$0.stopHolding = false;
            this$0.resetStopButton();
            this$0.stopRec();
        }
    }

    private final void beginStopHold() {
        this.stopHolding = true;
        TextView textView = this.stopLabel;
        View view = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stopLabel");
            textView = null;
        }
        textView.setText("HOLD…");
        View view2 = this.stopFill;
        if (view2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stopFill");
            view2 = null;
        }
        view2.setPivotX(0.0f);
        View view3 = this.stopFill;
        if (view3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stopFill");
            view3 = null;
        }
        view3.setScaleX(0.0f);
        View view4 = this.stopFill;
        if (view4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stopFill");
        } else {
            view = view4;
        }
        view.animate().scaleX(1.0f).setDuration(1500L).setInterpolator(new LinearInterpolator()).start();
        this.ui.removeCallbacks(this.hideRunnable);
        this.ui.postDelayed(this.stopCompleteRunnable, 1500L);
    }

    private final void endStopHold() {
        if (this.stopHolding) {
            this.stopHolding = false;
            this.ui.removeCallbacks(this.stopCompleteRunnable);
            resetStopButton();
            this.ui.postDelayed(this.hideRunnable, 5000L);
        }
    }

    private final void resetStopButton() {
        View view = this.stopFill;
        TextView textView = null;
        if (view == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stopFill");
            view = null;
        }
        view.animate().cancel();
        View view2 = this.stopFill;
        if (view2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stopFill");
            view2 = null;
        }
        view2.animate().scaleX(0.0f).setDuration(150L).start();
        TextView textView2 = this.stopLabel;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("stopLabel");
        } else {
            textView = textView2;
        }
        textView.setText("■  Hold to stop");
    }

    private final int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    private final void showMetricPicker(final DashboardView view, final DashTile tile) {
        final Metric[] metrics = Metric.values();
        Collection destination$iv$iv = new ArrayList(metrics.length);
        for (Metric metric : metrics) {
            destination$iv$iv.add(metric.getLabel() + (metric.getUnit().length() > 0 ? "  (" + metric.getUnit() + ")" : ""));
        }
        Collection thisCollection$iv = (List) destination$iv$iv;
        String[] labels = (String[]) thisCollection$iv.toArray(new String[0]);
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert).setTitle("Metric").setItems(labels, new DialogInterface.OnClickListener() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                MainActivity.showMetricPicker$lambda$66(view, tile, metrics, dialogInterface, i);
            }
        }).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void showMetricPicker$lambda$66(DashboardView $view, DashTile $tile, Metric[] $metrics, DialogInterface dialogInterface, int i) {
        $view.applyMetric($tile, $metrics[i]);
    }

    private final int liveZone(int hr, int maxHr) {
        double f = ((double) hr) / ((double) maxHr);
        if (f >= 0.9d) {
            return 4;
        }
        if (f >= 0.8d) {
            return 3;
        }
        if (f >= 0.7d) {
            return 2;
        }
        return f >= 0.6d ? 1 : 0;
    }

    private final String fmtZoneTime(long ms) {
        long s = ms / ((long) 1000);
        if (s >= 3600) {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            long j = 3600;
            long j2 = 60;
            String str = String.format(Locale.US, "%d:%02d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(s / j), Long.valueOf((s % j) / j2), Long.valueOf(s % j2)}, 3));
            Intrinsics.checkNotNullExpressionValue(str, "format(...)");
            return str;
        }
        StringCompanionObject stringCompanionObject2 = StringCompanionObject.INSTANCE;
        long j3 = 60;
        String str2 = String.format(Locale.US, "%d:%02d", Arrays.copyOf(new Object[]{Long.valueOf(s / j3), Long.valueOf(s % j3)}, 2));
        Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
        return str2;
    }

    private final void buildHrPage() {
        this.hrPageMaxHr = Prefs.INSTANCE.maxHr(this);
        LinearLayout linearLayout = this.hrLegend;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("hrLegend");
            linearLayout = null;
        }
        linearLayout.removeAllViews();
        this.hrTimeLbls.clear();
        this.hrPctLbls.clear();
        Typeface medium = Typeface.create("sans-serif-medium", 0);
        HrZone[] hrZoneArrValues = HrZone.values();
        int index$iv = 0;
        int length = hrZoneArrValues.length;
        int i = 0;
        while (i < length) {
            int index$iv2 = index$iv + 1;
            int i2 = index$iv;
            int color = (int) hrZoneArrValues[i].getColor();
            LinearLayout $this$buildHrPage_u24lambda_u2473_u24lambda_u2468 = new LinearLayout(this);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.setOrientation(1);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.setGravity(1);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.setBackgroundResource(R.drawable.card_solid);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.setPadding(dp(2), dp(7), dp(2), dp(7));
            LinearLayout.LayoutParams $this$buildHrPage_u24lambda_u2473_u24lambda_u2468_u24lambda_u2467 = new LinearLayout.LayoutParams(0, -2, 1.0f);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468_u24lambda_u2467.setMargins(dp(2), 0, dp(2), 0);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.setLayoutParams($this$buildHrPage_u24lambda_u2473_u24lambda_u2468_u24lambda_u2467);
            View $this$buildHrPage_u24lambda_u2473_u24lambda_u2471 = new View(this);
            GradientDrawable $this$buildHrPage_u24lambda_u2473_u24lambda_u2471_u24lambda_u2469 = new GradientDrawable();
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2471_u24lambda_u2469.setColor(color);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2471_u24lambda_u2469.setCornerRadius(dp(2));
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2471.setBackground($this$buildHrPage_u24lambda_u2473_u24lambda_u2471_u24lambda_u2469);
            LinearLayout.LayoutParams $this$buildHrPage_u24lambda_u2473_u24lambda_u2471_u24lambda_u2470 = new LinearLayout.LayoutParams(dp(22), dp(4));
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2471_u24lambda_u2470.gravity = 1;
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2471.setLayoutParams($this$buildHrPage_u24lambda_u2473_u24lambda_u2471_u24lambda_u2470);
            Typeface typeface = medium;
            TextView zlbl = buildHrPage$lambda$73$cell(this, typeface, "Z" + (i2 + 1), 11.0f, Color.parseColor("#FF9E9E9E"), false, 3);
            TextView pct = buildHrPage$lambda$73$cell(this, typeface, "0%", 15.0f, -1, true, 1);
            Typeface medium2 = medium;
            TextView time = buildHrPage$lambda$73$cell(this, typeface, "0:00", 11.0f, Color.parseColor("#FF8E8E93"), false, 0);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.addView($this$buildHrPage_u24lambda_u2473_u24lambda_u2471);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.addView(zlbl);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.addView(pct);
            $this$buildHrPage_u24lambda_u2473_u24lambda_u2468.addView(time);
            LinearLayout linearLayout2 = this.hrLegend;
            if (linearLayout2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hrLegend");
                linearLayout2 = null;
            }
            linearLayout2.addView($this$buildHrPage_u24lambda_u2473_u24lambda_u2468);
            this.hrPctLbls.add(pct);
            this.hrTimeLbls.add(time);
            i++;
            index$iv = index$iv2;
            medium = medium2;
        }
    }

    private static final TextView buildHrPage$lambda$73$cell(MainActivity this$0, Typeface medium, String t, float size, int colr, boolean med, int topPad) {
        TextView $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472 = new TextView(this$0);
        $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472.setText(t);
        $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472.setTextColor(colr);
        $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472.setTextSize(size);
        if (med) {
            $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472.setTypeface(medium);
        }
        $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472.setIncludeFontPadding(false);
        $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472.setGravity(17);
        $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472.setPadding(0, this$0.dp(topPad), 0, 0);
        $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        return $this$buildHrPage_u24lambda_u2473_u24cell_u24lambda_u2472;
    }

    private final void updateHrPage() {
        long[] hrHistMs;
        RideRecorder recorder;
        RideRecorder recorder2;
        if (this.hrPctLbls.isEmpty()) {
            return;
        }
        RideService rideService = this.ride;
        int hr = rideService != null ? rideService.getLastHr() : 0;
        TextView textView = this.hrBig;
        long[] zoneMs = null;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("hrBig");
            textView = null;
        }
        textView.setText(hr > 0 ? String.valueOf(hr) : "--");
        int maxHr = RangesKt.coerceAtLeast(this.hrPageMaxHr, 1);
        if (hr > 0) {
            int zi = liveZone(hr, maxHr);
            HrZone z = HrZone.values()[zi];
            TextView textView2 = this.hrZoneLbl;
            if (textView2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hrZoneLbl");
                textView2 = null;
            }
            textView2.setText("Z" + (zi + 1) + " · " + z.getLabel());
            TextView textView3 = this.hrZoneLbl;
            if (textView3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hrZoneLbl");
                textView3 = null;
            }
            textView3.setTextColor((int) z.getColor());
        } else {
            TextView textView4 = this.hrZoneLbl;
            if (textView4 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hrZoneLbl");
                textView4 = null;
            }
            textView4.setText("—");
            TextView textView5 = this.hrZoneLbl;
            if (textView5 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("hrZoneLbl");
                textView5 = null;
            }
            textView5.setTextColor(Color.parseColor("#FF8E8E93"));
        }
        HrGraphView hrGraphView = this.hrGraph;
        if (hrGraphView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("hrGraph");
            hrGraphView = null;
        }
        RideService rideService2 = this.ride;
        if (rideService2 == null || (recorder2 = rideService2.getRecorder()) == null || (hrHistMs = recorder2.getHrHistMs()) == null) {
            hrHistMs = new long[0];
        }
        hrGraphView.setData(hrHistMs, maxHr, hr);
        RideService rideService3 = this.ride;
        if (rideService3 != null && (recorder = rideService3.getRecorder()) != null) {
            zoneMs = recorder.getZoneMs();
        }
        long[] zoneMs2 = zoneMs;
        long total = zoneMs2 != null ? ArraysKt.sum(zoneMs2) : 0L;
        for (int i = 0; i < 5; i++) {
            long ms = zoneMs2 != null ? zoneMs2[i] : 0L;
            this.hrTimeLbls.get(i).setText(fmtZoneTime(ms));
            float frac = total > 0 ? ms / total : 0.0f;
            this.hrPctLbls.get(i).setText(Math.round(100 * frac) + "%");
        }
    }

    private final String metricValue(Metric m) {
        RideService rideService = this.ride;
        RideRecorder r = rideService != null ? rideService.getRecorder() : null;
        boolean rec = r != null && r.isRecording();
        switch (WhenMappings.$EnumSwitchMapping$0[m.ordinal()]) {
            case 1:
                Units units = Units.INSTANCE;
                RideService rideService2 = this.ride;
                return units.fmtSpeed(rideService2 != null ? rideService2.getCurSpeedMps() : 0.0f);
            case 2:
                if (!rec) {
                    return "--";
                }
                Units units2 = Units.INSTANCE;
                Intrinsics.checkNotNull(r);
                return units2.fmtSpeed(r.getAvgSpeedMps());
            case 3:
                if ((r != null ? r.getMaxSpeedMps() : 0.0f) <= 0.0f) {
                    return "--";
                }
                Units units3 = Units.INSTANCE;
                Intrinsics.checkNotNull(r);
                return units3.fmtSpeed(r.getMaxSpeedMps());
            case 4:
                RideService rideService3 = this.ride;
                int it = rideService3 != null ? rideService3.getLastHr() : 0;
                return it > 0 ? String.valueOf(it) : "--";
            case 5:
                if ((r != null ? r.getHrAvg() : 0) <= 0) {
                    return "--";
                }
                Intrinsics.checkNotNull(r);
                return String.valueOf(r.getHrAvg());
            case 6:
                if ((r != null ? r.getHrMax() : 0) <= 0) {
                    return "--";
                }
                Intrinsics.checkNotNull(r);
                return String.valueOf(r.getHrMax());
            case 7:
                return Units.INSTANCE.fmtDist(r != null ? r.getDistanceM() : 0.0d);
            case 8:
                return Units.INSTANCE.fmtHms(r != null ? r.getElapsedMs() : 0L);
            case 9:
                Units units4 = Units.INSTANCE;
                RideService rideService4 = this.ride;
                return units4.fmtFeet(rideService4 != null ? rideService4.getCurEleM() : 0.0d);
            case 10:
                if ((r != null ? r.getAscentM() : 0.0d) <= 0.0d) {
                    return "--";
                }
                Units units5 = Units.INSTANCE;
                Intrinsics.checkNotNull(r);
                return units5.fmtFeet(r.getAscentM());
            case 11:
                RideService it2 = this.ride;
                if (it2 == null) {
                    return "--";
                }
                StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
                String str = String.format(Locale.US, "%.1f", Arrays.copyOf(new Object[]{Float.valueOf(it2.getCurGrade())}, 1));
                Intrinsics.checkNotNullExpressionValue(str, "format(...)");
                return str == null ? "--" : str;
            case 12:
                RideService rideService5 = this.ride;
                int it3 = rideService5 != null ? rideService5.getCurCadence() : 0;
                return it3 > 0 ? String.valueOf(it3) : "--";
            case 13:
                RideService rideService6 = this.ride;
                int it4 = rideService6 != null ? rideService6.getCurPower() : 0;
                return it4 > 0 ? String.valueOf(it4) : "--";
            case 14:
                if ((r != null ? r.getPowerAvg() : 0) <= 0) {
                    return "--";
                }
                Intrinsics.checkNotNull(r);
                return String.valueOf(r.getPowerAvg());
            case 15:
                if ((r != null ? r.getPowerMax() : 0) <= 0) {
                    return "--";
                }
                Intrinsics.checkNotNull(r);
                return String.valueOf(r.getPowerMax());
            case 16:
                String str2 = this.clockFmt.format(new Date());
                Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
                return str2;
            case 17:
                int it5 = batteryInfo().getFirst().intValue();
                return it5 >= 0 && it5 < 101 ? String.valueOf(it5) : "--";
            default:
                throw new NoWhenBranchMatchedException();
        }
    }

    private final Pair<Integer, Boolean> batteryInfo() {
        Intent i = registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int i2 = -1;
        if (i == null) {
            return TuplesKt.to(-1, false);
        }
        int level = i.getIntExtra("level", -1);
        int scale = i.getIntExtra("scale", -1);
        int status = i.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
        boolean charging = status == 2 || status == 5;
        if (level >= 0 && scale > 0) {
            i2 = (level * 100) / scale;
        }
        return TuplesKt.to(Integer.valueOf(i2), Boolean.valueOf(charging));
    }

    private final void checkLowBattery() {
        Pair<Integer, Boolean> pairBatteryInfo = batteryInfo();
        int pct = pairBatteryInfo.component1().intValue();
        boolean charging = pairBatteryInfo.component2().booleanValue();
        boolean z = false;
        if (pct >= 0 && pct < 101) {
            if (pct > 15) {
                this.lowBattWarned = false;
            }
            if (1 <= pct && pct < 11) {
                z = true;
            }
            if (z && !this.lowBattWarned && !charging) {
                this.lowBattWarned = true;
                try {
                    MainActivity $this$checkLowBattery_u24lambda_u2479 = this;
                    new AlertDialog.Builder($this$checkLowBattery_u24lambda_u2479, android.R.style.Theme_Material_Dialog_Alert).setTitle("Battery low — " + pct + "%").setMessage("Consider wrapping up. Your ride will auto-save and the phone will power off at 2% to protect it.").setPositiveButton("OK", (DialogInterface.OnClickListener) null).show();
                } catch (Throwable th) {
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:65:0x0125  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void uiTick() {
        String strFmtFeet = "--";
        String str = "--";
        String strFmtSpeed = "--";
        String strFmtSpeed2 = "--";
        String strValueOf = "--";
        String strValueOf2 = "--";
        String strFmtFeet2 = "--";
        String str2;
        ArrayList<float[]> eleSamples;
        updateRecUi();
        checkLowBattery();
        RideService rideService = this.ride;
        RideRecorder r = rideService != null ? rideService.getRecorder() : null;
        boolean rec = r != null && r.isRecording();
        double dist = r != null ? r.getDistanceM() : 0.0d;
        long elapsed = r != null ? r.getElapsedMs() : 0L;
        TextView textView = this.mDist;
        if (textView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mDist");
            textView = null;
        }
        textView.setText(Units.INSTANCE.fmtDist(dist));
        TextView textView2 = this.mTime;
        if (textView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mTime");
            textView2 = null;
        }
        textView2.setText(Units.INSTANCE.fmtHms(elapsed));
        RideService rideService2 = this.ride;
        double eleCur = rideService2 != null ? rideService2.getCurEleM() : 0.0d;
        Iterable $this$forEach$iv = this.dashViews;
        for (Object element$iv : $this$forEach$iv) {
            DashboardView it = (DashboardView) element$iv;
            if (!it.getEditing()) {
                it.refresh();
            }
        }
        updateHrPage();
        ElevationView elevationView = this.elevView;
        if (elevationView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("elevView");
            elevationView = null;
        }
        elevationView.setData((r == null || (eleSamples = r.getEleSamples()) == null) ? CollectionsKt.emptyList() : eleSamples);
        TextView textView3 = this.eEle;
        if (textView3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("eEle");
            textView3 = null;
        }
        textView3.setText(Units.INSTANCE.fmtFeet(eleCur));
        TextView textView4 = this.eAscent;
        if (textView4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("eAscent");
            textView4 = null;
        }
        if ((r != null ? r.getAscentM() : 0.0d) > 0.0d) {
            Units units = Units.INSTANCE;
            Intrinsics.checkNotNull(r);
            strFmtFeet = units.fmtFeet(r.getAscentM());
        }
        textView4.setText(strFmtFeet);
        TextView textView5 = this.eGrade;
        if (textView5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("eGrade");
            textView5 = null;
        }
        RideService it2 = this.ride;
        if (it2 != null) {
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            String str3 = String.format(Locale.US, "%.1f", Arrays.copyOf(new Object[]{Float.valueOf(it2.getCurGrade())}, 1));
            Intrinsics.checkNotNullExpressionValue(str3, "format(...)");
            str = str3 != null ? str3 : "--";
        }
        textView5.setText(str);
        TextView textView6 = this.sDist;
        if (textView6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sDist");
            textView6 = null;
        }
        textView6.setText(Units.INSTANCE.fmtDist(dist));
        TextView textView7 = this.sTime;
        if (textView7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sTime");
            textView7 = null;
        }
        textView7.setText(Units.INSTANCE.fmtHms(elapsed));
        TextView textView8 = this.sAvgSpd;
        if (textView8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sAvgSpd");
            textView8 = null;
        }
        if (rec) {
            Units units2 = Units.INSTANCE;
            Intrinsics.checkNotNull(r);
            strFmtSpeed = units2.fmtSpeed(r.getAvgSpeedMps());
        }
        textView8.setText(strFmtSpeed);
        TextView textView9 = this.sMaxSpd;
        if (textView9 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sMaxSpd");
            textView9 = null;
        }
        if ((r != null ? r.getMaxSpeedMps() : 0.0f) > 0.0f) {
            Units units3 = Units.INSTANCE;
            Intrinsics.checkNotNull(r);
            strFmtSpeed2 = units3.fmtSpeed(r.getMaxSpeedMps());
        }
        textView9.setText(strFmtSpeed2);
        TextView textView10 = this.sAvgHr;
        if (textView10 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sAvgHr");
            textView10 = null;
        }
        if ((r != null ? r.getHrAvg() : 0) > 0) {
            Intrinsics.checkNotNull(r);
            strValueOf = String.valueOf(r.getHrAvg());
        }
        textView10.setText(strValueOf);
        TextView textView11 = this.sMaxHr;
        if (textView11 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sMaxHr");
            textView11 = null;
        }
        if ((r != null ? r.getHrMax() : 0) > 0) {
            Intrinsics.checkNotNull(r);
            strValueOf2 = String.valueOf(r.getHrMax());
        }
        textView11.setText(strValueOf2);
        TextView textView12 = this.sAscent;
        if (textView12 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sAscent");
            textView12 = null;
        }
        if ((r != null ? r.getAscentM() : 0.0d) > 0.0d) {
            Units units4 = Units.INSTANCE;
            Intrinsics.checkNotNull(r);
            strFmtFeet2 = units4.fmtFeet(r.getAscentM());
        }
        textView12.setText(strFmtFeet2);
        TextView textView13 = this.sStarted;
        if (textView13 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("sStarted");
            textView13 = null;
        }
        if ((r != null ? r.getStartMs() : 0L) > 0) {
            SimpleDateFormat simpleDateFormat = this.clockFmt;
            Intrinsics.checkNotNull(r);
            str2 = simpleDateFormat.format(new Date(r.getStartMs()));
        } else {
            str2 = "--:--";
        }
        textView13.setText(str2);
        this.ui.postDelayed(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.this.uiTick();
            }
        }, 1000L);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private final void updateScreenPolicy() {
        boolean autoOff = false;
        RideRecorder recorder;
        if (Prefs.INSTANCE.endurance(this)) {
            RideService rideService = this.ride;
            if ((rideService == null || (recorder = rideService.getRecorder()) == null || !recorder.isRecording()) ? false : true) {
                autoOff = true;
            }
        } else {
            autoOff = false;
        }
        if (autoOff) {
            getWindow().clearFlags(128);
            if (!this.screenOffArmed) {
                this.screenOffArmed = true;
                this.ui.postDelayed(this.screenOffRunnable, this.screenOffMs);
                return;
            }
            return;
        }
        getWindow().addFlags(128);
        this.screenOffArmed = false;
        this.ui.removeCallbacks(this.screenOffRunnable);
    }

    private final void sleepScreen() {
        new Thread(new Runnable() { // from class: com.bike.computer.MainActivity$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                MainActivity.sleepScreen$lambda$83();
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void sleepScreen$lambda$83() {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "input keyevent 223"}).waitFor();
        } catch (Exception e) {
        }
    }

    @Override // android.app.Activity
    public void onUserInteraction() {
        RideRecorder recorder;
        super.onUserInteraction();
        if (Prefs.INSTANCE.endurance(this)) {
            RideService rideService = this.ride;
            boolean z = false;
            if (rideService != null && (recorder = rideService.getRecorder()) != null && recorder.isRecording()) {
                z = true;
            }
            if (z) {
                this.screenOffArmed = true;
                this.ui.removeCallbacks(this.screenOffRunnable);
                this.ui.postDelayed(this.screenOffRunnable, this.screenOffMs);
            }
        }
    }

    private final void enterImmersive() {
        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController $this$enterImmersive_u24lambda_u2484 = getWindow().getInsetsController();
            if ($this$enterImmersive_u24lambda_u2484 != null) {
                $this$enterImmersive_u24lambda_u2484.hide(WindowInsets.Type.systemBars());
                $this$enterImmersive_u24lambda_u2484.setSystemBarsBehavior(2);
            }
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enterImmersive();
        }
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        MapView mapView = this.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.onStart();
        bindService(new Intent(this, (Class<?>) RideService.class), this.conn, 1);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        MapView mapView = this.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.onResume();
        AppState.INSTANCE.setOnMap(true);
        Voice.INSTANCE.setEnabled(Prefs.INSTANCE.voice(this));
        RideService rideService = this.ride;
        if (rideService != null) {
            rideService.setAutoPauseEnabled(Prefs.INSTANCE.autoPause(this));
        }
        if (ActionBus.INSTANCE.getStopNav()) {
            ActionBus.INSTANCE.setStopNav(false);
            if (this.navigating) {
                cancelNav();
            }
        }
        if (Prefs.INSTANCE.maxHr(this) != this.hrPageMaxHr) {
            buildHrPage();
        }
        if (!Intrinsics.areEqual(Prefs.INSTANCE.pageSignature(this), this.pageSig)) {
            buildPager(false);
        }
        applyMapPower();
        updateScreenPolicy();
        tryConsumePendingRoute();
        showChrome();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        AppState.INSTANCE.setOnMap(false);
        super.onBackPressed();
    }

    @Override // android.app.Activity
    protected void onPause() {
        MapView mapView = this.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.onPause();
        super.onPause();
    }

    @Override // android.app.Activity
    protected void onStop() {
        this.ui.removeCallbacks(this.screenOffRunnable);
        MapView mapView = null;
        try {
            RideService rideService = this.ride;
            if (rideService != null) {
                rideService.setOnUpdate(null);
            }
            unbindService(this.conn);
        } catch (Exception e) {
        }
        MapView mapView2 = this.mapView;
        if (mapView2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
        } else {
            mapView = mapView2;
        }
        mapView.onStop();
        super.onStop();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        MapView mapView = this.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.onLowMemory();
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        Intrinsics.checkNotNullParameter(outState, "outState");
        super.onSaveInstanceState(outState);
        MapView mapView = this.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.onSaveInstanceState(outState);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        Voice.INSTANCE.shutdown();
        MapView mapView = this.mapView;
        if (mapView == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mapView");
            mapView = null;
        }
        mapView.onDestroy();
    }
}

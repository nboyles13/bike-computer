package com.bike.computer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import kotlin.Deprecated;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.sequences.SequencesKt;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;
import kotlinx.coroutines.DebugKt;

public final class RideService extends Service implements LocationListener {
    public static final String ACTION_CANCEL_SHUTDOWN = "com.bike.computer.CANCEL_SHUTDOWN";
    public static final String ACTION_START_REC = "com.bike.computer.START_REC";
    private static final String CHAN = "ride";
    private static final String CRIT_CHAN = "critbatt";
    private static final int NOTIF_ID = 1;
    private boolean criticalDone;
    private double cumDistM;
    private int curCadence;
    private double curEleM;
    private float curGrade;
    private int curPower;
    private float curSpeedMps;
    private CyclingSensor cyclingSensor;
    private HrSensor hrSensor;
    private int lastHr;
    private Location lastLocation;
    private long lastNotifMs;
    private Location lastTrailLoc;
    private LocationManager lm;
    private long lowSpeedSince;
    private boolean manualPause;
    private Function0<Unit> onUpdate;
    private Sensor pressureSensor;
    private Handler shutdownHandler;
    private int shutdownSecs;
    private PowerManager.WakeLock wakeLock;
    private boolean wifiToggledOff;
    private final LocalBinder binder = new LocalBinder();

    /* JADX INFO: renamed from: recorder$delegate, reason: from kotlin metadata */
    private final Lazy recorder = LazyKt.lazy(new Function0() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda0
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return RideService.recorder_delegate$lambda$0();
        }
    });
    private String hrStatus = "scanning…";
    private String cyclingStatus = "scanning…";
    private final ArrayList<double[]> trail = new ArrayList<>();

    /* JADX INFO: renamed from: sensorMgr$delegate, reason: from kotlin metadata */
    private final Lazy sensorMgr = LazyKt.lazy(new Function0() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda6
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return RideService.sensorMgr_delegate$lambda$1(RideService.this);
        }
    });
    private double baroAltM = Double.NaN;
    private final ArrayDeque<double[]> gradeWin = new ArrayDeque<>();
    private final SensorEventListener baroListener = new SensorEventListener() { // from class: com.bike.computer.RideService$baroListener$1
        @Override // android.hardware.SensorEventListener
        public void onSensorChanged(SensorEvent e) {
            Intrinsics.checkNotNullParameter(e, "e");
            double alt = (((double) 1) - Math.pow(((double) e.values[0]) / 1013.25d, 0.19029495718363465d)) * 44330.0d;
            RideService.this.baroAltM = Double.isNaN(RideService.this.baroAltM) ? alt : (RideService.this.baroAltM * 0.85d) + (0.15d * alt);
        }

        @Override // android.hardware.SensorEventListener
        public void onAccuracyChanged(Sensor s, int a) {
        }
    };
    private boolean autoPauseEnabled = true;
    private final LedController led = new LedController();
    private boolean ledEnabled = true;
    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() { // from class: com.bike.computer.RideService$batteryReceiver$1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context c, Intent i) {
            Intrinsics.checkNotNullParameter(c, "c");
            Intrinsics.checkNotNullParameter(i, "i");
            int level = i.getIntExtra("level", -1);
            int scale = i.getIntExtra("scale", -1);
            int plugged = i.getIntExtra("plugged", 0);
            if (level < 0 || scale <= 0) {
                return;
            }
            int pct = (level * 100) / scale;
            if (pct <= 2 && plugged == 0 && RideService.this.getRecorder().isRecording() && !RideService.this.criticalDone) {
                RideService.this.criticalDone = true;
                RideService.this.criticalShutdown();
            }
        }
    };

    /* JADX INFO: compiled from: RideService.kt */
    public final class LocalBinder extends Binder {
        public LocalBinder() {
        }

        /* JADX INFO: renamed from: getService, reason: from getter */
        public final RideService getThis$0() {
            return RideService.this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final RideRecorder recorder_delegate$lambda$0() {
        return new RideRecorder("/sdcard/BikeComputer/rides");
    }

    public final RideRecorder getRecorder() {
        return (RideRecorder) this.recorder.getValue();
    }

    public final Location getLastLocation() {
        return this.lastLocation;
    }

    public final int getLastHr() {
        return this.lastHr;
    }

    public final String getHrStatus() {
        return this.hrStatus;
    }

    public final String getCyclingStatus() {
        return this.cyclingStatus;
    }

    public final int getCurCadence() {
        return this.curCadence;
    }

    public final int getCurPower() {
        return this.curPower;
    }

    public final float getCurSpeedMps() {
        return this.curSpeedMps;
    }

    public final double getCurEleM() {
        return this.curEleM;
    }

    public final float getCurGrade() {
        return this.curGrade;
    }

    public final ArrayList<double[]> getTrail() {
        return this.trail;
    }

    private final SensorManager getSensorMgr() {
        return (SensorManager) this.sensorMgr.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final SensorManager sensorMgr_delegate$lambda$1(RideService this$0) {
        Object systemService = this$0.getSystemService("sensor");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.hardware.SensorManager");
        return (SensorManager) systemService;
    }

    private final boolean getHasBaro() {
        return this.pressureSensor != null;
    }

    public final boolean getAutoPauseEnabled() {
        return this.autoPauseEnabled;
    }

    public final void setAutoPauseEnabled(boolean z) {
        this.autoPauseEnabled = z;
    }

    public final boolean getManualPause() {
        return this.manualPause;
    }

    public final boolean getLedEnabled() {
        return this.ledEnabled;
    }

    public final void setLedEnabled(boolean v) {
        this.ledEnabled = v;
        if (v) {
            updateLed();
        } else {
            this.led.off();
        }
    }

    public final Function0<Unit> getOnUpdate() {
        return this.onUpdate;
    }

    public final void setOnUpdate(Function0<Unit> function0) {
        this.onUpdate = function0;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        createChannel();
        setLedEnabled(Prefs.INSTANCE.led(this));
        Object systemService = getSystemService("location");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.location.LocationManager");
        this.lm = (LocationManager) systemService;
        this.pressureSensor = getSensorMgr().getDefaultSensor(6);
        Sensor it = this.pressureSensor;
        if (it != null) {
            getSensorMgr().registerListener(this.baroListener, it, 3);
        }
        startGps();
        HrSensor it2 = newHrSensor();
        it2.start();
        this.hrSensor = it2;
        CyclingSensor it3 = new CyclingSensor(this, new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda7
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return RideService.onCreate$lambda$4(RideService.this, ((Integer) obj).intValue());
            }
        }, new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda8
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return RideService.onCreate$lambda$5(RideService.this, ((Integer) obj).intValue());
            }
        }, new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda9
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return RideService.onCreate$lambda$6(RideService.this, (String) obj);
            }
        });
        it3.start();
        this.cyclingSensor = it3;
        registerReceiver(this.batteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit onCreate$lambda$4(RideService this$0, int w) {
        this$0.curPower = w;
        Function0<Unit> function0 = this$0.onUpdate;
        if (function0 != null) {
            function0.invoke();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit onCreate$lambda$5(RideService this$0, int c) {
        this$0.curCadence = c;
        Function0<Unit> function0 = this$0.onUpdate;
        if (function0 != null) {
            function0.invoke();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit onCreate$lambda$6(RideService this$0, String s) {
        Intrinsics.checkNotNullParameter(s, "s");
        this$0.cyclingStatus = s;
        Function0<Unit> function0 = this$0.onUpdate;
        if (function0 != null) {
            function0.invoke();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void criticalShutdown() {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "input keyevent 224"});
        } catch (Throwable th) {
        }
        this.shutdownSecs = 10;
        this.shutdownHandler = new Handler(getMainLooper());
        tickShutdown();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void tickShutdown() {
        if (this.shutdownSecs <= 0) {
            doShutdown();
            return;
        }
        try {
            Object systemService = getSystemService("notification");
            Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.NotificationManager");
            ((NotificationManager) systemService).notify(2, buildShutdownNotification(this.shutdownSecs));
        } catch (Throwable th) {
        }
        this.shutdownSecs--;
        Handler handler = this.shutdownHandler;
        if (handler != null) {
            handler.postDelayed(new Runnable() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    RideService.this.tickShutdown();
                }
            }, 1000L);
        }
    }

    public final void abortShutdown() {
        Handler handler = this.shutdownHandler;
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        this.shutdownHandler = null;
        try {
            Object systemService = getSystemService("notification");
            Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.NotificationManager");
            ((NotificationManager) systemService).cancel(2);
        } catch (Throwable th) {
        }
    }

    private final void doShutdown() {
        RideSummary rideSummary;
        String strSubstringAfterLast$default = null;
        this.shutdownHandler = null;
        try {
            Object systemService = getSystemService("notification");
            Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.NotificationManager");
            ((NotificationManager) systemService).cancel(2);
        } catch (Throwable th) {
        }
        RideRecorder rec = getRecorder();
        if (rec.getStartMs() > 0 && rec.getPoints() >= 2) {
            rideSummary = new RideSummary(rec.getStartMs(), null, rec.getDistanceM(), rec.getElapsedMs(), rec.getAvgSpeedMps(), rec.getMaxSpeedMps(), rec.getHrAvg(), rec.getHrMax(), rec.getAscentM(), rec.getPowerAvg(), rec.getPowerMax(), null, false, null, 12288, null);
        } else {
            rideSummary = null;
        }
        RideSummary summary = rideSummary;
        String path = stopRecording();
        if (summary != null) {
            try {
                RideHistory rideHistory = RideHistory.INSTANCE;
                if (path != null) {
                    strSubstringAfterLast$default = StringsKt.substringAfterLast(path, '/', path);
                    rideHistory.add(summary.copy((12287 & 1) != 0 ? summary.startMs : 0L, (12287 & 2) != 0 ? summary.route : null, (12287 & 4) != 0 ? summary.distanceM : 0.0d, (12287 & 8) != 0 ? summary.movingMs : 0L, (12287 & 16) != 0 ? summary.avgMps : 0.0f, (12287 & 32) != 0 ? summary.maxMps : 0.0f, (12287 & 64) != 0 ? summary.hrAvg : 0, (12287 & 128) != 0 ? summary.hrMax : 0, (12287 & 256) != 0 ? summary.ascentM : 0.0d, (12287 & 512) != 0 ? summary.powerAvg : 0, (12287 & 1024) != 0 ? summary.powerMax : 0, (12287 & 2048) != 0 ? summary.gpx : strSubstringAfterLast$default, (12287 & 4096) != 0 ? summary.uploaded : false, (12287 & 8192) != 0 ? summary.name : null));
                } else {
                    rideHistory.add(summary.copy((12287 & 1) != 0 ? summary.startMs : 0L, (12287 & 2) != 0 ? summary.route : null, (12287 & 4) != 0 ? summary.distanceM : 0.0d, (12287 & 8) != 0 ? summary.movingMs : 0L, (12287 & 16) != 0 ? summary.avgMps : 0.0f, (12287 & 32) != 0 ? summary.maxMps : 0.0f, (12287 & 64) != 0 ? summary.hrAvg : 0, (12287 & 128) != 0 ? summary.hrMax : 0, (12287 & 256) != 0 ? summary.ascentM : 0.0d, (12287 & 512) != 0 ? summary.powerAvg : 0, (12287 & 1024) != 0 ? summary.powerMax : 0, (12287 & 2048) != 0 ? summary.gpx : strSubstringAfterLast$default, (12287 & 4096) != 0 ? summary.uploaded : false, (12287 & 8192) != 0 ? summary.name : null));
                }
            } catch (Throwable th) {
            }
        }
        new Thread(new Runnable() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                RideService.doShutdown$lambda$17(RideService.this);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void doShutdown$lambda$17(RideService this$0) {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "sync"}).waitFor();
        } catch (Throwable th) {
        }
        try {
            Thread.sleep(1500L);
        } catch (Exception e) {
        }
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "svc power shutdown"}).waitFor();
        } catch (Throwable th2) {
        }
        try {
            Thread.sleep(1500L);
        } catch (Exception e2) {
        }
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot -p"}).waitFor();
        } catch (Throwable th3) {
        }
    }

    private final Notification buildShutdownNotification(int sec) {
        PendingIntent cancel = PendingIntent.getService(this, 99, new Intent(this, (Class<?>) RideService.class).setAction(ACTION_CANCEL_SHUTDOWN), 201326592);
        Notification notificationBuild = new Notification.Builder(this, CRIT_CHAN).setContentTitle("Battery 2% — powering off in " + sec + "s").setContentText("Saving your ride. Tap Cancel to keep riding.").setSmallIcon(android.R.drawable.ic_lock_idle_low_battery).setOngoing(true).addAction(0, "Cancel", cancel).build();
        Intrinsics.checkNotNullExpressionValue(notificationBuild, "build(...)");
        return notificationBuild;
    }

    private final HrSensor newHrSensor() {
        return new HrSensor(this, new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda10
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return RideService.newHrSensor$lambda$18(RideService.this, ((Integer) obj).intValue());
            }
        }, new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda11
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return RideService.newHrSensor$lambda$19(((Integer) obj).intValue());
            }
        }, new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda12
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return RideService.newHrSensor$lambda$20(RideService.this, (String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit newHrSensor$lambda$18(RideService this$0, int bpm) {
        this$0.lastHr = bpm;
        this$0.updateLed();
        Function0<Unit> function0 = this$0.onUpdate;
        if (function0 != null) {
            function0.invoke();
        }
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit newHrSensor$lambda$19(int it) {
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit newHrSensor$lambda$20(RideService this$0, String s) {
        Intrinsics.checkNotNullParameter(s, "s");
        this$0.hrStatus = s;
        Function0<Unit> function0 = this$0.onUpdate;
        if (function0 != null) {
            function0.invoke();
        }
        return Unit.INSTANCE;
    }

    private final void startGps() {
        if (checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != 0) {
            return;
        }
        try {
            LocationManager locationManager = this.lm;
            if (locationManager == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lm");
                locationManager = null;
            }
            locationManager.requestLocationUpdates("gps", 2000L, 0.0f, this, Looper.getMainLooper());
        } catch (Exception e) {
        }
    }

    public final void retryPermissions() {
        startGps();
        if (this.hrSensor == null) {
            HrSensor it = newHrSensor();
            it.start();
            this.hrSensor = it;
        }
    }

    private final void setWifi(boolean on) {
        if (on) {
            if (this.wifiToggledOff) {
                this.wifiToggledOff = false;
                Prefs.INSTANCE.setWifiDisabledByApp(this, false);
                rootExec("svc wifi enable");
                return;
            }
            return;
        }
        if (Prefs.INSTANCE.wifiOffOnRide(this)) {
            this.wifiToggledOff = true;
            Prefs.INSTANCE.setWifiDisabledByApp(this, true);
            rootExec("svc wifi disable");
        }
    }

    private final void rootExec(final String cmd) {
        new Thread(new Runnable() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                RideService.rootExec$lambda$22(cmd);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void rootExec$lambda$22(String $cmd) {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", $cmd}).waitFor();
        } catch (Exception e) {
        }
    }

    private final void updateLed() {
        int z;
        if (!this.ledEnabled || Prefs.INSTANCE.endurance(this)) {
            this.led.off();
            return;
        }
        if (getRecorder().isRecording() && this.lastHr > 0 && (z = getRecorder().zoneOf(this.lastHr)) >= 0) {
            int c = (int) HrZone.values()[z].getColor();
            this.led.set(Color.red(c), Color.green(c), Color.blue(c));
        } else {
            this.led.off();
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Intrinsics.areEqual(intent != null ? intent.getAction() : null, ACTION_CANCEL_SHUTDOWN)) {
            abortShutdown();
            return 1;
        }
        if (Intrinsics.areEqual(intent != null ? intent.getAction() : null, ACTION_START_REC) && !getRecorder().isRecording()) {
            this.trail.clear();
            this.lastTrailLoc = null;
            getRecorder().setMaxHrForZones(Prefs.INSTANCE.maxHr(this));
            try { getRecorder().start(); } catch (java.io.IOException e) { throw new RuntimeException(e); }
            startForeground(1, buildNotification());
            acquireWake();
            updateLed();
            if (Prefs.INSTANCE.closeApps(this)) {
                closeOtherApps();
            }
            setWifi(false);
        }
        return 1;
    }

    private final void updateGrade(Location loc, Location prev) {
        double alt;
        if (this.curSpeedMps < 1.0f) {
            this.curGrade *= 0.8f;
            return;
        }
        if (prev != null) {
            this.cumDistM += (double) prev.distanceTo(loc);
        }
        if (getHasBaro() && !Double.isNaN(this.baroAltM)) {
            alt = this.baroAltM;
        } else if (!loc.hasAltitude()) {
            return;
        } else {
            alt = loc.getAltitude();
        }
        this.gradeWin.addLast(new double[]{this.cumDistM, alt});
        while (this.gradeWin.size() > 2 && this.cumDistM - this.gradeWin.first()[0] > 20.0d) {
            this.gradeWin.removeFirst();
        }
        double[] f = this.gradeWin.first();
        double run = this.cumDistM - f[0];
        if (run >= 5.0d) {
            float g = RangesKt.coerceIn((float) (((alt - f[1]) / run) * 100.0d), -40.0f, 40.0f);
            this.curGrade = (this.curGrade * 0.7f) + (0.3f * g);
        }
    }

    private final void autoPauseCheck() {
        if (getRecorder().isRecording() && this.autoPauseEnabled && !this.manualPause) {
            if (this.curSpeedMps < 0.8f) {
                if (this.lowSpeedSince == 0) {
                    this.lowSpeedSince = System.currentTimeMillis();
                }
                if (getRecorder().getPaused() || System.currentTimeMillis() - this.lowSpeedSince <= 4000) {
                    return;
                }
                getRecorder().pause();
                return;
            }
            this.lowSpeedSince = 0L;
            if (getRecorder().getPaused()) {
                getRecorder().resume();
            }
        }
    }

    public final boolean getHrConnected() {
        return this.lastHr > 0;
    }

    public final boolean getCyclingConnected() {
        return this.curPower > 0 || this.curCadence > 0;
    }

    public final String getHrDeviceName() {
        HrSensor hrSensor = this.hrSensor;
        if (hrSensor != null) {
            return hrSensor.getDeviceName();
        }
        return null;
    }

    public final String getCyclingDeviceName() {
        CyclingSensor cyclingSensor = this.cyclingSensor;
        if (cyclingSensor != null) {
            return cyclingSensor.getDeviceName();
        }
        return null;
    }

    public final void rescanSensor() {
        try {
            HrSensor hrSensor = this.hrSensor;
            if (hrSensor != null) {
                hrSensor.stop();
            }
        } catch (Exception e) {
        }
        this.lastHr = 0;
        this.hrStatus = "scanning…";
        HrSensor it = newHrSensor();
        it.start();
        this.hrSensor = it;
    }

    public final void togglePause() {
        if (getRecorder().isRecording()) {
            if (!getRecorder().getPaused()) {
                getRecorder().pause();
                this.manualPause = true;
            } else {
                getRecorder().resume();
                this.manualPause = false;
            }
        }
    }

    public final String stopRecording() {
        this.manualPause = false;
        this.lowSpeedSince = 0L;
        this.led.off();
        String path;
        try { path = getRecorder().stop(); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        this.trail.clear();
        this.lastTrailLoc = null;
        releaseWake();
        setWifi(true);
        stopForeground(1);
        stopSelf();
        return path;
    }

    @Override // android.location.LocationListener
    public void onLocationChanged(Location loc) {
        Location prev;
        Intrinsics.checkNotNullParameter(loc, "loc");
        Location prevLoc = this.lastLocation;
        this.lastLocation = loc;
        float rawSpd = loc.hasSpeed() ? RangesKt.coerceAtLeast(loc.getSpeed(), 0.0f) : 0.0f;
        float it = (this.curSpeedMps * 0.6f) + (0.4f * rawSpd);
        this.curSpeedMps = it >= 0.3f ? it : 0.0f;
        if (loc.hasAltitude()) {
            this.curEleM = loc.getAltitude();
        }
        updateGrade(loc, prevLoc);
        autoPauseCheck();
        if (getRecorder().isRecording()) {
            try { getRecorder().add(loc, this.lastHr, this.curPower, this.curCadence, (!getHasBaro() || Double.isNaN(this.baroAltM)) ? null : Double.valueOf(this.baroAltM)); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        }
        if (getRecorder().isRecording() && ((prev = this.lastTrailLoc) == null || prev.distanceTo(loc) > 4.0f)) {
            this.trail.add(new double[]{loc.getLongitude(), loc.getLatitude()});
            this.lastTrailLoc = loc;
        }
        if (getRecorder().isRecording() && System.currentTimeMillis() - this.lastNotifMs > 5000) {
            this.lastNotifMs = System.currentTimeMillis();
            Object systemService = getSystemService("notification");
            Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.NotificationManager");
            ((NotificationManager) systemService).notify(1, buildNotification());
        }
        Function0<Unit> function0 = this.onUpdate;
        if (function0 != null) {
            function0.invoke();
        }
    }

    @Override // android.location.LocationListener
    public void onProviderEnabled(String p) {
        Intrinsics.checkNotNullParameter(p, "p");
    }

    @Override // android.location.LocationListener
    public void onProviderDisabled(String p) {
        Intrinsics.checkNotNullParameter(p, "p");
    }

    @Override // android.location.LocationListener
    @Deprecated(message = "Deprecated in Java")
    public void onStatusChanged(String p, int s, Bundle e) {
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        if (!getRecorder().isRecording()) {
            stopSelf();
            return false;
        }
        return false;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        setWifi(true);
        try {
            unregisterReceiver(this.batteryReceiver);
        } catch (Exception e) {
        }
        try {
            LocationManager locationManager = this.lm;
            if (locationManager == null) {
                Intrinsics.throwUninitializedPropertyAccessException("lm");
                locationManager = null;
            }
            locationManager.removeUpdates(this);
        } catch (Exception e2) {
        }
        try {
            getSensorMgr().unregisterListener(this.baroListener);
        } catch (Exception e3) {
        }
        this.led.close();
        if (getRecorder().isRecording()) {
            try { getRecorder().stop(); } catch (java.io.IOException e) { throw new RuntimeException(e); }
        }
        releaseWake();
        HrSensor hrSensor = this.hrSensor;
        if (hrSensor != null) {
            hrSensor.stop();
        }
        CyclingSensor cyclingSensor = this.cyclingSensor;
        if (cyclingSensor != null) {
            cyclingSensor.stop();
        }
    }

    private final void closeOtherApps() {
        new Thread(new Runnable() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                RideService.closeOtherApps$lambda$28(RideService.this);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void closeOtherApps$lambda$28(RideService this$0) {
        try {
            final Set keep = SetsKt.setOf((Object[]) new String[]{this$0.getPackageName(), "com.tailscale.ipn"});
            InputStream inputStream = Runtime.getRuntime().exec(new String[]{"su", "-c", "pm list packages -3"}).getInputStream();
            Intrinsics.checkNotNullExpressionValue(inputStream, "getInputStream(...)");
            Reader inputStreamReader = new InputStreamReader(inputStream, Charsets.UTF_8);
            List pkgs = SequencesKt.toList(SequencesKt.filter(SequencesKt.map(StringsKt.lineSequence(TextStreamsKt.readText(inputStreamReader instanceof BufferedReader ? (BufferedReader) inputStreamReader : new BufferedReader(inputStreamReader, 8192))), new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda13
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return RideService.closeOtherApps$lambda$28$lambda$25((String) obj);
                }
            }), new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda14
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return Boolean.valueOf(RideService.closeOtherApps$lambda$28$lambda$26(keep, (String) obj));
                }
            }));
            if (!pkgs.isEmpty()) {
                String cmd = CollectionsKt.joinToString(pkgs, "\n", "", "", -1, "...", new Function1() { // from class: com.bike.computer.RideService$$ExternalSyntheticLambda1
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return RideService.closeOtherApps$lambda$28$lambda$27((String) obj);
                    }
                });
                Runtime.getRuntime().exec(new String[]{"su", "-c", cmd}).waitFor();
            }
        } catch (Exception e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String closeOtherApps$lambda$28$lambda$25(String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return StringsKt.trim((CharSequence) StringsKt.removePrefix(it, (CharSequence) "package:")).toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean closeOtherApps$lambda$28$lambda$26(Set $keep, String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return (it.length() > 0) && !$keep.contains(it);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CharSequence closeOtherApps$lambda$28$lambda$27(String it) {
        Intrinsics.checkNotNullParameter(it, "it");
        return "am force-stop " + it;
    }

    private final void acquireWake() {
        Object systemService = getSystemService("power");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.os.PowerManager");
        PowerManager pm = (PowerManager) systemService;
        PowerManager.WakeLock $this$acquireWake_u24lambda_u2429 = pm.newWakeLock(1, "bike:recording");
        $this$acquireWake_u24lambda_u2429.acquire();
        this.wakeLock = $this$acquireWake_u24lambda_u2429;
    }

    private final void releaseWake() {
        try {
            PowerManager.WakeLock it = this.wakeLock;
            if (it != null && it.isHeld()) {
                it.release();
            }
        } catch (Exception e) {
        }
        this.wakeLock = null;
    }

    private final void createChannel() {
        Object systemService = getSystemService("notification");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.app.NotificationManager");
        NotificationManager nm = (NotificationManager) systemService;
        NotificationChannel $this$createChannel_u24lambda_u2431 = new NotificationChannel(CHAN, "Ride recording", 2);
        $this$createChannel_u24lambda_u2431.setShowBadge(false);
        nm.createNotificationChannel($this$createChannel_u24lambda_u2431);
        NotificationChannel $this$createChannel_u24lambda_u2432 = new NotificationChannel(CRIT_CHAN, "Critical battery", 4);
        $this$createChannel_u24lambda_u2432.enableVibration(true);
        $this$createChannel_u24lambda_u2432.setShowBadge(true);
        nm.createNotificationChannel($this$createChannel_u24lambda_u2432);
    }

    private final Notification buildNotification() {
        PendingIntent open = PendingIntent.getActivity(this, 0, new Intent(this, (Class<?>) MainActivity.class), AccessibilityEventCompat.TYPE_VIEW_TARGETED_BY_SCROLL);
        StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
        String mi = String.format(Locale.US, "%.2f mi", Arrays.copyOf(new Object[]{Double.valueOf(Units.INSTANCE.miles(getRecorder().getDistanceM()))}, 1));
        Intrinsics.checkNotNullExpressionValue(mi, "format(...)");
        Notification notificationBuild = new Notification.Builder(this, CHAN).setContentTitle("Recording ride").setContentText(Units.INSTANCE.fmtHms(getRecorder().getElapsedMs()) + " · " + mi).setSmallIcon(android.R.drawable.ic_media_play).setOngoing(true).setContentIntent(open).build();
        Intrinsics.checkNotNullExpressionValue(notificationBuild, "build(...)");
        return notificationBuild;
    }
}

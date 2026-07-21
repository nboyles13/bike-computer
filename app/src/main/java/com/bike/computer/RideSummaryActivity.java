package com.bike.computer;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

public final class RideSummaryActivity extends Activity {
    private LinearLayout container;
    private long zoneMaxMs;
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("EEE d MMM · h:mm a", Locale.US);
    private final SimpleDateFormat shortDate = new SimpleDateFormat("d MMM yyyy", Locale.US);
    private final String ridesDir = "/sdcard/BikeComputer/rides";

    @Override // android.app.Activity
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list);
        ImmersiveKt.enterImmersive(this);
        this.container = (LinearLayout) findViewById(R.id.list_container);
        long startMs = getIntent().getLongExtra("startMs", 0L);
        boolean toWelcome = getIntent().getBooleanExtra("welcomeOnDone", false);
        TextView doneBtn = (TextView) findViewById(R.id.list_done);
        TextView back = (TextView) findViewById(R.id.list_back);
        if (toWelcome) {
            doneBtn.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda18
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    RideSummaryActivity.this.done(true);
                }
            });
        } else {
            doneBtn.setVisibility(8);
            back.setVisibility(0);
            back.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda19
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    RideSummaryActivity.this.finish();
                }
            });
        }
        final RideSummary ride = RideHistory.INSTANCE.byStart(startMs);
        TextView title = (TextView) findViewById(R.id.list_title);
        if (ride == null) {
            title.setText("Ride");
            hint("Ride not found.");
            return;
        }
        String name = ride.getName();
        if (name == null && (name = ride.getRoute()) == null) {
            name = "Ride summary";
        }
        title.setText(name);
        TextView $this$onCreate_u24lambda_u243 = (TextView) findViewById(R.id.list_menu);
        $this$onCreate_u24lambda_u243.setVisibility(0);
        $this$onCreate_u24lambda_u243.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda20
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RideSummaryActivity.onCreate$lambda$3$lambda$2(RideSummaryActivity.this, ride, view);
            }
        });
        String str = this.dateFmt.format(new Date(ride.getStartMs()));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        text(str);
        final TextView $this$onCreate_u24lambda_u244 = new TextView(this);
        $this$onCreate_u24lambda_u244.setText("Loading…");
        $this$onCreate_u24lambda_u244.setTextColor(Color.parseColor("#FF8E8E93"));
        $this$onCreate_u24lambda_u244.setTextSize(15.0f);
        $this$onCreate_u24lambda_u244.setPadding(dp(4), dp(12), dp(4), dp(12));
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView($this$onCreate_u24lambda_u244);
        final File f = gpxFile(ride);
        new Thread(new Runnable() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda21
            @Override // java.lang.Runnable
            public final void run() {
                RideSummaryActivity.onCreate$lambda$10(f, RideSummaryActivity.this, $this$onCreate_u24lambda_u244, ride);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$3$lambda$2(RideSummaryActivity this$0, RideSummary $ride, View it) {
        this$0.showRideMenu($ride, this$0.gpxFile($ride));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:15:0x002a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void onCreate$lambda$10(File $f, final RideSummaryActivity this$0, final TextView $loading, final RideSummary $ride) {
        String str = "";
        if ($f != null) {
            try {
                str = FilesKt.readText($f, kotlin.text.Charsets.UTF_8);
            } catch (Throwable th) {
                str = null;
            }
            if (str == null) {
                str = "";
            }
        }
        String gpxText = str;
        Object objM118constructorimpl;
        try {
            objM118constructorimpl = GpxRoute.INSTANCE.parse(gpxText);
        } catch (Throwable th2) {
            objM118constructorimpl = null;
        }
        List listEmptyList = CollectionsKt.emptyList();
        if (objM118constructorimpl == null) {
            objM118constructorimpl = listEmptyList;
        }
        final List pts = (List) objM118constructorimpl;
        final int maxHr = Prefs.INSTANCE.maxHr(this$0);
        final long[] zones = gpxText.length() > 0 ? GpxSummary.INSTANCE.zoneTimes(gpxText, maxHr) : new long[5];
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                RideSummaryActivity.onCreate$lambda$10$lambda$9(this$0, $loading, pts, $ride, zones, maxHr);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$10$lambda$9(RideSummaryActivity this$0, TextView $loading, List $pts, RideSummary $ride, long[] $zones, int $maxHr) {
        if (this$0.isFinishing()) {
            return;
        }
        LinearLayout linearLayout = this$0.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.removeView($loading);
        if ($pts.size() >= 2) {
            this$0.routeMap($pts);
        }
        this$0.stat("Distance", Units.INSTANCE.fmtDist($ride.getDistanceM()) + " mi");
        this$0.stat("Moving time", this$0.fmtDur($ride.getMovingMs()));
        this$0.stat("Avg speed", Units.INSTANCE.fmtSpeed($ride.getAvgMps()) + " mph");
        this$0.stat("Fastest speed", Units.INSTANCE.fmtSpeed($ride.getMaxMps()) + " mph");
        if ($ride.getHrMax() > 0) {
            this$0.stat("Highest HR", $ride.getHrMax() + " bpm");
        }
        if ($ride.getHrAvg() > 0) {
            this$0.stat("Avg HR", $ride.getHrAvg() + " bpm");
        }
        if ($ride.getAscentM() > 0.0d) {
            this$0.stat("Ascent", Units.INSTANCE.fmtFeet($ride.getAscentM()) + " ft");
        }
        if ($ride.getPowerMax() > 0) {
            this$0.stat("Max power", $ride.getPowerMax() + " W");
        }
        if ($ride.getPowerAvg() > 0) {
            this$0.stat("Avg power", $ride.getPowerAvg() + " W");
        }
        this$0.hrZonesSection($ride, $zones, $maxHr);
        String it = $ride.getRoute();
        if (it != null) {
            this$0.showComparison(it, $ride);
        }
    }

    private final File gpxFile(RideSummary ride) {
        String it = ride.getGpx();
        if (it == null) {
            return null;
        }
        File it2 = new File(this.ridesDir, it);
        if (it2.exists()) {
            return it2;
        }
        return null;
    }

    private final void routeMap(List<double[]> pts) {
        FrameLayout card = new FrameLayout(this);
        card.setBackgroundResource(R.drawable.card_solid);
        RouteThumb $this$routeMap_u24lambda_u2413 = new RouteThumb(this);
        $this$routeMap_u24lambda_u2413.setPoints(pts);
        card.addView($this$routeMap_u24lambda_u2413, new FrameLayout.LayoutParams(-1, -1));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(180));
        lp.setMargins(dp(2), dp(6), dp(2), dp(10));
        card.setLayoutParams(lp);
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(card);
    }

    private final void showRideMenu(final RideSummary ride, final File f) {
        ArrayList labels = new ArrayList();
        final ArrayList acts = new ArrayList();
        labels.add("Rename ride");
        acts.add(new Function0() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return RideSummaryActivity.showRideMenu$lambda$14(RideSummaryActivity.this, ride);
            }
        });
        if (f != null) {
            labels.add("Save as a route");
            acts.add(new Function0() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda3
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return RideSummaryActivity.showRideMenu$lambda$15(RideSummaryActivity.this, ride, f);
                }
            });
            if (Prefs.INSTANCE.driveConnected(this)) {
                if (ride.getUploaded()) {
                    labels.add("Uploaded to Drive ✓");
                    acts.add(new Function0() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda4
                        @Override // kotlin.jvm.functions.Function0
                        public final Object invoke() {
                            return RideSummaryActivity.showRideMenu$lambda$16(RideSummaryActivity.this);
                        }
                    });
                } else {
                    labels.add("Upload to Drive");
                    acts.add(new Function0() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda5
                        @Override // kotlin.jvm.functions.Function0
                        public final Object invoke() {
                            return RideSummaryActivity.showRideMenu$lambda$17(RideSummaryActivity.this, ride, f);
                        }
                    });
                }
            }
            labels.add("Share GPX");
            acts.add(new Function0() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda6
                @Override // kotlin.jvm.functions.Function0
                public final Object invoke() {
                    return RideSummaryActivity.showRideMenu$lambda$18(RideSummaryActivity.this, f);
                }
            });
        }
        labels.add("Delete ride");
        acts.add(new Function0() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda7
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return RideSummaryActivity.showRideMenu$lambda$19(RideSummaryActivity.this, ride);
            }
        });
        ArrayList $this$toTypedArray$iv = labels;
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert).setTitle("Ride options").setItems((CharSequence[]) $this$toTypedArray$iv.toArray(new String[0]), new DialogInterface.OnClickListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda8
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                RideSummaryActivity.showRideMenu$lambda$20(acts, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit showRideMenu$lambda$14(RideSummaryActivity this$0, RideSummary $ride) {
        this$0.promptRename($ride);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit showRideMenu$lambda$15(RideSummaryActivity this$0, RideSummary $ride, File $f) {
        this$0.promptSaveAsRoute($ride, $f);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit showRideMenu$lambda$16(RideSummaryActivity this$0) {
        this$0.toast("Already uploaded");
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit showRideMenu$lambda$17(RideSummaryActivity this$0, RideSummary $ride, File $f) {
        this$0.uploadToDrive($ride, $f);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit showRideMenu$lambda$18(RideSummaryActivity this$0, File $f) {
        this$0.shareGpx($f);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit showRideMenu$lambda$19(RideSummaryActivity this$0, RideSummary $ride) {
        this$0.confirmDelete($ride);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void showRideMenu$lambda$20(ArrayList $acts, DialogInterface dialogInterface, int i) {
        ((Function0) $acts.get(i)).invoke();
    }

    private final void promptRename(final RideSummary ride) {
        String route;
        final EditText input = new EditText(this);
        RideSummary rideSummaryByStart = RideHistory.INSTANCE.byStart(ride.getStartMs());
        if ((rideSummaryByStart == null || (route = rideSummaryByStart.getName()) == null) && (route = ride.getRoute()) == null) {
            route = "";
        }
        input.setText(route);
        input.setHint("Ride name");
        input.setSingleLine(true);
        input.setSelectAllOnFocus(true);
        input.setTextColor(-1);
        input.setHintTextColor(Color.parseColor("#FF6E6E6E"));
        input.setPadding(dp(16), dp(12), dp(16), dp(12));
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert).setTitle("Rename ride").setMessage("Give this ride its own name. This is separate from any route it followed.").setView(input).setPositiveButton("Save", new DialogInterface.OnClickListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda13
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                RideSummaryActivity.promptRename$lambda$22(input, ride, RideSummaryActivity.this, dialogInterface, i);
            }
        }).setNeutralButton("Clear name", new DialogInterface.OnClickListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda14
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                RideSummaryActivity.promptRename$lambda$23(ride, RideSummaryActivity.this, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void promptRename$lambda$22(EditText $input, RideSummary $ride, RideSummaryActivity this$0, DialogInterface dialogInterface, int i) {
        String str;
        String strTake = StringsKt.take(StringsKt.trim((CharSequence) $input.getText().toString()).toString(), 80);
        if (StringsKt.isBlank(strTake)) {
            strTake = null;
        }
        String n = strTake;
        RideHistory.INSTANCE.rename($ride.getStartMs(), n);
        TextView textView = (TextView) this$0.findViewById(R.id.list_title);
        if (n != null) {
            str = n;
        } else {
            String route = $ride.getRoute();
            if (route == null) {
                route = "Ride summary";
            }
            str = route;
        }
        textView.setText(str);
        this$0.toast(n == null ? "Name cleared" : "Renamed");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void promptRename$lambda$23(RideSummary $ride, RideSummaryActivity this$0, DialogInterface dialogInterface, int i) {
        RideHistory.INSTANCE.rename($ride.getStartMs(), null);
        TextView textView = (TextView) this$0.findViewById(R.id.list_title);
        String route = $ride.getRoute();
        if (route == null) {
            route = "Ride summary";
        }
        textView.setText(route);
        this$0.toast("Name cleared");
    }

    private final void confirmDelete(final RideSummary ride) {
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert).setTitle("Delete ride?").setMessage("Removes this ride from your history and deletes its GPX track. This can't be undone.").setPositiveButton("Delete", new DialogInterface.OnClickListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda9
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                RideSummaryActivity.confirmDelete$lambda$26(ride, RideSummaryActivity.this, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void confirmDelete$lambda$26(final RideSummary $ride, final RideSummaryActivity this$0, DialogInterface dialogInterface, int i) {
        new Thread(new Runnable() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                RideSummaryActivity.confirmDelete$lambda$26$lambda$25($ride, this$0);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void confirmDelete$lambda$26$lambda$25(RideSummary $ride, final RideSummaryActivity this$0) {
        RideHistory.INSTANCE.delete($ride.getStartMs());
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                RideSummaryActivity.confirmDelete$lambda$26$lambda$25$lambda$24(this$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void confirmDelete$lambda$26$lambda$25$lambda$24(RideSummaryActivity this$0) {
        this$0.toast("Ride deleted");
        this$0.done(this$0.getIntent().getBooleanExtra("welcomeOnDone", false));
    }

    private final void uploadToDrive(final RideSummary ride, final File f) {
        toast("Uploading to Drive…");
        new Thread(new Runnable() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                RideSummaryActivity.uploadToDrive$lambda$31(RideSummaryActivity.this, f, ride);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void uploadToDrive$lambda$31(final RideSummaryActivity this$0, File $f, final RideSummary $ride) {
        final Object r;
        Object objTmp;
        try {
            objTmp = GoogleDriveClient.INSTANCE.uploadGpx(this$0, $f);
        } catch (Throwable th) {
            objTmp = th;
        }
        r = objTmp;
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                RideSummaryActivity.uploadToDrive$lambda$31$lambda$30(r, $ride, this$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void uploadToDrive$lambda$31$lambda$30(Object $r, RideSummary $ride, RideSummaryActivity this$0) {
        if (!($r instanceof Throwable)) {
            RideHistory.INSTANCE.markUploaded($ride.getStartMs());
            this$0.toast((String) $r);
            this$0.recreate();
        }
        Throwable it = $r instanceof Throwable ? (Throwable) $r : null;
        if (it != null) {
            this$0.toast("Upload failed: " + it.getMessage());
        }
    }

    private final void shareGpx(File f) {
        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", f);
            Intent $this$shareGpx_u24lambda_u2432 = new Intent("android.intent.action.SEND");
            $this$shareGpx_u24lambda_u2432.setType("application/gpx+xml");
            $this$shareGpx_u24lambda_u2432.putExtra("android.intent.extra.STREAM", uri);
            $this$shareGpx_u24lambda_u2432.addFlags(1);
            startActivity(Intent.createChooser($this$shareGpx_u24lambda_u2432, "Share ride"));
        } catch (Exception e) {
            toast("Share failed: " + e.getMessage());
        }
    }

    private final void toast(String s) {
        Toast.makeText(this, s, 0).show();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImmersiveKt.enterImmersive(this);
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        done(getIntent().getBooleanExtra("welcomeOnDone", false));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void done(boolean toWelcome) {
        if (toWelcome) {
            startActivity(new Intent(this, (Class<?>) WelcomeActivity.class).addFlags(603979776));
        }
        finish();
    }

    private final void showComparison(String route, RideSummary ride) {
        List<RideSummary> listForRoute = RideHistory.INSTANCE.forRoute(route);
        header(listForRoute.size() <= 1 ? "First time on this route" : "vs your other " + (listForRoute.size() - 1) + " on “" + route + "”");
        RideSummary best = (RideSummary) CollectionsKt.first((List) listForRoute);
        boolean isPR = best.getStartMs() == ride.getStartMs();
        if (listForRoute.size() > 1) {
            if (isPR) {
                stat("This ride", fmtDur(ride.getMovingMs()) + "  🏆 fastest!");
            } else {
                stat("This ride", fmtDur(ride.getMovingMs()));
                stat("Best", fmtDur(best.getMovingMs()) + " · " + this.shortDate.format(new Date(best.getStartMs())));
                stat("Behind best", "+" + fmtDur(ride.getMovingMs() - best.getMovingMs()));
            }
        }
        int i = 0;
        for (RideSummary a : listForRoute) {
            int i2 = i;
            i++;
            attemptRow(i2 + 1, a, a.getStartMs() == ride.getStartMs(), i2 == 0);
        }
    }

    private final void hrZonesSection(RideSummary ride, long[] zones, int maxHr) {
        long total = ArraysKt.sum(zones);
        if (total <= 0) {
            return;
        }
        this.zoneMaxMs = ArraysKt.maxOrThrow(zones);
        header("Heart rate zones");
        text(ride.getHrMax() > maxHr ? "Based on max HR " + maxHr + " bpm — but this ride peaked at " + ride.getHrMax() + ". Raise it in Settings ▸ Sensors to shift time into the lower zones." : "Based on your max HR of " + maxHr + " bpm (60 / 70 / 80 / 90 %). Change it in Settings ▸ Sensors.");
        for (int i = 4; -1 < i; i--) {
            zoneBar(i, HrZone.values()[i], zones[i], total, maxHr);
        }
    }

    private final void zoneBar(int idx, HrZone z, long ms, long total, int maxHr) {
        String str;
        float pct = total > 0 ? ms / total : 0.0f;
        int color = (int) z.getColor();
        boolean dominant = total > 0 && ms == this.zoneMaxMs;
        switch (idx) {
            case 0:
                str = "below " + ((maxHr * 60) / 100);
                break;
            case 4:
                str = ((maxHr * 90) / 100) + "+";
                break;
            default:
                str = ((z.getLowerPct() * maxHr) / 100) + "–" + (((z.getLowerPct() + 10) * maxHr) / 100);
                break;
        }
        String range = str;
        Typeface medium = Typeface.create("sans-serif-medium", 0);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(0);
        row.setGravity(16);
        row.setPadding(dp(6), dp(9), dp(8), dp(9));
        if (dominant) {
            row.setBackground(roundRect(withAlpha(color, 31), 12));
        }
        TextView badge = new TextView(this);
        badge.setText(String.valueOf(idx + 1));
        badge.setTextColor(-1);
        badge.setTextSize(15.0f);
        badge.setTypeface(medium, 1);
        badge.setGravity(17);
        badge.setBackground(roundRect(color, 9));
        LinearLayout.LayoutParams $this$zoneBar_u24lambda_u2433 = new LinearLayout.LayoutParams(dp(32), dp(32));
        $this$zoneBar_u24lambda_u2433.rightMargin = dp(13);
        badge.setLayoutParams($this$zoneBar_u24lambda_u2433);
        LinearLayout col = new LinearLayout(this);
        col.setOrientation(1);
        col.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        LinearLayout lineA = new LinearLayout(this);
        lineA.setOrientation(0);
        lineA.setGravity(80);
        TextView name = new TextView(this);
        name.setText(z.getLabel());
        name.setTextColor(-1);
        name.setTextSize(15.0f);
        name.setTypeface(medium);
        TextView rng = new TextView(this);
        rng.setText("  " + range);
        rng.setTextColor(Color.parseColor("#FF7C7C82"));
        rng.setTextSize(12.0f);
        rng.setPadding(0, 0, 0, dp(1));
        LinearLayout nameWrap = new LinearLayout(this);
        nameWrap.setOrientation(0);
        nameWrap.setGravity(80);
        nameWrap.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        nameWrap.addView(name);
        nameWrap.addView(rng);
        TextView time = new TextView(this);
        time.setText(fmtDur(ms));
        time.setTextColor(-1);
        time.setTextSize(16.0f);
        time.setTypeface(medium, 1);
        TextView pctv = new TextView(this);
        pctv.setText("  " + Math.round(100 * pct) + "%");
        pctv.setTextSize(13.0f);
        pctv.setTextColor(Color.parseColor("#FF9A9AA0"));
        pctv.setPadding(0, 0, 0, dp(1));
        LinearLayout valWrap = new LinearLayout(this);
        valWrap.setOrientation(0);
        valWrap.setGravity(80);
        valWrap.addView(time);
        valWrap.addView(pctv);
        lineA.addView(nameWrap);
        lineA.addView(valWrap);
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(0);
        bar.setBackground(roundRect(Color.parseColor("#17FFFFFF"), 5));
        LinearLayout.LayoutParams $this$zoneBar_u24lambda_u2434 = new LinearLayout.LayoutParams(-1, dp(9));
        $this$zoneBar_u24lambda_u2434.topMargin = dp(9);
        bar.setLayoutParams($this$zoneBar_u24lambda_u2434);
        View fill = new View(this);
        fill.setBackground(roundRect(color, 5));
        fill.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0E-4f));
        View rest = new View(this);
        rest.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
        bar.addView(fill);
        bar.addView(rest);
        col.addView(lineA);
        col.addView(bar);
        row.addView(badge);
        row.addView(col);
        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(-1, -2);
        rlp.topMargin = dp(3);
        row.setLayoutParams(rlp);
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(row);
        animateBar(bar, fill, rest, RangesKt.coerceAtLeast(pct, 0.006f), 4 - idx);
    }

    private final void animateBar(final View bar, View fill, View rest, float target, int order) {
        ViewGroup.LayoutParams layoutParams = fill.getLayoutParams();
        Intrinsics.checkNotNull(layoutParams, "null cannot be cast to non-null type android.widget.LinearLayout.LayoutParams");
        final LinearLayout.LayoutParams flp = (LinearLayout.LayoutParams) layoutParams;
        ViewGroup.LayoutParams layoutParams2 = rest.getLayoutParams();
        Intrinsics.checkNotNull(layoutParams2, "null cannot be cast to non-null type android.widget.LinearLayout.LayoutParams");
        final LinearLayout.LayoutParams rlp = (LinearLayout.LayoutParams) layoutParams2;
        ValueAnimator $this$animateBar_u24lambda_u2436 = ValueAnimator.ofFloat(0.0f, target);
        $this$animateBar_u24lambda_u2436.setDuration(620L);
        $this$animateBar_u24lambda_u2436.setStartDelay((((long) order) * 75) + 140);
        $this$animateBar_u24lambda_u2436.setInterpolator(new DecelerateInterpolator(1.4f));
        $this$animateBar_u24lambda_u2436.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda15
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                RideSummaryActivity.animateBar$lambda$36$lambda$35(flp, rlp, bar, valueAnimator);
            }
        });
        $this$animateBar_u24lambda_u2436.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void animateBar$lambda$36$lambda$35(LinearLayout.LayoutParams $flp, LinearLayout.LayoutParams $rlp, View $bar, ValueAnimator a) {
        Intrinsics.checkNotNullParameter(a, "a");
        Object animatedValue = a.getAnimatedValue();
        Intrinsics.checkNotNull(animatedValue, "null cannot be cast to non-null type kotlin.Float");
        float f = ((Float) animatedValue).floatValue();
        $flp.weight = f;
        $rlp.weight = RangesKt.coerceAtLeast(1.0f - f, 1.0E-4f);
        $bar.requestLayout();
    }

    private final GradientDrawable roundRect(int color, int radiusDp) {
        GradientDrawable $this$roundRect_u24lambda_u2437 = new GradientDrawable();
        $this$roundRect_u24lambda_u2437.setColor(color);
        $this$roundRect_u24lambda_u2437.setCornerRadius(dp(radiusDp));
        return $this$roundRect_u24lambda_u2437;
    }

    private final int withAlpha(int color, int alpha) {
        return (16777215 & color) | (alpha << 24);
    }

    private final void promptSaveAsRoute(RideSummary ride, final File f) {
        final EditText input = new EditText(this);
        String route = ride.getRoute();
        if (route == null) {
            route = "Ride " + this.shortDate.format(new Date(ride.getStartMs()));
        }
        input.setText(route);
        input.setSingleLine(true);
        input.setSelectAllOnFocus(true);
        input.setTextColor(-1);
        input.setPadding(dp(16), dp(12), dp(16), dp(12));
        new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert).setTitle("Save as route").setMessage("Saves this ride's track as a route you can navigate again (from the home screen or Settings ▸ Routes).").setView(input).setPositiveButton("Save", new DialogInterface.OnClickListener() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda11
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                RideSummaryActivity.promptSaveAsRoute$lambda$43(input, RideSummaryActivity.this, f, dialogInterface, i);
            }
        }).setNegativeButton("Cancel", (DialogInterface.OnClickListener) null).show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void promptSaveAsRoute$lambda$43(EditText $input, final RideSummaryActivity this$0, final File $f, DialogInterface dialogInterface, int i) {
        final String name = StringsKt.take(new Regex("[/\\\\:*?\"<>|]").replace(StringsKt.trim((CharSequence) $input.getText().toString()).toString(), "_"), 60);
        if (name.length() == 0) {
            this$0.toast("Name required");
        } else {
            this$0.toast("Saving route…");
            new Thread(new Runnable() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    RideSummaryActivity.promptSaveAsRoute$lambda$43$lambda$42(this$0, $f, name);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void promptSaveAsRoute$lambda$43$lambda$42(final RideSummaryActivity this$0, File $f, final String $name) {
        final Object r;
        Object objTmp;
        try {
            File dir = new File("/sdcard/BikeComputer/routes");
            dir.mkdirs();
            objTmp = FilesKt.copyTo($f, new File(dir, $name + ".gpx"), true, 8192);
        } catch (Throwable th) {
            objTmp = th;
        }
        r = objTmp;
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.RideSummaryActivity$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                RideSummaryActivity.promptSaveAsRoute$lambda$43$lambda$42$lambda$41(r, this$0, $name);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void promptSaveAsRoute$lambda$43$lambda$42$lambda$41(Object $r, RideSummaryActivity this$0, String $name) {
        if (!($r instanceof Throwable)) {
            this$0.toast("Saved route “" + $name + "”");
        }
        Throwable it = $r instanceof Throwable ? (Throwable) $r : null;
        if (it != null) {
            this$0.toast("Save failed: " + it.getMessage());
        }
    }

    private final int dp(int v) {
        return (int) TypedValue.applyDimension(1, v, getResources().getDisplayMetrics());
    }

    private final void text(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(Color.parseColor("#FF9E9E9E"));
        t.setTextSize(15.0f);
        t.setPadding(dp(4), dp(2), dp(4), dp(10));
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(t);
    }

    private final void hint(String s) {
        text(s);
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
        t.setPadding(dp(4), dp(22), dp(4), dp(8));
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(t);
    }

    private final void stat(String label, String value) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(0);
        row.setGravity(16);
        row.setPadding(dp(4), dp(15), dp(4), dp(15));
        TextView l = new TextView(this);
        l.setText(label);
        l.setTextColor(Color.parseColor("#FFB8B8BD"));
        l.setTextSize(17.0f);
        l.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView v = new TextView(this);
        v.setText(value);
        v.setTextColor(-1);
        v.setTextSize(20.0f);
        v.setTypeface(Typeface.create("sans-serif-medium", 0));
        row.addView(l);
        row.addView(v);
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(row);
        divider();
    }

    private final void attemptRow(int rank, RideSummary a, boolean isThis, boolean isBest) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(0);
        row.setGravity(16);
        row.setPadding(dp(4), dp(13), dp(4), dp(13));
        TextView left = new TextView(this);
        left.setText(rank + ".  " + this.shortDate.format(new Date(a.getStartMs())) + (isThis ? "  ← this ride" : ""));
        left.setTextColor(Color.parseColor(isThis ? "#FF30D158" : "#FFB8B8BD"));
        left.setTextSize(16.0f);
        left.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView v = new TextView(this);
        v.setText(fmtDur(a.getMovingMs()) + (isBest ? "  🏆" : ""));
        v.setTextColor(isBest ? Color.parseColor("#FFFFD60A") : -1);
        v.setTextSize(18.0f);
        v.setTypeface(Typeface.create("sans-serif-medium", 0));
        row.addView(left);
        row.addView(v);
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(row);
        divider();
    }

    private final void divider() {
        View d = new View(this);
        d.setBackgroundColor(Color.parseColor("#1FFFFFFF"));
        d.setLayoutParams(new LinearLayout.LayoutParams(-1, 1));
        LinearLayout linearLayout = this.container;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(d);
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

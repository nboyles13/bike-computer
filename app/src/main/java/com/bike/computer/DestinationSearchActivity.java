package com.bike.computer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bike.computer.Geocoder;
import com.bike.computer.RideService;
import java.util.List;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

public final class DestinationSearchActivity extends Activity {
    private final ServiceConnection conn = new ServiceConnection() { // from class: com.bike.computer.DestinationSearchActivity$conn$1
        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName n, IBinder b) {
            DestinationSearchActivity destinationSearchActivity = DestinationSearchActivity.this;
            Intrinsics.checkNotNull(b, "null cannot be cast to non-null type com.bike.computer.RideService.LocalBinder");
            destinationSearchActivity.ride = ((RideService.LocalBinder) b).getThis$0();
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName n) {
            DestinationSearchActivity.this.ride = null;
        }
    };
    private LinearLayout container;
    private LinearLayout results;
    private RideService ride;

    @Override // android.app.Activity
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_list);
        ImmersiveKt.enterImmersive(this);
        this.container = (LinearLayout) findViewById(R.id.list_container);
        ((TextView) findViewById(R.id.list_title)).setText("Navigate to…");
        ((TextView) findViewById(R.id.list_done)).setVisibility(8);
        TextView $this$onCreate_u24lambda_u241 = (TextView) findViewById(R.id.list_back);
        $this$onCreate_u24lambda_u241.setVisibility(0);
        $this$onCreate_u24lambda_u241.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.DestinationSearchActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DestinationSearchActivity.this.finish();
            }
        });
        final EditText field = new EditText(this);
        field.setBackgroundResource(R.drawable.card_solid);
        field.setPadding(dp(16), dp(14), dp(16), dp(14));
        field.setTextColor(-1);
        field.setTextSize(17.0f);
        field.setSingleLine(true);
        field.setHint("Place or address");
        field.setHintTextColor(Color.parseColor("#FF6E6E6E"));
        field.setInputType(8193);
        field.setImeOptions(3);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(dp(2), dp(4), dp(2), dp(4));
        field.setLayoutParams(lp);
        field.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.bike.computer.DestinationSearchActivity$$ExternalSyntheticLambda2
            @Override // android.widget.TextView.OnEditorActionListener
            public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return DestinationSearchActivity.onCreate$lambda$2(DestinationSearchActivity.this, field, textView, i, keyEvent);
            }
        });
        LinearLayout linearLayout = this.container;
        LinearLayout linearLayout2 = null;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        linearLayout.addView(field);
        TextView btn = new TextView(this);
        btn.setText("Search");
        btn.setTextColor(Color.parseColor("#FF4C8DFF"));
        btn.setTextSize(17.0f);
        btn.setBackgroundResource(R.drawable.card_solid);
        btn.setPadding(dp(16), dp(14), dp(16), dp(14));
        btn.setGravity(17);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(-1, -2);
        lp2.setMargins(dp(2), dp(4), dp(2), dp(8));
        btn.setLayoutParams(lp2);
        btn.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.DestinationSearchActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DestinationSearchActivity.onCreate$lambda$3(DestinationSearchActivity.this, field, view);
            }
        });
        LinearLayout linearLayout3 = this.container;
        if (linearLayout3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout3 = null;
        }
        linearLayout3.addView(btn);
        this.results = new LinearLayout(this);
        LinearLayout linearLayout4 = this.results;
        if (linearLayout4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("results");
            linearLayout4 = null;
        }
        linearLayout4.setOrientation(1);
        LinearLayout linearLayout5 = this.container;
        if (linearLayout5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout5 = null;
        }
        LinearLayout linearLayout6 = this.results;
        if (linearLayout6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("results");
        } else {
            linearLayout2 = linearLayout6;
        }
        linearLayout5.addView(linearLayout2);
        field.requestFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean onCreate$lambda$2(DestinationSearchActivity this$0, EditText $field, TextView textView, int id, KeyEvent keyEvent) {
        if (id != 3) {
            return false;
        }
        this$0.doSearch($field.getText().toString());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void onCreate$lambda$3(DestinationSearchActivity this$0, EditText $field, View it) {
        this$0.doSearch($field.getText().toString());
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, (Class<?>) RideService.class), this.conn, 1);
    }

    @Override // android.app.Activity
    protected void onStop() {
        try {
            unbindService(this.conn);
        } catch (Exception e) {
        }
        super.onStop();
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            ImmersiveKt.enterImmersive(this);
        }
    }

    private final void doSearch(final String q) {
        Location it;
        if (StringsKt.trim((CharSequence) q).toString().length() == 0) {
            return;
        }
        Object systemService = getSystemService("input_method");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.view.inputmethod.InputMethodManager");
        InputMethodManager inputMethodManager = (InputMethodManager) systemService;
        LinearLayout linearLayout = this.container;
        double[] dArr = null;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("container");
            linearLayout = null;
        }
        inputMethodManager.hideSoftInputFromWindow(linearLayout.getWindowToken(), 0);
        LinearLayout linearLayout2 = this.results;
        if (linearLayout2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("results");
            linearLayout2 = null;
        }
        linearLayout2.removeAllViews();
        hint("Searching…");
        RideService rideService = this.ride;
        if (rideService != null && (it = rideService.getLastLocation()) != null) {
            dArr = new double[]{it.getLatitude(), it.getLongitude()};
        }
        final double[] near = dArr;
        new Thread(new Runnable() { // from class: com.bike.computer.DestinationSearchActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DestinationSearchActivity.doSearch$lambda$9(DestinationSearchActivity.this, q, near);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void doSearch$lambda$9(final DestinationSearchActivity this$0, final String $q, double[] $near) {
        Object r0;
        try {
            r0 = Geocoder.INSTANCE.search($q, $near);
        } catch (Throwable th) {
            r0 = th;
        }
        final Object r = r0;
        this$0.runOnUiThread(new Runnable() { // from class: com.bike.computer.DestinationSearchActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                DestinationSearchActivity.doSearch$lambda$9$lambda$8(this$0, r, $q);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void doSearch$lambda$9$lambda$8(DestinationSearchActivity this$0, Object $r, String $q) {
        LinearLayout linearLayout = this$0.results;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("results");
            linearLayout = null;
        }
        linearLayout.removeAllViews();
        if (!($r instanceof Throwable)) {
            List<Geocoder.Place> places = (List) $r;
            if (places.isEmpty()) {
                this$0.hint("No places found for “" + $q + "”.");
            } else {
                for (Geocoder.Place p : places) {
                    this$0.resultRow(p);
                }
            }
        }
        Throwable it = ($r instanceof Throwable) ? (Throwable) $r : null;
        if (it != null) {
            this$0.hint("Search failed (need Wi-Fi): " + it.getMessage());
        }
    }

    private final void resultRow(final Geocoder.Place p) {
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(0);
        c.setGravity(16);
        c.setBackgroundResource(R.drawable.card_solid);
        c.setPadding(dp(16), dp(14), dp(16), dp(14));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(dp(2), dp(4), dp(2), dp(4));
        c.setLayoutParams(lp);
        c.setClickable(true);
        c.setOnClickListener(new View.OnClickListener() { // from class: com.bike.computer.DestinationSearchActivity$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                DestinationSearchActivity.this.navigateTo(p);
            }
        });
        TextView t = new TextView(this);
        t.setText(p.getName());
        t.setTextColor(-1);
        t.setTextSize(15.0f);
        t.setMaxLines(2);
        t.setEllipsize(TextUtils.TruncateAt.END);
        t.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView go = new TextView(this);
        go.setText("›");
        go.setTextColor(Color.parseColor("#FF6E6E6E"));
        go.setTextSize(22.0f);
        c.addView(t);
        c.addView(go);
        LinearLayout linearLayout = this.results;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("results");
            linearLayout = null;
        }
        linearLayout.addView(c);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void navigateTo(Geocoder.Place p) {
        ActionBus.INSTANCE.setPendingDestination(new double[]{p.getLat(), p.getLon()});
        Toast.makeText(this, "Routing to " + StringsKt.substringBefore(p.getName(), ',', p.getName()) + "…", 0).show();
        startActivity(new Intent(this, (Class<?>) MainActivity.class));
        finish();
    }

    private final void hint(String s) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextColor(Color.parseColor("#FF9E9E9E"));
        t.setTextSize(15.0f);
        t.setPadding(dp(6), dp(8), dp(6), dp(8));
        LinearLayout linearLayout = this.results;
        if (linearLayout == null) {
            Intrinsics.throwUninitializedPropertyAccessException("results");
            linearLayout = null;
        }
        linearLayout.addView(t);
    }

    private final int dp(int v) {
        return (int) TypedValue.applyDimension(1, v, getResources().getDisplayMetrics());
    }
}

package com.bike.computer;

import android.location.Location;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;

/* JADX INFO: compiled from: RideRecorder.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000t\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\u001b\n\u0002\u0010\u0016\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0010\u0014\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u000e\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010A\u001a\u00020\r2\u0006\u0010B\u001a\u00020\rJ\u0006\u0010O\u001a\u00020PJ\u0006\u0010Q\u001a\u00020PJ\u0006\u0010V\u001a\u00020PJ;\u0010W\u001a\u00020P2\u0006\u0010X\u001a\u00020\u000b2\u0006\u0010B\u001a\u00020\r2\b\b\u0002\u0010Y\u001a\u00020\r2\b\b\u0002\u0010Z\u001a\u00020\r2\n\b\u0002\u0010[\u001a\u0004\u0018\u00010\u0011¢\u0006\u0002\u0010\\J\b\u0010]\u001a\u0004\u0018\u00010\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\r@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001e\u0010\u0012\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\u0011@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u001e\u0010\u0016\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\u0015@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u001e\u0010\u001a\u001a\u00020\u00192\u0006\u0010\f\u001a\u00020\u0019@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u000e\u0010\u001d\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010 \u001a\u00020\u001f2\u0006\u0010\f\u001a\u00020\u001f@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u001e\u0010#\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\u0011@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0014R\u001e\u0010%\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\u0011@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b&\u0010\u0014R\u000e\u0010'\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010*\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\r@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0010R\u001e\u0010,\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\r@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b-\u0010\u0010R\u000e\u0010.\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010/\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u00100\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\r@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b1\u0010\u0010R\u000e\u00102\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u00103\u001a\u00020\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u00104\u001a\u00020\r8F¢\u0006\u0006\u001a\u0004\b5\u0010\u0010R\u001a\u00106\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b7\u0010\u0010\"\u0004\b8\u00109R\u0011\u0010:\u001a\u00020;¢\u0006\b\n\u0000\u001a\u0004\b<\u0010=R\u0011\u0010>\u001a\u00020;¢\u0006\b\n\u0000\u001a\u0004\b?\u0010=R\u000e\u0010@\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R!\u0010C\u001a\u0012\u0012\u0004\u0012\u00020E0Dj\b\u0012\u0004\u0012\u00020E`F¢\u0006\b\n\u0000\u001a\u0004\bG\u0010HR\u000e\u0010I\u001a\u00020JX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010K\u001a\u00020JX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010L\u001a\u00020\u00198F¢\u0006\u0006\u001a\u0004\bL\u0010\u001cR\u0011\u0010M\u001a\u00020\u00158F¢\u0006\u0006\u001a\u0004\bN\u0010\u0018R\u0011\u0010R\u001a\u00020\u001f8F¢\u0006\u0006\u001a\u0004\bS\u0010\"R\u0011\u0010T\u001a\u00020\r8F¢\u0006\u0006\u001a\u0004\bU\u0010\u0010¨\u0006^"}, d2 = {"Lcom/bike/computer/RideRecorder;", "", "dir", "", "<init>", "(Ljava/lang/String;)V", "writer", "Ljava/io/Writer;", "file", "Ljava/io/File;", "last", "Landroid/location/Location;", "value", "", "points", "getPoints", "()I", "", "distanceM", "getDistanceM", "()D", "", "startMs", "getStartMs", "()J", "", "paused", "getPaused", "()Z", "pauseStartMs", "pausedAccumMs", "", "maxSpeedMps", "getMaxSpeedMps", "()F", "curEleM", "getCurEleM", "ascentM", "getAscentM", "eleSmoothed", "eleRef", "baroOffset", "maxCadence", "getMaxCadence", "hrMax", "getHrMax", "hrSum", "hrCount", "powerMax", "getPowerMax", "powerSum", "powerCount", "powerAvg", "getPowerAvg", "maxHrForZones", "getMaxHrForZones", "setMaxHrForZones", "(I)V", "zoneMs", "", "getZoneMs", "()[J", "hrHistMs", "getHrHistMs", "lastZoneTickMs", "zoneOf", "hr", "eleSamples", "Ljava/util/ArrayList;", "", "Lkotlin/collections/ArrayList;", "getEleSamples", "()Ljava/util/ArrayList;", "iso", "Ljava/text/SimpleDateFormat;", "fnameFmt", "isRecording", "elapsedMs", "getElapsedMs", "pause", "", "resume", "avgSpeedMps", "getAvgSpeedMps", "hrAvg", "getHrAvg", "start", "add", "loc", "power", "cadence", "baroAlt", "(Landroid/location/Location;IIILjava/lang/Double;)V", "stop", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class RideRecorder {
    private double ascentM;
    private double baroOffset;
    private double curEleM;
    private final String dir;
    private double distanceM;
    private double eleRef;
    private final ArrayList<float[]> eleSamples;
    private double eleSmoothed;
    private File file;
    private final SimpleDateFormat fnameFmt;
    private int hrCount;
    private final long[] hrHistMs;
    private int hrMax;
    private long hrSum;
    private final SimpleDateFormat iso;
    private Location last;
    private long lastZoneTickMs;
    private int maxCadence;
    private int maxHrForZones;
    private float maxSpeedMps;
    private long pauseStartMs;
    private boolean paused;
    private long pausedAccumMs;
    private int points;
    private int powerCount;
    private int powerMax;
    private long powerSum;
    private long startMs;
    private Writer writer;
    private final long[] zoneMs;

    public RideRecorder(String dir) {
        Intrinsics.checkNotNullParameter(dir, "dir");
        this.dir = dir;
        this.eleSmoothed = Double.NaN;
        this.eleRef = Double.NaN;
        this.baroOffset = Double.NaN;
        this.maxHrForZones = 185;
        this.zoneMs = new long[5];
        this.hrHistMs = new long[251];
        this.eleSamples = new ArrayList<>();
        SimpleDateFormat $this$iso_u24lambda_u240 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        $this$iso_u24lambda_u240.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.iso = $this$iso_u24lambda_u240;
        this.fnameFmt = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US);
    }

    public final int getPoints() {
        return this.points;
    }

    public final double getDistanceM() {
        return this.distanceM;
    }

    public final long getStartMs() {
        return this.startMs;
    }

    public final boolean getPaused() {
        return this.paused;
    }

    public final float getMaxSpeedMps() {
        return this.maxSpeedMps;
    }

    public final double getCurEleM() {
        return this.curEleM;
    }

    public final double getAscentM() {
        return this.ascentM;
    }

    public final int getMaxCadence() {
        return this.maxCadence;
    }

    public final int getHrMax() {
        return this.hrMax;
    }

    public final int getPowerMax() {
        return this.powerMax;
    }

    public final int getPowerAvg() {
        if (this.powerCount > 0) {
            return (int) (this.powerSum / ((long) this.powerCount));
        }
        return 0;
    }

    public final int getMaxHrForZones() {
        return this.maxHrForZones;
    }

    public final void setMaxHrForZones(int i) {
        this.maxHrForZones = i;
    }

    public final long[] getZoneMs() {
        return this.zoneMs;
    }

    public final long[] getHrHistMs() {
        return this.hrHistMs;
    }

    public final int zoneOf(int hr) {
        if (hr <= 0) {
            return -1;
        }
        double f = ((double) hr) / ((double) this.maxHrForZones);
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

    public final ArrayList<float[]> getEleSamples() {
        return this.eleSamples;
    }

    public final boolean isRecording() {
        return this.writer != null;
    }

    public final long getElapsedMs() {
        if (this.startMs == 0) {
            return 0L;
        }
        return ((System.currentTimeMillis() - this.startMs) - this.pausedAccumMs) - (this.paused ? System.currentTimeMillis() - this.pauseStartMs : 0L);
    }

    public final void pause() {
        if (!isRecording() || this.paused) {
            return;
        }
        this.paused = true;
        this.pauseStartMs = System.currentTimeMillis();
        this.lastZoneTickMs = 0L;
    }

    public final void resume() {
        if (isRecording() && this.paused) {
            this.pausedAccumMs += System.currentTimeMillis() - this.pauseStartMs;
            this.paused = false;
            this.lastZoneTickMs = 0L;
        }
    }

    public final float getAvgSpeedMps() {
        if (getElapsedMs() > 0) {
            return (float) (this.distanceM / (getElapsedMs() / 1000.0d));
        }
        return 0.0f;
    }

    public final int getHrAvg() {
        if (this.hrCount > 0) {
            return (int) (this.hrSum / ((long) this.hrCount));
        }
        return 0;
    }

    public final void start() throws IOException {
        new File(this.dir).mkdirs();
        this.startMs = System.currentTimeMillis();
        this.points = 0;
        this.distanceM = 0.0d;
        this.last = null;
        this.paused = false;
        this.pauseStartMs = 0L;
        this.pausedAccumMs = 0L;
        this.maxSpeedMps = 0.0f;
        this.curEleM = 0.0d;
        this.ascentM = 0.0d;
        this.eleSmoothed = Double.NaN;
        this.eleRef = Double.NaN;
        this.baroOffset = Double.NaN;
        this.maxCadence = 0;
        this.hrMax = 0;
        this.hrSum = 0L;
        this.hrCount = 0;
        this.powerMax = 0;
        this.powerSum = 0L;
        this.powerCount = 0;
        Arrays.fill(this.zoneMs, 0L);
        Arrays.fill(this.hrHistMs, 0L);
        this.lastZoneTickMs = 0L;
        this.eleSamples.clear();
        File f = new File(this.dir, "ride_" + this.fnameFmt.format(new Date(this.startMs)) + ".gpx");
        this.file = f;
        Writer outputStreamWriter = new OutputStreamWriter(new FileOutputStream(f), Charsets.UTF_8);
        BufferedWriter w = outputStreamWriter instanceof BufferedWriter ? (BufferedWriter) outputStreamWriter : new BufferedWriter(outputStreamWriter, 8192);
        this.writer = w;
        w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        w.write("<gpx version=\"1.1\" creator=\"BikeComputer\" xmlns=\"http://www.topografix.com/GPX/1/1\" xmlns:gpxtpx=\"http://www.garmin.com/xmlschemas/TrackPointExtension/v1\">\n");
        w.write("<trk><name>Ride " + this.iso.format(new Date(this.startMs)) + "</name><trkseg>\n");
        w.flush();
    }

    public static /* synthetic */ void add$default(RideRecorder rideRecorder, Location location, int i, int i2, int i3, Double d, int i4, Object obj) throws IOException {
        int i5 = (i4 & 4) != 0 ? 0 : i2;
        int i6 = (i4 & 8) != 0 ? 0 : i3;
        if ((i4 & 16) != 0) {
            d = null;
        }
        rideRecorder.add(location, i, i5, i6, d);
    }

    public final void add(Location loc, int hr, int power, int cadence, Double baroAlt) throws IOException {
        Intrinsics.checkNotNullParameter(loc, "loc");
        Writer w = this.writer;
        if (w == null || this.paused) {
            return;
        }
        if (power > 0) {
            if (power > this.powerMax) {
                this.powerMax = power;
            }
            this.powerSum += (long) power;
            this.powerCount++;
        }
        if (cadence > 0 && cadence > this.maxCadence) {
            this.maxCadence = cadence;
        }
        Location it = this.last;
        if (it != null) {
            this.distanceM += (double) it.distanceTo(loc);
        }
        this.last = loc;
        this.points++;
        float spd = loc.hasSpeed() ? loc.getSpeed() : 0.0f;
        if (spd > this.maxSpeedMps) {
            this.maxSpeedMps = spd;
        }
        Double alt = null;
        Double gpsAlt = loc.hasAltitude() ? Double.valueOf(loc.getAltitude()) : null;
        if (baroAlt != null) {
            if (gpsAlt != null) {
                this.baroOffset = Double.isNaN(this.baroOffset) ? gpsAlt.doubleValue() - baroAlt.doubleValue() : (this.baroOffset * 0.995d) + ((gpsAlt.doubleValue() - baroAlt.doubleValue()) * 0.005d);
            }
            alt = Double.valueOf(baroAlt.doubleValue() + (Double.isNaN(this.baroOffset) ? 0.0d : this.baroOffset));
        } else if (gpsAlt != null) {
            alt = gpsAlt;
        }
        if (alt != null) {
            this.eleSmoothed = Double.isNaN(this.eleSmoothed) ? alt.doubleValue() : (this.eleSmoothed * 0.8d) + (alt.doubleValue() * 0.2d);
            this.curEleM = this.eleSmoothed;
            double thresh = baroAlt != null ? 1.5d : 3.0d;
            if (Double.isNaN(this.eleRef)) {
                this.eleRef = this.eleSmoothed;
            }
            if (this.eleSmoothed - this.eleRef > thresh) {
                this.ascentM += this.eleSmoothed - this.eleRef;
                this.eleRef = this.eleSmoothed;
            } else if (this.eleSmoothed < this.eleRef) {
                this.eleRef = this.eleSmoothed;
            }
            if (this.eleSamples.isEmpty() || this.distanceM - ((double) ((float[]) CollectionsKt.last((List) this.eleSamples))[0]) > 15.0d) {
                this.eleSamples.add(new float[]{(float) this.distanceM, (float) this.eleSmoothed});
            }
        }
        if (hr > 0) {
            if (hr > this.hrMax) {
                this.hrMax = hr;
            }
            this.hrSum += (long) hr;
            this.hrCount++;
            long now = System.currentTimeMillis();
            if (this.lastZoneTickMs > 0) {
                long dt = RangesKt.coerceIn(now - this.lastZoneTickMs, 0L, 5000L);
                int z = zoneOf(hr);
                if (z >= 0) {
                    long[] jArr = this.zoneMs;
                    jArr[z] = jArr[z] + dt;
                }
                if (hr >= 0 && hr < 251) {
                    long[] jArr2 = this.hrHistMs;
                    jArr2[hr] = jArr2[hr] + dt;
                }
            }
            this.lastZoneTickMs = now;
        }
        long t = loc.getTime() > 0 ? loc.getTime() : System.currentTimeMillis();
        StringBuilder sb = new StringBuilder(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        sb.append("<trkpt lat=\"").append(loc.getLatitude()).append("\" lon=\"").append(loc.getLongitude()).append("\">");
        if (alt != null) {
            StringBuilder sbAppend = sb.append("<ele>");
            StringCompanionObject stringCompanionObject = StringCompanionObject.INSTANCE;
            String str = String.format(Locale.US, "%.1f", Arrays.copyOf(new Object[]{Double.valueOf(this.eleSmoothed)}, 1));
            Intrinsics.checkNotNullExpressionValue(str, "format(...)");
            sbAppend.append(str).append("</ele>");
        }
        sb.append("<time>").append(this.iso.format(new Date(t))).append("</time>");
        if (hr > 0 || power > 0 || cadence > 0) {
            sb.append("<extensions>");
            if (power > 0) {
                sb.append("<power>").append(power).append("</power>");
            }
            if (hr > 0 || cadence > 0) {
                sb.append("<gpxtpx:TrackPointExtension>");
                if (hr > 0) {
                    sb.append("<gpxtpx:hr>").append(hr).append("</gpxtpx:hr>");
                }
                if (cadence > 0) {
                    sb.append("<gpxtpx:cad>").append(cadence).append("</gpxtpx:cad>");
                }
                sb.append("</gpxtpx:TrackPointExtension>");
            }
            sb.append("</extensions>");
        }
        sb.append("</trkpt>\n");
        w.write(sb.toString());
        w.flush();
    }

    public final String stop() throws IOException {
        Writer w = this.writer;
        if (w == null) {
            return null;
        }
        w.write("</trkseg></trk></gpx>\n");
        w.flush();
        w.close();
        this.writer = null;
        File file = this.file;
        if (file != null) {
            return file.getAbsolutePath();
        }
        return null;
    }
}

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
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.StringCompanionObject;
import kotlin.ranges.RangesKt;
import kotlin.text.Charsets;

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

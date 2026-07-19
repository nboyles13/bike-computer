package com.bike.computer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: HrSensor.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\b\u0003\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0000*\u000214\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u0012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\u0004\b\u000b\u0010\fJ\u0006\u0010&\u001a\u00020\u0007J\b\u0010'\u001a\u00020\u0007H\u0002J\u0010\u0010(\u001a\u00020\u00072\u0006\u0010)\u001a\u00020*H\u0002J\u0012\u0010+\u001a\u00020,2\b\u0010-\u001a\u0004\u0018\u00010\nH\u0002J\u0006\u0010.\u001a\u00020\u0007J\u0010\u00106\u001a\u00020\u00072\u0006\u00107\u001a\u000208H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\r\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0011\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0012\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0013\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0014\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0015\u001a\n \u000f*\u0004\u0018\u00010\u00160\u0016X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0017R\u0016\u0010\u0018\u001a\u0004\u0018\u00010\u00198BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\nX\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020!X\u0082\u0004¢\u0006\u0002\n\u0000R\"\u0010#\u001a\u0004\u0018\u00010\n2\b\u0010\"\u001a\u0004\u0018\u00010\n@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b$\u0010%R\u000e\u0010/\u001a\u00020,X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u00100\u001a\u000201X\u0082\u0004¢\u0006\u0004\n\u0002\u00102R\u0010\u00103\u001a\u000204X\u0082\u0004¢\u0006\u0004\n\u0002\u00105¨\u00069"}, d2 = {"Lcom/bike/computer/HrSensor;", "", "ctx", "Landroid/content/Context;", "onHr", "Lkotlin/Function1;", "", "", "onBattery", "onStatus", "", "<init>", "(Landroid/content/Context;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "HR_SVC", "Ljava/util/UUID;", "kotlin.jvm.PlatformType", "Ljava/util/UUID;", "HR_CHR", "BAT_SVC", "BAT_CHR", "CCCD", "adapter", "Landroid/bluetooth/BluetoothAdapter;", "Landroid/bluetooth/BluetoothAdapter;", "scanner", "Landroid/bluetooth/le/BluetoothLeScanner;", "getScanner", "()Landroid/bluetooth/le/BluetoothLeScanner;", "gatt", "Landroid/bluetooth/BluetoothGatt;", "TAG", "seenCount", "handler", "Landroid/os/Handler;", "value", "deviceName", "getDeviceName", "()Ljava/lang/String;", "start", "startScan", "removeBond", "dev", "Landroid/bluetooth/BluetoothDevice;", "nameLooksHr", "", "n", "stop", "connecting", "scanCb", "com/bike/computer/HrSensor$scanCb$1", "Lcom/bike/computer/HrSensor$scanCb$1;", "gattCb", "com/bike/computer/HrSensor$gattCb$1", "Lcom/bike/computer/HrSensor$gattCb$1;", "parseHr", "v", "", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class HrSensor {
    private final UUID BAT_CHR;
    private final UUID BAT_SVC;
    private final UUID CCCD;
    private final UUID HR_CHR;
    private final UUID HR_SVC;
    private final String TAG;
    private final BluetoothAdapter adapter;
    private boolean connecting;
    private final Context ctx;
    private String deviceName;
    private BluetoothGatt gatt;
    private final BluetoothGattCallback gattCb;
    private final Handler handler;
    private final Function1<? super Integer, Unit> onBattery;
    private final Function1<? super Integer, Unit> onHr;
    private final Function1<? super String, Unit> onStatus;
    private final ScanCallback scanCb;
    private int seenCount;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v20, types: [com.bike.computer.HrSensor$scanCb$1] */
    /* JADX WARN: Type inference failed for: r0v21, types: [com.bike.computer.HrSensor$gattCb$1] */
    public HrSensor(Context ctx, Function1<? super Integer, Unit> onHr, Function1<? super Integer, Unit> onBattery, Function1<? super String, Unit> onStatus) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(onHr, "onHr");
        Intrinsics.checkNotNullParameter(onBattery, "onBattery");
        Intrinsics.checkNotNullParameter(onStatus, "onStatus");
        this.ctx = ctx;
        this.onHr = onHr;
        this.onBattery = onBattery;
        this.onStatus = onStatus;
        this.HR_SVC = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb");
        this.HR_CHR = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb");
        this.BAT_SVC = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
        this.BAT_CHR = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
        this.CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        Object systemService = this.ctx.getSystemService("bluetooth");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.bluetooth.BluetoothManager");
        this.adapter = ((BluetoothManager) systemService).getAdapter();
        this.TAG = "BikeHr";
        this.handler = new Handler(Looper.getMainLooper());
        this.scanCb = new ScanCallback() { // from class: com.bike.computer.HrSensor$scanCb$1
            /* JADX WARN: Removed duplicated region for block: B:30:0x006b  */
            @Override // android.bluetooth.le.ScanCallback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onScanResult(int callbackType, ScanResult result) {
                String name;
                boolean hasHrSvc;
                Iterable serviceUuids;
                boolean z;
                Intrinsics.checkNotNullParameter(result, "result");
                if (HrSensor.this.connecting) {
                    return;
                }
                ScanRecord rec = result.getScanRecord();
                if ((rec == null || (name = rec.getDeviceName()) == null) && (name = result.getDevice().getName()) == null) {
                    name = "";
                }
                if (rec == null || (serviceUuids = rec.getServiceUuids()) == null) {
                    hasHrSvc = false;
                } else {
                    Iterable $this$any$iv = serviceUuids;
                    HrSensor hrSensor = HrSensor.this;
                    if (($this$any$iv instanceof Collection) && ((Collection) $this$any$iv).isEmpty()) {
                        z = false;
                    } else {
                        Iterator it = $this$any$iv.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                Object element$iv = it.next();
                                ParcelUuid it2 = (ParcelUuid) element$iv;
                                if (Intrinsics.areEqual(it2.getUuid(), hrSensor.HR_SVC)) {
                                    z = true;
                                    break;
                                }
                            } else {
                                z = false;
                                break;
                            }
                        }
                    }
                    hasHrSvc = z;
                }
                boolean looksHr = StringsKt.contains((CharSequence) name, (CharSequence) "polar", true) || StringsKt.contains((CharSequence) name, (CharSequence) "heart", true) || StringsKt.contains((CharSequence) name, (CharSequence) "hr", true) || StringsKt.contains((CharSequence) name, (CharSequence) "tickr", true);
                HrSensor.this.seenCount++;
                if (HrSensor.this.seenCount <= 60 || hasHrSvc || looksHr) {
                    Log.i(HrSensor.this.TAG, "scan #" + HrSensor.this.seenCount + " " + result.getDevice().getAddress() + " name=\"" + name + "\" hrSvc=" + hasHrSvc + " uuids=" + (rec != null ? rec.getServiceUuids() : null));
                }
                if (hasHrSvc || looksHr) {
                    HrSensor.this.connecting = true;
                    HrSensor hrSensor2 = HrSensor.this;
                    String address = name;
                    if (StringsKt.isBlank(address)) {
                        address = result.getDevice().getAddress();
                    }
                    hrSensor2.deviceName = address;
                    BluetoothLeScanner scanner = HrSensor.this.getScanner();
                    if (scanner != null) {
                        scanner.stopScan(this);
                    }
                    Log.i(HrSensor.this.TAG, "MATCH -> connecting " + result.getDevice().getAddress());
                    HrSensor.this.onStatus.invoke("connecting " + result.getDevice().getAddress());
                    HrSensor.this.gatt = result.getDevice().connectGatt(HrSensor.this.ctx, false, HrSensor.this.gattCb, 2);
                }
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onScanFailed(int errorCode) {
                Log.i(HrSensor.this.TAG, "onScanFailed " + errorCode);
                HrSensor.this.onStatus.invoke("scan fail " + errorCode);
            }
        };
        this.gattCb = new BluetoothGattCallback() { // from class: com.bike.computer.HrSensor$gattCb$1
            @Override // android.bluetooth.BluetoothGattCallback
            public void onConnectionStateChange(BluetoothGatt g, int status, int newState) {
                Intrinsics.checkNotNullParameter(g, "g");
                Log.i(HrSensor.this.TAG, "connState status=" + status + " newState=" + newState);
                switch (newState) {
                    case 0:
                        HrSensor.this.onStatus.invoke("disconnected");
                        break;
                    case 2:
                        HrSensor.this.onStatus.invoke("connected");
                        g.discoverServices();
                        break;
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onServicesDiscovered(BluetoothGatt g, int status) {
                Intrinsics.checkNotNullParameter(g, "g");
                Log.i(HrSensor.this.TAG, "servicesDiscovered status=" + status + " svcCount=" + g.getServices().size());
                BluetoothGattService service = g.getService(HrSensor.this.HR_SVC);
                BluetoothGattCharacteristic chr = service != null ? service.getCharacteristic(HrSensor.this.HR_CHR) : null;
                if (chr == null) {
                    HrSensor.this.onStatus.invoke("no HR char");
                    return;
                }
                g.setCharacteristicNotification(chr, true);
                BluetoothGattDescriptor cccd = chr.getDescriptor(HrSensor.this.CCCD);
                if (cccd == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= 33) {
                    g.writeDescriptor(cccd, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else {
                    cccd.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    g.writeDescriptor(cccd);
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onDescriptorWrite(BluetoothGatt g, BluetoothGattDescriptor d, int status) {
                BluetoothGattCharacteristic it;
                Intrinsics.checkNotNullParameter(g, "g");
                Intrinsics.checkNotNullParameter(d, "d");
                HrSensor.this.onStatus.invoke("live");
                BluetoothGattService service = g.getService(HrSensor.this.BAT_SVC);
                if (service != null && (it = service.getCharacteristic(HrSensor.this.BAT_CHR)) != null) {
                    g.readCharacteristic(it);
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicChanged(BluetoothGatt g, BluetoothGattCharacteristic c, byte[] value) {
                Intrinsics.checkNotNullParameter(g, "g");
                Intrinsics.checkNotNullParameter(c, "c");
                Intrinsics.checkNotNullParameter(value, "value");
                if (Intrinsics.areEqual(c.getUuid(), HrSensor.this.HR_CHR)) {
                    HrSensor.this.parseHr(value);
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            @Deprecated(message = "Deprecated in Java")
            public void onCharacteristicChanged(BluetoothGatt g, BluetoothGattCharacteristic c) {
                byte[] it;
                Intrinsics.checkNotNullParameter(g, "g");
                Intrinsics.checkNotNullParameter(c, "c");
                if (Intrinsics.areEqual(c.getUuid(), HrSensor.this.HR_CHR) && (it = c.getValue()) != null) {
                    HrSensor.this.parseHr(it);
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicRead(BluetoothGatt g, BluetoothGattCharacteristic c, byte[] value, int status) {
                Intrinsics.checkNotNullParameter(g, "g");
                Intrinsics.checkNotNullParameter(c, "c");
                Intrinsics.checkNotNullParameter(value, "value");
                if (Intrinsics.areEqual(c.getUuid(), HrSensor.this.BAT_CHR)) {
                    if (!(value.length == 0)) {
                        HrSensor.this.onBattery.invoke(Integer.valueOf(value[0] & UByte.MAX_VALUE));
                    }
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            @Deprecated(message = "Deprecated in Java")
            public void onCharacteristicRead(BluetoothGatt g, BluetoothGattCharacteristic c, int status) {
                byte[] it;
                Intrinsics.checkNotNullParameter(g, "g");
                Intrinsics.checkNotNullParameter(c, "c");
                if (Intrinsics.areEqual(c.getUuid(), HrSensor.this.BAT_CHR) && (it = c.getValue()) != null) {
                    HrSensor hrSensor = HrSensor.this;
                    if (!(it.length == 0)) {
                        hrSensor.onBattery.invoke(Integer.valueOf(it[0] & UByte.MAX_VALUE));
                    }
                }
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final BluetoothLeScanner getScanner() {
        BluetoothAdapter bluetoothAdapter = this.adapter;
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.getBluetoothLeScanner();
        }
        return null;
    }

    public final String getDeviceName() {
        return this.deviceName;
    }

    public final void start() {
        Collection destination$iv$iv;
        String str = this.TAG;
        BluetoothAdapter bluetoothAdapter = this.adapter;
        BluetoothAdapter bluetoothAdapter2 = this.adapter;
        Iterable bondedDevices = null;
        Log.i(str, "start() adapter=" + bluetoothAdapter + " enabled=" + (bluetoothAdapter2 != null ? Boolean.valueOf(bluetoothAdapter2.isEnabled()) : null) + " scanner=" + getScanner());
        if (this.adapter == null || !this.adapter.isEnabled()) {
            this.onStatus.invoke("BT off");
            return;
        }
        try {
            bondedDevices = this.adapter.getBondedDevices();
        } catch (Exception e) {
        }
        if (bondedDevices != null) {
            Iterable $this$filterTo$iv$iv = bondedDevices;
            Collection destination$iv$iv2 = new ArrayList();
            for (Object element$iv$iv : $this$filterTo$iv$iv) {
                if (nameLooksHr(((BluetoothDevice) element$iv$iv).getName())) {
                    destination$iv$iv2.add(element$iv$iv);
                }
            }
            destination$iv$iv = (List) destination$iv$iv2;
        } else {
            destination$iv$iv = CollectionsKt.emptyList();
        }
        Collection bondedHr = destination$iv$iv;
        if (bondedHr.isEmpty()) {
            startScan();
            return;
        }
        Collection $this$forEach$iv = bondedHr;
        for (Object element$iv : $this$forEach$iv) {
            BluetoothDevice it = (BluetoothDevice) element$iv;
            Intrinsics.checkNotNull(it);
            removeBond(it);
        }
        this.onStatus.invoke("clearing bond");
        this.handler.postDelayed(new Runnable() { // from class: com.bike.computer.HrSensor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HrSensor.this.startScan();
            }
        }, 1500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void startScan() {
        this.onStatus.invoke("scanning");
        ScanSettings settings = new ScanSettings.Builder().setScanMode(2).build();
        BluetoothLeScanner scanner = getScanner();
        if (scanner != null) {
            scanner.startScan((List<ScanFilter>) null, settings, this.scanCb);
        }
    }

    private final void removeBond(BluetoothDevice dev) {
        try {
            Object r = dev.getClass().getMethod("removeBond", new Class[0]).invoke(dev, new Object[0]);
            Log.i(this.TAG, "removeBond " + dev.getAddress() + " -> " + r);
        } catch (Exception e) {
            Log.i(this.TAG, "removeBond failed: " + e);
        }
    }

    private final boolean nameLooksHr(String n) {
        if (n == null) {
            return false;
        }
        return StringsKt.contains((CharSequence) n, (CharSequence) "polar", true) || StringsKt.contains((CharSequence) n, (CharSequence) "heart", true) || StringsKt.contains((CharSequence) n, (CharSequence) "hr", true) || StringsKt.contains((CharSequence) n, (CharSequence) "tickr", true) || StringsKt.contains((CharSequence) n, (CharSequence) "wahoo", true);
    }

    public final void stop() {
        try {
            BluetoothLeScanner scanner = getScanner();
            if (scanner != null) {
                scanner.stopScan(this.scanCb);
            }
        } catch (Exception e) {
        }
        try {
            BluetoothGatt bluetoothGatt = this.gatt;
            if (bluetoothGatt != null) {
                bluetoothGatt.disconnect();
            }
            BluetoothGatt bluetoothGatt2 = this.gatt;
            if (bluetoothGatt2 != null) {
                bluetoothGatt2.close();
            }
        } catch (Exception e2) {
        }
        this.gatt = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void parseHr(byte[] v) {
        int hr;
        if (v.length == 0) {
            return;
        }
        int flags = v[0];
        if ((flags & 1) == 0) {
            hr = v[1] & UByte.MAX_VALUE;
        } else {
            hr = ((v[2] & UByte.MAX_VALUE) << 8) | (v[1] & UByte.MAX_VALUE);
        }
        Log.i(this.TAG, "HR=" + hr);
        this.onHr.invoke(Integer.valueOf(hr));
    }
}

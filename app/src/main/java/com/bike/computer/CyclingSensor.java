package com.bike.computer;

import android.bluetooth.BluetoothAdapter;
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
import android.os.ParcelUuid;
import android.util.Log;
import androidx.recyclerview.widget.ItemTouchHelper;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: CyclingSensor.kt */
/* JADX INFO: loaded from: classes3.dex */
@Metadata(d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\b\u0003\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0002\b\n*\u0002-0\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u0012\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\u0004\b\u000b\u0010\fJ\u001d\u0010\u0015\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000e2\u0006\u0010\u0016\u001a\u00020\nH\u0002¢\u0006\u0002\u0010\u0017J\u0006\u0010*\u001a\u00020\u0007J\u0006\u0010+\u001a\u00020\u0007J\u0018\u00102\u001a\u00020\u00072\u0006\u00103\u001a\u00020\u000e2\u0006\u00104\u001a\u000205H\u0002J\u0018\u00106\u001a\u00020\u00062\u0006\u00104\u001a\u0002052\u0006\u00107\u001a\u00020\u0006H\u0002J\u0018\u00108\u001a\u00020\u00062\u0006\u00104\u001a\u0002052\u0006\u00107\u001a\u00020\u0006H\u0002J\u0018\u00109\u001a\u00020\u00062\u0006\u00104\u001a\u0002052\u0006\u00107\u001a\u00020\u0006H\u0002J\u0010\u0010:\u001a\u00020\u00072\u0006\u00104\u001a\u000205H\u0002J\u0010\u0010;\u001a\u00020\u00072\u0006\u00104\u001a\u000205H\u0002J\u0018\u0010<\u001a\u00020\u00072\u0006\u0010=\u001a\u00020\u00062\u0006\u0010>\u001a\u00020\u0006H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\r\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0011\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0012\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0013\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0014\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000eX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010R\u0018\u0010\u0018\u001a\n \u000f*\u0004\u0018\u00010\u00190\u0019X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u001aR\u0016\u0010\u001b\u001a\u0004\u0018\u00010\u001c8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001eR\u0010\u0010\u001f\u001a\u0004\u0018\u00010 X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\"X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\nX\u0082D¢\u0006\u0002\n\u0000R\"\u0010%\u001a\u0004\u0018\u00010\n2\b\u0010$\u001a\u0004\u0018\u00010\n@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b&\u0010'R\u000e\u0010(\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010)\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010,\u001a\u00020-X\u0082\u0004¢\u0006\u0004\n\u0002\u0010.R\u0010\u0010/\u001a\u000200X\u0082\u0004¢\u0006\u0004\n\u0002\u00101¨\u0006?"}, d2 = {"Lcom/bike/computer/CyclingSensor;", "", "ctx", "Landroid/content/Context;", "onPower", "Lkotlin/Function1;", "", "", "onCadence", "onStatus", "", "<init>", "(Landroid/content/Context;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "CPS_SVC", "Ljava/util/UUID;", "kotlin.jvm.PlatformType", "Ljava/util/UUID;", "CPM_CHR", "CSC_SVC", "CSC_CHR", "CCCD", "uuid", "s", "(Ljava/lang/String;)Ljava/util/UUID;", "adapter", "Landroid/bluetooth/BluetoothAdapter;", "Landroid/bluetooth/BluetoothAdapter;", "scanner", "Landroid/bluetooth/le/BluetoothLeScanner;", "getScanner", "()Landroid/bluetooth/le/BluetoothLeScanner;", "gatt", "Landroid/bluetooth/BluetoothGatt;", "connecting", "", "TAG", "value", "deviceName", "getDeviceName", "()Ljava/lang/String;", "lastCrankRevs", "lastCrankTime", "start", "stop", "scanCb", "com/bike/computer/CyclingSensor$scanCb$1", "Lcom/bike/computer/CyclingSensor$scanCb$1;", "gattCb", "com/bike/computer/CyclingSensor$gattCb$1", "Lcom/bike/computer/CyclingSensor$gattCb$1;", "handle", "u", "v", "", "u8", "i", "u16", "s16", "parseCpm", "parseCsc", "cadenceFromCrank", "revs", "time1024", "app_debug"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class CyclingSensor {
    private final UUID CCCD;
    private final UUID CPM_CHR;
    private final UUID CPS_SVC;
    private final UUID CSC_CHR;
    private final UUID CSC_SVC;
    private final String TAG;
    private final BluetoothAdapter adapter;
    private boolean connecting;
    private final Context ctx;
    private String deviceName;
    private BluetoothGatt gatt;
    private final BluetoothGattCallback gattCb;
    private int lastCrankRevs;
    private int lastCrankTime;
    private final Function1<Integer, Unit> onCadence;
    private final Function1<Integer, Unit> onPower;
    private final Function1<String, Unit> onStatus;
    private final ScanCallback scanCb;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v20, types: [com.bike.computer.CyclingSensor$scanCb$1] */
    /* JADX WARN: Type inference failed for: r0v21, types: [com.bike.computer.CyclingSensor$gattCb$1] */
    public CyclingSensor(Context ctx, Function1<Integer, Unit> onPower, Function1<Integer, Unit> onCadence, Function1<String, Unit> onStatus) {
        Intrinsics.checkNotNullParameter(ctx, "ctx");
        Intrinsics.checkNotNullParameter(onPower, "onPower");
        Intrinsics.checkNotNullParameter(onCadence, "onCadence");
        Intrinsics.checkNotNullParameter(onStatus, "onStatus");
        this.ctx = ctx;
        this.onPower = onPower;
        this.onCadence = onCadence;
        this.onStatus = onStatus;
        this.CPS_SVC = uuid("00001818");
        this.CPM_CHR = uuid("00002a63");
        this.CSC_SVC = uuid("00001816");
        this.CSC_CHR = uuid("00002a5b");
        this.CCCD = uuid("00002902");
        Object systemService = this.ctx.getSystemService("bluetooth");
        Intrinsics.checkNotNull(systemService, "null cannot be cast to non-null type android.bluetooth.BluetoothManager");
        this.adapter = ((BluetoothManager) systemService).getAdapter();
        this.TAG = "BikeCyc";
        this.lastCrankRevs = -1;
        this.lastCrankTime = -1;
        this.scanCb = new ScanCallback() { // from class: com.bike.computer.CyclingSensor$scanCb$1
            /* JADX WARN: Removed duplicated region for block: B:36:0x007f  */
            @Override // android.bluetooth.le.ScanCallback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
            */
            public void onScanResult(int callbackType, ScanResult result) {
                String name;
                boolean hasSvc = false;
                Iterable serviceUuids;
                boolean z;
                Intrinsics.checkNotNullParameter(result, "result");
                if (CyclingSensor.this.connecting) {
                    return;
                }
                ScanRecord rec = result.getScanRecord();
                if ((rec == null || (name = rec.getDeviceName()) == null) && (name = result.getDevice().getName()) == null) {
                    name = "";
                }
                if (rec == null || (serviceUuids = rec.getServiceUuids()) == null) {
                    hasSvc = false;
                } else {
                    Iterable $this$any$iv = serviceUuids;
                    CyclingSensor cyclingSensor = CyclingSensor.this;
                    if (($this$any$iv instanceof Collection) && ((Collection) $this$any$iv).isEmpty()) {
                        z = false;
                    } else {
                        Iterator it = $this$any$iv.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                Object element$iv = it.next();
                                ParcelUuid it2 = (ParcelUuid) element$iv;
                                if (Intrinsics.areEqual(it2.getUuid(), cyclingSensor.CPS_SVC) || Intrinsics.areEqual(it2.getUuid(), cyclingSensor.CSC_SVC)) {
                                    z = true;
                                    break;
                                }
                            } else {
                                z = false;
                                break;
                            }
                        }
                    }
                    if (z) {
                        hasSvc = true;
                    }
                }
                boolean looks = StringsKt.contains((CharSequence) name, (CharSequence) "power", true) || StringsKt.contains((CharSequence) name, (CharSequence) "cadence", true) || StringsKt.contains((CharSequence) name, (CharSequence) "stages", true) || StringsKt.contains((CharSequence) name, (CharSequence) "assioma", true) || StringsKt.contains((CharSequence) name, (CharSequence) "4iiii", true);
                if (hasSvc || looks) {
                    CyclingSensor.this.connecting = true;
                    CyclingSensor cyclingSensor2 = CyclingSensor.this;
                    String address = name;
                    if (StringsKt.isBlank(address)) {
                        address = result.getDevice().getAddress();
                    }
                    cyclingSensor2.deviceName = address;
                    BluetoothLeScanner scanner = CyclingSensor.this.getScanner();
                    if (scanner != null) {
                        scanner.stopScan(this);
                    }
                    CyclingSensor.this.onStatus.invoke("connecting " + result.getDevice().getAddress());
                    try {
                        CyclingSensor.this.gatt = result.getDevice().connectGatt(CyclingSensor.this.ctx, false, CyclingSensor.this.gattCb, 2);
                    } catch (SecurityException e) {
                        CyclingSensor.this.onStatus.invoke("no BT permission");
                    }
                }
            }

            @Override // android.bluetooth.le.ScanCallback
            public void onScanFailed(int errorCode) {
                CyclingSensor.this.onStatus.invoke("scan fail " + errorCode);
            }
        };
        this.gattCb = new BluetoothGattCallback() { // from class: com.bike.computer.CyclingSensor$gattCb$1
            @Override // android.bluetooth.BluetoothGattCallback
            public void onConnectionStateChange(BluetoothGatt g, int status, int newState) {
                Intrinsics.checkNotNullParameter(g, "g");
                switch (newState) {
                    case 0:
                        CyclingSensor.this.onStatus.invoke("disconnected");
                        break;
                    case 2:
                        g.discoverServices();
                        break;
                }
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onServicesDiscovered(BluetoothGatt g, int status) {
                BluetoothGattCharacteristic chr;
                Intrinsics.checkNotNullParameter(g, "g");
                BluetoothGattService service = g.getService(CyclingSensor.this.CPS_SVC);
                if (service == null || (chr = service.getCharacteristic(CyclingSensor.this.CPM_CHR)) == null) {
                    BluetoothGattService service2 = g.getService(CyclingSensor.this.CSC_SVC);
                    chr = service2 != null ? service2.getCharacteristic(CyclingSensor.this.CSC_CHR) : null;
                }
                if (chr == null) {
                    CyclingSensor.this.onStatus.invoke("no power/cadence char");
                    return;
                }
                g.setCharacteristicNotification(chr, true);
                BluetoothGattDescriptor cccd = chr.getDescriptor(CyclingSensor.this.CCCD);
                if (cccd == null) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= 33) {
                    g.writeDescriptor(cccd, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                } else {
                    cccd.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    g.writeDescriptor(cccd);
                }
                CyclingSensor.this.onStatus.invoke("live");
            }

            @Override // android.bluetooth.BluetoothGattCallback
            public void onCharacteristicChanged(BluetoothGatt g, BluetoothGattCharacteristic c, byte[] value) {
                Intrinsics.checkNotNullParameter(g, "g");
                Intrinsics.checkNotNullParameter(c, "c");
                Intrinsics.checkNotNullParameter(value, "value");
                CyclingSensor cyclingSensor = CyclingSensor.this;
                UUID uuid = c.getUuid();
                Intrinsics.checkNotNullExpressionValue(uuid, "getUuid(...)");
                cyclingSensor.handle(uuid, value);
            }

            @Override // android.bluetooth.BluetoothGattCallback
            @Deprecated(message = "Deprecated in Java")
            public void onCharacteristicChanged(BluetoothGatt g, BluetoothGattCharacteristic c) {
                Intrinsics.checkNotNullParameter(g, "g");
                Intrinsics.checkNotNullParameter(c, "c");
                byte[] it = c.getValue();
                if (it != null) {
                    CyclingSensor cyclingSensor = CyclingSensor.this;
                    UUID uuid = c.getUuid();
                    Intrinsics.checkNotNullExpressionValue(uuid, "getUuid(...)");
                    cyclingSensor.handle(uuid, it);
                }
            }
        };
    }

    private final UUID uuid(String s) {
        return UUID.fromString(s + "-0000-1000-8000-00805f9b34fb");
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
        if (this.adapter == null || !this.adapter.isEnabled()) {
            this.onStatus.invoke("BT off");
            return;
        }
        if (!Ble.canScan(this.ctx)) {
            this.onStatus.invoke("no BT permission");
            return;
        }
        ScanSettings settings = new ScanSettings.Builder().setScanMode(2).build();
        BluetoothLeScanner scanner = getScanner();
        if (scanner != null) {
            try {
                scanner.startScan((List<ScanFilter>) null, settings, this.scanCb);
            } catch (SecurityException e) {
                this.onStatus.invoke("no BT permission");
            }
        }
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
    public final void handle(UUID u, byte[] v) {
        try {
            if (Intrinsics.areEqual(u, this.CPM_CHR)) {
                parseCpm(v);
            } else if (Intrinsics.areEqual(u, this.CSC_CHR)) {
                parseCsc(v);
            }
        } catch (Exception e) {
            Log.w(this.TAG, "parse: " + e);
        }
    }

    private final int u8(byte[] v, int i) {
        return v[i] & UByte.MAX_VALUE;
    }

    private final int u16(byte[] v, int i) {
        return u8(v, i) | (u8(v, i + 1) << 8);
    }

    private final int s16(byte[] v, int i) {
        int it = u16(v, i);
        return it >= 32768 ? it - 65536 : it;
    }

    private final void parseCpm(byte[] v) {
        if (v.length < 4) {
            return;
        }
        int flags = u16(v, 0);
        this.onPower.invoke(Integer.valueOf(RangesKt.coerceAtLeast(s16(v, 2), 0)));
        int i = (flags & 1) != 0 ? 4 + 1 : 4;
        if ((flags & 4) != 0) {
            i += 2;
        }
        if ((flags & 16) != 0) {
            i += 6;
        }
        if ((flags & 32) != 0 && v.length >= i + 4) {
            cadenceFromCrank(u16(v, i), u16(v, i + 2));
        }
    }

    private final void parseCsc(byte[] v) {
        if (v.length == 0) {
            return;
        }
        int flags = u8(v, 0);
        int i = (flags & 1) != 0 ? 1 + 6 : 1;
        if ((flags & 2) != 0 && v.length >= i + 4) {
            cadenceFromCrank(u16(v, i), u16(v, i + 2));
        }
    }

    private final void cadenceFromCrank(int revs, int time1024) {
        if (this.lastCrankRevs >= 0 && time1024 != this.lastCrankTime) {
            int dRev = ((revs - this.lastCrankRevs) + 65536) % 65536;
            int dT = ((time1024 - this.lastCrankTime) + 65536) % 65536;
            if (dT > 0) {
                this.onCadence.invoke(Integer.valueOf(RangesKt.coerceIn((int) Math.round(((((double) dRev) * 1024.0d) * 60.0d) / ((double) dT)), 0, ItemTouchHelper.Callback.DEFAULT_SWIPE_ANIMATION_DURATION)));
            }
        }
        this.lastCrankRevs = revs;
        this.lastCrankTime = time1024;
    }
}

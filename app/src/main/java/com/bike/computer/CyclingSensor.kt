package com.bike.computer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.util.Log
import java.util.UUID
import kotlin.math.roundToInt

/** Scans for and connects a BLE power / speed-cadence sensor, streaming power + cadence. */
class CyclingSensor(
    private val ctx: Context,
    private val onPower: (Int) -> Unit,
    private val onCadence: (Int) -> Unit,
    private val onStatus: (String) -> Unit,
) {
    private val CPS_SVC = uuid("00001818")
    private val CPM_CHR = uuid("00002a63")
    private val CSC_SVC = uuid("00001816")
    private val CSC_CHR = uuid("00002a5b")
    private val CCCD = uuid("00002902")
    private val TAG = "BikeCyc"
    private val adapter: BluetoothAdapter? =
        (ctx.getSystemService("bluetooth") as BluetoothManager).adapter

    private var connecting = false
    private var gatt: BluetoothGatt? = null
    private var lastCrankRevs = -1
    private var lastCrankTime = -1

    var deviceName: String? = null
        private set

    private fun uuid(s: String): UUID = UUID.fromString("$s-0000-1000-8000-00805f9b34fb")

    private val scanner: BluetoothLeScanner?
        get() = adapter?.bluetoothLeScanner

    fun start() {
        if (adapter == null || !adapter.isEnabled) {
            onStatus("BT off")
            return
        }
        if (!Ble.canScan(ctx)) {
            onStatus("no BT permission")
            return
        }
        val settings = ScanSettings.Builder().setScanMode(2).build()
        val scanner = scanner ?: return
        try {
            scanner.startScan(null, settings, scanCb)
        } catch (e: SecurityException) {
            onStatus("no BT permission")
        }
    }

    fun stop() {
        try {
            scanner?.stopScan(scanCb)
        } catch (e: Exception) {
        }
        try {
            gatt?.disconnect()
            gatt?.close()
        } catch (e: Exception) {
        }
        gatt = null
    }

    private fun handle(u: UUID, v: ByteArray) {
        try {
            when (u) {
                CPM_CHR -> parseCpm(v)
                CSC_CHR -> parseCsc(v)
            }
        } catch (e: Exception) {
            Log.w(TAG, "parse: $e")
        }
    }

    private fun u8(v: ByteArray, i: Int): Int = v[i].toInt() and 0xFF
    private fun u16(v: ByteArray, i: Int): Int = u8(v, i) or (u8(v, i + 1) shl 8)
    private fun s16(v: ByteArray, i: Int): Int {
        val it = u16(v, i)
        return if (it >= 32768) it - 65536 else it
    }

    private fun parseCpm(v: ByteArray) {
        if (v.size < 4) return
        val flags = u16(v, 0)
        onPower(s16(v, 2).coerceAtLeast(0))
        var i = if (flags and 1 != 0) 4 + 1 else 4
        if (flags and 4 != 0) i += 2
        if (flags and 16 != 0) i += 6
        if (flags and 32 != 0 && v.size >= i + 4) cadenceFromCrank(u16(v, i), u16(v, i + 2))
    }

    private fun parseCsc(v: ByteArray) {
        if (v.isEmpty()) return
        val flags = u8(v, 0)
        val i = if (flags and 1 != 0) 1 + 6 else 1
        if (flags and 2 != 0 && v.size >= i + 4) cadenceFromCrank(u16(v, i), u16(v, i + 2))
    }

    private fun cadenceFromCrank(revs: Int, time1024: Int) {
        if (lastCrankRevs >= 0 && time1024 != lastCrankTime) {
            val dRev = ((revs - lastCrankRevs) + 65536) % 65536
            val dT = ((time1024 - lastCrankTime) + 65536) % 65536
            if (dT > 0) {
                onCadence((dRev * 1024.0 * 60.0 / dT).roundToInt().coerceIn(0, 250))
            }
        }
        lastCrankRevs = revs
        lastCrankTime = time1024
    }

    private val scanCb = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (connecting) return
            val rec = result.scanRecord
            val name = rec?.deviceName ?: result.device.name ?: ""
            val hasSvc = rec?.serviceUuids?.any { it.uuid == CPS_SVC || it.uuid == CSC_SVC } ?: false
            val looks = name.contains("power", true) || name.contains("cadence", true) ||
                name.contains("stages", true) || name.contains("assioma", true) ||
                name.contains("4iiii", true)
            if (hasSvc || looks) {
                connecting = true
                deviceName = name.ifBlank { result.device.address }
                scanner?.stopScan(this)
                onStatus("connecting ${result.device.address}")
                try {
                    gatt = result.device.connectGatt(ctx, false, gattCb, 2)
                } catch (e: SecurityException) {
                    onStatus("no BT permission")
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            onStatus("scan fail $errorCode")
        }
    }

    private val gattCb = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                0 -> onStatus("disconnected")
                2 -> g.discoverServices()
            }
        }

        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            var chr = g.getService(CPS_SVC)?.getCharacteristic(CPM_CHR)
            if (chr == null) {
                chr = g.getService(CSC_SVC)?.getCharacteristic(CSC_CHR)
            }
            if (chr == null) {
                onStatus("no power/cadence char")
                return
            }
            g.setCharacteristicNotification(chr, true)
            val cccd = chr.getDescriptor(CCCD) ?: return
            if (Build.VERSION.SDK_INT >= 33) {
                g.writeDescriptor(cccd, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
            } else {
                @Suppress("DEPRECATION")
                cccd.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                @Suppress("DEPRECATION")
                g.writeDescriptor(cccd)
            }
            onStatus("live")
        }

        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic, value: ByteArray) {
            handle(c.uuid, value)
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic) {
            @Suppress("DEPRECATION")
            val it = c.value
            if (it != null) handle(c.uuid, it)
        }
    }
}

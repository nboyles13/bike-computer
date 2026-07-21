package com.bike.computer

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
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
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.UUID

/** Scans for and connects a BLE heart-rate strap, streaming HR + battery via callbacks. */
class HrSensor(
    private val ctx: Context,
    private val onHr: (Int) -> Unit,
    private val onBattery: (Int) -> Unit,
    private val onStatus: (String) -> Unit,
) {
    private val HR_SVC = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    private val HR_CHR = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    private val BAT_SVC = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb")
    private val BAT_CHR = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
    private val CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    private val TAG = "BikeHr"
    private val adapter: BluetoothAdapter? =
        (ctx.getSystemService("bluetooth") as BluetoothManager).adapter
    private val handler = Handler(Looper.getMainLooper())

    private var connecting = false
    private var gatt: BluetoothGatt? = null
    private var seenCount = 0

    var deviceName: String? = null
        private set

    private val scanner: BluetoothLeScanner?
        get() = adapter?.bluetoothLeScanner

    fun start() {
        Log.i(TAG, "start() adapter=$adapter enabled=${adapter?.isEnabled} scanner=$scanner")
        if (adapter == null || !adapter.isEnabled) {
            onStatus("BT off")
            return
        }
        val bonded = try {
            adapter.bondedDevices
        } catch (e: Exception) {
            null
        }
        val bondedHr = bonded?.filter { nameLooksHr(it.name) } ?: emptyList()
        if (bondedHr.isEmpty()) {
            startScan()
            return
        }
        for (it in bondedHr) removeBond(it)
        onStatus("clearing bond")
        handler.postDelayed({ startScan() }, 1500L)
    }

    private fun startScan() {
        if (!Ble.canScan(ctx)) {
            onStatus("no BT permission")
            return
        }
        onStatus("scanning")
        val settings = ScanSettings.Builder().setScanMode(2).build()
        val scanner = scanner ?: return
        try {
            scanner.startScan(null, settings, scanCb)
        } catch (e: SecurityException) {
            onStatus("no BT permission")
        }
    }

    private fun removeBond(dev: BluetoothDevice) {
        try {
            val r = dev.javaClass.getMethod("removeBond").invoke(dev)
            Log.i(TAG, "removeBond ${dev.address} -> $r")
        } catch (e: Exception) {
            Log.i(TAG, "removeBond failed: $e")
        }
    }

    private fun nameLooksHr(n: String?): Boolean {
        if (n == null) return false
        return n.contains("polar", true) || n.contains("heart", true) ||
            n.contains("hr", true) || n.contains("tickr", true) || n.contains("wahoo", true)
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

    private fun parseHr(v: ByteArray) {
        if (v.isEmpty()) return
        val flags = v[0].toInt()
        val hr = if (flags and 1 == 0) {
            v[1].toInt() and 0xFF
        } else {
            ((v[2].toInt() and 0xFF) shl 8) or (v[1].toInt() and 0xFF)
        }
        Log.i(TAG, "HR=$hr")
        onHr(hr)
    }

    private val scanCb = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (connecting) return
            val rec = result.scanRecord
            val name = rec?.deviceName ?: result.device.name ?: ""
            val hasHrSvc = rec?.serviceUuids?.any { it.uuid == HR_SVC } ?: false
            val looksHr = name.contains("polar", true) || name.contains("heart", true) ||
                name.contains("hr", true) || name.contains("tickr", true)
            seenCount++
            if (seenCount <= 60 || hasHrSvc || looksHr) {
                Log.i(
                    TAG,
                    "scan #$seenCount ${result.device.address} name=\"$name\" hrSvc=$hasHrSvc uuids=${rec?.serviceUuids}",
                )
            }
            if (hasHrSvc || looksHr) {
                connecting = true
                deviceName = name.ifBlank { result.device.address }
                scanner?.stopScan(this)
                Log.i(TAG, "MATCH -> connecting ${result.device.address}")
                onStatus("connecting ${result.device.address}")
                try {
                    gatt = result.device.connectGatt(ctx, false, gattCb, 2)
                } catch (e: SecurityException) {
                    onStatus("no BT permission")
                }
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.i(TAG, "onScanFailed $errorCode")
            onStatus("scan fail $errorCode")
        }
    }

    private val gattCb = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            Log.i(TAG, "connState status=$status newState=$newState")
            when (newState) {
                0 -> onStatus("disconnected")
                2 -> {
                    onStatus("connected")
                    g.discoverServices()
                }
            }
        }

        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            Log.i(TAG, "servicesDiscovered status=$status svcCount=${g.services.size}")
            val chr = g.getService(HR_SVC)?.getCharacteristic(HR_CHR)
            if (chr == null) {
                onStatus("no HR char")
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
        }

        override fun onDescriptorWrite(g: BluetoothGatt, d: BluetoothGattDescriptor, status: Int) {
            onStatus("live")
            val it = g.getService(BAT_SVC)?.getCharacteristic(BAT_CHR)
            if (it != null) g.readCharacteristic(it)
        }

        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic, value: ByteArray) {
            if (c.uuid == HR_CHR) parseHr(value)
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic) {
            @Suppress("DEPRECATION")
            val it = c.value
            if (c.uuid == HR_CHR && it != null) parseHr(it)
        }

        override fun onCharacteristicRead(g: BluetoothGatt, c: BluetoothGattCharacteristic, value: ByteArray, status: Int) {
            if (c.uuid == BAT_CHR && value.isNotEmpty()) onBattery(value[0].toInt() and 0xFF)
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(g: BluetoothGatt, c: BluetoothGattCharacteristic, status: Int) {
            @Suppress("DEPRECATION")
            val it = c.value
            if (c.uuid == BAT_CHR && it != null && it.isNotEmpty()) onBattery(it[0].toInt() and 0xFF)
        }
    }
}

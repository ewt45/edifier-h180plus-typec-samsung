package org.ewt45.edifier

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
import androidx.core.content.IntentCompat

private const val TAG = "UsbHelper"

class UsbHelper(private val context: Context) {

    companion object {
        val usbDeviceStatus: MutableState<String> = mutableStateOf("No USB device connected")
    }

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    val permissionIntent = createUsbPermissionIntent()

    private val usbReceiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, intent: Intent?) {
            intent ?: return
            handleIntent(intent)
        }
    }

    fun register() {
        val filter = IntentFilter().apply {
//            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
//            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addAction(ACTION_USB_PERMISSION)
        }
        ContextCompat.registerReceiver(
            context,
            usbReceiver,
            filter,
            RECEIVER_NOT_EXPORTED
        ) // Use Context.RECEIVER_EXPORTED for Android 12+
        Log.i(TAG, "USB Receiver registered")
    }

    fun unregister() {
        try {
            context.unregisterReceiver(usbReceiver)
            Log.i(TAG, "USB Receiver unregistered")
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "USB Receiver not registered or already unregistered: ${e.message}")
        }
    }

    /**
     * @throws UsbConnectException 处理失败时
     */
    fun handleIntent(intent: Intent) {
        val device: UsbDevice =
            IntentCompat.getParcelableExtra(intent, UsbManager.EXTRA_DEVICE, UsbDevice::class.java)
                ?: return
        when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                // 在device_filter.xml中设置筛选edfier h180 plus, 这里不再校验
                // 根据官方文档，通过intent-filter获取的设备自动获得权限
                Log.d(TAG, "handleIntent: 检测到typec耳机连接")
                usbDeviceStatus.value =
                    "USB device attached (intent): ${device.deviceName} (${device.vendorId}:${device.productId})"
                //FIXME service中注册广播好像不会收到？
                if (!usbManager.hasPermission(device))
                    usbManager.requestPermission(device, permissionIntent)
                else
                    connectUsbDevice(device)
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                device.let {
                    Log.i(TAG, "USB device detached: ${it.deviceName}")
                    usbDeviceStatus.value = "USB device detached: ${it.deviceName}"
                }
                throw UsbConnectException("usb设备断开连接")
            }
            //检测到设备接入后 申请权限
            ACTION_USB_PERMISSION -> {
                synchronized(this) {
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        connectUsbDevice(device)
                    } else {
                        Log.w(TAG, "Permission denied for device ${device.deviceName}")
                    }
                }
            }
        }
    }

    /**
     * 检测到usb连接，确保有权限后，连接设备
     */
    private fun connectUsbDevice(device: UsbDevice) {
        usbManager.openDevice(device).let {
//            if (it == null) throw Exception("无法连接设备")
//            // 发送数据参考edifier connect
//            val bytes: ByteArray = intArrayOf(0xaa, 0xec, 0xd8, 0x00, 0x00, 0x6e).map { v -> v.toByte() }.toByteArray()
//            Log.d(TAG, "connectUsbDevice: 发送数据 ${bytes.toReadableString()}")
//            var ret: Int
//            val buffer = ByteArray(128)
//            ret = it.controlTransfer(64, 6, 0, 0, bytes, bytes.size, 4000)
//            ret = it.controlTransfer(192, 12, 0, 0, buffer, buffer.size, 0);
//            if (ret < 0)
//                ret = it.controlTransfer(192, 12, 0, 0, buffer, buffer.size, 0);
//            if (ret < 0)
//                return
//            Log.d(TAG, "connectUsbDevice: 接收数据 ${buffer.copyOf(ret).toReadableString()}")
        }

    }

    private fun createUsbPermissionIntent(): PendingIntent {
        return PendingIntent.getBroadcast(
            context, 0,
            Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
        )
    }

}


package org.ewt45.edifier.tool

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.PowerManager
import androidx.core.app.NotificationManagerCompat


class Utils {

}

fun ByteArray.toReadableString(): String = this.joinToString(separator = " ") { String.format("%02x", it) }

/** 检查自身是否拥有权限 */
fun checkPermission(ctx: Context, permission: String): Boolean = ctx.checkSelfPermission(permission) == PERMISSION_GRANTED

fun checkNotifyPermission(ctx: Context): Boolean = NotificationManagerCompat.from(ctx).areNotificationsEnabled()

fun checkIsIgnoringBatteryOptimizations(ctx: Context): Boolean  = ctx.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(ctx.packageName)

/** 寻找H180plus耳机设备 */
fun filterUsbDevice(ctx: Context): UsbDevice? = ctx.getSystemService(UsbManager::class.java).deviceList.values
    .firstNotNullOfOrNull { if (it.vendorId == 11673 && it.productId == 40998) it else null }
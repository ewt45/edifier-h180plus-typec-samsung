package org.ewt45.edifier.tool

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.PowerManager
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import org.ewt45.edifier.MainService


class Utils {

}

fun ByteArray.toReadableString(): String = this.joinToString(separator = " ") { String.format("%02x", it) }

/** 检查自身是否拥有权限 */
fun checkPermission(ctx: Context, permission: String): Boolean = ctx.checkSelfPermission(permission) == PERMISSION_GRANTED

fun checkNotifyPermission(ctx: Context): Boolean = NotificationManagerCompat.from(ctx).areNotificationsEnabled()

fun checkIsIgnoringBatteryOptimizations(ctx: Context): Boolean  = ctx.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(ctx.packageName)

/** 寻找H180plus耳机设备. 如果在已连接的usb设备中未找到，返回null */
fun filterUsbDevice(ctx: Context): UsbDevice? = ctx.getSystemService(UsbManager::class.java).deviceList.values
    .firstNotNullOfOrNull { if (it.vendorId == 11673 && it.productId == 40998) it else null }

/** 返回布尔值表示当前MainService是否正在运行 */
fun findMainService(ctx: Context): Boolean {
    val activityManager = ctx.getSystemService(ActivityManager::class.java)
    val service = activityManager.getRunningServices(Int.MAX_VALUE).firstNotNullOfOrNull { if (it.service.className == MainService::class.qualifiedName) it else null }
    return service != null
}

fun intentOfAppSettings(ctx: Context)  = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = "package:${ctx.packageName}".toUri() }
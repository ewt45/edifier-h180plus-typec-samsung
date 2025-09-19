package org.ewt45.edifier.tool

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.PowerManager
import androidx.core.app.NotificationManagerCompat


class Utils {

}

fun ByteArray.toReadableString(): String = this.joinToString(separator = " ") { String.format("%02x", it) }

/** 检查自身是否拥有权限 */
fun checkPermission(ctx: Context, permission: String): Boolean = ctx.checkSelfPermission(permission) == PERMISSION_GRANTED

fun checkNotifyPermission(ctx: Context): Boolean = NotificationManagerCompat.from(ctx).areNotificationsEnabled()

fun checkIsIgnoringBatteryOptimizations(ctx: Context): Boolean  = ctx.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(ctx.packageName)
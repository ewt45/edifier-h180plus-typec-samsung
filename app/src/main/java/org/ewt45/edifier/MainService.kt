package org.ewt45.edifier

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.IntentCompat
import org.ewt45.edifier_180plustypec_samsung.R

class UsbConnectException(message: String) : Exception(message)

private const val TAG = "MainService"
private const val CHANNEL_ID = "MainServiceChannel"
private const val NOTIFICATION_ID = 1

class MainService : Service() {
    private lateinit var usbHelper: UsbHelper

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        usbHelper = UsbHelper(applicationContext)
        usbHelper.register()
        createNotificationChannel()
    }


    override fun onDestroy() {
        super.onDestroy()
        usbHelper.unregister()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 如果没有设备连接，不作处理，直接结束service
        val usbIntent = intent?.let { IntentCompat.getParcelableExtra(it, EXTRA_USB_DEVICE_CONNECTION, Intent::class.java) }
        if (usbIntent == null) {
            Log.w(TAG, "onStartCommand: Received null usbIntent, stopping service.")
            stopSelf(startId)
            return START_NOT_STICKY
        }

        // 尝试处理
        try {
            usbHelper.handleIntent(usbIntent)
            startForeground()
            Log.i(TAG, "前台服务正在运行")
        } catch (e: Exception) {
            Log.e(TAG, "onStartCommand: 服务处理intent时出错", e)
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    @SuppressLint("InlinedApi")
    private fun startForeground() = ServiceCompat.startForeground(
        this, NOTIFICATION_ID, createNotification(),
        ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
    )

    private fun createNotificationChannel() {
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Main Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("Edifier Control Active")
        .setContentText("Connected to USB device")
        .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app's icon
        .build()
}

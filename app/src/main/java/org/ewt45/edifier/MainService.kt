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

const val ACTION_SERVICE_CREATED = "org.ewt45.edifier.SERVICE_CREATED"
const val ACTION_SERVICE_DESTROYED = "org.ewt45.edifier.SERVICE_DESTROYED"

private const val TAG = "MainService"
private const val CHANNEL_ID = "MainServiceChannel"
private const val NOTIFICATION_ID = 1

class MainService : Service() {
    private lateinit var usbHelper: UsbHelper
    private lateinit var notification: Notification

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: MainService创建")
        usbHelper = UsbHelper(applicationContext)
        usbHelper.register()
        createNotificationChannel()
        notification = createNotification()
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
            startForeground() // !!这个要放这里才行，放开头无条件运行就会系统卡死!!
            Log.i(TAG, "前台服务正在运行")
        } catch (e: Exception) {
            Log.e(TAG, "onStartCommand: 服务处理intent时出错", e)
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    @SuppressLint("InlinedApi")
    private fun startForeground() = ServiceCompat.startForeground(
        this, NOTIFICATION_ID, notification,
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
        .setContentTitle("漫步者Typec耳机")
        .setContentText("已连接，播放音频不会发出滴滴声")
        .setSmallIcon(R.mipmap.ic_launcher) // Replace with your app's icon
        .build()
}

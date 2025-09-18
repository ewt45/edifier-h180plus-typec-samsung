package org.ewt45.edifier

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity

const val EXTRA_USB_DEVICE_CONNECTION = "org.ewt45.edifier.USB_DEVICE_CONNECTION"

private const val TAG = "TransitionActivity"
class TransitionActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: 1")
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: 2")

        //TODO 禁用电池优化后是否可以启动前台服务（不禁用是否可以启动？）
        startForegroundService(Intent(this, MainService::class.java).apply {
            putExtra(EXTRA_USB_DEVICE_CONNECTION, intent)
        })
        Log.d(TAG, "onCreate: 3")

        finish()
        Log.d(TAG, "onCreate: 4")

    }
}
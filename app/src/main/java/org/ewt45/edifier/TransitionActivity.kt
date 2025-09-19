package org.ewt45.edifier

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity

const val EXTRA_USB_DEVICE_CONNECTION = "org.ewt45.edifier.USB_DEVICE_CONNECTION"

private const val TAG = "TransitionActivity"
class TransitionActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: 过渡活动启动")
        super.onCreate(savedInstanceState)
        //TODO 禁用电池优化后是否可以启动前台服务（不禁用是否可以启动？）
        startForegroundService(Intent(this, MainService::class.java)
            .apply { putExtra(EXTRA_USB_DEVICE_CONNECTION, intent) })
        finish()
    }

    companion object {
        /**
         * 手动启动服务，启动后检查是否有连接的设备
         */
        fun startAndCheckManually(ctx: Context) {
            ctx.startActivity(Intent().apply {
                action = ACTION_CHECK_USB_MANUALLY
                component = ComponentName(ctx, TransitionActivity::class.java)
            })
        }
    }
}
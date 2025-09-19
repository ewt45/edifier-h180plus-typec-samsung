package org.ewt45.edifier.ui

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ewt45.edifier.ui.theme.Edifier180plustypecsamsungTheme
import androidx.core.net.toUri
import org.ewt45.edifier.MainService
import org.ewt45.edifier.TransitionActivity
import org.ewt45.edifier.tool.checkIsIgnoringBatteryOptimizations
import org.ewt45.edifier.tool.checkNotifyPermission
import org.ewt45.edifier.tool.checkPermission
import org.ewt45.edifier.ui.theme.GreenText
import org.ewt45.edifier.ui.theme.RedText


@Composable
fun MainScreen(message: String, modifier: Modifier = Modifier) {
    val TAG = "MainScreen"
    val ctx = LocalContext.current

    // 录音权限
    var isAudioable by remember { mutableStateOf(checkPermission(ctx, RECORD_AUDIO)) }
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())
    { isGranted: Boolean -> isAudioable = isGranted }

    // 通知权限
    var isNotifiable by remember { mutableStateOf(checkNotifyPermission(ctx)) }
    // 安卓13以上可以用这个。还是用通用的吧
//    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())  { isGranted: Boolean -> isNotifiable = isGranted }
    val notifyLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult())
    { isNotifiable = checkNotifyPermission(ctx) }

    // 电池优化
    var isIgnoreBatteryOpt by remember { mutableStateOf(checkIsIgnoringBatteryOptimizations(ctx)) }
    val ignoreBatteryOptLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult())
    { isIgnoreBatteryOpt = checkIsIgnoringBatteryOptimizations(ctx) }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message)

        Spacer(modifier = Modifier.height(24.dp))

        Row {
            Button(onClick = { TransitionActivity.startAndCheckManually(ctx) })
            { Text("手动开启") }

            Spacer(Modifier.width(16.dp))

            Button(onClick = { ctx.stopService(Intent(ctx, MainService::class.java)) })
            { Text("手动关闭") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 录音权限
        Text("录音权限: " + if (isAudioable) "已授予" else "未授予", color = if (isAudioable) GreenText else RedText)
        Button(onClick = { if (!isAudioable) audioLauncher.launch(RECORD_AUDIO) })
        { Text("点击授权") }

        Spacer(modifier = Modifier.height(16.dp))

        // 通知权限
        Text("通知权限: " + if (isNotifiable) "已授予" else "未授予", color = if (isNotifiable) GreenText else RedText)
        Button(onClick = {
            if (!isNotifiable) notifyLauncher.launch(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = "package:${ctx.packageName}".toUri() })
//                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        })
        { Text(if (isNotifiable) "通知权限已授予" else "申请通知权限") }

        Spacer(modifier = Modifier.height(16.dp))

        // 电池优化
        Text("关闭电池优化: " + if (isIgnoreBatteryOpt) "已关闭" else "未关闭", color = if (isIgnoreBatteryOpt) GreenText else RedText)
        Text("提示1：点击按钮后，通过左上角搜索快速定位到app，然后取消勾选。\n提示2: 如果打开的是应用信息界面，点击“电池”，选择“不受限制”。", fontSize = 14.sp, textAlign = TextAlign.Center)
        Button(onClick = {
            val intent = Intent().apply {
                action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
//                data = "package:${ctx.packageName}".toUri() //加了这个反而无法启动
            }
            try {
                ignoreBatteryOptLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e(TAG, "MainScreen: ", e)
                ctx.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply { data = "package:${ctx.packageName}".toUri() })
            }
        }) {
            Text("关闭电池优化")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar() {
    TopAppBar(
        title = { Text("设置") },
    )
}

@Preview(widthDp = 320, heightDp = 640)
@Composable
fun MainTopBarPreview() {
    MainTopBar()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Edifier180plustypecsamsungTheme {
        MainScreen("No USB device connected (Preview)")
    }
}

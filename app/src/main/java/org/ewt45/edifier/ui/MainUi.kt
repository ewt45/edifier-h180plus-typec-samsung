package org.ewt45.edifier.ui

import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ewt45.edifier.ui.theme.Edifier180plustypecsamsungTheme
import org.ewt45.edifier.MainService
import org.ewt45.edifier.TransitionActivity
import org.ewt45.edifier.tool.checkIsIgnoringBatteryOptimizations
import org.ewt45.edifier.tool.checkNotifyPermission
import org.ewt45.edifier.tool.checkPermission
import org.ewt45.edifier.tool.filterUsbDevice
import org.ewt45.edifier.tool.findMainService
import org.ewt45.edifier.tool.intentOfAppSettings
import org.ewt45.edifier.ui.theme.GreenText
import org.ewt45.edifier.ui.theme.RedText


@Composable
fun MainScreen(modifier: Modifier = Modifier) {
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
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                DeviceInfoCard()
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                PermissionCard(
                    isAudioable, isNotifiable, isIgnoreBatteryOpt,
                    onClickAudio = { if (!isAudioable) audioLauncher.launch(RECORD_AUDIO) },
                    // 这个是安卓13以上特有，不如用通用界面 notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    onClickNotify = { if (!isNotifiable) notifyLauncher.launch(intentOfAppSettings(ctx)) },
                    onClickIgnoreBatteryOpt = {
                        try {
                            if (!isIgnoreBatteryOpt) ignoreBatteryOptLauncher.launch(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS))
//                            intent.data = "package:${ctx.packageName}".toUri() //加了这个反而无法启动
                        } catch (e: Exception) {
                            Log.e(TAG, "MainScreen: ", e)
                            ctx.startActivity(intentOfAppSettings(ctx))
                        }
                    }
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    """
                说明:
                - 需要录音权限跳过确认弹窗，否则每次连接耳机时需要手动点击弹窗确认。${"\n"}
                - 需要通知权限显示通知，否则无法持续运行服务。${"\n"}
                - 需要关闭电池优化，否则连接耳机时可能无法自动启动。${"\n"}
                - 在应用设置界面取消勾选“在应用程序未使用时移除权限”，否则授予的权限可能会被收回。${"\n"}
                - 一般情况下，插入耳机后会自动运行一个前台服务，期间有短暂的0.几秒卡顿。如果服务中断（播放又出现滴滴声），请点击此页面的“手动打开服务”按钮。
                """.trimIndent()
                )
            }
        }

    }
}

@Composable
fun DeviceInfoCard() {
    val ctx = LocalContext.current
    var isDevFound by remember { mutableStateOf(filterUsbDevice(ctx) != null) }
    var isServiceFound by remember { mutableStateOf(findMainService(ctx)) }

    Row(verticalAlignment = Alignment.CenterVertically){
        Text((if(isDevFound) "已检测到耳机设备" else "未检测到耳机设备") + (if (isServiceFound) "\n服务正在运行" else "\n服务未运行"))

        Spacer(Modifier.width(16.dp))

        IconButton(onClick = {
            isDevFound = filterUsbDevice(ctx) != null
            isServiceFound = findMainService(ctx)
        })
        {Icon(Icons.Default.Refresh, null)}
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row {
        Button(onClick = { TransitionActivity.startAndCheckManually(ctx) })
        { Text("手动开启服务") }

        Spacer(Modifier.width(16.dp))

        Button(onClick = { ctx.stopService(Intent(ctx, MainService::class.java)) })
        { Text("手动关闭服务") }
    }
}

@Composable
fun PermissionCard(
    isAudioable: Boolean,
    isNotifiable: Boolean,
    isIgnoreBatteryOpt: Boolean,
    onClickAudio: () -> Unit,
    onClickNotify: () -> Unit,
    onClickIgnoreBatteryOpt: () -> Unit
) {
    // 录音权限
    Text("录音权限: " + if (isAudioable) "已授予" else "未授予", color = if (isAudioable) GreenText else RedText)
    Button(onClick = onClickAudio)
    { Text("点击授权") }

    Spacer(modifier = Modifier.height(16.dp))

    // 通知权限
    Text("通知权限: " + if (isNotifiable) "已授予" else "未授予", color = if (isNotifiable) GreenText else RedText)
    Button(onClick = onClickNotify)
    { Text(if (isNotifiable) "通知权限已授予" else "申请通知权限") }

    Spacer(modifier = Modifier.height(16.dp))

    // 电池优化
    Text("关闭电池优化: " + if (isIgnoreBatteryOpt) "已关闭" else "未关闭", color = if (isIgnoreBatteryOpt) GreenText else RedText)
    Text("提示1：点击按钮后，通过右上角搜索快速定位到app，然后取消勾选。\n提示2: 如果打开的是应用信息界面，点击“电池”，选择“不受限制”。", fontSize = 14.sp, lineHeight = 16.sp)
    Button(onClick = onClickIgnoreBatteryOpt) {
        Text("关闭电池优化")
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
        MainScreen()
    }
}

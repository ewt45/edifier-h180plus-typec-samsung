package org.ewt45.edifier.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import org.ewt45.edifier.ui.theme.Edifier180plustypecsamsungTheme

@Composable
fun MainScreen(name: String, modifier: Modifier = Modifier) {
    AudioRecordingPermissionRequester()

    Text(
        text = name, // Display USB status or original greeting
        modifier = modifier
    )
}


@Composable
fun AudioRecordingPermissionRequester() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasPermission = isGranted
        if (isGranted) {
            // 权限已授予，您可以开始录音了
            // TODO: 在此处添加您的录音逻辑或更新UI状态
            println("录音权限已授予")
        } else {
            // 权限被拒绝。
            // 您可能需要向用户解释为什么需要此权限，
            // 或者禁用需要此权限的功能。
            // TODO: 处理权限被拒绝的情况
            println("录音权限被拒绝")
        }
    }

    // 在 Composable 首次组合时检查并请求权限
    LaunchedEffect(Unit) { // Unit 作为 key 表示此 LaunchedEffect 只在首次组合时运行
        if (!hasPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

//    // UI部分可以根据需要保留或修改
//    Column {
//        Text(if (hasPermission) "录音权限已授予" else "录音权限尚未授予")
//
//        if (hasPermission) {
//            Button(onClick = {
//                // 执行录音相关操作
//                println("开始录音操作...")
//            }) {
//                Text("开始录音")
//            }
//        } else {
//            Button(onClick = {
//                // 如果用户想再次尝试，可以提供一个按钮
//                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
//            }) {
//                Text("再次请求录音权限")
//            }
//        }
//    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Edifier180plustypecsamsungTheme {
        // Preview with a default status as UsbHelper won't be active in preview
        MainScreen("No USB device connected (Preview)")
    }
}
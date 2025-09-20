package org.ewt45.edifier

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import org.ewt45.edifier.ui.theme.Edifier180plustypecsamsungTheme
import org.ewt45.edifier.ui.MainScreen
import org.ewt45.edifier.ui.MainTopBar

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate: ")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Edifier180plustypecsamsungTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { MainTopBar() },
                ) { innerPadding ->
                    MainScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }

//        // Handle intent if activity is launched by USB device attachment
//        intent?.let { usbHelper.handleIntent(it) }
    }

//    override fun onNewIntent(intent: Intent) {
//        super.onNewIntent(intent)
//        // Handle intent if activity is already running and a USB device is attached
//        intent.let { usbHelper.handleIntent(it) }
//    }
}


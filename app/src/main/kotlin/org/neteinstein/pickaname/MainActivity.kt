package org.neteinstein.pickaname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import org.neteinstein.pickaname.presentation.navigation.PickANameNavHost
import org.neteinstein.pickaname.presentation.theme.PickANameTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must run before super.onCreate()/setContentView so the system splash theme takes
        // effect immediately at cold start, before Compose has drawn its first frame.
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PickANameTheme {
                PickANameNavHost()
            }
        }
    }
}

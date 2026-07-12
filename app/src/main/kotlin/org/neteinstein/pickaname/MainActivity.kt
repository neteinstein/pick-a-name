package org.neteinstein.pickaname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.neteinstein.pickaname.presentation.navigation.PickANameNavHost
import org.neteinstein.pickaname.presentation.theme.PickANameTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PickANameTheme {
                PickANameNavHost()
            }
        }
    }
}

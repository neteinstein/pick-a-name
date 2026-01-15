package org.neteinstein.pickaname

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import org.neteinstein.pickaname.presentation.navigation.PickANameNavigation
import org.neteinstein.pickaname.presentation.theme.PickANameTheme

/**
 * Main activity for the Pick-A-Name app
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PickANameTheme {
                PickANameNavigation()
            }
        }
    }
}

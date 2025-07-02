package io.tigranes.app_two

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.tigranes.app_two.ui.navigation.PhotoFilterNavigation
import io.tigranes.app_two.ui.theme.App_twoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App_twoTheme {
                PhotoFilterNavigation()
            }
        }
    }
}
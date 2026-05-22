package com.example.wishlistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.wishlistapp.ui.navigation.WishNavHost
import com.example.wishlistapp.ui.settings.SettingsViewModel
import com.example.wishlistapp.ui.theme.WishListAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity serves as the entry point of the application.
 * It follows a single-activity architecture, delegating UI rendering to Compose 
 * and navigation to the WishNavHost.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val isDarkTheme by settingsViewModel.isDarkTheme.collectAsState()

            WishListAppTheme(darkTheme = isDarkTheme) {
                // Fixed: Calling the centralized WishNavHost from the navigation package
                WishNavHost()
            }
        }
    }
}

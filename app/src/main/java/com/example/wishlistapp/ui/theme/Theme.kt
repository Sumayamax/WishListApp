package com.example.wishlistapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPink,
    onPrimary = TextGray,
    primaryContainer = LightPink,
    onPrimaryContainer = TextGray,
    secondary = AccentLavender,
    onSecondary = TextGray,
    background = BackgroundPink,
    surface = BackgroundPink,
    onBackground = TextGray,
    onSurface = TextGray,
)

@Composable
fun WishListAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For this specific app concept, we'll focus on the Light Theme 
    // to maintain the "Soft Feminine" aesthetic as requested.
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

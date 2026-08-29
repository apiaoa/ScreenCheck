package com.screencheck.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// The app intentionally uses one dark scheme on every device,
// regardless of the system light/dark setting.
private val DarkScheme = darkColorScheme(
    primary = Color(0xFF5B9BFF),
    onPrimary = Color(0xFF06255E),
    secondary = Color(0xFF9FB4CC),
    onSecondary = Color(0xFF16222F),
    background = Color(0xFF101318),
    onBackground = Color(0xFFE6EBF2),
    surface = Color(0xFF161B23),
    onSurface = Color(0xFFE6EBF2),
    surfaceVariant = Color(0xFF232B36),
    onSurfaceVariant = Color(0xFFA6B1BE),
    outlineVariant = Color(0xFF2A3340),
)

@Composable
fun ScreenCheckTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkScheme,
        content = content,
    )
}

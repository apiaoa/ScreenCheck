package com.screencheck.app

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.screencheck.app.ui.color.ColorTestScreen
import com.screencheck.app.ui.gray.GrayTestScreen
import com.screencheck.app.ui.home.HomeScreen
import com.screencheck.app.ui.info.InfoScreen

private enum class AppScreen { Home, ColorTest, GrayTest, Info }

@Composable
fun AppRoot() {
    var screen by rememberSaveable { mutableStateOf(AppScreen.Home) }

    BackHandler(enabled = screen != AppScreen.Home) { screen = AppScreen.Home }

    when (screen) {
        AppScreen.Home -> HomeScreen(
            onOpenColorTest = { screen = AppScreen.ColorTest },
            onOpenGrayTest = { screen = AppScreen.GrayTest },
            onOpenInfo = { screen = AppScreen.Info },
        )

        AppScreen.ColorTest -> ColorTestScreen(onExit = { screen = AppScreen.Home })
        AppScreen.GrayTest -> GrayTestScreen(onExit = { screen = AppScreen.Home })
        AppScreen.Info -> InfoScreen(onBack = { screen = AppScreen.Home })
    }
}

package com.screencheck.app.ui.color

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.screencheck.app.R
import com.screencheck.app.ui.test.HintKind
import com.screencheck.app.ui.test.ImmersiveTestEffect
import com.screencheck.app.ui.test.OneTimeHint
import com.screencheck.app.ui.test.TapOrHoldSurface

// Test order per spec: black -> white -> red -> green -> blue, then loop.
private val TestColors = listOf(
    Color.Black,
    Color.White,
    Color.Red,
    Color.Green,
    Color.Blue,
)

/**
 * Full-screen solid colors. Tap anywhere to advance to the next color;
 * hold ~1.5s to exit back to Home.
 */
@Composable
fun ColorTestScreen(onExit: () -> Unit) {
    ImmersiveTestEffect()

    var index by rememberSaveable { mutableIntStateOf(0) }

    TapOrHoldSurface(
        onTap = { index = (index + 1) % TestColors.size },
        onLongPressExit = onExit,
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(TestColors[index]))
        OneTimeHint(
            kind = HintKind.Color,
            text = stringResource(R.string.hint_color_test),
        )
    }
}

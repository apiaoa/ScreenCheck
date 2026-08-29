package com.screencheck.app.ui.gray

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.screencheck.app.R
import com.screencheck.app.ui.test.HintKind
import com.screencheck.app.ui.test.ImmersiveTestEffect
import com.screencheck.app.ui.test.OneTimeHint
import com.screencheck.app.ui.test.TapOrHoldSurface

private enum class GrayPattern { Steps, Gradient, MidGray }

/**
 * Grayscale and gradient tests. Tap anywhere to cycle:
 * 16-step gray bars -> black-to-white horizontal gradient -> mid gray.
 */
@Composable
fun GrayTestScreen(onExit: () -> Unit) {
    ImmersiveTestEffect()

    var pattern by rememberSaveable { mutableStateOf(GrayPattern.Steps) }

    TapOrHoldSurface(
        onTap = {
            pattern = when (pattern) {
                GrayPattern.Steps -> GrayPattern.Gradient
                GrayPattern.Gradient -> GrayPattern.MidGray
                GrayPattern.MidGray -> GrayPattern.Steps
            }
        },
        onLongPressExit = onExit,
        modifier = Modifier.fillMaxSize(),
    ) {
        when (pattern) {
            GrayPattern.Steps -> GraySteps()

            GrayPattern.Gradient -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(listOf(Color.Black, Color.White)),
                    ),
            )

            GrayPattern.MidGray -> Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFF808080)),
            )
        }
        OneTimeHint(
            kind = HintKind.Gray,
            text = stringResource(R.string.hint_gray_test),
        )
    }
}

/** 16 full-height vertical bars from black to white with no gaps. */
@Composable
private fun GraySteps() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val steps = 16
        val barWidth = size.width / steps
        repeat(steps) { i ->
            val v = (255 * i) / (steps - 1)
            // +1 px overlap hides hairline seams between adjacent bars.
            drawRect(
                color = Color(v, v, v),
                topLeft = Offset(barWidth * i, 0f),
                size = Size(barWidth + 1f, size.height),
            )
        }
    }
}

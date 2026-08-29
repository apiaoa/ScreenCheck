package com.screencheck.app.ui.test

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import android.view.WindowManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LONG_PRESS_EXIT_MS = 1500L
private const val HINT_VISIBLE_MS = 3000L

/** Which one-time gesture hints have already been shown in this process. */
object GestureHints {
    var colorTestShown: Boolean = false
    var grayTestShown: Boolean = false
}

enum class HintKind { Color, Gray }

/**
 * Applies full-screen test chrome to the host activity while composed:
 * hides system bars (swipe to reveal temporarily), keeps the screen on and
 * pins the window brightness to maximum. Nothing here touches permissions;
 * it is all window-level API. Everything is restored on dispose.
 */
@Composable
fun ImmersiveTestEffect() {
    val view = LocalView.current
    val activity = LocalActivity.current ?: return

    DisposableEffect(Unit) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, view)
        val attrs = window.attributes
        val originalBrightness = attrs.screenBrightness
        val hadKeepScreenOn =
            attrs.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0

        attrs.screenBrightness =
            WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        window.attributes = attrs

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        onDispose {
            val restoreAttrs = window.attributes
            restoreAttrs.screenBrightness = originalBrightness
            if (!hadKeepScreenOn) {
                restoreAttrs.flags = restoreAttrs.flags and
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON.inv()
            }
            window.attributes = restoreAttrs
            controller.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}

/**
 * Full-screen gesture surface shared by every test mode:
 * quick tap fires [onTap], holding for [LONG_PRESS_EXIT_MS] fires
 * [onLongPressExit] once and suppresses the following tap.
 */
@Composable
fun TapOrHoldSurface(
    onTap: () -> Unit,
    onLongPressExit: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    var exited = false
                    val exitJob = scope.launch {
                        delay(LONG_PRESS_EXIT_MS)
                        exited = true
                        onLongPressExit()
                    }
                    tryAwaitRelease()
                    exitJob.cancel()
                    if (!exited) onTap()
                },
            )
        },
        content = content,
    )
}

/**
 * Semi-transparent gesture hint pinned to the top of a test screen.
 * Shown once per process for a few seconds, then never again.
 */
@Composable
fun BoxScope.OneTimeHint(kind: HintKind, text: String) {
    val alreadyShown = when (kind) {
        HintKind.Color -> GestureHints.colorTestShown
        HintKind.Gray -> GestureHints.grayTestShown
    }
    var visible by rememberSaveable { mutableStateOf(!alreadyShown) }

    DisposableEffect(Unit) {
        when (kind) {
            HintKind.Color -> GestureHints.colorTestShown = true
            HintKind.Gray -> GestureHints.grayTestShown = true
        }
        onDispose {}
    }

    LaunchedEffect(Unit) {
        delay(HINT_VISIBLE_MS)
        visible = false
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 48.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.92f),
            modifier = Modifier
                .background(Color(0xCC1C2129), RoundedCornerShape(50))
                .padding(horizontal = 16.dp, vertical = 10.dp),
        )
    }
}

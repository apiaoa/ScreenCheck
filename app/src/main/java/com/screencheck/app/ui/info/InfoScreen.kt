package com.screencheck.app.ui.info

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.screencheck.app.R
import java.util.Locale
import kotlin.math.hypot

/** Everything shown on the info page, read from read-only system APIs. */
private data class DisplaySpecs(
    val widthPx: Int,
    val heightPx: Int,
    val densityDpi: Int,
    val densityScale: Float,
    val refreshRateHz: Float?,
    val maxRefreshRateHz: Float?,
    val portrait: Boolean,
    val hdr: Boolean?,
    val diagonalInches: Double,
)

@Composable
private fun rememberDisplaySpecs(): DisplaySpecs {
    val context = LocalContext.current
    return remember {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display? =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.display
            } else {
                @Suppress("DEPRECATION")
                wm.defaultDisplay
            }

        val realSize: Pair<Int, Int> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val bounds = wm.maximumWindowMetrics.bounds
                bounds.width() to bounds.height()
            } else {
                val metrics = DisplayMetrics()
                @Suppress("DEPRECATION")
                display?.getRealMetrics(metrics)
                metrics.widthPixels to metrics.heightPixels
            }

        val config = context.resources.configuration
        val displayDensity = context.resources.displayMetrics.density

        @Suppress("DEPRECATION")
        val hdr = display?.isHdr

        val dpi = config.densityDpi.toDouble()

        DisplaySpecs(
            widthPx = realSize.first,
            heightPx = realSize.second,
            densityDpi = config.densityDpi,
            densityScale = displayDensity,
            refreshRateHz = display?.refreshRate,
            maxRefreshRateHz = display?.supportedModes?.maxOfOrNull { it.refreshRate },
            portrait = config.orientation == Configuration.ORIENTATION_PORTRAIT,
            hdr = hdr,
            diagonalInches = hypot(
                realSize.first / dpi,
                realSize.second / dpi,
            ),
        )
    }
}

@Composable
fun InfoScreen(onBack: () -> Unit) {
    val specs = rememberDisplaySpecs()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton(onClick = onBack)
            Spacer(Modifier.width(14.dp))
            Text(
                text = stringResource(R.string.info_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
        Spacer(Modifier.height(20.dp))

        SpecCard(specs)

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.info_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        ChevronLeftGlyph(tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SpecCard(specs: DisplaySpecs) {
    val notAvailable = stringResource(R.string.value_not_available)

    val rows: List<Pair<String, String>> = listOf(
        stringResource(R.string.info_resolution) to
            "${specs.widthPx} × ${specs.heightPx}",
        stringResource(R.string.info_screen_size) to
            String.format(Locale.US, "%.1f″", specs.diagonalInches),
        stringResource(R.string.info_density) to
            String.format(Locale.US, "%d dpi · %.2fx", specs.densityDpi, specs.densityScale),
        stringResource(R.string.info_refresh_rate) to
            (specs.refreshRateHz?.let { String.format(Locale.US, "%.0f Hz", it) } ?: notAvailable),
        stringResource(R.string.info_max_refresh_rate) to
            (specs.maxRefreshRateHz?.let { String.format(Locale.US, "%.0f Hz", it) } ?: notAvailable),
        stringResource(R.string.info_orientation) to
            stringResource(
                if (specs.portrait) R.string.info_orientation_portrait
                else R.string.info_orientation_landscape,
            ),
        stringResource(R.string.info_hdr) to when (specs.hdr) {
            true -> stringResource(R.string.hdr_supported)
            false -> stringResource(R.string.hdr_not_supported)
            null -> notAvailable
        },
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column {
            rows.forEachIndexed { index, (label, value) ->
                if (index > 0) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(start = 18.dp),
                    )
                }
                Row(modifier = Modifier.padding(horizontal = 18.dp, vertical = 15.dp)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = value,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ChevronLeftGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(width = 12.dp, height = 22.dp)) {
        val path = Path().apply {
            moveTo(size.width, size.height * 0.10f)
            lineTo(0f, size.height * 0.5f)
            lineTo(size.width, size.height * 0.90f)
        }
        drawPath(
            path = path,
            color = tint.copy(alpha = 0.8f),
            style = Stroke(width = size.width * 0.26f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

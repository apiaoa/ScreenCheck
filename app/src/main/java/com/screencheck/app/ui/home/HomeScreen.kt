package com.screencheck.app.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.screencheck.app.R

@Composable
fun HomeScreen(
    onOpenColorTest: () -> Unit,
    onOpenGrayTest: () -> Unit,
    onOpenInfo: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
    ) {
        Spacer(Modifier.height(56.dp))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.home_tagline),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(32.dp))

        FeatureCard(
            icon = { DeadPixelGlyph() },
            titleRes = R.string.home_color_title,
            descRes = R.string.home_color_desc,
            onClick = onOpenColorTest,
        )
        FeatureCard(
            icon = { GrayScaleGlyph() },
            titleRes = R.string.home_gray_title,
            descRes = R.string.home_gray_desc,
            onClick = onOpenGrayTest,
        )
        FeatureCard(
            icon = { InfoGlyph() },
            titleRes = R.string.home_info_title,
            descRes = R.string.home_info_desc,
            onClick = onOpenInfo,
        )

        Spacer(Modifier.weight(1f))
        Text(
            text = stringResource(R.string.home_footer),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
        )
    }
}

@Composable
private fun FeatureCard(
    icon: @Composable () -> Unit,
    titleRes: Int,
    descRes: Int,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                icon()
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(
                    text = stringResource(titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(descRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(8.dp))
            ChevronGlyph(tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Three overlapping RGB dots: the classic dead-pixel test motif. */
@Composable
private fun DeadPixelGlyph() {
    Canvas(modifier = Modifier.size(width = 36.dp, height = 36.dp)) {
        val s = size.minDimension
        drawCircle(Color(0xFFFF3B30), radius = s * 0.18f, center = Offset(s * 0.30f, s * 0.32f))
        drawCircle(Color(0xFF30D158), radius = s * 0.18f, center = Offset(s * 0.70f, s * 0.38f))
        drawCircle(Color(0xFF0A84FF), radius = s * 0.18f, center = Offset(s * 0.48f, s * 0.74f))
    }
}

/** Vertical gray bars, echoing the grayscale test pattern. */
@Composable
private fun GrayScaleGlyph() {
    val grays = listOf(0xFF3A3A3A, 0xFF5C5C5C, 0xFF828282, 0xFFAAAAAA, 0xFFD2D2D2, 0xFFF4F4F4)
    Canvas(modifier = Modifier.size(width = 36.dp, height = 36.dp)) {
        val barCount = grays.size
        val gap = size.width / (barCount * 3)
        val barWidth = (size.width - gap * (barCount - 1)) / barCount
        grays.forEachIndexed { index, gray ->
            val x = index * (barWidth + gap)
            drawRect(
                color = Color(gray),
                topLeft = Offset(x, 0f),
                size = androidx.compose.ui.geometry.Size(barWidth, size.height),
            )
        }
    }
}

/** Outlined circle with an "i" mark for the info entry. */
@Composable
private fun InfoGlyph() {
    Canvas(modifier = Modifier.size(width = 36.dp, height = 36.dp)) {
        val s = size.minDimension
        val tint = Color(0xFF5B9BFF)
        drawCircle(tint, radius = s * 0.40f, style = Stroke(width = s * 0.07f))
        drawCircle(tint, radius = s * 0.06f, center = Offset(s * 0.5f, s * 0.30f))
        drawLine(
            color = tint,
            start = Offset(s * 0.5f, s * 0.46f),
            end = Offset(s * 0.5f, s * 0.72f),
            strokeWidth = s * 0.11f,
            cap = StrokeCap.Round,
        )
    }
}

@Composable
private fun ChevronGlyph(tint: Color) {
    Canvas(modifier = Modifier.size(width = 12.dp, height = 22.dp)) {
        val path = Path().apply {
            moveTo(0f, size.height * 0.10f)
            lineTo(size.width, size.height * 0.5f)
            lineTo(0f, size.height * 0.90f)
        }
        drawPath(
            path = path,
            color = tint.copy(alpha = 0.7f),
            style = Stroke(width = size.width * 0.26f, cap = StrokeCap.Round, join = StrokeJoin.Round),
        )
    }
}

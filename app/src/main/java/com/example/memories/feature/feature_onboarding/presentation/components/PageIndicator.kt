package com.example.memories.feature.feature_onboarding.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.feature.feature_onboarding.presentation.OnboardingPreviewSurface
import kotlin.math.floor
import kotlin.math.sin

private val DotSize = 8.dp
private val DotSpacing = 12.dp
private val ActiveWidth = 28.dp
private val StretchAmount = 10.dp

/**
 * A "worm" indicator: inactive dots sit on a fixed grid while an active pill slides across them,
 * stretching at the midpoint of the swipe. Driven by the pager's fractional position rather than
 * its settled page, so it tracks the drag continuously.
 *
 * @param fraction current pager position as `currentPage + currentPageOffsetFraction`. Passed as a
 *   lambda so that scrolling only invalidates the draw phase.
 */
@Composable
fun PageIndicator(
    totalPages: Int,
    fraction: () -> Float,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)

    val step = DotSize + DotSpacing
    val totalWidth = ActiveWidth + step * (totalPages - 1)

    Canvas(
        modifier = modifier
            .width(totalWidth)
            .height(DotSize)
    ) {
        val dotPx = DotSize.toPx()
        val stepPx = step.toPx()
        val activePx = ActiveWidth.toPx()
        val centerY = size.height / 2f
        val firstCenterX = activePx / 2f

        repeat(totalPages) { index ->
            drawCircle(
                color = inactiveColor,
                radius = dotPx / 2f,
                center = Offset(firstCenterX + index * stepPx, centerY)
            )
        }

        val position = fraction().coerceIn(0f, (totalPages - 1).toFloat())
        val travel = position - floor(position)
        // Peaks at the halfway point of a swipe and returns to zero once settled.
        val stretch = sin(travel * Math.PI).toFloat() * StretchAmount.toPx()
        val pillWidth = activePx + stretch
        val pillCenterX = firstCenterX + position * stepPx

        drawRoundRect(
            color = activeColor,
            topLeft = Offset(pillCenterX - pillWidth / 2f, centerY - dotPx / 2f),
            size = Size(pillWidth, dotPx),
            cornerRadius = CornerRadius(dotPx / 2f)
        )
    }
}

@Preview(name = "Settled on page 2")
@Composable
private fun PageIndicatorSettledPreview() {
    OnboardingPreviewSurface {
        Box(modifier = Modifier.padding(16.dp)) {
            PageIndicator(totalPages = 5, fraction = { 1f })
        }
    }
}

@Preview(name = "Mid-swipe stretch")
@Composable
private fun PageIndicatorMidSwipePreview() {
    OnboardingPreviewSurface {
        Box(modifier = Modifier.padding(16.dp)) {
            PageIndicator(totalPages = 5, fraction = { 1.5f })
        }
    }
}

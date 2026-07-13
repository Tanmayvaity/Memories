package com.example.memories.feature.feature_onboarding.presentation.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.memories.LocalTheme
import com.example.memories.feature.feature_onboarding.presentation.OnboardingPreviewSurface
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin
import kotlin.random.Random

private const val SPECK_COUNT = 42
private const val TWO_PI = 2f * PI.toFloat()

/**
 * A single mote in the ambient dust field. Loop counts are integers so every speck's drift and
 * twinkle are seamless when the shared 0→1 timeline wraps.
 */
private class Speck(random: Random) {
    val x = random.nextFloat()
    val y = random.nextFloat()

    /** 0..1-ish plane: far specks are smaller, dimmer, and move less against the pager. */
    val depth = 0.3f + random.nextFloat() * 0.7f
    val driftLoops = 1 + random.nextInt(2)
    val twinkleLoops = 4 + random.nextInt(5)
    val twinklePhase = random.nextFloat() * TWO_PI
}

private fun fract(value: Float): Float = value - floor(value)

/**
 * The scene behind the onboarding content: two slow-drifting radial glows plus a field of
 * twinkling dust motes. The palette is monochrome, so everything is the primary colour at very low
 * alpha — a soft starfield on the AMOLED dark theme, faint drifting grain on light.
 *
 * @param fraction current pager position (`currentPage + currentPageOffsetFraction`), read at draw
 *   time only. Specks and glows shift against it at depth-dependent rates, so the background
 *   parallaxes as the user swipes without ever recomposing.
 */
@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    fraction: () -> Float = { 0f }
) {
    val isDark = LocalTheme.current
    val glow = MaterialTheme.colorScheme.primary

    val specks = remember { List(SPECK_COUNT) { Speck(Random(it * 7919 + 13)) } }

    val transition = rememberInfiniteTransition(label = "ambient")

    val drift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "drift"
    )
    val counterDrift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 21000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "counter_drift"
    )
    // One shared timeline for every speck; loops every 90s so the drift stays subliminal.
    val time by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 90_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "speck_time"
    )

    val topAlpha = if (isDark) 0.14f else 0.06f
    val bottomAlpha = if (isDark) 0.08f else 0.04f
    val speckAlpha = if (isDark) 0.38f else 0.2f

    Canvas(modifier = modifier) {
        val pagerShift = fraction()

        val topRadius = size.maxDimension * 0.7f
        val topCenter = Offset(
            x = lerp(size.width * 0.15f, size.width * 0.75f, drift) -
                    pagerShift * size.width * 0.04f,
            y = size.height * 0.18f
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glow.copy(alpha = topAlpha), Color.Transparent),
                center = topCenter,
                radius = topRadius
            ),
            radius = topRadius,
            center = topCenter
        )

        val bottomRadius = size.maxDimension * 0.55f
        val bottomCenter = Offset(
            x = lerp(size.width * 0.85f, size.width * 0.25f, counterDrift) -
                    pagerShift * size.width * 0.06f,
            y = size.height * 0.82f
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(glow.copy(alpha = bottomAlpha), Color.Transparent),
                center = bottomCenter,
                radius = bottomRadius
            ),
            radius = bottomRadius,
            center = bottomCenter
        )

        specks.forEach { speck ->
            // Wrap with fract so specks recycle across the edges instead of scrolling away.
            val fx = fract(speck.x - pagerShift * 0.055f * speck.depth)
            val fy = fract(speck.y - time * speck.driftLoops)
            val twinkle = 0.45f + 0.55f *
                    (0.5f + 0.5f * sin(time * TWO_PI * speck.twinkleLoops + speck.twinklePhase))

            drawCircle(
                color = glow.copy(alpha = speckAlpha * speck.depth * twinkle),
                radius = (0.7f + speck.depth * 1.2f).dp.toPx(),
                center = Offset(fx * size.width, fy * size.height)
            )
        }
    }
}

@Preview(name = "Ambient - dark")
@Composable
private fun AmbientBackgroundDarkPreview() {
    OnboardingPreviewSurface(darkTheme = true) {
        AmbientBackground(modifier = Modifier.size(width = 360.dp, height = 640.dp))
    }
}

@Preview(name = "Ambient - light")
@Composable
private fun AmbientBackgroundLightPreview() {
    OnboardingPreviewSurface(darkTheme = false) {
        AmbientBackground(modifier = Modifier.size(width = 360.dp, height = 640.dp))
    }
}

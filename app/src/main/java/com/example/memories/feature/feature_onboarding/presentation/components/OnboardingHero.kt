package com.example.memories.feature.feature_onboarding.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.feature.feature_onboarding.presentation.OnboardingPreviewSurface
import kotlin.math.cos
import kotlin.math.sin

private val HeroSize = 220.dp
private val CardSize = 104.dp

/**
 * The illustration at the top of each feature page: a soft glow, two slowly counter-rotating rings
 * (one dashed), and the feature icon in a raised squircle.
 *
 * @param parallax the page's distance from the pager's settled position, in pages. Layers move at
 *   different rates against it so the hero gains depth as the user swipes.
 */
@Composable
fun OnboardingHero(
    iconRes: Int,
    parallax: () -> Float,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "hero")
    val spin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 36000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin"
    )
    val breathe by transition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    val ringColor = MaterialTheme.colorScheme.outlineVariant
    val glowColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(HeroSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Rings drift furthest — they read as the background plane.
                    translationX = parallax() * size.width * 0.35f
                    alpha = (1f - kotlin.math.abs(parallax())).coerceIn(0f, 1f)
                }
        ) {
            val outer = size.minDimension / 2f
            val inner = outer * 0.72f

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowColor.copy(alpha = 0.10f), Color.Transparent),
                    center = center,
                    radius = inner
                ),
                radius = inner,
                center = center
            )

            rotate(degrees = spin) {
                drawCircle(
                    color = ringColor,
                    radius = outer - 1.dp.toPx(),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            floatArrayOf(6.dp.toPx(), 10.dp.toPx())
                        )
                    )
                )
            }
            rotate(degrees = -spin * 0.6f) {
                drawCircle(
                    color = ringColor,
                    radius = inner,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Satellites riding the rings: two on the outer orbit, one counter-orbiting on the
            // inner. Each is a bright core inside a soft halo.
            val outerOrbit = outer - 1.dp.toPx()
            listOf(spin, spin + 150f).forEach { degrees ->
                val radians = Math.toRadians(degrees.toDouble())
                val position = center +
                        Offset(cos(radians).toFloat(), sin(radians).toFloat()) * outerOrbit
                drawCircle(
                    color = glowColor.copy(alpha = 0.12f),
                    radius = 7.dp.toPx(),
                    center = position
                )
                drawCircle(
                    color = glowColor.copy(alpha = 0.85f),
                    radius = 2.2.dp.toPx(),
                    center = position
                )
            }
            val innerRadians = Math.toRadians((-spin * 0.6f + 210f).toDouble())
            val innerPosition = center +
                    Offset(cos(innerRadians).toFloat(), sin(innerRadians).toFloat()) * inner
            drawCircle(
                color = glowColor.copy(alpha = 0.1f),
                radius = 5.dp.toPx(),
                center = innerPosition
            )
            drawCircle(
                color = glowColor.copy(alpha = 0.6f),
                radius = 1.8.dp.toPx(),
                center = innerPosition
            )
        }

        Surface(
            modifier = Modifier
                .size(CardSize)
                .graphicsLayer {
                    // The card sits in front, so it moves less than the rings.
                    translationX = parallax() * size.width * 0.9f
                    scaleX = breathe
                    scaleY = breathe
                }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(32.dp)
                ),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 0.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private val StackCardWidth = 132.dp
private val StackCardHeight = 168.dp

/**
 * The welcome page illustration: three memory cards fanned out like a stack of prints, the middle
 * one carrying the app mark. The whole stack sways gently.
 */
@Composable
fun MemoryCardStack(
    iconRes: Int,
    parallax: () -> Float,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "stack")
    val sway by transition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )
    // 0→1 over the whole cycle, but the gloss band only crosses the card for a slice of it, so
    // the shine reads as an occasional glint rather than a constant sweep.
    val shimmer by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val glowColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .size(width = 260.dp, height = 220.dp)
            .drawBehind {
                val radius = size.minDimension * 0.75f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(glowColor.copy(alpha = 0.08f), Color.Transparent),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = radius
                    ),
                    radius = radius,
                    center = Offset(size.width / 2f, size.height / 2f)
                )
            },
        contentAlignment = Alignment.Center
    ) {
        BackCard(rotation = -14f + sway, offsetFraction = -0.22f, parallax = parallax, depth = 0.45f)
        BackCard(rotation = 14f + sway * 0.5f, offsetFraction = 0.22f, parallax = parallax, depth = 0.6f)

        Surface(
            modifier = Modifier
                .size(width = StackCardWidth, height = StackCardHeight)
                .graphicsLayer {
                    rotationZ = sway * 0.4f
                    translationX = parallax() * size.width
                }
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(24.dp)
                ),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        // Diagonal gloss band sliding across the card face, like light catching
                        // a glossy print.
                        val bandStart = (shimmer * 2.6f - 0.8f) * size.width
                        drawRect(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    glowColor.copy(alpha = 0.07f),
                                    Color.Transparent
                                ),
                                start = Offset(bandStart, 0f),
                                end = Offset(bandStart + size.width * 0.7f, size.height)
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun BackCard(
    rotation: Float,
    offsetFraction: Float,
    parallax: () -> Float,
    depth: Float
) {
    Surface(
        modifier = Modifier
            .size(width = StackCardWidth, height = StackCardHeight)
            .graphicsLayer {
                rotationZ = rotation
                translationX = size.width * offsetFraction + parallax() * size.width * depth
                alpha = 0.55f
            }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {}
}

@Preview(name = "Hero - dark")
@Composable
private fun OnboardingHeroDarkPreview() {
    OnboardingPreviewSurface(darkTheme = true) {
        Box(modifier = Modifier.padding(24.dp)) {
            OnboardingHero(iconRes = R.drawable.ic_camera, parallax = { 0f })
        }
    }
}

@Preview(name = "Hero - light")
@Composable
private fun OnboardingHeroLightPreview() {
    OnboardingPreviewSurface(darkTheme = false) {
        Box(modifier = Modifier.padding(24.dp)) {
            OnboardingHero(iconRes = R.drawable.ic_camera, parallax = { 0f })
        }
    }
}

@Preview(name = "Card stack - dark")
@Composable
private fun MemoryCardStackDarkPreview() {
    OnboardingPreviewSurface(darkTheme = true) {
        Box(modifier = Modifier.padding(24.dp)) {
            MemoryCardStack(iconRes = R.drawable.ic_memory, parallax = { 0f })
        }
    }
}

@Preview(name = "Card stack - light")
@Composable
private fun MemoryCardStackLightPreview() {
    OnboardingPreviewSurface(darkTheme = false) {
        Box(modifier = Modifier.padding(24.dp)) {
            MemoryCardStack(iconRes = R.drawable.ic_memory, parallax = { 0f })
        }
    }
}

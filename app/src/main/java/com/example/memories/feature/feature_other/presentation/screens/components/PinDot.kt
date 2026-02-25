package com.example.memories.feature.feature_other.presentation.screens.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.ui.theme.MemoriesTheme

@Preview
@Composable
fun PinDot(
    isFilled : Boolean = true
) {
    val color by animateColorAsState(
        targetValue = if (isFilled) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.outline,
        animationSpec = tween(durationMillis = 150),
        label = "dotColor"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFilled) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dotScale"
    )

    Surface(
        shape = CircleShape,
        color = color,
        modifier = Modifier
            .size(12.dp)
            .scale(scale)
    ) {}
}

@Composable
fun PinDotRow(
    modifier: Modifier = Modifier,
    pin : String = "",
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        modifier = modifier.padding(8.dp)
    ) {
        (0 until 4).forEach { it ->
            PinDot(
                isFilled = it < pin.length
            )
        }
    }
}


@Preview
@Composable
private fun PinDotPreview() {
    MemoriesTheme {
        PinDot()
    }
}


@Preview
@Composable
private fun PinDotRowReview() {
    MemoriesTheme {
        PinDotRow()
    }
}
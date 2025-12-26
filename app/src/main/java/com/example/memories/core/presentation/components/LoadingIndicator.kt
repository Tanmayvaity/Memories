package com.example.memories.core.presentation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    text : String = "Your data is being loaded",
) {
    val transition = rememberInfiniteTransition()
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000),
        )
    )
    val dotCount by transition.animateValue(
        initialValue = 0,
        targetValue = 4,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = RepeatMode.Restart
        )
    )
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularWavyProgressIndicator(
            waveSpeed = 16.dp,
            wavelength = 16.dp,
        )
        Text(
            text = buildString {
                append(text)
                append(".".repeat(dotCount))
            },
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(5.dp),
            textAlign = TextAlign.Center
        )
    }
}


@Preview
@PreviewLightDark
@PreviewDynamicColors
@Composable
fun LoadingIndicatorPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        LoadingIndicator(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        )
    }
}
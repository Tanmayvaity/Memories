package com.example.memories.feature.feature_feed.presentation.history.components

import android.R.attr.translationX
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.feature.feature_feed.presentation.history.TimeLineDisplayMode
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun AnimatedSegmentedRow(
    selectedIndex: Int,
    options: List<String>,
    onSelect: (Int) -> Unit,
    pagerPosition : Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        // The sliding indicator — positioned via fraction of total width
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(1f / options.size)
                .graphicsLayer {
                    translationX = size.width * pagerPosition
                }
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surface)
        )

        // The labels on top
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { index, label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontWeight = if (index == selectedIndex) FontWeight.SemiBold
                        else FontWeight.Medium
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun AnimatedSegmentedRowPreview() {
    MemoriesTheme {
        AnimatedSegmentedRow(
            selectedIndex = 1,
            options = TimeLineDisplayMode.entries.toList().map { it -> it.displayName },
            onSelect = {},
            pagerPosition = 0f
        )
    }

}
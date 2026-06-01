package com.example.memories.feature.feature_other.presentation.screens.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.memories.feature.feature_other.domain.model.DayActivity

/**
 * GitHub-style activity heatmap: each column is a week (Mon→Sun top to bottom), each cell a day
 * coloured by its memory count. [days] is expected to be a contiguous run starting on a Monday.
 */
@Composable
fun CalendarHeatmap(
    days: List<DayActivity>,
    modifier: Modifier = Modifier,
) {
    if (days.isEmpty()) return
    val maxCount = (days.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
    val weeks = days.chunked(7)

    val empty = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
    val base = MaterialTheme.colorScheme.primary

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            weeks.forEach { week ->
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    week.forEach { day ->
                        Box(
                            modifier = Modifier
                                .size(13.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(cellColor(day.count, maxCount, empty, base))
                        )
                    }
                }
            }
        }
        Spacer(Modifier.size(8.dp))
        LegendStrip(empty = empty, base = base)
    }
}

private fun cellColor(count: Int, max: Int, empty: Color, base: Color): Color {
    if (count <= 0) return empty
    // 4 intensity buckets
    val ratio = count.toFloat() / max
    val alpha = when {
        ratio <= 0.25f -> 0.35f
        ratio <= 0.5f -> 0.55f
        ratio <= 0.75f -> 0.75f
        else -> 1f
    }
    return base.copy(alpha = alpha)
}

@Composable
private fun LegendStrip(empty: Color, base: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Less",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(6.dp))
        listOf(empty, base.copy(alpha = 0.35f), base.copy(alpha = 0.55f), base.copy(alpha = 0.75f), base)
            .forEach { c ->
                Box(
                    modifier = Modifier
                        .size(11.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(c)
                )
                Spacer(Modifier.width(3.dp))
            }
        Spacer(Modifier.width(3.dp))
        Text(
            text = "More",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

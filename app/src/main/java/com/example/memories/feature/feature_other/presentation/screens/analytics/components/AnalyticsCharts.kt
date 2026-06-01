package com.example.memories.feature.feature_other.presentation.screens.analytics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import com.example.memories.feature.feature_other.domain.model.PeriodCount
import com.example.memories.feature.feature_other.domain.model.TagCount
import com.example.memories.feature.feature_other.domain.model.WordPoint
import com.example.memories.core.domain.model.MediaBreakdown

private val EMPTY_TEXT = "Not enough data yet"

/** Vertical bar chart driven by [PeriodCount]. */
@Composable
fun MemoriesBarChart(
    data: List<PeriodCount>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
) {
    val max = (data.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            data.forEach { point ->
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(point.count / max.toFloat())
                            .width(14.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(barColor)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            data.forEach { point ->
                Text(
                    text = point.label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Simple line chart for the word-count trend. */
@Composable
fun WordTrendChart(
    data: List<WordPoint>,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.tertiary,
) {
    val values = data.map { it.words }
    val max = (values.maxOrNull() ?: 0).coerceAtLeast(1)
    val fillColor = lineColor.copy(alpha = 0.15f)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            if (values.size < 2) return@Canvas
            val stepX = size.width / (values.size - 1)
            val points = values.mapIndexed { i, v ->
                Offset(x = i * stepX, y = size.height * (1f - v / max.toFloat()))
            }
            val line = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            val fill = Path().apply {
                addPath(line)
                lineTo(points.last().x, size.height)
                lineTo(points.first().x, size.height)
                close()
            }
            drawPath(fill, color = fillColor, style = Fill)
            drawPath(line, color = lineColor, style = Stroke(width = 4f))
            points.forEach { drawCircle(lineColor, radius = 5f, center = it) }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            data.forEach { point ->
                Text(
                    text = point.label,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Stacked horizontal bar + legend for the photo/video/text breakdown. */
@Composable
fun MediaBreakdownBar(
    media: MediaBreakdown,
    modifier: Modifier = Modifier,
) {
    val photoColor = MaterialTheme.colorScheme.primary
    val videoColor = MaterialTheme.colorScheme.tertiary
    val textColor = MaterialTheme.colorScheme.secondary
    val total = (media.photo + media.video + media.textOnly).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (media.photo > 0) Box(Modifier.fillMaxHeight().weight(media.photo.toFloat()).background(photoColor))
            if (media.video > 0) Box(Modifier.fillMaxHeight().weight(media.video.toFloat()).background(videoColor))
            if (media.textOnly > 0) Box(Modifier.fillMaxHeight().weight(media.textOnly.toFloat()).background(textColor))
        }
        Spacer(Modifier.height(12.dp))
        LegendRow(photoColor, "Photo", media.photo, total)
        LegendRow(videoColor, "Video", media.video, total)
        LegendRow(textColor, "Text only", media.textOnly, total)
    }
}

@Composable
private fun LegendRow(color: Color, label: String, value: Int, total: Int) {
    val percent = (value * 100f / total).toInt()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$value ($percent%)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Horizontal bars for the most-used tags. */
@Composable
fun TopTagsBars(
    tags: List<TagCount>,
    modifier: Modifier = Modifier,
    barColor: Color = MaterialTheme.colorScheme.primary,
) {
    if (tags.isEmpty()) {
        Text(
            text = EMPTY_TEXT,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    val max = (tags.maxOfOrNull { it.count } ?: 0).coerceAtLeast(1)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        tags.forEach { tag ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tag.label,
                    modifier = Modifier.width(96.dp),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(tag.count / max.toFloat())
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(barColor)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${tag.count}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

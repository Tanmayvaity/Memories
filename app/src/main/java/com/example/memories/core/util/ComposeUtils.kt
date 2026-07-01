package com.example.memories.core.util

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.example.memories.core.presentation.components.IconItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs


@Composable
fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 8.dp
): Modifier {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    return drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRect(
                color = Color.Red,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha
            )
        }
    }
}


@Composable
fun RevealBottomBarWhenNotScrollable(
    scrollState: ScrollableState,
    onUnableToScroll: () -> Unit,
) {
    val canScroll by remember(scrollState) {
        derivedStateOf { scrollState.canScrollForward || scrollState.canScrollBackward }
    }
    LaunchedEffect(canScroll) {
        if (!canScroll) {
            onUnableToScroll()
        }
    }
}

@Composable
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
fun SheetState.hideWithCallback(
    scope: CoroutineScope,
    onComplete: () -> Unit
) {
    scope.launch {
        hide()
    }.invokeOnCompletion { onComplete() }
}

@Composable
fun PlayButton(modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    IconItem(
        imageVector = Icons.Default.PlayArrow,
        contentDescription = "Video media",
        modifier = modifier
            .padding(8.dp),
        alpha = 0.7f,
        color = MaterialTheme.colorScheme.primary,
        onClick = onClick
    )
}

@Composable
fun WindowSizeClass.AdaptiveContent(
    compact: @Composable () -> Unit = {},
    medium: @Composable () -> Unit = {},
    expanded: () -> Unit = {},
    large: () -> Unit = {},
    xLarge: () -> Unit = {}
) {
    when {
        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXTRA_LARGE_LOWER_BOUND) -> {
            xLarge()
        }

        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_LARGE_LOWER_BOUND) -> {
            large()
        }

        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) -> {
            expanded()
        }

        this.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) -> {
            medium()
        }

        else -> {
            compact()
        }
    }

}


@Composable
fun rememberActiveItemKey(
    listState: LazyListState,
    zoneTop: Float = 0f,
    zoneBottom: Float = 0.6f,
): State<Any?> = remember(listState) {
    derivedStateOf {
        val layout = listState.layoutInfo
        val vpStart = layout.viewportStartOffset
        val vpEnd = layout.viewportEndOffset
        val vpHeight = (vpEnd - vpStart).toFloat()
        if (vpHeight <= 0f) return@derivedStateOf null

        val zoneStartPx = vpStart + zoneTop * vpHeight
        val zoneEndPx = vpStart + zoneBottom * vpHeight
        val zoneCenter = (zoneStartPx + zoneEndPx) / 2f
        layout.visibleItemsInfo
            // keep only items that actually overlap the zone
            .filter { item ->
                val itemTop = item.offset.toFloat()
                val itemBottom = (item.offset + item.size).toFloat()
                itemBottom > zoneStartPx && itemTop < zoneEndPx
            }
            // of those, the one whose center is closest to zone center wins
            .minByOrNull { item ->
                val itemCenter = item.offset + item.size / 2f
                abs(itemCenter - zoneCenter)
            }
            ?.key
    }

}


@Composable
fun rememberSettledActiveKey(listState: LazyListState): State<Any?> {
    val rawActiveKey = rememberActiveItemKey(listState)   // from step 1
    val committedKey = remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress to rawActiveKey.value }
            .collect { (scrolling, key) ->
                if (scrolling) {
                    committedKey.value = null      // pause everything during scroll
                } else {
                    committedKey.value = key       // settle → commit the centered item
                }
            }
    }
    return committedKey
}
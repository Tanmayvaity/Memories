package com.example.memories.feature.feature_feed.presentation.feed.components

import androidx.annotation.PluralsRes
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.core.presentation.MenuItem
import com.example.memories.feature.feature_feed.presentation.feed.MemoryEntryMode
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun CustomFloatingActionButton(
    expandable: Boolean = false,
    onFabClick: (MemoryEntryMode) -> Unit = {},
    fabIcon: ImageVector = Icons.Default.KeyboardArrowUp,
    actionList : List<Triple<MemoryEntryMode, ImageVector,String>> = emptyList()
) {
    var isExpanded by remember { mutableStateOf(false) }
    if (!expandable) { // Close the expanded fab if you change to non expandable nav destination
        isExpanded = false
    }

    val fabSize = 64.dp
    val expandedFabWidth by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )
    val expandedFabHeight by animateDpAsState(
        targetValue = if (isExpanded) 58.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )

    Column {

        // ExpandedBox over the FAB
        Box(
            modifier = Modifier
                .offset(y = (25).dp)
                .size(
                    width = expandedFabWidth,
                    height = (animateDpAsState(if (isExpanded) 175.dp else 0.dp, animationSpec = spring(dampingRatio = 4f))).value)
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(18.dp)
                )
        ) {
            // Customize the content of the expanded box as needed
            Column(
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 5.dp)

            ) {
                actionList.forEach {
                    Card(
                        onClick = {
                            onFabClick(it.first)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, end = 10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                        ) {
                            Icon(
                                imageVector = it.second,
                                contentDescription = it.third.toString(),
                                modifier = Modifier.padding(end = 5.dp),
                            )
                            Text(
                                text = it.third,
                                softWrap = false
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                if (expandable) {
                    isExpanded = !isExpanded
                }
            },
            modifier = Modifier
                .width(expandedFabWidth)
                .height(expandedFabHeight),
            shape = RoundedCornerShape(18.dp)

        ) {

            Icon(
                imageVector = fabIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .offset(x = animateDpAsState(if (isExpanded) -70.dp else 0.dp, animationSpec = spring(dampingRatio = 3f)).value)
            )

            Text(
                text = "Create Memory",
                softWrap = false,
                modifier = Modifier
                    .offset(x = animateDpAsState(if (isExpanded) 10.dp else 50.dp, animationSpec = spring(dampingRatio = 3f)).value)
                    .alpha(
                        animateFloatAsState(
                            targetValue = if (isExpanded) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = if (isExpanded) 250 else 100,
                                delayMillis = if (isExpanded) 100 else 0,
                                easing = EaseIn)).value)
            )

        }
    }
}

@PreviewLightDark
@Preview
@Composable
fun CustomFloatingActionButtonPreview(modifier: Modifier = Modifier) {
    MemoriesTheme{
        CustomFloatingActionButton(
            expandable = true,
            actionList = listOf(
                Triple(MemoryEntryMode.CreateMemory, Icons.Default.Add,"Create Memory"),
                Triple(MemoryEntryMode.EditImage, Icons.Outlined.Edit,"Edit Image")
            ),
        )
    }
}



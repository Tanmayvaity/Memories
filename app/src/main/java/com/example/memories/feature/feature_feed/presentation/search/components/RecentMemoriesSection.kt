package com.example.memories.feature.feature_feed.presentation.search.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_feed.presentation.components.ErrorStateCard
import com.example.memories.feature.feature_feed.presentation.search.SectionState
import com.example.memories.feature.feature_feed.presentation.search.SectionStateContainer
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun RecentMemoriesSection(
    modifier: Modifier = Modifier,
    recentMemoriesState: SectionState<List<MemoryWithMediaModel>>,
    rainbowColorsBrush: Brush,
    onItemClick: (String) -> Unit,
    onNavigateToMemoryCreate: () -> Unit = {},

    ) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HeadingText(
            title = "Recent Memories",
            modifier = Modifier.height(48.dp),
            textStyle = MaterialTheme.typography.headlineSmall
        )

        AnimatedContent(
            targetState = recentMemoriesState
        ) { state ->
            SectionStateContainer(
                state = state,
                loadingContent = {

                    LoadingIndicator(
                        showText = true,
                        text = "Loading Memories",
                        modifier = Modifier.height(64.dp)
                    )
                },
                emptyContent = {
                    EmptyResultPlaceHolder(
                        emptyText = "No memories have been made why not make one from today",
                        buttonText = "Create",
                        onButtonClick = onNavigateToMemoryCreate,
                        height = 300.dp
                    )
                },
                errorContent = { errorText ->
                    ErrorStateCard(
                        reasonText = errorText ?: "Check you connection",
                        onRetryClick = {}
                    )
                },
                successContent = { recentMemories ->
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(recentMemories) { index, item ->
                            RecentMemoryItem(
                                rainbowColorsBrush = rainbowColorsBrush,
                                onClick = {
                                    onItemClick(item.memory.memoryId)
                                },
                                memoryForTimeStamp = item.memory.memoryForTimeStamp!!,
                                uri = item.mediaList.firstOrNull()?.uri,
                            )
                        }
                    }
                },
            )
        }


    }
}

@Composable
private fun RecentMemoryItem(
    rainbowColorsBrush: Brush,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    memoryForTimeStamp: Long,
    uri: String? = null
) {

    Box(
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)
            .border(
                BorderStroke(width = 3.dp, brush = rainbowColorsBrush),
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(
                model = uri,
                contentDescription = "recent memories image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(
                        BorderStroke(width = 3.dp, brush = rainbowColorsBrush),
                        CircleShape
                    )
            )
        } else {
            Text(
                text = memoryForTimeStamp.formatTime(format = "MMM dd"),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Preview
@Composable
private fun RecentMemoriesSectionPreview() {
    MemoriesTheme {
        val previewMemories = List(5) { index ->
            MemoryWithMediaModel(
                memory = MemoryModel(
                    memoryId = "preview_$index",
                    title = "Memory $index",
                    content = "Content $index",
                    timeStamp = System.currentTimeMillis(),
                    memoryForTimeStamp = System.currentTimeMillis()
                )
            )
        }
        RecentMemoriesSection(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            recentMemoriesState = SectionState.Empty,
            rainbowColorsBrush = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Yellow, Color.Green, Color.Blue, Color.Magenta)
            ),
            onItemClick = {},
            onNavigateToMemoryCreate = {}
        )
    }
}
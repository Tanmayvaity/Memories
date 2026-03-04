package com.example.memories.feature.feature_feed.presentation.search.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.feature.feature_feed.presentation.components.ErrorStateCard
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.feature.feature_feed.presentation.search.SectionState
import com.example.memories.feature.feature_feed.presentation.search.SectionStateContainer
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun RecentSearchSection(
    modifier: Modifier = Modifier,
    onRecentSearchItemClick: (String) -> Unit = {},
    onClearAllClick: () -> Unit = {},
    onDeleteSearchClick: (String) -> Unit = {},
    onExploreClick : () -> Unit = {},
    searchState: SectionState<List<MemoryWithMediaModel>>,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeadingText(
                title = "Recent Searches",
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.headlineSmall
            )
            val showClearAll = searchState is SectionState.Success && searchState.data.isNotEmpty()

            AnimatedVisibility(visible = showClearAll) {
                TextButton(onClick = onClearAllClick) {
                    Text(
                        text = "Clear All",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        AnimatedContent(
            targetState = searchState,
            label = "recent_search_transition"
        ) { state ->
            SectionStateContainer(
                state = state,
                loadingContent = {
                    LoadingIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        showText = true,
                        text = "Loading Recent Searches"
                    )
                },
                emptyContent = {
                    EmptyResultPlaceHolder(
                        emptyText = "Your search will appear here once you start to explore your memories",
                        buttonText = "Start Exploring",
                        onButtonClick = onExploreClick,
                        height = 300.dp,
                        buttonIcon = Icons.AutoMirrored.Filled.ArrowForward
                    )


//                    Box(
//                        modifier = Modifier
//                            .height(300.dp)
//                            .clip(RoundedCornerShape(16.dp))
//                            .background(MaterialTheme.colorScheme.surfaceVariant)
//                            .padding(16.dp),
//                        contentAlignment = Alignment.Center,
//                    ) {
//                        Column(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalAlignment = Alignment.CenterHorizontally,
//                            verticalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Text(
//                                text = "Your search will appear here once you start to explore your memories",
//                                textAlign = TextAlign.Center,
//                                color = MaterialTheme.colorScheme.onSurfaceVariant
//                            )
//                            Button(
//                                onClick = {}
//                            ) {
//                                Row(
//                                    verticalAlignment = Alignment.CenterVertically,
//                                    modifier = Modifier.padding(3.dp)
//                                ) {
//                                    Text(
//                                        text = "Start Exploring"
//                                    )
//                                    Icon(
//                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
//                                        contentDescription = null,
//                                        tint = MaterialTheme.colorScheme.onPrimary
//                                    )
//                                }
//                            }
//                        }
//                    }
                },
                errorContent = {
                    ErrorStateCard(
                        onRetryClick = {}
                    )
                },
                successContent = { recentSearches ->
                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 300.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(recentSearches) { item ->
                            MemoryItem(
                                modifier = Modifier.animateItem(),
                                title = item.memory.title,
                                content = item.memory.content,
                                imageUri = item.mediaList.firstOrNull()?.uri,
                                memoryForTimeStamp = item.memory.memoryForTimeStamp ?: 0L,
                                backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                                onClick = { onRecentSearchItemClick(item.memory.memoryId) },
                                onIconClick = { onDeleteSearchClick(item.memory.memoryId) }
                            )
                        }
                    }
                },
            )
        }
    }
}


@Preview
@Composable
fun RecentSearchSectionPreview() {
    MemoriesTheme {
        RecentSearchSection(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            searchState = SectionState.Error("Error")
        )

    }
}
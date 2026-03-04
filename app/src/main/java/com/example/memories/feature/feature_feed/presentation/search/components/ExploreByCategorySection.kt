package com.example.memories.feature.feature_feed.presentation.search.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.presentation.components.HeadingText
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.core.presentation.components.TagChip
import com.example.memories.feature.feature_feed.presentation.components.ErrorStateCard
import com.example.memories.feature.feature_feed.presentation.search.SectionState
import com.example.memories.feature.feature_feed.presentation.search.SectionStateContainer
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun ExploreByCategorySection(
    tagsState: SectionState<List<TagModel>>,
    selectedIndex: Int,
    onTagSelected: (Int, TagModel) -> Unit = { _, _ -> },
    onCreateTagClick : () -> Unit = {},
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        HeadingText(
            title = "Explore by Category",
            modifier = Modifier.height(48.dp),
            textStyle = MaterialTheme.typography.headlineSmall
        )

        AnimatedContent(
            targetState = tagsState
        ) { state ->
            SectionStateContainer(
                state = state,
                loadingContent = {
                    LoadingIndicator(
                        showText = true,
                        text = "Loading Categories",
                        modifier = Modifier.height(64.dp)
                    )
                },
                emptyContent = {
                    EmptyResultPlaceHolder(
                        emptyText = "No categories found",
                        buttonText = "Create Tag",
                        onButtonClick = onCreateTagClick,
                        height = 150.dp
                    )
                },
                errorContent = { errorText ->
                    ErrorStateCard(
                        reasonText = errorText ?: "Check your connection",
                        onRetryClick = {}
                    )
                },
                successContent = { tags ->
                    LazyRow(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp),
                        state = lazyListState
                    ) {
                        itemsIndexed(tags) { index, tag ->
                            TagChip(
                                tag = tag,
                                selected = index == selectedIndex,
                                showBorder = true
                            ) {
                                onTagSelected(index, tag)
                            }
                        }
                    }
                },
            )
        }
    }
}

@Preview
@Composable
private fun ExploreByCategorySectionPreview() {
    MemoriesTheme {
        ExploreByCategorySection(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
//            tagsState = SectionState.Success(
//                listOf(
//                    TagModel(label = "Anime"),
//                    TagModel(label = "Another")
//                )
//            ),
            tagsState = SectionState.Loading,
            selectedIndex = 0,
        )
    }
}
package com.example.memories.feature.feature_feed.presentation.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.memories.core.presentation.components.TagChip
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun ExploreByCategorySection(
    tags: List<TagModel>,                 // your tag model
    selectedIndex: Int,                   // current selected tag index
    onTagSelected: (Int, TagModel) -> Unit = { _, _ -> }, // callback when a tag is clicked
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = "Explore by Category",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp)
        )

        LazyRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 5.dp)
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
    }
}

@PreviewLightDark
@Preview
@Composable
fun ExploreByCategorySectionPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        ExploreByCategorySection(
            tags = listOf(
                TagModel(label = "Anime"),
                TagModel(label = "Another")
            ),
            selectedIndex = 0,
        )
    }
}
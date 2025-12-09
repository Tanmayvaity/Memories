package com.example.memories.feature.feature_feed.presentation.search.components

import android.R.attr.theme
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.ShimmerLayout
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.feature.feature_feed.presentation.search.SearchEvents
import com.example.memories.feature.feature_feed.presentation.search.SearchState
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.ui.theme.VeryDarkGray
import com.example.memories.ui.theme.VeryLightGray

@Composable
fun RecentSearchSection(
    state: SearchState,
    theme: Boolean,
    modifier: Modifier = Modifier,
    onRecentSearchItemClick : (String) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Recent Searches",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(vertical = 5.dp)
        ) {

            // Shimmer loading
            if (state.isRecentSearchLoading) {
                items(6) {
                    ShimmerLayout(
                        isLoading = state.isRecentSearchLoading
                    ) { }
                }
            }
            // Actual data
            items(state.recentSearch) { item ->
                MemoryItem(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    memoryItem = item,
                    backgroundColor = if (theme) VeryDarkGray else VeryLightGray,
                    onClick = {
                        onRecentSearchItemClick(item.memory.memoryId)
                    }
                )
            }
        }
    }
}



@Preview
@PreviewLightDark
@Composable
fun RecentSearchSectionPreview(){
    MemoriesTheme{
        RecentSearchSection(
            theme = false,
            state = SearchState(
                recentSearch = listOf(
                    MemoryWithMediaModel(
                        memory = MemoryModel(
                            title = "Hello",
                            content = "Hello Again "
                        )
                    )
                )
            )
        )
    }
}
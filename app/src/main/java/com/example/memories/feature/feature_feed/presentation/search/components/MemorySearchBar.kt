package com.example.memories.feature.feature_feed.presentation.search.components

import android.R.attr.contentDescription
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.feature.feature_feed.presentation.search.SearchEvents
import com.example.memories.feature.feature_feed.presentation.search.SearchState
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorySearchBar(
    searchText: String,
    state: SearchState,
    onNavigateToMemoryDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    onClearInput: () -> Unit = {},
    onItemClick : (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    val horizontalPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        label = "searchBarPadding"
    )

    SearchBar(
        tonalElevation = 10.dp,
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .background(MaterialTheme.colorScheme.surface),
        inputField = {
            SearchBarDefaults.InputField(
                query = searchText,
//                onQueryChange = { onEvent(SearchEvents.InputTextChange(it)) },
                onQueryChange = {
                    onQueryChange(it)
                },
                onSearch = {},
                expanded = expanded,
                onExpandedChange = {
                    expanded = it
                },
                placeholder = { Text("Search") },

                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = {
                            onClearInput()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                            )
                        }
                    }
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            if (expanded) expanded = false
                        }
                    ) {
                        Icon(
                            imageVector = if (expanded)
                                Icons.AutoMirrored.Filled.ArrowBack
                            else
                                Icons.Outlined.Search,
                            contentDescription = null
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        },
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        }
    ) {

        Box(modifier = Modifier.fillMaxSize()) {

            if (state.isLoading) {
                CircularProgressIndicator(
                    strokeWidth = 3.dp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                items(
                    items = state.data,
                    key = { it.memory.memoryId }
                ) { item ->
                    MemoryItem(
                        memoryItem = item,
                        modifier = Modifier
                            .animateItem()
                            .padding(bottom = 5.dp)
                    ) {
                        onItemClick(item.memory.memoryId)
                    }
                }
            }
        }
    }
}


@PreviewLightDark
@PreviewDynamicColors
@Preview
@Composable
fun MemorySearchBarPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MemorySearchBar(
            searchText = "",
            state = SearchState(),
            onNavigateToMemoryDetail = {}
        )
    }
}

package com.example.memories.feature.feature_feed.presentation.search.components

import android.R.attr.contentDescription
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.memories.R
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.LoadingIndicator
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.feature.feature_feed.presentation.search.SearchEvents
import com.example.memories.feature.feature_feed.presentation.search.SearchState
import com.example.memories.navigation.AppScreen
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.launch
import kotlin.math.exp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MemorySearchBar(
    searchText: String,
    state: SearchState,
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit = {},
    onClearInput: () -> Unit = {},
    onItemClick : (String) -> Unit = {},
    onCrossClick : (String) -> Unit = {},
    isSearching : Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val horizontalPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 16.dp,
        label = "searchBarPadding"
    )
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.not_found))

    SearchBar(
        tonalElevation = 0.dp,
        modifier = modifier
            .padding(horizontal = horizontalPadding),
        colors = SearchBarDefaults.colors(
            containerColor = if (expanded) {
                MaterialTheme.colorScheme.surface
            } else {
                SearchBarDefaults.colors().containerColor
            },
            dividerColor = Color.Transparent,
        ),
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

                    if(isSearching){
                        ContainedLoadingIndicator(
                            modifier = Modifier.size(32.dp),
                        )
                    }

                    if (searchText.isNotEmpty() && !isSearching) {
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

        Box(modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                items(
                    items = state.searchResults,
                    key = { it.memory.memoryId }
                ) { item ->
                    MemoryItem(
                        modifier = Modifier
                            .animateItem()
                            .padding(bottom = 5.dp),
                        onClick = {
                            onItemClick(item.memory.memoryId)
                        },
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        title = item.memory.title,
                        content = item.memory.content,
                        imageUri = item.mediaList.firstOrNull()?.uri,
                        memoryForTimeStamp = item.memory.memoryForTimeStamp ?: 0L,
                        onIconClick = {
                            onCrossClick(item.memory.memoryId)
                        }
                    )
                }
            }
            if(state.searchResults.isEmpty() && !isSearching) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LottieAnimation(
                        composition = composition,
                        modifier = Modifier.height(400.dp),
                        speed = 2f,
                        iterations = 1
                    )
                    Text(
                        text = "No Memories Found",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineLarge,
                    )
                }
            }
        }
    }
}


@PreviewLightDark
@Composable
fun MemorySearchBarPreview(modifier: Modifier = Modifier) {
    val data = List(20){ MemoryWithMediaModel() }
    MemoriesTheme {
        MemorySearchBar(
            searchText = "5",
            state = SearchState(
                searchResults = data ,
            ),
            isSearching = false
        )
    }
}

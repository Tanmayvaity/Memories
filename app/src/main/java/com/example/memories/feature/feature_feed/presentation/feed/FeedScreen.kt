package com.example.memories.feature.feature_feed.presentation.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.feature.feature_feed.presentation.feed.components.MemoryItem
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun FeedRoot(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel<FeedViewModel>()

) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    FeedScreen(
        state = state,
        onEvent = viewModel::onEvent
    )

    LaunchedEffect(Unit) {
        viewModel.onEvent(FeedEvents.FetchFeed)
    }


}


@Composable
fun FeedScreen(
    state : FeedState,
    onEvent : (FeedEvents) -> Unit
){
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Your Posts",
                showDivider = true
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {

            items(state.memories){ it ->
                MemoryItem(
                    memoryItem = it,
                    onClick = {}
                )
            }
        }
    }
}


@Preview
@Composable
fun FeedScreenPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        FeedScreen(
            state = FeedState(
                memories = List(30){MemoryWithMediaModel()}
            ),
            onEvent =  {}
        )
    }
}


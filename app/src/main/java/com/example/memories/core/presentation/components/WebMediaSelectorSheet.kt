package com.example.memories.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.memories.core.domain.model.Photo
import com.example.memories.core.domain.model.Video
import com.example.memories.feature.feature_feed.presentation.history.components.AnimatedSegmentedRow
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebMediaSelectorSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onMediaSelected: () -> Unit,
    remoteImages: LazyPagingItems<Photo>,
    remoteVideos: LazyPagingItems<Video>
) {
    val pager = rememberPagerState(initialPage = 0, pageCount = {2})
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val imageGridState = rememberLazyGridState()
    val videoGridState = rememberLazyGridState()

    LaunchedEffect(selectedTab) {
        pager.animateScrollToPage(selectedTab)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            AnimatedSegmentedRow(
                selectedIndex = selectedTab,
                onSelect = { selectedTab = it },
                options = listOf("Images", "Videos"),
                pagerPosition = pager.currentPage + pager.currentPageOffsetFraction,
                modifier = Modifier.padding(8.dp)
            )
            HorizontalPager(
                state = pager
            ) { page ->
                when (page) {
                    0 -> {
                        PagedMediaGrid(
                            items = remoteImages,
                            key = { "photo-${it.id}" },
                            gridState = imageGridState
                        ) { image ->
                            MediaGridCell(url = image.src.portrait)
                        }
                    }

                    1 -> {
                        PagedMediaGrid(
                            items = remoteVideos,
                            key = { "video-${it.id}" },
                            gridState = videoGridState
                        ) { video ->
                            MediaGridCell(url = video.image, isVideo = true)  // video thumbnail
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun WebMediaSelectorSheetPreview() {
    MemoriesTheme {
        WebMediaSelectorSheet(
            onDismiss = {},
            onMediaSelected = {},
            remoteImages = flowOf(PagingData.from(emptyList<Photo>())).collectAsLazyPagingItems(),
            remoteVideos =  flowOf(PagingData.from(emptyList<Video>())).collectAsLazyPagingItems()
        )
    }
}
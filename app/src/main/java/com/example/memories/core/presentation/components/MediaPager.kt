package com.example.memories.core.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun MediaPager(
    mediaUris: List<String> = emptyList(),
    modifier: Modifier = Modifier,
    pagerHeight: Dp = 300.dp,
    imageContentScale: ContentScale = ContentScale.FillWidth,
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { mediaUris.size }
    )
) {
    Box(
        modifier = modifier
            .height(pagerHeight)
            .fillMaxWidth()
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->

            AsyncImage(
                model = if (LocalInspectionMode.current)
                    R.drawable.ic_launcher_background
                else
                    mediaUris[page],
                contentDescription = "Media item $page",
                modifier = Modifier.fillMaxWidth(),
                contentScale = imageContentScale
            )
        }

        MediaPageIndicatorLine(
            currentPage = pagerState.currentPage,
            pageCount = pagerState.pageCount,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Preview
@Composable
fun MediaPagerPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MediaPager(
            mediaUris = listOf("","","","","")
        )
    }
}
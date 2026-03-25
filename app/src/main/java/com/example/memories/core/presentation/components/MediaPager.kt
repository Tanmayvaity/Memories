package com.example.memories.core.presentation.components

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.util.PlayButton
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun MediaPager(
    uris: List<UriType>,
    modifier: Modifier = Modifier,
    pagerHeight: Dp = 300.dp,
    imageModifier: Modifier = Modifier,
    imageContentScale: ContentScale = ContentScale.FillWidth,
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { uris.size }
    ),
    emptyPageContent: @Composable () -> Unit = {},
    pageOverlay: @Composable BoxScope.(page: Int) -> Unit = {},
    playVideoCapability: Boolean = false,
    onPlayIconClick: (Uri) -> Unit = {},
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
            val uriType = uris.getOrNull(page)
            val uri = uriType!!.uri
            val type = uriType!!.type
            if (uri != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!playVideoCapability) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Media item $page",
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(imageModifier),
                            contentScale = imageContentScale
                        )
                        if (type != null && type.isVideoFile()) {
                            PlayButton(
                                modifier = Modifier.align(Alignment.Center),
                                onClick = {
                                    onPlayIconClick(uri.toUri())
                                }
                            )
                        }
                    } else {
                        // implement exoplayer
//                        AndroidView(
//                            modifier = Modifier.fillMaxWidth(),
//                            factory = {ctx ->
//                                PlayerView(ctx).also {
//                                    it.player = exoPlayer
//                                }
//                            }
//                        )

                    }
                    pageOverlay(page)

                }
            } else {
                emptyPageContent()
            }
        }

        MediaPageIndicatorLine(
            currentPage = pagerState.currentPage,
            pageCount = pagerState.pageCount,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp),
        )
    }
}

@Preview
@Composable
fun MediaPagerPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MediaPager(
            uris = SnapshotStateList<UriType>().apply {
                repeat(5) {
                    add(UriType("",Type.IMAGE_JPG))
                }
            },
        )
    }
}
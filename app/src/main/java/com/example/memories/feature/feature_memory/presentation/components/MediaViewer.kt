package com.example.memories.feature.feature_memory.presentation.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.memories.R
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.components.MediaPageIndicatorLine
import com.example.memories.core.presentation.components.VideoPlayer
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(UnstableApi::class)
@Composable
fun MediaViewer(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    uriTypeList: List<UriType> = emptyList(),
    errorImage: Int = R.drawable.ic_launcher_background,
    imageContentDescription: String,
    lifecycle: Lifecycle.Event
) {

    Box {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.height(350.dp)
        ) { page ->
            val uriWrapper = uriTypeList[page]
            if (uriWrapper.type == Type.IMAGE) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uriWrapper.uri)
                        .crossfade(true)
                        .build(),
                    error = painterResource(errorImage),
                    contentDescription = imageContentDescription,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }

            if (uriWrapper.type == Type.VIDEO) {
                val player = ExoPlayer.Builder(LocalContext.current).build().apply {
                    val mediaItem = MediaItem.fromUri(uriWrapper.uri!!.toUri())
                    setMediaItem(mediaItem)
                    playWhenReady = false
                    prepare()
                }

                VideoPlayer(
                    uri = uriWrapper.uri,
                    lifecycle = lifecycle,
                    onBackGesture = {},
                    player = player,
                )


//                AndroidView(
//                    factory = { context ->
//                        PlayerView(context).also {
//                            it.player = player
//                            it.useController = true
//                            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
//                        }
//                    },
//                    update = {
//                        when (lifecycle) {
//                            Lifecycle.Event.ON_PAUSE -> {
//                                it.onPause()
//                                it.player?.pause()
//                            }
//
//                            Lifecycle.Event.ON_RESUME -> {
//                                it.onResume()
//                            }
//
//                            Lifecycle.Event.ON_DESTROY -> {
//                                it.player?.release()
//                            }
//
//                            else -> Unit
//                        }
//                    },
//                )
            }


        }
        MediaPageIndicatorLine(
            modifier= Modifier.align(Alignment.BottomCenter),
            currentPage = pagerState.currentPage,
            pageCount = pagerState.pageCount
        )


    }
}

@Preview
@Composable
fun MediaViewerPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        MediaViewer(
            pagerState = rememberPagerState(pageCount = {3}),
            uriTypeList = emptyList(),
            errorImage = R.drawable.ic_launcher_background,
            imageContentDescription = "captured image",
            lifecycle = Lifecycle.Event.ON_RESUME
        )
    }
}

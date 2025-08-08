package com.example.memories.feature.feature_memory.presentation.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.memories.ui.theme.MemoriesTheme

@OptIn(UnstableApi::class)
@Composable
fun MediaViewer(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    uriType: UriType,
    errorImage: Int = R.drawable.ic_launcher_background,
    imageContentDescription: String,
    lifecycle: Lifecycle.Event
) {

    Box {
        HorizontalPager(
            state = pagerState
        ) {
            if (uriType.type == Type.IMAGE) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uriType.uri)
                        .crossfade(true)
                        .build(),
                    error = painterResource(errorImage),
                    contentDescription = imageContentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }

            if (uriType.type == Type.VIDEO) {
                val player = ExoPlayer.Builder(LocalContext.current).build().apply {
                    val mediaItem = MediaItem.fromUri(uriType.uri!!.toUri())
                    setMediaItem(mediaItem)
                    playWhenReady = false
                    prepare()
                }

                AndroidView(
                    factory = { context ->
                        PlayerView(context).also {
                            it.player = player
                            it.useController = true
                            it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                        }
                    },
                    update = {
                        when (lifecycle) {
                            Lifecycle.Event.ON_PAUSE -> {
                                it.onPause()
                                it.player?.pause()
                            }

                            Lifecycle.Event.ON_RESUME -> {
                                it.onResume()
                            }

                            Lifecycle.Event.ON_DESTROY -> {
                                it.player?.release()
                            }

                            else -> Unit
                        }
                    },
                )
            }


        }
        MediaPageIndicator(
            modifier= Modifier.align(Alignment.TopEnd),
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
            uriType = UriType(),
            errorImage = R.drawable.ic_launcher_background,
            imageContentDescription = "captured image",
            lifecycle = Lifecycle.Event.ON_RESUME
        )
    }
}

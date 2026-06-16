package com.example.memories.core.presentation.components

import android.view.SurfaceView
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun MediaPager(
    uris: List<UriType>,
    modifier: Modifier = Modifier,
    pagerHeight: Dp = 300.dp,
    imageModifier: (Int) -> Modifier = { Modifier },
    imageContentScale: ContentScale = ContentScale.FillWidth,
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { uris.size }
    ),
    emptyPageContent: @Composable () -> Unit = {},
    pageOverlay: @Composable BoxScope.(page: Int) -> Unit = {},
    showPlayerController : Boolean = true,
    player : ExoPlayer? = null,
    isActive : Boolean = false
) {
    val context = LocalContext.current
    val activePlayer : ExoPlayer = remember {
        player
            ?: ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = false
            }

    }

    DisposableEffect(Unit) {
        onDispose {
            if(player == null){
                activePlayer.release()
            }
        }
    }

    LaunchedEffect(pagerState.isScrollInProgress) {
        activePlayer.pause()
    }


    LaunchedEffect(isActive,pagerState.settledPage, uris) {

        if(!isActive){
            activePlayer.pause()
        }

        val item = uris.getOrNull(pagerState.settledPage) ?: return@LaunchedEffect
        if (item.type != null && item.type.isVideoFile()) {
            val uri = item.uri ?: return@LaunchedEffect
            activePlayer.setMediaItem(MediaItem.fromUri(uri))
            activePlayer.prepare()
            activePlayer.play()
        } else {
            activePlayer.pause()
//            player.clearMediaItems()
        }
    }

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
            val settled = page == pagerState.settledPage
            if (uri != null && type != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (type.isImageFile()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Media item $page",
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(imageModifier(page)),
                            contentScale = imageContentScale
                        )
//                        if (type != null && type.isVideoFile()) {
//                            PlayButton(
//                                modifier = Modifier.align(Alignment.Center),
//                                onClick = {
//                                    onPlayIconClick(uri.toUri())
//                                }
//                            )
//                        }
                    } else {
                        // implement exoplayer
                        AnimatedContent(targetState = settled) { settled ->
                            if(settled && isActive){
                                AndroidView(
                                    modifier = Modifier.fillMaxSize(),
                                    factory = { ctx ->
                                        PlayerView(ctx).apply {
                                            useController = showPlayerController
                                            setKeepContentOnPlayerReset(true)
                                        }
                                    },
                                    update = {
                                        it.player = activePlayer

                                    }
                                )
                            }else{
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(uri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Media item $page",
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentScale = imageContentScale
                                )
                            }

                        }


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
                    add(UriType("", Type.IMAGE_JPG))
                }
            },
        )
    }
}
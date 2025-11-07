package com.example.memories.core.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.memories.core.util.getExoPlayer
import com.example.memories.ui.theme.MemoriesTheme


@Composable
fun VideoPlayer(
    uri: String?,
    modifier: Modifier = Modifier,
    lifecycle: Lifecycle.Event,
    onBackGesture : (ExoPlayer) -> Unit = {},
    player : ExoPlayer
) {

    if(uri==null)return


    val context = LocalContext.current


    // Remember player across recompositions


    // Handle playback errors gracefully
    LaunchedEffect(player) {
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("VideoPlayer", "Playback failed: ${error.message}", error)
                if (error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED) {
                    player.stop()
                    player.clearMediaItems()
                    player.setMediaItem(MediaItem.fromUri(uri.toUri()))
                    player.prepare()
                    player.play()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                Log.d("VideoPlayer", "State: $playbackState")
            }
        })
    }

    // Release player when leaving composition
    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    // AndroidView for ExoPlayer
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
            }
        },
        update = { view ->
            when (lifecycle) {
                Lifecycle.Event.ON_PAUSE -> {
                    view.onPause()
                    view.player?.pause()
                }

                Lifecycle.Event.ON_RESUME -> view.onResume()

                else -> Unit
            }
        },
        modifier = modifier
            .fillMaxSize()

    )
}



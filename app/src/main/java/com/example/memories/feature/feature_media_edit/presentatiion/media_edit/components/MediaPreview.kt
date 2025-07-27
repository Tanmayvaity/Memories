package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.memories.R
import com.example.memories.core.presentation.IconItem
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType

const val TAG = "ImagePreview"

@OptIn(UnstableApi::class)
@Preview
@Composable
fun MediaPreview(
    modifier: Modifier = Modifier,
    onBackPress: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onEditItemClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    bitmap: Bitmap? = null,
    uriType: UriType? = null
) {
    val context = LocalContext.current
    var lifecycle by remember {
        mutableStateOf(Lifecycle.Event.ON_CREATE)
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)

        }
    }

    LaunchedEffect(Unit) {
        Log.d(TAG, "ImagePreview: uri is ${uriType!!.uri}")
    }


    AnimatedVisibility(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
//        visible = if(imageUri!=null) true else false,
        visible = bitmap != null || uriType?.type == Type.VIDEO,
        enter = fadeIn(
            animationSpec = tween(500)
        ),
        exit = fadeOut(
            animationSpec = tween(500)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
        ) {


            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (uriType!!.type == Type.IMAGE) {
                    // bitmap would be always null in case if the file is not an image
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Captured Image",
                            modifier = Modifier
                                .weight(5f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                if (uriType!!.type == Type.VIDEO) {

                    val player = ExoPlayer.Builder(context).build().apply {
                        val mediaItem = MediaItem.fromUri(uriType!!.uri!!.toUri())
                        setMediaItem(mediaItem)
                        playWhenReady = true
                        prepare()
                    }

                    player.addListener(object : Player.Listener {
                        override fun onPlayerError(error: PlaybackException) {
                            Log.e(TAG, "Playback failed: ${error.message}", error)
                            val cause = error.cause

                           if(error.errorCode == PlaybackException.ERROR_CODE_IO_UNSPECIFIED){
                               player.stop()
                               player.clearMediaItems()
                               player.setMediaItem(MediaItem.fromUri(uriType!!.uri!!.toUri()))
                               player.prepare()
                               player.play()
                           }

                        }

                        override fun onPlaybackStateChanged(playbackState: Int) {
                            Log.d(TAG, "State: $playbackState")
                        }

                    })



                    AndroidView(
                        factory = { context ->
                            PlayerView(context).also {
                                it.player = player
                                it.useController = true
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
                        modifier = Modifier
                            .weight(5f)
                            .fillMaxWidth()
//                        ,
                    )
                }
                Row(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(10.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    OutlinedButton(
                        enabled = bitmap != null || uriType.type == Type.VIDEO,
                        onClick = {
                            onDownloadClick()
                        },
                        modifier = Modifier
                            .height(70.dp)
                            .weight(1f)
                            .padding(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = Color.Black,
                            disabledContainerColor = Color.LightGray
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.download)
                        )
                    }
                    IconItem(
                        drawableRes = R.drawable.ic_edit,
                        contentDescription = "Edit Media",
                        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        onClick = {
                            onEditItemClick()
                        }
                    )
                    Button(
                        onClick = {
                            onNextClick()
                        },
                        modifier = Modifier
                            .height(70.dp)
                            .weight(1f)
                            .padding(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    ) {
                        Text(
                            text = stringResource(R.string.navigate_to_next)
                        )
                    }
                }

            }

//            IconItem(
//                modifier = Modifier
//                    .rotate(45f)
//                    .padding(top = 100.dp)
//                    .align(Alignment.TopStart),
//                drawableRes = R.drawable.ic_create,
//                contentDescription = "",
//                backgroundColor = Color.Transparent,
//                color = Color.White,
//                onClick = {
//                    onBackPress()
//                },
//            )


        }
    }


}


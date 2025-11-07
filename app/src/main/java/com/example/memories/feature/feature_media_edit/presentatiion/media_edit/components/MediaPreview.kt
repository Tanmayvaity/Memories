package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components

import android.graphics.Bitmap
import android.util.Log
import androidx.activity.BackEventCompat
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
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
import coil3.compose.AsyncImage
import com.example.memories.R
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.components.VideoPlayer
import com.example.memories.core.util.getExoPlayer
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow

const val TAG = "ImagePreview"

@OptIn(UnstableApi::class)
@Composable
fun MediaPreview(
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    bitmap: Bitmap? = null,
    uriType: UriType? = null
) {
    val context = LocalContext.current
    var player : ExoPlayer?  = null
    var showEditCard by remember { mutableStateOf(false) }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .animateContentSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ){
            Card(
                modifier = Modifier.padding(5.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.LightGray.copy(alpha = 0.4f)
                )
            ){
                if(uriType!!.type == Type.IMAGE){
                    AsyncImage(
                        model = if (LocalInspectionMode.current && bitmap == null) R.drawable.ic_launcher_background else uriType!!.uri,
                        contentDescription = "Choosen/taken image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(25.dp))
                    )

                }
                if(uriType!!.type == Type.VIDEO){
                     player = remember(uriType.uri) {getExoPlayer(context,uriType!!.uri!!)  }
                    VideoPlayer(
                        uri = uriType.uri,
                        lifecycle = lifecycle,
                        player = player
                    )
                }
            }
            MediaActionBar(
                modifier = Modifier.align(Alignment.TopEnd),
                onNextClick = onNextClick,
                onDownloadClick = onDownloadClick,
                onEditClick = {
                    showEditCard = !showEditCard
                }
            )
        }

        AnimatedVisibility(
            visible = showEditCard,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = 10.dp,end = 10.dp, bottom = 10.dp)
            ) {
                EditList(
                    menuItems = getEditMenuItems()
                )
            }
        }
    }
}

@Preview
@Composable
fun MediaPreviewPreview() {
    MemoriesTheme {
        MediaPreview(
            uriType = UriType(uri = "",type = Type.IMAGE)
        )
    }
}



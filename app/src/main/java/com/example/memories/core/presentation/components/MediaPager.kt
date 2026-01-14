package com.example.memories.core.presentation.components

import android.graphics.Bitmap
import android.graphics.RenderNode
import android.graphics.RuntimeShader
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.memories.ui.theme.MemoriesTheme
import com.google.common.math.Quantiles.scale

enum class MediaCreationType {
    SHOW,       // Read-only display using readOnlyMediaUriList
    EDIT,       // Edit mode using bitmapList
    NOT_BITMAP  // Edit mode using mediaUris
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun MediaPager(
    modifier: Modifier = Modifier,
    mediaUris: SnapshotStateList<String?>? = null,
    bitmapList: List<Bitmap?>? = null,
    pagerHeight: Dp = 300.dp,
    imageContentScale: ContentScale = ContentScale.FillWidth,
    pagerState: PagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 5 }
    ),
    type: MediaCreationType = MediaCreationType.EDIT,
    readOnlyMediaUriList: List<String> = emptyList(),
    onAddMediaClick: () -> Unit = {},
    onRemoveMediaClick: () -> Unit = {},
    runtimeShader : RuntimeShader? = null,
    adjustmentShader : RuntimeShader? = null,
    rotation : Float = 0f
) {
    val filterEffect = if(runtimeShader != null){
        android.graphics.RenderEffect.createRuntimeShaderEffect(
            runtimeShader,
            "inputShader"
        )
    }else{
        null
    }

    val adjustmentEffect = if(adjustmentShader != null){
       android.graphics.RenderEffect.createRuntimeShaderEffect(
            adjustmentShader,
            "inputShader"
        )
    }else{
        null
    }


    val chainEffect = when {
        filterEffect != null && adjustmentEffect != null ->
            android.graphics.RenderEffect.createChainEffect(filterEffect,adjustmentEffect)

        adjustmentEffect != null -> adjustmentEffect
        filterEffect != null -> filterEffect
        else -> null
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
            val media = mediaUris?.getOrNull(page)
            val bitmap = bitmapList?.getOrNull(page)

            // Determine if content exists based on mode
            val hasContent = when (type) {
                MediaCreationType.SHOW -> page < readOnlyMediaUriList.size
                MediaCreationType.EDIT -> bitmap != null
                MediaCreationType.NOT_BITMAP -> media != null
            }

            // Show add icon only in edit modes when no content
            val showIcon = when (type) {
                MediaCreationType.EDIT -> bitmap == null
                MediaCreationType.NOT_BITMAP -> media == null
                MediaCreationType.SHOW -> false
            }

            // Show image when content exists or in preview mode
            val showImage = hasContent || LocalInspectionMode.current

            // Add Icon
            AnimatedVisibility(visible = showIcon) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    IconItem(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add media",
                        alpha = 0.3f,
                        color = MaterialTheme.colorScheme.onSurface,
                        onClick = { onAddMediaClick() }
                    )
                }
            }

            // Image Content
            AnimatedVisibility(visible = showImage) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when (type) {
                        MediaCreationType.SHOW -> {
                            // Read-only URI display
                            if (page < readOnlyMediaUriList.size) {
                                Log.d("MediaPager", "SHOW mode - URI: ${readOnlyMediaUriList[page]}")
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(readOnlyMediaUriList[page])
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Media item $page",
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = imageContentScale
                                )
                            }
                        }

                        MediaCreationType.NOT_BITMAP -> {
                            // Edit with URIs
                            media?.let { uri ->
                                Log.d("MediaPager", "NOT_BITMAP mode - URI: $uri")
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(uri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Media item $page",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .graphicsLayer {
                                            renderEffect = chainEffect?.asComposeRenderEffect()
                                            rotationZ = rotation
                                        },
                                    contentScale = imageContentScale
                                )
                            }
                        }

                        MediaCreationType.EDIT -> {
                            // Edit with bitmaps
                            bitmap?.let { bmp ->
                                Log.d("MediaPager", "EDIT mode - Bitmap: $bmp")
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "Media item $page",
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    contentScale = imageContentScale
                                )
                            }
                        }
                    }

                    // Close button only for editable modes when content exists
                    if (type != MediaCreationType.SHOW && hasContent) {
                        IconItem(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove media",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            alpha = 0.3f,
                            onClick = { onRemoveMediaClick() },
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
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
            mediaUris = SnapshotStateList<String?>().apply {
                repeat(5) {
                    add("")
                }
            },
            type = MediaCreationType.NOT_BITMAP
        )
    }
}
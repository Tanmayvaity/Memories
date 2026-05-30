package com.example.memories.feature.feature_other.presentation.screens.manage_media.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.net.toUri
import com.example.memories.R
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.components.LoadingIconItem
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.formatTime
import com.example.memories.ui.theme.MemoriesTheme

/**
 * Full screen viewer for a single piece of media opened from the Manage Media grid.
 * Surfaces the memory the media belongs to and lets the user favourite, download or share it.
 * Deletion is intentionally not offered here since media is tied to its memory.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaViewerDialog(
    media: MediaModel,
    associatedMemory: MemoryWithMediaModel?,
    onDismiss: () -> Unit,
    isDownloading: Boolean = false,
    isSharing: Boolean = false,
    onFavourite: (mediaId: String, currentFavouriteState: Boolean) -> Unit = { _, _ -> },
    onDownload: (uri: android.net.Uri, type: Type) -> Unit = { _, _ -> },
    onShare: (uri: android.net.Uri, type: Type) -> Unit = { _, _ -> },
    onPlayVideo: (uri: android.net.Uri) -> Unit = {},
    onOpenMemory: (memoryId: String) -> Unit = {},
    onEdit: (uri: String) -> Unit = {}
) {
    val pagerState = rememberPagerState { 1 }
    val scrollState = rememberScrollState()
    var isFavourite by remember(media.mediaId) { mutableStateOf(media.favourite) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = { Text("") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                MediaPager(
                    modifier = Modifier.fillMaxWidth(),
                    uris = listOf(UriType(media.uri, media.type)),
                    imageContentScale = ContentScale.Fit,
                    pagerState = pagerState,
                    onPlayIconClick = onPlayVideo
                )

                if (associatedMemory != null) {
                    val memory = associatedMemory.memory
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "From memory",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = memory.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = (memory.memoryForTimeStamp ?: memory.timeStamp)
                                    .formatTime(format = "dd MMM yyyy"),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { onOpenMemory(memory.memoryId) }) {
                            Text("Open")
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (media.type.isImageFile()) {
                        LoadingIconItem(
                            drawableRes = R.drawable.ic_adjust,
                            onClick = { onEdit(media.uri) }
                        )
                    }
                    LoadingIconItem(
                        drawableRes = if (isFavourite) R.drawable.ic_favourite_filled
                        else R.drawable.ic_favourite,
                        onClick = {
                            onFavourite(media.mediaId, isFavourite)
                            isFavourite = !isFavourite
                        }
                    )
                    LoadingIconItem(
                        drawableRes = R.drawable.ic_download,
                        onClick = { onDownload(media.uri.toUri(), media.type) },
                        isLoading = isDownloading
                    )
                    LoadingIconItem(
                        drawableRes = R.drawable.ic_share,
                        onClick = { onShare(media.uri.toUri(), media.type) },
                        isLoading = isSharing
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MediaViewerDialogPreview() {
    MemoriesTheme {
        MediaViewerDialog(
            media = MediaModel(memoryId = "1", uri = "", type = Type.IMAGE_JPG),
            associatedMemory = MemoryWithMediaModel(),
            onDismiss = {}
        )
    }
}

package com.example.memories.feature.feature_other.presentation.screens.manage_media

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.memories.R
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.presentation.components.AppTopBar
import com.example.memories.core.util.PlayButton
import com.example.memories.core.util.startChooser
import com.example.memories.core.util.startMediaChooser
import com.example.memories.core.util.startMultiShareChooser
import com.example.memories.feature.feature_other.presentation.screens.manage_media.components.MediaViewerDialog
import com.example.memories.feature.feature_other.presentation.viewmodels.ManageMediaEvents
import com.example.memories.feature.feature_other.presentation.viewmodels.ManageMediaUiEvent
import com.example.memories.feature.feature_other.presentation.viewmodels.ManageMediaViewModel

@Composable
fun ManageMediaRoot(
    onBack: () -> Unit,
    onNavigateToMemoryDetail: (String) -> Unit = {},
    onNavigateToMediaEdit: (String) -> Unit = {},
    viewModel: ManageMediaViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val mediaItems = viewModel.media.collectAsLazyPagingItems()
    val showHidden by viewModel.showHidden.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ManageMediaUiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                is ManageMediaUiEvent.Error ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()

                is ManageMediaUiEvent.ShowShareChooser -> context.startChooser(event.uri)
                is ManageMediaUiEvent.ShowMultiShareChooser ->
                    context.startMultiShareChooser(event.uris)

                is ManageMediaUiEvent.ShowMediaChooser -> context.startMediaChooser(event.uri)
            }
        }
    }

    ManageMediaScreen(
        mediaItems = mediaItems,
        isDownloading = state.isDownloading,
        isSharing = state.isSharing,
        showHidden = showHidden,
        associatedMemory = state.associatedMemory,
        onBack = onBack,
        onEvent = viewModel::onEvent,
        onNavigateToMemoryDetail = onNavigateToMemoryDetail,
        onNavigateToMediaEdit = onNavigateToMediaEdit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageMediaScreen(
    mediaItems: LazyPagingItems<MediaModel>,
    isDownloading: Boolean = false,
    isSharing: Boolean = false,
    showHidden: Boolean = false,
    associatedMemory: com.example.memories.core.domain.model.MemoryWithMediaModel? = null,
    onBack: () -> Unit = {},
    onEvent: (ManageMediaEvents) -> Unit = {},
    onNavigateToMemoryDetail: (String) -> Unit = {},
    onNavigateToMediaEdit: (String) -> Unit = {}
) {
    val selected = remember { mutableStateMapOf<String, MediaModel>() }
    val selectionMode = selected.isNotEmpty()
    var viewedMedia by remember { mutableStateOf<MediaModel?>(null) }

    fun onItemClick(media: MediaModel) {
        if (selectionMode) {
            if (selected.containsKey(media.mediaId)) selected.remove(media.mediaId)
            else selected[media.mediaId] = media
        } else {
            viewedMedia = media
            onEvent(ManageMediaEvents.LoadAssociatedMemory(media.memoryId))
        }
    }

    Scaffold(
        topBar = {
            if (selectionMode) {
                SelectionTopBar(
                    count = selected.size,
                    onClear = { selected.clear() },
                    onDownload = {
                        onEvent(ManageMediaEvents.DownloadMultiple(selected.values.toList()))
                        selected.clear()
                    },
                    onShare = {
                        onEvent(ManageMediaEvents.ShareMultiple(selected.values.toList()))
                        selected.clear()
                    },
                    isBusy = isDownloading || isSharing
                )
            } else {
                AppTopBar(
                    showDivider = false,
                    showNavigationIcon = true,
                    onNavigationIconClick = onBack,
                    title = {
                        Text(text = "Manage Media", fontWeight = FontWeight.SemiBold)
                    },
                    showAction = true,
                    actionContent = {
                        IconButton(onClick = { onEvent(ManageMediaEvents.ToggleShowHidden) }) {
                            Icon(
                                painter = painterResource(
                                    if (showHidden) R.drawable.ic_hidden else R.drawable.ic_not_hidden
                                ),
                                contentDescription = if (showHidden) "Show visible media"
                                else "Show hidden media"
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        if (mediaItems.itemCount == 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No media yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(
                    count = mediaItems.itemCount,
                    key = mediaItems.itemKey { it.mediaId }
                ) { index ->
                    val media = mediaItems[index] ?: return@items
                    MediaGridItem(
                        media = media,
                        selected = selected.containsKey(media.mediaId),
                        selectionMode = selectionMode,
                        onClick = { onItemClick(media) },
                        onLongClick = { selected[media.mediaId] = media }
                    )
                }
            }
        }
    }

    viewedMedia?.let { media ->
        MediaViewerDialog(
            media = media,
            associatedMemory = associatedMemory,
            isDownloading = isDownloading,
            isSharing = isSharing,
            onDismiss = {
                viewedMedia = null
                onEvent(ManageMediaEvents.ClearAssociatedMemory)
            },
            onFavourite = { mediaId, current ->
                onEvent(ManageMediaEvents.ToggleFavourite(mediaId, current))
            },
            onDownload = { uri, type ->
                onEvent(ManageMediaEvents.DownloadMedia(uri, type))
            },
            onShare = { uri, type ->
                onEvent(ManageMediaEvents.ShareMedia(uri, type))
            },
            onPlayVideo = { uri ->
                onEvent(ManageMediaEvents.PlayVideo(uri))
            },
            onOpenMemory = { memoryId ->
                viewedMedia = null
                onEvent(ManageMediaEvents.ClearAssociatedMemory)
                onNavigateToMemoryDetail(memoryId)
            },
            onEdit = { uri ->
                viewedMedia = null
                onEvent(ManageMediaEvents.ClearAssociatedMemory)
                onNavigateToMediaEdit(uri)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectionTopBar(
    count: Int,
    onClear: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    isBusy: Boolean
) {
    TopAppBar(
        title = { Text(text = "$count selected", fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onClear) {
                Icon(Icons.Default.Close, contentDescription = "Clear selection")
            }
        },
        actions = {
            IconButton(onClick = onDownload, enabled = !isBusy) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = "Download selected"
                )
            }
            IconButton(onClick = onShare, enabled = !isBusy) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = "Share selected"
                )
            }
        }
    )
}

@Composable
private fun MediaGridItem(
    media: MediaModel,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val previewMode = LocalInspectionMode.current
    Box(
        modifier = Modifier
            .padding(1.dp)
            .aspectRatio(1f)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(if (previewMode) R.drawable.ic_launcher_background else media.uri)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.ic_launcher_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (media.type.isVideoFile()) {
            PlayButton(modifier = Modifier.align(Alignment.Center))
        }

        if (selectionMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                        else MaterialTheme.colorScheme.scrim.copy(alpha = 0.15f)
                    )
            )
            SelectionMarker(
                selected = selected,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
            )
        }
    }
}

@Composable
private fun SelectionMarker(selected: Boolean, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(22.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(
                if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

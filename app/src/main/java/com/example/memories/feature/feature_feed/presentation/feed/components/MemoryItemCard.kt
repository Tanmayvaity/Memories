package com.example.memories.feature.feature_feed.presentation.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.example.memories.R
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.SyncStatus
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.ActionSelectorBottomSheet
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.core.util.formatTime
import com.example.memories.feature.feature_firebase.navigation.IS_FIREBASE_ENABLED
import com.example.memories.ui.theme.MemoriesTheme
import kotlinx.coroutines.launch

private const val MAX_VISIBLE_TAGS = 3

/** Photos can be any colour, so overlays use a fixed scrim instead of theme colours. */
private val PhotoScrim = Color.Black.copy(alpha = 0.45f)
private val PhotoErrorTint = Color(0xFFFF8A80)

@Composable
fun MemoryItemCard(
    modifier: Modifier = Modifier,
    memoryItem: MemoryWithMediaModel = MemoryWithMediaModel(),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: () -> Unit = {},
    onFavouriteButtonClick: () -> Unit = {},
    onHideButtonClick: () -> Unit = {},
    onDeleteButtonClick: () -> Unit = {},
    elevation: Int = 0,
    shape: Shape = RoundedCornerShape(16.dp),
    isPlayerActive: Boolean = false,
    player: ExoPlayer? = null
) {
    val memory = memoryItem.memory
    val actions: @Composable (onPhoto: Boolean) -> Unit = { onPhoto ->
        MemoryCardActions(
            title = memory.title,
            isFavourite = memory.favourite,
            isHidden = memory.hidden,
            onFavouriteClick = onFavouriteButtonClick,
            onHideClick = onHideButtonClick,
            onDeleteClick = onDeleteButtonClick,
            onPhoto = onPhoto,
        )
    }

    Card(
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
        shape = shape,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
    ) {
        if (memoryItem.mediaList.isNotEmpty()) {
            MediaMemoryContent(
                memoryItem = memoryItem,
                isPlayerActive = isPlayerActive,
                player = player,
                actions = { actions(true) },
            )
        } else {
            TextMemoryContent(
                memoryItem = memoryItem,
                actions = { actions(false) },
            )
        }
    }
}

/** Journal-style layout: a date block on the left, the entry on the right. */
@Composable
private fun TextMemoryContent(
    memoryItem: MemoryWithMediaModel,
    actions: @Composable () -> Unit,
) {
    val memory = memoryItem.memory
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 12.dp, bottom = 16.dp, end = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        memory.memoryForTimeStamp?.let { DateBlock(timestamp = it, syncStatus = memory.syncStatus) }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = memory.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 8.dp),
                )
                actions()
            }
            MemoryBody(
                memoryItem = memoryItem,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
    }
}

/** Photo on top with the date and actions overlaid, the entry below. */
@Composable
private fun MediaMemoryContent(
    memoryItem: MemoryWithMediaModel,
    isPlayerActive: Boolean,
    player: ExoPlayer?,
    actions: @Composable () -> Unit,
) {
    val memory = memoryItem.memory
    val isPreviewModeOn = LocalInspectionMode.current
    val pager = rememberPagerState(
        pageCount = { if (isPreviewModeOn) 5 else memoryItem.mediaList.size }
    )

    Box {
        MediaPager(
            uris = memoryItem.mediaList.map { UriType(it.uri, it.type) },
            pagerState = pager,
            pagerHeight = 240.dp,
            imageContentScale = ContentScale.Crop,
            showPlayerController = true,
            autoShowPlayerController = false,
            fillVideo = true,
            player = player,
            isActive = isPlayerActive
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            memory.memoryForTimeStamp?.let {
                DateChip(text = it.formatTime(format = "EEE, dd MMM"), syncStatus = memory.syncStatus)
            }
            Box(modifier = Modifier.weight(1f))
            actions()
        }
    }
    Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp)) {
        Text(
            text = memory.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        MemoryBody(memoryItem = memoryItem)
    }
}

/** Story preview and tags, shared by both layouts. */
@Composable
private fun MemoryBody(
    memoryItem: MemoryWithMediaModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (memoryItem.memory.content.isNotBlank()) {
            Text(
                text = memoryItem.memory.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                lineHeight = 20.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
        if (memoryItem.tagsList.isNotEmpty()) {
            TagChips(
                tags = memoryItem.tagsList,
                modifier = Modifier.padding(top = 12.dp)
            )
        }
    }
}

@Composable
private fun DateBlock(timestamp: Long, syncStatus: SyncStatus) {
    Column(
        modifier = Modifier
            .width(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = timestamp.formatTime(format = "dd"),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = timestamp.formatTime(format = "MMM").uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = timestamp.formatTime(format = "EEE"),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp),
        )
        SyncStatusIcon(
            status = syncStatus,
            onPhoto = false,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun DateChip(text: String, syncStatus: SyncStatus) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PhotoScrim)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
        )
        SyncStatusIcon(status = syncStatus, onPhoto = true)
    }
}

/**
 * Shows a warning only when the memory failed to sync; synced and pending memories show nothing.
 * Hidden in the `base` flavor, which has no remote sync.
 */
@Composable
private fun SyncStatusIcon(
    status: SyncStatus,
    onPhoto: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!IS_FIREBASE_ENABLED || status != SyncStatus.SYNC_FAILED) return

    Icon(
        painter = painterResource(R.drawable.ic_cloud_off),
        contentDescription = "Sync failed",
        // Theme error red is too dark to read on the photo scrim.
        tint = if (onPhoto) PhotoErrorTint else MaterialTheme.colorScheme.error,
        modifier = modifier.size(14.dp),
    )
}

/** Favourite toggle plus the ⋮ button that opens the Hide/Delete sheet. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemoryCardActions(
    title: String,
    isFavourite: Boolean,
    isHidden: Boolean,
    onFavouriteClick: () -> Unit,
    onHideClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onPhoto: Boolean,
) {
    var showOptions by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Let the sheet animate away before running the action (delete opens a dialog).
    fun dismissThen(action: () -> Unit) {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            showOptions = false
            action()
        }
    }

    val iconTint = if (onPhoto) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val buttonColors = if (onPhoto) {
        IconButtonDefaults.iconButtonColors(containerColor = PhotoScrim)
    } else {
        IconButtonDefaults.iconButtonColors()
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onFavouriteClick, colors = buttonColors) {
            Icon(
                imageVector = if (isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavourite) "Remove from favourites" else "Add to favourites",
                tint = if (isFavourite && !onPhoto) MaterialTheme.colorScheme.primary else iconTint,
                modifier = Modifier.size(20.dp),
            )
        }
        IconButton(onClick = { showOptions = true }, colors = buttonColors) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = iconTint,
                modifier = Modifier.size(20.dp),
            )
        }
    }

    if (showOptions) {
        ActionSelectorBottomSheet(
            sheetState = sheetState,
            sheetTitle = title,
            onDismiss = { showOptions = false },
            items = listOf(
                MenuItem(
                    title = if (isHidden) "Unhide" else "Hide",
                    content = if (isHidden) "Show this memory in your feed again"
                    else "Move this memory to Hidden memories",
                    icon = if (isHidden) R.drawable.ic_not_hidden else R.drawable.ic_hidden,
                    onClick = { dismissThen(onHideClick) },
                ),
                MenuItem(
                    title = "Delete",
                    content = "Permanently remove this memory and its media",
                    icon = R.drawable.ic_delete,
                    onClick = { dismissThen(onDeleteClick) },
                ),
            ),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagChips(
    tags: List<TagModel>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        tags.take(MAX_VISIBLE_TAGS).forEach { tag -> TagChip(text = "#${tag.label}") }
        if (tags.size > MAX_VISIBLE_TAGS) {
            TagChip(text = "+${tags.size - MAX_VISIBLE_TAGS}")
        }
    }
}

@Composable
private fun TagChip(text: String) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

@PreviewLightDark
@Composable
fun MemoryItemCardPreview() {
    MemoriesTheme {
        MemoryItemCard(
            memoryItem = MemoryWithMediaModel(
                memory = MemoryModel(
                    title = "Trip to the Hills",
                    content = "A peaceful day in the mountains 🌄 Fog rolled in by noon and we waited it out with chai.",
                    memoryForTimeStamp = 0L,
                    favourite = true,
                ),
                tagsList = listOf(
                    TagModel(label = "travel"),
                    TagModel(label = "outdoors"),
                    TagModel(label = "family"),
                    TagModel(label = "monsoon"),
                ),
            )
        )
    }
}

@PreviewLightDark
@Composable
fun MemoryItemCardWithMediaPreview() {
    MemoriesTheme {
        MemoryItemCard(
            memoryItem = MemoryWithMediaModel(
                memory = MemoryModel(
                    title = "Trip to the Hills",
                    content = "A peaceful day in the mountains 🌄",
                    memoryForTimeStamp = 0L
                ),
                mediaList = listOf(
                    MediaModel(
                        uri = "android.resource://com.example.memories/drawable/ic_launcher_background",
                        memoryId = "",
                    )
                ),
            )
        )
    }
}

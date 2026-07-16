package com.example.memories.feature.feature_memory.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.presentation.components.MediaPager
import com.example.memories.ui.theme.MemoriesTheme

/**
 * Full screen preview for media attached to a memory that has not been saved yet.
 * Videos play inline with controls; swiping moves between the other filled slots.
 * No favourite/download/share actions here since the media has no MediaModel yet.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPreviewDialog(
    uriList: List<UriType>,
    onDismiss: () -> Unit,
    initialPage: Int = 0,
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, (uriList.size - 1).coerceAtLeast(0)),
        pageCount = { uriList.size }
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        ),
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "${pagerState.currentPage + 1} of ${uriList.size}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close preview")
                        }
                    },
                )
            },
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                MediaPager(
                    modifier = Modifier.fillMaxWidth(),
                    uris = uriList,
                    pagerState = pagerState,
                    pagerHeight = maxHeight,
                    imageContentScale = ContentScale.Fit,
                    showPlayerController = true,
                    isActive = true
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MediaPreviewDialogPreview() {
    MemoriesTheme {
        MediaPreviewDialog(
            uriList = List(3) { UriType("", Type.IMAGE_JPG) },
            onDismiss = {}
        )
    }
}

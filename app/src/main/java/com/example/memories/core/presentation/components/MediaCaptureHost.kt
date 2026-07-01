package com.example.memories.core.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.memories.core.domain.model.MediaActionType
import com.example.memories.core.domain.model.MediaType
import com.example.memories.core.domain.model.Photo
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.domain.model.Video
import com.example.memories.core.presentation.UiState
import com.example.memories.navigation.CustomNavType.mediaType
import kotlinx.coroutines.flow.flowOf

@Composable
fun MediaCaptureHost(
    tempMediaUri: String?,
    mediaActionType: MediaActionType?,
    mediaType: MediaType,
    currentPosition: Int?,
    showPickerSheet: Boolean,
    showTypeSheet: Boolean,
    onPickerSheetDismiss: () -> Unit,
    onTypeSheetDismiss: () -> Unit,
    onShowTypeSheet: () -> Unit,
    onRequestDeviceCameraUri: (MediaType) -> Unit,
    onUpdateMediaActionType: (MediaActionType) -> Unit,
    onMediaSelected: (UriType, position: Int) -> Unit,
    onWebMediaSelected: (url: String, isVideo: Boolean, position: Int) -> Unit = { _, _, _ -> },
    onNavigateToCamera: () -> Unit,
    remoteImages: LazyPagingItems<Photo> = flowOf(PagingData.from(emptyList<Photo>())).collectAsLazyPagingItems(),
    remoteVideos: LazyPagingItems<Video> = flowOf(PagingData.from(emptyList<Video>())).collectAsLazyPagingItems()
) {
    val context = LocalContext.current
    var showWebTypeSheet by remember { mutableStateOf(false) }

    val browseFilesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { safeUri ->
            currentPosition?.let {
                val uriType = UriType(
                    uri = safeUri.toString(),
                    type = Type.fromUri(
                        uri = safeUri,
                        context = context
                    )
                )
                onMediaSelected(uriType, currentPosition)
            }

        }
    }

    val onCaptureSuccess: (Boolean) -> Unit = { successful ->
        if (successful && tempMediaUri != null && currentPosition != null &&
            mediaActionType == MediaActionType.DEVICE_CAMERA
        ) {
            onMediaSelected(
                UriType(tempMediaUri, Type.fromUri(tempMediaUri.toUri(), context)),
                currentPosition
            )
            onUpdateMediaActionType(MediaActionType.NONE)
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = onCaptureSuccess
    )

    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = onCaptureSuccess
    )

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null && currentPosition != null &&
            mediaActionType == MediaActionType.PHOTO_PICKER
        ) {
            onMediaSelected(
                UriType(uri.toString(), Type.fromUri(uri, context)),
                currentPosition
            )
            onUpdateMediaActionType(MediaActionType.NONE)
        }
    }

    LaunchedEffect(tempMediaUri) {
        val uri = tempMediaUri ?: return@LaunchedEffect
        if (mediaActionType != MediaActionType.DEVICE_CAMERA) return@LaunchedEffect

        when (mediaType) {
            MediaType.IMAGE -> takePictureLauncher.launch(uri.toUri())
            MediaType.VIDEO -> captureVideoLauncher.launch(uri.toUri())
            else -> {}
        }
    }

    if (showPickerSheet) {
        MediaPickerSheet(
            onDismiss = onPickerSheetDismiss,
            onDeviceCamera = {
                onUpdateMediaActionType(MediaActionType.DEVICE_CAMERA)
                onPickerSheetDismiss()
                onShowTypeSheet()
            },
            onCustomCamera = {
                onUpdateMediaActionType(MediaActionType.CUSTOM_APP_CAMERA_FEATURE)
                onNavigateToCamera()
                onPickerSheetDismiss()
            },
            onGallery = {
                onUpdateMediaActionType(MediaActionType.PHOTO_PICKER)
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
                )
                onPickerSheetDismiss()
            },
            onWeb = {
                onUpdateMediaActionType(MediaActionType.WEB_PICKER)
                showWebTypeSheet = true
                onPickerSheetDismiss()
            },
            onInternal = {
                onUpdateMediaActionType(MediaActionType.INTERNAL)
                browseFilesLauncher.launch(
                    arrayOf("image/*", "video/*")
                )
                onPickerSheetDismiss()
            }

        )
    }

    if (showTypeSheet) {
        MediaTypeSelectorSheet(
            onDismiss = onTypeSheetDismiss,
            onSelectType = { type ->
                onRequestDeviceCameraUri(type)
                onTypeSheetDismiss()
            }
        )
    }

    if (showWebTypeSheet) {
        WebMediaSelectorSheet(
            onDismiss = { showWebTypeSheet = false },
            onMediaSelected = { url, isVideo ->
                if (currentPosition != null) {
                    onWebMediaSelected(url, isVideo, currentPosition)
                }
                onUpdateMediaActionType(MediaActionType.NONE)
                showWebTypeSheet = false
            },
            remoteImages = remoteImages,
            remoteVideos = remoteVideos
        )
    }
}


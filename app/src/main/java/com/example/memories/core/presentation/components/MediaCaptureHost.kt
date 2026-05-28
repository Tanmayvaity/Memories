package com.example.memories.core.presentation.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import com.example.memories.core.domain.model.MediaActionType
import com.example.memories.core.domain.model.MediaType
import com.example.memories.core.domain.model.Type
import com.example.memories.core.domain.model.UriType

/**
 * Reusable orchestration for the media-capture flow shared by MemoryScreen and MediaEditScreen.
 *
 * The caller owns the sheet-visibility booleans (`showPickerSheet`, `showTypeSheet`) and the slot
 * position. The host owns the three ActivityResult launchers, the conditional sheet rendering, and
 * the bridge that fires the device-camera launcher once the ViewModel has produced a `tempMediaUri`.
 *
 * Caller responsibilities:
 *  - Set `showPickerSheet = true` when the user taps an "add media" affordance (after dispatching
 *    `UpdateCurrentPosition(position)` to the host VM).
 *  - In `onRequestDeviceCameraUri`, dispatch the VM's `OpenDeviceCamera(mediaType)` event so the VM
 *    produces a writable temp URI via `GenerateSharableUriUseCase` and writes it to its state.
 *  - In `onMediaSelected`, dispatch `AddMediaUri(uriType, position)` to the VM.
 *  - In `onNavigateToCamera`, navigate to `AppScreen.Camera`. On return, observe `takenUri` from
 *    savedStateHandle and dispatch `AddMediaUri` yourself (this host does not handle the
 *    custom-camera return path — see MemoryRoot/MediaEditRoot for the pattern).
 */
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
    onNavigateToCamera: () -> Unit,
) {
    val context = LocalContext.current

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
}

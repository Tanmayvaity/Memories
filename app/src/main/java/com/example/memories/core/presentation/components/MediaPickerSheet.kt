package com.example.memories.core.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPickerSheet(
    onDismiss: () -> Unit,
    onDeviceCamera: () -> Unit,
    onCustomCamera: () -> Unit,
    onGallery: () -> Unit,
    onWeb: () -> Unit
) {
    ActionSelectorBottomSheet(
        onDismiss = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetTitle = "Media Picker",
        showLoading = false,
        loadingItemIndex = null,
        items = listOf(
            MenuItem(
                title = "Use Device Camera",
                icon = R.drawable.ic_camera,
                content = "Open system default camera app",
                onClick = onDeviceCamera
            ),
            MenuItem(
                title = "Use Memories Camera",
                icon = R.drawable.ic_aperture,
                content = "Capture with Memories' custom camera",
                onClick = onCustomCamera
            ),
            MenuItem(
                title = "Choose from Gallery",
                icon = R.drawable.ic_feed,
                content = "Select from your device photos",
                onClick = onGallery
            ),
            MenuItem(
                title = "Choose from the web",
                icon = R.drawable.ic_web,
                content = "Select media from the pexels api",
                onClick = onWeb
            )
        )
    )
}

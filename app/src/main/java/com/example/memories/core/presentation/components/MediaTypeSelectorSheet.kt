package com.example.memories.core.presentation.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.example.memories.R
import com.example.memories.core.domain.model.MediaType
import com.example.memories.core.presentation.MenuItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaTypeSelectorSheet(
    onDismiss: () -> Unit,
    onSelectType: (MediaType) -> Unit,
) {
    ActionSelectorBottomSheet(
        onDismiss = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetTitle = "What would you like to capture?",
        showLoading = false,
        loadingItemIndex = null,
        items = listOf(
            MenuItem(
                title = "Take a Photo",
                icon = R.drawable.ic_camera,
                content = "Snap a quick picture",
                onClick = { onSelectType(MediaType.IMAGE) }
            ),
            MenuItem(
                title = "Record a Video",
                icon = R.drawable.ic_video,
                content = "Capture a moving moment",
                onClick = { onSelectType(MediaType.VIDEO) }
            )
        )
    )
}

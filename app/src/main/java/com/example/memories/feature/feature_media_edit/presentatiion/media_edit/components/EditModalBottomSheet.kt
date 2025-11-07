package com.example.memories.feature.feature_media_edit.presentatiion.media_edit.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.ui.theme.MemoriesTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditModalBottomSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onDismiss()
        },
    ) {
        val actionList = getEditMenuItems()

        EditList(menuItems = actionList)

    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun EditModalBottomSheetPreview(){
    MemoriesTheme {
        EditModalBottomSheet()
    }
}
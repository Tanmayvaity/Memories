package com.example.memories.core.presentation.components

import android.R.attr.onClick
import android.R.attr.textColor
import android.R.id.message
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.core.presentation.ActionItem
import com.example.memories.core.presentation.MenuItem
import com.example.memories.ui.theme.MemoriesTheme
import kotlin.collections.forEachIndexed
import com.example.memories.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentActionSheet(
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: String,
    actionList: List<MenuItem> = emptyList()
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState

    ) {
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(vertical = 10.dp, horizontal = 15.dp)
        ) {
            Text(
                text = title.toString(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(3.dp)
            )
            actionList.forEachIndexed { index, menuItem ->
                ActionItem(
                    onClick = {
                        menuItem.onClick()
                    },
                    backgroundColor = Color.Transparent,
                    itemText = menuItem.title,
                    icon = menuItem.icon!!,
                    iconContentDescription = menuItem.iconContentDescription,
                    iconColor = if (index == 2) Color.Red else MaterialTheme.colorScheme.onSurface,
                    textColor = if (index == 2) Color.Red else MaterialTheme.colorScheme.onSurface,
                )
            }


        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentActionSheetPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        ContentActionSheet(
            title = "Just a Title",
            actionList =
                listOf(
                    MenuItem(
                        title = "Like",
                        icon = R.drawable.ic_favourite,
                        iconContentDescription = "Favourite icon",
                        onClick = {}
                    ),
                    MenuItem(
                        title = "Hide",
                        icon = R.drawable.ic_hidden,
                        iconContentDescription = "Favourite icon",
                        onClick = {}
                    ),
                    MenuItem(
                        title = "Delete",
                        icon = R.drawable.ic_delete,
                        iconContentDescription = "Delete icon",
                        onClick = {}

                    )
                )
        )
    }
}
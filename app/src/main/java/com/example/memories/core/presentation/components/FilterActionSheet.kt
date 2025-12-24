package com.example.memories.core.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.LocalTheme
import com.example.memories.core.presentation.MenuItem
import com.example.memories.ui.theme.MemoriesTheme
import kotlin.collections.forEachIndexed
import com.example.memories.R
import com.example.memories.feature.feature_feed.domain.model.OrderByType
import com.example.memories.feature.feature_feed.domain.model.toIndex
import com.example.memories.feature.feature_feed.presentation.feed.FeedState
import com.example.memories.feature.feature_feed.presentation.feed.components.CardList
import com.example.memories.feature.feature_feed.presentation.feed.components.ChipRow
import com.example.memories.ui.theme.VeryDarkGray
import com.example.memories.ui.theme.VeryLightGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterActionSheet(
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: String,
    onReset: () -> Unit = {},
    showActionList: List<MenuItem> = emptyList(),
    sortByActionList: List<MenuItem> = emptyList(),
    orderByActionList: List<MenuItem> = emptyList(),
    modifier: Modifier = Modifier,
    onApplyFilter: () -> Unit = {},
    state: FeedState = FeedState()
) {
    val theme = LocalTheme.current
    val currentSelectedSortItem = remember(state.sortType) { state.sortType }
    val currentSelectedFetchItem = remember(state.type) { state.type }
    val currentSelectedOrderByItem = remember(state.orderByType) { state.orderByType }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState,
        containerColor = if (theme) VeryDarkGray else VeryLightGray
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(vertical = 10.dp, horizontal = 20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeadingText(
                    modifier = Modifier
                        .weight(1f)
                        .padding(3.dp),
                    title = title
                )
                TextButton(
                    onClick = onReset
                ) {
                    Text(
                        text = "Reset",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HeadingText(
                title = "Show",
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(3.dp, top = 16.dp)
            )
            ChipRow(
                items = showActionList,
                selectedItemIndex = currentSelectedFetchItem.toIndex(),
                modifier = Modifier.padding(vertical = 10.dp)
            )
            HeadingText(
                title = "Sort By",
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(3.dp, top = 16.dp, bottom = 10.dp)
            )
            CardList(
                items = sortByActionList,
                selectedIndex = currentSelectedSortItem.toIndex(),
                onItemClick = { item ->
                    item.onClick()
                }
            )
            HeadingText(
                title = "Order By",
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(3.dp, top = 16.dp, bottom = 10.dp)
            )
            CardList(
                items = orderByActionList,
                selectedIndex = currentSelectedOrderByItem.toIndex(),
                onItemClick = { item -> item.onClick() }

            )
            Button(
                onClick = onApplyFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 64.dp),
            ) {
                Text(
                    text = "Apply Filters",
                    modifier = Modifier
                        .padding(12.dp)
                )
            }


        }
    }
}


@Composable
fun HeadingText(
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleLarge,
    title: String,
) {
    Text(
        text = title.toString(),
        style = textStyle,
        fontWeight = FontWeight.Bold,
        modifier = modifier

    )
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentActionSheetPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        FilterActionSheet(
            title = "Filter & Sort Posts",
            showActionList =
                listOf(
                    MenuItem(
                        title = "All",
                        icon = R.drawable.ic_favourite,
                        iconContentDescription = "Favourite icon",
                        onClick = {}
                    ),
                    MenuItem(
                        title = "Favorite",
                        icon = R.drawable.ic_favourite,
                        iconContentDescription = "Favourite icon",
                        onClick = {}
                    ),
                    MenuItem(
                        title = "Hidden",
                        icon = R.drawable.ic_hidden,
                        iconContentDescription = "Hidden icon",
                        onClick = {}

                    )
                ),
            sortByActionList =
                listOf(
                    MenuItem(
                        title = "Date",
                        icon = R.drawable.ic_timer,
                        iconContentDescription = "Favourite icon",
                        onClick = {}
                    ),
                    MenuItem(
                        title = "Favorite",
                        icon = R.drawable.ic_favourite,
                        iconContentDescription = "Favourite icon",
                        onClick = {}
                    ),
                    MenuItem(
                        title = "Hidden",
                        icon = R.drawable.ic_hidden,
                        iconContentDescription = "Hidden icon",
                        onClick = {}

                    )
                ),
            orderByActionList =
                listOf(
                    MenuItem(
                        title = "Ascending",
                        icon = R.drawable.ic_up,
                        iconContentDescription = "up icon",
                        onClick = {}
                    ),
                    MenuItem(
                        title = "Descending",
                        icon = R.drawable.ic_down,
                        iconContentDescription = "down icon",
                        onClick = {}
                    ),

                )

        )
    }
}
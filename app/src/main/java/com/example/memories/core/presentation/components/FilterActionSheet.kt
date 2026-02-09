package com.example.memories.core.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.LocalTheme
import com.example.memories.ui.theme.MemoriesTheme
import com.example.memories.core.util.SmallPhonePreview
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import com.example.memories.feature.feature_feed.presentation.feed.FeedState
import com.example.memories.feature.feature_feed.presentation.feed.components.CardList
import com.example.memories.feature.feature_feed.presentation.feed.components.ChipRow
import kotlin.enums.EnumEntries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterActionSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: String,
    onReset: () -> Unit = {},
    fetchTypeEntries : EnumEntries<FetchType> = FetchType.entries,
    onFetchTypeClick : (FetchType) -> Unit = {},
    sortByEntries : EnumEntries<SortType> = SortType.entries,
    onSortByClick : (SortType) -> Unit = {},
    orderByEntries : EnumEntries<SortOrder> = SortOrder.entries,
    onOrderByClick : (SortOrder) -> Unit = {},
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
        containerColor = MaterialTheme.colorScheme.surface
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
                items = fetchTypeEntries.toList() ,
                selectedItemIndex = currentSelectedFetchItem.ordinal,
                modifier = Modifier.padding(vertical = 10.dp),
                onItemClick = { type ->
                    onFetchTypeClick(type)
                }
            )
            HeadingText(
                title = "Sort By",
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(3.dp, top = 16.dp, bottom = 10.dp)
            )
            CardList(
                items = sortByEntries.toList(),
                selectedIndex = currentSelectedSortItem.ordinal,
                onItemClick = { item ->
                    onSortByClick(item as SortType)
                }
            )
            HeadingText(
                title = "Order By",
                textStyle = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(3.dp, top = 16.dp, bottom = 10.dp)
            )
            CardList(
                items = orderByEntries.toList(),
                selectedIndex = currentSelectedOrderByItem.ordinal,
                onItemClick = { item ->
                    onOrderByClick(item as SortOrder)
                }

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



@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentActionSheetPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        FilterActionSheet(
            title = "Filter & Sort Posts",
            fetchTypeEntries = FetchType.entries,
            sortByEntries = SortType.entries,
            orderByEntries = SortOrder.entries,
        )
    }
}
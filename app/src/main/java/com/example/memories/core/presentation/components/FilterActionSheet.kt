package com.example.memories.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.FilterOption
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import com.example.memories.feature.feature_feed.presentation.feed.FeedState
import com.example.memories.ui.theme.MemoriesTheme
import kotlin.enums.EnumEntries

/**
 * Feed filter and sort sheet. Styled like the Tags screen's sort sheet: one [SelectableOptionRow]
 * per option, grouped under Show / Sort By / Order By.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterActionSheet(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    title: String,
    onReset: () -> Unit = {},
    fetchTypeEntries: EnumEntries<FetchType> = FetchType.entries,
    onFetchTypeClick: (FetchType) -> Unit = {},
    sortByEntries: EnumEntries<SortType> = SortType.entries,
    onSortByClick: (SortType) -> Unit = {},
    orderByEntries: EnumEntries<SortOrder> = SortOrder.entries,
    onOrderByClick: (SortOrder) -> Unit = {},
    onApplyFilter: () -> Unit = {},
    state: FeedState = FeedState()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onReset) {
                    Text(text = "Reset")
                }
            }

            FilterSection(
                title = "Show",
                options = fetchTypeEntries,
                selected = state.type,
                onSelect = onFetchTypeClick,
                topSpacing = 16.dp,
            )
            FilterSection(
                title = "Sort By",
                options = sortByEntries,
                selected = state.sortType,
                onSelect = onSortByClick,
            )
            FilterSection(
                title = "Order By",
                options = orderByEntries,
                selected = state.orderByType,
                onSelect = onOrderByClick,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onApplyFilter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Text(text = "Apply Filters")
            }
        }
    }
}

@Composable
private fun <T : FilterOption> FilterSection(
    title: String,
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    topSpacing: Dp = 24.dp,
) {
    Spacer(modifier = Modifier.height(topSpacing))
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.forEach { option ->
            SelectableOptionRow(
                title = option.title,
                description = option.description,
                icon = option.icon,
                isSelected = option == selected,
                onSelect = { onSelect(option) }
            )
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

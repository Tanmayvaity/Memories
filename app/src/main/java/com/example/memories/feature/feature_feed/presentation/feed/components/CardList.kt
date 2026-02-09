package com.example.memories.feature.feature_feed.presentation.feed.components

import android.R.attr.onClick
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem
import com.example.memories.core.presentation.components.IconItem
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.FilterOption
import com.example.memories.ui.theme.MemoriesTheme
import kotlin.collections.forEachIndexed
import kotlin.enums.EnumEntries

@Composable
fun <T : FilterOption>CardList(
    items: List<T>,
    selectedIndex: Int,
    onItemClick: (FilterOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(5.dp)
        ) {
            items.forEachIndexed { index, item ->
                RowItem(
                    item = item,
                    selected = index == selectedIndex,
                    onClick = {
                        onItemClick(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun <T : FilterOption>RowItem(
    item: T,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable(onClick = onClick, interactionSource = interactionSource, indication = null)
    ) {
        IconItem(
            drawableRes = item.icon,
            contentDescription = item.description,
            alpha = 0f,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = item.title,
            modifier = Modifier
                .weight(1f)
                .padding(start = 5.dp)
        )

        RadioButton(
            selected = selected,
            onClick = onClick,
            modifier = Modifier.clip(CircleShape)
        )
    }
}




@Preview
@Composable
fun CardListPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {
        CardList(
            items = FetchType.entries.toList(),
            selectedIndex = 0,
            onItemClick = {},
        )
    }
}
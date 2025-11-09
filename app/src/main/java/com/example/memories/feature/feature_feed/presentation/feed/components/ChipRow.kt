package com.example.memories.feature.feature_feed.presentation.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.memories.R
import com.example.memories.core.presentation.MenuItem
import com.example.memories.ui.theme.MemoriesTheme

@Composable
fun ChipRow(
    modifier: Modifier = Modifier,
    items: List<MenuItem> = emptyList(),
    selectedItemIndex: Int = 0
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        items.forEachIndexed { index, item ->
            FilterChip(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(48.dp)
                    .weight(1f)
                ,
                selected = selectedItemIndex == index,
                onClick = { item.onClick() },
                label = {
                    Text(
                        text = item.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                },
                leadingIcon = {
                    if (item.icon == -1) return@FilterChip
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = null
                    )

                },
            )
        }
    }
}

@Preview
@Composable
fun ChipRowPreview(modifier: Modifier = Modifier) {
    MemoriesTheme {

        ChipRow(
            selectedItemIndex = 1,
            items = listOf<MenuItem>(
                MenuItem(
                    title = "All",
                    onClick = {
//                                onEvent(FeedEvents.FetchFeed)

                    },
                    iconContentDescription = "",
                    icon = -1
                ),
                MenuItem(
                    title = "Favorite",
                    onClick = {

//                                onEvent(FeedEvents.FetchFeed)
                    },
                    iconContentDescription = "",
                    icon = R.drawable.ic_favourite_filled
                ),
                MenuItem(
                    title = "Hidden",
                    onClick = {

//                                onEvent(FeedEvents.FetchFeed)
                    },
                    iconContentDescription = "",
                    icon = R.drawable.ic_not_hidden
                ),


                )
        )

    }
}
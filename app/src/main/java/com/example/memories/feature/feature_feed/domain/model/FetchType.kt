package com.example.memories.feature.feature_feed.domain.model

import androidx.annotation.DrawableRes
import com.example.memories.R
import java.util.logging.Filter

interface FilterOption {
    val title: String
    val description: String
    @get:DrawableRes
    val icon: Int
}

enum class FetchType(
    override val title: String,
    override val description: String,
    override val icon: Int
) : FilterOption {
    ALL("All", "All Memories", R.drawable.ic_feed),
    FAVORITE("Favorite", "Favorite Memories", R.drawable.ic_favourite),
//    HIDDEN("Hidden", "Hidden Memories", R.drawable.ic_hidden)
}

enum class SortType(
    override val title: String,
    override val description: String,
    override val icon: Int
) : FilterOption {
    CreatedForDate("Created For Date", "Sort Memories by Date Created", R.drawable.ic_calender),
    DateAdded("Date Added", "Sort Memories by Date Added", R.drawable.ic_timer),
    Title("Title", "Sort Memories by Title", R.drawable.ic_title)
}

enum class SortOrder(
    override val title: String,
    override val description: String,
    override val icon: Int
) : FilterOption {
    Ascending("Ascending", "Low to High", R.drawable.ic_up),
    Descending("Descending", "High to Low", R.drawable.ic_down)
}

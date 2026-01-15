package com.example.memories.feature.feature_feed.presentation.tags

import androidx.annotation.DrawableRes
import com.example.memories.R

enum class SortBy(
    val title: String,
    val description: String,
    @DrawableRes val icon: Int
) {
    Count("Count", "Number of memories", R.drawable.ic_count),
    Label("Label", "Alphabetical (A-Z)", R.drawable.ic_language)
}


package com.example.memories.feature.feature_feed.presentation.tags

sealed class TagEvents {
    data class Fetch(
        val sortBy: SortBy,
        val orderBy : SortOrder
    ) : TagEvents()

    data class DeleteTag(val id : String): TagEvents()
}
package com.example.memories.feature.feature_feed.presentation.tags

import com.example.memories.feature.feature_feed.domain.model.SortOrder

sealed class TagEvents {
    data class ChangeSortOrderBy(val type : SortOrder) : TagEvents()

    data class ChangeSortBy(val type : SortBy) : TagEvents()

    data class DeleteTag(val id : String): TagEvents()

    object ApplyFilter : TagEvents()

    data class InputTextChange(val value : String) : TagEvents()

    data class CreateTag(val name : String) : TagEvents()
}
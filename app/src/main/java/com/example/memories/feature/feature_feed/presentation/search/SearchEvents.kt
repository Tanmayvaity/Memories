package com.example.memories.feature.feature_feed.presentation.search

import com.example.memories.core.domain.model.TagModel

sealed class SearchEvents {
    data class InputTextChange(val input : String) : SearchEvents()
    object ClearInput : SearchEvents()

    data class AddSearch(val memoryId : String) : SearchEvents()

    data class SelectTag(val tag : TagModel) : SearchEvents()

    data class DeleteSearch(val memoryId : String) : SearchEvents()

    object DeleteAllSearch : SearchEvents()
}
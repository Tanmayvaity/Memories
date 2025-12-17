package com.example.memories.feature.feature_feed.presentation.feed

import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.feature.feature_feed.domain.model.FetchType

sealed class FeedEvents {
    object FetchFeed : FeedEvents()

    data class ChangeFetchType(val type : FetchType) : FeedEvents()

    object Refresh : FeedEvents()

    data class ToggleFavourite(val id : String,val isFav: Boolean) : FeedEvents()
    data class ToggleHidden(val id : String,val isHidden : Boolean): FeedEvents()

    data class Delete(val memory : MemoryModel,val uriList : List<String>) : FeedEvents()
}



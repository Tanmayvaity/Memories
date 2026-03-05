package com.example.memories.feature.feature_feed.presentation.feed

import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import com.example.memories.feature.feature_feed.presentation.common.MemoryAction

sealed class FeedEvents {
    data class ChangeFetchType(val type : FetchType) : FeedEvents()

    data class ChangeSortType(val type : SortType) : FeedEvents()
    data class ChangeSortOrderBy(val type : SortOrder) : FeedEvents()
    object  ResetFilterState : FeedEvents()
    object Refresh : FeedEvents()

    data class Action(val action: MemoryAction) : FeedEvents()

    object ApplyFilter : FeedEvents()
}



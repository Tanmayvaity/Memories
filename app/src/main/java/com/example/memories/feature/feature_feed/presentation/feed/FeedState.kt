package com.example.memories.feature.feature_feed.presentation.feed

import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType

data class FeedState(
    val type : FetchType = FetchType.ALL,
    val sortType : SortType = SortType.DateAdded,
    val orderByType: SortOrder = SortOrder.Descending,
    val isDeleting : Boolean = false
)

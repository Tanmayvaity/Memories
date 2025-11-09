package com.example.memories.feature.feature_feed.presentation.feed

import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType

data class FeedState(
    val memories : List<MemoryWithMediaModel> = emptyList(),
    val isLoading : Boolean = false,
    val error : String? = null,
    val type : FetchType = FetchType.ALL

)

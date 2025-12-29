package com.example.memories.feature.feature_feed.presentation.tags_with_memory

import com.example.memories.core.domain.model.MemoryWithMediaModel

data class TagWithMemoryState(
    val isLoading : Boolean = false,
    val memories : List<MemoryWithMediaModel> = emptyList(),
    val error : String? = null,
    val label : String? = null,
)
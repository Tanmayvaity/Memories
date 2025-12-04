package com.example.memories.feature.feature_memory.presentation

import com.example.memories.core.domain.model.TagModel

data class MemoryState(
    val title : String = "",
    val content : String = "",
    val isTitleHintVisible : Boolean = true,
    val titleHintContent : String = "Write Title",
    val contentHintContent : String = "Write your story...",
    val isContentHintVisible : Boolean = true,
    val totalNumberOfTags : List<TagModel> = emptyList(),
    val tagsSelectedForThisMemory : List<TagModel> = emptyList(),
    val tagTextFieldValue : String = "",
)

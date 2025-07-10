package com.example.memories.feature.feature_memory.presentation

data class MemoryState(
    val title : String = "",
    val content : String = "",
    val isTitleHintVisible : Boolean = true,
    val titleHintContent : String = "Write Title",
    val contentHintContent : String = "Write your story...",
    val isContentHintVisible : Boolean = true
)
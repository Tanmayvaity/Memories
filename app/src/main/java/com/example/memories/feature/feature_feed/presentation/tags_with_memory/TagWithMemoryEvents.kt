package com.example.memories.feature.feature_feed.presentation.tags_with_memory

sealed class TagWithMemoryEvents {
    data class Fetch(val id : String) : TagWithMemoryEvents()
}
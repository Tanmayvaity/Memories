package com.example.memories.feature.feature_feed.presentation.feed_detail

sealed class MemoryDetailEvents {
    data class Fetch(val id:String): MemoryDetailEvents()
    data class Favourite(val id : String) : MemoryDetailEvents()
    data class UnFavourite(val id:String) : MemoryDetailEvents()
}
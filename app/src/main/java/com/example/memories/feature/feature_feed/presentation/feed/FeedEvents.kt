package com.example.memories.feature.feature_feed.presentation.feed

sealed class FeedEvents {
    object FetchFeed : FeedEvents()

    object Refresh : FeedEvents()

    data class ToggleFavourite(val id : String) : FeedEvents()
    data class ToggleHidden(val id : String): FeedEvents()
}
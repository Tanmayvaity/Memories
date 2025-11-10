package com.example.memories.feature.feature_feed.presentation.search

sealed class SearchEvents {
    data class InputTextChange(val input : String) : SearchEvents()
    object ClearInput : SearchEvents()
}
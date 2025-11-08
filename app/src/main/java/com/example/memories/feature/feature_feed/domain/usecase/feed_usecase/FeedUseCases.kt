package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.GetFeedUseCase

data class FeedUseCases(
    val getFeedUseCase: GetFeedUseCase,
    val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    val toggleHiddenUseCase: ToggleHiddenUseCase,
    val getMemoryByIdUseCase: GetMemoryByIdUseCase
)
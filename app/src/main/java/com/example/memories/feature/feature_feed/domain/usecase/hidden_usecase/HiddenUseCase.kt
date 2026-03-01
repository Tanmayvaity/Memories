package com.example.memories.feature.feature_feed.domain.usecase.hidden_usecase

import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.DeleteUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.ToggleFavouriteUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.ToggleHiddenUseCase

data class HiddenUseCase(
    val getHiddenFeedUseCase: GetHiddenFeedUseCase,
    val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    val toggleHiddenUseCase: ToggleHiddenUseCase,
    val deleteMemoryUseCase: DeleteUseCase,
)
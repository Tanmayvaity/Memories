package com.example.memories.feature.feature_feed.domain.usecase.history_usecase

import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.GetFeedUseCase

data class HistoryUseCaseWrapper(
    val fetchTodayMemoriesUseCase: FetchTodayMemoriesUseCase,
    val fetchAllMemories : GetFeedUseCase
)
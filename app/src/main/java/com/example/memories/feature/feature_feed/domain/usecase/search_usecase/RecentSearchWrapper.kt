package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

data class RecentSearchWrapper(
    val saveSearchIdUseCase: SaveSearchIdUseCase,
    val fetchRecentSearchUseCase: FetchRecentSearchUseCase
)
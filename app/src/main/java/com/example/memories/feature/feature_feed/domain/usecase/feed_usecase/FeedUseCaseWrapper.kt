package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

data class FeedUseCaseWrapper(
    val getFeedUseCase: GetFeedUseCase,
    val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    val toggleHiddenUseCase: ToggleHiddenUseCase,
    val getMemoryByIdUseCase: GetMemoryByIdUseCase,
    val deleteMemoryUseCase: DeleteUseCase,
    val searchByTitleUseCase: SearchByTitleUseCase
)
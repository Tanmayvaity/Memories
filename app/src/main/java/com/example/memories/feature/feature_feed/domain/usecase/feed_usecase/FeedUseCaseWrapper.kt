package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.core.domain.usecase.FetchTagUseCase
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.FetchMemoryByTagUseCase
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.FetchOnThisDataUseCase

data class FeedUseCaseWrapper(
    val getFeedUseCase: GetFeedUseCase,
    val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    val toggleHiddenUseCase: ToggleHiddenUseCase,
    val getMemoryByIdUseCase: GetMemoryByIdUseCase,
    val deleteMemoryUseCase: DeleteUseCase,
    val searchByTitleUseCase: SearchByTitleUseCase,
    val fetchTagUseCase: FetchTagUseCase,
    val fetchMemoryByTagUseCase: FetchMemoryByTagUseCase,
    val fetchOnThisDataUseCase: FetchOnThisDataUseCase
)
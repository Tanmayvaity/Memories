package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import com.example.memories.core.domain.usecase.FetchTagUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.GetMemoryByIdUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.SearchByTitleUseCase

data class SearchUseCase(
    val fetchMemoryByTagUseCase: FetchMemoryByTagUseCase,
    val fetchOnThisDayUseCase: FetchOnThisDayUseCase,
    val fetchRecentSearchUseCase: FetchRecentSearchUseCase,
    val saveSearchIdUseCase: SaveSearchIdUseCase,
    val searchByTitleUseCase: SearchByTitleUseCase,
    val fetchTagUseCase: FetchTagUseCase,
    val getMemoryByIdUseCase: GetMemoryByIdUseCase,
    val fetchRecentMemoriesUseCase: FetchRecentMemoriesUseCase
)
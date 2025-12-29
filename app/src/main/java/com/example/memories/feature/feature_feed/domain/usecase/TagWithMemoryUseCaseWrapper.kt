package com.example.memories.feature.feature_feed.domain.usecase

import com.example.memories.core.domain.usecase.DeleteTagUseCase
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.FetchMemoryByTagUseCase

data class TagWithMemoryUseCaseWrapper(
    val fetchMemoryByTagUseCase: FetchMemoryByTagUseCase,
    val deleteTagUseCase: DeleteTagUseCase,
)
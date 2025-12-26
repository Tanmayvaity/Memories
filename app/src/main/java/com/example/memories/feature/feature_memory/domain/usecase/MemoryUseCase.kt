package com.example.memories.feature.feature_memory.domain.usecase

import com.example.memories.core.domain.usecase.AddTagUseCase
import com.example.memories.core.domain.usecase.DeleteTagUseCase
import com.example.memories.core.domain.usecase.FetchTagUseCase
import com.example.memories.core.domain.usecase.FetchTagsByLabelUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.GetMemoryByIdUseCase

data class MemoryUseCase(
    val createMemoryUseCase : MemoryCreateUseCase,
    val fetchTagUseCase: FetchTagUseCase,
    val addTagUseCase : AddTagUseCase,
    val fetchTagsByLabelUseCase: FetchTagsByLabelUseCase,
    val fetchMemoryByIdUseCase : GetMemoryByIdUseCase,
    val updateMemoryUseCase : MemoryUpdateUseCase,
    val tagDeleteTagUseCase: DeleteTagUseCase
)
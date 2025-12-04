package com.example.memories.feature.feature_memory.domain.usecase

data class MemoryUseCase(
    val createMemoryUseCase : MemoryCreateUseCase,
    val fetchTagUseCase: FetchTagUseCase,
    val addTagUseCase : AddTagUseCase,
    val fetchTagsByLabelUseCase: FetchTagsByLabelUseCase
)
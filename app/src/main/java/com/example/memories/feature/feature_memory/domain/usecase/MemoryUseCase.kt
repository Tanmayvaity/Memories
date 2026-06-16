package com.example.memories.feature.feature_memory.domain.usecase

import com.example.memories.core.domain.usecase.AddTagUseCase
import com.example.memories.core.domain.usecase.DeleteTagUseCase
import com.example.memories.core.domain.usecase.FetchRemoteImagesUseCase
import com.example.memories.core.domain.usecase.FetchRemoteVideosUseCase
import com.example.memories.core.domain.usecase.FetchTagUseCase
import com.example.memories.core.domain.usecase.FetchTagsByLabelUseCase
import com.example.memories.core.domain.usecase.GenerateSharableUriUseCase
import com.example.memories.core.domain.usecase.GetMemoryByIdUseCase
import com.example.memories.core.domain.usecase.SaveRemoteMediaUseCase

data class MemoryUseCase(
    val createMemoryUseCase : MemoryCreateUseCase,
    val fetchTagUseCase: FetchTagUseCase,
    val addTagUseCase : AddTagUseCase,
    val fetchTagsByLabelUseCase: FetchTagsByLabelUseCase,
    val fetchMemoryByIdUseCase : GetMemoryByIdUseCase,
    val updateMemoryUseCase : MemoryUpdateUseCase,
    val tagDeleteTagUseCase: DeleteTagUseCase,
    val generateSharableUriUseCase: GenerateSharableUriUseCase,
    val suggestTagsUseCase: SuggestTagsUseCase,
    val fetchRemoteImagesUseCase: FetchRemoteImagesUseCase,
    val fetchRemoteVideosUseCase: FetchRemoteVideosUseCase,
    val saveRemoteMediaUseCase: SaveRemoteMediaUseCase
)
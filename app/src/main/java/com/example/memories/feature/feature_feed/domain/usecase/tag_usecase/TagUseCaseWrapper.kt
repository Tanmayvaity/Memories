package com.example.memories.feature.feature_feed.domain.usecase.tag_usecase

import com.example.memories.core.domain.usecase.AddTagUseCase
import com.example.memories.core.domain.usecase.DeleteTagUseCase

data class TagUseCaseWrapper(
    val getTagsWithMemoryCountUseCase: GetTagsWithMemoryCountUseCase,
    val deleteTagUseCase: DeleteTagUseCase,
    val getTagsWithMemoryCountBySearchUseCase: GetTagsWithMemoryCountBySearchUseCase,
    val addTagUseCase : AddTagUseCase
)
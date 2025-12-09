package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchTagsByLabelUseCase @Inject constructor(
    private val tagRepository: TagRepository
) {
    suspend operator fun invoke(label : String) : Flow<List<TagModel>> {
        return tagRepository.fetchTagsByLabel(label)
    }

}
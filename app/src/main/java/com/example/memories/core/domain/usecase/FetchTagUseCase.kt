package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchTagUseCase @Inject constructor(
    val tagRepository: TagRepository
){
     operator fun invoke(): Flow<List<TagModel>>{
        return tagRepository.fetchTags()

    }

    companion object{
        private const val TAG = "FetchTagUseCase"
    }
}

package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.repository.TagRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchTagUseCase @Inject constructor(
    val tagRepository: TagRepository
){
    suspend operator fun invoke(): Result<Flow<List<TagModel>>>{
         try {
             val tags = tagRepository.fetchTags()
             return Result.Success(tags)
         }catch (e : Exception){
             return Result.Error(e)
         }

    }

    companion object{
        private const val TAG = "FetchTagUseCase"
    }
}

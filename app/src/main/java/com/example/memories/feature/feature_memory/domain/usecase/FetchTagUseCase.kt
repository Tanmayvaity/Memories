package com.example.memories.feature.feature_memory.domain.usecase

import android.util.Log
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.TagModel
import com.example.memories.feature.feature_memory.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchTagUseCase @Inject constructor(
    val memoryRepository: MemoryRepository
){
    suspend operator fun invoke(): Result<Flow<List<TagModel>>>{
         try {
             val tags = memoryRepository.fetchTags()
             return Result.Success(tags)
         }catch (e : Exception){
             return Result.Error(e)
         }

    }

    companion object{
        private const val TAG = "FetchTagUseCase"
    }
}

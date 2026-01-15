package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import android.R.attr.type
import androidx.paging.PagingData
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.TagsWithMemoryModel
import com.example.memories.core.domain.repository.MemoryRepository
import com.example.memories.feature.feature_feed.domain.model.FetchType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest

class FetchMemoryByTagUseCase(
    private val repository: MemoryRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
     operator fun invoke(tagId : String): Flow<PagingData<MemoryWithMediaModel>>{
        return repository.getMemoryByTag(tagId)
    }

}
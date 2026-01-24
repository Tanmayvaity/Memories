package com.example.memories.feature.feature_feed.domain.usecase.search_usecase

import android.R.attr.type
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.SearchModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow

class DeleteAllSearchUseCase(
    private val repository: RecentSearchRepository
) {
    suspend operator fun invoke(){
        repository.deleteAllSearch()
    }

}
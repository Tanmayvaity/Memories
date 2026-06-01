package com.example.memories.feature.feature_other.domain.usecase

import androidx.paging.PagingData
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow

class GetAllMediaPagedUseCase(
    private val repository: MemoryRepository
) {
    operator fun invoke(showHidden: Boolean = false): Flow<PagingData<MediaModel>> {
        return repository.getAllMediaPaged(showHidden)
    }
}

package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.core.domain.repository.MemoryRepository

class ToggleHiddenUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(id : String,isHidden: Boolean){
        return repository.updateHiddenState(id,isHidden)
    }


}
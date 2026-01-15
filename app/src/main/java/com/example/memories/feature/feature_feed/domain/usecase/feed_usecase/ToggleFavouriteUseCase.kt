package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.core.domain.repository.MemoryRepository

class ToggleFavouriteUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(id : String,isFavourite: Boolean){
        return repository.updateFavouriteState(id,isFavourite)
    }


}
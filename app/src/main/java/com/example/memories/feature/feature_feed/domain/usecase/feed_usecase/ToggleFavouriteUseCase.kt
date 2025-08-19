package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.feature.feature_feed.domain.repository.FeedRepository

class ToggleFavouriteUseCase(
    private val repository: FeedRepository
) {
    suspend operator fun invoke(id : String,isFavourite: Boolean){
        return repository.updateFavouriteState(id,isFavourite)
    }


}
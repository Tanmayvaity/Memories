package com.example.memories.feature.feature_feed.domain.usecase.feed_usecase

import com.example.memories.feature.feature_feed.domain.repository.FeedRepository

class ToggleHiddenUseCase(
    private val repository: FeedRepository
) {
    suspend operator fun invoke(id : String,isHidden: Boolean){
        return repository.updateHiddenState(id,isHidden)
    }


}
package com.example.memories.core.domain.usecase

import com.example.memories.core.domain.repository.MemoryRepository

class ToggleMediaFavouriteUseCase(
    private val repository: MemoryRepository
) {
    suspend operator fun invoke(mediaId: String, currentFavouriteState: Boolean) {
        repository.updateMediaFavouriteState(mediaId, !currentFavouriteState)
    }
}
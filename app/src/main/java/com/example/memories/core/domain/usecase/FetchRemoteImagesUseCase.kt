package com.example.memories.core.domain.usecase

import android.util.Log
import androidx.paging.PagingData
import com.example.memories.core.domain.model.Photo
import com.example.memories.core.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class FetchRemoteImagesUseCase(
    private val repository: MediaRepository
) {
    operator fun invoke(): Flow<PagingData<Photo>> {
        return repository.getRemoteImages()
    }
}
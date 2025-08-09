package com.example.memories.feature.feature_feed.domain.usecase

import android.net.Uri
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository

class DeleteMediaUseCase(
    val repository : MediaFeedRepository
) {
    suspend operator fun invoke(
        uri : Uri
    ){
        repository.delete(uri)
    }

}
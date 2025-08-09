package com.example.memories.feature.feature_feed.domain.usecase

import android.net.Uri
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository

class DeleteMediasUseCase(
    val repository : MediaFeedRepository
) {
    suspend operator fun invoke(
        uriList : List<Uri>
    ){
        repository.deleteMedias(uriList)
    }

}
package com.example.memories.feature.feature_feed.domain.usecaase

import android.net.Uri
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class DeleteMediaUseCase(
    val repository : MediaFeedRepository
) {
    suspend operator fun invoke(
        uri : Uri
    ){
        repository.delete(uri)
    }

}
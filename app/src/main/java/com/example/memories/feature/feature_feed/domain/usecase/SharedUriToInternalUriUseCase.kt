package com.example.memories.feature.feature_feed.domain.usecase

import android.net.Uri
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import java.io.File

class SharedUriToInternalUriUseCase(
    val repository : MediaFeedRepository
) {
    suspend operator fun invoke(
        uriList : List<Uri>
    ):List<File>{
        return repository.sharedUriToInternalUri(uriList)
    }

}
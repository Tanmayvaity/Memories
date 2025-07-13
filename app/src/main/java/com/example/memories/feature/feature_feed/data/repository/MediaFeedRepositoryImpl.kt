package com.example.memories.feature.feature_feed.data.repository

import android.net.Uri
import com.example.memories.core.data.data_source.MediaManager
import com.example.memories.feature.feature_feed.domain.model.MediaImage
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class MediaFeedRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager
) : MediaFeedRepository {
    override suspend fun fetchMediaFromShared(): Flow<MediaImage> {
        return mediaManager.fetchMediaFromShared()
    }

    override suspend fun delete(uri: Uri) {
        mediaManager.deleteMedia(uri)
    }

    override suspend fun deleteMedias(uriList: List<Uri>) {
        mediaManager.deleteMedias(uriList)
    }

    override suspend fun sharedUriToInternalUri(uriList: List<Uri>) : List<File> {
        return mediaManager.sharedUriToInternalUri(uriList)
    }

    override suspend  fun observeChanges(): Flow<Unit> {
        return mediaManager.observeMediaChanges()
    }

}
package com.example.memories.feature.feature_feed.domain.repository

import android.net.Uri
import com.example.memories.feature.feature_feed.domain.model.MediaImage
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MediaFeedRepository {
    suspend fun fetchMediaFromShared(): Flow<MediaImage>

    suspend fun delete(uri : Uri)

    suspend fun deleteMedias(uriList : List<Uri>)

    suspend fun sharedUriToInternalUri(
        uriList : List<Uri>
    ): List<File>


    suspend fun observeChanges() : Flow<Unit>
}
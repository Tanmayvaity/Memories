package com.example.memories.feature.feature_feed.domain.repository

import android.net.Uri
import android.util.Size
import androidx.paging.PagingData
import com.example.memories.feature.feature_feed.domain.model.MediaImage
import com.example.memories.feature.feature_media_edit.domain.model.BitmapResult
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MediaFeedRepository {
    suspend fun fetchMediaFromShared(): Flow<PagingData<MediaImage>>

    suspend fun delete(uri : Uri)

    suspend fun deleteMedias(uriList : List<Uri>)

    suspend fun sharedUriToInternalUri(
        uriList : List<Uri>
    ): List<File>

    suspend fun observeChanges() : Flow<Unit>

    suspend fun getMediaThumbnail(uri : Uri,size: Size) : BitmapResult
}


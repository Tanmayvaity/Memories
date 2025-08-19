package com.example.memories.feature.feature_feed.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.paging.PagingData
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.domain.model.MediaObject
import kotlinx.coroutines.flow.Flow
import java.io.File

interface MediaFeedRepository {
    suspend fun fetchMediaFromShared(): Flow<PagingData<MediaObject>>

    suspend fun delete(uri : Uri)

    suspend fun deleteMedias(uriList : List<Uri>)

    suspend fun sharedUriToInternalUri(
        uriList : List<Uri>
    ): List<File>


    suspend fun getMediaThumbnail(uri : Uri,size: Size) : Result<Bitmap>
}


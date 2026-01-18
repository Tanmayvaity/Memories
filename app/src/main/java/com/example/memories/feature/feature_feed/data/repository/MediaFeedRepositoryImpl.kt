package com.example.memories.feature.feature_feed.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.domain.model.MediaObject
import com.example.memories.feature.feature_feed.domain.repository.MediaFeedRepository
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject

class MediaFeedRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager
) : MediaFeedRepository {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun fetchMediaFromShared(): Flow<PagingData<MediaObject>> {
        return Pager(
            config = PagingConfig(pageSize = 100, enablePlaceholders = true),
            pagingSourceFactory = { SharedPagingSource(mediaManager) }
        ).flow

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


    @RequiresApi(Build.VERSION_CODES.Q)
    override suspend fun getMediaThumbnail(
        uri: Uri,
        size: Size
    ): Result<Bitmap> {
        return mediaManager.getMediaThumbnail(uri,size)
    }


}
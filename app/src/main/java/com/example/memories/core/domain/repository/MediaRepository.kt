package com.example.memories.core.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import androidx.paging.PagingData
import com.example.memories.core.domain.model.Photo
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.core.domain.model.Video
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import com.example.memories.feature.feature_media_edit.domain.model.ShaderStep
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    suspend fun uriToBitmap(
        uri : Uri,
        degrees : Float
    ): Result<Bitmap>

    suspend fun downloadWithBitmap(
        bitmap : Bitmap
    ): Result<String>

    suspend fun downloadVideo(
        uri : Uri
    ): Result<String>

    suspend fun saveBitmapToInternalStorage(
        bitmap: Bitmap?
    ): Result<Uri>

    /** Saves an in-memory bitmap to cache and returns a shareable FileProvider URI. */
    suspend fun saveBitmapToCache(
        bitmap: Bitmap
    ): Result<Uri>

    suspend fun deleteMedia(
        uriList : List<Uri>
    ) : Result<String>

    /** Builds a single shader step combining a filter with a map of adjustment values. */
    fun composeShader(
        filterType: FilterType,
        adjustValues: Map<AdjustType, Float>
    ): ShaderStep


    suspend fun applyFilterToUri(
        uri : Uri,
        shaderCode : String,
        degrees : Float
    ): Bitmap?

    suspend fun saveToCacheStorage(
        uri : Uri,
        bitmap: Bitmap,
    ) : Result<UriType>

    suspend fun saveToCacheStorageWithUri(
        uri : Uri
    ): Result<Uri>

    /** Downloads a remote media [url] into cache and returns the resulting content URI + type. */
    suspend fun saveRemoteMediaToCache(
        url : String,
        isImage : Boolean
    ): Result<UriType>


    fun generateShareableUri(isImage : Boolean? = false,uri : Uri? = null) : Uri?

    fun getRemoteImages() : Flow<PagingData<Photo>>

    fun getRemoteVideos() : Flow<PagingData<Video>>

}
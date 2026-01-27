package com.example.memories.feature.feature_media_edit.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType

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

    suspend fun deleteMedia(
        uriList : List<Uri>
    ) : Result<String>

    fun applyFilter( filterType: FilterType) : String?

    fun applyAdjustFilter(adjustType: AdjustType,value : Float ) : String?


    fun fetchAllAdjustShader(): List<String>

    fun fetchAllFilterShader() : List<String>


    suspend fun applyFilterToUri(
        uri : Uri,
        shaderCode : String,
        degrees : Float
    ): Bitmap?

    suspend fun saveToCacheStorage(
        uri : Uri,
        bitmap: Bitmap,
    ) : Result<Uri>

    suspend fun saveToCacheStorageWithUri(
        uri : Uri
    ): Result<Uri>

}
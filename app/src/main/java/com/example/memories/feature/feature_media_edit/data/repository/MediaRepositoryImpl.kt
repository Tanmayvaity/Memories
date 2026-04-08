package com.example.memories.feature.feature_media_edit.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.memories.core.data.data_source.graphics.ShaderStep
import com.example.memories.core.data.data_source.media.FilterShaders
import com.example.memories.core.data.data_source.media.MediaManager
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.UriType
import com.example.memories.feature.feature_media_edit.data.CoolFadeStep
import com.example.memories.feature.feature_media_edit.data.GrayScaleStep
import com.example.memories.feature.feature_media_edit.data.InvertStep
import com.example.memories.feature.feature_media_edit.data.OriginalStep
import com.example.memories.feature.feature_media_edit.data.SepiaStep
import com.example.memories.feature.feature_media_edit.data.VintageStep
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository
import java.util.logging.Filter
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    val mediaManager: MediaManager,
) : MediaRepository {
    override suspend fun uriToBitmap(
        uri: Uri,
        degrees: Float
    ) : Result<Bitmap> {
        return mediaManager.uriToBitmap(uri, degrees)
    }

    override suspend fun downloadWithBitmap(bitmap: Bitmap): Result<String> {
        return mediaManager.downloadImageWithBitmap(bitmap)
    }

    override suspend fun downloadVideo(uri: Uri): Result<String> {
        return mediaManager.downloadVideo(uri)
    }

    override suspend fun saveBitmapToInternalStorage(bitmap: Bitmap?): Result<Uri> {
        return mediaManager.saveBitmapToInternalStorage(bitmap)
    }

    override suspend fun deleteMedia(uriList: List<Uri>) : Result<String> {
        return mediaManager.deleteInternalMedia(uriList)
    }

    override fun applyFilter(filterType: FilterType): ShaderStep{
        return when(filterType){
            FilterType.ORIGINAL -> OriginalStep()
            FilterType.GRAYSCALE -> GrayScaleStep()
            FilterType.SEPIA -> SepiaStep()
            FilterType.INVERT -> InvertStep()
            FilterType.VINTAGE -> VintageStep()
            FilterType.COOL_FADE -> CoolFadeStep()
        }

    }
    override fun applyAdjustFilter(adjustType: AdjustType, value : Float): String? {
        return when(adjustType){

            AdjustType.BRIGHTNESS -> {
                FilterShaders.BRIGHTNESS_SHADER
            }
            AdjustType.BLUR -> {
                FilterShaders.BLUR_SHADER
            }
            else -> null

//            AdjustType.CONTRAST -> TODO()
//            AdjustType.SATURATION -> TODO()
//            AdjustType.TEMPERATURE -> TODO()
//            AdjustType.FADE -> TODO()
//            AdjustType.VIGNETTE -> TODO()
        }

    }

    override fun fetchAllAdjustShader(): List<String> {
        return buildList {
            add(FilterShaders.BRIGHTNESS_SHADER)
        }
    }

    override fun fetchAllFilterShader(): List<String> {
        return buildList {
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override suspend fun applyFilterToUri(uri: Uri, shaderCode: String, degrees : Float): Bitmap? {
        return mediaManager.applyFilter(uri,shaderCode,degrees)
    }

    override suspend fun saveToCacheStorage(uri: Uri, bitmap: Bitmap): Result<UriType> {
        return mediaManager.saveToCacheStorage(uri,bitmap)
    }

    override suspend fun saveToCacheStorageWithUri(uri: Uri): Result<Uri> {
        return mediaManager.saveToCacheStorageWithUri(uri)
    }

    override fun generateShareableUri(isImage: Boolean?,uri: Uri?): Uri? {
        return mediaManager.generateShareableUri(isImage,uri)
    }


}
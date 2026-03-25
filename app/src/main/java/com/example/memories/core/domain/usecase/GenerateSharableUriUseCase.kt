package com.example.memories.core.domain.usecase

import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class GenerateSharableUriUseCase (
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(isImage : Boolean?,uri: Uri? = null) : Result<Uri?> {

        return try{
            val uri = mediaRepository.generateShareableUri(isImage,uri)
            Result.Success(uri)
        }catch (e : Exception){
            Result.Error(e)
        }

    }
}
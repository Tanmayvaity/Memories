package com.example.memories.feature.feature_media_edit.domain.usecase

import android.graphics.Bitmap
import android.net.Uri
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class SaveBitmapToInternalStorageUseCase(
    val repository: MediaRepository
) {
    suspend operator fun invoke(bitmap: Bitmap?): Result<Uri>{
        return repository.saveBitmapToInternalStorage(bitmap)
    }
}
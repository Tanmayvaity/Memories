package com.example.memories.feature.feature_media_edit.domain.usecase

import android.R.attr.bitmap
import android.graphics.Bitmap
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.model.AdjustType
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class ApplyAdjustFilterUseCase(
    val mediaRepository: MediaRepository
) {
    operator fun invoke(
        adjustType: AdjustType,
        value: Float
    ): String? {
        return mediaRepository.applyAdjustFilter(adjustType,value)
    }
}

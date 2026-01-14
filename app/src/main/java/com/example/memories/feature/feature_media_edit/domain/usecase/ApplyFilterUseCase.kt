package com.example.memories.feature.feature_media_edit.domain.usecase

import android.R.attr.bitmap
import android.graphics.Bitmap
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_media_edit.domain.model.FilterType
import com.example.memories.feature.feature_media_edit.domain.repository.MediaRepository

class ApplyFilterUseCase(
    val mediaRepository: MediaRepository
) {
    operator fun invoke(
        filterType: FilterType
    ): String? {
        return mediaRepository.applyFilter(filterType)
    }
}

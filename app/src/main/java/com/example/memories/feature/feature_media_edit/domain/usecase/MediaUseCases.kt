package com.example.memories.feature.feature_media_edit.domain.usecase

import com.example.memories.core.domain.usecase.DownloadVideoUseCase
import com.example.memories.core.domain.usecase.DownloadWithBitmapUseCase
import com.example.memories.core.domain.usecase.GenerateSharableUriUseCase

data class MediaUseCases(
    val uriToBitmapUseCase: UriToBitmapUseCase,
    val downloadWithBitmap: DownloadWithBitmapUseCase,
    val saveBitmapToInternalStorageUseCase: SaveBitmapToInternalStorageUseCase,
    val downloadVideoUseCase: DownloadVideoUseCase,
    val applyFilterUseCase: ApplyFilterUseCase,
    val applyAdjustFilterUseCase: ApplyAdjustFilterUseCase,
    val saveToCacheStorageWithBitmapUseCase: SaveToCacheStorageWithBitmapUseCase,
    val generateSharableUriUseCase: GenerateSharableUriUseCase,
)
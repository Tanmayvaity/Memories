package com.example.memories.feature.feature_media_edit.domain.usecase

data class MediaUseCases(
    val uriToBitmapUseCase: UriToBitmapUseCase,
    val downloadWithBitmap: DownloadWithBitmapUseCase,
    val saveBitmapToInternalStorageUseCase: SaveBitmapToInternalStorageUseCase,
    val downloadVideoUseCase: DownloadVideoUseCase,
    val applyFilterUseCase : ApplyFilterUseCase,
    val applyAdjustFilterUseCase: ApplyAdjustFilterUseCase,
    val saveToCacheStorageWithBitmapUseCase: SaveToCacheStorageWithBitmapUseCase,
    )
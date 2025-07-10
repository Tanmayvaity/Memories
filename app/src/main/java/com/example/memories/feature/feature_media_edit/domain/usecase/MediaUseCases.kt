package com.example.memories.feature.feature_media_edit.domain.usecase

data class MediaUseCases(
    val uriToBitmapUseCase: UriToBitmapUseCase,
    val downloadWithBitmap: DownloadWithBitmap,
    val saveBitmapToInternalStorageUseCase: SaveBitmapToInternalStorageUseCase
)
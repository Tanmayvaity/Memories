package com.example.memories.feature.feature_other.domain.usecase

import com.example.memories.core.domain.usecase.DownloadVideoUseCase
import com.example.memories.core.domain.usecase.GenerateSharableUriUseCase
import com.example.memories.core.domain.usecase.ToggleMediaFavouriteUseCase
import com.example.memories.core.domain.usecase.GetMemoryByIdUseCase
import com.example.memories.core.domain.usecase.DownloadWithBitmapUseCase

/**
 * Aggregates the use cases backing the "Manage Media" screen: paged media listing,
 * per-media favourite toggling, and the shared download/share/associated-memory use cases
 * reused from the feed/media-edit features.
 */
data class MediaManagementUseCaseWrapper(
    val getAllMediaPagedUseCase: GetAllMediaPagedUseCase,
    val toggleMediaFavouriteUseCase: ToggleMediaFavouriteUseCase,
    val downloadWithBitmapUseCase: DownloadWithBitmapUseCase,
    val downloadVideoUseCase: DownloadVideoUseCase,
    val generateShareableUriUseCase: GenerateSharableUriUseCase,
    val getMemoryByIdUseCase: GetMemoryByIdUseCase
)

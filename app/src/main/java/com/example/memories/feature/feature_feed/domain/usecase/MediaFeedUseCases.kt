package com.example.memories.feature.feature_feed.domain.usecase

data class MediaFeedUseCases (
    val fetchMediaFromSharedUseCase: FetchMediaFromSharedUseCase,
    val deleteMediaUseCase: DeleteMediaUseCase,
    val deleteMediasUseCase: DeleteMediasUseCase,
    val sharedUriToInternalUriUseCase: SharedUriToInternalUriUseCase,
    val getMediaThumbnailUseCase: GetMediaThumbnailUseCase
)
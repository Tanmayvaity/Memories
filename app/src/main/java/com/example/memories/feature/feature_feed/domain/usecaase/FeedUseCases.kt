package com.example.memories.feature.feature_feed.domain.usecaase

data class FeedUseCases (
    val fetchMediaFromSharedUseCase: FetchMediaFromSharedUseCase,
    val deleteMediaUseCase: DeleteMediaUseCase,
    val deleteMediasUseCase: DeleteMediasUseCase,
    val sharedUriToInternalUriUseCase: SharedUriToInternalUriUseCase,
    val observeMediaChangesUseCase: ObserveMediaChangesUseCase,
    val getMediaThumbnailUseCase: GetMediaThumbnailUseCase
)
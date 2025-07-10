package com.example.memories.feature.feature_feed.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_feed.domain.model.MediaImage
import com.example.memories.feature.feature_feed.domain.usecaase.FeedUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedScreenViewModel @Inject constructor(
    val feedUseCases: FeedUseCases
) : ViewModel(){

    private val _mediaState = MutableStateFlow<UiState>(UiState())

    val mediaState: StateFlow<UiState> = _mediaState.asStateFlow()
    init {
        viewModelScope.launch {
            _mediaState.update { _mediaState.value.copy(isLoading = true) }
            feedUseCases
                .fetchMediaFromSharedUseCase()
                .collect { image ->
                    _mediaState.update {
                        mediaState.value.copy(data = _mediaState.value.data + image, isLoading = false)
                    }
                }


        }
    }


    data class UiState(
        var isLoading : Boolean = false,
        var data : List<MediaImage> = emptyList<MediaImage>()
    )

}
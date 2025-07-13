package com.example.memories.feature.feature_feed.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_feed.domain.model.MediaImage
import com.example.memories.feature.feature_feed.domain.usecaase.FeedUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SharedScreenViewModel @Inject constructor(
    val feedUseCases: FeedUseCases
) : ViewModel() {

    companion object{
        const val TAG ="SharedViewModel"
    }

    private val _mediaState = MutableStateFlow<UiState>(UiState())
    val mediaState: StateFlow<UiState> = _mediaState.asStateFlow()

    private val _selectedMediaUri = MutableStateFlow<List<Uri>>(emptyList())
    val selectedMediaUri: StateFlow<List<Uri>> = _selectedMediaUri.asStateFlow()


    private val _internalFileList = MutableStateFlow<List<File>>(emptyList())
    val internalFileList: StateFlow<List<File>> = _internalFileList.asStateFlow()


    init {
        onEvent(FeedEvent.Feed)
    }

    fun onEvent(event: FeedEvent) {
        when (event) {

            is FeedEvent.ObserveMediaChanges->{
                viewModelScope.launch {
                    feedUseCases.observeMediaChangesUseCase()
                        .onStart { emit(Unit) }
                        .collectLatest {it ->
                            Log.d(TAG, "onEvent: ${it}")
                            onEvent(FeedEvent.Feed)
                        }
                }
            }


            is FeedEvent.Feed -> {
                viewModelScope.launch {
                    fetch()
                }
            }


            is FeedEvent.Delete -> {
                viewModelScope.launch {
                    feedUseCases.deleteMediaUseCase(event.uri)
                    _mediaState.update {
                        mediaState.value.copy(data = mediaState.value.data.filter { it.uri != event.uri })
                    }
                }
            }

            is FeedEvent.DeleteMultiple ->{
                val selectedList = _selectedMediaUri.value.toSet()
                _mediaState.update {
                    mediaState.value.copy(data = mediaState.value.data.filterNot {it.uri in selectedList })
                }
            }

            is FeedEvent.MediaSelect -> {
                _selectedMediaUri.update {
                    _selectedMediaUri.value + event.uri
                }
            }

            is FeedEvent.MediaUnSelect -> {
                _selectedMediaUri.update {
                    _selectedMediaUri.value - event.uri
                }
            }
            is FeedEvent.MediaSelectedEmpty ->{
                _selectedMediaUri.update {
                    emptyList<Uri>()
                }
            }

            is FeedEvent.Share -> {
                viewModelScope.launch {
                    val list = feedUseCases.sharedUriToInternalUriUseCase(listOf<Uri>(_selectedMediaUri.value.last()))

                    _internalFileList.update {
                        list
                    }
                }
            }

            is FeedEvent.ShareMultiple->{
                viewModelScope.launch {
                    val list = feedUseCases.sharedUriToInternalUriUseCase(_selectedMediaUri.value)

                    _internalFileList.update {
                        list
                    }
                }
            }



        }

    }

    private suspend fun fetch() {
//        val selectedList = _selectedMediaUri.value.toSet()
//        _mediaState.update {
//            it.copy(data = emptyList())
//        }


        _mediaState.update { _mediaState.value.copy(isLoading = true) }
        feedUseCases
            .fetchMediaFromSharedUseCase()
            .collect { image ->
                _mediaState.update {
//                    delay(5000)
                    val alreadyExists = it.data.any { it.uri == image.uri }
                    if (alreadyExists) {
                        it
                    }else{
                        it.copy(data = it.data + image, isLoading = false)

                    }

                }
            }

        _mediaState.update {
            it.copy(
                isLoading = false
            )
        }


    }


    data class UiState(
        var isLoading: Boolean = false,
        var data: List<MediaImage> = emptyList<MediaImage>()
    )

}
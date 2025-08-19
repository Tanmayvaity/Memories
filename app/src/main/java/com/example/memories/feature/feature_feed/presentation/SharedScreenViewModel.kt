package com.example.memories.feature.feature_feed.presentation

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.domain.model.MediaObject
import com.example.memories.feature.feature_feed.domain.usecase.MediaFeedUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SharedScreenViewModel @Inject constructor(
    val feedUseCases: MediaFeedUseCases
) : ViewModel() {

    companion object{
        const val TAG ="SharedViewModel"
    }

    private val _pagingState = MutableStateFlow<PagingData<MediaObject>>(PagingData.empty())
    val pagingState = _pagingState.asStateFlow()

//    private val _mediaState = MutableStateFlow<UiState>(UiState())
//    val mediaState: StateFlow<UiState> = _mediaState.asStateFlow()

    private val _selectedMediaUri = MutableStateFlow<List<Uri>>(emptyList())
    val selectedMediaUri: StateFlow<List<Uri>> = _selectedMediaUri.asStateFlow()

    private val _bitmapThumbnail = MutableStateFlow<Bitmap?>(null)
    val bitmapThumbnail = _bitmapThumbnail.asStateFlow()


    private val _internalFileList = MutableStateFlow<List<File>>(emptyList())
    val internalFileList: StateFlow<List<File>> = _internalFileList.asStateFlow()


    init {
        onEvent(MediaFeedEvent.Feed)
    }

    fun onEvent(event: MediaFeedEvent) {
        when (event) {




            is MediaFeedEvent.Feed -> {
                viewModelScope.launch {
                    fetch()
                }
            }


            is MediaFeedEvent.Delete -> {
                viewModelScope.launch {
                    feedUseCases.deleteMediaUseCase(event.uri)
//                    _mediaState.update {
//                        mediaState.value.copy(data = mediaState.value.data.filter { it.uri != event.uri })
//                    }

                }
            }

            is MediaFeedEvent.DeleteMultiple ->{
                val selectedList = _selectedMediaUri.value.toSet()
//                _mediaState.update {
//                    mediaState.value.copy(data = mediaState.value.data.filterNot {it.uri in selectedList })
//                }
                _pagingState.update {it->
                    it.filter{it.uri !in selectedList}
                }

            }

            is MediaFeedEvent.MediaSelect -> {
                _selectedMediaUri.update {
                    _selectedMediaUri.value + event.uri
                }
            }

            is MediaFeedEvent.MediaUnSelect -> {
                _selectedMediaUri.update {
                    _selectedMediaUri.value - event.uri
                }
            }
            is MediaFeedEvent.MediaSelectedEmpty ->{
                _selectedMediaUri.update {
                    emptyList<Uri>()
                }
            }

            is MediaFeedEvent.Share -> {
                viewModelScope.launch {
                    val list = feedUseCases.sharedUriToInternalUriUseCase(listOf<Uri>(_selectedMediaUri.value.last()))

                    _internalFileList.update {
                        list
                    }
                }
            }

            is MediaFeedEvent.ShareMultiple->{
                viewModelScope.launch {
                    val list = feedUseCases.sharedUriToInternalUriUseCase(_selectedMediaUri.value)

                    _internalFileList.update {
                        list
                    }
                }
            }

            is MediaFeedEvent.FetchThumbnail ->{
                viewModelScope.launch {
                    val result = feedUseCases.getMediaThumbnailUseCase(event.uri,event.size)
                    when(result){
                        is Result.Success ->{
                            _bitmapThumbnail.update {
                                result.data
                            }
                        }
                        is Result.Error -> {
                            Log.e(TAG, "onEvent: ${result.error.message}")
                        }

                    }

                }
            }



        }

    }

    private suspend fun fetch() {
        feedUseCases
            .fetchMediaFromSharedUseCase()
            .cachedIn(viewModelScope)
            .collect { value ->
                _pagingState.update {
                    value
                }
            }

    }


}
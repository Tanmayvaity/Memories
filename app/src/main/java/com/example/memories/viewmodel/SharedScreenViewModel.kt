package com.example.memories.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.model.media.MediaRepository
import com.example.memories.model.models.MediaImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SharedScreenViewModel: ViewModel() {

    val mediaRepository : MediaRepository by lazy { MediaRepository() }
//    private val _mediaImages = MutableStateFlow<List<MediaImage>>(emptyList())
//    val mediaImages: StateFlow<List<MediaImage>> = _mediaImages.asStateFlow()

//    private val _appMediaImages = MutableStateFlow<List<MediaImage>>(emptyList())
//    val appMediaImages: StateFlow<List<MediaImage>> = _appMediaImages.asStateFlow()


    private val _fetchFromThisApp : MutableStateFlow<Boolean> = MutableStateFlow<Boolean>(true)
    val fetchFromThisApp = _fetchFromThisApp.asStateFlow()


    private val _mediaState = MutableStateFlow<UiState>(UiState())

    val mediaState: StateFlow<UiState> = _mediaState.asStateFlow()

    private var fetchJob : Job? = null


    fun fetchMediaFromShared(context : Context){
        fetchJob?.cancel()

        fetchJob = viewModelScope.launch {
            _mediaState.update { _mediaState.value.copy(isLoading = true) }
            mediaRepository
                .fetchMediaFromShared(context,_fetchFromThisApp.value)
                .collect { image ->
                    if(!_mediaState.value.data.contains(image)){
                        _mediaState.update { _mediaState.value.copy( data = _mediaState.value.data + image)}
                    }

//                    if(!_appMediaImages.value.contains(image) && fromApp){
//                        _appMediaImages.update { _appMediaImages.value + image }
//                    }

                }

            _mediaState.update { _mediaState.value.copy(isLoading = false)}

        }
    }

    fun toggleFromAppState(){
        _fetchFromThisApp.update { !_fetchFromThisApp.value }
    }

    fun clearData(){
        _mediaState.update { _mediaState.value.copy(data = emptyList()) }
        fetchJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("Cleared", "SharedScreenViewModel : onCleared: ")
    }

    data class UiState(
        var isLoading : Boolean = false,
        var data : List<MediaImage> = emptyList<MediaImage>()
    )
}
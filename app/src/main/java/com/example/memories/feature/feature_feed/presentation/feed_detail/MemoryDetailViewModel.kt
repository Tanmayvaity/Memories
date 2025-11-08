package com.example.memories.feature.feature_feed.presentation.feed_detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryDetailViewModel @Inject constructor(
    val feedUseCases: FeedUseCases
) : ViewModel(){

    private val _memory = MutableStateFlow(MemoryWithMediaModel())
    val memory = _memory.asStateFlow()



    fun onEvent(event: MemoryDetailEvents){
        when(event){
            is MemoryDetailEvents.Fetch -> {
                viewModelScope.launch {
                    val memory = feedUseCases.getMemoryByIdUseCase(event.id)
                    if(memory!=null){
                        _memory.update {
                            memory
                        }
                    }
                    Log.d(TAG, "MemoryDetailEvents.Fetch : ${memory.toString()}")
                }
            }

            is MemoryDetailEvents.Favourite -> {
                viewModelScope.launch {
                    _memory.update { it.copy(
                        memory = it.memory.copy(favourite = true)
                    ) }
                    feedUseCases.toggleFavouriteUseCase(
                        event.id,
                        isFavourite = true
                    )
                }

            }
            is MemoryDetailEvents.UnFavourite -> {
                viewModelScope.launch {
                    _memory.update { it.copy(
                        memory = it.memory.copy(favourite = false)
                    ) }
                    feedUseCases.toggleFavouriteUseCase(
                        event.id,
                        isFavourite = false
                    )
                }
            }
        }


    }

    companion object {
        private const val TAG = "MemoryDetailViewModel"
    }

}
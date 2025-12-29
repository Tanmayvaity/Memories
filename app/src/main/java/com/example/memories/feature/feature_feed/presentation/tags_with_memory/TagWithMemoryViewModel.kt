package com.example.memories.feature.feature_feed.presentation.tags_with_memory

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_feed.domain.usecase.TagWithMemoryUseCaseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TagWithMemoryViewModel @Inject constructor(
    private val tagUseCase : TagWithMemoryUseCaseWrapper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val _state = MutableStateFlow(TagWithMemoryState())
    val state = _state.asStateFlow()

    init {
        Log.d(TAG, "inside init")
        savedStateHandle.get<String>("id")?.let { itemId ->
            Log.d(TAG, "TagWithMemoryViewModel-saveStateHandle : ${itemId}")
            onEvent(TagWithMemoryEvents.Fetch(itemId))
        }
        savedStateHandle.get<String>("tagLabel")?.let { label ->
            Log.d(TAG, "TagWithMemoryViewModel-saveStateHandle tag label : ${label}")
            _state.update { it.copy(label = label) }
        }
    }
    companion object{
        private const val TAG = "TagWithMemoryViewModel"
    }



    fun onEvent(event : TagWithMemoryEvents){
        when(event){
            is TagWithMemoryEvents.Fetch -> {
                _state.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    tagUseCase.fetchMemoryByTagUseCase(event.id).collectLatest { memories ->
                        _state.update { it.copy(memories = memories, isLoading = false) }
                    }
                }

            }
        }
    }

}
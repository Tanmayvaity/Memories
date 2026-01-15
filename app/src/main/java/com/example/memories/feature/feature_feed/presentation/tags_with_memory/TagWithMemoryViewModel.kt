package com.example.memories.feature.feature_feed.presentation.tags_with_memory

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.usecase.TagWithMemoryUseCaseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TagWithMemoryViewModel @Inject constructor(
    private val tagUseCase : TagWithMemoryUseCaseWrapper,
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val _state = MutableStateFlow(TagWithMemoryState())
    private val _tagId = MutableStateFlow<String?>(null)
    val state = _state.asStateFlow()

    init {
        Log.d(TAG, "inside init")
        savedStateHandle.get<String>("id")?.let { itemId ->
            _tagId.update { itemId }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val memoriesForTag: Flow<PagingData<MemoryWithMediaModel>> = _tagId
        .flatMapLatest { tagId ->
            if(tagId == null){
                flowOf(PagingData.empty())
            }else{
                tagUseCase.fetchMemoryByTagUseCase(tagId)
            }
        }
        .cachedIn(viewModelScope)


    fun onEvent(event : TagWithMemoryEvents){
        when(event){
            is TagWithMemoryEvents.Fetch -> {
                _state.update { it.copy(isLoading = true) }
//                viewModelScope.launch {
//                    tagUseCase.fetchMemoryByTagUseCase(event.id).collectLatest { memories ->
//                        _state.update { it.copy(memories = memories, isLoading = false) }
//                    }
//                }

            }
        }
    }

}
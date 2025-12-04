package com.example.memories.feature.feature_memory.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.presentation.search.SearchState
import com.example.memories.feature.feature_memory.domain.usecase.MemoryUseCase
import com.example.memories.feature.feature_memory.presentation.MemoryEvents.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemoryViewModel @Inject constructor(
    val memoryUseCase: MemoryUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "MemoryViewModel"

    }

    private val _errorFlow = Channel<String>()
    val errorFlow = _errorFlow.receiveAsFlow()

    private val _successFlow = Channel<String>()
    val successFlow = _successFlow.receiveAsFlow()


    private val _memoryState = MutableStateFlow<MemoryState>(MemoryState())

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val memoryState: StateFlow<MemoryState> = _memoryState.asStateFlow()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchedTags = memoryState
        .map { it.tagTextFieldValue }
        .debounce(500L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            Log.d(TAG, "query fired with query content:${query.toString()} ")
            memoryUseCase.fetchTagsByLabelUseCase(query)
        }
        .onEach { tags ->
            _memoryState.update { it.copy(totalNumberOfTags = tags) }
        }
        .launchIn(
            scope = viewModelScope,
        )



    fun onEvent(event: MemoryEvents) {
        when (event) {
            is MemoryEvents.TitleChanged -> {
                _memoryState.update {
                    _memoryState.value.copy(
                        title = event.value,
//                        titleHintContent = ""
                    )
                }
            }

            is MemoryEvents.TitleFocusChanged -> {
                _memoryState.update {
                    _memoryState.value.copy(
                        isTitleHintVisible = !event.focusState.isFocused && _memoryState.value.title.isBlank(),
                    )
                }
            }

            is MemoryEvents.ContentChanged -> {
                _memoryState.update {
                    _memoryState.value.copy(
                        content = event.value
                    )
                }
            }

            is MemoryEvents.ContentFocusChanged -> {
                _memoryState.update {
                    _memoryState.value.copy(
                        isContentHintVisible = !event.focusState.isFocused && _memoryState.value.content.isBlank(),
                    )
                }
            }

            is MemoryEvents.CreateMemory -> {
                viewModelScope.launch {
                    val result = memoryUseCase.createMemoryUseCase(
                        uriList = event.uriList,
                        title = event.title,
                        content = event.content,
                        tagList = _memoryState.value.tagsSelectedForThisMemory
                    )
                    when (result) {
                        is Result.Error -> {
                            Log.e(TAG, "onEvent: ${result.error.message}")
                            _errorFlow.send(result.error.message.toString())
                        }

                        is Result.Success<String> -> {
                            _successFlow.send(result.data.toString())
                        }
                    }


                }
            }

            is MemoryEvents.FetchTags -> {
                viewModelScope.launch {
                    val result = memoryUseCase.fetchTagUseCase()
                    when (result) {
                        is Result.Success -> {
                            result.data?.collect { tags ->
                                _memoryState.update { it.copy(totalNumberOfTags = tags) }
                            }
                        }

                        else -> {}
                    }

                }
            }

            is MemoryEvents.AddTag -> {
                viewModelScope.launch {
                    val isTagInsertSuccessful = memoryUseCase.addTagUseCase(event.tag)
                    if (isTagInsertSuccessful is Result.Success) {
                        onEvent(UpdateTagsInTextField(isTagInsertSuccessful.data!!))
                    }

                }
            }

            is MemoryEvents.UpdateTagsInTextField -> {
                if (_memoryState.value.tagsSelectedForThisMemory.contains(event.tag)) return

                _memoryState.update {
                    it.copy(
                        tagsSelectedForThisMemory = it.tagsSelectedForThisMemory + event.tag
                    )
                }
            }

            is MemoryEvents.RemoveTagsFromTextField -> {
                if (_memoryState.value.tagsSelectedForThisMemory.contains(event.tag)) {
                    _memoryState.update {
                        it.copy(
                            tagsSelectedForThisMemory = it.tagsSelectedForThisMemory - event.tag
                        )
                    }
                }
            }

            is MemoryEvents.TagsTextFieldContentChanged -> {
                _memoryState.update { it.copy(tagTextFieldValue = event.value) }
//                Log.d(TAG, "onEvent: ${tagInputText.value}")
            }
        }
    }
}
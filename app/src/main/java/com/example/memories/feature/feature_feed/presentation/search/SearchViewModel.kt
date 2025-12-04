package com.example.memories.feature.feature_feed.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import com.example.memories.feature.feature_feed.presentation.feed.FeedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    val feedUseCase: FeedUseCaseWrapper
) : ViewModel() {

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    private val _state = MutableStateFlow(SearchState())

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val state : StateFlow<SearchState> = _inputText
        .debounce(500)
        .flatMapLatest { query ->
            Log.d("SearchViewModel", "Search query : ${query}")
            if (query.isBlank() || query.isEmpty()) {
                flowOf(SearchState()) // empty state if no input
            } else {
                feedUseCase.searchByTitleUseCase(query)
                    .map { results ->
                        SearchState(isLoading = false, data = results)
                    }
                    .onStart {
                        emit(SearchState(isLoading = true))
                    }
            }
        }
        .onEach {
            _state.update { it.copy(isLoading = false) }
            Log.d("SearchViewModel", "${state.value}")
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SearchState()
        )


    fun onEvent(event: SearchEvents) {
        when (event) {
            is SearchEvents.InputTextChange -> {
                _inputText.update { event.input }
            }
            is SearchEvents.ClearInput -> {
                _inputText.update { "" }
            }
        }
    }





}

data class SearchState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: List<MemoryWithMediaModel> = emptyList()
)
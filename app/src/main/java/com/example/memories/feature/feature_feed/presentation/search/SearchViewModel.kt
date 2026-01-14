package com.example.memories.feature.feature_feed.presentation.search

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.SearchModel
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.TagsWithMemoryModel
import com.example.memories.feature.feature_feed.domain.model.OnThisDayMemories
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.RecentSearchWrapper
import com.example.memories.feature.feature_feed.presentation.feed.FeedState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SearchViewModel @Inject constructor(
    val feedUseCase: FeedUseCaseWrapper,
    val recentSearchUseCase: RecentSearchWrapper
) : ViewModel() {


//    val inputText = _inputText.asStateFlow()

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val query = _inputText
    .debounce(400)
    .flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(_state.value.recentSearch)
        } else {
            feedUseCase.searchByTitleUseCase(query)
        }
    }
    .onEach { results ->
        _state.update { it.copy(data = results) }
    }
    .launchIn(viewModelScope)


//    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
//    val searchState  = _inputText
//        .debounce(500)
//        .flatMapLatest { query ->
//            Log.d("SearchViewModel", "Search query : ${query}")
//            if (query.isBlank() || query.isEmpty()) {
//                flowOf(SearchState()) // empty state if no input
//            } else {
//                feedUseCase.searchByTitleUseCase(query)
//                    .map { results ->
////                        _state.update {
////                            it.copy(isLoading = false,data = results)
////                        }
//                        SearchState(isLoading = false, data = results)
//                    }
//                    .onStart {
////                        emit(SearchState(isLoading = true))
//                        _state.update {
//                            it.copy(isLoading = true)
//                        }
//                    }
//            }
//
//        }
//        .onEach {
//            _state.update { it.copy(isLoading = false) }
//            Log.d("SearchViewModel", "${state.value}")
//        }
////        .launchIn(viewModelScope)
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = SearchState()
//        )

    init {
        onEvent(SearchEvents.FetchRecentSearch)
        onEvent(SearchEvents.FetchTags)
        onEvent(SearchEvents.FetchOnThisDayData)
    }



    @OptIn(FlowPreview::class)
    fun onEvent(event: SearchEvents) {
        when (event) {

            is SearchEvents.FetchOnThisDayData -> {
                viewModelScope.launch {
                    val result = feedUseCase.fetchOnThisDataUseCase()
                    _state.update { it.copy(
                        onThisDateMemories = result
                    ) }
                    Log.d(TAG, "onEvent: FetchOnThisDayDate : ${result}")
                }
            }

            is SearchEvents.InputTextChange -> {
                _inputText.update { event.input }
                if (_inputText.value.isEmpty() || _inputText.value.isBlank()) {
                    _state.update {
                        it.copy(data = it.recentSearch)
                    }
//                    return
                }
//                viewModelScope.launch {
//                    feedUseCase.searchByTitleUseCase(_inputText.value)
//                        .collect { results ->
//                            _state.update {
//                                it.copy(data = results)
//                            }
//                        }
//                }
            }

            is SearchEvents.ClearInput -> {
                _inputText.update { "" }
                onEvent(SearchEvents.Expand)
            }

            is SearchEvents.AddSearch -> {
                viewModelScope.launch {
                    recentSearchUseCase.saveSearchIdUseCase(event.memoryId)
                }
            }

            is SearchEvents.Expand -> {
                _state.update {
                    it.copy(
                        data = it.recentSearch
                    )
                }
            }

            is SearchEvents.FetchTags -> {
                viewModelScope.launch {
                    val result = feedUseCase.fetchTagUseCase()
                    if(result is Result.Success && result.data !=null){
                        var initialSelectDone = false
                        result.data.collect { tags ->
                            _state.update { it.copy(tags = tags) }
                            Log.d(TAG, "fetchedTags : $tags")

                            if (!initialSelectDone && tags.isNotEmpty()) {
                                _state.update { it.copy(currentTag = tags.first()) }
                                onEvent(SearchEvents.SelectTag(state.value.currentTag!!))
                                initialSelectDone = true
                            }
                        }
//                        if(state.value.tags.isEmpty())return@launch
//                        _state.update { it.copy(currentTag = state.value.tags.first()) }
//                        onEvent(SearchEvents.SelectTag(state.value.currentTag!!))
                    }
                }
            }


            is SearchEvents.FetchRecentSearch -> {
                viewModelScope.launch {
                    _state.update { it.copy(isRecentSearchLoading = true) }
                    val result = recentSearchUseCase.fetchRecentSearchUseCase()
                    when (result) {
                        is Result.Success -> {
                            result.data!!.collect { searches ->
                                val list = mutableListOf<MemoryWithMediaModel>()
                                searches.forEach { search ->
                                    feedUseCase.getMemoryByIdUseCase(search.memoryId).also { item ->
//                                        Log.d("SearchViewModel", "onEvent:FetchRecentSearch ${item}")
                                        list.add(item!!)

//                                        Log.d(TAG, "onEvent: state updated FetchRecentSearch ${_state.value.recentSearch} ")
                                    }

                                }
                                _state.update { it.copy(isRecentSearchLoading = false) }
                                _state.update {
                                    it.copy(
                                        recentSearch = list
                                    )
                                }
                            }
                        }

                        else -> {}
                    }

                }
            }

            is SearchEvents.SelectTag -> {
                _state.update { it.copy(currentTag = event.tag, isMemoriesTagLoading = true) }
                viewModelScope.launch {
                    feedUseCase.fetchMemoryByTagUseCase(event.tag.tagId).collect { memories ->
                        _state.update { it.copy(memories = memories, isMemoriesTagLoading = false) }
//                        Log.d(TAG, "onEvent: SelectTag ${memories}")
                    }
                }
            }
        }
    }


    companion object {
        private const val TAG = "SearchViewModel"
    }


}

data class SearchState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val data: List<MemoryWithMediaModel> = emptyList(),
    val recentSearch: List<MemoryWithMediaModel> = emptyList(),
    val isRecentSearchLoading: Boolean = false,
    val tags : List<TagModel> = emptyList(),
    val currentTag : TagModel? = null,
    val memories : List<MemoryWithMediaModel> = emptyList(),
    val isMemoriesTagLoading : Boolean = false,
    val onThisDateMemories : List<OnThisDayMemories> = emptyList()
)
package com.example.memories.feature.feature_feed.presentation.search

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.Result
import com.example.memories.core.domain.model.SearchModel
import com.example.memories.core.domain.model.TagModel
import com.example.memories.core.domain.model.TagsWithMemoryModel
import com.example.memories.feature.feature_feed.domain.model.OnThisDayMemories
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.SearchUseCase
import com.example.memories.feature.feature_feed.presentation.feed.FeedState
import com.example.memories.feature.feature_feed.presentation.search.SearchEvents.*
import com.example.memories.feature.feature_feed.presentation.search.toSectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class SearchViewModel @Inject constructor(
    val searchUseCase: SearchUseCase,
) : ViewModel() {


    private val _inputText = MutableStateFlow("")
    val inputText = _inputText.asStateFlow()

    private val _currentTag = MutableStateFlow<TagModel?>(null)

    val recentSearches: StateFlow<SectionState<List<MemoryWithMediaModel>>> =
        searchUseCase.fetchRecentSearchUseCase()
            .flatMapLatest { searches ->
                flow {
                    val memories = searchUseCase
                        .fetchMemoryByIdsUseCase(searches.map { it.memoryId })
                    Log.d(TAG, "${memories.size}: ")
                    emit(memories)
                }

            }
            .toSectionState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = SectionState.Loading


            )
    val searchResults: StateFlow<List<MemoryWithMediaModel>> = combine(
        _inputText.debounce(400).distinctUntilChanged(),
        recentSearches
    ) { query, recentState ->
        query to recentState
    }.flatMapLatest { (query, recentState) ->
        if (query.isBlank() && recentState is SectionState.Success) {
            flowOf(recentState.data.reversed())
        } else if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            searchUseCase.searchByTitleUseCase(query)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    val onThisDayMemories: StateFlow<List<MemoryWithMediaModel>> =
        searchUseCase.fetchOnThisDayUseCase()
            .catch {
                Log.e(TAG, "Error fetching on this day memories", it)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    val recentMemories: StateFlow<SectionState<List<MemoryWithMediaModel>>> =
        searchUseCase.fetchRecentMemoriesUseCase()
            .toSectionState()
//            .catch { e -> Log.e(TAG, "Error fetching recent memories", e) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SectionState.Loading)

    val tags: StateFlow<SectionState<List<TagModel>>> = searchUseCase.fetchTagUseCase()
        .onEach { tags ->
//            delay(2000)
            if (_currentTag.value == null && tags.isNotEmpty()) {
                _currentTag.value = tags.first()
            }
        }
        .toSectionState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SectionState.Loading)

    @OptIn(ExperimentalCoroutinesApi::class)
    val memoriesForTag: Flow<PagingData<MemoryWithMediaModel>> = _currentTag
        .flatMapLatest { tag ->
            if (tag == null) {
                flowOf(PagingData.empty())
            } else {
                searchUseCase.fetchMemoryByTagUseCase(tag.tagId)
            }
        }
        .cachedIn(viewModelScope)

    init {
        Log.d(TAG, "SearchViewModel created: ${hashCode()}")
//        viewModelScope.launch {
//            _onThisDayMemories.value = searchUseCase.fetchOnThisDayUseCase()
//        }

    }

    val state: StateFlow<SearchState> =
        combine(
            inputText,
            tags,
            _currentTag,
            recentSearches,
            recentMemories,
            searchResults,
            onThisDayMemories,
        ) { values: Array<Any?> ->
            val input = values[0] as String
            val tagsList = values[1] as SectionState<List<TagModel>>
            val selectedTag = values[2] as TagModel?
            val recentSearchList = values[3] as SectionState<List<MemoryWithMediaModel>>
            val recentMemoriesList = values[4] as SectionState<List<MemoryWithMediaModel>>
            val searchResultsList = values[5] as List<MemoryWithMediaModel>
            val onThisDayList = values[6] as List<MemoryWithMediaModel>

            SearchState(
                inputText = input,
                tags = tagsList,
                selectedTag = selectedTag,
                recentSearches = recentSearchList,
                recentMemories = recentMemoriesList,
                searchResults = searchResultsList,
                onThisDay = onThisDayList,
            )
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                SearchState()
            )


    @OptIn(FlowPreview::class)
    fun onEvent(event: SearchEvents) {
        when (event) {
            is SearchEvents.InputTextChange -> {
                _inputText.update { event.input }
            }

            is SearchEvents.ClearInput -> {
                _inputText.update {""}
            }

            is SearchEvents.AddSearch -> {
                viewModelScope.launch {
                    searchUseCase.saveSearchIdUseCase(event.memoryId)
                }
            }

            is SearchEvents.SelectTag -> {
                _currentTag.update { event.tag }
            }

            is SearchEvents.DeleteAllSearch -> {
                viewModelScope.launch {
                    searchUseCase.deleteAllSearchUseCase()
                }
            }

            is SearchEvents.DeleteSearch -> {
                viewModelScope.launch {
                    searchUseCase.deleteSearchByIdUseCase(event.memoryId)
                }
            }

        }
    }


    companion object {
        private const val TAG = "SearchViewModel"
    }


}

@Stable
data class SearchState(
    val inputText: String = "",
    val tags: SectionState<List<TagModel>> = SectionState.Loading,
    val selectedTag: TagModel? = null,
    val recentSearches: SectionState<List<MemoryWithMediaModel>> = SectionState.Loading,

    val recentMemories: SectionState<List<MemoryWithMediaModel>> = SectionState.Loading,

    val searchResults: List<MemoryWithMediaModel> = emptyList(),
    val onThisDay: List<MemoryWithMediaModel> = emptyList(),
)
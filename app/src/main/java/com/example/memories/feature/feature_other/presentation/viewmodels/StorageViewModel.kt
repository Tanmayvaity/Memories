package com.example.memories.feature.feature_other.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.feature.feature_feed.domain.model.FetchType
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.SortType
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.FeedUseCaseWrapper
import com.example.memories.feature.feature_feed.domain.usecase.search_usecase.SearchUseCase
import com.example.memories.feature.feature_feed.domain.usecase.tag_usecase.TagUseCaseWrapper
import com.example.memories.feature.feature_feed.presentation.common.MemoryAction
import com.example.memories.feature.feature_feed.presentation.common.MemoryActionHandler
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.common.toSectionState
import com.example.memories.feature.feature_feed.presentation.tags.SortBy
import com.example.memories.feature.feature_other.domain.model.StorageStats
import com.example.memories.feature.feature_other.domain.usecase.SystemUseCaseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class StorageViewModel @Inject constructor(
    private val systemUseCase: SystemUseCaseWrapper,
    private val searchUseCase: SearchUseCase,
    private val tagUseCase: TagUseCaseWrapper,
    private val memoryActionHandler: MemoryActionHandler
) : ViewModel() {

    val refresh: StateFlow<Long>
        field = MutableStateFlow(1)

    val deletingCache : StateFlow<Boolean>
        field = MutableStateFlow(false)

    val state : StateFlow<StorageState> = refresh
        .mapLatest {
            val stats = systemUseCase.getStorageStatsUseCase()
            StorageState(
                isLoading = false,
                isSupported = true,
                stats = stats
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StorageState()
        )

    /* ----------------------------- Manage data ----------------------------- */

    val memoryQuery: StateFlow<String>
        field = MutableStateFlow("")

    val tagQuery: StateFlow<String>
        field = MutableStateFlow("")

    val showHidden: StateFlow<Boolean>
        field = MutableStateFlow(false)

    val latestMemories: Flow<PagingData<MemoryWithMediaModel>> =
        combine(
            memoryQuery.debounce(300).distinctUntilChanged(),
            showHidden
        ) { query, hidden -> query to hidden }
            .flatMapLatest { (query, hidden) ->
                searchUseCase.searchMemoriesPagedUseCase(query, hidden)
            }
            .cachedIn(viewModelScope)

    val tags: StateFlow<SectionState<List<TagWithMemoryCountModel>>> =
        tagQuery
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                tagUseCase.getTagsWithMemoryCountUseCase(
                    sortOrder = SortOrder.Descending,
                    sortBy = SortBy.Count,
                    query = query
                )
            }
            .toSectionState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SectionState.Loading
            )

    val recentSearches: StateFlow<SectionState<List<MemoryWithMediaModel>>> =
        searchUseCase.fetchRecentSearchUseCase()
            .flatMapLatest { searches ->
                searchUseCase.fetchMemoryByIdsUseCase(searches.map { it.memoryId })
            }
            .toSectionState()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SectionState.Loading
            )

    fun onEvent(event : StorageEvents){
        when(event){
            is StorageEvents.DeleteCache -> {
                viewModelScope.launch {
                    deletingCache.update { true }
                    systemUseCase.deleteCacheUseCase()
                    refresh.update { it + 1 }
                    deletingCache.update { false }
                }
            }

            is StorageEvents.MemoryQueryChange -> memoryQuery.update { event.query }

            is StorageEvents.ToggleShowHidden -> showHidden.update { !it }

            is StorageEvents.TagQueryChange -> tagQuery.update { event.query }

            is StorageEvents.DeleteMemories -> {
                viewModelScope.launch {
                    event.memories.forEach { item ->
                        memoryActionHandler.handle(
                            MemoryAction.Delete(
                                memory = item.memory,
                                uriList = item.mediaList.map { it.uri }
                            )
                        )
                    }
                }
            }

            is StorageEvents.DeleteTags -> {
                viewModelScope.launch {
                    event.ids.forEach { id ->
                        tagUseCase.deleteTagUseCase(id)
                    }
                }
            }

            is StorageEvents.DeleteRecentSearch -> {
                viewModelScope.launch {
                    searchUseCase.deleteSearchByIdUseCase(event.memoryId)
                }
            }

            is StorageEvents.ClearAllRecentSearches -> {
                viewModelScope.launch {
                    searchUseCase.deleteAllSearchUseCase()
                }
            }
        }
    }

}

data class StorageState(
    val isLoading: Boolean = true,
    val isSupported: Boolean = true,
    val stats: StorageStats? = null,
)


sealed interface StorageEvents{
    object DeleteCache : StorageEvents

    data class MemoryQueryChange(val query: String) : StorageEvents
    object ToggleShowHidden : StorageEvents
    data class TagQueryChange(val query: String) : StorageEvents
    data class DeleteMemories(val memories: List<MemoryWithMediaModel>) : StorageEvents
    data class DeleteTags(val ids: List<String>) : StorageEvents
    data class DeleteRecentSearch(val memoryId: String) : StorageEvents
    object ClearAllRecentSearches : StorageEvents
}

package com.example.memories.feature.feature_feed.presentation.tags

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memories.feature.feature_feed.domain.model.SortOrder
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_feed.domain.usecase.tag_usecase.TagUseCaseWrapper
import com.example.memories.feature.feature_feed.presentation.common.SectionState
import com.example.memories.feature.feature_feed.presentation.common.toSectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class TagsViewModel @Inject constructor(
    val tagsUseCase: TagUseCaseWrapper
) : ViewModel() {

    data class FetchType(
        val orderByType: SortOrder,
        val sortByType: SortBy
    )


    private val _fetchType = MutableStateFlow(
        FetchType(
            orderByType = SortOrder.Descending,
            sortByType = SortBy.Count
        )
    )

    private val _state = MutableStateFlow(TagsState())
    val state = _state.asStateFlow()

    private val _inputText = MutableStateFlow<String>("")
    val inputText = _inputText.asStateFlow()


    val tags: StateFlow<SectionState<List<TagWithMemoryCountModel>>> = combine(
        _inputText.debounce(300),
        _fetchType
    ) { query, fetchType ->
        Triple<String, SortOrder, SortBy>(query, fetchType.orderByType, fetchType.sortByType)
    }
        .flatMapLatest { (query, sortBy, orderBy) ->
            tagsUseCase.getTagsWithMemoryCountUseCase(
                query = query,
                sortOrder = sortBy,
                sortBy = orderBy
            )
        }
        .toSectionState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SectionState.Loading
        )

    fun onEvent(event: TagEvents) {
        when (event) {

            is TagEvents.InputTextChange -> {
                _inputText.update { event.value }
            }

            is TagEvents.CreateTag -> {
                _state.update { it.copy(isTagInserting = true) }

                viewModelScope.launch {
                    tagsUseCase.addTagUseCase(event.name)
                    _state.update { it.copy(isTagInserting = false) }
                }
            }

            is TagEvents.ApplyFilter -> {
                _fetchType.update {
                    it.copy(
                        orderByType = state.value.orderByType,
                        sortByType = state.value.sortByType
                    )
                }
            }

            is TagEvents.DeleteTag -> {
                viewModelScope.launch {
                    tagsUseCase.deleteTagUseCase(event.id)
                }
            }

            is TagEvents.ChangeSortOrderBy -> {
                _state.update { it.copy(orderByType = event.type) }
            }

            is TagEvents.ChangeSortBy -> {
                _state.update { it.copy(sortByType = event.type) }
            }
        }
    }


}


data class TagsState(
    val sortByType: SortBy = SortBy.Count,
    val orderByType: SortOrder = SortOrder.Descending,
    val isTagInserting: Boolean = false,
)
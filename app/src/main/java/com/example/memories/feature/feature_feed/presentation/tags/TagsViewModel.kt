package com.example.memories.feature.feature_feed.presentation.tags

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Index
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
import com.example.memories.feature.feature_feed.domain.usecase.tag_usecase.TagUseCaseWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TagsViewModel @Inject constructor(
    val tagsUseCase : TagUseCaseWrapper
) : ViewModel() {


    val state = tagsUseCase.getTagsWithMemoryCountUseCase()
        .map { tags ->
            TagsState(
                tags = tags,
                isLoading = false
            )
        }
        .onStart {
            emit(TagsState(isLoading = true))
        }
        .catch { e ->
            emit(
                TagsState(
                    isLoading = false,
                    error = e.message
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TagsState(isLoading = true)
        )

    private val _inputText =  MutableStateFlow<String>("")
    val inputText = _inputText.asStateFlow()

    fun onEvent(event : TagEvents){
        when(event){
            is TagEvents.Fetch -> {

            }

            is TagEvents.DeleteTag -> {
                viewModelScope.launch {
                    tagsUseCase.deleteTagUseCase(event.id)
                }
            }
        }
    }


}


data class TagsState(
    val isLoading : Boolean = false,
    val error : String? = null,
    val tags : List<TagWithMemoryCountModel> = emptyList(),
    val sortBy : SortBy = SortBy.Count,
    val orderBy : SortOrder = SortOrder.Descending,
)
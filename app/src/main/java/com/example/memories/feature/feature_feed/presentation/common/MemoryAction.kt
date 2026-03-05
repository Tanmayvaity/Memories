package com.example.memories.feature.feature_feed.presentation.common

import android.R.attr.action
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.Result
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.DeleteUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.ToggleFavouriteUseCase
import com.example.memories.feature.feature_feed.domain.usecase.feed_usecase.ToggleHiddenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MemoryAction {
    data class ToggleFavourite(val id: String, val currentFavouriteState: Boolean) : MemoryAction
    data class ToggleHidden(val id: String, val currentHiddenState: Boolean) : MemoryAction
    data class Delete(val memory: MemoryModel, val uriList: List<String>) : MemoryAction
}

class MemoryActionHandler @Inject constructor(
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    private val toggleHiddenUseCase: ToggleHiddenUseCase,
    private val deleteUseCase: DeleteUseCase,
) {
    suspend fun handle(action: MemoryAction) {
        when (action) {
            is MemoryAction.ToggleFavourite -> {
                toggleFavouriteUseCase(action.id, !action.currentFavouriteState)
            }
            is MemoryAction.ToggleHidden -> {
                toggleHiddenUseCase(action.id, !action.currentHiddenState)
            }
            is MemoryAction.Delete -> {
                deleteUseCase(action.memory, action.uriList)
            }
        }
    }

    suspend fun delete(memory: MemoryModel, uriList: List<String>): Result<String> {
        return deleteUseCase(memory, uriList)
    }
}
package com.example.memories.feature.feature_feed.presentation.common

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class SectionState<out T> {
    data object Loading : SectionState<Nothing>()
    data class Success<T>(val data: T) : SectionState<T>()
    data class Error(val message: String? = null) : SectionState<Nothing>()
    data object Empty : SectionState<Nothing>()
}

fun <T> Flow<List<T>>.toSectionState(): Flow<SectionState<List<T>>> {
    return this
        .map<List<T>, SectionState<List<T>>> { items ->
            if (items.isEmpty()) SectionState.Empty
            else SectionState.Success(items)
        }
        .onStart { emit(SectionState.Loading) }
        .catch { e ->
            emit(SectionState.Error(e.message))
        }
}

fun SectionState<*>.isEmpty(): Boolean {
    return this is SectionState.Empty
}


@Composable
fun <T> SectionStateContainer(
    state: SectionState<T>,
    loadingContent: @Composable () -> Unit,
    emptyContent: @Composable () -> Unit,
    errorContent: @Composable (String?) -> Unit,
    successContent: @Composable (T) -> Unit
) {
    when (state) {
        is SectionState.Loading -> {
            loadingContent()
        }

        is SectionState.Empty -> {
            emptyContent()
        }

        is SectionState.Error -> {
            errorContent(state.message)
        }

        is SectionState.Success -> {
            successContent(state.data)
        }
    }
}

package com.example.memories.core.util

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalCoroutinesApi::class)
fun <K, T : Any> pagedFlowByKey(
    key: StateFlow<K?>,
    scope: CoroutineScope,
    fetch: (K) -> Flow<PagingData<T>>
): Flow<PagingData<T>> = key
    .flatMapLatest { k ->
        if (k == null) flowOf(PagingData.empty())
        else fetch(k)
    }
    .cachedIn(scope)
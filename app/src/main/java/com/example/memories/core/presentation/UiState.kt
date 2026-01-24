package com.example.memories.core.presentation

data class UiState<T> (
    val isLoading : Boolean = false,
    val data : List<T> = emptyList(),
    val error : Throwable? = null
)
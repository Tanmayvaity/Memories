package com.example.memories.core.presentation

sealed class MediaResult<out T> {
    data class Success<T>(val data: T) : MediaResult<T>()
    data class Error(val message: String) : MediaResult<Nothing>()
}
package com.example.memories.model.models

sealed class MediaResult {
    data class Success(val successMessage : String) : MediaResult()
    data class Error(val error:Throwable) : MediaResult()
}
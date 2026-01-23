package com.example.memories.feature.feature_other.domain.model

sealed interface DeletionStatus {
    data object Idle : DeletionStatus
    data class InProgress(val step: String?) : DeletionStatus
    data object Success : DeletionStatus
    data class Error(val message: String) : DeletionStatus
}
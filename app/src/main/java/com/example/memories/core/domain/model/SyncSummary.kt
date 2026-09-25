package com.example.memories.core.domain.model

/** What the feed's sync status card shows for the signed-in account. */
data class SyncSummary(
    val pendingCount: Int,
    val isUploading: Boolean = false,
)

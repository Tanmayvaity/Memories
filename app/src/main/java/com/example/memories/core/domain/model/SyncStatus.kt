package com.example.memories.core.domain.model

/**
 * Tracks what still has to happen remotely for a row.
 *
 * Rows owned by [LOCAL_OWNER] are never uploaded — they carry a pending status only so that
 * they can be claimed and pushed once the user signs in.
 */
enum class SyncStatus {
    SYNCED,
    CREATE_SYNC_PENDING,
    UPDATE_SYNC_PENDING,
    SYNC_FAILED,
    DELETE_SYNC_PENDING;

    companion object {
        fun fromName(name: String): SyncStatus =
            entries.firstOrNull { it.name == name } ?: CREATE_SYNC_PENDING
    }
}

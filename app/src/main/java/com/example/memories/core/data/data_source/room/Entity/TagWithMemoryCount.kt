package com.example.memories.core.data.data_source.room.Entity

import androidx.room.ColumnInfo

data class TagWithMemoryCount(
    @ColumnInfo("tag_id")
    val tagId: String,
    val label: String,
    @ColumnInfo("memory_count")
    val memoryCount: Int
)

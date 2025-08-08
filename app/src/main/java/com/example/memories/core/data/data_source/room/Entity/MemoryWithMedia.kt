package com.example.memories.core.data.data_source.room.Entity

import androidx.room.Embedded
import androidx.room.Relation

data class MemoryWithMedia(
    @Embedded val memory : MemoryEntity,
    @Relation(
        parentColumn = "memory_id",
        entityColumn = "memory_id",
    )
    val list : List<MediaEntity>
)
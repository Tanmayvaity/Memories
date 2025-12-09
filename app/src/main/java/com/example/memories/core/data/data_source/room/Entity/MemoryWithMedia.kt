package com.example.memories.core.data.data_source.room.Entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class MemoryWithMedia(
    @Embedded val memory: MemoryEntity,
    @Relation(
        parentColumn = "memory_id",
        entityColumn = "memory_id",
    )
    val list: List<MediaEntity>,
    @Relation(
        parentColumn = "memory_id",
        entityColumn = "tag_id",
        associateBy = Junction(MemoryTagCrossRef::class)
    )
    val tags: List<TagEntity>,
)

data class TagsWithMemory(
    @Embedded val tag: TagEntity,
    @Relation(
        parentColumn = "tag_id",
        entityColumn = "memory_id",
        associateBy = Junction(MemoryTagCrossRef::class)
    )
    val memories: List<MemoryEntity>,
//        @Relation(
//        parentColumn = "memory_id",
//        entityColumn = "memory_id",
//    )
//    val list: List<MediaEntity>,
)
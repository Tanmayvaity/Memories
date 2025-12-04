package com.example.memories.core.data.data_source.room.mapper

import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.data.data_source.room.Entity.TagEntity
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.TagModel

fun TagEntity.toDomain() : TagModel {
    return TagModel(
        tagId = tagId,
        label = label,
    )
}
fun TagModel.toEntity() : TagEntity {
    return TagEntity(
        tagId = tagId,
        label = label,
    )
}






package com.example.memories.core.data.data_source.room.mapper

import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.domain.model.MemoryModel

fun MemoryEntity.toDomain() : MemoryModel {
    return MemoryModel(
        memoryId = memoryId,
        title = title,
        content = content,
        hidden = hidden,
        favourite = favourite,
        timeStamp = timeStamp
    )
}
fun MemoryModel.toEntity() : MemoryEntity {
    return MemoryEntity(
        title = title,
        content = content,
        memoryId = memoryId,
        hidden = hidden,
        favourite = favourite,
        timeStamp = timeStamp,
    )
}


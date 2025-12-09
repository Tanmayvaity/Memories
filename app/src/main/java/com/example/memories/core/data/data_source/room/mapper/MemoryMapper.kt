package com.example.memories.core.data.data_source.room.mapper

import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryTagCrossRef
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.data.data_source.room.Entity.TagsWithMemory
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryTagCrossRefModel
import com.example.memories.core.domain.model.MemoryWithMediaModel
import com.example.memories.core.domain.model.TagsWithMemoryModel

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
        longitude = null,
        latitude = null
    )
}

fun MemoryWithMedia.toDomain(): MemoryWithMediaModel {
    return MemoryWithMediaModel(
        memory = memory.toDomain(),
        mediaList = list.map { it -> it.toDomain() },
        tagsList = tags.map { tag -> tag.toDomain() }
    )
}

fun MemoryWithMediaModel.toEntity() : MemoryWithMedia {
    return MemoryWithMedia(
        memory = memory.toEntity(),
        list = mediaList.map { it -> it.toEntity() },
        tags = tagsList.map { it -> it.toEntity() }
    )
}

fun TagsWithMemory.toDomain() : TagsWithMemoryModel {
    return TagsWithMemoryModel(
        tag = tag.toDomain(),
//        mediaList = list.map { it -> it.toDomain() },
        memoryList = memories.map { it -> it.toDomain() }
    )
}

fun MemoryTagCrossRef.toDomain(): MemoryTagCrossRefModel {
    return MemoryTagCrossRefModel(
        memoryId = memoryId,
        tagId = tagId
    )
}

fun MemoryTagCrossRefModel.toEntity() : MemoryTagCrossRef {
    return MemoryTagCrossRef(
        memoryId = memoryId,
        tagId = tagId
    )
}







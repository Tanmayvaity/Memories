package com.example.memories.core.data.data_source.room

import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.SearchEntity
import com.example.memories.core.data.data_source.room.Entity.TagEntity
import com.example.memories.core.domain.model.Type

/** Small builders so DAO tests stay readable. */
object TestEntities {

    fun memory(
        id: String,
        title: String = "Title $id",
        content: String = "content",
        hidden: Boolean = false,
        favourite: Boolean = false,
        timeStamp: Long = 1_000L,
        memoryForTimeStamp: Long = 1_000L,
    ) = MemoryEntity(
        memoryId = id,
        title = title,
        content = content,
        hidden = hidden,
        favourite = favourite,
        timeStamp = timeStamp,
        longitude = null,
        latitude = null,
        memoryForTimeStamp = memoryForTimeStamp,
    )

    fun media(
        id: String,
        memoryId: String,
        position: Int = 0,
        type: Type = Type.IMAGE_JPG,
        timeStamp: Long = 1_000L,
    ) = MediaEntity(
        mediaId = id,
        memoryId = memoryId,
        uri = "content://media/$id",
        hidden = false,
        favourite = false,
        timeStamp = timeStamp,
        longitude = null,
        latitude = null,
        position = position,
        type = type,
    )

    fun tag(id: String, label: String) = TagEntity(tagId = id, label = label)

    fun search(memoryId: String, timeStamp: Long) =
        SearchEntity(memoryId = memoryId, timeStamp = timeStamp)
}

package com.example.memories.core.data.data_source.room.mapper

import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryTagCrossRef
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.data.data_source.room.Entity.TagEntity
import com.example.memories.core.data.data_source.room.Entity.TagsWithMemory
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.MediaModel
import com.example.memories.core.domain.model.MemoryModel
import com.example.memories.core.domain.model.MemoryTagCrossRefModel
import com.example.memories.core.domain.model.Type
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MemoryMapperTest {

    private fun memoryEntity(
        id: String = "m1",
        title: String = "Title",
        content: String = "Content",
        hidden: Boolean = false,
        favourite: Boolean = false,
        timeStamp: Long = 100L,
        memoryFor: Long = 200L,
    ) = MemoryEntity(
        memoryId = id,
        title = title,
        content = content,
        hidden = hidden,
        favourite = favourite,
        timeStamp = timeStamp,
        longitude = 10L,
        latitude = 20L,
        memoryForTimeStamp = memoryFor,
    )

    @Test
    fun memoryEntity_toDomain_copiesAllFields() {
        val entity = memoryEntity(hidden = true, favourite = true)

        val model = entity.toDomain()

        assertEquals(entity.memoryId, model.memoryId)
        assertEquals(entity.title, model.title)
        assertEquals(entity.content, model.content)
        assertEquals(entity.hidden, model.hidden)
        assertEquals(entity.favourite, model.favourite)
        assertEquals(entity.timeStamp, model.timeStamp)
        assertEquals(entity.memoryForTimeStamp, model.memoryForTimeStamp)
    }

    @Test
    fun memoryModel_toEntity_copiesFieldsAndNullsLocation() {
        val model = MemoryModel(
            memoryId = "m2",
            title = "T",
            content = "C",
            hidden = true,
            favourite = false,
            timeStamp = 5L,
            memoryForTimeStamp = 9L,
        )

        val entity = model.toEntity()

        assertEquals("m2", entity.memoryId)
        assertEquals("T", entity.title)
        assertEquals("C", entity.content)
        assertEquals(true, entity.hidden)
        assertEquals(false, entity.favourite)
        assertEquals(5L, entity.timeStamp)
        assertEquals(9L, entity.memoryForTimeStamp)
        assertNull(entity.longitude)
        assertNull(entity.latitude)
    }

    @Test(expected = NullPointerException::class)
    fun memoryModel_toEntity_throwsWhenMemoryForTimeStampNull() {
        // memoryForTimeStamp defaults to null and toEntity force-unwraps it
        MemoryModel(title = "T", content = "C").toEntity()
    }

    @Test
    fun memoryWithMedia_toDomain_sortsMediaByPosition() {
        val memory = memoryEntity()
        val media = listOf(
            media("a", position = 2),
            media("b", position = 0),
            media("c", position = 1),
        )
        val withMedia = MemoryWithMedia(memory = memory, list = media, tags = emptyList())

        val model = withMedia.toDomain()

        assertEquals(listOf("b", "c", "a"), model.mediaList.map { it.mediaId })
    }

    @Test
    fun memoryWithMedia_toDomain_mapsTags() {
        val withMedia = MemoryWithMedia(
            memory = memoryEntity(),
            list = emptyList(),
            tags = listOf(TagEntity("t1", "work"), TagEntity("t2", "home")),
        )

        val model = withMedia.toDomain()

        assertEquals(listOf("work", "home"), model.tagsList.map { it.label })
    }

    @Test
    fun tagsWithMemory_toDomain_mapsTagAndMemories() {
        val tagsWithMemory = TagsWithMemory(
            tag = TagEntity("t1", "trips"),
            memories = listOf(memoryEntity(id = "m1"), memoryEntity(id = "m2")),
        )

        val model = tagsWithMemory.toDomain()

        assertEquals("trips", model.tag.label)
        assertEquals(listOf("m1", "m2"), model.memoryList.map { it.memoryId })
    }

    @Test
    fun memoryTagCrossRef_roundTrips() {
        val model = MemoryTagCrossRefModel(memoryId = "m1", tagId = "t1")

        val entity = model.toEntity()
        assertEquals("m1", entity.memoryId)
        assertEquals("t1", entity.tagId)

        val back = MemoryTagCrossRef("m1", "t1").toDomain()
        assertEquals(model, back)
    }

    private fun media(id: String, position: Int) = MediaEntity(
        mediaId = id,
        memoryId = "m1",
        uri = "uri/$id",
        hidden = false,
        favourite = false,
        timeStamp = 0L,
        longitude = null,
        latitude = null,
        position = position,
        type = Type.IMAGE_JPG,
    )
}

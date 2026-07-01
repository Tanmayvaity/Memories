package com.example.memories.core.data.data_source.room.mapper

import com.example.memories.core.data.data_source.room.Entity.TagEntity
import com.example.memories.core.data.data_source.room.Entity.TagWithMemoryCount
import com.example.memories.core.data.data_source.room.mapper.toDomain
import com.example.memories.core.data.data_source.room.mapper.toEntity
import com.example.memories.core.domain.model.TagModel
import org.junit.Assert.assertEquals
import org.junit.Test

class TagMapperTest {

    @Test
    fun tagEntity_toDomain_copiesFields() {
        val model = TagEntity(tagId = "t1", label = "work").toDomain()

        assertEquals("t1", model.tagId)
        assertEquals("work", model.label)
    }

    @Test
    fun tagModel_toEntity_copiesFields() {
        val entity = TagModel(tagId = "t2", label = "home").toEntity()

        assertEquals("t2", entity.tagId)
        assertEquals("home", entity.label)
    }

    @Test
    fun tagWithMemoryCount_toDomain_mapsLabelToTagLabel() {
        val model = TagWithMemoryCount(tagId = "t1", label = "trips", memoryCount = 4).toDomain()

        assertEquals("t1", model.tagId)
        assertEquals("trips", model.tagLabel)
        assertEquals(4, model.memoryCount)
    }
}

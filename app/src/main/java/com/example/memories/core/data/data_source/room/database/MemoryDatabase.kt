package com.example.memories.core.data.data_source.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryTagCrossRef
import com.example.memories.core.data.data_source.room.Entity.SearchEntity
import com.example.memories.core.data.data_source.room.Entity.TagEntity
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.MemoryTagCrossRefDao
import com.example.memories.core.data.data_source.room.dao.SearchDao
import com.example.memories.core.data.data_source.room.dao.TagDao

@Database(
    entities = [
        MemoryEntity::class,
        MediaEntity::class,
        TagEntity::class,
        MemoryTagCrossRef::class,
        SearchEntity::class
               ],
    version = 4
)
abstract class MemoryDatabase : RoomDatabase() {
    abstract val mediaDao: MediaDao
    abstract val memoryDao : MemoryDao
    abstract val tagDao : TagDao
    abstract val memoryTagCrossRefDao : MemoryTagCrossRefDao
    abstract val searchDao : SearchDao
}

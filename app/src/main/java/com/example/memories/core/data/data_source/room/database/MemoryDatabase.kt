package com.example.memories.core.data.data_source.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryEntity
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.dao.MemoryDao

@Database(
    entities = [MemoryEntity::class, MediaEntity::class],
    version = 1
)
abstract class MemoryDatabase : RoomDatabase() {
    abstract val mediaDao: MediaDao
    abstract val memoryDao : MemoryDao
}
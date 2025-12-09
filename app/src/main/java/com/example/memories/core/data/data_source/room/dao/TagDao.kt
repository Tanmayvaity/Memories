package com.example.memories.core.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.memories.core.data.data_source.room.Entity.MediaEntity
import com.example.memories.core.data.data_source.room.Entity.MemoryWithMedia
import com.example.memories.core.data.data_source.room.Entity.TagEntity
import com.example.memories.core.data.data_source.room.Entity.TagsWithMemory
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM TagEntity")
    fun getAllTags(): Flow<List<TagEntity>>

    @Transaction
    @Query("SELECT * FROM TagEntity WHERE label LIKE '%' || :label || '%'")
    fun getAllTagsByLabel(label : String): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTags(tags: List<TagEntity>)


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag : TagEntity)


    @Transaction
    @Query("SELECT * FROM TagEntity where tag_id = :id")
    fun getMemoryByTag(id : String): Flow<TagsWithMemory>

}
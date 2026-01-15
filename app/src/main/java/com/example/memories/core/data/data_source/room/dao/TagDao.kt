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
import com.example.memories.core.data.data_source.room.Entity.TagWithMemoryCount
import com.example.memories.core.data.data_source.room.Entity.TagsWithMemory
import com.example.memories.feature.feature_feed.domain.model.TagWithMemoryCountModel
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


    @Transaction
    @Query("DELETE FROM TagEntity WHERE tag_id = :id")
    suspend fun deleteTag(id : String)

    @Transaction
    @Query(
        """
SELECT 
    t.tag_id,
    t.label,
    COUNT(mt.memory_id) AS memory_count
FROM tagentity t
LEFT JOIN MemoryTagCrossRef mt 
    ON t.tag_id = mt.tag_id
GROUP BY t.tag_id, t.label
ORDER BY memory_count DESC
        """
    )
    fun getTagsWithMemoryCount() : Flow<List<TagWithMemoryCount>>


    @Transaction
    @Query(
        """
SELECT 
    t.tag_id,
    t.label,
    COUNT(mt.memory_id) AS memory_count
FROM tagentity t
LEFT JOIN MemoryTagCrossRef mt 
    ON t.tag_id = mt.tag_id
GROUP BY t.tag_id, t.label
ORDER BY label DESC
        """
    )
    fun getTagsWithMemoryCountByLabel() : Flow<List<TagWithMemoryCount>>



    @Transaction
    @Query(
        """
SELECT 
    t.tag_id,
    t.label,
    COUNT(mt.memory_id) AS memory_count
FROM tagentity t
LEFT JOIN MemoryTagCrossRef mt 
    ON t.tag_id = mt.tag_id
GROUP BY t.tag_id, t.label
ORDER BY memory_count ASC
        """
    )
    fun getTagsWithMemoryCountAscending() : Flow<List<TagWithMemoryCount>>


    @Transaction
    @Query(
        """
SELECT 
    t.tag_id,
    t.label,
    COUNT(mt.memory_id) AS memory_count
FROM tagentity t
LEFT JOIN MemoryTagCrossRef mt 
    ON t.tag_id = mt.tag_id
GROUP BY t.tag_id, t.label
ORDER BY label ASC
        """
    )
    fun getTagsWithMemoryCountByLabelAscending() : Flow<List<TagWithMemoryCount>>


    @Transaction
    @Query(
        """
    SELECT 
        t.tag_id,
        t.label,
        COUNT(mt.memory_id) AS memory_count
    FROM tagentity t
    LEFT JOIN MemoryTagCrossRef mt 
        ON t.tag_id = mt.tag_id
    WHERE t.label LIKE '%' || :query || '%'
    GROUP BY t.tag_id, t.label
    ORDER BY memory_count DESC
    """
    )
    fun getTagsWithMemoryCountBySearch(query: String): Flow<List<TagWithMemoryCount>>

}
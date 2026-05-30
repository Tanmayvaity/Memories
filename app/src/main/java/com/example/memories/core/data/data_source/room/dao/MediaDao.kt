package com.example.memories.core.data.data_source.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.memories.core.data.data_source.room.Entity.MediaEntity

@Dao
interface MediaDao {

    @Query("SELECT * FROM MediaEntity")
    suspend fun getAllMedia(): List<MediaEntity>

    @Query("SELECT * FROM MediaEntity WHERE favourite = 1")
    suspend fun getAllFavouriteMedia(): List<MediaEntity>

    @Query("SELECT * FROM MediaEntity WHERE hidden = 1")
    suspend fun getAllHiddenMedia(): List<MediaEntity>

//    @Query("SELECT * FROM MediaEntity WHERE hidden = 0 ORDER BY time_stamp DESC")
    @Query("""
        SELECT media.* FROM MediaEntity AS media
INNER JOIN MemoryEntity AS memory ON media.memory_id = memory.memory_id
WHERE memory.hidden = 0 AND media.hidden = 0
ORDER BY media.time_stamp DESC
    """)
    fun getAllMediaPaged(): PagingSource<Int, MediaEntity>

    @Query("UPDATE MediaEntity SET favourite = :favourite WHERE media_id = :mediaId")
    suspend fun updateMediaFavourite(mediaId: String, favourite: Boolean)

//    @Insert
//    suspend fun insertAllMedia(mediaList: List<MediaEntity>)

}
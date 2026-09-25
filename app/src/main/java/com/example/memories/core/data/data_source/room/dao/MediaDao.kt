package com.example.memories.core.data.data_source.room.dao

import com.example.memories.core.domain.model.LOCAL_OWNER
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

    @Query("""
        SELECT media.* FROM MediaEntity AS media
INNER JOIN MemoryEntity AS memory ON media.memory_id = memory.memory_id
WHERE memory.hidden = :showHidden or memory.hidden = 0
ORDER BY media.time_stamp DESC
    """)
    fun getAllMediaPaged(showHidden: Boolean): PagingSource<Int, MediaEntity>

    @Query("UPDATE MediaEntity SET favourite = :favourite WHERE media_id = :mediaId")
    suspend fun updateMediaFavourite(mediaId: String, favourite: Boolean)

    /** Claims rows not yet owned by any account; rows owned by another user are left untouched. */
    @Query("UPDATE MediaEntity SET owner = :ownerId WHERE owner = '$LOCAL_OWNER'")
    suspend fun updateOwner(ownerId: String)

//    @Insert
//    suspend fun insertAllMedia(mediaList: List<MediaEntity>)

}
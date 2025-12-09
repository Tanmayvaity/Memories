package com.example.memories.core.data.data_source.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.memories.core.data.data_source.room.Entity.SearchEntity
import com.example.memories.navigation.TopLevelScreen
import kotlinx.coroutines.flow.Flow


@Dao
interface SearchDao {

    @Upsert
    suspend fun insertSearch(search: SearchEntity)


    @Query(
        """
        DELETE FROM SearchEntity
        WHERE memory_id NOT IN (
        SELECT memory_id FROM SearchEntity
        ORDER BY time_stamp DESC
        LIMIT 20
    )
    """
    )
    suspend fun trimRecentSearch()

    @Transaction
    suspend fun insertAndTrim(search: SearchEntity) {
        insertSearch(search)
        trimRecentSearch()

    }

    @Query("SELECT * FROM SearchEntity ORDER BY time_stamp DESC")
    fun fetchRecentSearch(): Flow<List<SearchEntity>>
}
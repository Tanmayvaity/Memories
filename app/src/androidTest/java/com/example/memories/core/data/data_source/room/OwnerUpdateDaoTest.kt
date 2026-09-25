package com.example.memories.core.data.data_source.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.memories.core.data.data_source.room.dao.MediaDao
import com.example.memories.core.data.data_source.room.dao.MemoryDao
import com.example.memories.core.data.data_source.room.dao.TagDao
import com.example.memories.core.data.data_source.room.database.MemoryDatabase
import com.example.memories.core.domain.model.LOCAL_OWNER
import com.example.memories.core.domain.model.SyncStatus
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * `updateOwner` on each DAO must only claim rows still owned by [LOCAL_OWNER]; rows that already
 * belong to an account are never reassigned.
 */
@RunWith(AndroidJUnit4::class)
class OwnerUpdateDaoTest {

    private lateinit var db: MemoryDatabase
    private lateinit var memoryDao: MemoryDao
    private lateinit var mediaDao: MediaDao
    private lateinit var tagDao: TagDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MemoryDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        memoryDao = db.memoryDao
        mediaDao = db.mediaDao
        tagDao = db.tagDao
    }

    @After
    fun tearDown() {
        if (::db.isInitialized) db.close()
    }

    // region MemoryDao

    @Test
    fun memoryUpdateOwner_claimsLocalRows() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1"))
        memoryDao.insertMemory(TestEntities.memory("m2"))

        memoryDao.updateOwner(USER_A)

        assertEquals(mapOf("m1" to USER_A, "m2" to USER_A), owners("MemoryEntity", "memory_id"))
    }

    @Test
    fun memoryUpdateOwner_leavesOtherUsersRowsUntouched() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1", owner = USER_A))
        memoryDao.insertMemory(TestEntities.memory("m2"))

        memoryDao.updateOwner(USER_B)

        assertEquals(mapOf("m1" to USER_A, "m2" to USER_B), owners("MemoryEntity", "memory_id"))
    }

    @Test
    fun memoryUpdateOwner_toLocal_isNoOpForOwnedRows() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1", owner = USER_A))

        memoryDao.updateOwner(LOCAL_OWNER)

        assertEquals(mapOf("m1" to USER_A), owners("MemoryEntity", "memory_id"))
    }

    @Test
    fun memoryUpdateOwner_doesNotChangeSyncStatus() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1"))

        memoryDao.updateOwner(USER_A)

        assertEquals(
            SyncStatus.CREATE_SYNC_PENDING.name,
            column("SELECT sync_status FROM MemoryEntity WHERE memory_id = 'm1'")
        )
    }

    // endregion

    // region MediaDao

    @Test
    fun mediaUpdateOwner_claimsOnlyLocalRows() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1"))
        memoryDao.insertAllMedia(
            listOf(
                TestEntities.media("md1", "m1"),
                TestEntities.media("md2", "m1", owner = USER_A),
            )
        )

        mediaDao.updateOwner(USER_B)

        assertEquals(mapOf("md1" to USER_B, "md2" to USER_A), owners("MediaEntity", "media_id"))
    }

    @Test
    fun mediaUpdateOwner_doesNotTouchMemoryTable() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1"))
        memoryDao.insertAllMedia(listOf(TestEntities.media("md1", "m1")))

        mediaDao.updateOwner(USER_A)

        assertEquals(mapOf("m1" to LOCAL_OWNER), owners("MemoryEntity", "memory_id"))
    }

    // endregion

    // region TagDao

    @Test
    fun tagUpdateOwner_claimsOnlyLocalRows() = runTest {
        tagDao.insertTag(TestEntities.tag("t1", "Work"))
        tagDao.insertTag(TestEntities.tag("t2", "Home", owner = USER_A))

        tagDao.updateOwner(USER_B)

        assertEquals(mapOf("t1" to USER_B, "t2" to USER_A), owners("TagEntity", "tag_id"))
    }

    // endregion

    /**
     * local rows → A signs in → A signs out (no owner update) → new local rows → B signs in.
     * A's rows must survive B's sign-in.
     */
    @Test
    fun signInSignOutSignIn_eachAccountOnlyClaimsLocalRows() = runTest {
        memoryDao.insertMemory(TestEntities.memory("m1"))
        tagDao.insertTag(TestEntities.tag("t1", "Work"))
        claimAll(USER_A)

        memoryDao.insertMemory(TestEntities.memory("m2", owner = USER_A))
        memoryDao.insertMemory(TestEntities.memory("m3"))
        tagDao.insertTag(TestEntities.tag("t2", "Home"))
        claimAll(USER_B)

        assertEquals(
            mapOf("m1" to USER_A, "m2" to USER_A, "m3" to USER_B),
            owners("MemoryEntity", "memory_id")
        )
        assertEquals(mapOf("t1" to USER_A, "t2" to USER_B), owners("TagEntity", "tag_id"))
    }

    private suspend fun claimAll(ownerId: String) {
        memoryDao.updateOwner(ownerId)
        mediaDao.updateOwner(ownerId)
        tagDao.updateOwner(ownerId)
    }

    private fun owners(table: String, idColumn: String): Map<String, String> =
        db.openHelper.readableDatabase
            .query("SELECT $idColumn, owner FROM $table ORDER BY $idColumn")
            .use { cursor ->
                buildMap {
                    while (cursor.moveToNext()) put(cursor.getString(0), cursor.getString(1))
                }
            }

    private fun column(sql: String): String =
        db.openHelper.readableDatabase.query(sql).use { cursor ->
            cursor.moveToFirst()
            cursor.getString(0)
        }

    private companion object {
        const val USER_A = "uid-a"
        const val USER_B = "uid-b"
    }
}

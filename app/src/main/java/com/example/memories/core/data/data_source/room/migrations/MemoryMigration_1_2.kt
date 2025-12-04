package com.example.memories.core.data.data_source.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


val MEMORY_MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // 1. Add new columns to MemoryEntity
        db.execSQL("ALTER TABLE MemoryEntity ADD COLUMN longitude INTEGER")
        db.execSQL("ALTER TABLE MemoryEntity ADD COLUMN latitude INTEGER")

        // 2. Create TagEntity
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS TagEntity (
                tag_id TEXT NOT NULL,
                memory_id TEXT NOT NULL,
                label TEXT NOT NULL,
                PRIMARY KEY(tag_id)
            )
        """.trimIndent())

        // 3. Create new MediaEntity table WITH CASCADE
        db.execSQL("""
            CREATE TABLE MediaEntity_new (
                media_id TEXT NOT NULL,
                memory_id TEXT NOT NULL,
                uri TEXT NOT NULL,
                hidden INTEGER NOT NULL,
                favourite INTEGER NOT NULL,
                time_stamp INTEGER NOT NULL,
                longitude INTEGER,
                latitude INTEGER,
                PRIMARY KEY(media_id),
                FOREIGN KEY(memory_id) REFERENCES MemoryEntity(memory_id) ON DELETE CASCADE
            )
        """.trimIndent())

        // 4. Copy data from old to new
        db.execSQL("""
            INSERT INTO MediaEntity_new
            (media_id, memory_id, uri, hidden, favourite, time_stamp)
            SELECT media_id, memory_id, uri, hidden, favourite, time_stamp
            FROM MediaEntity
        """.trimIndent())

        // 5. Drop old MediaEntity table
        db.execSQL("DROP TABLE MediaEntity")

        // 6. Rename new version to original name
        db.execSQL("ALTER TABLE MediaEntity_new RENAME TO MediaEntity")
    }
}

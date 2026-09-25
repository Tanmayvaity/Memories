package com.example.memories.core.data.data_source.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MEMORY_MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // Existing rows predate any account, so they belong to no one and have never been
        // uploaded — they become CREATE_SYNC_PENDING and get claimed on first sign-in.
        listOf("MemoryEntity", "MediaEntity", "TagEntity").forEach { table ->
            db.execSQL(
                "ALTER TABLE $table ADD COLUMN sync_status TEXT NOT NULL DEFAULT 'CREATE_SYNC_PENDING'"
            )
            db.execSQL(
                "ALTER TABLE $table ADD COLUMN owner TEXT NOT NULL DEFAULT 'local'"
            )
        }
    }
}

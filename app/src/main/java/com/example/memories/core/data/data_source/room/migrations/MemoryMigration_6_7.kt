package com.example.memories.core.data.data_source.room.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MEMORY_MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            "ALTER TABLE MediaEntity ADD COLUMN type TEXT NOT NULL DEFAULT 'unknown'"
        )

        // Backfill existing rows based on file extension in uri
        db.execSQL("UPDATE MediaEntity SET type = 'image/jpeg' WHERE uri LIKE '%.jpg' OR uri LIKE '%.jpeg'")
        db.execSQL("UPDATE MediaEntity SET type = 'image/png' WHERE uri LIKE '%.png'")
        db.execSQL("UPDATE MediaEntity SET type = 'video/mp4' WHERE uri LIKE '%.mp4'")
    }
}
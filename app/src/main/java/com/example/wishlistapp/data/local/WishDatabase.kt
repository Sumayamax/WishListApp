package com.example.wishlistapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.wishlistapp.data.local.dao.WishDao
import com.example.wishlistapp.data.local.entity.WishEntity

@Database(entities = [WishEntity::class], version = 2, exportSchema = false)
abstract class WishDatabase : RoomDatabase() {
    abstract val wishDao: WishDao

    companion object {
        const val DATABASE_NAME = "wish_db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE wishes ADD COLUMN category TEXT NOT NULL DEFAULT 'OTHER'")
            }
        }
    }
}

package com.example.plotpoints.bookmarksDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookmarkPlace::class], version = 1)
abstract class PPDatabase : RoomDatabase() {
    abstract fun bookmarkPlaceDao(): BookmarkPlaceDao
}

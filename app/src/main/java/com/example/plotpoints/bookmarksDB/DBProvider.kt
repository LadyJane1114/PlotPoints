package com.example.plotpoints.bookmarksDB

import android.content.Context
import androidx.room.Room

object DBProvider {
    private var db: PPDatabase? = null

    fun getDatabase(context: Context): PPDatabase {
        return db ?: Room.databaseBuilder(
            context.applicationContext,
            PPDatabase::class.java,
            "favorites-db"
        )
            .fallbackToDestructiveMigration(false)
            .build().also { db = it }
    }
}
package com.example.plotpoints.bookmarksDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkPlace(
    @PrimaryKey val mapboxID: String,
    val name: String,
    val address: String?,
    val latitude: Double,
    val longitude: Double
)
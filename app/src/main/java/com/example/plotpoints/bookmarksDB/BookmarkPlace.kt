package com.example.plotpoints.bookmarksDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mapbox.search.common.metadata.ImageInfo
import com.mapbox.search.common.metadata.OpenHours

@Entity(tableName = "bookmarks")
data class BookmarkPlace(
    @PrimaryKey val mapboxID: String,
    val name: String,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val makiIcon: String?,
    val distanceMeters: Double?,
    val etaMinutes: Double?
)
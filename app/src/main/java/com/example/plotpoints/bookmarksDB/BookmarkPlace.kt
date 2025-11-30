package com.example.plotpoints.bookmarksDB

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mapbox.geojson.BoundingBox
import com.mapbox.search.common.metadata.ImageInfo
import com.mapbox.search.common.metadata.OpenHours

@Entity(tableName = "bookmarks")
data class BookmarkPlace(
    @PrimaryKey val mapboxID: String,
    val name: String,
    val address: String?,
    val latitude: Double,
    val longitude: Double,
    val categories: List<String>?,
    val categoryIds: List<String>?,
    val makiIcon: String?,
    val distanceMeters: Double?,
    val etaMinutes: Double?,
    val openHours: OpenHours?,
    val phone: String?,
    val website: String?,
    val averageRating: Double?,
    val reviewCount: Int?,
    val primaryPhotos: List<ImageInfo>?,
    val otherPhotos: List<ImageInfo>
)
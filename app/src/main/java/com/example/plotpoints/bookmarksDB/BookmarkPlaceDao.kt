package com.example.plotpoints.bookmarksDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkPlaceDao {

    @Query("SELECT * FROM bookmarks")
    fun getAllBookmarks(): Flow<List<BookmarkPlace>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun addFavorite(place: BookmarkPlace)

    @Delete
    suspend fun removeFavorite(place: BookmarkPlace)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE mapboxID = :id)")
    suspend fun isFavorite(id: String): Boolean
}
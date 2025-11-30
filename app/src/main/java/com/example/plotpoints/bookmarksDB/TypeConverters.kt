package com.example.plotpoints.bookmarksDB

import androidx.room.TypeConverter
import com.mapbox.search.common.metadata.ImageInfo
import com.mapbox.search.common.metadata.OpenHours
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

object Converters {

    private val moshi = Moshi.Builder().build()

    // List<String>
    private val listStringType = Types.newParameterizedType(List::class.java, String::class.java)
    private val listStringAdapter = moshi.adapter<List<String>>(listStringType)

    @TypeConverter
    fun fromStringList(list: List<String>?): String? = list?.let { listStringAdapter.toJson(it) }

    @TypeConverter
    fun toStringList(json: String?): List<String> = json?.let { listStringAdapter.fromJson(it) } ?: emptyList()

    // OpenHours
    private val openHoursAdapter = moshi.adapter(OpenHours::class.java)

    @TypeConverter
    fun fromOpenHours(openHours: OpenHours?): String? = openHours?.let { openHoursAdapter.toJson(it) }

    @TypeConverter
    fun toOpenHours(json: String?): OpenHours? = json?.let { openHoursAdapter.fromJson(it) }

    // List<ImageInfo>
    private val listImageType = Types.newParameterizedType(List::class.java, ImageInfo::class.java)
    private val listImageAdapter = moshi.adapter<List<ImageInfo>>(listImageType)

    @TypeConverter
    fun fromImageInfoList(list: List<ImageInfo>?): String? = list?.let { listImageAdapter.toJson(it) }

    @TypeConverter
    fun toImageInfoList(json: String?): List<ImageInfo> = json?.let { listImageAdapter.fromJson(it) } ?: emptyList()
}

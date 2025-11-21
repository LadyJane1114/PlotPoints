package com.example.plotpoints.models

import com.google.gson.annotations.SerializedName

data class Features(
    val name: String,
    @SerializedName("mapbox_id")
    val mapboxID: String,
    @SerializedName("feature_type")
    val featureType: String,
    val address: String,
    @SerializedName("full_address")
    val fullAddress: String,
    val maki: String,
    @SerializedName("poi_category")
    val poiCategory: List<String>
)


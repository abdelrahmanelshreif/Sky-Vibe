package com.abdelrahman_elshreif.sky_vibe.data.model

import com.google.gson.annotations.SerializedName

data class NominatimLocation(
    @SerializedName("display_name")
    val displayName: String,
    val lat: Double,
    val lon: Double
)
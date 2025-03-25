package com.abdelrahman_elshreif.sky_vibe.model

import com.google.gson.annotations.SerializedName

data class Rain(
    @SerializedName("3h")
    val threeHours: Double
)
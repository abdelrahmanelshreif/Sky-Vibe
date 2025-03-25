package com.abdelrahman_elshreif.sky_vibe.model

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
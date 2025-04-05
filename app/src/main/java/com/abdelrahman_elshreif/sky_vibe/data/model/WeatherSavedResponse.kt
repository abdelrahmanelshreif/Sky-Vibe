package com.abdelrahman_elshreif.sky_vibe.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_data")
data class WeatherDataEntity(
    @PrimaryKey
    val id: Int = 0,
    val jsonData: String
)
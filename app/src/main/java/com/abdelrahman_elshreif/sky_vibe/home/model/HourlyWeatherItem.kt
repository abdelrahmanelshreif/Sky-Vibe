package com.abdelrahman_elshreif.sky_vibe.home.model

data class HourlyWeatherItem(
    val timestamp: Long,
    val temperature: Double,
    val feelsLikeTemperature: Double,
    val weatherCondition: String,
    val weatherDescription: String,
    val icon: String,
    val windSpeed: Double,
    val humidity: Int
)

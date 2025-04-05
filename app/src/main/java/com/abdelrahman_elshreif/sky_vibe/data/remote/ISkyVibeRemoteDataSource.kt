package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse

interface ISkyVibeRemoteDataSource {
    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
    ): WeatherResponse?

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): WeatherResponse?

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): WeatherResponse?

    suspend fun getSuggestedLocations(query: String): List<NominatimLocation>
}
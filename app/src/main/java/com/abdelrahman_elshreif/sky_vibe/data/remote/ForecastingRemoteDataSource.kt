package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.model.WeatherForecastResponse
import com.abdelrahman_elshreif.sky_vibe.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Suppress("UNCHECKED_CAST")
class ForecastingRemoteDataSource(private val services: SkyVibeApiServices) {

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
    ): WeatherResponse? {
        return services.getWeatherByCoordinates(lat, lon).body()
    }

    suspend fun getForecastDataOfCoordinates(
        lat:Double,
        lon:Double
    ): WeatherForecastResponse? {
        return services.getForecastByCoordinates(lat,lon).body()
    }

}

package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse

@Suppress("UNCHECKED_CAST")
class ForecastingRemoteDataSource(private val services: SkyVibeApiServices) {

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
    ): WeatherResponse? {
        return services.getWeatherByCoordinates(lat, lon).body()
    }

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): WeatherResponse? {
        return services.getWeatherByCoordinates(lat, lon, language = lang).body()
    }

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): WeatherResponse? {
        return services.getWeatherByCoordinates(lat, lon, language = lang, units = unit).body()
    }


}

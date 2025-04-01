package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse

@Suppress("UNCHECKED_CAST")
class ForecastingRemoteDataSource(private val skyVibeservices: SkyVibeApiServices,private val osmServices: OSMApiServices) {

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
    ): WeatherResponse? {
        return skyVibeservices.getWeatherByCoordinates(lat, lon).body()
    }

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): WeatherResponse? {
        return skyVibeservices.getWeatherByCoordinates(lat, lon, language = lang).body()
    }

    suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): WeatherResponse? {
        return skyVibeservices.getWeatherByCoordinates(lat, lon, language = lang, units = unit).body()
    }

    suspend fun getSuggestedLocations(query:String): List<NominatimLocation> {
        return osmServices.searchLocations(query)
    }


}

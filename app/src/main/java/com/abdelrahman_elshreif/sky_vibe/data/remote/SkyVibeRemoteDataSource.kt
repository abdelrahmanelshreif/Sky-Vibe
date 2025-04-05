package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse

@Suppress("UNCHECKED_CAST")
class SkyVibeRemoteDataSource(private val skyVibeservices: SkyVibeApiServices, private val osmServices: OSMApiServices) :
    ISkyVibeRemoteDataSource {

    override suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
    ): WeatherResponse? {
        return skyVibeservices.getWeatherByCoordinates(lat, lon).body()
    }

    override suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): WeatherResponse? {
        return skyVibeservices.getWeatherByCoordinates(lat, lon, language = lang).body()
    }

    override suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): WeatherResponse? {
        return skyVibeservices.getWeatherByCoordinates(lat, lon, language = lang, units = unit).body()
    }

    override suspend fun getSuggestedLocations(query:String): List<NominatimLocation> {
        return osmServices.searchLocations(query)
    }


}

package com.abdelrahman_elshreif.sky_vibe.data.repo

import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherDataEntity
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface ISkyVibeRepository {
    // Weather Data
    fun getWeatherByCoordinates(lat: Double, lon: Double): Flow<WeatherResponse?>
    fun getWeatherByCoordinates(lat: Double, lon: Double, lang: String): Flow<WeatherResponse?>
    fun getWeatherByCoordinates(lat: Double, lon: Double, lang: String, unit: String): Flow<WeatherResponse?>
    suspend fun getLastSavedWeather(): WeatherDataEntity?
    suspend fun insertWeatherData(weatherData: WeatherDataEntity): Long

    // Favorite Locations
    suspend fun getAllSavedLocations(): Flow<List<SkyVibeLocation>>
    suspend fun addLocationToFavourite(location: SkyVibeLocation): Long
    suspend fun deleteLocationFromFavourite(location: SkyVibeLocation)
    fun searchLocations(query: String): Flow<List<NominatimLocation>>

    // Alerts
    suspend fun getAlerts(): Flow<List<WeatherAlert>>
    suspend fun addNewAlert(weatherAlert: WeatherAlert): Long
    suspend fun deleteAlert(weatherAlert: WeatherAlert)
    suspend fun updateAlert(weatherAlert: WeatherAlert)
    suspend fun disableAlert(alertId: Long)

    // Location Storage
    suspend fun getSavedLocation(): Pair<Double, Double>?
    suspend fun saveLocation(lat: Double, lon: Double)
    suspend fun clearLocation()

    // Settings
    suspend fun getTempUnit(): Flow<String>
    suspend fun getWindUnit(): Flow<String>
    suspend fun getLanguage(): Flow<String>
    suspend fun getLocationMethod(): Flow<String>
    suspend fun saveTempUnit(unit: String)
    suspend fun saveWindUnit(unit: String)
    suspend fun saveLanguage(language: String)
    suspend fun saveLocationMethod(method: String)
}
package com.abdelrahman_elshreif.sky_vibe.data.local

import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherDataEntity
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface ISkyVibeLocalDataSource {
    suspend fun getFavouriteLocations(): Flow<List<SkyVibeLocation>>

    suspend fun addLocationToFavourite(location: SkyVibeLocation): Long

    suspend fun deleteLocationFromFavourite(location: SkyVibeLocation)

    suspend fun getAlerts(): Flow<List<WeatherAlert>>

    suspend fun addAlert(weatherAlert: WeatherAlert): Long

    suspend fun deleteAlert(weatherAlert: WeatherAlert)

    suspend fun updateAlert(weatherAlert: WeatherAlert)
    suspend fun disableAlert(alertId: Long)
    suspend fun getLastSavedWeather(): WeatherDataEntity?
    suspend fun insertWeatherData(weatherData: WeatherDataEntity): Long
}
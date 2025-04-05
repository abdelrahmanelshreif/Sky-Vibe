package com.abdelrahman_elshreif.sky_vibe.data.local

import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeSkyVibeLocalDataSource : ISkyVibeLocalDataSource {
    private val favouriteLocations = mutableListOf<SkyVibeLocation>()
    private val weatherAlerts = mutableListOf<WeatherAlert>()

    override suspend fun getFavouriteLocations(): Flow<List<SkyVibeLocation>> {
        return flow {
            emit(favouriteLocations)
        }
    }

    override suspend fun addLocationToFavourite(location: SkyVibeLocation): Long {
        favouriteLocations.add(location)
        return location.hashCode().toLong()
    }

    override suspend fun deleteLocationFromFavourite(location: SkyVibeLocation) {
        favouriteLocations.remove(location)
    }

    override suspend fun getAlerts(): Flow<List<WeatherAlert>> {
        return flow {
            emit(weatherAlerts)
        }
    }

    override suspend fun addAlert(weatherAlert: WeatherAlert): Long {
        weatherAlerts.add(weatherAlert)
        return weatherAlert.hashCode().toLong()
    }

    override suspend fun deleteAlert(weatherAlert: WeatherAlert) {
        weatherAlerts.remove(weatherAlert)

    }

    override suspend fun updateAlert(weatherAlert: WeatherAlert) {
        val index = weatherAlerts.indexOfFirst { it.id == weatherAlert.id }
        if (index != -1) {
            weatherAlerts[index] = weatherAlert
        }
    }

    override suspend fun disableAlert(alertId: Long) {
        TODO("Not yet implemented")
    }
}
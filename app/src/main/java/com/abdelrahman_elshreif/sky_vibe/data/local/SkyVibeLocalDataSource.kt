package com.abdelrahman_elshreif.sky_vibe.data.local

import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import kotlinx.coroutines.flow.Flow

class SkyVibeLocalDataSource(
    private val favouriteLocationDao: FavouriteLocationDao,
    private val alertsDao: WeatherAlertDao
) {

    suspend fun getFavouriteLocations(): Flow<List<SkyVibeLocation>> {
        return favouriteLocationDao.getAllLocations()
    }

    suspend fun addLocationToFavourite(location: SkyVibeLocation): Long {
        return favouriteLocationDao.insertLocation(location)
    }

    suspend fun deleteLocationFromFavourite(location: SkyVibeLocation) {
        return favouriteLocationDao.deleteLocation(location)
    }

    suspend fun getAlerts(): Flow<List<WeatherAlert>> {
        return alertsDao.getAllAlerts()
    }

    suspend fun addAlert(weatherAlert: WeatherAlert): Long {
        return alertsDao.insertNewAlert(weatherAlert)
    }

    suspend fun deleteAlert(weatherAlert: WeatherAlert) {
        return alertsDao.deleteAlert(weatherAlert)
    }

    suspend fun updateAlert(weatherAlert: WeatherAlert) {
        return alertsDao.updateAlert(weatherAlert)
    }

}

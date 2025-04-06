package com.abdelrahman_elshreif.sky_vibe.data.local

import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherDataEntity
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

class SkyVibeLocalDataSource(
    private val favouriteLocationDao: FavouriteLocationDao,
    private val alertsDao: WeatherAlertDao,
    private val weatherDao: WeatherDao
) : ISkyVibeLocalDataSource {

    override suspend fun getFavouriteLocations(): Flow<List<SkyVibeLocation>> {
        return favouriteLocationDao.getAllLocations()
    }

    override suspend fun addLocationToFavourite(location: SkyVibeLocation): Long {
        return favouriteLocationDao.insertLocation(location)
    }

    override suspend fun deleteLocationFromFavourite(location: SkyVibeLocation) {
        return favouriteLocationDao.deleteLocation(location)
    }

    override suspend fun getAlerts(): Flow<List<WeatherAlert>> {
        return alertsDao.getAllAlerts()
    }

    override suspend fun addAlert(weatherAlert: WeatherAlert): Long {
        return alertsDao.insertNewAlert(weatherAlert)
    }

    override suspend fun deleteAlert(weatherAlert: WeatherAlert) {
        return alertsDao.deleteAlert(weatherAlert)
    }

    override suspend fun updateAlert(weatherAlert: WeatherAlert) {
        return alertsDao.updateAlert(weatherAlert)
    }

    override suspend fun disableAlert(alertId: Long) {
        return alertsDao.disableAlertById(alertId)
    }

    override suspend fun getLastSavedWeather(): WeatherDataEntity? {
        return weatherDao.getWeatherData()
    }

    override suspend fun insertWeatherData(weatherData: WeatherDataEntity): Long {
        return weatherDao.insertWeatherData(weatherData)
    }

}

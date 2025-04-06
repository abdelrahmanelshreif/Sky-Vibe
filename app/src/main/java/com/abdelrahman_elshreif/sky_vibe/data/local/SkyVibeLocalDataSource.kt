package com.abdelrahman_elshreif.sky_vibe.data.local

import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherDataEntity
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import kotlinx.coroutines.flow.Flow

class SkyVibeLocalDataSource(
    private val favouriteLocationDao: FavouriteLocationDao,
    private val alertsDao: WeatherAlertDao,
    private val weatherDao: WeatherDao,
    private val locationDataStore: ILocationDataStore,
    private val settingDataStore: SettingDataStore
) : ISkyVibeLocalDataSource {

    // Weather Data
    override suspend fun getLastSavedWeather(): WeatherDataEntity? {
        return weatherDao.getWeatherData()
    }

    override suspend fun insertWeatherData(weatherData: WeatherDataEntity): Long {
        return weatherDao.insertWeatherData(weatherData)
    }

    // Favorite Locations
    override suspend fun getFavouriteLocations(): Flow<List<SkyVibeLocation>> {
        return favouriteLocationDao.getAllLocations()
    }

    override suspend fun addLocationToFavourite(location: SkyVibeLocation): Long {
        return favouriteLocationDao.insertLocation(location)
    }

    override suspend fun deleteLocationFromFavourite(location: SkyVibeLocation) {
        return favouriteLocationDao.deleteLocation(location)
    }

    // Alerts
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

    // Location Storage
    override suspend fun getSavedLocation(): Pair<Double, Double>? {
        return locationDataStore.getLocationFromDataStore()
    }

    override suspend fun saveLocation(lat: Double, lon: Double) {
        locationDataStore.saveLocationToDataStore(lat, lon)
    }

    override suspend fun clearLocation() {
        locationDataStore.clearLocationFromDataStore()
    }

    // Settings
    override suspend fun getTempUnit(): Flow<String> {
        return settingDataStore.tempUnit
    }

    override suspend fun getWindUnit(): Flow<String> {
        return settingDataStore.windSpeedUnit
    }

    override suspend fun getLanguage(): Flow<String> {
        return settingDataStore.language
    }

    override suspend fun getLocationMethod(): Flow<String> {
        return settingDataStore.locationMethod
    }

    override suspend fun saveTempUnit(unit: String) {
        settingDataStore.saveTempUnit(getResourceIdForUnit(unit))
    }

    override suspend fun saveWindUnit(unit: String) {
        settingDataStore.saveWindSpeedUnit(getResourceIdForUnit(unit))
    }

    override suspend fun saveLanguage(language: String) {
        settingDataStore.saveLanguage(getResourceIdForLanguage(language))
    }

    override suspend fun saveLocationMethod(method: String) {
        settingDataStore.saveLocationMethod(getResourceIdForMethod(method))
    }

    // Helper methods for resource IDs
    private fun getResourceIdForUnit(unit: String): Int {
        return when (unit) {
            SettingOption.CELSIUS.storedValue -> R.string.celsius
            SettingOption.FAHRENHEIT.storedValue -> R.string.fahrenheit
            SettingOption.KELVIN.storedValue -> R.string.kelvin
            SettingOption.METER_SEC.storedValue -> R.string.meter_sec
            SettingOption.MILE_HOUR.storedValue -> R.string.mile_hour
            else -> throw IllegalArgumentException("Unknown unit: $unit")
        }
    }

    private fun getResourceIdForLanguage(language: String): Int {
        return when (language) {
            SettingOption.ENGLISH.storedValue -> R.string.english
            SettingOption.ARABIC.storedValue -> R.string.arabic
            else -> throw IllegalArgumentException("Unknown language: $language")
        }
    }

    private fun getResourceIdForMethod(method: String): Int {
        return when (method) {
            SettingOption.GPS.storedValue -> R.string.gps
            SettingOption.MAP.storedValue -> R.string.map
            else -> throw IllegalArgumentException("Unknown method: $method")
        }
    }
}

package com.abdelrahman_elshreif.sky_vibe.data.repo

import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.local.ISkyVibeLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherDataEntity
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.remote.ISkyVibeRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SkyVibeRepository private constructor(
    private val remoteDataSource: ISkyVibeRemoteDataSource,
    private val localDataSource: ISkyVibeLocalDataSource,
) : ISkyVibeRepository {

    companion object {
        @Volatile
        private var repository: SkyVibeRepository? = null

        fun getInstance(
            remoteDataSource: ISkyVibeRemoteDataSource,
            localDataSource: ISkyVibeLocalDataSource
        ): SkyVibeRepository {
            return repository ?: synchronized(this) {
                repository ?: SkyVibeRepository(remoteDataSource, localDataSource).also {
                    repository = it
                }
            }
        }
    }

    // Weather Data Operations
    override fun getWeatherByCoordinates(lat: Double, lon: Double): Flow<WeatherResponse?> = flow {
        try {
            val response = remoteDataSource.getWeatherDataOfCoordinates(lat, lon)
            emit(response)
        } catch (e: Exception) {
            emit(null)
        }
    }

    override fun getWeatherByCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<WeatherResponse?> = flow {
        try {
            val response = remoteDataSource.getWeatherDataOfCoordinates(lat, lon, lang = lang)
            emit(response)
        } catch (e: Exception) {
            emit(null)
        }
    }

    override fun getWeatherByCoordinates(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<WeatherResponse?> = flow {
        try {
            val response = remoteDataSource.getWeatherDataOfCoordinates(
                lat, lon, lang = lang, unit = unit
            )
            emit(response)
        } catch (e: Exception) {
            emit(null)
        }
    }

    override suspend fun getLastSavedWeather(): WeatherDataEntity? {
        return localDataSource.getLastSavedWeather()
    }

    override suspend fun insertWeatherData(weatherData: WeatherDataEntity): Long {
        return localDataSource.insertWeatherData(weatherData)
    }

    // Favorite Locations Operations
    override suspend fun getAllSavedLocations(): Flow<List<SkyVibeLocation>> {
        return localDataSource.getFavouriteLocations()
    }

    override suspend fun addLocationToFavourite(location: SkyVibeLocation): Long {
        return localDataSource.addLocationToFavourite(location)
    }

    override suspend fun deleteLocationFromFavourite(location: SkyVibeLocation) {
        localDataSource.deleteLocationFromFavourite(location)
    }

    override fun searchLocations(query: String): Flow<List<NominatimLocation>> = flow {
        try {
            val response = remoteDataSource.getSuggestedLocations(query)
            emit(response)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    // Alerts Operations
    override suspend fun getAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.getAlerts()
    }

    override suspend fun addNewAlert(weatherAlert: WeatherAlert): Long {
        return localDataSource.addAlert(weatherAlert)
    }

    override suspend fun deleteAlert(weatherAlert: WeatherAlert) {
        localDataSource.deleteAlert(weatherAlert)
    }

    override suspend fun updateAlert(weatherAlert: WeatherAlert) {
        localDataSource.updateAlert(weatherAlert)
    }

    override suspend fun disableAlert(alertId: Long) {
        localDataSource.disableAlert(alertId)
    }

    // Location Storage Operations
    override suspend fun getSavedLocation(): Pair<Double, Double>? {
        return localDataSource.getSavedLocation()
    }

    override suspend fun saveLocation(lat: Double, lon: Double) {
        localDataSource.saveLocation(lat, lon)
    }

    override suspend fun clearLocation() {
        localDataSource.clearLocation()
    }

    // Settings Operations
    override suspend fun getTempUnit(): Flow<String> {
        return localDataSource.getTempUnit()
    }

    override suspend fun getWindUnit(): Flow<String> {
        return localDataSource.getWindUnit()
    }

    override suspend fun getLanguage(): Flow<String> {
        return localDataSource.getLanguage()
    }

    override suspend fun getLocationMethod(): Flow<String> {
        return localDataSource.getLocationMethod()
    }

    override suspend fun saveTempUnit(unit: String) {
        localDataSource.saveTempUnit(unit)
    }

    override suspend fun saveWindUnit(unit: String) {
        localDataSource.saveWindUnit(unit)
    }

    override suspend fun saveLanguage(language: String) {
        localDataSource.saveLanguage(language)
    }

    override suspend fun saveLocationMethod(method: String) {
        localDataSource.saveLocationMethod(method)
    }
}

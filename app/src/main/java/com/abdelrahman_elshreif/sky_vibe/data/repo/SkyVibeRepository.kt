package com.abdelrahman_elshreif.sky_vibe.data.repo

import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.local.ISkyVibeLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherDataEntity
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.remote.ISkyVibeRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

@Suppress("UNCHECKED_CAST")
class SkyVibeRepository private constructor(
    private val remoteDataSource: ISkyVibeRemoteDataSource,
    private val localDataSource: ISkyVibeLocalDataSource,
) : ISkyVibeRepository {
    companion object {
        var repository: SkyVibeRepository? = null
        fun getInstance(
            remoteDataSource: ISkyVibeRemoteDataSource,
            localDataSource: ISkyVibeLocalDataSource
        ): SkyVibeRepository? {
            if (repository == null) {
                repository = SkyVibeRepository(remoteDataSource, localDataSource)
            }
            return repository
        }
    }

    override fun getWeatherByCoordinates(lat: Double, lon: Double): Flow<WeatherResponse?> =
        flow {
            val response = remoteDataSource.getWeatherDataOfCoordinates(lat, lon)
            emit(response)
        }.catch {
            emit(null)
        }

    override fun getWeatherByCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): Flow<WeatherResponse?> =
        flow {
            val response = remoteDataSource.getWeatherDataOfCoordinates(lat, lon, lang = lang)
            emit(response)
        }.catch {
            emit(null)
        }

    override fun getWeatherByCoordinates(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): Flow<WeatherResponse?> =
        flow {
            val response =
                remoteDataSource.getWeatherDataOfCoordinates(lat, lon, lang = lang, unit = unit)
            emit(response)
        }.catch {
            emit(null)
        }


    override suspend fun getAllSavedLocations(): Flow<List<SkyVibeLocation>> {
        return localDataSource.getFavouriteLocations()
    }

    override suspend fun addLocationToFavourite(location: SkyVibeLocation): Long {
        return localDataSource.addLocationToFavourite(location)
    }

    override suspend fun deleteLocationFromFavourite(location: SkyVibeLocation) {
        return localDataSource.deleteLocationFromFavourite(location)
    }

    override fun searchLocations(query: String) =
        flow {
            val response = remoteDataSource.getSuggestedLocations(query)
            emit(response)
        }

    override suspend fun getAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.getAlerts()
    }

    override suspend fun addNewAlert(weatherAlert: WeatherAlert): Long {
        return localDataSource.addAlert(weatherAlert)
    }

    override suspend fun deleteAlert(weatherAlert: WeatherAlert) {
        return localDataSource.deleteAlert(weatherAlert)
    }

    override suspend fun updateAlert(weatherAlert: WeatherAlert) {
        return localDataSource.updateAlert(weatherAlert)
    }

    override suspend fun disableAlert(alertId: Long) {
        return localDataSource.disableAlert(alertId)
    }

    override suspend fun getLastSavedWeather(): WeatherDataEntity? {
        return localDataSource.getLastSavedWeather()
    }

    override suspend fun insertWeatherData(weatherData: WeatherDataEntity): Long {
        return localDataSource.insertWeatherData(weatherData)

    }

    suspend fun getSavedLocation(): Pair<Double, Double>? {
        return localDataSource.getSavedLocation()
    }


    suspend fun getAddressFromLocation(lat: Double, lon: Double): String {
        return localDataSource.getAddressFromLocation(lat, lon)
    }

}

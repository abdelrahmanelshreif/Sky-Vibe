package com.abdelrahman_elshreif.sky_vibe.data.repo

import com.abdelrahman_elshreif.sky_vibe.data.local.SkyVibeLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.remote.ForecastingRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

@Suppress("UNCHECKED_CAST")
class SkyVibeRepository private constructor(
    private val remoteDataSource: ForecastingRemoteDataSource,
    private val localDataSource: SkyVibeLocalDataSource,
) {
    companion object {
        private var repository: SkyVibeRepository? = null
        fun getInstance(
            remoteDataSource: ForecastingRemoteDataSource,
            localDataSource: SkyVibeLocalDataSource
        ): SkyVibeRepository? {
            if (repository == null) {
                repository = SkyVibeRepository(remoteDataSource, localDataSource)
            }
            return repository
        }
    }

    fun getWeatherByCoordinates(lat: Double, lon: Double): Flow<WeatherResponse?> =
        flow {
            val response = remoteDataSource.getWeatherDataOfCoordinates(lat, lon)
            emit(response)
        }.catch {
            emit(null)
        }

    fun getWeatherByCoordinates(lat: Double, lon: Double, lang: String): Flow<WeatherResponse?> =
        flow {
            val response = remoteDataSource.getWeatherDataOfCoordinates(lat, lon, lang = lang)
            emit(response)
        }.catch {
            emit(null)
        }

    fun getWeatherByCoordinates(
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


    suspend fun getAllSavedLocations(): Flow<List<SkyVibeLocation>> {
        return localDataSource.getFavouriteLocations()
    }

    suspend fun addLocationToFavourite(location: SkyVibeLocation): Long {
        return localDataSource.addLocationToFavourite(location)
    }

    suspend fun deleteLocationFromFavourite(location: SkyVibeLocation) {
        return localDataSource.deleteLocationFromFavourite(location)
    }

    suspend fun searchLocations(query:String): List<NominatimLocation> {
        return remoteDataSource.getSuggestedLocations(query)
    }
}

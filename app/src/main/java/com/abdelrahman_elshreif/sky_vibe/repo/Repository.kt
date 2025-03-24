package com.abdelrahman_elshreif.sky_vibe.repo

import com.abdelrahman_elshreif.sky_vibe.data.local.ForecastingLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.ForecastingRemoteDataSource
import com.abdelrahman_elshreif.sky_vibe.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Suppress("UNCHECKED_CAST")
class Repository private constructor(
    private val remoteDataSource: ForecastingRemoteDataSource,
    private val localDataSource: ForecastingLocalDataSource,
) {

    suspend fun getWeatherByCoordinates(lat: Double, lon: Double): Flow<WeatherResponse?> {
        return remoteDataSource.getWeatherDataOfCoordinates(lat, lon) as Flow<WeatherResponse?>
    }



    companion object {
        private var repository: Repository? = null
        fun getInstance(
            remoteDataSource: ForecastingRemoteDataSource,
            localDataSource: ForecastingLocalDataSource
        ): Repository? {
            if (repository == null) {
                repository = Repository(remoteDataSource, localDataSource)
            }
            return repository
        }
    }
}
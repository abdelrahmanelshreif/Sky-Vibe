package com.abdelrahman_elshreif.sky_vibe.data.repo

import android.util.Log
import com.abdelrahman_elshreif.sky_vibe.data.local.ForecastingLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.ForecastingRemoteDataSource
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherForecastResponse
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.math.log

@Suppress("UNCHECKED_CAST")
class SkyVibeRepository private constructor(
    private val remoteDataSource: ForecastingRemoteDataSource,
    private val localDataSource: ForecastingLocalDataSource,
) {
    companion object {
        private var repository: SkyVibeRepository? = null
        fun getInstance(
            remoteDataSource: ForecastingRemoteDataSource,
            localDataSource: ForecastingLocalDataSource
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
        }.catch { ex ->
            Log.i("TAG", "getWeatherByCoordinates: ${ex.message}")
            emit(null)
        }

    fun getForecastDataByCoordinates(lat: Double, lon: Double): Flow<WeatherForecastResponse?> =
        flow {
            val response = remoteDataSource.getForecastDataOfCoordinates(lat, lon)
            emit(response)
        }.catch { ex ->
            Log.i("TAG", "getForecastDataByCoordinates: ${ex.message}")
            emit(null)
        }

}
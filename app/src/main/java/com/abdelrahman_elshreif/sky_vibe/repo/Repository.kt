package com.abdelrahman_elshreif.sky_vibe.repo

import com.abdelrahman_elshreif.sky_vibe.data.local.ForecastingLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.ForecastingRemoteDataSource

class Repository private constructor(
    private val remoteDataSource: ForecastingRemoteDataSource,
    private val localDataSource: ForecastingLocalDataSource,
) {
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
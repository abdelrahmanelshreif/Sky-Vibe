package com.abdelrahman_elshreif.sky_vibe.data.local

import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import kotlinx.coroutines.flow.Flow

class SkyVibeLocalDataSource(private val favouriteLocationDao: FavouriteLocationDao) {

    suspend fun getFavouriteLocations(): Flow<List<SkyVibeLocation>> {
        return favouriteLocationDao.getAllLocations()
    }

    suspend fun addLocationToFavourite(location: SkyVibeLocation): Long {
        return favouriteLocationDao.insertLocation(location)
    }

    suspend fun deleteLocationFromFavourite(location: SkyVibeLocation): Long {
        return favouriteLocationDao.deleteLocation(location)
    }


}

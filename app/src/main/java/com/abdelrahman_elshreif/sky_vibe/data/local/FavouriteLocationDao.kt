package com.abdelrahman_elshreif.sky_vibe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteLocationDao {


    @Query("SELECT * FROM locations")
    suspend fun getAllLocations(): Flow<List<SkyVibeLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: SkyVibeLocation): Long

    @Delete
    suspend fun deleteLocation(location: SkyVibeLocation): Long


}
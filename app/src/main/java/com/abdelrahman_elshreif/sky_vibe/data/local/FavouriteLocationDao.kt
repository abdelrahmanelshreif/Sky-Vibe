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
     fun getAllLocations(): Flow<List<SkyVibeLocation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertLocation(location: SkyVibeLocation): Long

    @Delete
     fun deleteLocation(location: SkyVibeLocation)


}
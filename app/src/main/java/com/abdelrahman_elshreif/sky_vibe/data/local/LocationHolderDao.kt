package com.abdelrahman_elshreif.sky_vibe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.abdelrahman_elshreif.sky_vibe.data.model.LocationHolder

@Dao
interface LocationHolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationHolder)

    @Query("SELECT * FROM locations")
    suspend fun getLocations(): List<LocationHolder>

}
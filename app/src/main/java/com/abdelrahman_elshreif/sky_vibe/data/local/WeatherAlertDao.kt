package com.abdelrahman_elshreif.sky_vibe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import kotlinx.coroutines.flow.Flow


@Dao
interface WeatherAlertDao {

    @Query("Select * from alerts")
    fun getAllAlerts(): Flow<List<WeatherAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNewAlert(weatherAlert: WeatherAlert): Long

    @Update
    fun updateAlert(alert:WeatherAlert)

    @Delete
    fun deleteAlert(weatherAlert: WeatherAlert)

    @Query("UPDATE alerts SET isEnabled = 0 WHERE id = :alertId")
    fun disableAlertById(alertId: Long)
}
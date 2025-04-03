package com.abdelrahman_elshreif.sky_vibe.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation


@Database(
    entities = [SkyVibeLocation::class, WeatherAlert::class],
    version = 1,
    exportSchema = false
)
abstract class SkyVibeDatabase : RoomDatabase() {
    abstract fun getFavouriteLocationDao(): FavouriteLocationDao
    abstract fun getAlertsDao(): WeatherAlertDao

    companion object {
        @Volatile
        private var Instance: SkyVibeDatabase? = null


        fun getInstance(ctx: Context): SkyVibeDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    SkyVibeDatabase::class.java,
                    name = "location_database"
                ).build()
                Instance = instance
                instance
            }
        }
    }


}
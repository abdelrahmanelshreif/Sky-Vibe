package com.abdelrahman_elshreif.sky_vibe.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var alertArea: String = "UNKNOWN LOCATION",
    val startTime: Long,
    val endTime: Long,
    val type: AlertType,
    val latitude: Double,
    val longitude: Double,
    val isEnabled: Boolean = true,
    val description: String = ""
) {

}


enum class AlertType {
    NOTIFICATION,
    ALARM
}
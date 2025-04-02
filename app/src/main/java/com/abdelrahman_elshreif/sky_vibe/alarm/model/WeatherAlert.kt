package com.abdelrahman_elshreif.sky_vibe.alarm.model

import androidx.room.PrimaryKey

data class WeatherAlert(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val type: AlertType,
    val isEnabled: Boolean = true,
    val description: String = ""
)


enum class AlertType {
    NOTIFICATION,
    ALARM
}
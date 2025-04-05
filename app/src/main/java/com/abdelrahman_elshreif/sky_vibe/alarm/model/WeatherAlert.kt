package com.abdelrahman_elshreif.sky_vibe.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abdelrahman_elshreif.sky_vibe.R

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

fun AlertType.toLocalizedStringRes(): Int = when (this) {
    AlertType.ALARM -> R.string.alert_type_alarm
    AlertType.NOTIFICATION -> R.string.alert_type_notification
}
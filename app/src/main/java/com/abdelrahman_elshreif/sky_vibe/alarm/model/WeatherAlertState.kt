package com.abdelrahman_elshreif.sky_vibe.alarm.model

data class WeatherAlertState(
    val alerts: List<WeatherAlert> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class AddAlertState(
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long = System.currentTimeMillis() + 3600000, // +1 hour
    val alertType: AlertType = AlertType.NOTIFICATION,
    val isValid: Boolean = false
)
package com.abdelrahman_elshreif.sky_vibe.alarm.model

sealed class WeatherAlertEvent {
    object OnAddAlertClick : WeatherAlertEvent()
    object OnDismissDialog : WeatherAlertEvent()
    data class OnAlertToggled(val alert: WeatherAlert) : WeatherAlertEvent()
    data class OnAlertDeleted(val alert: WeatherAlert) : WeatherAlertEvent()
    data class OnSaveAlert(val alert: WeatherAlert) : WeatherAlertEvent()
}
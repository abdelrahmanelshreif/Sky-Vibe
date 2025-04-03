package com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel

import android.content.Context
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherNotificationManager

object NotificationHelper {
    fun createNotificationChannel(context: Context) {
        WeatherNotificationManager.createNotificationChannel(context)
    }
}
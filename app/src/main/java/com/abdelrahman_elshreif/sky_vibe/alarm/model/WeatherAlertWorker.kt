package com.abdelrahman_elshreif.sky_vibe.alarm.model

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherAlarmPlayer
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherNotificationManager


class WeatherAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notificationManager = WeatherNotificationManager(context)
    private val alarmPlayer = WeatherAlarmPlayer(context)

    override suspend fun doWork(): Result {
        return try {
            val alertId = inputData.getLong("alertId", 0)
            val alertType = inputData.getString("alertType")
            val message = inputData.getString("message")

            when (alertType) {
                AlertType.NOTIFICATION.name -> {
                    notificationManager.showNotification(alertId, message)
                }

                AlertType.ALARM.name -> {
                    alarmPlayer.playAlarm()
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}



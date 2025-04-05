package com.abdelrahman_elshreif.sky_vibe.workers

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abdelrahman_elshreif.sky_vibe.alarm.model.AlertType
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherAlarmPlayer
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherNotificationManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay


class WeatherAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val notificationManager = WeatherNotificationManager(context)

    override suspend fun doWork(): Result {
        try {
            val alertId = inputData.getLong("alertId", 0)
            val alertType = inputData.getString("alertType")
            val message = inputData.getString("message")
            val endTime = inputData.getLong("endTime", 0)

            Log.d("ALARM", "Executing alert $alertId of type $alertType")

            when (alertType) {
                AlertType.NOTIFICATION.name -> {
                    notificationManager.showNotification(alertId, message)
                }
                AlertType.ALARM.name -> {
                    try {
                        WeatherAlarmPlayer.start(context)
                        notificationManager.showAlarmNotification(alertId, message)

                        if (endTime > System.currentTimeMillis()) {
                            val initialRemaining = endTime - System.currentTimeMillis()
                            Log.d("ALARM", "Alarm will run for ${initialRemaining/1000}s")

                            var remaining = initialRemaining
                            val chunkSize = 1000L

                            try {
                                while (remaining > 0 && !isStopped) {
                                    delay(minOf(chunkSize, remaining))
                                    remaining = endTime - System.currentTimeMillis()
                                }
                            } catch (e: Exception) {
                                when (e) {
                                    is CancellationException-> {
                                        Log.d("ALARM", "Alarm was cancelled")
                                        WeatherAlarmPlayer.stop()
                                        NotificationManagerCompat.from(context).cancel(alertId.toInt())
                                        return Result.success()
                                    }
                                    else -> throw e
                                }
                            }
                            if (!isStopped) {
                                Log.d("ALARM", "Natural alarm completion")
                                WeatherAlarmPlayer.stop()
                                NotificationManagerCompat.from(context).cancel(alertId.toInt())
                            }
                        } else {
                            Log.w("ALARM", "Invalid endTime, stopping immediately")
                            WeatherAlarmPlayer.stop()
                        }
                    } catch (e: Exception) {
                        WeatherAlarmPlayer.stop()
                        throw e
                    }
                }
            }
            return Result.success()
        } catch (e: Exception) {
            if (e is CancellationException) {
                Log.d("ALARM", "Work was cancelled", e)
                return Result.success()
            }
            Log.e("ALARM", "Error in doWork", e)
            return Result.failure()
        }
    }

}


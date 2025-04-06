package com.abdelrahman_elshreif.sky_vibe.workers

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abdelrahman_elshreif.sky_vibe.alarm.model.AlertType
import com.abdelrahman_elshreif.sky_vibe.data.local.LocationDataStore
import com.abdelrahman_elshreif.sky_vibe.data.local.SkyVibeDatabase
import com.abdelrahman_elshreif.sky_vibe.data.local.SkyVibeLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.OSMHelper
import com.abdelrahman_elshreif.sky_vibe.data.remote.RetrofitHelper
import com.abdelrahman_elshreif.sky_vibe.data.remote.SkyVibeRemoteDataSource
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherAlarmPlayer
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherNotificationManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext


class WeatherAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    private val notificationManager = WeatherNotificationManager(context)
    private val repository = SkyVibeRepository.getInstance(
        SkyVibeRemoteDataSource(
            RetrofitHelper.apiservice,
            OSMHelper.apiService
        ),
        SkyVibeLocalDataSource(
            SkyVibeDatabase.getInstance(context).getFavouriteLocationDao(),
            SkyVibeDatabase.getInstance(context).getAlertsDao(),
            SkyVibeDatabase.getInstance(context).getWeathersDao(),
            LocationDataStore(context),
            SettingDataStore(context)

        )
    )
    private val locationUtilities = LocationUtilities(context)
    private lateinit var address: String
    private lateinit var weatherFeeling: String
    override suspend fun doWork(): Result {
        try {
            val alertId = inputData.getLong("alertId", 0)
            val alertType = inputData.getString("alertType")
            val message = inputData.getString("message")
            val endTime = inputData.getLong("endTime", 0)
            val latitude = inputData.getDouble("latitude", 0.0)
            val longitude = inputData.getDouble("longitude", 0.0)

            withContext(Dispatchers.IO) {
                address = locationUtilities.getAddressFromLocation(latitude, longitude)
                weatherFeeling =
                    repository?.getWeatherByCoordinates(lat = latitude, lon = longitude)
                        .let { data ->
                            data!!.first()!!.current.weather[0].description
                        }

                repository?.disableAlert(alertId)
            }

            Log.d("ALARM", "Executing alert $alertId of type $alertType")

            when (alertType) {
                AlertType.NOTIFICATION.name -> {
                    notificationManager.showNotification(alertId, address, weatherFeeling)
                }

                AlertType.ALARM.name -> {
                    try {
                        WeatherAlarmPlayer.start(context)
                        notificationManager.showAlarmNotification(alertId, address, weatherFeeling)

                        if (endTime > System.currentTimeMillis()) {
                            val initialRemaining = endTime - System.currentTimeMillis()
                            Log.d("ALARM", "Alarm will run for ${initialRemaining / 1000}s")

                            var remaining = initialRemaining
                            val chunkSize = 1000L

                            try {
                                while (remaining > 0 && !isStopped) {
                                    delay(minOf(chunkSize, remaining))
                                    remaining = endTime - System.currentTimeMillis()
                                }
                            } catch (e: Exception) {
                                when (e) {
                                    is CancellationException -> {
                                        Log.d("ALARM", "Alarm was cancelled")
                                        WeatherAlarmPlayer.stop()
                                        NotificationManagerCompat.from(context)
                                            .cancel(alertId.toInt())
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

package com.abdelrahman_elshreif.sky_vibe.utils

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.workers.WeatherAlertWorker
import java.util.concurrent.TimeUnit

class AlertScheduler(private val workManager: WorkManager) {

    fun schedule(alert: WeatherAlert) {
        val currentTime = System.currentTimeMillis()
        val delayTime = alert.startTime - currentTime

        if (delayTime < 3000) return

        val inputData = workDataOf(
            "alertId" to alert.id,
            "alertType" to alert.type.name,
            "message" to alert.description,
            "endTime" to alert.endTime,
            "latitude" to alert.latitude,
            "longitude" to alert.longitude
        )

        val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayTime, TimeUnit.MILLISECONDS)
            .addTag("alert_${alert.id}")
            .build()

        workManager.enqueueUniqueWork("alert_${alert.id}", ExistingWorkPolicy.REPLACE, workRequest)
    }

    fun cancel(alertId: Long) {
        workManager.cancelUniqueWork("alert_$alertId")
    }
}

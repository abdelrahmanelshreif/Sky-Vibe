package com.abdelrahman_elshreif.sky_vibe.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.alarm.view.AlarmCancelReceiver

class WeatherNotificationManager(private val context: Context) {


    fun showNotification(alertId: Long, message: String?) {
        val notificationManager = NotificationManagerCompat.from(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.weather_alert)
            .setContentTitle(context.getString(R.string.weather_alert))
            .setContentText(message ?: "Weather Alert!")
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notificationManager.notify(alertId.toInt(), notification)
    }


    @SuppressLint("MissingPermission")
    fun showAlarmNotification(alertId: Long, message: String?) {
        val stopIntent = PendingIntent.getBroadcast(
            context,
            alertId.toInt(),
            Intent(context, AlarmCancelReceiver::class.java).apply {
                action = "${context.packageName}.STOP_ALARM"  // Match manifest action
                putExtra("alertId", alertId)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.weather_alert)
            .setContentTitle(context.getString(R.string.weather_alert))
            .setContentText(message ?: "Weather Alert!")
            .setAutoCancel(false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.weather_alert,
                "Stop Alarm",
                stopIntent
            )
            .build()

        NotificationManagerCompat.from(context)
            .notify(alertId.toInt(), notification)
    }
    companion object {
        const val CHANNEL_ID = "weather_alerts"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.weather_alert),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Weather alert notifications"
                }
                context.getSystemService(NotificationManager::class.java)
                    ?.createNotificationChannel(channel)
            }
        }
    }
}
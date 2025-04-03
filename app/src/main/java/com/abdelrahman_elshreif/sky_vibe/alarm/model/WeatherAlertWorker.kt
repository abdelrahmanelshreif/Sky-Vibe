package com.abdelrahman_elshreif.sky_vibe.alarm.model

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.abdelrahman_elshreif.sky_vibe.R

class WeatherAlertWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val alertId = inputData.getLong("alertId", 0)
            val alertType = inputData.getString("alertType")
            val message = inputData.getString("message")

            when (alertType) {
                AlertType.NOTIFICATION.name -> showNotification(alertId, message)
                AlertType.ALARM.name -> playAlarm()
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showNotification(alertId: Long, message: String?) {
        val notificationManager = NotificationManagerCompat.from(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.weather_alert),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alert notifications"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.weather_alert)
            .setContentTitle(context.getString(R.string.weather_alert))
            .setContentText(message ?: "Weather Alert!")
            .setAutoCancel(true)
            .build()

        notificationManager.notify(alertId.toInt(), notification)
    }

    private fun playAlarm() {
        val mediaPlayer = MediaPlayer.create(context, R.raw.alarm_def)
        mediaPlayer.start()
    }

    companion object {
        const val CHANNEL_ID = "weather_alerts"
    }
}

@Composable
fun NotificationPermissionHandler(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onPermissionGranted()
        }
    }

    LaunchedEffect(Unit) {
        checkAndRequestPermission()
    }
}


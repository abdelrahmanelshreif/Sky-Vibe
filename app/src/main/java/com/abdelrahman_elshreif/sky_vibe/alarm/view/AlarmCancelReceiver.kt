package com.abdelrahman_elshreif.sky_vibe.alarm.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherAlarmPlayer


class AlarmCancelReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "${context.packageName}.STOP_ALARM") {
            val alertId = intent.getLongExtra("alertId", 0)
            Log.d("Alarm", "Received stop action for alarm $alertId")

            WeatherAlarmPlayer.stop()

            NotificationManagerCompat.from(context).cancel(alertId.toInt())

            WorkManager.getInstance(context)
                .cancelUniqueWork("alert_${alertId}")
                .result
                .addListener({
                    Log.d("Alarm", "Work cancelled successfully")
                }, ContextCompat.getMainExecutor(context))
        }
    }
}
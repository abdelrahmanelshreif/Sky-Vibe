package com.abdelrahman_elshreif.sky_vibe.utils

import android.content.Context
import android.media.MediaPlayer
import com.abdelrahman_elshreif.sky_vibe.R

class WeatherAlarmPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playAlarm() {
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_def).apply {
            setOnCompletionListener { release() }
            start()
        }
    }

    fun stopAlarm() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }
}
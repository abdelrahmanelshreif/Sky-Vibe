package com.abdelrahman_elshreif.sky_vibe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.abdelrahman_elshreif.sky_vibe.R

object WeatherAlarmPlayer {
    private var mediaPlayer: MediaPlayer? = null

    @Synchronized
    fun start(context: Context) {
        stop()
        mediaPlayer = MediaPlayer.create(context, R.raw.alarm_def).apply {
            isLooping = true
            start()
        }
    }

    @Synchronized
    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) stop()
            release()
        }
        mediaPlayer = null
    }
}
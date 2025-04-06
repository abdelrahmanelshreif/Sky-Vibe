package com.abdelrahman_elshreif.sky_vibe.utils

import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption

object UnitConverters {
    fun convertTemperature(temp: Double?, tempUnit: String): Double {
        return when (tempUnit) {
            SettingOption.KELVIN.storedValue -> (temp ?: 0.0) + 273.15
            SettingOption.FAHRENHEIT.storedValue -> (temp ?: 0.0) * 9.0 / 5.0 + 32
            else -> temp ?: 0.0
        }
    }

    fun convertWindSpeed(speed: Double?, windSpeedUnit: String): Double {
        return when (windSpeedUnit) {
            SettingOption.MILE_HOUR.storedValue -> (speed ?: 0.0) * 2.23694
            else -> speed ?: 0.0
        }
    }
}
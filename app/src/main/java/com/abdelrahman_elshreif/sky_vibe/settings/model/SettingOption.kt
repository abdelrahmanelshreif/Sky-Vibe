package com.abdelrahman_elshreif.sky_vibe.settings.model

import com.abdelrahman_elshreif.sky_vibe.R

enum class SettingOption(val resourceId: Int, val storedValue: String) {

    CELSIUS(R.string.celsius_c, "celsius_c"),
    KELVIN(R.string.kelvin_k, "kelvin_k"),
    FAHRENHEIT(R.string.fahrenheit_f, "fahrenheit_f"),

    // Wind Speed Units
    METER_SEC(R.string.meter_sec, "meter_sec"),
    MILE_HOUR(R.string.mile_hour, "mile_hour"),

    // Languages
    ENGLISH(R.string.english, "english"),
    ARABIC(R.string.arabic, "arabic"),

    // Location Methods
    GPS(R.string.gps, "gps"),
    MAP(R.string.map, "map");

    companion object {
        fun fromResourceId(resourceId: Int): String {
            return entries.find { it.resourceId == resourceId }?.storedValue
                ?: CELSIUS.storedValue
        }

        fun toResourceId(storedValue: String): Int {
            return entries.find { it.storedValue == storedValue }?.resourceId
                ?: R.string.celsius_c
        }
    }
}
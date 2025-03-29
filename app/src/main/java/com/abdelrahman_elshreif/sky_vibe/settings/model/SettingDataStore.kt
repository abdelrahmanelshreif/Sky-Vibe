package com.abdelrahman_elshreif.sky_vibe.settings.model

import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.abdelrahman_elshreif.sky_vibe.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore by preferencesDataStore(name = "setting_prefs")

class SettingDataStore(private val context: Context) {

    companion object {
        val TEMP_UNIT_KEY = stringPreferencesKey("temp_unit")
        val WIND_UNIT_KEY = stringPreferencesKey("wind_unit")
        val LANGUAGE_KEY = stringPreferencesKey("language")
        val LOCATION_METHOD_KEY = stringPreferencesKey("location_method")
    }

    val tempUnit: Flow<String> = context.dataStore.data.map {
        it[TEMP_UNIT_KEY] ?: "Celsius"
    }
    val windSpeedUnit: Flow<String> = context.dataStore.data.map {
        it[WIND_UNIT_KEY] ?: "meter/sec"
    }
    val language = context.dataStore.data.map {
        it[LANGUAGE_KEY] ?: "English"
    }

    val locationMethod = context.dataStore.data.map {
        it[LOCATION_METHOD_KEY] ?: "GPS"
    }

    suspend fun saveSetting(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { pref ->
            pref[key] = value
        }
    }

}
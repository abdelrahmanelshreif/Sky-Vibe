package com.abdelrahman_elshreif.sky_vibe.settings.model

import android.content.Context
import android.content.SharedPreferences
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

        private const val PREF_NAME = "settings_pref"
        private const val PREF_LANGUAGE_KEY = "language"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


    val tempUnit: Flow<String> = context.dataStore.data.map {
        it[TEMP_UNIT_KEY] ?: SettingOption.CELSIUS.storedValue
    }

    val windSpeedUnit: Flow<String> = context.dataStore.data.map {
        it[WIND_UNIT_KEY] ?: SettingOption.METER_SEC.storedValue
    }

    val language: Flow<String> = context.dataStore.data.map {
        it[LANGUAGE_KEY] ?: SettingOption.ENGLISH.storedValue
    }

    val locationMethod: Flow<String> = context.dataStore.data.map {
        it[LOCATION_METHOD_KEY] ?: SettingOption.GPS.storedValue
    }

    suspend fun saveTempUnit(resourceId: Int) {
        val storedValue = SettingOption.fromResourceId(resourceId)
        context.dataStore.edit { pref ->
            pref[TEMP_UNIT_KEY] = storedValue
        }
    }

    suspend fun saveWindSpeedUnit(resourceId: Int) {
        val storedValue = SettingOption.fromResourceId(resourceId)
        context.dataStore.edit { pref ->
            pref[WIND_UNIT_KEY] = storedValue
        }
    }

    suspend fun saveLanguage(resourceId: Int) {
        val storedValue = SettingOption.fromResourceId(resourceId)
        context.dataStore.edit { pref ->
            pref[LANGUAGE_KEY] = storedValue
            saveLanguageToSharedPrefs(storedValue)
        }

    }
    fun saveLanguageToSharedPrefs(languageCode: String) {
        sharedPreferences.edit().putString(PREF_LANGUAGE_KEY, languageCode).apply()
    }

    fun getLanguageFromSharedPrefs(): String {
        return sharedPreferences.getString(PREF_LANGUAGE_KEY, "en") ?: "en"
    }

    suspend fun saveLocationMethod(resourceId: Int) {
        val storedValue = SettingOption.fromResourceId(resourceId)
        context.dataStore.edit { pref ->
            pref[LOCATION_METHOD_KEY] = storedValue
        }
    }

}

package com.abdelrahman_elshreif.sky_vibe.data.local

import android.content.Context
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

interface ILocationDataStore {
    suspend fun getLocationFromDataStore(): Pair<Double, Double>?
    suspend fun saveLocationToDataStore(latitude: Double, longitude: Double)
    suspend fun clearLocationFromDataStore()
}

class LocationDataStore(private val context: Context) : ILocationDataStore {
    companion object {
        private val Context.dataStore by preferencesDataStore(name = "location_prefs")
        private val LATITUDE = doublePreferencesKey("latitude")
        private val LONGITUDE = doublePreferencesKey("longitude")
    }

    override suspend fun getLocationFromDataStore(): Pair<Double, Double>? {
        return context.dataStore.data.map { preferences ->
            val latitude = preferences[LATITUDE]
            val longitude = preferences[LONGITUDE]
            if (latitude != null && longitude != null) {
                Pair(latitude, longitude)
            } else null
        }.firstOrNull()
    }

    override suspend fun saveLocationToDataStore(latitude: Double, longitude: Double) {
        context.dataStore.edit { preferences ->
            preferences[LATITUDE] = latitude
            preferences[LONGITUDE] = longitude
        }
    }

    override suspend fun clearLocationFromDataStore() {
        context.dataStore.edit { preferences ->
            preferences.remove(LATITUDE)
            preferences.remove(LONGITUDE)
        }
    }
}
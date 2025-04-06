package com.abdelrahman_elshreif.sky_vibe.settings.model

import kotlinx.coroutines.flow.Flow

interface ISettingsDataSource {
    val tempUnit: Flow<String>
    val windSpeedUnit: Flow<String>
    val language: Flow<String>
    val locationMethod: Flow<String>
    suspend fun saveTempUnit(resourceId: Int)
    suspend fun saveWindSpeedUnit(resourceId: Int)
    suspend fun saveLanguage(resourceId: Int)
    suspend fun saveLocationMethod(resourceId: Int)
    fun getLanguageFromSharedPrefs(): String
}
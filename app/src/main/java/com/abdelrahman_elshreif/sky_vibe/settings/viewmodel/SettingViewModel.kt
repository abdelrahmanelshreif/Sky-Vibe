package com.abdelrahman_elshreif.sky_vibe.settings.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SettingViewModel(context: Context) : ViewModel() {

    private val settingDataStore = SettingDataStore(context)

    private val _tempUnit = MutableStateFlow(R.string.celsius_c)
    val tempUnit = _tempUnit.asStateFlow()


    private val _windUnit = MutableStateFlow(R.string.meter_sec)
    val windUnit = _windUnit.asStateFlow()

    private val _locationMethod = MutableStateFlow(R.string.gps)
    val locationMethod = _locationMethod.asStateFlow()

    private val _language = MutableStateFlow(R.string.english)
    val language = _language.asStateFlow()


    val tempOptions = listOf(R.string.celsius_c, R.string.kelvin_k, R.string.fahrenheit_f)
    val locationOptions = listOf(R.string.gps, R.string.map)
    val windSpeedOptions = listOf(R.string.meter_sec, R.string.mile_hour)
    val languageOptions = listOf(R.string.english, R.string.arabic)


    init {
        viewModelScope.launch {
            settingDataStore.tempUnit.collect {
                _tempUnit.value = SettingOption.toResourceId(it)
            }
        }

        viewModelScope.launch {
            settingDataStore.language.collect {
                _language.value = SettingOption.toResourceId(it)
            }
        }

        viewModelScope.launch {
            settingDataStore.windSpeedUnit.collect {
                _windUnit.value = SettingOption.toResourceId(it)
            }
        }

        viewModelScope.launch {
            settingDataStore.locationMethod.collect {
                _locationMethod.value = SettingOption.toResourceId(it)
            }
        }
    }


    fun updateSelection(rowIndex: Int, selectedIndex: Int) {

        require(rowIndex in 0..3) {
            "Invalid row index"
        }


        viewModelScope.launch {
            try {
                when (rowIndex) {
                    0 -> settingDataStore.saveTempUnit(tempOptions[selectedIndex])
                    1 -> settingDataStore.saveLocationMethod(locationOptions[selectedIndex])
                    2 -> settingDataStore.saveWindSpeedUnit(windSpeedOptions[selectedIndex])
                    3 -> settingDataStore.saveLanguage(languageOptions[selectedIndex])
                }
            } catch (e: Exception) {
                println("Error saving setting: ${e.message}")
            }
        }
    }
}
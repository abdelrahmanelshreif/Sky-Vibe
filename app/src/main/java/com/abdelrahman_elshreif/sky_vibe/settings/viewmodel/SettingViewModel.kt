package com.abdelrahman_elshreif.sky_vibe.settings.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.ui.res.stringResource
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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

    val optionsList = listOf(
        R.string.temp_unit to listOf(R.string.celsius_c, R.string.kelvin_k, R.string.fahrenheit_f),
        R.string.location to listOf(R.string.gps, R.string.map),
        R.string.wind_speed_unit to listOf(R.string.meter_sec, R.string.mile_hour),
        R.string.language to listOf(R.string.english, R.string.arabic)
    )
    private val _selectedOptions = MutableStateFlow(listOf(0, 0, 0, 0))
    val selectedOptions = _selectedOptions.asStateFlow()

    init {
        // NOW WE LOAD THE DATA FROM DATASTORE
        viewModelScope.launch {
            settingDataStore.tempUnit.collect {
                _tempUnit.value = it
            }
        }

        viewModelScope.launch {
            settingDataStore.language.collect {
                _language.value = it
            }
        }

        viewModelScope.launch {
            settingDataStore.windSpeedUnit.collect {
                _windUnit.value = it
            }
        }

        viewModelScope.launch {
            settingDataStore.locationMethod.collect {
                _locationMethod.value = it
            }
        }
    }


    private fun saveSetting(key: Preferences.Key<String>, value: String) {
        viewModelScope.launch {
            settingDataStore.saveSetting(key, value)
        }
    }

    fun updateSelection(rowIndex: Int, selectedIndex: Int) {
        val newList = _selectedOptions.value.toMutableList().also { it[rowIndex] = selectedIndex }
        _selectedOptions.value = newList


    }
}
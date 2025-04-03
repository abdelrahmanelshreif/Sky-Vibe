package com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FavouriteWeatherDetailsViewModel(
    private val repository: SkyVibeRepository,
    private val settingDataStore: SettingDataStore,
    val locationUtilities: LocationUtilities
) : ViewModel() {

    private val _favouriteWeatherData = MutableStateFlow<WeatherResponse?>(null)
    val favouriteWeatherData = _favouriteWeatherData.asStateFlow()

    private val _tempUnit = MutableStateFlow("")
    val tempUnit = _tempUnit.asStateFlow()

    private val _windUnit = MutableStateFlow("")
    val windUnit = _windUnit.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()


    init {
        fetchSetting()
    }

    private fun fetchSetting() {
        viewModelScope.launch {
            launch {
                settingDataStore.tempUnit
                    .distinctUntilChanged()
                    .collect { tempUnit ->
                        _tempUnit.value = tempUnit
                    }
            }

            launch {
                settingDataStore.windSpeedUnit
                    .distinctUntilChanged()
                    .collect { windUnit ->
                        _windUnit.value = windUnit
                    }
            }

        }
    }

    fun fetchWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true

            val tempUnit =
                settingDataStore.tempUnit.firstOrNull() ?: SettingOption.CELSIUS.storedValue
            val windSpeedUnit =
                settingDataStore.windSpeedUnit.firstOrNull() ?: SettingOption.METER_SEC.storedValue
            val language = settingDataStore.language.firstOrNull()?.let {
                if (it == "english") "en" else "ar"
            } ?: "en"

            repository.getWeatherByCoordinates(lat, lon, lang = language)
                .catch { ex ->
                    _toastEvent.emit(ex.message ?: "Unknown error")
                    _favouriteWeatherData.value = null
                }
                .map { weatherData ->
                    weatherData!!.copy(
                        current = weatherData.current.copy(
                            temp = convertTemperature(weatherData.current.temp, tempUnit),
                            windSpeed = convertWindSpeed(
                                weatherData.current.windSpeed,
                                windSpeedUnit
                            )
                        ),
                        hourly = weatherData.hourly.map { hourlyData ->
                            hourlyData.copy(
                                temp = convertTemperature(hourlyData.temp, tempUnit),
                                windSpeed = convertWindSpeed(hourlyData.windSpeed, windSpeedUnit)
                            )
                        },
                        daily = weatherData.daily.map { dailyData ->
                            dailyData.copy(
                                temp = dailyData.temp.copy(
                                    min = convertTemperature(dailyData.temp.min, tempUnit),
                                    max = convertTemperature(dailyData.temp.max, tempUnit)
                                ),
                                windSpeed = convertWindSpeed(dailyData.windSpeed, windSpeedUnit)
                            )
                        }
                    )
                }
                .collect { weatherData ->
                    _favouriteWeatherData.value = weatherData
                }
            _isLoading.value = false
        }
    }

    private fun convertTemperature(temp: Double?, tempUnit: String): Double {
        return when (tempUnit) {
            SettingOption.KELVIN.storedValue -> (temp ?: 0.0) + 273.15
            SettingOption.FAHRENHEIT.storedValue -> (temp ?: 0.0) * 9.0 / 5.0 + 32
            else -> temp ?: 0.0
        }
    }

    private fun convertWindSpeed(speed: Double?, windSpeedUnit: String): Double {
        return when (windSpeedUnit) {
            SettingOption.MILE_HOUR.storedValue -> (speed ?: 0.0) * 2.23694
            else -> speed ?: 0.0
        }
    }
}
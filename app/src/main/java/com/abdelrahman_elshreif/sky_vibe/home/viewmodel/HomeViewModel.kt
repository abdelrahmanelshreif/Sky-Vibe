package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.log

class HomeViewModel(
    private val repository: SkyVibeRepository,
    private val locationUtilities: LocationUtilities,
    private val settingDataStore: SettingDataStore,
) : ViewModel() {


    private val _homeWeatherData = MutableStateFlow<WeatherResponse?>(null)
    val homeWeatherData = _homeWeatherData.asStateFlow()

    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location: StateFlow<Pair<Double, Double>?> = _location

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    private val _tempUnit = MutableStateFlow("")
    val tempUnit = _tempUnit.asStateFlow()

    private val _windUnit = MutableStateFlow("")
    val windUnit = _windUnit.asStateFlow()

    private val _locationMethod = MutableStateFlow("")
    val locationMethod = _locationMethod.asStateFlow()



    init {
        fetchSetting()
        fetchLocation()
    }

    private fun fetchSetting() {
        viewModelScope.launch {
            launch {
                settingDataStore.tempUnit
                    .distinctUntilChanged()
                    .catch { e -> Log.e("HomeViewModel", "Error collecting tempUnit", e) }
                    .collect { tempUnit ->
                        _tempUnit.value = tempUnit
                    }
            }

            launch {
                settingDataStore.windSpeedUnit
                    .distinctUntilChanged()
                    .catch { e -> Log.e("HomeViewModel", "Error collecting windSpeedUnit", e) }
                    .collect { windUnit ->
                        _windUnit.value = windUnit
                    }
            }

            launch {
                settingDataStore.locationMethod
                    .distinctUntilChanged()
                    .catch { e -> Log.e("HomeViewModel", "Error collecting locationMethod", e) }
                    .collect { locMethod ->
                        _locationMethod.value = locMethod
                    }
            }
        }
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
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
                    _homeWeatherData.value = null
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
                    _homeWeatherData.value = weatherData
                }
            _isLoading.value = false
        }
    }


    private fun fetchLocation() {
        viewModelScope.launch {
            val loc = locationUtilities.getOrFetchLocation()
            loc?.let {
                _location.value = it
                fetchWeatherData(it.first, it.second)
            }
        }
    }

    fun fetchLocationAndWeather() {
        fetchLocation()
        fetchWeatherData(_location.value?.first ?: 0.0, _location.value?.second ?: 0.0)
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


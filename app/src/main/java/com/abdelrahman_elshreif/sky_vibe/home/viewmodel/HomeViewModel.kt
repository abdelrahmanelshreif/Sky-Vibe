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


    init {
        fetchLocation()
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

            Log.i(
                "TAG",
                "fetchWeatherData: TempUnit=$tempUnit, WindSpeedUnit=$windSpeedUnit, Language=$language"
            )

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
            settingDataStore.tempUnit.collect().let {
                Log.i("TAG", "fetchWeatherData: $it")
            }
        }
        viewModelScope.launch {
            val loc = locationUtilities.getOrFetchLocation()
            loc?.let {
                _location.value = it
                fetchWeatherData(it.first, it.second)
            }
        }
    }

    fun fetchLocationAndWeather() {
        viewModelScope.launch {
            settingDataStore.tempUnit.collect().let {
                Log.i("TAG", "fetchWeatherData: $it")
            }
        }
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


package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherDataEntity
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.repo.ISkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import com.abdelrahman_elshreif.sky_vibe.utils.NetworkUtils
import com.abdelrahman_elshreif.sky_vibe.utils.UnitConverters
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: ISkyVibeRepository,
    private val networkUtils: NetworkUtils
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

    private val _savedLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val savedLocation = _savedLocation.asStateFlow()

    init {
        viewModelScope.launch {
            _savedLocation.value = repository.getSavedLocation()
            _savedLocation.value?.let { (lat, lon) ->
                fetchWeatherData(lat, lon)
            }
        }
        fetchSettings()
    }

    fun saveSelectedLocationAndFetch(lat: Double, lon: Double) {
        viewModelScope.launch {
            repository.saveLocation(lat, lon)
            _savedLocation.value = Pair(lat, lon)
            _location.value = Pair(lat, lon)
            fetchWeatherData(lat, lon)
        }
    }

    private fun onLocationMethodChanged() {
        viewModelScope.launch {
            when (_locationMethod.value) {
                "gps" -> {
                    repository.clearLocation()
                    _savedLocation.value = null
                }
                "map" -> {
                    val saved = repository.getSavedLocation()
                    _savedLocation.value = saved
                    saved?.let { (lat, lon) ->
                        _location.value = Pair(lat, lon)
                        fetchWeatherData(lat, lon)
                    }
                }
            }
        }
    }

    fun fetchWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val tempUnit = repository.getTempUnit().firstOrNull() ?: SettingOption.CELSIUS.storedValue
                val windSpeedUnit = repository.getWindUnit().firstOrNull() ?: SettingOption.METER_SEC.storedValue
                val language = repository.getLanguage().firstOrNull()?.let {
                    if (it == "english") "en" else "ar"
                } ?: "en"

                if (networkUtils.checkNetworkAvailability()) {
                    repository.getWeatherByCoordinates(lat, lon, lang = language)
                        .catch { ex ->
                            _toastEvent.emit(ex.message ?: "Unknown error")
                            _homeWeatherData.value = null
                        }
                        .collect { weatherData ->
                            weatherData?.let {
                                val jsonData = Gson().toJson(weatherData)
                                repository.insertWeatherData(WeatherDataEntity(jsonData = jsonData))
                                val processedData = processWeatherData(weatherData, tempUnit, windSpeedUnit)
                                _homeWeatherData.value = processedData
                            }
                        }
                } else {
                    _toastEvent.emit("Please connect to network to get latest updates")
                    val weatherDataEntity = repository.getLastSavedWeather()
                    weatherDataEntity?.let {
                        val weatherData = Gson().fromJson(it.jsonData, WeatherResponse::class.java)
                        val processedData = processWeatherData(weatherData, tempUnit, windSpeedUnit)
                        _homeWeatherData.value = processedData
                    } ?: run {
                        _toastEvent.emit("No offline data available")
                    }
                }
            } catch (e: Exception) {
                _toastEvent.emit("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun reprocessExistingData() {
        viewModelScope.launch {
            try {
                val tempUnit = repository.getTempUnit().firstOrNull() ?: SettingOption.CELSIUS.storedValue
                val windSpeedUnit = repository.getWindUnit().firstOrNull() ?: SettingOption.METER_SEC.storedValue

                val weatherDataEntity = repository.getLastSavedWeather()
                weatherDataEntity?.let {
                    val originalData = Gson().fromJson(it.jsonData, WeatherResponse::class.java)
                    val processedData = processWeatherData(originalData, tempUnit, windSpeedUnit)
                    _homeWeatherData.value = processedData
                }
            } catch (e: Exception) {
                _toastEvent.emit("Error processing data: ${e.message}")
            }
        }
    }

    private fun fetchSettings() {
        viewModelScope.launch {
            launch {
                repository.getTempUnit()
                    .distinctUntilChanged()
                    .catch { e -> _toastEvent.emit("Error collecting tempUnit: ${e.message}") }
                    .collect { tempUnit ->
                        _tempUnit.value = tempUnit
                        reprocessExistingData()
                    }
            }

            launch {
                repository.getWindUnit()
                    .distinctUntilChanged()
                    .catch { e -> _toastEvent.emit("Error collecting windUnit: ${e.message}") }
                    .collect { windUnit ->
                        _windUnit.value = windUnit
                        reprocessExistingData()
                    }
            }

            launch {
                repository.getLocationMethod()
                    .distinctUntilChanged()
                    .catch { e -> _toastEvent.emit("Error collecting locationMethod: ${e.message}") }
                    .collect { locMethod ->
                        _locationMethod.value = locMethod
                        onLocationMethodChanged()
                    }
            }
        }
    }

    private fun processWeatherData(
        weatherData: WeatherResponse,
        tempUnit: String,
        windSpeedUnit: String
    ): WeatherResponse {
        return weatherData.copy(
            current = weatherData.current.copy(
                temp = UnitConverters.convertTemperature(weatherData.current.temp, tempUnit),
                windSpeed = UnitConverters.convertWindSpeed(weatherData.current.windSpeed, windSpeedUnit)
            ),
            hourly = weatherData.hourly.map { hourlyData ->
                hourlyData.copy(
                    temp = UnitConverters.convertTemperature(hourlyData.temp, tempUnit),
                    windSpeed = UnitConverters.convertWindSpeed(hourlyData.windSpeed, windSpeedUnit)
                )
            },
            daily = weatherData.daily.map { dailyData ->
                dailyData.copy(
                    temp = dailyData.temp.copy(
                        day = UnitConverters.convertTemperature(dailyData.temp.day, tempUnit),
                        min = UnitConverters.convertTemperature(dailyData.temp.min, tempUnit),
                        max = UnitConverters.convertTemperature(dailyData.temp.max, tempUnit)
                    ),
                    windSpeed = UnitConverters.convertWindSpeed(dailyData.windSpeed, windSpeedUnit)
                )
            }
        )
    }
}

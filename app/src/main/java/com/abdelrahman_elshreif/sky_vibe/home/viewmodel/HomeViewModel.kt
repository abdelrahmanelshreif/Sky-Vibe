package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherDataEntity
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import com.abdelrahman_elshreif.sky_vibe.utils.NetworkUtils
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SkyVibeRepository,
    private val locationUtilities: LocationUtilities,
    private val networkUtils: NetworkUtils,
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

    private val _savedLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val savedLocation = _savedLocation.asStateFlow()

    private val _savedAddress = MutableStateFlow("")
    val savedAddress = _savedAddress.asStateFlow()

    init {
        viewModelScope.launch {
            _savedLocation.value = locationUtilities.getLocationFromDataStore()
            _savedLocation.value?.let { (lat, lon) ->
                updateAddress(lat, lon)
            }
        }
        fetchSetting()
    }

    private fun updateAddress(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _savedAddress.value = locationUtilities.getAddressFromLocation(lat, lon)
        }
    }

    fun saveSelectedLocationAndFetch(lat: Double, lon: Double) {
        viewModelScope.launch {
            locationUtilities.saveLocationToDataStore(lat, lon)
            _savedLocation.value = Pair(lat, lon)
            _location.value = Pair(lat, lon)
            updateAddress(lat, lon)
            fetchWeatherData(lat, lon)
        }
    }

    private fun onLocationMethodChanged() {
        viewModelScope.launch {
            when (_locationMethod.value) {
                "gps" -> {
                    locationUtilities.clearLocationFromSharedPrefs()
                    _savedLocation.value = null
                    fetchLocationAndWeather()
                }

                "map" -> {
                    val saved = locationUtilities.getLocationFromDataStore()
                    _savedLocation.value = saved
                    saved?.let { (lat, lon) ->
                        _location.value = Pair(lat, lon)
                        updateAddress(lat, lon)
                        fetchWeatherData(lat, lon)
                    }
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
                            val processedData =
                                processWeatherData(weatherData, tempUnit, windSpeedUnit)
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
            _isLoading.value = false
        }
    }

    private fun reprocessExistingData() {
        viewModelScope.launch {
            val tempUnit =
                settingDataStore.tempUnit.firstOrNull() ?: SettingOption.CELSIUS.storedValue
            val windSpeedUnit =
                settingDataStore.windSpeedUnit.firstOrNull() ?: SettingOption.METER_SEC.storedValue

            val weatherDataEntity = repository.getLastSavedWeather()
            weatherDataEntity?.let {
                val originalData = Gson().fromJson(it.jsonData, WeatherResponse::class.java)
                val processedData = processWeatherData(originalData, tempUnit, windSpeedUnit)
                _homeWeatherData.value = processedData
            }
        }
    }

    @SuppressLint("LogNotTimber")
    private fun fetchSetting() {
        viewModelScope.launch {
            launch {
                settingDataStore.tempUnit
                    .distinctUntilChanged()
                    .catch { e -> Log.e("HomeViewModel", "Error collecting tempUnit", e) }
                    .collect { tempUnit ->
                        _tempUnit.value = tempUnit
                        reprocessExistingData()
                    }
            }

            launch {
                settingDataStore.windSpeedUnit
                    .distinctUntilChanged()
                    .catch { e -> Log.e("HomeViewModel", "Error collecting windSpeedUnit", e) }
                    .collect { windUnit ->
                        _windUnit.value = windUnit
                        reprocessExistingData()
                    }
            }

            launch {
                settingDataStore.locationMethod
                    .distinctUntilChanged()
                    .catch { e -> Log.e("HomeViewModel", "Error collecting locationMethod", e) }
                    .collect { locMethod ->
                        _locationMethod.value = locMethod
                        onLocationMethodChanged()
                    }
            }
        }
    }

    @SuppressLint("LogNotTimber")
    private fun processWeatherData(
        weatherData: WeatherResponse,
        tempUnit: String,
        windSpeedUnit: String
    ): WeatherResponse {
        val processedData = weatherData.copy(
            current = weatherData.current.copy(
                temp = convertTemperature(weatherData.current.temp, tempUnit),
                windSpeed = convertWindSpeed(weatherData.current.windSpeed, windSpeedUnit)
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
                        day = convertTemperature(dailyData.temp.day, tempUnit),
                        min = convertTemperature(dailyData.temp.min, tempUnit),
                        max = convertTemperature(dailyData.temp.max, tempUnit)
                    ),
                    windSpeed = convertWindSpeed(dailyData.windSpeed, windSpeedUnit)
                )
            }
        )
        Log.d("HomeViewModel", "Processed Data: $processedData")
        return processedData
    }

    private fun fetchLocation() {
        viewModelScope.launch {
            val loc = locationUtilities.getOrFetchLocation()
            loc?.let {
                if (_location.value != it) {
                    _location.value = it
                    updateAddress(it.first, it.second)
                    fetchWeatherData(it.first, it.second)
                }
            }
        }
    }

    fun fetchLocationAndWeather() {
        fetchLocation()
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


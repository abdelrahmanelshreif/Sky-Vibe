package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import com.google.type.Date
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class HomeViewModel(
    private val repository: SkyVibeRepository,
    private val locationUtilities: LocationUtilities
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
            repository.getWeatherByCoordinates(lat, lon)
                .catch { ex ->
                    _toastEvent.emit(ex.message ?: "Unknown error")
                    _homeWeatherData.value = null
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
}


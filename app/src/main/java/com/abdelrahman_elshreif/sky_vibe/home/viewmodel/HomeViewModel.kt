package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.repo.SkyVibeRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: SkyVibeRepository
) : ViewModel() {

    private val _homeWeatherData = MutableStateFlow<WeatherResponse?>(null)
    val homeWeatherData = _homeWeatherData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent = _toastEvent.asSharedFlow()

    fun fetchWeatherData(lat: Double, lon: Double) {
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

}
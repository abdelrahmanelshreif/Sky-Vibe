    package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

    import android.util.Log
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.abdelrahman_elshreif.sky_vibe.model.WeatherResponse
    import com.abdelrahman_elshreif.sky_vibe.repo.SkyVibeRepository
    import kotlinx.coroutines.flow.*
    import kotlinx.coroutines.launch
    import timber.log.Timber

    class HomeViewModel(private val repository: SkyVibeRepository) : ViewModel() {

        private val _homeWeatherData = MutableStateFlow<WeatherResponse?>(null)
        val homeWeatherData = _homeWeatherData.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading = _isLoading.asStateFlow()

        private val _toastEvent = MutableSharedFlow<String>()
        val toastEvent = _toastEvent.asSharedFlow()

        fun fetchWeatherData(lat: Double, lon: Double) {
            Timber.tag("WeatherViewModel").d("Fetching weather for: lat=$lat, lon=$lon")

            viewModelScope.launch {
                _isLoading.value = true
                repository.getWeatherByCoordinates(lat, lon)
                    .catch { ex ->
                        Log.e("WeatherViewModel", "Error fetching weather data", ex)
                        _toastEvent.emit(ex.message ?: "Unknown error")
                        _homeWeatherData.value = null
                    }
                    .collect { weatherData ->
                        Timber.tag("WeatherViewModel").d("Weather Data: $weatherData")
                        _homeWeatherData.value = weatherData
                    }
                _isLoading.value = false
            }
        }
    }
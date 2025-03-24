package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationUseCase(private val locationUtilities: LocationUtilities) {
    suspend fun getCurrentLocation(): Location? {
        return locationUtilities.getFreshLocation()
    }

    fun getAddressFromLocation(lat: Double, lon: Double): String {
        return locationUtilities.getAddressFromLocation(lat, lon)
    }

    fun checkLocationAvailability(): Boolean {
        return locationUtilities.checkPermissions() && locationUtilities.isLocationEnabled()
    }
}

class LocationViewModel(
    private val locationUseCase: LocationUseCase
) : ViewModel() {
    private val _locationFlow = MutableStateFlow<Location?>(null)
    val locationFlow: StateFlow<Location?> = _locationFlow

    private val _addressFlow = MutableStateFlow("")
    val addressFlow: StateFlow<String> = _addressFlow

    fun fetchLocation() {
        viewModelScope.launch {
            try {
                if (!locationUseCase.checkLocationAvailability()) {
                    _addressFlow.value = "Location not available"
                    return@launch
                }

                val location = locationUseCase.getCurrentLocation()
                _locationFlow.value = location

                location?.let {
                    _addressFlow.value = locationUseCase.getAddressFromLocation(it.latitude, it.longitude)
                }
            } catch (e: Exception) {
                _addressFlow.value = "Error: ${e.message}"
            }
        }
    }
}

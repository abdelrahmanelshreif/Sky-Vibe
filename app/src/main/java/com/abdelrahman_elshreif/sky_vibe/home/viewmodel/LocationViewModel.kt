package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.repo.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val locationRepository: LocationRepository) : ViewModel() {
    private val _locationFlow = MutableStateFlow<Location?>(null)
    val loctaionFlow: StateFlow<Location?> = _locationFlow

    private val _addressFlow = MutableStateFlow("")
    val addressFlow: StateFlow<String> = _addressFlow

    fun fetchLocation() {
        viewModelScope.launch {
            try {
                if (!locationRepository.checkPermissions()) {
                    _addressFlow.value = "Location Permission not Granted"
                    return@launch
                }
                if (!locationRepository.isLocationEnabled()) {
                    _addressFlow.value = "Location services disabled"
                    return@launch
                }

                val location = locationRepository.getFreshLocation()
                _locationFlow.value = location

                location?.let {
                    _addressFlow.value =
                        locationRepository.getAddressFromLocation(it.latitude, it.longitude)
                }
            } catch (e: Exception) {
                _addressFlow.value = "Error : ${e.message}"
            }

        }
    }


}
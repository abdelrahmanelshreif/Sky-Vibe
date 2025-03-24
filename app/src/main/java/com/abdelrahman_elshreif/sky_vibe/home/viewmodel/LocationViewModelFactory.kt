package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities

@Suppress("UNCHECKED_CAST")
class LocationViewModelFactory(
    private val locationUtilities: LocationUtilities
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            val locationUseCase = LocationUseCase(locationUtilities)
            return LocationViewModel(locationUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
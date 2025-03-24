package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.repo.LocationRepository

@Suppress("UNCHECKED_CAST")
class LocationViewModelFactory(private val _repo: LocationRepository?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let { LocationViewModel(it) } as T
    }
}

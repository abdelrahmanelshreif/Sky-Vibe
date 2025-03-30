package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val _repo: SkyVibeRepository?,
    private val _locationUtilities: LocationUtilities,
    private val _settingDataStore: SettingDataStore
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let { HomeViewModel(_repo, _locationUtilities, _settingDataStore) } as T
    }
}
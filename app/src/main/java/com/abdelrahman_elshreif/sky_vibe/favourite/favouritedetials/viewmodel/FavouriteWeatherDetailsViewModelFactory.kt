package com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities


@Suppress("UNCHECKED_CAST")
class FavouriteWeatherDetailsViewModelFactory(
    private val _repo: SkyVibeRepository?,
    private val _locationUtilities: LocationUtilities,
    private val _settingDataStore: SettingDataStore,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let {
            FavouriteWeatherDetailsViewModel(
                _repo,
                _settingDataStore,
                _locationUtilities
            )
        } as T
    }
}
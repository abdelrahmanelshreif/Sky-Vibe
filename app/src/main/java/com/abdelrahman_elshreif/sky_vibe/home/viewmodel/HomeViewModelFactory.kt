package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.data.repo.ISkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import com.abdelrahman_elshreif.sky_vibe.utils.NetworkUtils


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val _repo: ISkyVibeRepository?,
    private val _networkUtils: NetworkUtils,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let {
            HomeViewModel(
                it,
                _networkUtils
            )
        }
                as T
    }
}
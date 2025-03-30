package com.abdelrahman_elshreif.sky_vibe.settings.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingDataStore


@Suppress("UNCHECKED_CAST")
class SettingViewModelFactory(private val _settingDataStore: SettingDataStore) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(_settingDataStore) as T
    }
}
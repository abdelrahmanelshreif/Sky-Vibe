package com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.AlertScheduler
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities

@Suppress("UNCHECKED_CAST")
class AlarmViewModelFactory(
    private val _repo: SkyVibeRepository?,
    private val _alertScheduler: AlertScheduler,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let { AlarmViewModel(_repo, _alertScheduler) } as T
    }
}
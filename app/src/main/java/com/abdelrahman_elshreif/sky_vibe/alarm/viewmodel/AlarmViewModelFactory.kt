package com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkManager
import com.abdelrahman_elshreif.sky_vibe.data.repo.ISkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities

@Suppress("UNCHECKED_CAST")
class AlarmViewModelFactory(
    private val _repo: ISkyVibeRepository?,
    private val _workManager: WorkManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let { AlarmViewModel(it, _workManager) } as T
    }
}
package com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository

@Suppress("UNCHECKED_CAST")
class AlarmViewModelFactory(
    private val _repo: SkyVibeRepository?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let { AlarmViewModel(_repo) } as T
    }
}
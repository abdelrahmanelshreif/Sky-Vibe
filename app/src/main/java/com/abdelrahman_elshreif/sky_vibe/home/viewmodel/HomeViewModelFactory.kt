package com.abdelrahman_elshreif.sky_vibe.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.repo.SkyVibeRepository


@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val _repo: SkyVibeRepository?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let { HomeViewModel(it) } as T
    }
}
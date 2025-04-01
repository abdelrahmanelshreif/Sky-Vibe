package com.abdelrahman_elshreif.sky_vibe.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository


@Suppress("UNCHECKED_CAST")
class FavouriteViewModelFactory(
    private val _repo: SkyVibeRepository?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return _repo?.let { FavouriteViewModel(_repo) } as T
    }
}
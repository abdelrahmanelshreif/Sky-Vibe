package com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel

import androidx.lifecycle.ViewModel
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities

class FavouriteWeatherDetailsViewModel(
    val repo: SkyVibeRepository,
    val locationUtilities: LocationUtilities
):ViewModel() {

}
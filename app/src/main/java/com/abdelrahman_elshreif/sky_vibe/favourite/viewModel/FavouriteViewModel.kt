package com.abdelrahman_elshreif.sky_vibe.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class FavouriteViewModel(private val repository: SkyVibeRepository) : ViewModel() {

    private val _favouritePlaces = MutableStateFlow<List<GeoPoint>>(emptyList())
    val favouritePlaces = _favouritePlaces.asStateFlow()

    fun addFavouritePlace(location: SkyVibeLocation) {
        viewModelScope.launch {
            repository.addLocationToFavourite(location)
        }

    }

    fun removeFavouritePlace(location: SkyVibeLocation) {
        viewModelScope.launch {
            repository.deleteLocationFromFavourite(location)
        }
    }

    fun getFavouriteLocation() {
        viewModelScope.launch {
            repository.getAllSavedLocations()
        }
    }


}
package com.abdelrahman_elshreif.sky_vibe.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class FavouriteViewModel(private val repository: SkyVibeRepository) : ViewModel() {


    private val _favouritePlaces = MutableStateFlow<List<GeoPoint>>(emptyList())
    val favouritePlaces = _favouritePlaces.asStateFlow()

    private val _locationSuggestions = MutableStateFlow<List<NominatimLocation>>(emptyList())
    val locationSuggestions = _locationSuggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>(0)
    val toastEvent = _toastEvent.asSharedFlow()

    @OptIn(FlowPreview::class)
    fun searchLocations(query: String) {
        viewModelScope.launch {

            repository.searchLocations(query)
                .debounce(500)
                .flowOn(Dispatchers.IO)
                .onStart {
                    _isLoading.value = true
                }
                .catch {
                    _locationSuggestions.value = emptyList()
                    _toastEvent.emit("Error Fetching Locations , Please try again later...")
                }.onCompletion {
                    _isLoading.value = false
                }
                .collect {
                    _locationSuggestions.value = it
                }

        }
    }

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
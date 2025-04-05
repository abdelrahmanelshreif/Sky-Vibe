package com.abdelrahman_elshreif.sky_vibe.favourite.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.favourite.model.FavouriteScreenState
import com.abdelrahman_elshreif.sky_vibe.map.model.MapScreenEvent
import com.abdelrahman_elshreif.sky_vibe.map.model.MapScreenNavigationEvent
import com.abdelrahman_elshreif.sky_vibe.map.model.MapScreenState
import com.abdelrahman_elshreif.sky_vibe.map.model.SearchBarEvent
import com.abdelrahman_elshreif.sky_vibe.map.model.SearchBarState
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class FavouriteViewModel(
    private val repository: SkyVibeRepository, private val locationUtilities: LocationUtilities
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapScreenState())
    val uiState = _uiState.asStateFlow()

    private val _favUiState = MutableStateFlow(FavouriteScreenState())
    val favUiState = _favUiState.asStateFlow()

    private val _searchBarUiState = MutableStateFlow(SearchBarState())
    val searchBarUiState = _searchBarUiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<MapScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()


    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(800)
                .filterNot { it.isBlank() }
                .collect { query ->
                    performSearch(query)
                }
        }
        loadSavedLocations()
    }

    private fun loadSavedLocations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.getAllSavedLocations()
                    .collect { locations ->
                        _favUiState.update {
                            it.copy(
                                favouriteLocations = locations,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _favUiState.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun handleMapEvent(event: MapScreenEvent) {
        when (event) {
            is MapScreenEvent.OnMapClicked -> {
                handleMapClick(event.latitude, event.longitude)
            }

            is MapScreenEvent.OnLocationSelected -> {
                updateSelectedLocation(event.location)
            }

            MapScreenEvent.OnSaveLocation -> {
                saveLocation()
            }

            is MapScreenEvent.OnLocateMeButtonPressed -> {
                handleLocateMe()
            }

            MapScreenEvent.OnBackBtnPressed ->
                handleNavigateBack()
        }
    }

    private fun handleNavigateBack() {
        viewModelScope.launch {
            _navigationEvent.emit(MapScreenNavigationEvent.NavigateBack)
        }
    }


    private fun handleLocateMe() {
        viewModelScope.launch(Dispatchers.IO) {
            val loc = locationUtilities.getFreshLocation()
            val locAddress =
                loc?.let { locationUtilities.getAddressFromLocation(it.latitude, loc.longitude) }
            _uiState.update {
                it.copy(selectedLocation = locAddress?.let { it1 ->
                    NominatimLocation(
                        it1,
                        loc.latitude,
                        loc.longitude
                    )
                })
            }
        }
    }

    fun handleSearchBarEvent(event: SearchBarEvent) {
        when (event) {
            is SearchBarEvent.OnQueryChanged -> {
                handleSearchQuery(query = event.query)
            }

            is SearchBarEvent.OnSuggestionSelected -> {
                handleLocationSelection(event.location)
            }

        }
    }

    private fun handleLocationSelection(location: NominatimLocation) {
        _uiState.update {
            it.copy(selectedLocation = location)
        }

    }

    private fun handleSearchQuery(query: String) {
        _searchBarUiState.update {
            it.copy(query = query)
        }
        _searchQuery.value = query
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            repository.searchLocations(query)
                .flowOn(Dispatchers.IO)
                .onStart {
                    _searchBarUiState.update {
                        it.copy(isLoading = true)
                    }
                }.catch {
                    _searchBarUiState.update {
                        it.copy(suggestedLocations = emptyList())
                    }

                }.onCompletion {
                    _searchBarUiState.update {
                        it.copy(isLoading = false)
                    }
                }.collect { locationResponse ->
                    _searchBarUiState.update {
                        it.copy(suggestedLocations = locationResponse)
                    }
                }

        }
    }

    private fun saveLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value.selectedLocation?.let { location ->
                    val skyVibeLocation = SkyVibeLocation(
                        latitude = location.lat,
                        longitude = location.lon,
                        address = location.displayName
                    )
                    repository.addLocationToFavourite(skyVibeLocation)

                    _uiState.update {
                        it.copy(selectedLocation = null)
                    }
                    _searchBarUiState.update {
                        it.copy(query = "", suggestedLocations = emptyList())
                    }
                    handleNavigateBack()
                }
            } catch (e: Exception) {
                println("Error saving location: ${e.message}")
            }
        }
    }

    private fun updateSelectedLocation(location: NominatimLocation) {
        _uiState.update {
            it.copy(selectedLocation = location)
        }
    }

    private fun handleMapClick(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val geoAddress = locationUtilities.getAddressFromLocation(latitude, longitude)
            val newLocation = NominatimLocation(
                lat = latitude, lon = longitude, displayName = geoAddress
            )
            _uiState.update {
                it.copy(selectedLocation = newLocation)
            }
        }

    }

    fun removeFavouritePlace(location: SkyVibeLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteLocationFromFavourite(location)
        }
    }

}
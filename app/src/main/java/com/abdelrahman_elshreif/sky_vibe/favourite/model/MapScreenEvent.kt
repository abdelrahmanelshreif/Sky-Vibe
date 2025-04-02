package com.abdelrahman_elshreif.sky_vibe.favourite.model

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation

sealed class MapScreenEvent {
    data class OnMapClicked(val latitude: Double, val longitude: Double) : MapScreenEvent()
    data class OnLocationSelected(val location: NominatimLocation) : MapScreenEvent()
    object OnSaveLocation : MapScreenEvent()
}

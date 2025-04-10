package com.abdelrahman_elshreif.sky_vibe.map.model

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation

sealed class MapScreenEvent {
    data class OnMapClicked(val latitude: Double, val longitude: Double) : MapScreenEvent()
    data class OnLocationSelected(val location: NominatimLocation) : MapScreenEvent()
    object OnLocateMeButtonPressed : MapScreenEvent()
    object OnSaveLocation : MapScreenEvent()
    object OnBackBtnPressed : MapScreenEvent()
}

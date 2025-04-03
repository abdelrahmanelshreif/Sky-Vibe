package com.abdelrahman_elshreif.sky_vibe.map.model

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation

sealed class SearchBarEvent {
    data class OnQueryChanged(val query: String) : SearchBarEvent()
    data class OnSuggestionSelected(val location: NominatimLocation) : SearchBarEvent()
}
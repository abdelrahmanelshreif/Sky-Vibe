package com.abdelrahman_elshreif.sky_vibe.map.model

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation

data class SearchBarState(
    var query: String = "",
    val suggestedLocations: List<NominatimLocation> = emptyList(),
    val isLoading: Boolean = false
)


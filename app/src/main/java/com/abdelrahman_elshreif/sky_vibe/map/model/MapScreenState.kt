package com.abdelrahman_elshreif.sky_vibe.map.model

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import org.osmdroid.util.GeoPoint

data class MapScreenState(
    val selectedLocation: NominatimLocation? = null,
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val defaultLocation: GeoPoint = GeoPoint(30.7967, 30.9949)
)
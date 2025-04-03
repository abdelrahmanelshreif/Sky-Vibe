package com.abdelrahman_elshreif.sky_vibe.favourite.model

import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation

data class FavoriteLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)

data class FavouriteScreenState(
    val isLoading: Boolean = true,
    val favouriteLocations: List<SkyVibeLocation> = emptyList(),
    val error: String? = null
)
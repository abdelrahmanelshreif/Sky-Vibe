package com.abdelrahman_elshreif.sky_vibe.map.model

sealed class MapScreenNavigationEvent {
    object NavigateBack : MapScreenNavigationEvent()
}
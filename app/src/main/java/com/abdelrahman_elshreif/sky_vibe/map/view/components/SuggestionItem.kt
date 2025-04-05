package com.abdelrahman_elshreif.sky_vibe.map.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun SuggestionItem(
    location: NominatimLocation,
    onClick: () -> Unit
) {
    Text(
        text = location.displayName,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}


fun updateMapMarker(mapView: MapView, location: NominatimLocation) {
    val geoPoint = GeoPoint(location.lat, location.lon)
    mapView.controller.setCenter(geoPoint)

    val marker = Marker(mapView).apply {
        position = geoPoint
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = location.displayName.takeIf { it.isNotBlank() } ?: "Unknown Location"
    }

    mapView.overlays.add(marker)
    mapView.invalidate()
}
package com.abdelrahman_elshreif.sky_vibe.favourite.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MapScreen(viewModel: FavouriteViewModel) {
    val favoritePlaces by viewModel.favouritePlaces.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Map View as the background
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    controller.setCenter(GeoPoint(48.8583, 2.2944))
                }
            },
            update = { mapView ->
                mapView.overlays.clear()
                favoritePlaces.forEach { geoPoint ->
                    val marker = Marker(mapView)
                    marker.position = geoPoint
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    mapView.overlays.add(marker)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            AutoCompleteSearchBar(
                viewModel,
                onSuggestionClick = {
                    Log.i("TAG", "MapScreen: $it was clicked")
                    // Handle suggestion click, e.g., update map center
                }
            )
        }
    }
}


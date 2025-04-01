package com.abdelrahman_elshreif.sky_vibe.favourite.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("StateFlowValueCalledInComposition", "LogNotTimber")
@Composable
fun MapScreen(viewModel: FavouriteViewModel) {

    var selectedLocation by remember { mutableStateOf<NominatimLocation?>(null) }
    val locationSuggestions by viewModel.locationSuggestions.collectAsState(initial = emptyList())

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                MapView(context).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                }
            },
            update = { mapView ->
                val defaultGeoPoint = GeoPoint(30.7967, 30.9949)

                mapView.overlays.clear()

                selectedLocation?.let { location ->
                    val safeLatitude = location.lat
                    val safeLongitude = location.lon
                    val geoPoint = GeoPoint(safeLatitude, safeLongitude)
                    mapView.controller.setCenter(geoPoint)

                    val marker = Marker(mapView).apply {
                        position = geoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = location.displayName.takeIf { it.isNotBlank() } ?: "Unknown Location"
                    }
                    mapView.overlays.add(marker)
                    mapView.invalidate()

                    Log.d("MapScreen", "Location updated: ${location.displayName}")
                } ?: run {
                    mapView.controller.setCenter(defaultGeoPoint)
                    mapView.invalidate()
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
                viewModel = viewModel,
                onSuggestionClick = { selectedSuggestion ->
                    val location = locationSuggestions
                        .find { it.displayName == selectedSuggestion.displayName }
                    location?.let {
                        selectedLocation = it
                        Log.i("MapScreen", "Selected location: $it")
                    }
                }
            )
        }
    }
}
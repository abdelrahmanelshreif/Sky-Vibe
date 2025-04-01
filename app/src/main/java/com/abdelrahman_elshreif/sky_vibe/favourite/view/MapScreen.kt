package com.abdelrahman_elshreif.sky_vibe.favourite.view

import android.annotation.SuppressLint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
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
import androidx.navigation.NavController
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import kotlinx.coroutines.flow.MutableStateFlow
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("StateFlowValueCalledInComposition", "LogNotTimber", "ClickableViewAccessibility")
@Composable
fun MapScreen(
    viewModel: FavouriteViewModel,
    locationUtilities: LocationUtilities,
    navController: NavController
) {
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

                mapView.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        val projection = mapView.projection
                        val geoPoint =
                            projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint


                        val geoAddress = locationUtilities.getAddressFromLocation(
                            lat = geoPoint.latitude,
                            lon = geoPoint.longitude
                        )

                        selectedLocation = NominatimLocation(
                            lat = geoPoint.latitude,
                            lon = geoPoint.longitude,
                            displayName = geoAddress,
                        )

                        mapView.overlays.clear()

                        val marker = Marker(mapView).apply {
                            position = geoPoint
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = geoAddress
                        }

                        mapView.overlays.add(marker)
                        mapView.invalidate()

                        Log.d(
                            "MapScreen",
                            "New Marker at: ${geoPoint.latitude}, ${geoPoint.longitude}"
                        )

                        true
                    } else {
                        false
                    }
                }

                selectedLocation?.let { location ->
                    val geoPoint = GeoPoint(location.lat, location.lon)
                    mapView.controller.setCenter(geoPoint)

                    val marker = Marker(mapView).apply {
                        position = geoPoint
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title =
                            location.displayName.takeIf { it.isNotBlank() } ?: "Unknown Location"
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
                    val location =
                        locationSuggestions.find { it.displayName == selectedSuggestion.displayName }
                    location?.let {
                        selectedLocation = it
                    }
                }
            )

            selectedLocation?.let { location ->
                Button(
                    onClick = {
                        viewModel.addFavouritePlace(
                            SkyVibeLocation(
                                latitude = location.lat,
                                longitude = location.lon,
                                address = location.displayName
                            )
                        )
                        Log.d("MapScreen", "Saved location: ${location.displayName}")
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Save Location")
                }
            }
        }
    }
}

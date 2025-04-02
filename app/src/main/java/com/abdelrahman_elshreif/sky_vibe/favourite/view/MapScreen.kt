package com.abdelrahman_elshreif.sky_vibe.favourite.view

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.favourite.model.MapScreenEvent
import com.abdelrahman_elshreif.sky_vibe.favourite.model.MapScreenNavigationEvent
import com.abdelrahman_elshreif.sky_vibe.favourite.model.MapScreenState
import com.abdelrahman_elshreif.sky_vibe.favourite.model.SearchBarEvent
import com.abdelrahman_elshreif.sky_vibe.favourite.model.SearchBarState
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import kotlinx.coroutines.flow.collect
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MapScreen(
    viewModel: FavouriteViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is MapScreenNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val mapState by viewModel.uiState.collectAsState()
    val searchBarState by viewModel.searchBarUiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        MapView(
            state = mapState,
            onMapClicked = { lat, lon ->
                viewModel.handleMapEvent(MapScreenEvent.OnMapClicked(lat, lon))
            }
        )

        IconButton(
            onClick = { viewModel.handleMapEvent(MapScreenEvent.OnBackBtnPressed) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 72.dp, end = 16.dp, top = 16.dp)
                .align(Alignment.TopCenter)
        ) {
            SearchBar(
                state = searchBarState,
                onEvent = { event ->
                    when (event) {
                        is SearchBarEvent.OnQueryChanged -> {
                            viewModel.handleSearchBarEvent(
                                SearchBarEvent.OnQueryChanged(event.query)
                            )
                        }

                        is SearchBarEvent.OnSuggestionSelected -> {
                            viewModel.handleSearchBarEvent(
                                SearchBarEvent.OnSuggestionSelected(event.location)
                            )
                        }
                    }
                }
            )

            mapState.selectedLocation?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        viewModel.handleMapEvent(MapScreenEvent.OnSaveLocation)
//                        viewModel.handleMapEvent(MapScreenEvent.OnBackBtnPressed)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Location")
                }

            }


        }
        LocationButton(
            onClick = { viewModel.handleMapEvent(MapScreenEvent.OnLocateMeButtonPressed) },
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun LocationButton(onClick: () -> Unit, modifier: Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = "My Location",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }

}

@SuppressLint("ClickableViewAccessibility")
@Composable
private fun MapView(
    state: MapScreenState,
    onMapClicked: (Double, Double) -> Unit
) {
    AndroidView(
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                zoomController.setZoomInEnabled(true)
                zoomController.setZoomOutEnabled(true)
                setMultiTouchControls(true)
                setBuiltInZoomControls(true)
                controller.setZoom(15.0)
                zoomController.apply {
                    setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
                    activate()
                }

            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            mapView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val projection = mapView.projection
                    val geoPoint = projection.fromPixels(
                        event.x.toInt(),
                        event.y.toInt()
                    ) as GeoPoint

                    onMapClicked(geoPoint.latitude, geoPoint.longitude)
                    true
                } else false
            }

            state.selectedLocation?.let { location ->
                updateMapMarker(mapView, location)
            } ?: run {
                mapView.controller.setCenter(state.defaultLocation)
                mapView.invalidate()
            }
        }
    )
}

@Composable
private fun SearchBar(
    state: SearchBarState,
    onEvent: (SearchBarEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        OutlinedTextField(
            value = state.query,
            onValueChange = { query ->
                onEvent(SearchBarEvent.OnQueryChanged(query))
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search for location...") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            trailingIcon = {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                }
            }
        )

        if (state.suggestedLocations.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp)
            ) {
                items(state.suggestedLocations) { location ->
                    SuggestionItem(
                        location = location,
                        onClick = {
                            onEvent(SearchBarEvent.OnSuggestionSelected(location))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionItem(
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


private fun updateMapMarker(mapView: MapView, location: NominatimLocation) {
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
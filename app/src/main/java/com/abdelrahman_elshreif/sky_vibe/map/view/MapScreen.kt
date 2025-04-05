package com.abdelrahman_elshreif.sky_vibe.map.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.map.model.MapScreenEvent
import com.abdelrahman_elshreif.sky_vibe.map.model.MapScreenNavigationEvent
import com.abdelrahman_elshreif.sky_vibe.map.model.SearchBarEvent
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import com.abdelrahman_elshreif.sky_vibe.map.view.components.LocationButton
import com.abdelrahman_elshreif.sky_vibe.map.view.components.MapView
import com.abdelrahman_elshreif.sky_vibe.map.view.components.SearchBar

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MapScreen(
    viewModel: FavouriteViewModel,
    navController: NavController,
    isLocationSelection: Boolean = false,
    onLocationSelected: ((Double, Double) -> Unit)? = null,
    locationMethod: String = "map"
) {
    val mapState by viewModel.uiState.collectAsState()
    val searchBarState by viewModel.searchBarUiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is MapScreenNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (locationMethod != "gps") {
            Text(
                text = stringResource(R.string.tap_to_select_location),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            )
        }
        MapView(
            state = mapState,
            onMapClicked = { lat, lon ->
                viewModel.handleMapEvent(MapScreenEvent.OnMapClicked(lat, lon))
            }
        )

        if (!isLocationSelection) {
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
        }
        val modifier = if (isLocationSelection) {
            Modifier
                .fillMaxWidth()
                .padding(start = 72.dp, end = 16.dp, top = 16.dp)
                .align(Alignment.TopCenter)
        } else {
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.TopCenter)
        }

        val customizedModifier = if (!isLocationSelection) {
            Modifier
                .fillMaxWidth()
                .padding(start = 72.dp, end = 16.dp, top = 16.dp)
                .align(Alignment.TopCenter)
        } else {
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .align(Alignment.TopCenter)
        }

        Column(
            modifier = customizedModifier
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
                        if (isLocationSelection) {
                            if (onLocationSelected != null) {
                                onLocationSelected(it.lat, it.lon)
                            }
                        } else {
                            viewModel.handleMapEvent(MapScreenEvent.OnSaveLocation)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        stringResource(
                            if (isLocationSelection) R.string.confirm_location
                            else R.string.save_location
                        )
                    )
                }
            }


        }
        LocationButton(
            onClick = { viewModel.handleMapEvent(MapScreenEvent.OnLocateMeButtonPressed) },
            modifier = Modifier
                .align(alignment = BottomEnd)
                .padding(16.dp)
        )
        if (locationMethod == "gps") {
            LocationButton(
                onClick = { viewModel.handleMapEvent(MapScreenEvent.OnLocateMeButtonPressed) },
                modifier = Modifier
                    .align(alignment = BottomEnd)
                    .padding(16.dp)
            )
        }

    }
}







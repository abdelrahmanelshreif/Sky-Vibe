package com.abdelrahman_elshreif.sky_vibe.favourite.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import com.abdelrahman_elshreif.sky_vibe.navigation.Screen
import com.abdelrahman_elshreif.sky_vibe.utils.NetworkUtils


@Composable
fun FavouriteScreen(
    favViewModel: FavouriteViewModel,
    navController: NavController,
    networkUtils: NetworkUtils
) {

    val uiState by favViewModel.favUiState.collectAsState()
    var locationToDelete by remember { mutableStateOf<SkyVibeLocation?>(null) }
    val context = LocalContext.current

    locationToDelete?.let { location ->
        DeleteConfirmationDialog(locationAddress = location.address!!, onConfirm = {
            favViewModel.removeFavouritePlace(location)
            locationToDelete = null
        }, onDismiss = {
            locationToDelete = null
        })
    }

    LaunchedEffect(Unit) {
        favViewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = {
            if (networkUtils.checkNetworkAvailability()) {
                navController.navigate(Screen.Map.route)
            } else {
                favViewModel.handleNetworkFailure()
            }
        })
        {
            Icon(
                Icons.Default.AddLocationAlt,
                contentDescription = stringResource(R.string.add_location)
            )
        }
    }) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: stringResource(R.string.unknown_error_occurred),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            uiState.favouriteLocations.isEmpty() -> {
                EmptyFavouriteScreen()
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(uiState.favouriteLocations) { location ->
                        FavouriteLocationItem(
                            location = location,
                            onDeleteClick = {
                                locationToDelete = location
                            },
                            onLocationClick = {
                                navController.navigate(
                                    Screen.FavouriteWeatherDetails.createRoute(
                                        latitude = location.latitude,
                                        longitude = location.longitude
                                    )
                                )
                            },
                            Modifier
                        )
                    }
                }
            }
        }
    }
}


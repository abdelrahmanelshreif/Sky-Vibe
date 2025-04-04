package com.abdelrahman_elshreif.sky_vibe.home.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import com.abdelrahman_elshreif.sky_vibe.home.view.components.ErrorState
import com.abdelrahman_elshreif.sky_vibe.home.view.components.HomeContent
import com.abdelrahman_elshreif.sky_vibe.home.view.components.LoadingWeatherState
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.map.view.MapScreen

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel, favouriteViewModel: FavouriteViewModel) {

    val weatherData by homeViewModel.homeWeatherData.collectAsState(null)
    val isLoading by homeViewModel.isLoading.collectAsState(true)
    val tempUnit by homeViewModel.tempUnit.collectAsState()
    val windUnit by homeViewModel.windUnit.collectAsState()
    val locationMethod by homeViewModel.locationMethod.collectAsState()
    val savedLocation by homeViewModel.savedLocation.collectAsState(null)
    val savedAddress by homeViewModel.savedAddress.collectAsState("")
    var showLocationSelection by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            homeViewModel.fetchLocationAndWeather()
        } else {
            if (savedLocation == null) {
                showLocationSelection = true
                Toast.makeText(
                    context,
                    context.getString(R.string.please_select_location_manually),
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                savedLocation?.let { (lat, lon) ->
                    homeViewModel.fetchWeatherData(lat, lon)
                }
            }
        }
    }

    LaunchedEffect(locationMethod) {
        when (locationMethod) {
            "gps" -> {
                if (!context.hasPermission(locationPermission)) {
                    permissionLauncher.launch(locationPermission)
                } else {
                    homeViewModel.fetchLocationAndWeather()
                }
            }
            "map" -> {
                if (savedLocation == null) {
                    showLocationSelection = true
                } else {
                    homeViewModel.fetchWeatherData(savedLocation!!.first, savedLocation!!.second)
                }
            }
        }
    }

    if (showLocationSelection) {
        MapScreen(
            viewModel = favouriteViewModel,
            navController = rememberNavController(),
            isLocationSelection = true,
            onLocationSelected = { lat, lon ->
                showLocationSelection = false
                homeViewModel.saveSelectedLocationAndFetch(lat, lon)
            },
            locationMethod = locationMethod
        )
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3C6FD1),
                                Color(0xFF5A8FE3),
                                Color(0xFF72B5FF),
                                Color(0xFFB0D9FF)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    )
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when {
                    isLoading -> LoadingWeatherState()
                    weatherData != null -> HomeContent(
                        weatherData = weatherData!!,
                        tempUnit = tempUnit,
                        windUnit = windUnit,
                        address = savedAddress
                    )
                    else -> ErrorState()
                }
            }

            if (locationMethod != "gps") {
                FloatingActionButton(
                    onClick = { showLocationSelection = true },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(BottomEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = stringResource(R.string.select_location)
                    )
                }
            }
        }
    }
}














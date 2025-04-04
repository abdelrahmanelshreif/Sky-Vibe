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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.home.view.components.LoadingWeatherState
import com.abdelrahman_elshreif.sky_vibe.home.view.components.NextDaysForecast
import com.abdelrahman_elshreif.sky_vibe.home.view.components.TodayHourlyForecast
import com.abdelrahman_elshreif.sky_vibe.home.view.components.WeatherDetailsCard
import com.abdelrahman_elshreif.sky_vibe.home.view.components.WeatherHeader
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel) {
    val weatherData by homeViewModel.homeWeatherData.collectAsState(null)
    val isLoading by homeViewModel.isLoading.collectAsState(true)

    val tempUnit by homeViewModel.tempUnit.collectAsState()
    val windUnit by homeViewModel.windUnit.collectAsState()
    val locationMethod by homeViewModel.locationMethod.collectAsState()

    val context = LocalContext.current
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            homeViewModel.fetchLocationAndWeather()
        } else {
            // Provide feedback to the user if permission is denied
            Toast.makeText(context, "Location permission is required to fetch weather data.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!context.hasPermission(locationPermission)) {
            permissionLauncher.launch(locationPermission)
        } else {
            homeViewModel.fetchLocationAndWeather()
        }
    }

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
                weatherData = weatherData!! ,
                tempUnit = tempUnit,
                windUnit = windUnit
            )
            else -> ErrorState() // Handle the case where weather data is null
        }
    }
}

@Composable
fun ErrorState() {
    Text(
        text = "Unable to load weather data. Please try again later.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun LoadingWeatherState() {
    CircularProgressIndicator(
        modifier = Modifier.padding(16.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeContent(
    weatherData: WeatherResponse,
    tempUnit: String,
    windUnit: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            WeatherHeader(weatherData, tempUnit)
            Spacer(modifier = Modifier.height(16.dp))
            WeatherDetailsCard(weatherData, windUnit)
            Spacer(modifier = Modifier.height(16.dp))
            TodayHourlyForecast(hourlyForecastData = weatherData.hourly, tempUnit)
            NextDaysForecast(dailyForecastData = weatherData.daily, tempUnit)
        }
    }
}












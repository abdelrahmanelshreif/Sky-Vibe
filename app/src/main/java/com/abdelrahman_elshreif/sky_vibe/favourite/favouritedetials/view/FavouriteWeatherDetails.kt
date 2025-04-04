package com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel.FavouriteWeatherDetailsViewModel
import com.abdelrahman_elshreif.sky_vibe.home.view.components.LoadingWeatherState
import com.abdelrahman_elshreif.sky_vibe.home.view.components.NextDaysForecast
import com.abdelrahman_elshreif.sky_vibe.home.view.components.TodayHourlyForecast
import com.abdelrahman_elshreif.sky_vibe.home.view.components.WeatherDetailsCard
import com.abdelrahman_elshreif.sky_vibe.home.view.components.WeatherHeader


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavouriteWeatherDetails(
    lat: Double,
    lon: Double,
    favouriteWeatherDetailsViewModel: FavouriteWeatherDetailsViewModel,
    navController: NavController
) {

    val tempUnit = favouriteWeatherDetailsViewModel.tempUnit.collectAsState()
    val windUint = favouriteWeatherDetailsViewModel.windUnit.collectAsState()
    val weather = favouriteWeatherDetailsViewModel.favouriteWeatherData.collectAsState()
    val isLoading = favouriteWeatherDetailsViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        favouriteWeatherDetailsViewModel.fetchWeatherData(lat, lon)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 56.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                isLoading.value -> LoadingWeatherState()
                weather.value != null -> FavouriteLocationContent(
                    weather.value!!,
                    tempUnit.value,
                    windUint.value
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavouriteLocationContent(
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


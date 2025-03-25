package com.abdelrahman_elshreif.sky_vibe.home.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.LocationViewModel
import com.abdelrahman_elshreif.sky_vibe.model.WeatherResponse

@Composable
fun WeatherApp(
    homeViewModel: HomeViewModel,
    locationViewModel: LocationViewModel,
    onRequestPermission: () -> Unit
) {
    val location by locationViewModel.locationFlow.collectAsState()
    val weatherData by homeViewModel.homeWeatherData.collectAsState()

    LaunchedEffect(location) {
        onRequestPermission()
        location?.let {
            homeViewModel.getWeatherData(it.latitude, it.longitude)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFe9e9e9))
            .padding(16.dp)
    ) {
        when {
            weatherData == null -> LoadingWeatherState()
            else -> {
                AnimatedWeatherContent(
                    weatherData = weatherData!!
                )
            }
        }
    }
}

@Composable
fun AnimatedWeatherContent(
    weatherData: WeatherResponse
) {
    AnimatedVisibility(visible = true) {
        Column {
            WeatherHeader(weatherData)
            Spacer(modifier = Modifier.height(16.dp))
            WeatherDetailsCard(weatherData)
            Spacer(modifier = Modifier.height(16.dp))
            HourlyForecastCard()
        }
    }
}

@Composable
fun HourlyForecastCard() {
    Text("")
}

@SuppressLint("DefaultLocale")
@Composable
fun WeatherHeader(weatherData: WeatherResponse) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${weatherData.name} , ${weatherData.sys.country} ",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        AnimatedContent(targetState = weatherData.main.temp) { temp ->
            Text(
                text = stringResource(R.string.c, String.format("%.1f", temp)),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Text(
            text = weatherData.weather.firstOrNull()?.description?.capitalize()
                ?: "Weather Unavailable",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun WeatherDetailsCard(weatherData: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.weather_details),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(
                    icon = Icons.Default.Air,
                    label = stringResource(R.string.wind),
                    value = stringResource(R.string.m_s, weatherData.wind.speed)
                )
                WeatherDetailItem(
                    icon = Icons.Default.WbCloudy,
                    label = stringResource(R.string.clouds),
                    value = "${weatherData.clouds.all}% "
                )
                WeatherDetailItem(
                    icon = Icons.Default.WaterDrop,
                    label = stringResource(R.string.humidity),
                    value = "${weatherData.main.humidity}%"
                )
                WeatherDetailItem(
                    icon = Icons.Default.Compress,
                    label = stringResource(R.string.pressure),
                    value = stringResource(R.string.hpa, weatherData.main.pressure)
                )
            }
        }
    }
}

@Composable
fun LoadingWeatherState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun WeatherDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
    }
}
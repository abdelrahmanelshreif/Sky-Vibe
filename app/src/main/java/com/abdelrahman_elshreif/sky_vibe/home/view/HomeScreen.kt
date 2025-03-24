package com.abdelrahman_elshreif.sky_vibe.home.view

import androidx.compose.ui.graphics.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.LocationViewModel


@Composable
fun HomeScreen(viewModel: LocationViewModel, onRequestPermission: () -> Unit) {
    val location by viewModel.locationFlow.collectAsState()
    val address by viewModel.addressFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Latitude: ${location?.latitude ?: "Unknown"}")
        Text(text = "Longitude: ${location?.longitude ?: "Unknown"}")
        Text(text = "Address: $address")

        Button(onClick = { }) {
            Text(text = "Get Location")
        }
    }
}

//
//@Preview(showBackground = true)
@Composable
fun WeatherApp(viewModel: LocationViewModel, onRequestPermission: () -> Unit) {
    onRequestPermission()
    val location by viewModel.locationFlow.collectAsState()
    val address by viewModel.addressFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFe9e9e9))
    ) {
        Header(address)
        HourlyForecast()
        WeatherDetails()
    }
}

@Composable
fun Header(address: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = address, fontSize = 24.sp)
        Text(text = "Partly Cloudy", fontSize = 20.sp)
        Text(text = "Tuesday, 24 August 2020", fontSize = 16.sp)
    }
}

@Composable
fun WeatherDetails() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = stringResource(R.string.details), fontSize = 20.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherDetailItem(label = "72", description = stringResource(R.string.fahrenheit))
            WeatherDetailItem(label = "134 mph/h", description = stringResource(R.string.pressure))
            WeatherDetailItem(label = "0.2", description = stringResource(R.string.uv_index))
            WeatherDetailItem(label = "48%", description = stringResource(R.string.humidity))
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, description: String) {
    Column(
        modifier = Modifier

            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontSize = 24.sp)
        Text(text = description, fontSize = 12.sp)
    }
}

@Composable
fun HourlyForecast() {

    Card(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.hourly_forecast), fontSize = 20.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                HourlyWeatherItem(
                    time = "2 PM",
                    temperature = "28°",
                    icon = stringResource(R.string.very_sunny)
                )
                HourlyWeatherItem(
                    time = "3 PM",
                    temperature = "27°",
                    icon = stringResource(R.string.sunny)
                )
                HourlyWeatherItem(
                    time = "4 PM",
                    temperature = "26°",
                    icon = stringResource(R.string.cloudy)
                )
                HourlyWeatherItem(
                    time = "5 PM",
                    temperature = "22°",
                    icon = stringResource(R.string.sun_with_clouds_with_rain)
                )
                HourlyWeatherItem(
                    time = "6 PM",
                    temperature = "25°",
                    icon = stringResource(R.string.rainy)
                )
            }
        }
    }

}

@Composable
fun HourlyWeatherItem(time: String, temperature: String, icon: String) {
    Column(
        modifier = Modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = time, fontSize = 16.sp)
        Text(text = temperature, fontSize = 20.sp)
        Text(text = icon, fontSize = 24.sp)
    }
}


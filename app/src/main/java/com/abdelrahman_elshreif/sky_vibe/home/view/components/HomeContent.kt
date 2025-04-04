package com.abdelrahman_elshreif.sky_vibe.home.view.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import kotlin.math.log

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeContent(
    weatherData: WeatherResponse,
    tempUnit: String,
    windUnit: String,
    address: String,
) {
    Log.i("TA2G", "HomeContent:+$address ")
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            WeatherHeader(weatherData, tempUnit,address)
            Spacer(modifier = Modifier.height(16.dp))
            WeatherDetailsCard(weatherData, windUnit)
            Spacer(modifier = Modifier.height(16.dp))
            TodayHourlyForecast(hourlyForecastData = weatherData.hourly, tempUnit)
            NextDaysForecast(dailyForecastData = weatherData.daily, tempUnit)
        }
    }
}
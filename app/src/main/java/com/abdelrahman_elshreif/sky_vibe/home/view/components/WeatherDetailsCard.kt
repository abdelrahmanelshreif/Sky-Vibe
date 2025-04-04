package com.abdelrahman_elshreif.sky_vibe.home.view.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption

@SuppressLint("DefaultLocale")
@Composable
fun WeatherDetailsCard(weatherData: WeatherResponse, windUnit: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.weather_details),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeatherDetailItem(
                    icon = Icons.Default.Air,
                    label = stringResource(R.string.wind),
                    value = when (windUnit) {
                        SettingOption.METER_SEC.storedValue -> stringResource(
                            R.string.m_s,
                            String.format("%.1f ", weatherData.current.windSpeed)
                        )

                        SettingOption.MILE_HOUR.storedValue -> stringResource(
                            R.string.mi_hr,
                            String.format("%.1f ", weatherData.current.windSpeed)
                        )

                        else -> stringResource(
                            R.string.f,
                            String.format("%.1f ", weatherData.current.windSpeed)
                        )
                    }
                )
                WeatherDetailItem(
                    icon = Icons.Default.WbCloudy,
                    label = stringResource(R.string.clouds),
                    value = "${weatherData.current.clouds}% "
                )
                WeatherDetailItem(
                    icon = Icons.Default.WbSunny,
                    label = stringResource(R.string.uv_index),
                    value = "${weatherData.current.uvi}% "
                )
                WeatherDetailItem(
                    icon = Icons.Default.WaterDrop,
                    label = stringResource(R.string.humidity),
                    value = "${weatherData.current.humidity}%"
                )
                WeatherDetailItem(
                    icon = Icons.Default.Compress,
                    label = stringResource(R.string.pressure),
                    value = stringResource(R.string.hpa, weatherData.current.pressure)
                )
            }
        }
    }
}



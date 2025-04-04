package com.abdelrahman_elshreif.sky_vibe.home.view.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import com.abdelrahman_elshreif.sky_vibe.utils.Utility
import kotlin.math.roundToInt

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherHeader(weatherData: WeatherResponse, tempUnit: String) {
    val date = Utility.DateTimeUtil.convertUnixToDate(weatherData.current.dt)
    val currentTime = Utility.DateTimeUtil.convertUnixToDateTime(weatherData.current.dt)
    val sunriseTime = Utility.DateTimeUtil.convertUnixToDateTime(weatherData.current.sunrise)
    val sunsetTime = Utility.DateTimeUtil.convertUnixToDateTime(weatherData.current.sunset)
//    val zone = Utility.TimeZoneUtil.convertTimezoneToCityLocation(weatherData.timezone)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = LocationUtilities(LocalContext.current).getAddressFromLocation(weatherData.lat,weatherData.lon),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        AnimatedContent(targetState = weatherData.current.temp) { temp ->
            Text(
                text = when (tempUnit) {
                    SettingOption.CELSIUS.storedValue -> stringResource(R.string.c, temp.roundToInt())
                    SettingOption.KELVIN.storedValue -> stringResource(R.string.k, temp.roundToInt())
                    else -> stringResource(R.string.f, temp.roundToInt())
                },
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = weatherData.current.weather.firstOrNull()?.description?.capitalize()
                    ?: "Weather Unavailable",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            WeatherIcon(weatherData.current.weather[0].icon)
        }

        Text(
            text = "$date | $currentTime",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Sunrise",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = sunriseTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.padding(horizontal = 16.dp))

            Icon(
                imageVector = Icons.Default.NightlightRound,
                contentDescription = "Sunset",
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            Text(
                text = sunsetTime,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }
    }
}

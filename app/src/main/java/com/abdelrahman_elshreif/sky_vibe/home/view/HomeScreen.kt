package com.abdelrahman_elshreif.sky_vibe.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.DailyWeather
import com.abdelrahman_elshreif.sky_vibe.data.model.HourlyWeather
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.utils.Utility
import kotlin.math.roundToInt

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(homeViewModel: HomeViewModel, modifier: Modifier) {

    val weatherData = homeViewModel.homeWeatherData.collectAsState(null)
    val isLoading = homeViewModel.isLoading.collectAsState(true)

    val context = LocalContext.current
    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

        }
    }
    LaunchedEffect(Unit) {
        if (!context.hasPermission(locationPermission)) {
            permissionLauncher.launch(locationPermission)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF3C6FD1), Color(0xFF72B5FF)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        when {
            isLoading.value -> LoadingWeatherState()
            weatherData.value != null -> HomeContent(weatherData.value!!)
        }

    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeContent(
    weatherData: WeatherResponse,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            WeatherHeader(weatherData)
            Spacer(modifier = Modifier.height(16.dp))
            WeatherDetailsCard(weatherData)
            Spacer(modifier = Modifier.height(16.dp))
            TodayHourlyForecast(hourlyForecastData = weatherData.hourly)
            NextDaysForecast(dailyForecastData = weatherData.daily)
        }
    }
}

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherHeader(weatherData: WeatherResponse) {
    val date = Utility.DateTimeUtil.convertUnixToDate(weatherData.current.dt)
    val currentTime = Utility.DateTimeUtil.convertUnixToDateTime(weatherData.current.dt)
    val sunriseTime = Utility.DateTimeUtil.convertUnixToDateTime(weatherData.current.sunrise)
    val sunsetTime = Utility.DateTimeUtil.convertUnixToDateTime(weatherData.current.sunset)
    val zone = Utility.TimeZoneUtil.convertTimezoneToCityLocation(weatherData.timezone)
    Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = zone,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )
        AnimatedContent(targetState = weatherData.current.temp) { temp ->
            Text(
                text = stringResource(R.string.c, String.format("%.1f ", temp)),
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
                    ?: "com.abdelrahman_elshreif.sky_vibe.data.model.Weather Unavailable",
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
        Text(
            text = "Sunrise: $sunriseTime | Sunset: $sunsetTime",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}

@Composable
fun WeatherIcon(iconCode: String) {
    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    Image(
        painter = rememberAsyncImagePainter(iconUrl),
        contentDescription = "com.abdelrahman_elshreif.sky_vibe.data.model.Weather Icon",
        modifier = Modifier.size(64.dp)
    )
}

@Composable
fun WeatherDetailsCard(weatherData: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(4.dp)
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
                    value = stringResource(R.string.m_s, weatherData.current.windSpeed)
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
                text = "Loading...", style = MaterialTheme.typography.bodyLarge
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TodayHourlyForecast(hourlyForecastData: List<HourlyWeather>) {
    val todayDate = hourlyForecastData.firstOrNull()?.let {
        Utility.DateTimeUtil.convertUnixToDate(it.dt)
    }
    val groupedData = hourlyForecastData
        .groupBy {
            Utility.DateTimeUtil.convertUnixToDate(it.dt)
        }
        .filterKeys { it == todayDate }

    Text(
        text = stringResource(R.string.today_hourly_details),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
    )
    groupedData.forEach { (date, hourlyList) ->
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(hourlyList) { hourlyForecastItem ->
                        HourlyForecastItemUI(hourlyForecastItem)
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastItemUI(item: HourlyWeather) {
    Card(
        modifier = Modifier
            .width(70.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(32.dp),

        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .width(70.dp)
                .padding(4.dp)
        ) {
            Text(
                text = Utility.DateTimeUtil.convertUnixToDateHour(item.dt),
                style = MaterialTheme.typography.bodySmall
            )
            WeatherIcon(
                iconCode = item.weather[0].icon
            )

            Text(
                text = stringResource(R.string.c_forecasting, item.temp.roundToInt()),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NextDaysForecast(dailyForecastData: List<DailyWeather>) {

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = stringResource(R.string.next_7_days_forecast),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp, start = 8.dp, bottom = 4.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(dailyForecastData.take(8)) { dailyForecastDataItem ->
                DailyForecastItemUI(dailyForecastDataItem)
            }
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyForecastItemUI(dailyForecastDataItem: DailyWeather) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            //Day Name
            Text(
                text = Utility.DateTimeUtil.convertUnixToDate(dailyForecastDataItem.dt),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Average Temperature
                Text(
                    text = stringResource(
                        R.string.c_forecasting,
                        dailyForecastDataItem.temp.day.roundToInt()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                WeatherIcon(dailyForecastDataItem.weather[0].icon)
                // Min and Max Temperature
              Row {

                  Text(
                      text = stringResource(
                          R.string.degree_at_day_forecast,
                          dailyForecastDataItem.temp.min.roundToInt(),
                          dailyForecastDataItem.temp.max.roundToInt()
                      ),
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                  )
              }

            }
        }
    }
}


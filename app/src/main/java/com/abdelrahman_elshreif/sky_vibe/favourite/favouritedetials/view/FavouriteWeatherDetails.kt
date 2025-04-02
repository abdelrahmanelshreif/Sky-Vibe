package com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.view

import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.DailyWeather
import com.abdelrahman_elshreif.sky_vibe.data.model.HourlyWeather
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel.FavouriteWeatherDetailsViewModel
import com.abdelrahman_elshreif.sky_vibe.home.view.HomeContent
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.navigation.Screen
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import com.abdelrahman_elshreif.sky_vibe.utils.Utility
import kotlin.math.roundToInt

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

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherHeader(weatherData: WeatherResponse, tempUnit: String) {
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
                text = when (tempUnit) {
                    SettingOption.CELSIUS.storedValue -> stringResource(
                        R.string.c,
                        temp.roundToInt()
                    )

                    SettingOption.KELVIN.storedValue -> stringResource(
                        R.string.k,
                        temp.roundToInt()
                    )

                    else -> stringResource(
                        R.string.f,
                        temp.roundToInt()
                    )
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
        Modifier.padding(4.dp),
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
            textAlign = TextAlign.Center,
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
fun TodayHourlyForecast(hourlyForecastData: List<HourlyWeather>, tempUnit: String) {
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
                .fillMaxWidth()
                .background(color = Color.Transparent),
            shape = RoundedCornerShape(16.dp),
            CardDefaults.cardColors(Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.Transparent)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.Transparent)
                ) {
                    items(hourlyList) { hourlyForecastItem ->
                        HourlyForecastItemUI(hourlyForecastItem, tempUnit)
                    }
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastItemUI(item: HourlyWeather, tempUnit: String) {
    Card(
        modifier = Modifier
            .width(70.dp)
            .padding(8.dp)
            .background(color = Color.Transparent),
        shape = RoundedCornerShape(16.dp),
        CardDefaults.cardColors(Color.Cyan)
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
                text = when (tempUnit) {
                    SettingOption.CELSIUS.storedValue -> stringResource(
                        R.string.c,
                        item.temp.roundToInt()
                    )

                    SettingOption.KELVIN.storedValue -> stringResource(
                        R.string.k,
                        item.temp.roundToInt()
                    )

                    else -> stringResource(
                        R.string.f, item.temp.roundToInt()
                    )
                },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NextDaysForecast(dailyForecastData: List<DailyWeather>, tempUnit: String) {

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
            modifier = Modifier.padding(top = 8.dp, start = 8.dp, bottom = 4.dp)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            dailyForecastData.take(8).forEach { dailyWeather ->
                DailyForecastItemUI(dailyWeather, tempUnit)
            }
        }
    }

}

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyForecastItemUI(dailyForecastDataItem: DailyWeather, tempUnit: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),

        shape = RoundedCornerShape(32.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.width(240.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = Utility.DateTimeUtil.convertUnixToDate(dailyForecastDataItem.dt),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = dailyForecastDataItem.weather[0].description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W300
                    )


                }


                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(72.dp),
                    verticalArrangement = Arrangement.Center,

                    ) {
                    Text(
                        text = when (tempUnit) {
                            SettingOption.CELSIUS.storedValue -> stringResource(
                                R.string.c,
                                dailyForecastDataItem.temp.day.roundToInt()
                            )

                            SettingOption.KELVIN.storedValue -> stringResource(
                                R.string.k,
                                dailyForecastDataItem.temp.day.roundToInt()
                            )

                            else -> stringResource(
                                R.string.f,
                                dailyForecastDataItem.temp.day.roundToInt()
                            )
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    WeatherIcon(dailyForecastDataItem.weather[0].icon)

                    Text(
                        text = when (tempUnit) {
                            SettingOption.CELSIUS.storedValue -> stringResource(
                                R.string.degree_at_day_forecast,
                                dailyForecastDataItem.temp.min.roundToInt(),
                                dailyForecastDataItem.temp.max.roundToInt()
                            )

                            SettingOption.KELVIN.storedValue -> stringResource(
                                R.string.k,
                                dailyForecastDataItem.temp.min.roundToInt()
                            ) + " / " + stringResource(
                                R.string.k,
                                dailyForecastDataItem.temp.max.roundToInt()
                            )

                            else -> stringResource(
                                R.string.f,
                                dailyForecastDataItem.temp.min.roundToInt()
                            ) + " / " + stringResource(
                                R.string.f,
                                dailyForecastDataItem.temp.max.roundToInt()
                            )
                        }
                    )
                }
            }
        }
    }
}
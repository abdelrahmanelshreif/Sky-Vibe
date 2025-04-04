package com.abdelrahman_elshreif.sky_vibe.home.view.components

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.DailyWeather
import com.abdelrahman_elshreif.sky_vibe.settings.model.SettingOption
import com.abdelrahman_elshreif.sky_vibe.utils.Utility
import kotlin.math.roundToInt

@SuppressLint("DefaultLocale")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyForecastItemUI(dailyForecastDataItem: DailyWeather, tempUnit: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = Utility.DateTimeUtil.convertUnixToDate(dailyForecastDataItem.dt),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dailyForecastDataItem.weather[0].description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.W400,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = when (tempUnit) {
                            SettingOption.CELSIUS.storedValue -> stringResource(
                                R.string.c, dailyForecastDataItem.temp.day.roundToInt()
                            )

                            SettingOption.KELVIN.storedValue -> stringResource(
                                R.string.k, dailyForecastDataItem.temp.day.roundToInt()
                            )

                            else -> stringResource(
                                R.string.f, dailyForecastDataItem.temp.day.roundToInt()
                            )
                        },
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    WeatherIcon(dailyForecastDataItem.weather[0].icon)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = when (tempUnit) {
                            SettingOption.CELSIUS.storedValue -> stringResource(
                                R.string.degree_at_day_forecast,
                                dailyForecastDataItem.temp.min.roundToInt(),
                                dailyForecastDataItem.temp.max.roundToInt()
                            )

                            SettingOption.KELVIN.storedValue -> "${
                                stringResource(
                                    R.string.k,
                                    dailyForecastDataItem.temp.min.roundToInt()
                                )
                            } / ${
                                stringResource(
                                    R.string.k,
                                    dailyForecastDataItem.temp.max.roundToInt()
                                )
                            }"

                            else -> "${
                                stringResource(
                                    R.string.f,
                                    dailyForecastDataItem.temp.min.roundToInt()
                                )
                            } / ${
                                stringResource(
                                    R.string.f,
                                    dailyForecastDataItem.temp.max.roundToInt()
                                )
                            }"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}


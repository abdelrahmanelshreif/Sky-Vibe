package com.abdelrahman_elshreif.sky_vibe.home.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.HourlyWeather
import com.abdelrahman_elshreif.sky_vibe.utils.Utility

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
package com.abdelrahman_elshreif.sky_vibe.home.view.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.data.model.DailyWeather

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

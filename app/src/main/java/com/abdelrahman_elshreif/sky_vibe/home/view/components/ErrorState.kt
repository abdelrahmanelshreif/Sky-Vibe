package com.abdelrahman_elshreif.sky_vibe.home.view.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R

@Composable
fun ErrorState() {
    Text(
        text = stringResource(R.string.unable_to_load_weather_data_please_try_again_later),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(16.dp)
    )
}
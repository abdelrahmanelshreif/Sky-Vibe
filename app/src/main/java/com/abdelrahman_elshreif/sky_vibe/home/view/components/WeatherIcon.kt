package com.abdelrahman_elshreif.sky_vibe.home.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun WeatherIcon(iconCode: String) {
    val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
    Image(
        painter = rememberAsyncImagePainter(iconUrl),
        contentDescription = "com.abdelrahman_elshreif.sky_vibe.data.model.Weather Icon",
        modifier = Modifier.size(64.dp)
    )

}
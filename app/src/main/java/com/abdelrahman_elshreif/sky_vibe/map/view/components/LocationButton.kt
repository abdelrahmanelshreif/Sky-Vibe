package com.abdelrahman_elshreif.sky_vibe.map.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.abdelrahman_elshreif.sky_vibe.R

@Composable
fun LocationButton(onClick: () -> Unit, modifier: Modifier) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            imageVector = Icons.Default.MyLocation,
            contentDescription = stringResource(R.string.my_location),
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

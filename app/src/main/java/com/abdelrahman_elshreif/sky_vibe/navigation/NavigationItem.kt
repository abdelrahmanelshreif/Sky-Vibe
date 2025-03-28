package com.abdelrahman_elshreif.sky_vibe.navigation

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.abdelrahman_elshreif.sky_vibe.R

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)
@Composable
fun getNavigationItems(): List<NavigationItem> {
    val context = LocalContext.current
    return listOf(
        NavigationItem(
            title = context.getString(R.string.home),
            icon = Icons.Default.Home,
            route = Screen.Home.route
        ),
        NavigationItem(
            title = context.getString(R.string.favourite),
            icon = Icons.Default.Favorite,
            route = Screen.Favourite.route
        ),
        NavigationItem(
            title = context.getString(R.string.alarm),
            icon = Icons.Default.Alarm,
            route = Screen.Alarm.route
        ),
        NavigationItem(
            title = context.getString(R.string.settings),
            icon = Icons.Default.Settings,
            route = Screen.Settings.route
        )
    )
}
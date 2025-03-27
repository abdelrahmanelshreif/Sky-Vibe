package com.abdelrahman_elshreif.sky_vibe.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val navigationItems = listOf(
    NavigationItem(
        title = "Home",
        icon = Icons.Default.Person,
        route = Screen.Home.route
    ),
    NavigationItem(
        title = "Favourite",
        icon = Icons.Default.Person,
        route = Screen.Favourite.route
    ),

    NavigationItem(
        title = "Alarm",
        icon = Icons.Default.Person,
        route = Screen.Alarm.route
    ),

    NavigationItem(
        title = "Settings",
        icon = Icons.Default.Person,
        route = Screen.Settings.route
    )

)

package com.abdelrahman_elshreif.sky_vibe.navigation

sealed class Screen (val route :String ){
    object Home:Screen("home_screen")
    object Favourite:Screen("favourite_screen")
    object Alarm:Screen("alarm_screen")
    object Settings:Screen("setting_screen")
}


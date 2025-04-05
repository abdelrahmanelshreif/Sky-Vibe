package com.abdelrahman_elshreif.sky_vibe.navigation

sealed class Screen(val route: String) {

    object Home : Screen("home_screen")

    object Favourite : Screen("favourite_screen")

    object Alarm : Screen("alarm_screen")

    object Settings : Screen("setting_screen")

    object Map : Screen("add_location")

    object FavouriteWeatherDetails : Screen("favourite_weather_details/{latitude}/{longitude}") {
        fun createRoute(latitude: Double, longitude: Double): String {
            return "favourite_weather_details/$latitude/$longitude"
        }
    }
}


package com.abdelrahman_elshreif.sky_vibe.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.abdelrahman_elshreif.sky_vibe.alarm.view.AlarmScreen
import com.abdelrahman_elshreif.sky_vibe.favourite.view.FavouriteScreen
import com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.view.FavouriteWeatherDetails
import com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel.FavouriteWeatherDetailsViewModel
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import com.abdelrahman_elshreif.sky_vibe.home.view.HomeScreen
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.map.view.MapScreen
import com.abdelrahman_elshreif.sky_vibe.settings.view.SettingScreen
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    homeViewModel: HomeViewModel,
    favouriteViewModel: FavouriteViewModel,
    settingViewModel: SettingViewModel,
    favWeatherDetailViewModel: FavouriteWeatherDetailsViewModel,
    paddingValues: PaddingValues,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Home.route) {
            HomeScreen(homeViewModel)
        }
        composable(Screen.Favourite.route) {
            FavouriteScreen(favouriteViewModel, navController)
        }
        composable(Screen.Alarm.route) {
            AlarmScreen()
        }
        composable(Screen.Settings.route) {
            SettingScreen(settingViewModel)
        }

        composable("add_location") {
            MapScreen(viewModel = favouriteViewModel, navController)
        }

        composable(
            route = Screen.FavouriteWeatherDetails.route,
            arguments = listOf(
                navArgument("latitude") {
                    type = NavType.FloatType
                },
                navArgument("longitude") {
                    type = NavType.FloatType
                }
            )
        ) { navBackStackEntry ->
            val latitude = navBackStackEntry.arguments?.getFloat("latitude")?.toDouble() ?: 0.0
            val longitude = navBackStackEntry.arguments?.getFloat("longitude")?.toDouble() ?: 0.0

            FavouriteWeatherDetails(
                lat = latitude,
                lon = longitude,
                favWeatherDetailViewModel,
                navController = navController,
            )
        }
    }
}
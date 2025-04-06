package com.abdelrahman_elshreif.sky_vibe

import android.app.Application
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import androidx.annotation.RequiresApi
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import androidx.work.WorkManager
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.AlarmViewModel
import com.abdelrahman_elshreif.sky_vibe.favourite.favouritedetials.viewmodel.FavouriteWeatherDetailsViewModel
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.navigation.AppNavigation
import com.abdelrahman_elshreif.sky_vibe.navigation.getNavigationItems
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModel
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import com.abdelrahman_elshreif.sky_vibe.utils.NetworkUtils
import com.abdelrahman_elshreif.sky_vibe.utils.WeatherNotificationManager
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration

class SkyVibeApp : Application() {
    private lateinit var notificationManager: WeatherNotificationManager
    private lateinit var workManager: WorkManager

    override fun onCreate() {
        super.onCreate()
        Configuration.getInstance().load(
            applicationContext,
            PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )
        notificationManager = WeatherNotificationManager(this)
        workManager = WorkManager.getInstance(this)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkyVibeApp(
    homeViewModel: HomeViewModel,
    settingViewModel: SettingViewModel,
    favouriteViewModel: FavouriteViewModel,
    favWeatherDetailViewModel: FavouriteWeatherDetailsViewModel,
    alarmViewModel: AlarmViewModel,
    locationUtilities: LocationUtilities,
    networkUtils:NetworkUtils
) {
    val navController = rememberNavController()
    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }


        Scaffold(
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    getNavigationItems().forEachIndexed { index, navigationItem ->
                        NavigationBarItem(
                            selected = selectedNavigationIndex.intValue == index,
                            onClick = {
                                selectedNavigationIndex.intValue = index
                                navController.navigate(navigationItem.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = navigationItem.icon,
                                    contentDescription = navigationItem.title
                                )
                            },
                            label = {
                                Text(
                                    navigationItem.title,
                                    color = if (index == selectedNavigationIndex.intValue)
                                        Color.Black
                                    else Color.Gray
                                )
                            }
                        )
                    }
                }
            },
            content = { paddingValues ->
                AppNavigation(
                    homeViewModel,
                    favouriteViewModel,
                    settingViewModel,
                    favWeatherDetailViewModel,
                    alarmViewModel,
                    paddingValues,
                    navController,
                    networkUtils,
                    locationUtilities
                )
            }
        )
    }


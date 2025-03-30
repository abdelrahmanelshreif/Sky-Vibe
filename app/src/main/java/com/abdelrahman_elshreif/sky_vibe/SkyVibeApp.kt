package com.abdelrahman_elshreif.sky_vibe

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abdelrahman_elshreif.sky_vibe.alarm.view.AlarmScreen
import com.abdelrahman_elshreif.sky_vibe.favourite.view.FavouriteScreen
import com.abdelrahman_elshreif.sky_vibe.home.view.HomeScreen
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.navigation.Screen
import com.abdelrahman_elshreif.sky_vibe.navigation.getNavigationItems
import com.abdelrahman_elshreif.sky_vibe.settings.view.SettingScreen
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SkyVibeApp(homeViewModel: HomeViewModel, settingViewModel: SettingViewModel) {
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
            NavHost(
                navController = navController,
                startDestination = Screen.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(homeViewModel, modifier = Modifier)
                }
                composable(Screen.Favourite.route) {
                    FavouriteScreen()
                }
                composable(Screen.Alarm.route) {
                    AlarmScreen()
                }
                composable(Screen.Settings.route) {
                    SettingScreen(settingViewModel)
                }
            }
        }
    )
}





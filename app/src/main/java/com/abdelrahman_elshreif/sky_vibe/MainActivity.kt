package com.abdelrahman_elshreif.sky_vibe

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHost
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import com.abdelrahman_elshreif.sky_vibe.alarm.view.AlarmScreen
import com.abdelrahman_elshreif.sky_vibe.data.local.ForecastingLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.ForecastingRemoteDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.RetrofitHelper
import com.abdelrahman_elshreif.sky_vibe.home.view.HomeScreen
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.favourite.view.FavouriteScreen
import com.abdelrahman_elshreif.sky_vibe.navigation.Screen
import com.abdelrahman_elshreif.sky_vibe.navigation.getNavigationItems
import com.abdelrahman_elshreif.sky_vibe.settings.view.SettingScreen
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModel
import com.abdelrahman_elshreif.sky_vibe.settings.viewmodel.SettingViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities

class MainActivity : ComponentActivity() {

    private lateinit var locationUtilities: LocationUtilities

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        locationUtilities = LocationUtilities(this)

        val homeFactory = HomeViewModelFactory(
            SkyVibeRepository.getInstance(
                ForecastingRemoteDataSource(RetrofitHelper.apiservice),
                ForecastingLocalDataSource()
            ),
            locationUtilities
        )

        val settingFactory = SettingViewModelFactory(
            this@MainActivity
        )

        val homeViewModel: HomeViewModel by viewModels {homeFactory }
        val settingViewModel: SettingViewModel by viewModels { settingFactory }

        setContent {
            SkyVibeApp(homeViewModel,settingViewModel)
        }
    }
}

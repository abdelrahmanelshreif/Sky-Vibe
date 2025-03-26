package com.abdelrahman_elshreif.sky_vibe

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.abdelrahman_elshreif.sky_vibe.data.local.ForecastingLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.ForecastingRemoteDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.RetrofitHelper
import com.abdelrahman_elshreif.sky_vibe.home.view.WeatherApp
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModel
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.HomeViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var locationUtilities: LocationUtilities
    private lateinit var homeViewModel: HomeViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationUtilities = LocationUtilities(this)
        homeViewModel = ViewModelProvider(
            this, HomeViewModelFactory(
                SkyVibeRepository.getInstance(
                    ForecastingRemoteDataSource(RetrofitHelper.apiservice),
                    ForecastingLocalDataSource()
                )
            )
        )[HomeViewModel::class.java]
        setContent {
            WeatherApp(homeViewModel)
        }

        if (locationUtilities.checkLocationAvailability()) {
            fetchLocationAndWeather()
        } else {
            requestLocationPermissions()
        }

    }

    private fun requestLocationPermissions() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                fetchLocationAndWeather()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    openAppSettings()
                }
            }
        }

    private fun fetchLocationAndWeather() {
        lifecycleScope.launch {
            val location = locationUtilities.getOrFetchLocation()
            location.let {
                if (location != null) {
                    homeViewModel.fetchWeatherData(location.first, location.second)
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null)
        )
        startActivity(intent)
    }
}
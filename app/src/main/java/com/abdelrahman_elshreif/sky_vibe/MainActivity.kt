package com.abdelrahman_elshreif.sky_vibe

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.home.view.HomeScreen
import com.abdelrahman_elshreif.sky_vibe.home.view.WeatherApp
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.LocationViewModel
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.LocationViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities

class MainActivity : ComponentActivity() {

    private lateinit var locationViewModel: LocationViewModel

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationViewModel.fetchLocation()
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    openAppSettings()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationViewModel = ViewModelProvider(
            this,
            LocationViewModelFactory(LocationUtilities(this))
        ).get(LocationViewModel::class.java)

        setContent {
            WeatherApp (
                viewModel = locationViewModel,
                onRequestPermission = { requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
            )
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

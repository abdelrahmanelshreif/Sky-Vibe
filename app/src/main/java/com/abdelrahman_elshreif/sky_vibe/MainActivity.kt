package com.abdelrahman_elshreif.sky_vibe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.LocationViewModel
import com.abdelrahman_elshreif.sky_vibe.home.viewmodel.LocationViewModelFactory
import com.abdelrahman_elshreif.sky_vibe.repo.LocationRepository


class MainActivity : ComponentActivity() {

//    private val factory = LocationViewModelFactory(
//        LocationRepository(applicationContext)
//
//    )
//
//    private val locationViewModel =
//        ViewModelProvider(
//            this@MainActivity,
//            factory
//        ).get(LocationViewModel::class.java)

    private val locationViewModel: LocationViewModel by viewModels {
        LocationViewModelFactory(LocationRepository(applicationContext))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocationScreen(locationViewModel)
        }
    }
}


@Composable
fun LocationScreen(viewModel: LocationViewModel) {
    val location by viewModel.loctaionFlow.collectAsState()
    val address by viewModel.addressFlow.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Latitude: ${location?.latitude ?: "Unknown"}")
        Text(text = "Longitude: ${location?.longitude ?: "Unknown"}")
        Text(text = "Address: $address")

        Button(onClick = { viewModel.fetchLocation() }) {
            Text(text = "Get Location")
        }
    }
}
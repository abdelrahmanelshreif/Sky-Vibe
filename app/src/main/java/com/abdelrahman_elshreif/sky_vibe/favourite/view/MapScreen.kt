package com.abdelrahman_elshreif.sky_vibe.favourite.view

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.abdelrahman_elshreif.sky_vibe.favourite.viewModel.FavouriteViewModel
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun MapScreen(viewModel: FavouriteViewModel) {

    val favoritePlaces = viewModel.favouritePlaces.value
    AndroidView(factory = { context ->
        MapView(context).apply {
            setMultiTouchControls(true)
            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(48.8583, 2.2944))
        }
    }, update = { mapView ->
        mapView.overlays.clear()
        favoritePlaces.forEach { geoPoint ->
            val marker = Marker(mapView)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
        }
    })

}
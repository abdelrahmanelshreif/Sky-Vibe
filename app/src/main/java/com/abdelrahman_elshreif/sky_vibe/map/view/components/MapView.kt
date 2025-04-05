package com.abdelrahman_elshreif.sky_vibe.map.view.components

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.abdelrahman_elshreif.sky_vibe.map.model.MapScreenState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController

@SuppressLint("ClickableViewAccessibility")
@Composable
fun MapView(
    state: MapScreenState,
    onMapClicked: (Double, Double) -> Unit
) {
    AndroidView(
        factory = { context ->
            org.osmdroid.views.MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                zoomController.setZoomInEnabled(true)
                zoomController.setZoomOutEnabled(true)
                setMultiTouchControls(true)
                setBuiltInZoomControls(true)
                controller.setZoom(15.0)
                zoomController.apply {
                    setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
                    activate()
                }

            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            mapView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val projection = mapView.projection
                    val geoPoint = projection.fromPixels(
                        event.x.toInt(),
                        event.y.toInt()
                    ) as GeoPoint

                    onMapClicked(geoPoint.latitude, geoPoint.longitude)
                    true
                } else false
            }

            state.selectedLocation?.let { location ->
                updateMapMarker(mapView, location)
            } ?: run {
                mapView.controller.setCenter(state.defaultLocation)
                mapView.invalidate()
            }
        }
    )
}
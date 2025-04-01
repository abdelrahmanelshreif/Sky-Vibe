package com.abdelrahman_elshreif.sky_vibe.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location_prefs")

class LocationUtilities(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val LATITUDE = doublePreferencesKey("latitude")
    private val LONGITUDE = doublePreferencesKey("longitude")
    private val ADDRESS = stringPreferencesKey("address")


    @SuppressLint("MissingPermission")
    suspend fun getFreshLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            val locationRequest = LocationRequest.Builder(10000L).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    val location = result.lastLocation
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        continuation.resumeWithException(Exception("Failed to get location"))
                    }
                    fusedClient.removeLocationUpdates(this)
                }
            }
            fusedClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )

            continuation.invokeOnCancellation {
                fusedClient.removeLocationUpdates(locationCallback)
            }
        }
    }

    suspend fun getOrFetchLocation(): Pair<Double, Double>? {
        val savedLocation = getLocationFromDataStore()
        if (savedLocation != null) {
            return savedLocation
        } else {
            val (location, _) = fetchLocationAndAddress()
            if (location != null) {
                saveLocationToDataStore(location.latitude, location.longitude)
                return Pair(location.latitude, location.longitude)
            }
        }
        return null
    }

    private fun checkLocationAvailability(): Boolean {
        return checkPermissions() && isLocationEnabled()
    }

    private fun checkPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun getAddressFromLocation(lat: Double, lon: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            addresses?.firstOrNull()?.getAddressLine(0) ?: "Address not found"
        } catch (ex: Exception) {
            "Error fetching address: ${ex.message}"
        }
    }

    private suspend fun fetchLocationAndAddress(): Pair<Location?, String> {
        return try {
            if (!checkLocationAvailability()) {
                return Pair(null, "Location not available")
            }

            val location = getFreshLocation()
            val address = location?.let {
                getAddressFromLocation(it.latitude, it.longitude)
            } ?: "Location not found"

            Pair(location, address)
        } catch (e: Exception) {
            Pair(null, "Error: ${e.message}")
        }
    }

    private suspend fun getLocationFromDataStore(): Pair<Double, Double>? {
        return context.dataStore.data.map { preferences ->
            val latitude = preferences[LATITUDE]
            val longitude = preferences[LONGITUDE]
            if (latitude != null && longitude != null) {
                Pair(latitude, longitude)
            } else {
                null
            }
        }.firstOrNull()
    }

    private suspend fun saveLocationToDataStore(latitude: Double, longitude: Double) {
        context.dataStore.edit { preferences ->
            preferences[LATITUDE] = latitude
            preferences[LONGITUDE] = longitude
        }
    }

    suspend fun saveLocationFromMapToDataStore(
        lat: Double,
        lon: Double,
        address:String? = null
    ){
        val locationHolder = SkyVibeLocation(
            latitude = lat,
            longitude = lon,
            address = address
        )

        context.dataStore.edit {preferences->
            preferences[LATITUDE] = locationHolder.latitude
            preferences[LONGITUDE] = locationHolder.longitude
            locationHolder.address?.let {
                preferences[ADDRESS] = it
            }
        }
    }

}
package com.abdelrahman_elshreif.sky_vibe.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

interface ILocationUtilities {
    @SuppressLint("MissingPermission")
    suspend fun getFreshLocation(): Location?

    suspend fun getOrFetchLocation(): Pair<Double, Double>?
    fun checkLocationAvailability(): Boolean
    fun checkPermissions(): Boolean
    fun isLocationEnabled(): Boolean

    suspend fun getAddressFromLocation(lat: Double, lon: Double): String

    suspend fun fetchLocationAndAddress(): Pair<Location?, String>

    suspend fun getLocationFromDataStore(): Pair<Double, Double>?

    suspend fun clearLocationFromSharedPrefs()

    suspend fun saveLocationToDataStore(latitude: Double, longitude: Double)
}


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location_prefs")

class LocationUtilities(private val context: Context) : ILocationUtilities {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val LATITUDE = doublePreferencesKey("latitude")
    private val LONGITUDE = doublePreferencesKey("longitude")
    private val ADDRESS = stringPreferencesKey("address")

    @SuppressLint("MissingPermission")
    override suspend fun getFreshLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            val locationRequest =
                LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L).build()

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
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

    override suspend fun getOrFetchLocation(): Pair<Double, Double>? {
        return if (checkPermissions() && isLocationEnabled()) {
            fetchLocationAndAddress().let { (location, _) ->
                location?.let {
                    saveLocationToDataStore(it.latitude, it.longitude)
                    Pair(it.latitude, it.longitude)
                }
            }
        } else {
            getLocationFromDataStore()
        }
    }

    override fun checkLocationAvailability(): Boolean {
        return checkPermissions() && isLocationEnabled()
    }

    override fun checkPermissions(): Boolean {
        return (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }

    override fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override suspend fun getAddressFromLocation(lat: Double, lon: Double): String {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses = geocoder.getFromLocation(lat, lon, 1)
                if (!addresses.isNullOrEmpty()) {
                    val city = addresses[0].locality ?: addresses[0].subAdminArea ?: ""
                    val country = addresses[0].countryName ?: ""
                    "$city, $country".trim().replace("^, ".toRegex(), "")
                } else "Unknown Location"
            } catch (ex: Exception) {
                "Error fetching address: ${ex.message}"
            }
        }
    }

    override suspend fun fetchLocationAndAddress(): Pair<Location?, String> {
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

    override suspend fun getLocationFromDataStore(): Pair<Double, Double>? {
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

    override suspend fun clearLocationFromSharedPrefs() {
        context.dataStore.edit { preferences ->
            preferences.remove(LATITUDE)
            preferences.remove(LONGITUDE)
        }
    }

    override suspend fun saveLocationToDataStore(latitude: Double, longitude: Double) {
        context.dataStore.edit { preferences ->
            preferences[LATITUDE] = latitude
            preferences[LONGITUDE] = longitude
        }
    }


}
package com.abdelrahman_elshreif.sky_vibe.utils

import android.location.Location

class FakeLocationUtilities : ILocationUtilities {
    override suspend fun getFreshLocation(): Location? {
        TODO("Not yet implemented")
    }

    override suspend fun getOrFetchLocation(): Pair<Double, Double>? {
        return Pair(30.0, 31.0)
    }

    override fun checkLocationAvailability(): Boolean {
        TODO("Not yet implemented")
    }

    override fun checkPermissions(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isLocationEnabled(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getAddressFromLocation(lat: Double, lon: Double): String {
        return "Fake Address"
    }

    override suspend fun fetchLocationAndAddress(): Pair<Location?, String> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocationFromDataStore(): Pair<Double, Double>? {
        return Pair(30.0, 31.0)
    }

    override suspend fun clearLocationFromSharedPrefs() {
        TODO("Not yet implemented")
    }

    override suspend fun saveLocationToDataStore(latitude: Double, longitude: Double) {
        TODO("Not yet implemented")
    }
}

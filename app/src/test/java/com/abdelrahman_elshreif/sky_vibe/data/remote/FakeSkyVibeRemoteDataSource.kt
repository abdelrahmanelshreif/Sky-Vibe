package com.abdelrahman_elshreif.sky_vibe.data.remote

import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse

class FakeSkyVibeRemoteDataSource : ISkyVibeRemoteDataSource {
    private val fakeWeatherResponse = FakeWeatherResponseFactory.createFakeWeatherResponse()
    private val fakeSuggestedLocations = FakeWeatherResponseFactory.createFakeLocationsList()

    override suspend fun getWeatherDataOfCoordinates(lat: Double, lon: Double): WeatherResponse? {
        val fakeWeatherResponse = FakeWeatherResponseFactory.createFakeWeatherResponse()
        return fakeWeatherResponse
    }

    override suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): WeatherResponse? {
        return fakeWeatherResponse
    }

    override suspend fun getWeatherDataOfCoordinates(
        lat: Double,
        lon: Double,
        lang: String,
        unit: String
    ): WeatherResponse? {
        return fakeWeatherResponse
    }

    override suspend fun getSuggestedLocations(query: String): List<NominatimLocation> {
        return fakeSuggestedLocations
    }
}
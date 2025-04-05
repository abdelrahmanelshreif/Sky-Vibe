package com.abdelrahman_elshreif.sky_vibe.data.repo

import com.abdelrahman_elshreif.sky_vibe.alarm.model.AlertType
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.local.FakeSkyVibeLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.local.ISkyVibeLocalDataSource
import com.abdelrahman_elshreif.sky_vibe.data.model.NominatimLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import com.abdelrahman_elshreif.sky_vibe.data.model.WeatherResponse
import com.abdelrahman_elshreif.sky_vibe.data.remote.FakeSkyVibeRemoteDataSource
import com.abdelrahman_elshreif.sky_vibe.data.remote.ISkyVibeRemoteDataSource
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SkyVibeRepositoryTest {

    private lateinit var fakeSkyVibeRemoteDataSource: ISkyVibeRemoteDataSource
    private lateinit var fakeSkyVibeLocalDataSource: ISkyVibeLocalDataSource
    private lateinit var repository: SkyVibeRepository

    @Before
    fun setup() {
        SkyVibeRepository.repository = null
        fakeSkyVibeRemoteDataSource = FakeSkyVibeRemoteDataSource()
        fakeSkyVibeLocalDataSource = FakeSkyVibeLocalDataSource()
        repository =
            SkyVibeRepository.getInstance(fakeSkyVibeRemoteDataSource,fakeSkyVibeLocalDataSource)!!
    }

    @Test
    fun getWeatherByCoordinates_returnsWeatherData() = runTest {
        val lat = 30.0
        val lon = 31.0
        val result = repository.getWeatherByCoordinates(lat, lon).first()
        assertNotNull(result)
        assertEquals(lat, result?.lat)
        assertEquals(lon, result?.lon)
    }

    @Test
    fun getWeatherByCoordinates_withLanguage_returnsWeatherData() = runTest {
        val lat = 30.0
        val lon = 31.0
        val lang = "ar"

        val result = repository.getWeatherByCoordinates(lat, lon, lang).first()

        assertNotNull(result)
        assertEquals(lat, result?.lat)
        assertEquals(lon, result?.lon)
    }

    @Test
    fun getWeatherByCoordinates_withLanguageAndUnit_returnsWeatherData() = runTest {
        val lat = 30.0
        val lon = 31.0
        val lang = "ar"
        val unit = "metric"

        val result = repository.getWeatherByCoordinates(lat, lon, lang, unit).first()

        assertNotNull(result)
        assertEquals(lat, result?.lat)
        assertEquals(lon, result?.lon)
    }

    @Test
    fun getAllSavedLocations_returnsEmptyInitially() = runTest {
        val locations = repository.getAllSavedLocations().first()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun addLocationToFavourite_locationSaved() = runTest {
        val location = SkyVibeLocation(
            latitude = 30.0,
            longitude = 31.0,
            address = "Cairo, Egypt"
        )

        repository.addLocationToFavourite(location)
        val savedLocations = repository.getAllSavedLocations().first()

        assertTrue(savedLocations.isNotEmpty())
        assertEquals(location, savedLocations[0])
    }

    @Test
    fun deleteLocationFromFavourite_locationRemoved() = runTest {
        val location = SkyVibeLocation(
            latitude = 30.0,
            longitude = 31.0,
            address = "Cairo, Egypt"
        )
        repository.addLocationToFavourite(location)

        repository.deleteLocationFromFavourite(location)
        val locations = repository.getAllSavedLocations().first()

        assertTrue(locations.isEmpty())
    }

    @Test
    fun searchLocations_returnsMatchingResults() = runTest {
        val query = "Cairo"

        val results = repository.searchLocations(query).first()

        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.displayName.contains(query, ignoreCase = true) })
    }

    @Test
    fun getAlerts_returnsEmptyInitially() = runTest {
        val alerts = repository.getAlerts().first()

        assertTrue(alerts.isEmpty())
    }

    @Test
    fun addNewAlert_alertSaved() = runTest {
        val alert = WeatherAlert(
            alertArea = "Cairo, Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.NOTIFICATION,
            isEnabled = true,
            description = "Test Alert"
        )

        repository.addNewAlert(alert)
        val savedAlerts = repository.getAlerts().first()

        assertTrue(savedAlerts.isNotEmpty())
        assertEquals(alert, savedAlerts[0])
    }

    @Test
    fun deleteAlert_alertRemoved() = runTest {
        val alert = WeatherAlert(
            alertArea = "Cairo, Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.NOTIFICATION,
            isEnabled = true,
            description = "Test Alert"
        )
        repository.addNewAlert(alert)
        repository.deleteAlert(alert)
        val alerts = repository.getAlerts().first()

        assertTrue(alerts.isEmpty())
    }

    @Test
    fun updateAlert_alertModified() = runTest {
        val alert = WeatherAlert(
            alertArea = "Cairo, Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.NOTIFICATION,
            isEnabled = false,
            description = "Original Alert"
        )
        repository.addNewAlert(alert)

        val updatedAlert = alert.copy(
            description = "Updated Alert",
            isEnabled = true
        )
        repository.updateAlert(updatedAlert)
        val savedAlerts = repository.getAlerts().first()
        assertEquals(updatedAlert, savedAlerts[0])
    }

}


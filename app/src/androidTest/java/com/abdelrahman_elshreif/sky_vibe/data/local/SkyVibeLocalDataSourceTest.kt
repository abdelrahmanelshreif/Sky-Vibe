package com.abdelrahman_elshreif.sky_vibe.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.filters.SmallTest
import com.abdelrahman_elshreif.sky_vibe.alarm.model.AlertType
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@MediumTest
class SkyVibeLocalDataSourceTest {
    private lateinit var skyVibeDatabase: SkyVibeDatabase
    private lateinit var skyVibeLocalDataSource: SkyVibeLocalDataSource


    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        skyVibeDatabase = Room.inMemoryDatabaseBuilder(context, SkyVibeDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        skyVibeLocalDataSource = SkyVibeLocalDataSource(
            skyVibeDatabase.getFavouriteLocationDao(),
            skyVibeDatabase.getAlertsDao()
        )
    }

    @After
    fun dearDown() = skyVibeDatabase.close()

    @Test
    fun getFavouriteLocations_emptyInitially() = runTest {
        val locations = skyVibeLocalDataSource.getFavouriteLocations().first()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun addLocationToFavourite_locationSaved() = runTest {
        val location = SkyVibeLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            address = "Cairo, Egypt"
        )
        skyVibeLocalDataSource.addLocationToFavourite(location)

        val locations = skyVibeLocalDataSource.getFavouriteLocations().first()
        assertThat(locations.size, `is`(1))
        assertThat(locations[0].latitude, `is`(location.latitude))
        assertThat(locations[0].longitude, `is`(location.longitude))
        assertThat(locations[0].address, `is`(location.address))
    }

    @Test
    fun deleteLocationFromFavourite_locationRemoved() = runTest {
        val location = SkyVibeLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            address = "Cairo, Egypt"
        )
        skyVibeLocalDataSource.addLocationToFavourite(location)

        val savedLocation = skyVibeLocalDataSource.getFavouriteLocations().first()[0]
        skyVibeLocalDataSource.deleteLocationFromFavourite(savedLocation)

        val locations = skyVibeLocalDataSource.getFavouriteLocations().first()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun getAlerts_emptyInitially() = runTest {
        val alerts = skyVibeLocalDataSource.getAlerts().first()
        assertTrue(alerts.isEmpty())
    }

    @Test
    fun addAlert_alertSaved() = runTest {
        val alert = WeatherAlert(
            alertArea = "Alexandria, Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.ALARM,
            isEnabled = true,
            description = "Test Alert",
            longitude = 30.0,
            latitude = 30.0
        )
        skyVibeLocalDataSource.addAlert(alert)

        val alerts = skyVibeLocalDataSource.getAlerts().first()
        assertThat(alerts.size, `is`(1))
        assertThat(alerts[0].alertArea, `is`(alert.alertArea))
        assertThat(alerts[0].type, `is`(alert.type))
    }

    @Test
    fun deleteAlert_alertRemoved() = runTest {
        val alert = WeatherAlert(
            alertArea = "Cairo, Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.NOTIFICATION,
            isEnabled = true,
            description = "Test Alert",
            longitude = 30.0,
            latitude = 30.0
        )
        skyVibeLocalDataSource.addAlert(alert)

        val savedAlert = skyVibeLocalDataSource.getAlerts().first()[0]
        skyVibeLocalDataSource.deleteAlert(savedAlert)

        val alerts = skyVibeLocalDataSource.getAlerts().first()
        assertTrue(alerts.isEmpty())
    }

    @Test
    fun updateAlert_alertModified() = runTest {
        val alert = WeatherAlert(
            alertArea = "Giza, Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.ALARM,
            isEnabled = false,
            description = "Original Alert",
            longitude = 30.0,
            latitude = 30.0
        )
        skyVibeLocalDataSource.addAlert(alert)

        val savedAlert = skyVibeLocalDataSource.getAlerts().first()[0]
        val updatedAlert = savedAlert.copy(
            description = "Updated Alert",
            isEnabled = true
        )
        skyVibeLocalDataSource.updateAlert(updatedAlert)

        val retrievedAlerts = skyVibeLocalDataSource.getAlerts().first()
        assertThat(retrievedAlerts[0].description, `is`("Updated Alert"))
        assertThat(retrievedAlerts[0].isEnabled, `is`(true))
    }


    @Test
    fun disableAlert_alertIsEnabledFalse() = runTest {
        val alert = WeatherAlert(
            alertArea = "Giza, Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.ALARM,
            isEnabled = true,
            description = "Original Alert",
            longitude = 30.0,
            latitude = 30.0
        )

        val id = skyVibeLocalDataSource.addAlert(alert)

        skyVibeLocalDataSource.disableAlert(alertId = id)


        val retrievedAlerts = skyVibeLocalDataSource.getAlerts().first()
        assertThat(retrievedAlerts[0].isEnabled, `is`(false))
    }



}
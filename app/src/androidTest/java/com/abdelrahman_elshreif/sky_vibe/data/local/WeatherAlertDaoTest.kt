package com.abdelrahman_elshreif.sky_vibe.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.abdelrahman_elshreif.sky_vibe.alarm.model.AlertType
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherAlertDaoTest {
    private lateinit var alertDao: WeatherAlertDao
    private lateinit var database: SkyVibeDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SkyVibeDatabase::class.java
        ).build()
        alertDao = database.getAlertsDao()
    }

    @After
    fun dearDown() = database.close()

    @Test
    fun saveAlert_AlertSizeEqual1() = runTest {
        val alert = WeatherAlert(
            alertArea = "Tanta , Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.ALARM,
            isEnabled = true,
            description = "ALERT"
        )
        alertDao.insertNewAlert(alert)
        val retrievedAlerts = alertDao.getAllAlerts().first()
        assertThat(retrievedAlerts.size, `is`(1))
    }


    @Test
    fun getAllAlerts_emptyAlertsInitially() = runTest {
        val allAlerts = alertDao.getAllAlerts().first()
        assertTrue(allAlerts.isEmpty())
    }

    @Test
    fun saveAlert_retrieveAlert() = runTest {
        val alert = WeatherAlert(
            alertArea = "Tanta , Egypt",
            startTime = 1708090090,
            endTime = 1709090014,
            type = AlertType.ALARM,
            isEnabled = true,
            description = "ALERT"
        )
        alertDao.insertNewAlert(alert)
        val retrieveAlert = alertDao.getAllAlerts().first()[0]
        assertThat(retrieveAlert, notNullValue())
        assertThat(retrieveAlert.type, `is`(alert.type))
        assertThat(retrieveAlert.alertArea, `is`(alert.alertArea))
        assertThat(retrieveAlert.description, `is`(alert.description))
        assertThat(retrieveAlert.startTime, `is`(alert.startTime))
        assertThat(retrieveAlert.endTime, `is`(alert.endTime))
        assertThat(retrieveAlert.isEnabled, `is`(alert.isEnabled))
    }

    @Test
    fun insertMultipleAlerts_allSavedCorrectly() = runTest {
        val alert1 = WeatherAlert(
            alertArea = "Area 1",
            startTime = 1,
            endTime = 2,
            type = AlertType.NOTIFICATION,
            isEnabled = true,
            description = "Desc1"
        )
        val alert2 = WeatherAlert(
            alertArea = "Area 2",
            startTime = 3,
            endTime = 4,
            type = AlertType.ALARM,
            isEnabled = false,
            description = "Desc2"
        )

        alertDao.insertNewAlert(alert1)
        alertDao.insertNewAlert(alert2)

        val alerts = alertDao.getAllAlerts().first()
        assertThat(alerts.size, `is`(2))
    }


    @Test
    fun insertAndUpdateAlert_alertUpdatedCorrectly() = runTest {
        val alert = WeatherAlert(
            alertArea = "Alexandria, Egypt",
            startTime = 1708001111,
            endTime = 1709002222,
            type = AlertType.NOTIFICATION,
            isEnabled = false,
            description = "Old Desc"
        )

        val id = alertDao.insertNewAlert(alert)
        val updatedAlert = alert.copy(id = id, description = "Updated Desc", isEnabled = true)
        alertDao.updateAlert(updatedAlert)

        val updatedResult = alertDao.getAllAlerts().first()[0]
        assertThat(updatedResult.description, `is`("Updated Desc"))
        assertThat(updatedResult.isEnabled, `is`(true))
    }

    @Test
    fun deleteNonExistingAlert_noCrash() = runTest {
        val alert = WeatherAlert(
            id = 999,
            alertArea = "None",
            startTime = 0,
            endTime = 0,
            type = AlertType.ALARM,
            isEnabled = false,
            description = "none"
        )

        alertDao.deleteAlert(alert)
        val alerts = alertDao.getAllAlerts().first()
        assertTrue(alerts.isEmpty())
    }

}
package com.abdelrahman_elshreif.sky_vibe.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.abdelrahman_elshreif.sky_vibe.data.model.SkyVibeLocation
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
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
class FavouriteLocationDaoTest {
    private lateinit var dao: FavouriteLocationDao
    private lateinit var database: SkyVibeDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SkyVibeDatabase::class.java
        ).build()

        dao = database.getFavouriteLocationDao()
    }

    @After
    fun tearDown() = database.close()

    @Test
    fun getAllLocations_emptyInitially() = runTest {
        val allLocations = dao.getAllLocations().first()
        assertTrue(allLocations.isEmpty())
    }

    @Test
    fun insertLocation_locationSizeEqual1() = runTest {
        val location = SkyVibeLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            address = "Cairo, Egypt"
        )
        dao.insertLocation(location)
        val retrievedLocations = dao.getAllLocations().first()
        assertThat(retrievedLocations.size, `is`(1))
    }

    @Test
    fun insertLocation_retrieveLocation() = runTest {
        val location = SkyVibeLocation(
            latitude = 31.2001,
            longitude = 29.9187,
            address = "Alexandria, Egypt"
        )
        dao.insertLocation(location)
        val retrievedLocation = dao.getAllLocations().first()[0]

        assertThat(retrievedLocation, notNullValue())
        assertThat(retrievedLocation.latitude, `is`(location.latitude))
        assertThat(retrievedLocation.longitude, `is`(location.longitude))
        assertThat(retrievedLocation.address, `is`(location.address))
    }

    @Test
    fun insertMultipleLocations_allSavedCorrectly() = runTest {
        val location1 = SkyVibeLocation(
            latitude = 30.0444,
            longitude = 31.2357,
            address = "Cairo, Egypt"
        )
        val location2 = SkyVibeLocation(
            latitude = 31.2001,
            longitude = 29.9187,
            address = "Alexandria, Egypt"
        )

        dao.insertLocation(location1)
        dao.insertLocation(location2)

        val locations = dao.getAllLocations().first()
        assertThat(locations.size, `is`(2))
    }

    @Test
    fun deleteLocation_locationRemovedSuccessfully() = runTest {
        val location = SkyVibeLocation(
            latitude = 30.0131,
            longitude = 31.2089,
            address = "Giza, Egypt"
        )

        dao.insertLocation(location)
        val insertedLocation = dao.getAllLocations().first()[0]

        dao.deleteLocation(insertedLocation)

        val remainingLocations = dao.getAllLocations().first()
        assertTrue(remainingLocations.isEmpty())
    }


    @Test
    fun insertLocation_withNullAddress() = runTest {
        val location = SkyVibeLocation(
            latitude = 25.2048,
            longitude = 55.2708,
            address = null
        )

        dao.insertLocation(location)
        val retrievedLocation = dao.getAllLocations().first()[0]

        assertThat(retrievedLocation.address, `is`(location.address))
    }
}
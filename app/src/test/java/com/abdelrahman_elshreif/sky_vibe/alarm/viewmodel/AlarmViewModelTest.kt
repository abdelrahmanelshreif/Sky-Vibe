package com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel

import androidx.work.WorkManager
import com.abdelrahman_elshreif.sky_vibe.alarm.model.AlertType
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertEvent
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.AlertScheduler
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AlarmViewModelTest {

    private lateinit var viewModel: AlarmViewModel
    private lateinit var repository: SkyVibeRepository
    private lateinit var alertScheduler: AlertScheduler

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        alertScheduler = mockk(relaxed = true)
        viewModel = AlarmViewModel(repository, alertScheduler)
    }

    @Test
    fun onEvent_OnAlertDeleted_callsRepositoryAndCancelsScheduler() = runTest {
        val alert = WeatherAlert(
            id = 99,
            alertArea = "Alexandria",
            startTime = 0,
            endTime = 0,
            type = AlertType.ALARM,
            isEnabled = true,
            description = "Alert",
            latitude = 0.0,
            longitude = 0.0
        )

        viewModel.onEvent(WeatherAlertEvent.OnAlertDeleted(alert))

        coVerify { repository.deleteAlert(alert) }
        coVerify { alertScheduler.cancel(alert.id) }
    }
    @Test
    fun initializeNotifications_setsNotificationsEnabledTrue() {
        viewModel.initializeNotifications()
        assertTrue(viewModel.notificationsEnabled.value)
    }

    @Test
    fun disableNotifications_setsNotificationsEnabledFalse() {
        viewModel.initializeNotifications()
        viewModel.disableNotifications()
        assertFalse(viewModel.notificationsEnabled.value)
    }

    @Test
    fun onEvent_OnDismissDialog_hidesDialog() {
        viewModel.onEvent(WeatherAlertEvent.OnDismissDialog)
        assertFalse(viewModel.showAddDialog.value)
    }

    @Test
    fun onEvent_OnSaveAlert_callsRepositoryAndScheduler() = runTest {
        val alert = WeatherAlert(
            id = 0,
            alertArea = "",
            startTime = System.currentTimeMillis() + 10_000,
            endTime = System.currentTimeMillis() + 60_000,
            type = AlertType.ALARM,
            isEnabled = true,
            description = "Test Alert",
            latitude = 30.0,
            longitude = 31.0
        )

        coEvery { repository.getSavedLocation() } returns Pair(30.0, 31.0)
        coEvery { repository.getAddressFromLocation(30.0, 31.0) } returns "Test Address"
        coEvery { repository.addNewAlert(any()) } returns 1L

        viewModel.onEvent(WeatherAlertEvent.OnSaveAlert(alert))

        coVerify { repository.addNewAlert(any()) }
        coVerify { alertScheduler.schedule(any()) }
        assertFalse(viewModel.showAddDialog.value)
    }
}


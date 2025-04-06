package com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel

import androidx.work.WorkManager
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import io.mockk.mockk
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AlarmViewModelTest {

    private lateinit var viewModel: AlarmViewModel
    private lateinit var repository: SkyVibeRepository
    private lateinit var locationUtilities: LocationUtilities
    private lateinit var workManager: WorkManager

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        workManager = mockk(relaxed = true)
        locationUtilities = mockk(relaxed = true)
        viewModel = AlarmViewModel(repository, workManager, locationUtilities)
    }

    @Test
    fun getAlertState_initialState() {
        val initialState = viewModel.alertState.value

        assertNotNull(initialState)
        assertTrue(initialState.alerts.isEmpty())
        assertFalse(initialState.isLoading)
        assertNull(initialState.error)
    }

    @Test
    fun getShowAddDialog() {
    }

    @Test
    fun getNotificationsEnabled() {
    }

    @Test
    fun getLocationOnDemand() {
    }

    @Test
    fun initializeNotifications() {
    }

    @Test
    fun disableNotifications() {
    }

    @Test
    fun onEvent() {
    }
}


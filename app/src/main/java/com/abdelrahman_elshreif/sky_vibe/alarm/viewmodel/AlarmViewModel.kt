package com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertEvent
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertState
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertWorker
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class AlarmViewModel(
    private val repository: SkyVibeRepository,
    private val workManager: WorkManager,
    private val locationUtilities: LocationUtilities
) : ViewModel() {


    private val _alertState = MutableStateFlow(WeatherAlertState())
    val alertState = _alertState.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog = _showAddDialog.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()



    init {
        loadAlerts()
    }

    fun initializeNotifications() {
        _notificationsEnabled.value = true

    }


    fun disableNotifications() {
        _notificationsEnabled.value = false
    }


    fun onEvent(event: WeatherAlertEvent) {
        when (event) {
            is WeatherAlertEvent.OnAddAlertClick -> {
                _showAddDialog.value = true
            }

            is WeatherAlertEvent.OnAlertDeleted -> {
                deleteAlert(event)
            }

            is WeatherAlertEvent.OnAlertToggled -> {
                toggleAlert(event.alert)
            }

            WeatherAlertEvent.OnDismissDialog -> {
                _showAddDialog.value = false
            }

            is WeatherAlertEvent.OnSaveAlert -> {
                saveAlert(event.alert)
            }
        }
    }

    private fun saveAlert(alert: WeatherAlert) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val loc = locationUtilities.getLocationFromDataStore()
                alert.alertArea = locationUtilities.getAddressFromLocation(loc!!.first,loc.second)
                repository.addNewAlert(alert)

                scheduleAlert(alert)

                _showAddDialog.value = false

            } catch (e: Exception) {
                _alertState.update {
                    it.copy(
                        error = "Failed to Save Alarm : ${e.message}"
                    )
                }
            }
        }

    }

    private fun scheduleAlert(alert: WeatherAlert) {
        val currentTime = System.currentTimeMillis()
        val delayTime = alert.startTime - currentTime

        Log.d("ALARM", "Current time: ${Date(currentTime)}")
        Log.d("ALARM", "Alert time: ${Date(alert.startTime)}")
        Log.d("ALARM", "Delay: $delayTime ms")

        if (delayTime < 10000) {
            Log.d("ALARM", "Alert time is too close or in past, adding to immediate queue")
            return
        }

        val endTime = alert.endTime
        val inputData = workDataOf(
            "alertId" to alert.id,
            "alertType" to alert.type.name,
            "message" to alert.description,
            "endTime" to endTime
        )

        val alertWork = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInputData(inputData)
            .setInitialDelay(delayTime, TimeUnit.MILLISECONDS)
            .addTag("alert_${alert.id}")
            .build()

        workManager.enqueueUniqueWork(
            "alert_${alert.id}",
            ExistingWorkPolicy.REPLACE,
            alertWork
        )
    }

    private fun toggleAlert(alert: WeatherAlert) {
        viewModelScope.launch(Dispatchers.IO) {

            val updatedAlert = alert.copy(
                isEnabled = !alert.isEnabled
            )

            repository.updateAlert(updatedAlert)

            if (updatedAlert.isEnabled) {
                scheduleAlert(updatedAlert)
            } else {
                workManager.cancelUniqueWork("alert_${alert.id}")
            }

        }
    }

    private fun deleteAlert(event: WeatherAlertEvent.OnAlertDeleted) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(event.alert)
            workManager.cancelUniqueWork("alert_${event.alert.id}")
        }
    }

    private fun loadAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _alertState.update {
                    it.copy(isLoading = true)
                }

                repository.getAlerts()
                    .catch { ex ->
                        _alertState.update {
                            it.copy(
                                error = ex.message,
                                isLoading = false
                            )
                        }
                    }.collect { alerts ->
                        _alertState.update {
                            it.copy(
                                alerts = alerts,
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _alertState.update {
                    it.copy(
                        error = e.message,
                        isLoading = false
                    )
                }
            }
        }
    }

}
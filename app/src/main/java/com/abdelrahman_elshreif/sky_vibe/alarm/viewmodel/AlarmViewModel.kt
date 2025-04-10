package com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.workDataOf
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertEvent
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertState
import com.abdelrahman_elshreif.sky_vibe.data.repo.SkyVibeRepository
import com.abdelrahman_elshreif.sky_vibe.utils.AlertScheduler
import com.abdelrahman_elshreif.sky_vibe.workers.WeatherAlertWorker
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
    private val alertScheduler: AlertScheduler
) : ViewModel() {


    private val _alertState = MutableStateFlow(WeatherAlertState())
    val alertState = _alertState.asStateFlow()

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog = _showAddDialog.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(false)
    val notificationsEnabled = _notificationsEnabled.asStateFlow()

    private val _locationOnDemand = MutableStateFlow<Pair<Double, Double>?>(null)
    val locationOnDemand = _locationOnDemand.asStateFlow()

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
                getLocationForAlert()

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

    private fun getLocationForAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            _locationOnDemand.value = repository.getSavedLocation()
            _showAddDialog.value = true
        }
    }

    private fun saveAlert(alert: WeatherAlert) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val loc = repository.getSavedLocation()
                alert.alertArea = repository.getAddressFromLocation(loc!!.first, loc.second)

                val alertId = repository.addNewAlert(alert)
                val alertWithId = alert.copy(id = alertId)
                alertScheduler.schedule(alertWithId)
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

//    private fun scheduleAlert(alert: WeatherAlert) {
//        val currentTime = System.currentTimeMillis()
//        val delayTime = alert.startTime - currentTime
//
//        Log.d("ALARM", "Current time: ${Date(currentTime)}")
//        Log.d("ALARM", "Alert time: ${Date(alert.startTime)}")
//        Log.d("ALARM", "Delay: $delayTime ms")
//
//        if (delayTime < 3000) {
//            Log.d("ALARM", "Alert time is too close or in past, adding to immediate queue")
//            return
//        }
//        val endTime = alert.endTime
//
//        val inputData = workDataOf(
//            "alertId" to alert.id,
//            "alertType" to alert.type.name,
//            "message" to alert.description,
//            "endTime" to endTime,
//            "latitude" to alert.latitude,
//            "longitude" to alert.longitude
//        )
//
//        val alertWork = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
//            .setInputData(inputData)
//            .setInitialDelay(delayTime, TimeUnit.MILLISECONDS)
//            .addTag("alert_${alert.id}")
//            .build()
//
//        workManager.enqueueUniqueWork(
//            "alert_${alert.id}",
//            ExistingWorkPolicy.REPLACE,
//            alertWork
//        )
//    }

    private fun toggleAlert(alert: WeatherAlert) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedAlert = alert.copy(
                isEnabled = !alert.isEnabled
            )
            repository.updateAlert(updatedAlert)

            if (updatedAlert.isEnabled) {
                alertScheduler.schedule(updatedAlert)
            } else {
                alertScheduler.cancel(alert.id)
            }

        }
    }

    private fun deleteAlert(event: WeatherAlertEvent.OnAlertDeleted) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAlert(event.alert)
            alertScheduler.cancel(event.alert.id)
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
package com.abdelrahman_elshreif.sky_vibe.alarm.view

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertEvent
import com.abdelrahman_elshreif.sky_vibe.alarm.view.components.AddAlertDialog
import com.abdelrahman_elshreif.sky_vibe.alarm.view.components.AlertItem
import com.abdelrahman_elshreif.sky_vibe.alarm.view.components.EmptyAlertsMessage
import com.abdelrahman_elshreif.sky_vibe.alarm.view.components.ErrorMessage
import com.abdelrahman_elshreif.sky_vibe.alarm.view.components.LoadingIndicator
import com.abdelrahman_elshreif.sky_vibe.alarm.view.components.NotificationPermissionHandler
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.AlarmViewModel
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.NotificationHelper
import com.abdelrahman_elshreif.sky_vibe.utils.LocationUtilities
import kotlinx.coroutines.launch

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel,
    locationUtilities: LocationUtilities
) {
    val alertState by viewModel.alertState.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val context = LocalContext.current
    var currentLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var isLoadingLocation by remember { mutableStateOf(false) }

    NotificationPermissionHandler(
        onPermissionGranted = {
            NotificationHelper.createNotificationChannel(context)
            viewModel.initializeNotifications()
        },
        onPermissionDenied = {
            viewModel.disableNotifications()
        }
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.getLocationForAlert()
                }
            ) {
                Icon(
                    Icons.Default.AddAlarm,
                    contentDescription = stringResource(R.string.add_alarm)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                alertState.isLoading -> {
                    LoadingIndicator()
                }
                alertState.error != null -> {
                    ErrorMessage(alertState.error!!)
                }
                alertState.alerts.isEmpty() -> {
                    EmptyAlertsMessage()
                }
                else -> {
                    AlertsList(
                        alerts = alertState.alerts,
                        onAlertToggle = { alert ->
                            viewModel.onEvent(WeatherAlertEvent.OnAlertToggled(alert))
                        },
                        onAlertDelete = { alert ->
                            viewModel.onEvent(WeatherAlertEvent.OnAlertDeleted(alert))
                        }
                    )
                }
            }

            if (showAddDialog) {
                val savedLocation = viewModel.savedLocation.collectAsState().value
                if (savedLocation != null) {
                    AddAlertDialog(
                        onDismiss = { viewModel.onEvent(WeatherAlertEvent.OnDismissDialog) },
                        onSave = { alert ->
                            val alertWithLocation = alert.copy(
                                latitude = savedLocation.first,
                                longitude = savedLocation.second
                            )
                            viewModel.onEvent(WeatherAlertEvent.OnSaveAlert(alertWithLocation))
                        },
                        latitude = savedLocation.first,
                        longitude = savedLocation.second
                    )
                } else {
                    AlertDialog(
                        onDismissRequest = { viewModel.onEvent(WeatherAlertEvent.OnDismissDialog) },
                        title = { Text("Location Required") },
                        text = { Text("Please set a location in settings first.") },
                        confirmButton = {
                            TextButton(
                                onClick = { viewModel.onEvent(WeatherAlertEvent.OnDismissDialog) }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun AlertsList(
    alerts: List<WeatherAlert>,
    onAlertToggle: (WeatherAlert) -> Unit,
    onAlertDelete: (WeatherAlert) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(alerts) { item ->
            AlertItem(item,
                onToggle = { onAlertToggle(item) },
                onDelete = { onAlertDelete(item) }
            )
        }
    }
    Log.i("TAG", "AlertsList:${alerts} ")
}

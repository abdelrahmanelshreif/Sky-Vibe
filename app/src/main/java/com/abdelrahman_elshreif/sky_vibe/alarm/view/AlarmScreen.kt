package com.abdelrahman_elshreif.sky_vibe.alarm.view

import android.util.Log
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

@Composable
fun AlarmScreen(viewModel: AlarmViewModel) {

    val alertState by viewModel.alertState.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()
    val context = LocalContext.current

    NotificationPermissionHandler(
        onPermissionGranted = {
            NotificationHelper.createNotificationChannel(context)
            viewModel.initializeNotifications()
        },
        onPermissionDenied = {
            viewModel.disableNotifications()
        }
    )

    Scaffold(floatingActionButton = {

        FloatingActionButton(onClick = { viewModel.onEvent(WeatherAlertEvent.OnAddAlertClick) }) {
            Icon(
                Icons.Default.AddAlarm, contentDescription = stringResource(R.string.add_alarm)
            )
        }
    }) { paddingValues ->

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
                    AlertsList(alertState.alerts, onAlertToggle = { alert ->
                        viewModel.onEvent(WeatherAlertEvent.OnAlertToggled(alert))
                    }, onAlertDelete = { alert ->
                        viewModel.onEvent(WeatherAlertEvent.OnAlertDeleted(alert))
                    })
                }

            }

            if (showAddDialog) {
                AddAlertDialog(onDismiss = { viewModel.onEvent(WeatherAlertEvent.OnDismissDialog) },
                    onSave = { alert ->
                        viewModel.onEvent(WeatherAlertEvent.OnSaveAlert(alert))
                    })
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

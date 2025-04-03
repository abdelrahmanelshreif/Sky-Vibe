package com.abdelrahman_elshreif.sky_vibe.alarm.view

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertEvent
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.AlarmViewModel
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.NotificationHelper
import java.text.SimpleDateFormat
import java.util.*

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

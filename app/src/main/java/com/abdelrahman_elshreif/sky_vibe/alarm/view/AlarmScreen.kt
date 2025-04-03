package com.abdelrahman_elshreif.sky_vibe.alarm.view

import android.util.Log
import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlertEvent
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.AlarmViewModel
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel
) {
    val alertState by viewModel.alertState.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(WeatherAlertEvent.OnAddAlertClick) }
            ) {
                Icon(Icons.Default.AlarmAdd, contentDescription = "Add Alert")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
        }

        if (showAddDialog) {
            AddAlertDialog(
                onDismiss = { viewModel.onEvent(WeatherAlertEvent.OnDismissDialog) },
                onSave = { alert ->
                    viewModel.onEvent(WeatherAlertEvent.OnSaveAlert(alert))
                }
            )
        }
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(error: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun EmptyAlertsMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.no_alerts_set),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.tap_to_add_a_new_alert),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun AlertsList(
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


@Composable
private fun AlertItem(
    alert: WeatherAlert,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = formatDateTime(alert.startTime),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Type: ${alert.type}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = alert.isEnabled,
                        onCheckedChange = { onToggle() }
                    )
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Alert",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            if (alert.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = alert.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}


@Composable
fun DateTimePicker(
    label: String,
    dateTime: Long,
    onDateTimeSelected: (Long) -> Unit
) {

}

private fun formatDateTime(timestamp: Long): String {
    return SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        .format(Date(timestamp))
}
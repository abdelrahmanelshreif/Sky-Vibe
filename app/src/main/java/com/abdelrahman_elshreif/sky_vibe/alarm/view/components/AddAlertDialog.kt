package com.abdelrahman_elshreif.sky_vibe.alarm.view.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.abdelrahman_elshreif.sky_vibe.R
import com.abdelrahman_elshreif.sky_vibe.alarm.model.AlertType
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert
import com.abdelrahman_elshreif.sky_vibe.alarm.model.toLocalizedStringRes

@SuppressLint("AutoboxingStateCreation")
@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onSave: (WeatherAlert) -> Unit,
    latitude: Double,
    longitude: Double
) {
    var startTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }
    val endTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000 / 4) }
    var alertType by remember { mutableStateOf(AlertType.NOTIFICATION) }
    var description by remember { mutableStateOf("") }


    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    stringResource(R.string.add_weather_alert),
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                DateTimePicker(
                    label = stringResource(R.string.start_time),
                    dateTime = startTime,
                    onDateTimeSelected = { startTime = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

//                DateTimePicker(
//                    label = stringResource(R.string.end_time),
//                    dateTime = endTime,
//                    onDateTimeSelected = { endTime = it }
//                )
//
//                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text(stringResource(R.string.alert_type))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AlertType.entries.forEach { type ->
                            FilterChip(
                                selected = alertType == type,
                                onClick = { alertType = type },
                                label = { Text(stringResource(id = type.toLocalizedStringRes()))  }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.description_optional)) },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                WeatherAlert(
                                    startTime = startTime,
                                    endTime = endTime,
                                    type = alertType,
                                    description = description,
                                    latitude =latitude,
                                    longitude =longitude
                                )
                            )
                        }
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    }
}



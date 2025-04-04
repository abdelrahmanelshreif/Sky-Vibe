package com.abdelrahman_elshreif.sky_vibe.alarm.view.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.abdelrahman_elshreif.sky_vibe.alarm.model.AlertType
import com.abdelrahman_elshreif.sky_vibe.alarm.model.WeatherAlert

@SuppressLint("AutoboxingStateCreation")
@Composable
fun AddAlertDialog(
    onDismiss: () -> Unit,
    onSave: (WeatherAlert) -> Unit
) {
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }
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
                    "Add Weather Alert",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                DateTimePicker(
                    label = "Start Time",
                    dateTime = startTime,
                    onDateTimeSelected = { startTime = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                DateTimePicker(
                    label = "End Time",
                    dateTime = endTime,
                    onDateTimeSelected = { endTime = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text("Alert Type")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AlertType.entries.forEach { type ->
                            FilterChip(
                                selected = alertType == type,
                                onClick = { alertType = type },
                                label = { Text(type.name) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                WeatherAlert(
                                    startTime = startTime,
                                    endTime = endTime,
                                    type = alertType,
                                    description = description
                                )
                            )
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

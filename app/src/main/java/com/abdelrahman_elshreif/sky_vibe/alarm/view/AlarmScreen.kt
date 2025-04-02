package com.abdelrahman_elshreif.sky_vibe.alarm.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.abdelrahman_elshreif.sky_vibe.alarm.viewmodel.AlarmViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    alarmViewModel: AlarmViewModel,
    navController: NavController
) {
    var showAddAlertDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Alerts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddAlertDialog = true }
            ) {
                Icon(Icons.Default.Add, "Add Alert")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // List of active alerts
//            ActiveAlertsList(
//                alerts = viewModel.alerts.collectAsState().value,
//                onAlertToggle = viewModel::toggleAlert,
//                onAlertDelete = viewModel::deleteAlert
//            )

            // Add Alert Dialog
//            if (showAddAlertDialog) {
//                AddAlertDialog(
//                    onDismiss = { showAddAlertDialog = false },
//                    onConfirm = { alert ->
//                        viewModel.addAlert(alert)
//                        showAddAlertDialog = false
//                    }
//                )
//            }
        }
    }
}

@Composable
fun AddAlertDialog(onDismiss: () -> Unit, onConfirm: Any) {

}

@Composable
fun ActiveAlertsList(alerts: Any, onAlertToggle: Any, onAlertDelete: Any) {
    TODO("Not yet implemented")
}

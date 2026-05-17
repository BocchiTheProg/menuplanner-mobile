package com.example.menuplanner.ui.screens.security

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.menuplanner.data.security.BiometricType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(
    onNavigateBack: () -> Unit,
    viewModel: SecurityViewModel = hiltViewModel()
) {
    val biometricType by viewModel.biometricType.collectAsState()
    val isSwitchOn by viewModel.isBiometricSwitchEnabled.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Account Security") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Hardware Authentication Status", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Detected Hardware Support: ${biometricType.displayName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp, top = 4.dp)
            )

            HorizontalDivider()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Biometric Smart Locking", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "Require fingerprint check before reading account content.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = isSwitchOn,
                    onCheckedChange = { viewModel.toggleBiometrics(it) },
                    enabled = biometricType != BiometricType.NONE
                )
            }
            HorizontalDivider()
        }
    }
}
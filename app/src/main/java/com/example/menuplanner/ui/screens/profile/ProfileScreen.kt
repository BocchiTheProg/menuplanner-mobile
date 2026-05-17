package com.example.menuplanner.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.menuplanner.data.security.BiometricAuthState

@Composable
fun ProfileScreen(
    onNavigateToSecurity: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val isUnlocked by viewModel.isUnlocked.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.checkIfAuthenticationRequired()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isUnlocked) {
            // Protected Main Target View Area
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("User Profile", style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = "Welcome back! Your personal records are securely unlocked.",
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onNavigateToSecurity() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Security, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Security Configuration Settings")
                }
            }
        } else {
            // Locked Gate View Layer
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Profile Data Access Locked", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    val activity = context as? FragmentActivity
                    activity?.let { viewModel.initiateAuthentication(it) }
                }) {
                    Text("Unlock Content")
                }

                AnimatedVisibility(visible = authState == BiometricAuthState.FAILED) {
                    Text(
                        text = "Authentication failed. Try again.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                AnimatedVisibility(visible = authState == BiometricAuthState.UNAVAILABLE) {
                    Text(
                        text = "Biometrics sensor unavailable on this device.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
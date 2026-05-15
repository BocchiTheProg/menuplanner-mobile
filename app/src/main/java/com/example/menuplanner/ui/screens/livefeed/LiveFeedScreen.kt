package com.example.menuplanner.ui.screens.livefeed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.menuplanner.data.websocket.ConnectionState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveFeedScreen(
    viewModel: LiveFeedViewModel = hiltViewModel()
) {
    val connectionState by viewModel.connectionState.collectAsState()
    val events by viewModel.feedEvents.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Automatically manage connection based on Screen Lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                // Connect when screen opens or app is maximized
                viewModel.connect()
            } else if (event == Lifecycle.Event.ON_STOP) {
                // Disconnect properly when screen is closed or app is minimized
                viewModel.disconnect()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.disconnect()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Community Recipes") },
                actions = {
                    ConnectionIndicator(connectionState)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Show a progress indicator when connecting/reconnecting
            AnimatedVisibility(
                visible = connectionState == ConnectionState.Connecting ||
                        connectionState == ConnectionState.Reconnecting
            ) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (events.isEmpty() && connectionState == ConnectionState.Connected) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Waiting for someone to post a recipe...")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(events, key = { it.id }) { event ->
                        LiveEventCard(event)
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionIndicator(state: ConnectionState) {
    val color = when (state) {
        ConnectionState.Connected -> Color.Green
        ConnectionState.Connecting, ConnectionState.Reconnecting -> Color.Yellow
        ConnectionState.Disconnected -> Color.Red
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = state.name, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun LiveEventCard(event: com.example.menuplanner.data.websocket.LiveFeedEvent) {
    val timeFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val timeString = timeFormatter.format(Date(event.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "🔥 ${event.authorName} just added:", style = MaterialTheme.typography.labelMedium)
                Text(text = timeString, style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = event.recipeTitle, style = MaterialTheme.typography.titleMedium)
        }
    }
}
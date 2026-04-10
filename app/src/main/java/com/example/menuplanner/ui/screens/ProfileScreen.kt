package com.example.menuplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("User Profile", style = MaterialTheme.typography.headlineMedium)
        // TODO: Add some account info here
        Text("Lorem Ipsum is simply dummy text of the printing and typesetting industry.", modifier = Modifier.padding(top = 8.dp))
    }
}
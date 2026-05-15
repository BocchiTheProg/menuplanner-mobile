package com.example.menuplanner.data.websocket

// Connection states exactly as requested
enum class ConnectionState {
    Disconnected, Connecting, Connected, Reconnecting
}

// Data model representing a recipe coming from the live feed
data class LiveFeedEvent(
    val id: String,
    val recipeTitle: String,
    val authorName: String,
    val timestamp: Long
)
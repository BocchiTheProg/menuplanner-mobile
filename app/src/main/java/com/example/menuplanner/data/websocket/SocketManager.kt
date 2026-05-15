package com.example.menuplanner.data.websocket

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface SocketManager {
    val connectionState: StateFlow<ConnectionState>
    val incomingMessages: SharedFlow<LiveFeedEvent> // onMessage(handler)

    fun connect(url: String)
    fun disconnect()
    fun send(message: String)
}
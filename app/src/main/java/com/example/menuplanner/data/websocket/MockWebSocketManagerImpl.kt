package com.example.menuplanner.data.websocket

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockWebSocketManagerImpl @Inject constructor() : SocketManager {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val gson = Gson()

    private val _connectionState = MutableStateFlow(ConnectionState.Disconnected)
    override val connectionState = _connectionState.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<LiveFeedEvent>()
    override val incomingMessages = _incomingMessages.asSharedFlow()

    private var mockJob: Job? = null
    private val mockRecipeNames = listOf("Avocado Toast", "Borscht", "Vegan Pasta", "Grilled Chicken", "Sushi")
    private val mockAuthors = listOf("User1", "User2", "User3", "User4", "User5")

    override fun connect(url: String) {
        if (_connectionState.value == ConnectionState.Connected ||
            _connectionState.value == ConnectionState.Connecting) return

        mockJob = scope.launch {
            tryConnect()
        }
    }

    private suspend fun tryConnect(isReconnecting: Boolean = false) {
        _connectionState.value = if (isReconnecting) ConnectionState.Reconnecting else ConnectionState.Connecting
        delay(1500) // Simulate network delay

        _connectionState.value = ConnectionState.Connected
        startReceivingMockData()
    }

    private suspend fun startReceivingMockData() {
        var messageCount = 0
        while (currentCoroutineContext().isActive) {
            delay(Random.nextLong(3000, 5000))

            // Simulate a network drop every 5 messages to demonstrate Auto-Reconnect
            if (++messageCount % 5 == 0) {
                simulateConnectionDrop()
                return // Break this loop, reconnect logic takes over
            }

            // Generate Raw JSON (Simulating network payload)
            val rawJson = """
                {
                    "id": "${java.util.UUID.randomUUID()}",
                    "recipeTitle": "${mockRecipeNames.random()}",
                    "authorName": "${mockAuthors.random()}",
                    "timestamp": ${System.currentTimeMillis()}
                }
            """.trimIndent()

            // Parse from JSON into data model
            try {
                val event = gson.fromJson(rawJson, LiveFeedEvent::class.java)
                _incomingMessages.emit(event)
                Log.d("WebSocket", "Parsed JSON into object: $event")
            } catch (e: Exception) {
                Log.e("WebSocket", "JSON Parsing Error", e)
            }
        }
    }

    private suspend fun simulateConnectionDrop() {
        Log.w("WebSocket", "Connection Dropped! Attempting reconnect...")
        _connectionState.value = ConnectionState.Disconnected
        delay(2000) // Wait before retrying
        tryConnect(isReconnecting = true)
    }

    override fun disconnect() {
        mockJob?.cancel()
        mockJob = null
        _connectionState.value = ConnectionState.Disconnected
    }

    override fun send(message: String) {
        Log.d("WebSocket", "Sent message to server: $message")
    }
}